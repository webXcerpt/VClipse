package org.vclipse.tests;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.xtext.service.AbstractGenericModule;
import org.vclipse.tests.VClipseTestPlugin;

/**
 * Dependency injection module for the VClipse test plug-in.
 */
@SuppressWarnings("all")
public class VClipseTestModule extends AbstractGenericModule {
  protected VClipseTestPlugin plugin;
  
  public VClipseTestModule(final VClipseTestPlugin plugin) {
    this.plugin = plugin;
  }
  
  public AbstractUIPlugin bindPlugin() {
    return this.plugin;
  }
  
  public IPreferenceStore bindPreferenceStore() {
    return this.plugin.getPreferenceStore();
  }
}
