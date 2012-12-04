/*******************************************************************************
 * Copyright (c) 2012 webXcerpt Software GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    webXcerpt Software GmbH - initial creator
 ******************************************************************************/
package org.vclipse.vcml.ui.refactoring;

import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.ltk.ui.refactoring.UserInputWizardPage;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.vclipse.refactoring.IRefactoringUIContext;
import org.vclipse.refactoring.ui.InputPage;
import org.vclipse.refactoring.ui.InputPageUpdate;
import org.vclipse.refactoring.ui.ModifyListenerDelegate;
import org.vclipse.refactoring.ui.RefactoringUIConfiguration;
import org.vclipse.vcml.refactoring.ConstraintRefactorings;
import org.vclipse.vcml.vcml.ConstraintSource;
import org.vclipse.vcml.vcml.PFunction;
import org.vclipse.vcml.vcml.SimpleDescription;
import org.vclipse.vcml.vcml.VcmlPackage;

import com.google.common.collect.Lists;

public class VCMLUICustomisation extends RefactoringUIConfiguration {

	private static final VcmlPackage VCML_PACKAGE = VcmlPackage.eINSTANCE;
	
	public List<? extends UserInputWizardPage> pages_Extract(IRefactoringUIContext context) {
		List<UserInputWizardPage> pages = Lists.newArrayList();
		EStructuralFeature feature = context.getStructuralFeature();
		if(VCML_PACKAGE.getConstraint_Source() == feature) {
			pages.add(InputPage.getInstance(context));
		}
		return pages;
	}
	
	public void switch_widgets_Extract(IRefactoringUIContext context) {
		for(UserInputWizardPage curPage : context.getPages()) {
			if(curPage instanceof InputPage) {
				final EObject element = context.getSourceElement();
				if(element instanceof ConstraintSource) {
					final ConstraintSource constraintSource = (ConstraintSource)element;
					final InputPage page = (InputPage)curPage;
					page.getWidget(InputPage.NAME_LABEL, Label.class).setText("Restrictions amount/ file:");
					Group group = page.getWidget(InputPage.OPTIONS_GROUP, Group.class);
					group.dispose();
					Text widget = page.getWidget(InputPage.TEXT_INPUT, Text.class);
					addModifyListener(widget, new ModifyListenerDelegate() {
						@Override
						public void modifyText(ModifyEvent event) {
							Object source = event.getSource();
							if(source instanceof Text) {
								String text = ((Text)source).getText();
								if(!text.isEmpty()) {
									try {
										int wantEntriesInFile = Integer.parseInt(text);
										int existingAmount = constraintSource.getRestrictions().size();
										int gotEntriesInFile = existingAmount / wantEntriesInFile;
										if(gotEntriesInFile > ConstraintRefactorings.MINIMUM_SUBLISTS && gotEntriesInFile < ConstraintRefactorings.MIN_CONSTRAINTS_AMOUNT) {
											InputPageUpdate.updateNoErrors(page);
											super.modifyText(event);	
										} else {
											String errorMessage = "Please select a number > " + 
													ConstraintRefactorings.MIN_CONSTRAINTS_AMOUNT + " and < " + ConstraintRefactorings.MIN_CONSTRAINTS_AMOUNT;
											InputPageUpdate.update(page, errorMessage); 
										}
									} catch(NumberFormatException exception) {
										String errorMessage = "The value in the input field should represent an integer.";
										InputPageUpdate.update(page, errorMessage);
									}
								} else {
									super.modifyText(event);
								}
							}
						}
					});
				}
			}
		}
	}
	
	public List<? extends UserInputWizardPage> pages_Replace(IRefactoringUIContext context) {
		List<UserInputWizardPage> pages = Lists.newArrayList();
		EObject element = context.getSourceElement();
		if(element instanceof PFunction || element instanceof SimpleDescription || VCML_PACKAGE.getVcmlModel_Objects() == context.getStructuralFeature()) {
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
