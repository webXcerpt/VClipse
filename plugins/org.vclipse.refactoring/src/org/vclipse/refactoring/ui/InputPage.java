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
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.jface.wizard.IWizardContainer;
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
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.xtext.naming.IQualifiedNameProvider;
import org.eclipse.xtext.naming.QualifiedName;
import org.vclipse.refactoring.IRefactoringUIContext;
import org.vclipse.refactoring.RefactoringPlugin;
import org.vclipse.refactoring.core.DefaultRefactoringExecuter;
import org.vclipse.refactoring.utils.Extensions;
import org.vclipse.refactoring.utils.Labels;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.google.inject.Injector;

public class InputPage extends UserInputWizardPage {
	
	public static final String NAME_LABEL = "nameLabel";
	public static final String TEXT_INPUT = DefaultRefactoringExecuter.TEXT_FIELD_ENTRY;
	public static final String OPTIONS_GROUP = "optionsGroup";
	
	@Inject
	private Labels refactoringUtility;
	
	@Inject
	private Extensions extensions;
	
	private Map<String, Widget> widgets;
	
	private InputValidation validationThread;
	
	private IRefactoringUIContext context;
	
	private boolean validate = true;
	
	public static InputPage getInstance(IRefactoringUIContext context) {
		Injector injector = RefactoringPlugin.getInstance().getInjector();
		InputPage instance = injector.getInstance(InputPage.class);
		instance.setContext(context);
		return instance;
	}

	@SuppressWarnings("unchecked")
	public <T extends  Widget> T getWidget(final String name, final Class<T> type) {
		Iterator<Widget> iterator = Iterables.filter(widgets.values(), new Predicate<Widget>() {
			public boolean apply(Widget widget) {
				return widget.getClass() == type && widgets.get(name) == widget;
			}
		}).iterator();
		return iterator.hasNext() ? (T)iterator.next() : null;
	}
	
	@Override
 	public void createControl(Composite parent) {
		EObject element = context.getSourceElement();
		EStructuralFeature feature = context.getStructuralFeature();

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(2, false));
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		composite.setFont(parent.getFont());
		widgets.put("mainBackground", composite);

		Label label = new Label(composite, SWT.NONE);
		label.setText("New name: ");
		label.setLayoutData(new GridData());
		widgets.put(NAME_LABEL, label);

		if(element.eClass().getEAllStructuralFeatures().contains(feature)) {
			Object value = element.eGet(feature);
			if(value instanceof EObject) {
				createTextWidget(composite);
			} else if(value instanceof List<?>) {
				createTextWidget(composite);
//				List<EObject> values = (List<EObject>)value;
//				if(values.size() == 1) {
//				} else {
//					Set<String> names = refactoringUtility.getText(values);
//					if(names.isEmpty()) {
//						createTextWidget(composite);
//					} else {
//						createComboWidget(composite, names);					
//					}					
//				}
			}
		} else {
			createTextWidget(composite);
		}
		
		Group optionsGroup = new Group(composite, SWT.NONE);
		optionsGroup.setLayout(new GridLayout());
		GridData gridData = new GridData(GridData.FILL_BOTH);
		gridData.horizontalSpan = 2;
		optionsGroup.setLayoutData(gridData);
		optionsGroup.setText(" Options ");
		widgets.put(OPTIONS_GROUP, optionsGroup);
		
		final Button occurencesButton = new Button(optionsGroup, SWT.CHECK);
		occurencesButton.setText("Replace all occurences");
		occurencesButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				context.addAttribute(DefaultRefactoringExecuter.BUTTON_STATE, occurencesButton.getSelection());
				validateWidgets();
			}
		});
		occurencesButton.setSelection(false);
		widgets.put("occurencesButton", occurencesButton);
		
//		final Button removeExistingEObject = new Button(optionsGroup, SWT.CHECK);
//		removeExistingEObject.setText("Remove existing source");
//		removeExistingEObject.addSelectionListener(new SelectionAdapter() {
//			@Override
//			public void widgetSelected(SelectionEvent event) {
//				context.addAttribute(DefaultRefactoringExecuter.REMOVE_EXISTING_EOBJECT, removeExistingEObject.getSelection());
//				validateWidgets();
//			}
//		});
//		removeExistingEObject.setSelection(false);
//		removeExistingEObject.setEnabled(false);
//		widgets.add(removeExistingEObject);
		
		context.configureWidgets();
		validateWidgets();
		setControl(composite);
	}
	
	protected InputPage() {
		super("name");
		widgets = Maps.newHashMap();
	}
	
	protected void setContext(IRefactoringUIContext context) {
		this.context = context;
		this.context.setPages(Lists.newArrayList(this));
	}
	
	protected IRefactoringUIContext getContext() {
		return context;
	}
	
	protected IWizardContainer getContainer() {
		return super.getContainer();
	}

	private Combo createComboWidget(Composite composite, Set<String> names) {
		final Combo combo = new Combo(composite, SWT.NONE);
		combo.setFont(composite.getFont());
		combo.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false));
		combo.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent event) {
				EObject element = context.getSourceElement();
				final IQualifiedNameProvider nameProvider = extensions.getInstance(IQualifiedNameProvider.class, element);
				int index = combo.getSelectionIndex();
				if(index > -1) {
					final String item = combo.getItem(index);
					context.addAttribute(DefaultRefactoringExecuter.TEXT_FIELD_ENTRY, item);
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
						context.addAttribute(DefaultRefactoringExecuter.TEXT_FIELD_ENTRY, text);
						validateWidgets();
					}
				}
			}
		});
		combo.setItems(names.toArray(new String[names.size()]));
		widgets.put("selectionCombo", combo);
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
					context.addAttribute(DefaultRefactoringExecuter.TEXT_FIELD_ENTRY, string);
				}
				validateWidgets();
			}
		});
		widgets.put(DefaultRefactoringExecuter.TEXT_FIELD_ENTRY, text);
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
				validationThread = new InputValidation(this);
			} 
			if(!validationThread.isAlive()) {
				Display.getDefault().timerExec(1000, validationThread);
			}
		}
	}
	
	private String getCurrentInput() {
		Text text = getWidget(DefaultRefactoringExecuter.TEXT_FIELD_ENTRY, Text.class);
		if(text != null) {
			if(!text.isEnabled()) {
				validate = false;				
			}
			return text.getText();
		}
		Combo combo = getWidget(DefaultRefactoringExecuter.TEXT_FIELD_ENTRY, Combo.class);
		if(combo != null) {
			if(combo.isEnabled()) {
				validate = false;
			}
			return combo.getText();
		}
		return "";
	}
}