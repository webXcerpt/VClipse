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
package org.vclipse.vcml.utils;

import org.vclipse.vcml.vcml.Documentation;
import org.vclipse.vcml.vcml.FormattedDocumentationBlock;
import org.vclipse.vcml.vcml.Language;
import org.vclipse.vcml.vcml.MultipleLanguageDocumentation;
import org.vclipse.vcml.vcml.MultipleLanguageDocumentation_LanguageBlock;
import org.vclipse.vcml.vcml.SimpleDocumentation;
import org.vclipse.vcml.vcml.util.VcmlSwitch;

public abstract class DocumentationHandler extends VcmlSwitch<Object> {
	
	private Language language = VcmlUtils.getDefaultLanguage();
	
	public void handleDocumentation(Documentation documentation) {
		if(documentation != null) {
			this.doSwitch(documentation);
		}
	}
	
	@Override
	public Object caseFormattedDocumentationBlock(FormattedDocumentationBlock object) {
		String format = object.getFormat();
		addDocumentationEntry(language, object.getValue(), format==null ? VcmlUtils.DEFAULT_FORMAT : format);
		return this;
	}
	
	@Override
	public Object caseMultipleLanguageDocumentation_LanguageBlock(MultipleLanguageDocumentation_LanguageBlock object) {
		language = object.getLanguage();
		for(FormattedDocumentationBlock fdb : object.getFormattedDocumentationBlocks()) {
			doSwitch(fdb);
		}
		return this;
	}
	
	@Override
	public Object caseMultipleLanguageDocumentation(MultipleLanguageDocumentation object) {
		for(MultipleLanguageDocumentation_LanguageBlock lb : object.getLanguageblocks()) {
			doSwitch(lb);
		}
		return this;
	}
	
	@Override
	public Object caseSimpleDocumentation(SimpleDocumentation object) {
		addDocumentationEntry(language, object.getValue(), VcmlUtils.DEFAULT_FORMAT);
		return this;
	}

	abstract public void addDocumentationEntry(Language language, String text, String format);
}
