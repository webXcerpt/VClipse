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

import org.eclipse.core.runtime.IStatus;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.swt.widgets.Display;

/**
 * Update of the UI for the input dialog.
 */
public class InputPageUpdate {

	public static void updateNoErrors(final InputPage page) {
		Display.getDefault().syncExec(new Runnable() {
			@Override
			public void run() {
				page.setErrorMessage(null);
				page.setPageComplete(true);
			}
		});
	}
	
	public static void update(final InputPage page, final String errorMessage) {
		Display.getDefault().syncExec(new Runnable() {
			@Override
			public void run() {	
				page.setPageComplete(false);
				page.setErrorMessage(errorMessage);
			}
		});
	}
	
	public static void update(final InputPage page, final Exception exception) {
		Display.getDefault().syncExec(new Runnable() {
			@Override
			public void run() {
				String message = exception.getMessage();
				page.setPageComplete(false);
				page.setErrorMessage(message);
			}
		});
	}
	
	public static void update(final InputPage page, final RefactoringStatus initialStatus, final RefactoringStatus finalStatus) {
		Display.getDefault().syncExec(new Runnable() {
			@Override
			public void run() {
				if(initialStatus.isOK() && finalStatus.isOK()) {
					page.setPageComplete(true);
					page.setErrorMessage(null);
					return;
				} 
					
				String errorMessage = initialStatus.getMessageMatchingSeverity(IStatus.ERROR);
				if(errorMessage == null) {
					errorMessage = finalStatus.getMessageMatchingSeverity(IStatus.ERROR);
				}
				page.setErrorMessage(errorMessage);
				page.setPageComplete(false);
			}
		});
	}
}