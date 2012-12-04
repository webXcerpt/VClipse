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

import java.util.List;

import org.eclipse.jface.text.templates.TemplateVariable;
import org.eclipse.xtext.ui.editor.templates.AbstractTemplateVariableResolver;
import org.eclipse.xtext.ui.editor.templates.XtextTemplateContext;

import com.google.common.collect.Lists;

public class ChoiceVariableResolver extends AbstractTemplateVariableResolver {

	public ChoiceVariableResolver() {
		super("Choice", "Values for a choice variable");
	}
	
	@Override
	public List<String> resolveValues(TemplateVariable variable, XtextTemplateContext xtextTemplateContext) {
		List<?> parameter = variable.getVariableType().getParams();
		return Lists.newArrayList(parameter.toArray(new String[parameter.size()]));
	}
}
