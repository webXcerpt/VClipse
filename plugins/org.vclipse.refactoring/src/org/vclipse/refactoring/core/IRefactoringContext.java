package org.vclipse.refactoring.core;

import java.util.Map;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;

import com.google.inject.ImplementedBy;

@ImplementedBy(RefactoringContext.class)
public interface IRefactoringContext {
	
	public void setSourceElement(EObject object);
	
	public EObject getSourceElement();
	
	public void setStructuralFeature(EStructuralFeature feature);
	
	public EStructuralFeature getStructuralFeature();
	
	public void setType(RefactoringType type);
	
	public RefactoringType getType();
	
	public void addAttribute(Object key, Object value);

	public Map<?, ?> getAttributes();
	
	public String getDescription();
	
}
