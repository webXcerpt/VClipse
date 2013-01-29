/*******************************************************************************
 * Copyright (c) 2013 webXcerpt Software GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    	webXcerpt Software GmbH - initial creator
 *		www.webxcerpt.com
 ******************************************************************************/
package org.vclipse.vcml.compare.ui

import com.google.inject.Inject
import org.eclipse.core.commands.ExecutionEvent
import org.eclipse.core.resources.IFile
import org.vclipse.base.ui.FileListHandler
import org.vclipse.vcml.compare.VcmlComparePlugin

import com.google.common.collect.Iterables
import org.eclipse.core.runtime.IStatus

/*
 * Invokes the validation of the selection made in the project explorer on vcml files and the call of the compare dialog.
 */
class VcmlCompareInvokeExportHandler extends FileListHandler {

	@Inject
	private VcmlCompareExtractDifferencesDialog dialog

	/**
	 * The selected files are combined to an iterable in the FileListHandler
	 */
	override void handleListVariable(Iterable<IFile> iterable, ExecutionEvent event) {
		// can not compare more than 2 entries
		if(Iterables::size(iterable) > 2) {
			VcmlComparePlugin::log(IStatus::ERROR, "Only 2 files are allowed for this action.")
			return
		}
			
		// if only one file preselected, it should be the sap state, the (edited) vcml state should be selected 
		// by the user, it also possible to switch the entries in the ui later
		val iterator = iterable.iterator()
		if(Iterables::size(iterable) == 1) {
			dialog.setLeft(iterator.next)
			dialog.open
			return;
		}
		
		// for selection with 2 entries, compare the time stamp to get out which is the new one
		val first = iterator.next
		val second = iterator.next
		if(first.localTimeStamp > second.localTimeStamp) {
			dialog.setRight(first)
			dialog.setLeft(second)
			dialog.open
		} else {
			dialog.setRight(second)
			dialog.setLeft(first)
			dialog.open
		}
	}	
}