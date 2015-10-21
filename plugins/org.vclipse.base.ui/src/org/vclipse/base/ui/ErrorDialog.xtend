/** 
 * Copyright (c) 2010 - 2015 webXcerpt Software GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * webXcerpt Software GmbH - initial creator
 * www.webxcerpt.com
 */
package org.vclipse.base.ui

import org.eclipse.core.runtime.IStatus
import org.eclipse.jface.dialogs.IDialogConstants
import org.eclipse.jface.dialogs.MessageDialog
import org.eclipse.jface.layout.GridDataFactory
import org.eclipse.swt.SWT
import org.eclipse.swt.layout.GridData
import org.eclipse.swt.layout.GridLayout
import org.eclipse.swt.widgets.Composite
import org.eclipse.swt.widgets.Control
import org.eclipse.swt.widgets.Label
import org.eclipse.swt.widgets.Shell

class ErrorDialog extends MessageDialog {
	IStatus status

	new(Shell parentShell, String dialogTitle, String dialogMessage, IStatus status) {
		super(parentShell, dialogTitle, null, dialogMessage, MessageDialog.ERROR, #["OK"], 0)
		this.status = status
	}

	override protected Control createCustomArea(Composite composite) {
		val Composite mainArea = new Composite(composite, SWT.NONE) => [
			setLayout(new GridLayout(2, false))
		];
		val Label label = new Label(mainArea, SWT.NONE) => [
			setText(status.getMessage())
			setLayoutData(new GridData)
		]
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.BEGINNING).grab(true, true).hint(
			convertHorizontalDLUsToPixels(IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH), SWT.DEFAULT).applyTo(label)
		return mainArea
	}

}
