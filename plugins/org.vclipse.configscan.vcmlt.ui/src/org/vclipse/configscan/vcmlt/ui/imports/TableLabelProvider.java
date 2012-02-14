package org.vclipse.configscan.vcmlt.ui.imports;

import java.io.File;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.viewers.BaseLabelProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.swt.graphics.Image;

class TableLabelProvider extends BaseLabelProvider implements ILabelProvider {
	
	public Image getImage(Object element) {
		// TODO return file image -> get it from the IDE or PlatformUI ?
		return null;
	}

	public String getText(Object element) {
		if(element instanceof IFile) {
			return ((IFile)element).getFullPath().toString();
		} else if(element instanceof File) {
			return "... " + File.separator + " " + ((File)element).getName();
		}
		return null;
	}
}
