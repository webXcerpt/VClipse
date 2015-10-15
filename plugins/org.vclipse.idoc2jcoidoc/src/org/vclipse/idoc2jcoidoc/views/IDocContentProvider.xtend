/** 
 * Copyright (c) 2010 - 2013 webXcerpt Software GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * webXcerpt Software GmbH - initial creator
 * www.webxcerpt.com
 */
package org.vclipse.idoc2jcoidoc.views

import org.eclipse.jface.viewers.ITreeContentProvider
import org.eclipse.jface.viewers.Viewer
import org.vclipse.idoc2jcoidoc.views.IDocView.IDocViewInput
import com.sap.conn.idoc.IDocDocument
import com.sap.conn.idoc.IDocSegment

/** 
 */
class IDocContentProvider implements ITreeContentProvider {
	/** 
	 */
	static final Object[] EMPTY = newArrayOfSize(0)
	/** 
	 */
	IDocViewInput input

	/** 
	 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
	 */
	override void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		if (newInput instanceof IDocViewInput) {
			input = newInput as IDocViewInput
		}

	}

	/** 
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
	 */
	override Object[] getChildren(Object parentElement) {
		if (parentElement instanceof IDocDocument) {
			return (parentElement as IDocDocument).getRootSegment().getChildren()
		} else if (parentElement instanceof IDocSegment) {
			return (parentElement as IDocSegment).getChildren()
		} else {
			return EMPTY
		}
	}

	/** 
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
	 */
	override Object getParent(Object element) {
		return null
	}

	/** 
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
	 */
	override boolean hasChildren(Object element) {
		var Object[] children = getChildren(element)
		return children !== null && children.length > 0
	}

	/** 
	 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
	 */
	override Object[] getElements(Object inputElement) {
		return input.getDocuments().toArray()
	}

	/** 
	 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
	 */
	override void dispose() {
	}

	def protected IDocViewInput getInput() {
		return input
	}

}
