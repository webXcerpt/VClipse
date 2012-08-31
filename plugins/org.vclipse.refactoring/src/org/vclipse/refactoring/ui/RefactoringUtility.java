package org.vclipse.refactoring.ui;

import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.vclipse.base.VClipseStrings;

public class RefactoringUtility {

	public String getRefactoringText(IUIRefactoringContext context) {
		EStructuralFeature feature = context.getStructuralFeature();
		EObject element = context.getSourceElement();
		StringBuffer text = new StringBuffer();
		text.append(context.getType().name() + " ");
		if(feature != null) {
			appendToBuffer(text, feature.getName(), true);
		}
		appendToBuffer(text, element.eClass().getName(), false);
		return text.toString();
	}
	
	private void appendToBuffer(StringBuffer buffer, String text, boolean handleLastIndex) {
		List<String> parts = VClipseStrings.splitCamelCase(text);
		for(String part : parts) {
			buffer.append(part.toLowerCase());
			int indexOf = parts.indexOf(part);
			if(indexOf < parts.size()) {
				buffer.append(" ");
			}
			if(handleLastIndex && (indexOf == parts.size() - 1)) {
				buffer.append(" for ");
			}
		}
	}
}
