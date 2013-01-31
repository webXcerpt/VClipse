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

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.viewers.BaseLabelProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

class LabelProvider extends BaseLabelProvider implements ILabelProvider {
	
	private static final ISharedImages sharedImages = PlatformUI.getWorkbench().getSharedImages();
	
	public Image getImage(Object element) {
		if(element instanceof IFile || element instanceof File) {
			return sharedImages.getImage(ISharedImages.IMG_OBJ_FILE);
		}
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
