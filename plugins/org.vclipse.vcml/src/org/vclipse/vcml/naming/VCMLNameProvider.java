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

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.naming.IQualifiedNameProvider;
import org.vclipse.base.naming.INameProvider;
import org.vclipse.vcml.vcml.BillOfMaterial;
import org.vclipse.vcml.vcml.Import;
import org.vclipse.vcml.vcml.Material;
import org.vclipse.vcml.vcml.Option;
import org.vclipse.vcml.vcml.VCObject;
import org.vclipse.vcml.vcml.VariantTable;
import org.vclipse.vcml.vcml.VariantTableContent;

import com.google.inject.Inject;

/**
 * Default implementation for VCML objects.
 */
public class VCMLNameProvider extends INameProvider.AbstractImpl {

	@Inject
	private IQualifiedNameProvider nameProvider;
	
	/**
	 * Returns a name for the object if one exists, type name otherwise.
	 */
	public String getName(EObject object) {
		if(object instanceof Import) {
			return ((Import)object).getImportURI();
		}
		if(object instanceof Option) {
			return ((Option) object).getName().getName();
		}
		String icn = object.eClass().getInstanceClassName();
		if(object instanceof VariantTableContent) {
			VariantTable table = ((VariantTableContent)object).getTable();
			return icn + table.getName();
		}
		if(object instanceof BillOfMaterial) {
			Material material = ((BillOfMaterial)object).getMaterial();
			return icn + material.getName();
		}
		if(object instanceof VCObject) {
			return nameProvider.getFullyQualifiedName(object).toString();
		}
		return icn;
	}
}
