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
package org.vclipse.procedure.ui.labeling;

import org.eclipse.emf.edit.ui.provider.AdapterFactoryLabelProvider;
import org.vclipse.dependency.ui.labeling.DependencyLabelProvider;
import org.vclipse.vcml.vcml.Assignment;
import org.vclipse.vcml.vcml.CompoundStatement;
import org.vclipse.vcml.vcml.ConditionalStatement;
import org.vclipse.vcml.vcml.DelDefault;
import org.vclipse.vcml.vcml.IsSpecified_P;
import org.vclipse.vcml.vcml.SetDefault;

import com.google.inject.Inject;

/**
 * Provides labels for a EObjects.
 * 
 * see http://www.eclipse.org/Xtext/documentation/latest/xtext.html#labelProvider
 */
public class ProcedureLabelProvider extends DependencyLabelProvider {

	@Inject
	public ProcedureLabelProvider(AdapterFactoryLabelProvider delegate) {
		super(delegate);
	}

	public String text(Assignment element) {
		return text(element.getCharacteristic()) + " = ...";
	}
	
	public String text(CompoundStatement element) {
		return "(..., ...)";
	}
	
	public String text(ConditionalStatement element) {
		return "... if ...";
	}
	
	public String text(DelDefault element) {
		return "$del_default " + text(element.getCharacteristic());
	}
	
	public String text(IsSpecified_P element) {
		return text(element.getCharacteristic()) + "is specified";
	}
	
	public String text(SetDefault element) {
		return text(element.getCharacteristic()) + " ?= ...";
	}
}