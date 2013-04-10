/**
 * Copyright (c) 2010 - 2013 webXcerpt Software GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     	webXcerpt Software GmbH - initial creator
 * 		www.webxcerpt.com
 */
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
