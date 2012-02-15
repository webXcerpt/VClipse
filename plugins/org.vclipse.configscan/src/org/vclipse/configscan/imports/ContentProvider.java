package org.vclipse.configscan.imports;

import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.Viewer;

import com.google.common.collect.Lists;

class ContentProvider implements IStructuredContentProvider {
	
	private List<?> selectedFiles;

	public ContentProvider() {
		selectedFiles = Lists.newArrayList();
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		if(oldInput != newInput) {
			if(newInput instanceof IStructuredSelection) {
				selectedFiles = Lists.newArrayList((Iterator<?>) ((IStructuredSelection)newInput).iterator());
			}
		}
	}

	public Object[] getElements(Object inputElement) {
		return selectedFiles.toArray();
	}

	public void dispose() {
		selectedFiles.clear();
	}
}
