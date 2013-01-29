/*******************************************************************************
 * Copyright (c) 2013 webXcerpt Software GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    webXcerpt Software GmbH - initial creator - www.webxcerpt.com
 ******************************************************************************/
package org.vclipse.vcml.compare.ui

import com.google.inject.Inject
import org.eclipse.core.resources.IFile
import org.eclipse.core.resources.WorkspaceJob
import org.eclipse.core.runtime.CoreException
import org.eclipse.core.runtime.IProgressMonitor
import org.eclipse.emf.compare.DifferenceSource
import org.eclipse.xtext.util.StringInputStream
import org.vclipse.vcml.compare.VcmlCompareOperation
import org.vclipse.vcml.compare.VcmlComparePlugin
import org.eclipse.core.runtime.IStatus
import org.eclipse.core.runtime.Status

import static org.eclipse.core.runtime.IProgressMonitor.*
import static org.eclipse.emf.compare.DifferenceSource.*

/*
 * Job extracting the compare results.
 */
class VcmlCompareExtractResultsJob extends WorkspaceJob {
	
	private VcmlCompareOperation compareOperation
	
	private IFile left
	private IFile right
	private IFile result
	
	@Inject
	new(VcmlCompareOperation compareOperation) {
		super("Job extracting differences between 2 vcml files.")
		this.compareOperation = compareOperation
	}
	
	/*
	 * Setter for files the compare operation is running on.
	 */
	def protected setFileToCompare(IFile file, DifferenceSource source) {
		if(LEFT == source) {
			left = file
		} else {
			right = file
		}
	}
	
	/*
	 * Setter for the file containing results after extraction.
	 */
	def protected setResultFile(IFile file) {
		this.result = file
	}
	
	/*
	 * Creates the result file and executes the compare operation.
	 */
	override runInWorkspace(IProgressMonitor monitor) throws CoreException {
		// Show the both files that are being compared.
		val messageBuffer = new StringBuffer("Exporting differences between ")
		messageBuffer.append(left.name)
		messageBuffer.append(" and ")
		messageBuffer.append(right.name)
		monitor.beginTask(messageBuffer.toString, UNKNOWN)
		try {
			if(!result.accessible) {
				result.create(new StringInputStream(""), true, monitor)
			}
			// Execute the compare operation
			compareOperation.compare(right, left, result, monitor)
			monitor.done
			return Status::OK_STATUS
		} catch(Exception exception) {
			monitor.done
			val message = exception.message
			VcmlComparePlugin::log(IStatus::ERROR, 
				if (message == null || message.empty) exception.^class.simpleName else message
			)
			return Status::CANCEL_STATUS
		}
	}
}