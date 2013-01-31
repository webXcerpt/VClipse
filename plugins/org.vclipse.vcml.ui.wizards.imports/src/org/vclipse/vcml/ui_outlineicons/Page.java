/*******************************************************************************
 * Copyright (c) 2010 - 2013 webXcerpt Software GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *  
 * Contributors:
 *     	webXcerpt Software GmbH - initial creator
 * 		www.webxcerpt.com
 ******************************************************************************/
package org.vclipse.vcml.ui_outlineicons;

import java.io.File;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class Page extends WizardPage implements IWizardPage {

	private static final String ERROR_MESSAGE = "Please provide a jar/zip file containing icons.";

	private static final String[] JAR_EXTENSION = new String[]{"*.jar", "*.zip"};

	private File jarFile;
	
	private Text fileText;
	
	protected Page(String pageName) {
		this(pageName, null, null);
	}
	
	protected Page(String pageName, String title, ImageDescriptor titleImage) {
		super(pageName, "Import wizard for SAP icons", titleImage);
	}
	
	public void createControl(Composite parent) {
		Composite mainArea = new Composite(parent, SWT.NONE);
		mainArea.setLayout(new GridLayout(3, false));
		
		new Label(mainArea, SWT.NONE).setText("Select jar/zip file containing icons: ");
		
		fileText = new Text(mainArea, SWT.BORDER | SWT.READ_ONLY);
		fileText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				if(!fileText.getText().isEmpty() && jarFile != null) {
					setPageComplete(true);
					setErrorMessage(null);
					setDescription("This wizard can be used for the import of the SAP images for the VClipse vcml plug-in");
				} else {
					setPageComplete(false);
					setErrorMessage(ERROR_MESSAGE);
				}
			}
		});
		fileText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		Button button = new Button(mainArea, SWT.PUSH);
		button.setText("Browse...");
		button.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				FileDialog fd = new FileDialog(getShell());
				fd.setFilterExtensions(JAR_EXTENSION);
				fd.setText("Provide a jar file containing SAP images");
				String result = fd.open();
				jarFile = result == null ? null : new File(result);
				fileText.setText(jarFile == null ? "" : jarFile.getName());
			}
		});
		
		setPageComplete(false);
		setErrorMessage(ERROR_MESSAGE);
		setControl(mainArea);
	}
	
	public File getJarFile() {
		return jarFile;
	}
}
