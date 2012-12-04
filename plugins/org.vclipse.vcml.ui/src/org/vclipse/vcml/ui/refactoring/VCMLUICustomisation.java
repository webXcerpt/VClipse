package org.vclipse.vcml.ui.refactoring;

import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.ltk.ui.refactoring.UserInputWizardPage;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.vclipse.refactoring.IRefactoringUIContext;
import org.vclipse.refactoring.ui.InputPage;
import org.vclipse.refactoring.ui.RefactoringUIConfiguration;
import org.vclipse.vcml.vcml.PFunction;
import org.vclipse.vcml.vcml.SimpleDescription;
import org.vclipse.vcml.vcml.VcmlPackage;

import com.google.common.collect.Lists;

public class VCMLUICustomisation extends RefactoringUIConfiguration {

	private static final VcmlPackage VCML_PACKAGE = VcmlPackage.eINSTANCE;
	
	public List<? extends UserInputWizardPage> pages_Replace(IRefactoringUIContext context) {
		List<UserInputWizardPage> pages = Lists.newArrayList();
		EObject element = context.getSourceElement();
		if(element instanceof PFunction || 
				element instanceof SimpleDescription ||
					VCML_PACKAGE.getVcmlModel_Objects() == context.getStructuralFeature()) {
			pages.add(InputPage.getInstance(context));
		}
		return pages;
	}
	
	public void switch_widgets_Replace(IRefactoringUIContext context) {
		for(UserInputWizardPage page : context.getPages()) {
			if(page instanceof InputPage) {
				InputPage dip = (InputPage)page;
				Label label = dip.getWidget(InputPage.NAME_LABEL, Label.class);
				EObject element = context.getSourceElement();
				if(element instanceof PFunction) {
					label.setText("New literal value: ");					
				} else if(element instanceof SimpleDescription) {
					label.setText("New description: ");
				} else if(VCML_PACKAGE.getVcmlModel_Objects() == context.getStructuralFeature()) {
					Text text = dip.getWidget(InputPage.TEXT_INPUT, Text.class);
					text.setEnabled(false);
					dip.setPageComplete(true);
				}
			}
		}
	}
}
