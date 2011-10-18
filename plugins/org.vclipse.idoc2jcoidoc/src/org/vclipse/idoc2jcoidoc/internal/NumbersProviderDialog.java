/*******************************************************************************
 * Copyright (c) 2009 webXcerpt Software GmbH (http://www.webxcerpt.com).
 * All rights reserved.
 *******************************************************************************/
package org.vclipse.idoc2jcoidoc.internal;

import java.math.BigInteger;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * 
 */
public class NumbersProviderDialog extends TitleAreaDialog {

	/**
	 * 
	 */
	private String upsNumber;
	
	/**
	 * 
	 */
	private BigInteger iDocNumber;
	
	/**
	 * @param parentShell
	 */
	public NumbersProviderDialog(final Shell parentShell) {
		super(parentShell);
	}

	/**
	 * @see org.eclipse.jface.dialogs.TitleAreaDialog#createDialogArea(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected Control createDialogArea(final Composite parent) {
		final Composite mainArea = new Composite(parent, SWT.NONE);
		mainArea.setLayout(new GridLayout(2, false));
		
		Label label = new Label(mainArea, SWT.NONE);
		label.setText("UPS number:");
		
		Text text = new Text(mainArea, SWT.BORDER);
		text.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(final ModifyEvent event) {
				upsNumber = ((Text)event.widget).getText();
			}
		});
		
		label = new Label(mainArea, SWT.NONE);
		label.setText("IDoc number:");
		
		text = new Text(mainArea, SWT.BORDER);
		text.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(final ModifyEvent event) {
				final String text = ((Text)event.widget).getText();
				if(text.isEmpty()) {
					iDocNumber = BigInteger.ZERO;
				} else {
					iDocNumber = new BigInteger(text);
				}
			}
		});
		return super.createDialogArea(parent);
	}

	/**
	 * @return
	 */
	public String getUPSNumber() {
		return upsNumber;
	}

	/**
	 * @return
	 */
	public BigInteger getIDocNumber() {
		return iDocNumber;
	}

}
