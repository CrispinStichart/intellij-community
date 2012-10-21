package org.jetbrains.jps.maven.model.impl;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.jps.incremental.resources.ResourcesBuilder;
import org.jetbrains.jps.incremental.resources.StandardResourceBuilderEnabler;
import org.jetbrains.jps.maven.model.JpsMavenExtensionService;
import org.jetbrains.jps.maven.model.JpsMavenModuleExtension;
import org.jetbrains.jps.model.JpsElementChildRole;
import org.jetbrains.jps.model.JpsElementFactory;
import org.jetbrains.jps.model.JpsSimpleElement;
import org.jetbrains.jps.model.ex.JpsElementChildRoleBase;
import org.jetbrains.jps.model.module.JpsDependencyElement;
import org.jetbrains.jps.model.module.JpsModule;

/**
 * @author nik
 */
public class JpsMavenExtensionServiceImpl extends JpsMavenExtensionService {
  private static final JpsElementChildRole<JpsSimpleElement<Boolean>> PRODUCTION_ON_TEST_ROLE = JpsElementChildRoleBase.create("production on test");

  public JpsMavenExtensionServiceImpl() {
    ResourcesBuilder.registerEnabler(new StandardResourceBuilderEnabler() {
      @Override
      public boolean isResourceProcessingEnabled(JpsModule module) {
        // enable standard resource processing only if this is not a maven module
        // for maven modules use maven-aware resource builder
        return getExtension(module) == null;
      }
    });
  }

  @Nullable
  @Override
  public JpsMavenModuleExtension getExtension(@NotNull JpsModule module) {
    return module.getContainer().getChild(JpsMavenModuleExtensionImpl.ROLE);
  }

  @NotNull
  @Override
  public JpsMavenModuleExtension getOrCreateExtension(@NotNull JpsModule module) {
    JpsMavenModuleExtension extension = module.getContainer().getChild(JpsMavenModuleExtensionImpl.ROLE);
    if (extension == null) {
      extension = new JpsMavenModuleExtensionImpl();
      module.getContainer().setChild(JpsMavenModuleExtensionImpl.ROLE, extension);
    }
    return extension;
  }

  @Override
  public void setProductionOnTestDependency(@NotNull JpsDependencyElement dependency, boolean value) {
    if (value) {
      dependency.getContainer().setChild(PRODUCTION_ON_TEST_ROLE, JpsElementFactory.getInstance().createSimpleElement(true));
    }
    else {
      dependency.getContainer().removeChild(PRODUCTION_ON_TEST_ROLE);
    }
  }

  @Override
  public boolean isProductionOnTestDependency(@NotNull JpsDependencyElement dependency) {
    JpsSimpleElement<Boolean> child = dependency.getContainer().getChild(PRODUCTION_ON_TEST_ROLE);
    return child != null && child.getData();
  }
}
