/*******************************************************************************
 * Copyright (c) 2010 webXcerpt Software GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    webXcerpt Software GmbH - initial creator
 ******************************************************************************/
package org.vclipse.vcml.documentation;

import org.eclipse.emf.ecore.EClass;
import org.vclipse.base.DefaultClassNameProvider;
import org.vclipse.vcml.vcml.VcmlPackage;

public class VCMLClassNameProvider extends DefaultClassNameProvider {

	@Override
	public String getClassName(EClass cls) {
		if (cls.getEPackage() == VcmlPackage.eINSTANCE) {
			switch (cls.getClassifierID()) {
			case VcmlPackage.BILL_OF_MATERIAL: return "Bill Of Material";
			case VcmlPackage.BOM_ITEM: return "BOM Item";
			case VcmlPackage.CHARACTERISTIC_GROUP: return "Characteristic Group";
			case VcmlPackage.CHARACTERISTIC_VALUE: return "Characteristic Value";
			case VcmlPackage.CONFIGURATION_PROFILE: return "Configuration Profile";
			case VcmlPackage.DEPENDENCY_NET: return "Dependency Net";
			case VcmlPackage.INTERFACE_DESIGN: return "Interface Design";
			case VcmlPackage.SELECTION_CONDITION: return "Selection Condition";
			case VcmlPackage.VARIANT_FUNCTION: return "Variant Function";
			case VcmlPackage.VARIANT_TABLE: return "Variant Table";
			// ... to be extended if Java class names do not match the name output to the user
			}
		}
		return super.getClassName(cls);
	}
	
}
