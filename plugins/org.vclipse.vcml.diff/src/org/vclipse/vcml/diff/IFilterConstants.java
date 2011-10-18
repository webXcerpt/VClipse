/**
 * 
 */
package org.vclipse.vcml.diff;

import org.eclipse.emf.compare.diff.DiffPlugin;

/**
 *
 */
public interface IFilterConstants {
	
	public static final String CLASS_IGNORE_CHARACTERISTIC_ORDER = DiffPlugin.PLUGIN_ID + ".classIgnoreCharacteristicOrder";
	
	public static final String CHARACTERISTIC_IGNORE_VALUE_ORDER = DiffPlugin.PLUGIN_ID + ".characteristicIgnoreValueOrder";
	
	public static final String DEPENDENCY_NET_IGNORE_CONSTRAINTS_ORDER = DiffPlugin.PLUGIN_ID + ".dependencyNetIgnoreConstraintsOrder";
	
	public static final String MATERIAL_IGNORE_BOMS_ORDER = DiffPlugin.PLUGIN_ID + ".materialIgnoreBomsOrder";
	
	public static final String MATERIAL_IGNORE_CLASSES_ORDER = DiffPlugin.PLUGIN_ID + ".materialIgnoreClassesOrder";
	
	public static final String MATERIAL_IGNORE_CONFIGURATION_PROFILE_ORDER = DiffPlugin.PLUGIN_ID + ".materialIgnoreConfigurationProfileOrder";

	public static final String VARIANT_FUNCTION_IGNORE_ARGUMENTS_ORDER = DiffPlugin.PLUGIN_ID + ".variantFunctionIgnoreArgumentsOrder";

	public static final String VARIANT_TABLE_IGNORE_ARGUMENTS_ORDER = DiffPlugin.PLUGIN_ID + ".variantTableIgnoreArgumentsOrder";
}
