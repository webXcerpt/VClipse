/**
 * 
 */
package org.vclipse.vcml.diff;

import org.eclipse.emf.compare.diff.metamodel.DifferenceKind;
import org.eclipse.emf.ecore.EObject;
import org.vclipse.vcml.diff.compare.VcmlDiffFilter;

import com.google.inject.ImplementedBy;

@ImplementedBy(VcmlDiffFilter.class)
public interface IVcmlDiffFilter {

	public static final String CLASS_IGNORE_CHARACTERISTIC_ORDER = VcmlDiffPlugin.ID + ".classIgnoreCharacteristicOrder";
	
	public static final String CHARACTERISTIC_IGNORE_VALUE_ORDER = VcmlDiffPlugin.ID + ".characteristicIgnoreValueOrder";
	
	public static final String DEPENDENCY_NET_IGNORE_CONSTRAINTS_ORDER = VcmlDiffPlugin.ID + ".dependencyNetIgnoreConstraintsOrder";
	
	public static final String MATERIAL_IGNORE_BOMS_ORDER = VcmlDiffPlugin.ID + ".materialIgnoreBomsOrder";
	
	public static final String MATERIAL_IGNORE_CLASSES_ORDER = VcmlDiffPlugin.ID + ".materialIgnoreClassesOrder";
	
	public static final String MATERIAL_IGNORE_CONFIGURATION_PROFILE_ORDER = VcmlDiffPlugin.ID + ".materialIgnoreConfigurationProfileOrder";
	
	public static final String VARIANT_FUNCTION_IGNORE_ARGUMENTS_ORDER = VcmlDiffPlugin.ID + ".variantFunctionIgnoreArgumentsOrder";
	
	public static final String VARIANT_TABLE_IGNORE_ARGUMENTS_ORDER = VcmlDiffPlugin.ID + ".variantTableIgnoreArgumentsOrder";

	public boolean canHandle(EObject object, DifferenceKind kind);
	
	public boolean changeAllowed(EObject newParent, EObject oldParent, EObject newChild, EObject oldChild, DifferenceKind changeKind);
	
}
