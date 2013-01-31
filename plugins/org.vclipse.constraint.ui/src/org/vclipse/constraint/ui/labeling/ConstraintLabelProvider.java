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
package org.vclipse.constraint.ui.labeling;

import org.eclipse.emf.edit.ui.provider.AdapterFactoryLabelProvider;
import org.vclipse.dependency.ui.labeling.DependencyLabelProvider;
import org.vclipse.vcml.vcml.ConstraintClass;
import org.vclipse.vcml.vcml.ConstraintMaterial;
import org.vclipse.vcml.vcml.IsSpecified_C;
import org.vclipse.vcml.vcml.ObjectCharacteristicReference;
import org.vclipse.vcml.vcml.ShortVarReference;

import com.google.inject.Inject;

/**
 * Provides labels for a EObjects.
 * 
 * see http://www.eclipse.org/Xtext/documentation/latest/xtext.html#labelProvider
 */
public class ConstraintLabelProvider extends DependencyLabelProvider {

	@Inject
	public ConstraintLabelProvider(AdapterFactoryLabelProvider delegate) {
		super(delegate);
	}

	public String text(IsSpecified_C element) {
		return text(element.getCharacteristic()) + "is specified";
	}
	
	public String text(ConstraintClass constraintClass) {
		return constraintClass.getName() + " is_a " + constraintClass.getClass_().getName();
	}	
	
	public String text(ConstraintMaterial constraintMaterial) {
		return constraintMaterial.getName() + " is_a " + constraintMaterial.getObjectType().getType();
	}
	
	public String text(ObjectCharacteristicReference reference) {
		return reference.getLocation().getName() + "." + reference.getCharacteristic().getName();
	}
	
	public String text(ShortVarReference reference) {
		return reference.getRef().getName();
	}
	
}
