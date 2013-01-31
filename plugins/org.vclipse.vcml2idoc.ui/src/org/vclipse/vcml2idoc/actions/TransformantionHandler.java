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
package org.vclipse.vcml2idoc.actions;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;
import org.vclipse.base.ui.FileListHandler;
import org.vclipse.vcml2idoc.VCML2IDocPlugin;
import org.vclipse.vcml2idoc.transformation.IVCML2IDocTransformation;

import com.google.inject.Inject;

public class TransformantionHandler extends FileListHandler {

	@Inject
	private IVCML2IDocTransformation transformation;
	
	@Override
	public void handleListVariable(final Iterable<IFile> collection, ExecutionEvent event) {
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindow(event);
		if(window != null) {
			Job job = new WorkspaceJob("Conversion of vcml files to idoc files.") {
				@Override
				public IStatus runInWorkspace(IProgressMonitor monitor) throws CoreException {
					try {
						transformation.transform(collection, monitor);
						return Status.OK_STATUS;
					} catch (InvocationTargetException exception) {
						VCML2IDocPlugin.log(exception.getMessage(), exception);
						return Status.CANCEL_STATUS;
					}
				}
			};
			job.schedule();
		}
	}
}
