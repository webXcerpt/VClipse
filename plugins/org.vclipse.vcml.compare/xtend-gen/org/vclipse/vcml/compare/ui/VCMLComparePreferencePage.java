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
package org.vclipse.vcml.compare.ui;

import com.google.inject.Inject;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.vclipse.vcml.compare.FeatureFilter;

/**
 * Preference page allowing the user to match the values that should be ignored during compare operation.
 */
@SuppressWarnings("all")
public class VCMLComparePreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {
  private IPreferenceStore preferenceStore;
  
  @Inject
  public VCMLComparePreferencePage(final IPreferenceStore preferenceStore) {
    super(FieldEditorPreferencePage.GRID);
    this.preferenceStore = preferenceStore;
  }
  
  /**
   * sets the description and the preference store
   */
  public void init(final IWorkbench workbench) {
    this.setDescription("Following preferences affect the model that is exported during the VCML Diff operation.");
    this.setPreferenceStore(this.preferenceStore);
  }
  
  /**
   * Creates boolean field editors for different values
   */
  protected void createFieldEditors() {
    this.addBooleanFieldEditor(FeatureFilter.CHARACTERISTIC_IGNORE_VALUE_ORDER, "Ignore values order in characteristics");
    this.addBooleanFieldEditor(FeatureFilter.CLASS_IGNORE_CHARACTERISTIC_ORDER, "Ignore characteristics order in classes");
    this.addBooleanFieldEditor(FeatureFilter.DEPENDENCY_NET_IGNORE_CONSTRAINTS_ORDER, "Ignore constraints order in dependency nets");
    this.addBooleanFieldEditor(FeatureFilter.MATERIAL_IGNORE_BOMS_ORDER, "Ignore bill of materials order in materials");
    this.addBooleanFieldEditor(FeatureFilter.MATERIAL_IGNORE_CLASSES_ORDER, "Ignore classes order in materials");
    this.addBooleanFieldEditor(FeatureFilter.MATERIAL_IGNORE_CONFIGURATION_PROFILE_ORDER, "Ignore configuration profile order in materials");
    this.addBooleanFieldEditor(FeatureFilter.VARIANT_FUNCTION_IGNORE_ARGUMENTS_ORDER, "Ignore arguments order in variant functions");
    this.addBooleanFieldEditor(FeatureFilter.VARIANT_TABLE_IGNORE_ARGUMENTS_ORDER, "Ignore arguments order in variant tables");
  }
  
  /**
   * Creates a boolean field editor.
   */
  protected void addBooleanFieldEditor(final String name, final String label) {
    final Composite parent = this.getFieldEditorParent();
    BooleanFieldEditor _booleanFieldEditor = new BooleanFieldEditor(name, label, parent);
    this.addField(_booleanFieldEditor);
  }
}
