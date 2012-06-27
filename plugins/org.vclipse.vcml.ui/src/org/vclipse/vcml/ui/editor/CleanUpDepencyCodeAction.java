package org.vclipse.vcml.ui.editor;

import java.util.Iterator;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

import com.google.inject.Inject;

public class CleanUpDepencyCodeAction implements IObjectActionDelegate {

	@Inject
	private CleanUpDependenciesHandler cleanUpDependenciesHandler;
	
	private IStructuredSelection selection;
	
	public void run(IAction action) {
		Iterator<?> iterator = selection.iterator();
		while(iterator.hasNext()) {
			Object next = iterator.next();
			if(next instanceof IContainer) {
				cleanUpDependenciesHandler.handleContainer((IContainer)next);
			} else if(next instanceof IFile) {
				cleanUpDependenciesHandler.handleFile((IFile)next);
			}
		}	
	}

	public void selectionChanged(IAction action, ISelection selection) {
		if(selection instanceof IStructuredSelection) {
			this.selection = (IStructuredSelection)selection;
		}
	}

	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		
	}
}
