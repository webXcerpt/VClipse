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

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.emf.common.util.BasicDiagnostic;
import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EValidator;
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
import org.vclipse.refactoring.RefactoringPlugin;
import org.vclipse.refactoring.core.LanguageRefactoringProcessor;
import org.vclipse.refactoring.core.ModelBasedChange;
import org.vclipse.refactoring.core.Refactoring;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.google.inject.Injector;

public class InputPage extends UserInputWizardPage {
	
	@Inject
	private RefactoringUtility refactoringUtility;
	
	@Inject
	private LanguageRefactoringProcessor processor;
	
	private IUIRefactoringContext context;
	private List<Widget> widgets;
	private Thread validationThread;
	
	public static InputPage getInstance(IUIRefactoringContext context) {
		Injector injector = RefactoringPlugin.getInstance().getInjector();
		InputPage instance = injector.getInstance(InputPage.class);
		instance.setContext(context);
		return instance;
	}
	
	public void setContext(IUIRefactoringContext context) {
		this.context = context;
		this.context.setPages(Lists.newArrayList(this));
	}

	protected InputPage() {
		super("name");
		widgets = Lists.newArrayList();
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
				Set<String> names = refactoringUtility.getText(values);
				if(names.isEmpty()) {
					createTextWidget(composite);
				} else {
					createComboWidget(composite, names);					
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
				context.addAttribute(Refactoring.BUTTON_STATE, button.getSelection());
			}
		});
		button.setSelection(false);
		widgets.add(button);
		context.handleWidgets();
		validateWidgets();
		setControl(composite);
	}

	private Combo createComboWidget(Composite composite, Set<String> names) {
		final Combo combo = new Combo(composite, SWT.NONE);
		combo.setFont(composite.getFont());
		combo.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false));
		combo.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				int index = combo.getSelectionIndex();
				if(index > -1) {
					String item = combo.getItem(index);
					context.addAttribute(Refactoring.TEXT_FIELD_ENTRY, item);					
				} else {
					String text = combo.getText();
					if(text != null && !text.isEmpty()) { 
						context.addAttribute(Refactoring.TEXT_FIELD_ENTRY, text);		
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
				validateWidgets();
			}
		});
		widgets.add(text);
		return text;
	}
	
	public <T extends Widget> T getWidget(Class<T> type) {
		Iterator<T> iterator = Iterables.filter(widgets, type).iterator();
		return iterator.hasNext() ? iterator.next() : null;
	}
	
	private void validateWidgets() {
		setPageComplete(false);
		Iterator<Text> iterator = Iterables.filter(widgets, Text.class).iterator();
		if(iterator.hasNext()) {
			Text text = iterator.next();
			if(text.isEnabled()) {
				final String string = text.getText();
				context.addAttribute(Refactoring.TEXT_FIELD_ENTRY, string);				
				if(string.isEmpty()) {
					setErrorMessage("Please provide a textual input for the text field.");
				} else {
					if(validationThread == null) {
						validationThread = new Thread() {
							@Override
							public void run() {
								validateChangeModel();
								setErrorMessage(null);
								setPageComplete(true);
							}
						};
					}
					Display.getDefault().timerExec(1000, validationThread);
				}
			}
		}
	}
	
	private void validateChangeModel() {
		EObject element = context.getSourceElement();
		final  EValidator.Registry validator = refactoringUtility.getInstance(element, EValidator.Registry.class);
		if(validator != null) {
			final Object[] elements = processor.getElements();
			if(elements.length > 0) {
				if(elements[0] instanceof ModelBasedChange) {
					ModelBasedChange mbc = (ModelBasedChange)elements[0];
					final EObject changed = mbc.getChanged();
					EValidator evalidator = validator.getEValidator(changed.eClass().getEPackage());
					BasicDiagnostic diagnosticsCollection = new BasicDiagnostic();
					evalidator.validate(changed, diagnosticsCollection, Maps.newHashMap());
					List<Diagnostic> diagnostics = diagnosticsCollection.getChildren();
					final Iterator<Diagnostic> iterator = Iterables.filter(diagnostics, new Predicate<Diagnostic>() {
						public boolean apply(Diagnostic diagnostic) {
							return Diagnostic.ERROR == diagnostic.getSeverity();
						}
					}).iterator();
					Display.getDefault().asyncExec(new Runnable() {
						@Override
						public void run() {
							if(iterator.hasNext()) {
								Diagnostic diagnostic = iterator.next();
								setErrorMessage(diagnostic.getMessage());
								setPageComplete(false);
							} else {
								setErrorMessage(null);
								setPageComplete(true);
							}
						}
					});
				}
			}
		}
	}
}
