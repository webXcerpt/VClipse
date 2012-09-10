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
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Widget;
import org.vclipse.refactoring.RefactoringPlugin;
import org.vclipse.refactoring.core.Refactoring;

import com.google.common.collect.Lists;

public class ComboInputPage extends UserInputWizardPage {
	
	private IUIRefactoringContext context;
	
	private Label label;
	private Combo combo;
	
	private Composite mainArea;
	
	public ComboInputPage(IUIRefactoringContext context) {
		super("name");
		this.context = context;
		context.setPages(Lists.newArrayList(this));
	}

	@Override
	public void createControl(Composite parent) {
		mainArea = new Composite(parent, SWT.NONE);
		mainArea.setLayout(new GridLayout(2, false));
		mainArea.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		mainArea.setFont(parent.getFont());

		label = new Label(mainArea, SWT.NONE);
		label.setText("New name: ");
		label.setLayoutData(new GridData());

		combo = new Combo(mainArea, SWT.READ_ONLY);
		combo.setFont(mainArea.getFont());
		combo.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false));
		combo.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				int index = combo.getSelectionIndex();
				if(index > -1) {
					context.addAttribute(Refactoring.TEXT_FIELD_ENTRY, combo.getItem(index));					
				}
			}
		});
		
		context.handleWidgets();
		setControl(mainArea);
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
	
}
