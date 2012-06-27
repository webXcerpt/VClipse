/**
 * 
 */
package org.vclipse.vcml.diff.ui;

import java.util.Iterator;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.vclipse.vcml.diff.VcmlDiffPlugin;

import com.google.inject.Inject;

public class ExportDiffsAction implements IObjectActionDelegate {

	private ExportDiffsDialog dialog;
	
	private IStructuredSelection selection;
	
	@Inject
	public ExportDiffsAction(ExportDiffsDialog dialog) {
		this.dialog = dialog;
	}
	
	public void selectionChanged(IAction action, ISelection selection) {
		this.selection = (IStructuredSelection)selection;
	}
	
	public void run(IAction action) {
		if(selection.isEmpty()) {
			VcmlDiffPlugin.log("Can not run the " + ExportDiffsAction.class.getSimpleName() + 
					". There are no any vcml files selected.", IStatus.WARNING);
		}
		
		Iterator<?> iterator = selection.iterator();
		IFile firstFile = null, secondFile = null;
		for(int i=0; iterator.hasNext() && i<2; i++) {
			if(firstFile == null) {
				firstFile = (IFile)iterator.next();
			} else {
				secondFile = (IFile)iterator.next();
			}
		}
		
		if(firstFile != null) {
			if(secondFile != null) {
				if(firstFile.getLocalTimeStamp() > secondFile.getLocalTimeStamp()) {
					dialog.setNewFile(firstFile);
					dialog.setOldFile(secondFile);
					dialog.open();
				} else {
					dialog.setNewFile(secondFile);
					dialog.setOldFile(firstFile);
					dialog.open();
				}
				return;
			}
			dialog.setOldFile(firstFile);
			dialog.open();
		} 
	}

	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		// 
	}
}
