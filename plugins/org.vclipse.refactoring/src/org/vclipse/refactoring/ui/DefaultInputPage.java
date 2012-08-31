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

import java.lang.reflect.Field;

import org.eclipse.ltk.ui.refactoring.UserInputWizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.xtext.conversion.ValueConverterException;
import org.vclipse.refactoring.RefactoringPlugin;
import org.vclipse.refactoring.core.Refactoring;

import com.google.common.collect.Lists;

public class DefaultInputPage extends UserInputWizardPage {
	
	private IUIRefactoringContext context;
	
	private Label label;
	private Text text;
	private Button button;
	
	public DefaultInputPage(IUIRefactoringContext context) {
		super("name");
		this.context = context;
		context.setPages(Lists.newArrayList(this));
	}

	@Override
	public void createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(2, false));
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		composite.setFont(parent.getFont());

		label = new Label(composite, SWT.NONE);
		label.setText("New name: ");
		label.setLayoutData(new GridData());

		text = new Text(composite, SWT.BORDER);
		text.setFont(composite.getFont());
		text.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false));
		text.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				validate();
			}
		});
		
		button = new Button(composite, SWT.CHECK);
		button.setText("Replace all occurences");
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				context.addAttribute(Refactoring.BUTTON_STATE, button.getSelection());
			}
		});
		context.handleWidgets();
		validate();
		setControl(composite);
	}
	
	@SuppressWarnings("unchecked")
	public <T extends Widget> T getWidget(String name, Class<T> type) {
		if(name == null || name.isEmpty()) {
			return null;
		}
		for(Field field : getClass().getDeclaredFields()) {
			if(field.getName().equals(name)) {
				field.setAccessible(true);
				Object fieldValue;
				try {
					fieldValue = field.get(this);
					if(type.isAssignableFrom(fieldValue.getClass())) {
						return (T)fieldValue;
					}
				} catch (Exception e) {
					RefactoringPlugin.log(e.getMessage(), e);
				}
			}
		}
		return null;
	}
	
	private void validate() {
		if(text.getEnabled()) {
			String newName = text.getText();
			if(!newName.isEmpty()) {
				try {
					setPageComplete(true);
					setErrorMessage(null);
					setDescription("Please push OK button for refactoring execution.");
					context.addAttribute(Refactoring.TEXT_FIELD_ENTRY, newName);
				} catch(ValueConverterException exception) {
					setPageComplete(false);
					setErrorMessage("Text field contains not valid entry " + newName);
				}
			} else {
				setPageComplete(false);
				setErrorMessage("Text field contains not valid entry " + newName);
			}
		}
	}
}
