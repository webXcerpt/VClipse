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

import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.RefactoringTickProvider;
import org.vclipse.refactoring.IRefactoringUIContext;
import org.vclipse.refactoring.core.RefactoringTask;

import com.google.common.collect.Iterables;

/**
 * {@link IRunnableWithProgress} implementation for the validation during input in the input dialog.
 */
public class InputValidationRunnable implements IRunnableWithProgress {

	private final IRefactoringUIContext context;
	
	public InputValidationRunnable(IRefactoringUIContext context) {
		this.context = context;
	}
	
	@Override
	public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
		StringBuffer buffer = new StringBuffer("Validating re-factoring: ");
		buffer.append(context.getLabel());
		RefactoringTickProvider tickProvider = context.getRefactoring().getRefactoringTickProvider();
		monitor.beginTask(buffer.toString(), tickProvider.getCheckAllConditionsTicks());
		if(monitor.isCanceled()) {
			monitor.done();
			return;
		}
		
		RefactoringTask task = context.getRefactoring();
		try {
			// check initial conditions
			RefactoringStatus initialStatus = task.checkInitialConditions(monitor);
			if(monitor.isCanceled()) {
				monitor.done();
				return;
			}
			monitor.worked(tickProvider.getCheckInitialConditionsTicks());
			
			// execute re-factoring
			task.createChange(monitor);
			if(monitor.isCanceled()) {
				monitor.done();
				return;
			}
			monitor.worked(tickProvider.getCreateChangeTicks());
			
			// check final conditions
			RefactoringStatus finalStatus = task.checkFinalConditions(monitor);
			monitor.worked(tickProvider.getCheckFinalConditionsTicks());
			Iterator<InputPage> iterator = Iterables.filter(context.getPages(), InputPage.class).iterator();
			if(iterator.hasNext()) {
				InputPageUpdate.update(iterator.next(), initialStatus, finalStatus);				
			}
			monitor.done();
		} catch(CoreException exception) {
			throw new InvocationTargetException(exception);
		}
	}
}
