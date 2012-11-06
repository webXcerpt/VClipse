package org.vclipse.refactoring.tests.utils

import org.vclipse.refactoring.guice.RefactoringModule
import org.vclipse.refactoring.RefactoringPlugin

class RefactoringTestModule extends RefactoringModule {

	new() {
		super(RefactoringPlugin::getInstance())
	}
}