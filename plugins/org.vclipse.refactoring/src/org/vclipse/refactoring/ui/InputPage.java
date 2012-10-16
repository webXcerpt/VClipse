/*******************************************************************************
 * Copyright (c) 2012 webXcerpt Software GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *  
 * Contributors:
 *     webXcerpt Software GmbH - initial creator
 ******************************************************************************/
package org.vclipse.refactoring.ui;

import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.wizard.IWizardContainer;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.ui.refactoring.UserInputWizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.xtext.naming.IQualifiedNameProvider;
import org.eclipse.xtext.naming.QualifiedName;
import org.vclipse.refactoring.IRefactoringUIContext;
import org.vclipse.refactoring.RefactoringPlugin;
import org.vclipse.refactoring.core.RefactoringExecuter;
import org.vclipse.refactoring.core.RefactoringTask;
import org.vclipse.refactoring.utils.RefactoringUtility;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.google.inject.Injector;

public class InputPage extends UserInputWizardPage {
	
	@Inject
	private RefactoringUtility refactoringUtility;
	
	private List<Widget> widgets;
	
	private ValidationThread validationThread;
	
	private IRefactoringUIContext context;
	
	private boolean validate = true;
	
	private class ValidationThread extends Thread {

		private final IRefactoringUIContext context;
		private final InputPage inputPage;
		
		public ValidationThread(InputPage page, IRefactoringUIContext context) {
			this.context = context;
			this.inputPage = page;
			
			this.inputPage.setErrorMessage(null);
			this.inputPage.setPageComplete(false);
		}
		
		@Override
		public void run() {
			final RefactoringTask refactoring = context.getRefactoring();
			IWizardContainer container = this.inputPage.getContainer();
			try {
				container.run(true, true, new IRunnableWithProgress() {
					@Override
					public void run(final IProgressMonitor pm) throws InvocationTargetException, InterruptedException {
						StringBuffer taskBuffer = new StringBuffer("Validating re-factoring :").append(context.getLabel());
						pm.beginTask(taskBuffer.toString(), 100);
						try {
							if(pm.isCanceled()) {
								pm.done();
								return;
							}
							// checking initial conditions
							final RefactoringStatus initialStatus = refactoring.checkInitialConditions(pm);
							if(pm.isCanceled()) {
								pm.done();
								return;
							}
							// executing re-factoring 
							refactoring.createChange(pm);								
							if(pm.isCanceled()) {
								pm.done();
								return;
							}
							// checking final conditions
							final RefactoringStatus finalStatus = refactoring.checkFinalConditions(pm);
							Display.getDefault().syncExec(new Runnable() {
								@Override
								public void run() {
									if(initialStatus.isOK() && finalStatus.isOK()) {
										inputPage.setPageComplete(true);
										inputPage.setErrorMessage(null);
									} else {
										String errorMessage = initialStatus.getMessageMatchingSeverity(IStatus.ERROR);
										if(errorMessage == null) {
											errorMessage = finalStatus.getMessageMatchingSeverity(IStatus.ERROR);
										}
										inputPage.setErrorMessage(errorMessage);
										inputPage.setPageComplete(false);				
									}
								}
							});
							pm.done();
						} catch(final CoreException exception) {
							pm.done();
							throw new InvocationTargetException(exception);
						}
					}
				});							
			} catch(final InvocationTargetException exception) {
				Display.getDefault().syncExec(new Runnable() {
					@Override
					public void run() {
						inputPage.setPageComplete(false);
						inputPage.setErrorMessage(exception.getMessage());
					}
				});
			} catch(final InterruptedException exception) {
				
			}
			container.updateButtons();
		}
	}
	
	public static InputPage getInstance(IRefactoringUIContext context) {
		Injector injector = RefactoringPlugin.getInstance().getInjector();
		InputPage instance = injector.getInstance(InputPage.class);
		instance.setContext(context);
		return instance;
	}
	
	public void setContext(IRefactoringUIContext context) {
		this.context = context;
		this.context.setPages(Lists.newArrayList(this));
	}

