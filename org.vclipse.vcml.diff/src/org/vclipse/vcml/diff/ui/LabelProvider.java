/**
 * 
 */
package org.vclipse.vcml.diff.ui;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.viewers.BaseLabelProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

/**
 *
 */
public class LabelProvider extends BaseLabelProvider implements ILabelProvider {
	
	/**
	 * @see org.eclipse.jface.viewers.ILabelProvider#getImage(java.lang.Object)
	 */
	public Image getImage(Object element) {
		final ISharedImages sharedImages = PlatformUI.getWorkbench().getSharedImages();
		if(element instanceof IProject) {
			return sharedImages.getImage(IDE.SharedImages.IMG_OBJ_PROJECT);
		} else if(element instanceof IFolder) {
			return sharedImages.getImage(ISharedImages.IMG_OBJ_FOLDER);
		} else {
			return sharedImages.getImage(ISharedImages.IMG_OBJ_FILE);
		}
	}

	/**
	 * @see org.eclipse.jface.viewers.ILabelProvider#getText(java.lang.Object)
	 */
	public String getText(final Object element) {
		return element instanceof IResource ? ((IResource)element).getName() : null;
	}
}
