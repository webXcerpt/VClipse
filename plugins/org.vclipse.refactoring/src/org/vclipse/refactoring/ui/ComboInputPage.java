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
import org.vclipse.refactoring.core.Refactoring;

import com.google.common.collect.Lists;

public class ComboInputPage extends WidgetProvider {
	
	private IUIRefactoringContext context;
	
	private Label label;
	private Combo combo;
	private Button button;
	
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

		combo = new Combo(mainArea, SWT.NONE);
		combo.setFont(mainArea.getFont());
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
		
		button = new Button(mainArea, SWT.CHECK);
		button.setText("Replace all occurences");
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				context.addAttribute(Refactoring.BUTTON_STATE, button.getSelection());
			}
		});
		
		context.handleWidgets();
		setControl(mainArea);
	}
}
