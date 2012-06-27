package org.vclipse.vcml.naming;

import java.util.Map;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.naming.QualifiedName;
import org.eclipse.xtext.resource.IEObjectDescription;
import org.eclipse.xtext.validation.NamesAreUniqueValidationHelper;
import org.eclipse.xtext.validation.ValidationMessageAcceptor;
import org.vclipse.vcml.vcml.VcmlPackage;

import com.google.common.collect.Maps;

public class VcmlNamesAreUniqueValidationHelper extends NamesAreUniqueValidationHelper {

	private static final VcmlPackage VCML_PACKAGE = VcmlPackage.eINSTANCE;

	@Override
	protected void checkDescriptionForDuplicatedName(
			IEObjectDescription description,
			Map<EClass, Map<QualifiedName, IEObjectDescription>> clusterTypeToName,
			ValidationMessageAcceptor acceptor) {
		EObject object = description.getEObjectOrProxy();
		EClass eClass = object.eClass();
		QualifiedName qualifiedName = description.getName();
		EClass clusterType = getAssociatedClusterType(eClass);
		if(clusterType == null) {
			return;
		}
		Map<QualifiedName, IEObjectDescription> nameToDescription = clusterTypeToName.get(clusterType);
		if (nameToDescription == null) {
			nameToDescription = Maps.newHashMap();
			nameToDescription.put(qualifiedName, description);
			clusterTypeToName.put(clusterType, nameToDescription);
		} else {
			if (nameToDescription.containsKey(qualifiedName)) {
				IEObjectDescription prevDescription = nameToDescription.get(qualifiedName);
				if (prevDescription != null) {
					createDuplicateNameError(prevDescription, clusterType, acceptor);
					nameToDescription.put(qualifiedName, null);
				}
				createDuplicateNameError(description, clusterType, acceptor);
			} else {
				nameToDescription.put(qualifiedName, description);
			}
		}
	}
	
	@Override
	protected EClass getAssociatedClusterType(EClass eClass) {
		if(eClass == VCML_PACKAGE.getCharacteristicValue() || 
				   eClass == VCML_PACKAGE.getConstraintClass() || 
						   eClass == VCML_PACKAGE.getConstraintMaterial() || 
								   eClass == VCML_PACKAGE.getPartialKey() || 
										   eClass == VCML_PACKAGE.getShortVarDefinition()) {
			// do not handle names for this types
			return null;
		}
		return eClass;
	}
}
