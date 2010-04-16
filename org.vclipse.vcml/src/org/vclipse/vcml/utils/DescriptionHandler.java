/*******************************************************************************
 * Copyright (c) 2010 webXcerpt Software GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    webXcerpt Software GmbH - initial creator
 *******************************************************************************/
/**
 * 
 */
package org.vclipse.vcml.utils;

import org.vclipse.vcml.vcml.Description;
import org.vclipse.vcml.vcml.Language;
import org.vclipse.vcml.vcml.MultiLanguageDescription;
import org.vclipse.vcml.vcml.MultiLanguageDescriptions;
import org.vclipse.vcml.vcml.SimpleDescription;
import org.vclipse.vcml.vcml.util.VcmlSwitch;

public abstract class DescriptionHandler extends VcmlSwitch<Object> {
	
	public void handleDescription(Description description) {
		if(description != null) {
			this.doSwitch(description);
		}
	}
	
	@Override
	public Object caseMultiLanguageDescription(
			MultiLanguageDescription object) {
		handleSingleDescription(object.getLanguage(), object.getValue());
		return this;
	}
	
	@Override
	public Object caseMultiLanguageDescriptions(
			MultiLanguageDescriptions object) {
		for(MultiLanguageDescription mld : object.getDescriptions()) {
			doSwitch(mld);
		}
		return this;
	}
	
	@Override
	public Object caseSimpleDescription(SimpleDescription object) {
		handleSingleDescription(VCMLUtils.getDefaultLanguage(), object.getValue());
		return this;
	}
	
	abstract public void handleSingleDescription(Language language, String value);
}