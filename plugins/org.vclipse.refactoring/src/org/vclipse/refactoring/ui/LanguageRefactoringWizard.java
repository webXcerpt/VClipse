package org.vclipse.refactoring.ui;

import java.util.List;

import org.eclipse.ltk.core.refactoring.Refactoring;
import org.eclipse.ltk.ui.refactoring.RefactoringWizard;
import org.eclipse.ltk.ui.refactoring.UserInputWizardPage;

import com.google.common.collect.Lists;

public class LanguageRefactoringWizard extends RefactoringWizard {

	private List<? extends UserInputWizardPage> pages;

	public LanguageRefactoringWizard(List<? extends UserInputWizardPage> pages, Refactoring refactoring, int flags) {
		super(refactoring, flags);
		setDefaultPageTitle("Language Refactoring");
		setWindowTitle("Language Refactoring");
		this.pages = pages == null ? Lists.<UserInputWizardPage>newArrayList() : pages;
	}

	@Override
	protected void addUserInputPages() {
		for(UserInputWizardPage page : pages) {
			addPage(page);
		}
	}
}
