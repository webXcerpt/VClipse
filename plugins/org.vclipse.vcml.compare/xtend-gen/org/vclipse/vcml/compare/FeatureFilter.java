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

import com.google.common.base.Objects;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.xtext.xbase.lib.Functions.Function0;
import org.vclipse.vcml.compare.VCMLComparePlugin;
import org.vclipse.vcml.vcml.VcmlPackage;

@SuppressWarnings("all")
public class FeatureFilter extends org.eclipse.emf.compare.diff.FeatureFilter {
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
  
  protected VcmlPackage VCML_PACKAGE = VcmlPackage.eINSTANCE;
  
  /**
   * Returns true if the feature ordering change does matter, false otherwise.
   */
  public boolean checkForOrderingChanges(final EStructuralFeature feature) {
    boolean _xblockexpression = false;
    {
      VCMLComparePlugin _instance = VCMLComparePlugin.getInstance();
      final IPreferenceStore preferenceStore = _instance.getPreferenceStore();
      EReference _symbolicType_Values = this.VCML_PACKAGE.getSymbolicType_Values();
      boolean _equals = Objects.equal(_symbolicType_Values, feature);
      if (_equals) {
        boolean _boolean = preferenceStore.getBoolean(FeatureFilter.CHARACTERISTIC_IGNORE_VALUE_ORDER);
        return (!_boolean);
      }
      EReference _class_Characteristics = this.VCML_PACKAGE.getClass_Characteristics();
      boolean _equals_1 = Objects.equal(_class_Characteristics, feature);
      if (_equals_1) {
        boolean _boolean_1 = preferenceStore.getBoolean(FeatureFilter.CLASS_IGNORE_CHARACTERISTIC_ORDER);
        return (!_boolean_1);
      }
      EReference _dependencyNet_Constraints = this.VCML_PACKAGE.getDependencyNet_Constraints();
      boolean _equals_2 = Objects.equal(_dependencyNet_Constraints, feature);
      if (_equals_2) {
        boolean _boolean_2 = preferenceStore.getBoolean(FeatureFilter.DEPENDENCY_NET_IGNORE_CONSTRAINTS_ORDER);
        return (!_boolean_2);
      }
      EReference _material_Billofmaterials = this.VCML_PACKAGE.getMaterial_Billofmaterials();
      boolean _equals_3 = Objects.equal(_material_Billofmaterials, feature);
      if (_equals_3) {
        boolean _boolean_3 = preferenceStore.getBoolean(FeatureFilter.MATERIAL_IGNORE_BOMS_ORDER);
        return (!_boolean_3);
      }
      EReference _material_Classifications = this.VCML_PACKAGE.getMaterial_Classifications();
      boolean _equals_4 = Objects.equal(_material_Classifications, feature);
      if (_equals_4) {
        boolean _boolean_4 = preferenceStore.getBoolean(FeatureFilter.MATERIAL_IGNORE_CLASSES_ORDER);
        return (!_boolean_4);
      }
      EReference _material_Configurationprofiles = this.VCML_PACKAGE.getMaterial_Configurationprofiles();
      boolean _equals_5 = Objects.equal(_material_Configurationprofiles, feature);
      if (_equals_5) {
        boolean _boolean_5 = preferenceStore.getBoolean(FeatureFilter.MATERIAL_IGNORE_CONFIGURATION_PROFILE_ORDER);
        return (!_boolean_5);
      }
      EReference _variantFunction_Arguments = this.VCML_PACKAGE.getVariantFunction_Arguments();
      boolean _equals_6 = Objects.equal(_variantFunction_Arguments, feature);
      if (_equals_6) {
        boolean _boolean_6 = preferenceStore.getBoolean(FeatureFilter.VARIANT_FUNCTION_IGNORE_ARGUMENTS_ORDER);
        return (!_boolean_6);
      }
      EReference _variantTable_Arguments = this.VCML_PACKAGE.getVariantTable_Arguments();
      boolean _equals_7 = Objects.equal(_variantTable_Arguments, feature);
      if (_equals_7) {
        boolean _boolean_7 = preferenceStore.getBoolean(FeatureFilter.VARIANT_TABLE_IGNORE_ARGUMENTS_ORDER);
        return (!_boolean_7);
      }
      _xblockexpression = (false);
    }
    return _xblockexpression;
  }
}
