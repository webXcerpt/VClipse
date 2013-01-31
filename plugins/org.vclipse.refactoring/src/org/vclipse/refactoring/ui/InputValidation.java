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
package org.vclipse.refactoring.ui;

import org.eclipse.jface.wizard.IWizardContainer;
import org.vclipse.refactoring.IRefactoringUIContext;

/**
 * Background thread executing validation during input in the input dialog.
 */
public class InputValidation extends Thread {

	private final InputPage page;
	
	public InputValidation(InputPage page) {
		this.page = page;
		page.setErrorMessage(null);
		page.setPageComplete(false);
	}

	@Override
	public void run() {
		IRefactoringUIContext context = page.getContext();
		IWizardContainer container = page.getContainer();
		if(container != null) {
			InputValidationRunnable validation = new InputValidationRunnable(context);
			try {
				container.run(false, true, validation);
				container.updateButtons();
			} catch(Exception exception) {
				InputPageUpdate.update(page, exception);
			}
		}
	}
}
