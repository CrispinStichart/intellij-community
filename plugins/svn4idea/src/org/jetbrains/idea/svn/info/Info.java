/*
 * Copyright 2000-2014 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jetbrains.idea.svn.info;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.idea.svn.conflict.TreeConflictDescription;
import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.SVNLock;
import org.tmatesoft.svn.core.SVNNodeKind;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.internal.util.SVNDate;
import org.tmatesoft.svn.core.wc.SVNInfo;
import org.tmatesoft.svn.core.wc.SVNRevision;

import java.io.File;
import java.util.Date;

/**
 * @author Konstantin Kolosovsky.
 */
public class Info {

  private final File myFile;
  private final String myPath;
  private final SVNURL myURL;
  private final SVNRevision myRevision;
  private final SVNNodeKind myKind;
  private final SVNURL myRepositoryRootURL;
  private final String myRepositoryUUID;
  private final SVNRevision myCommittedRevision;
  private final Date myCommittedDate;
  private final String myAuthor;
  private final SVNLock myLock;
  private final boolean myIsRemote;
  private final String mySchedule;
  private final SVNURL myCopyFromURL;
  private final SVNRevision myCopyFromRevision;
  private final File myConflictOldFile;
  private final File myConflictNewFile;
  private final File myConflictWrkFile;
  private final File myPropConflictFile;
  private final SVNDepth myDepth;
  private final TreeConflictDescription myTreeConflict;

  @NotNull
  public static Info create(@NotNull SVNInfo info) {
    Info result;

    if (info.isRemote()) {
      result = new Info(info.getPath(), info.getURL(), info.getRevision(), info.getKind(), info.getRepositoryUUID(),
                           info.getRepositoryRootURL(), info.getCommittedRevision().getNumber(), info.getCommittedDate(), info.getAuthor(),
                           info.getLock(), info.getDepth());
    }
    else {
      result = new Info(info.getFile(), info.getURL(), info.getRepositoryRootURL(), info.getRevision().getNumber(), info.getKind(),
                           info.getRepositoryUUID(), info.getCommittedRevision().getNumber(), info.getCommittedDate().toString(),
                           info.getAuthor(), info.getSchedule(), info.getCopyFromURL(), info.getCopyFromRevision().getNumber(),
                           info.getConflictOldFile().getPath(), info.getConflictNewFile().getPath(), info.getConflictWrkFile().getPath(),
                           info.getPropConflictFile().getPath(), info.getLock(), info.getDepth(),
                           TreeConflictDescription.create(info.getTreeConflict()));
    }

    return result;
  }

  public Info(File file,
              SVNURL url,
              SVNURL rootURL,
              long revision,
              SVNNodeKind kind,
              String uuid,
              long committedRevision,
              String committedDate,
              String author,
              String schedule,
              SVNURL copyFromURL,
              long copyFromRevision,
              String conflictOld,
              String conflictNew,
              String conflictWorking,
              String propRejectFile,
              SVNLock lock,
              SVNDepth depth,
              TreeConflictDescription treeConflict) {
    myFile = file;
    myURL = url;
    myRevision = SVNRevision.create(revision);
    myKind = kind;
    myRepositoryUUID = uuid;
    myRepositoryRootURL = rootURL;

    myCommittedRevision = SVNRevision.create(committedRevision);
    myCommittedDate = committedDate != null ? SVNDate.parseDate(committedDate) : null;
    myAuthor = author;

    mySchedule = schedule;

    myCopyFromURL = copyFromURL;
    myCopyFromRevision = SVNRevision.create(copyFromRevision);

    myLock = lock;
    myTreeConflict = treeConflict;

    myConflictOldFile = resolveConflictFile(file, conflictOld);
    myConflictNewFile = resolveConflictFile(file, conflictNew);
    myConflictWrkFile = resolveConflictFile(file, conflictWorking);
    myPropConflictFile = resolveConflictFile(file, propRejectFile);

    myIsRemote = false;
    myDepth = depth;

    myPath = null;
  }

  public Info(String path,
              SVNURL url,
              SVNRevision revision,
              SVNNodeKind kind,
              String uuid,
              SVNURL reposRootURL,
              long committedRevision,
              Date date,
              String author,
              SVNLock lock,
              SVNDepth depth) {
    myIsRemote = true;
    myURL = url;
    myRevision = revision;
    myKind = kind;
    myRepositoryRootURL = reposRootURL;
    myRepositoryUUID = uuid;

    myCommittedDate = date;
    myCommittedRevision = SVNRevision.create(committedRevision);
    myAuthor = author;

    myLock = lock;
    myPath = path;
    myDepth = depth;

    myFile = null;
    mySchedule = null;
    myCopyFromURL = null;
    myCopyFromRevision = null;
    myConflictOldFile = null;
    myConflictNewFile = null;
    myConflictWrkFile = null;
    myPropConflictFile = null;
    myTreeConflict = null;
  }

  public String getAuthor() {
    return myAuthor;
  }

  public Date getCommittedDate() {
    return myCommittedDate;
  }

  public SVNRevision getCommittedRevision() {
    return myCommittedRevision;
  }

  public File getConflictNewFile() {
    return myConflictNewFile;
  }

  public File getConflictOldFile() {
    return myConflictOldFile;
  }

  public File getConflictWrkFile() {
    return myConflictWrkFile;
  }

  public TreeConflictDescription getTreeConflict() {
    return myTreeConflict;
  }

  public SVNRevision getCopyFromRevision() {
    return myCopyFromRevision;
  }

  public SVNURL getCopyFromURL() {
    return myCopyFromURL;
  }

  public File getFile() {
    return myFile;
  }

  public boolean isRemote() {
    return myIsRemote;
  }

  public SVNNodeKind getKind() {
    return myKind;
  }

  public SVNLock getLock() {
    return myLock;
  }

  public String getPath() {
    return myPath;
  }

  public File getPropConflictFile() {
    return myPropConflictFile;
  }

  public SVNURL getRepositoryRootURL() {
    return myRepositoryRootURL;
  }

  public String getRepositoryUUID() {
    return myRepositoryUUID;
  }

  public SVNRevision getRevision() {
    return myRevision;
  }

  public String getSchedule() {
    return mySchedule;
  }

  public SVNURL getURL() {
    return myURL;
  }

  public SVNDepth getDepth() {
    return myDepth;
  }

  @Nullable
  private static File resolveConflictFile(@Nullable File file, @Nullable String path) {
    return file != null && path != null ? new File(file.getParentFile(), path) : null;
  }
}
