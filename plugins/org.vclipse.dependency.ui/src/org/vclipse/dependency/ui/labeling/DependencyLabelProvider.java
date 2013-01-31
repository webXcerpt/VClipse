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
package org.vclipse.dependency.ui.labeling;

import org.eclipse.emf.edit.ui.provider.AdapterFactoryLabelProvider;
import org.vclipse.vcml.ui.labeling.AbstractVClipseLabelProvider;
import org.vclipse.vcml.vcml.CharacteristicReference_P;
import org.vclipse.vcml.vcml.IsInvisible;
import org.vclipse.vcml.vcml.ProcedureLocation;

import com.google.inject.Inject;

/**
 * Provides labels for a EObjects.
 * 
 * see http://www.eclipse.org/Xtext/documentation/latest/xtext.html#labelProvider
 */
public class DependencyLabelProvider extends AbstractVClipseLabelProvider {

	@Inject
	public DependencyLabelProvider(AdapterFactoryLabelProvider delegate) {
		super(delegate);
	}

	public String text(IsInvisible element) {
		return text(element.getCharacteristic()) + "is invisible ";
	}
	
	public String text(CharacteristicReference_P element) {
		ProcedureLocation location = element.getLocation();
		return (location!=null ? location.getLiteral() + "." : "") + text(element.getCharacteristic());
	}
}
