/**
 * 
 */
package org.vclipse.vcml.diff.ui;

import java.util.Iterator;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionDelegate;
import org.vclipse.vcml.diff.VcmlDiffPlugin;

public final class ExportDiffsAction implements IActionDelegate {

	private IStructuredSelection selection;
	
	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		this.selection = (IStructuredSelection)selection;
	}
	
	@Override
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
		
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		Shell shell = Display.getDefault().getActiveShell();
		ExportDiffsDialog dialog = new ExportDiffsDialog(shell, root);
		
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
}
