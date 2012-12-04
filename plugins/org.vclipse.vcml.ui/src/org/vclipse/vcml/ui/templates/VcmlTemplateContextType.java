/*******************************************************************************
 * Copyright (c) 2012 webXcerpt Software GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    webXcerpt Software GmbH - initial creator
 ******************************************************************************/
package org.vclipse.vcml.ui.templates;

import org.eclipse.xtext.ui.editor.templates.XtextTemplateContextType;

import com.google.inject.Inject;

public class VcmlTemplateContextType extends XtextTemplateContextType {

	@Inject
	public void setDomainValuesResolver(ChoiceVariableResolver resolver) {
		addResolver(resolver);
	}
	
	@Inject
	public void setFilteredCrossReferenceResolver(FilteringCrossReferenceResolver resolver) {
		addResolver(resolver);
	}
	
	@Inject
	public void setDomainValuesResolver(DomainValuesVariableResolver resolver) {
		addResolver(resolver);
	}
	
	@Inject
	public void setDepdencyResolver(DependencyResolver resolver) {
		addResolver(resolver);
	}
}
