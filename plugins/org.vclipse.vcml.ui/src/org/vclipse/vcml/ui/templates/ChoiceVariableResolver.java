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
