/*******************************************************************************
 * Copyright (c) 2010 - 2013 webXcerpt Software GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *  
 * Contributors:
 *     	webXcerpt Software GmbH - initial creator
 * 		www.webxcerpt.com
 ******************************************************************************/
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

/**
 * Extension of the default behavior returning null for types that should not be checked on name uniqueness.
 */
public class UniqueVCMLNamesValidationHelper extends NamesAreUniqueValidationHelper {

	private static final VcmlPackage VCML_PACKAGE = VcmlPackage.eINSTANCE;

	@Override
	protected void checkDescriptionForDuplicatedName(IEObjectDescription description, Map<EClass, Map<QualifiedName, IEObjectDescription>> clusterTypeToName, ValidationMessageAcceptor acceptor) {
		EObject object = description.getEObjectOrProxy();
		EClass eClass = object.eClass();
		QualifiedName qualifiedName = description.getName();
		EClass clusterType = getAssociatedClusterType(eClass);
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
