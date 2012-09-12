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

import org.apache.log4j.Logger;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
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
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;
import org.vclipse.refactoring.RefactoringPlugin;
import org.vclipse.refactoring.core.LanguageRefactoringProcessor;
import org.vclipse.refactoring.core.Refactoring;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.google.inject.Injector;

public class InputPage extends UserInputWizardPage {
	
	private Logger logger = Logger.getLogger(InputPage.class);
	
	private IUIRefactoringContext context;
	private List<Widget> widgets;
	
	@Inject
	private RefactoringUtility refactoringUtility;
	
	@Inject
	private LanguageRefactoringProcessor processor;
	
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
		if(element == null) {
			logger.error("element should not be null");
		} else if(feature == null) {
			logger.error("feature should not be null");
		} else {
			Composite composite = new Composite(parent, SWT.NONE);
			composite.setLayout(new GridLayout(2, false));
			composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
			composite.setFont(parent.getFont());
			widgets.add(composite);

			Label label = new Label(composite, SWT.NONE);
			label.setText("New name: ");
			label.setLayoutData(new GridData());
			widgets.add(label);

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
			
			final Button button = new Button(composite, SWT.CHECK);
			button.setText("Replace all occurences");
			button.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent event) {
					context.addAttribute(Refactoring.BUTTON_STATE, button.getSelection());
				}
			});
			widgets.add(button);
			context.handleWidgets();
			validate();
			setControl(composite);
		}
	}

	private void createComboWidget(Composite composite, Set<String> names) {
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
	}

	private void createTextWidget(Composite composite) {
		final Text text = new Text(composite, SWT.BORDER);
		text.setFont(composite.getFont());
		text.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false));
		text.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				String string = text.getText();
				if(!string.isEmpty()) {
					context.addAttribute(Refactoring.TEXT_FIELD_ENTRY, string);
				}
				validate();
			}
		});
		widgets.add(text);
	}
	
	public <T extends Widget> T getWidget(Class<T> type) {
		Iterator<T> iterator = Iterables.filter(widgets, type).iterator();
		return iterator.hasNext() ? iterator.next() : null;
	}
	
	private void validate() {
//		EObject element = context.getSourceElement();
//		EValidator.Registry validator = refactoringUtility.getValidator(element);
//		if(validator != null) {
//			EValidator eValidator = validator.getEValidator(element.eClass().getEPackage());
//			BasicDiagnostic diagnostics = new BasicDiagnostic();
//			Object[] elements = processor.getElements();
//			if(elements.length > 0) {
//				if(elements[0] instanceof ModelChange) {
//					ModelChange change = (ModelChange)elements[0];
//					EObject changed = change.getChanged();
//					ISerializer serializer = refactoringUtility.getSerializer(element);
//					try {
//						serializer.serialize(changed);
//					} catch(Exception exception) {
//						System.out.println(exception.getMessage());
//					}
//					boolean success = eValidator.validate(element, diagnostics, Maps.newHashMap());
//					System.out.println("");					
//				}
//			}
//		}
//		Iterator<Text> iterator = Iterables.filter(widgets, Text.class).iterator();
//		if(iterator.hasNext()) {
//			Text text = iterator.next();
//			if(text.getEnabled()) {
//				String newName = text.getText();
//				if(!newName.isEmpty()) {
//					try {
//						setPageComplete(true);
//						setErrorMessage(null);
//						setDescription("Please push OK button for refactoring execution.");
//						context.addAttribute(Refactoring.TEXT_FIELD_ENTRY, newName);
//					} catch(ValueConverterException exception) {
//						setPageComplete(false);
//						setErrorMessage("Text field contains not valid entry " + newName);
//					}
//				} else {
//					setPageComplete(false);
//					setErrorMessage("Text field contains not valid entry " + newName);
//				}
//			}
//		}
	}
}
