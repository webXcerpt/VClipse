/*******************************************************************************
 * Copyright (c) 2013 webXcerpt Software GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *		webXcerpt Software GmbH - initial creator
 *		www.webxcerpt.com
 ******************************************************************************/
package org.vclipse.vcml.compare

import org.eclipse.emf.ecore.EStructuralFeature
import org.vclipse.vcml.vcml.VcmlPackage

import static org.vclipse.vcml.compare.VCMLComparePlugin.*

/**
 * 
 */
class FeatureFilter extends org.eclipse.emf.compare.diff.FeatureFilter {
	
	public static String CLASS_IGNORE_CHARACTERISTIC_ORDER = ID + ".classIgnoreCharacteristicOrder"
	public static String CHARACTERISTIC_IGNORE_VALUE_ORDER = ID + ".characteristicIgnoreValueOrder"
	public static String DEPENDENCY_NET_IGNORE_CONSTRAINTS_ORDER = ID + ".dependencyNetIgnoreConstraintsOrder"
	public static String MATERIAL_IGNORE_BOMS_ORDER = ID + ".materialIgnoreBomsOrder"
	public static String MATERIAL_IGNORE_CLASSES_ORDER = ID + ".materialIgnoreClassesOrder"
	public static String MATERIAL_IGNORE_CONFIGURATION_PROFILE_ORDER = ID + ".materialIgnoreConfigurationProfileOrder"
	public static String VARIANT_FUNCTION_IGNORE_ARGUMENTS_ORDER = ID + ".variantFunctionIgnoreArgumentsOrder"
	public static String VARIANT_TABLE_IGNORE_ARGUMENTS_ORDER = ID + ".variantTableIgnoreArgumentsOrder"
	
	protected VcmlPackage VCML_PACKAGE = VcmlPackage::eINSTANCE
	
	/**
	 * Returns true if the feature ordering change does matter, false otherwise.
	 */
	override checkForOrderingChanges(EStructuralFeature feature) {
		val preferenceStore = getInstance().getPreferenceStore()
		if(VCML_PACKAGE.symbolicType_Values == feature) {
			return !preferenceStore.getBoolean(CHARACTERISTIC_IGNORE_VALUE_ORDER)
		} 
		if(VCML_PACKAGE.class_Characteristics == feature) {
			return !preferenceStore.getBoolean(CLASS_IGNORE_CHARACTERISTIC_ORDER)
		} 
		if(VCML_PACKAGE.dependencyNet_Constraints == feature) {
			return !preferenceStore.getBoolean(DEPENDENCY_NET_IGNORE_CONSTRAINTS_ORDER)
		} 
		if(VCML_PACKAGE.material_Billofmaterials == feature) {
			return !preferenceStore.getBoolean(MATERIAL_IGNORE_BOMS_ORDER)
		} 
		if(VCML_PACKAGE.material_Classifications == feature) {
			return !preferenceStore.getBoolean(MATERIAL_IGNORE_CLASSES_ORDER)
		} 
		if(VCML_PACKAGE.material_Configurationprofiles == feature) {
			return !preferenceStore.getBoolean(MATERIAL_IGNORE_CONFIGURATION_PROFILE_ORDER)
		} 
		if(VCML_PACKAGE.variantFunction_Arguments == feature) {
			return !preferenceStore.getBoolean(VARIANT_FUNCTION_IGNORE_ARGUMENTS_ORDER)
		} 
		if(VCML_PACKAGE.variantTable_Arguments == feature) {
			return !preferenceStore.getBoolean(VARIANT_TABLE_IGNORE_ARGUMENTS_ORDER)
		} 
		false	
	}
}