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
package org.vclipse.vcml.compare;

import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.xtext.xbase.lib.Functions.Function0;
import org.vclipse.vcml.compare.VCMLComparePlugin;

@SuppressWarnings("all")
public class FeatureFilter /* implements org.eclipse.emf.compare.diff.FeatureFilter  */{
  public static String CLASS_IGNORE_CHARACTERISTIC_ORDER = new Function0<String>() {
    public String apply() {
      String _plus = (VCMLComparePlugin.ID + ".classIgnoreCharacteristicOrder");
      return _plus;
    }
  }.apply();
  
  public static String CHARACTERISTIC_IGNORE_VALUE_ORDER = new Function0<String>() {
    public String apply() {
      String _plus = (VCMLComparePlugin.ID + ".characteristicIgnoreValueOrder");
      return _plus;
    }
  }.apply();
  
  public static String DEPENDENCY_NET_IGNORE_CONSTRAINTS_ORDER = new Function0<String>() {
    public String apply() {
      String _plus = (VCMLComparePlugin.ID + ".dependencyNetIgnoreConstraintsOrder");
      return _plus;
    }
  }.apply();
  
  public static String MATERIAL_IGNORE_BOMS_ORDER = new Function0<String>() {
    public String apply() {
      String _plus = (VCMLComparePlugin.ID + ".materialIgnoreBomsOrder");
      return _plus;
    }
  }.apply();
  
  public static String MATERIAL_IGNORE_CLASSES_ORDER = new Function0<String>() {
    public String apply() {
      String _plus = (VCMLComparePlugin.ID + ".materialIgnoreClassesOrder");
      return _plus;
    }
  }.apply();
  
  public static String MATERIAL_IGNORE_CONFIGURATION_PROFILE_ORDER = new Function0<String>() {
    public String apply() {
      String _plus = (VCMLComparePlugin.ID + ".materialIgnoreConfigurationProfileOrder");
      return _plus;
    }
  }.apply();
  
  public static String VARIANT_FUNCTION_IGNORE_ARGUMENTS_ORDER = new Function0<String>() {
    public String apply() {
      String _plus = (VCMLComparePlugin.ID + ".variantFunctionIgnoreArgumentsOrder");
      return _plus;
    }
  }.apply();
  
  public static String VARIANT_TABLE_IGNORE_ARGUMENTS_ORDER = new Function0<String>() {
    public String apply() {
      String _plus = (VCMLComparePlugin.ID + ".variantTableIgnoreArgumentsOrder");
      return _plus;
    }
  }.apply();
  
  protected /* VcmlPackage */Object VCML_PACKAGE /* Skipped initializer because of errors */;
  
  /**
   * Returns true if the feature ordering change does matter, false otherwise.
   */
  public boolean checkForOrderingChanges(final EStructuralFeature feature) {
    throw new Error("Unresolved compilation problems:"
      + "\nsymbolicType_Values cannot be resolved"
      + "\n== cannot be resolved"
      + "\nclass_Characteristics cannot be resolved"
      + "\n== cannot be resolved"
      + "\ndependencyNet_Constraints cannot be resolved"
      + "\n== cannot be resolved"
      + "\nmaterial_Billofmaterials cannot be resolved"
      + "\n== cannot be resolved"
      + "\nmaterial_Classifications cannot be resolved"
      + "\n== cannot be resolved"
      + "\nmaterial_Configurationprofiles cannot be resolved"
      + "\n== cannot be resolved"
      + "\nvariantFunction_Arguments cannot be resolved"
      + "\n== cannot be resolved"
      + "\nvariantTable_Arguments cannot be resolved"
      + "\n== cannot be resolved");
  }
}
