/**
 * 
 */
package org.vclipse.vcml.diff.ui;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.vclipse.vcml.diff.VcmlDiffPlugin;

/**
 *
 */
public class ContentProvider implements IStructuredContentProvider {

	/**
	 * 
	 */
	private IResource[] children;
	
	/**
	 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
	 */
	public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput) {		
		if(oldInput != newInput && newInput instanceof IContainer) {
			try {
				children = ((IContainer)newInput).members();
			} catch(final CoreException exception) {
				children = new IResource[0];
				VcmlDiffPlugin.log(exception.getMessage(), exception);
			}
		}
	}

	/**
	 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
	 */
	public Object[] getElements(final Object inputElement) {
		return children;
	}

	/**
	 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
	 */
	public void dispose() {
		// not used
	}
}
