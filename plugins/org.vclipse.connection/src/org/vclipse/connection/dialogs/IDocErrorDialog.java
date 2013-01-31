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
package org.vclipse.connection.dialogs;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Link;
import org.eclipse.ui.PlatformUI;

/**
 * Error dialog offering the user to put the JCo dll and jar files into the unpacked jco plugin.
 */
public class IDocErrorDialog extends MessageDialog {

	/**
	 * String for the title of the dialog.
	 */
	public static final String TITLE_MESSAGE = "Error: SAP IDoc not found.";
	
	/**
	 * String for the message in the dialog.
	 */
	public static final String ERROR_MESSAGE = "SAP IDoc library not properly installed!";
	
	/**
	 * 
	 */
	public IDocErrorDialog() {
		super(Display.getDefault().getActiveShell(), TITLE_MESSAGE, null, ERROR_MESSAGE, MessageDialog.ERROR, new String[]{"OK"}, 0);
	}

	/**
	 * @see org.eclipse.jface.dialogs.MessageDialog#createCustomArea(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected Control createCustomArea(final Composite composite) {
		final Link link = new Link(composite, SWT.NONE);
		link.setText("Please follow the VClipse installation instructions (see <a>VCLipse User Guide</a>):\n" +
				     "Download SAP IDoc3 from the SAP Service Marketplace and " +
				     "copy the file sapidoc3.jar into the directory " +
				     "plugins\\org.vclipse.sapjco3_1.1.0 inside your Eclipse installation directory.");
		link.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent event) {
				// -> gegen displayHelp(String contextid) eintauschen
				PlatformUI.getWorkbench().getHelpSystem().displayHelp();
			}
		});
		
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.BEGINNING).grab(true, false)
			.hint(convertHorizontalDLUsToPixels(
					IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH), SWT.DEFAULT).applyTo(link);
		return composite;
	}

}
