package org.vclipse.vcml.ui.templates;

import java.util.Iterator;
import java.util.List;

import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.jface.text.templates.TemplateVariable;
import org.eclipse.xtext.naming.IQualifiedNameProvider;
import org.eclipse.xtext.resource.IEObjectDescription;
import org.eclipse.xtext.scoping.IScope;
import org.eclipse.xtext.ui.editor.templates.AbstractTemplateVariableResolver;
import org.eclipse.xtext.ui.editor.templates.CrossReferenceTemplateVariableResolver;
import org.eclipse.xtext.ui.editor.templates.XtextTemplateContext;
import org.vclipse.vcml.services.VCMLGrammarAccess;
import org.vclipse.vcml.vcml.VcmlPackage;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.inject.Inject;

public class FilteringCrossReferenceResolver extends AbstractTemplateVariableResolver {

	public static final String VARIABLE_NAME = "FilteredCrossReference";

	@Inject
	private CrossReferenceTemplateVariableResolver crossReferenceResolver;
	
	@Inject
	private IQualifiedNameProvider nameProvider;
	
	@Inject
	private VCMLGrammarAccess grammarAccess;
	
	public FilteringCrossReferenceResolver() {
		super(VARIABLE_NAME, "Filtered cross reference");
	}
	
	@Override
	public List<String> resolveValues(TemplateVariable variable, XtextTemplateContext castedContext) {
		final List<String> resolvedValues = crossReferenceResolver.resolveValues(variable, castedContext);
		List<?> parameters = variable.getVariableType().getParams();
		if(parameters.size() == 2 && parameters.get(1) instanceof String) {
			IScope scope = castedContext.getScopeProvider().getScope(
					castedContext.getContentAssistContext().getCurrentModel(), 
						VcmlPackage.eINSTANCE.getVcmlModel_Objects());
			final EClassifier classifier = getEClassifierForGrammar((String)parameters.get(1), grammarAccess.getGrammar());
			Iterator<IEObjectDescription> iterator = Iterables.filter(scope.getAllElements(), new Predicate<IEObjectDescription>() {
				public boolean apply(IEObjectDescription input) {
					return resolvedValues.contains(nameProvider.apply(input.getEObjectOrProxy()).getLastSegment()) 
							&& input.getEObjectOrProxy().eClass().getName().equals(classifier.getName());
				}
			}).iterator();
			List<String> filteredNames = Lists.newArrayList();
			while(iterator.hasNext()) {
				filteredNames.add(nameProvider.apply(iterator.next().getEObjectOrProxy()).getLastSegment());
			}
			return filteredNames;
		}
		return resolvedValues;
	}
}
