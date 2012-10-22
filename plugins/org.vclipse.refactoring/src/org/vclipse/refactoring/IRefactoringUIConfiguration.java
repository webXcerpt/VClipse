package org.vclipse.refactoring;

import java.util.List;

import org.eclipse.ltk.ui.refactoring.UserInputWizardPage;

public interface IRefactoringUIConfiguration {

	public void configureWidgets(IRefactoringContext context);
	
	public List<? extends UserInputWizardPage> provideWizardPages(IRefactoringContext context);
	
}
