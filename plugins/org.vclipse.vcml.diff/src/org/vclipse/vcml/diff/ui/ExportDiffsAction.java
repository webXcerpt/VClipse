/**
 * 
 */
package org.vclipse.vcml.diff.ui;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

public class ExportDiffsAction implements IObjectActionDelegate {

	private IStructuredSelection strSelection;

	@Override
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		
	}

	@Override
	public void run(IAction action) {
		new ExportDiffsDialog(Display.getDefault().getActiveShell(), strSelection).open();
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		if(selection instanceof IStructuredSelection) {
			strSelection = (IStructuredSelection)selection;
		}
	}
}
