package org.vclipse.vcml.refactoring;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.resource.IReferenceDescription;
import org.vclipse.refactoring.IRefactoringContext;
import org.vclipse.refactoring.core.DefaultRefactoringExecuter;
import org.vclipse.vcml.vcml.SimpleDescription;

public class VCMLRefactoring extends DependenciesRefactorings {

	@SuppressWarnings("unchecked")
	public void refactoring_Replace_objects(IRefactoringContext context) {
		EObject element = context.getSourceElement();
		EStructuralFeature feature = context.getStructuralFeature();
		boolean continueRefactoring = true;
		if(feature.equals(element.eContainmentFeature())) {
			EObject container = element.eContainer();
			Object value = container.eGet(feature);
			if(value instanceof List<?>) {
				List<EObject> entries = (List<EObject>)value;
				int index = context.getIndex();
				if(index >= 0 && index < entries.size()) {
					Map<?, ?> attributes = context.getAttributes();
					Object buttonState = attributes.get(DefaultRefactoringExecuter.BUTTON_STATE);
					if(buttonState instanceof Boolean && (Boolean)buttonState) {
						EObject entry = entries.get(index);
						Resource resource = entry.eResource();
						Iterable<IReferenceDescription> references = referencesFinder.getReferences(entry);
						for(IReferenceDescription curReference : references) {
							URI uri = curReference.getSourceEObjectUri();
							EObject eobject = resource.getEObject(uri.fragment());
							EReference reference = curReference.getEReference();
							if(reference.isMany()) {
								EList<EObject> referencedEntries = (EList<EObject>)eobject.eGet(reference);
								EObject foundEntry = search.findEntry(element, referencedEntries);
								if(foundEntry != null) {
									int entryIndex = referencedEntries.indexOf(foundEntry);
									if(entryIndex > -1 && entryIndex < referencedEntries.size()) {
										referencedEntries.remove(entryIndex);
									}
								}
							} else {
								eobject.eSet(reference, null);
							}
						}
					}
					if(continueRefactoring) {
						entries.remove(index);						
					}
				}
			}
		}
	}
	
	public void refactoring_Replace_SimpleDescription(IRefactoringContext context) {
		Map<?, ?> attributes = context.getAttributes();
		Boolean replaceOccurences = (Boolean)attributes.get(DefaultRefactoringExecuter.BUTTON_STATE);
		String newDescription = (String)attributes.get(DefaultRefactoringExecuter.TEXT_FIELD_ENTRY);
		SimpleDescription sd = (SimpleDescription)context.getSourceElement();
		if(replaceOccurences != null && replaceOccurences) {
			Iterator<EObject> foundEntries = search.findEntries(sd).iterator();
			while(foundEntries.hasNext()) {
				((SimpleDescription)foundEntries.next()).setValue(newDescription);
			}
		}
	}
}
