// Copyright 2000-2022 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
package com.intellij.internal.statistic.libraryUsage

import com.intellij.codeInsight.hint.HintManager
import com.intellij.internal.statistic.libraryJar.findJarVersion
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.ui.Messages

internal class LibraryUsageStatisticsAction : AnAction() {

  override fun getActionUpdateThread(): ActionUpdateThread = ActionUpdateThread.BGT

  override fun update(e: AnActionEvent) {
    e.presentation.isEnabledAndVisible = e.project != null
  }

  override fun actionPerformed(e: AnActionEvent) {
    val project = e.project ?: return
    val editor = e.getData(CommonDataKeys.EDITOR) ?: return
    val caretOffset = e.getData(CommonDataKeys.CARET)?.offset ?: return
    val psiFile = e.getData(CommonDataKeys.PSI_FILE) ?: return
    val fileType = psiFile.fileType
    fun showErrorHint(message: String): Unit = HintManager.getInstance().showErrorHint(editor, message)

    val processor = LibraryUsageImportProcessorBean.INSTANCE.forLanguage(psiFile.language)
                    ?: return showErrorHint("LibraryUsageImportProcessor is not found for ${fileType.name} file type")

    val import = processor.imports(psiFile).find { caretOffset in it.textRange } ?: return showErrorHint("import at caret is not found")
    val qualifier = processor.importQualifier(import) ?: return showErrorHint("qualifier is null")
    val libraryName = LibraryUsageDescriptors.findSuitableLibrary(qualifier) ?: return showErrorHint("suitable library in not found")
    val resolvedElement = processor.resolve(import) ?: return showErrorHint("failed to resolve")
    val libraryVersion = resolvedElement.let(::findJarVersion) ?: return showErrorHint("failed to find version")
    val libraryUsage = LibraryUsage(
      name = libraryName,
      version = libraryVersion,
      fileType = fileType,
    )

    Messages.showInfoMessage(project, libraryUsage.toString(), "Library Info")
  }
}