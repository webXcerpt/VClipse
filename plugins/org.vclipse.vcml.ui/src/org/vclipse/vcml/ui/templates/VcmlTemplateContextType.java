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

}