	public <T extends Widget> T getWidget(Class<T> type) {
		Iterator<T> iterator = Iterables.filter(widgets, type).iterator();
		return iterator.hasNext() ? iterator.next() : null;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public void createControl(Composite parent) {
		EObject element = context.getSourceElement();
		EStructuralFeature feature = context.getStructuralFeature();
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(2, false));
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		composite.setFont(parent.getFont());
		widgets.add(composite);

		Label label = new Label(composite, SWT.NONE);
		label.setText("New name: ");
		label.setLayoutData(new GridData());
		widgets.add(label);

		if(element.eClass().getEAllStructuralFeatures().contains(feature)) {
			Object value = element.eGet(feature);
			if(value instanceof EObject) {
				createTextWidget(composite);
			} else if(value instanceof List<?>) {
				List<EObject> values = (List<EObject>)value;
				if(values.size() == 1) {
					createTextWidget(composite);
				} else {
					Set<String> names = refactoringUtility.getText(values);
					if(names.isEmpty()) {
						createTextWidget(composite);
					} else {
						createComboWidget(composite, names);					
					}					
				}
			}
		} else {
			createTextWidget(composite);
		}

		final Button button = new Button(composite, SWT.CHECK);
		button.setText("Replace all occurences");
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				context.addAttribute(RefactoringExecuter.BUTTON_STATE, button.getSelection());
			}
		});
		button.setSelection(false);
		widgets.add(button);
		context.configureWidgets();
		validateWidgets();
		setControl(composite);
	}

	protected InputPage() {
		super("name");
		widgets = Lists.newArrayList();
	}
	
	private Combo createComboWidget(Composite composite, Set<String> names) {
		final Combo combo = new Combo(composite, SWT.NONE);
		combo.setFont(composite.getFont());
		combo.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false));
		combo.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent event) {
				EObject element = context.getSourceElement();
				final IQualifiedNameProvider nameProvider = refactoringUtility.getInstance(IQualifiedNameProvider.class, element);
				int index = combo.getSelectionIndex();
				if(index > -1) {
					final String item = combo.getItem(index);
					context.addAttribute(RefactoringExecuter.TEXT_FIELD_ENTRY, item);
					EStructuralFeature feature = context.getStructuralFeature();
					Object value = element.eGet(feature);
					if(value instanceof List<?>) {
						List<?> entries = (List<?>)value;
						Iterator<?> iterator = Iterables.filter(entries, new Predicate<Object>() {
							public boolean apply(Object object) {
								if(object instanceof EObject) {
									EObject eobject = (EObject)object;
									QualifiedName qualifiedName = nameProvider.getFullyQualifiedName(eobject);
									if(qualifiedName != null) {
										return qualifiedName.getLastSegment().equals(item);
									}
								}
								return false;
							}
						}).iterator();
						if(iterator.hasNext()) {
							Object next = iterator.next();
							context.setIndex(entries.indexOf(next));
						}
					}
				} else {
					String text = combo.getText();
					if(text != null && !text.isEmpty()) { 
						context.addAttribute(RefactoringExecuter.TEXT_FIELD_ENTRY, text);
						validateWidgets();
					}
				}
			}
		});
		combo.setItems(names.toArray(new String[names.size()]));
		widgets.add(combo);
		return combo;
	}

	private Text createTextWidget(Composite composite) {
		final Text text = new Text(composite, SWT.BORDER);
		text.setFont(composite.getFont());
		text.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false));
		text.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent event) {
				String string = text.getText();
				if(!string.isEmpty()) {
					context.addAttribute(RefactoringExecuter.TEXT_FIELD_ENTRY, string);
				}
				validateWidgets();
			}
		});
		widgets.add(text);
		return text;
	}

	/*
	 *	Validation on text input
	 */
	private void validateWidgets() {
		final String input = getCurrentInput();
		if(input.isEmpty() && validate) {
			setErrorMessage("Please provide a textual input for the text field.");
			setPageComplete(false);
		} else {
			if(validationThread == null) {
				validationThread = new ValidationThread(this, context);
			}
			Display.getDefault().timerExec(1000, validationThread);
		}
	}
	
	private String getCurrentInput() {
		Iterator<Text> textIterator = Iterables.filter(widgets, Text.class).iterator();
		if(textIterator.hasNext()) {
			Text text = textIterator.next();
			if(!text.isEnabled()) {
				validate = false;
			}
			return text.getText();
		} else {
			Iterator<Combo> comboIterator = Iterables.filter(widgets, Combo.class).iterator();
			if(comboIterator.hasNext()) {
				Combo combo = comboIterator.next();
				if(!combo.isEnabled()) {
					validate = false;
				}
				return combo.getText();
			}
		}
		return "";
	}
}
