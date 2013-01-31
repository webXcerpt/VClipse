/*******************************************************************************
 * Copyright (c) 2010 - 2013 webXcerpt Software GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *  
 * Contributors:
 *     	webXcerpt Software GmbH - initial creator
 * 		www.webxcerpt.com
 ******************************************************************************/
package org.vclipse.configscan.imports;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.Viewer;

import com.google.common.collect.Lists;

class ContentProvider implements IStructuredContentProvider {
	
	private List<File> selectedFiles;

	public ContentProvider() {
		selectedFiles = Lists.newArrayList();
	}

	// we work on File type !
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		if(oldInput != newInput) {
			if(newInput instanceof IStructuredSelection) {
				Iterator<?> iterator = ((IStructuredSelection)newInput).iterator();
				while(iterator.hasNext()) {
					Object next = iterator.next();
					if(next instanceof File) {
						selectedFiles.add((File)next);
					} else if(next instanceof IFile) {
						selectedFiles.add(((IFile)next).getLocation().toFile());
					}
				}
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
