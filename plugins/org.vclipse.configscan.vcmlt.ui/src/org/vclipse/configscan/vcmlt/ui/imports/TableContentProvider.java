package org.vclipse.configscan.vcmlt.ui.imports;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.Viewer;

class TableContentProvider implements IStructuredContentProvider {
	
	private List<File> files;
	
	public TableContentProvider() {
		files = new ArrayList<File>();
	}
	
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		if(oldInput != newInput) {
			if(newInput instanceof IStructuredSelection) {
				Iterator<?> iterator = ((IStructuredSelection)newInput).iterator();
				while(iterator.hasNext()) {
					Object next = iterator.next();
					if(next instanceof IFile) {
						IFile ifile = (IFile)next;
						if(ifile.getFileExtension().equals("xml") || ifile.getFileExtension().equals("cfg")) {
							files.add(ifile.getLocation().toFile());
						}
					}
				}
			}
		}
	}

	public Object[] getElements(Object inputElement) {
		return files.toArray();
	}
	
	public void dispose() {
		files.clear();
	}
}
