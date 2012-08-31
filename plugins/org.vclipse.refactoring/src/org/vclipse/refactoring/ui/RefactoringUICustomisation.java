package org.vclipse.refactoring.ui;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.ltk.ui.refactoring.UserInputWizardPage;
import org.vclipse.refactoring.RefactoringPlugin;
import org.vclipse.refactoring.core.IRefactoringContext;
import org.vclipse.refactoring.core.RefactoringCustomisation;

import com.google.common.collect.Lists;

@SuppressWarnings("unchecked")
public class RefactoringUICustomisation extends RefactoringCustomisation {

	public static final String PAGES_PREFIX = "pages_";
	public static final String SWITCH_WIDGETS_PREFIX = "switch_widgets_";
	
	public void switchWidgets(IRefactoringContext context) {
		EObject element = context.getSourceElement();
		String prefix = SWITCH_WIDGETS_PREFIX + context.getType();
		Pair<EObject, Method> pair = getMethod(element, context.getStructuralFeature(), prefix);
		if(pair != null) {
			try {
				pair.getSecond().invoke(this, new Object[]{context, pair.getFirst()});
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException exception) {
				RefactoringPlugin.log(exception.getMessage(), exception);
			}
		} 
	}
	
	public List<? extends UserInputWizardPage> getPages(IRefactoringContext context) {
		EObject element = context.getSourceElement();
		String prefix = PAGES_PREFIX + context.getType();
		Pair<EObject, Method> pair = getMethod(element, context.getStructuralFeature(), prefix);
		if(pair != null) {
			try {
				return ((List<? extends UserInputWizardPage>)pair.getSecond().invoke(this, new Object[]{context, pair.getFirst()}));
			} catch(IllegalAccessException | IllegalArgumentException | InvocationTargetException exception) {
				RefactoringPlugin.log(exception.getMessage(), exception);
			}
		}
		return Lists.newArrayList();
	}
}
