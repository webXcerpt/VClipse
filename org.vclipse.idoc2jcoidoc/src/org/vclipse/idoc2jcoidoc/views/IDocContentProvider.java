/**
 * Copyright � 2008, 2010 webXcerpt Software GmbH.
 * All rights reserved.
 *  
 * Contributor :
 *               webXcerpt Software GmbH
 */
package org.vclipse.idoc2jcoidoc.views;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.vclipse.idoc2jcoidoc.views.IDocView.IDocViewInput;

import com.sap.conn.idoc.IDocDocument;
import com.sap.conn.idoc.IDocSegment;

/**
 *
 */
public class IDocContentProvider implements ITreeContentProvider {

	/**
	 * 
	 */
	private static final Object[] EMPTY = new Object[0];
	
	/**
	 * 
	 */
	private IDocViewInput input;
	
	/**
	 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
	 */
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		if(newInput instanceof IDocViewInput) {
			input = (IDocViewInput)newInput;
		}
	}
	
	/**
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
	 */
	public Object[] getChildren(Object parentElement) {
		if(parentElement instanceof IDocDocument) {
			return ((IDocDocument)parentElement).getRootSegment().getChildren();
		} else if(parentElement instanceof IDocSegment) {
			return ((IDocSegment)parentElement).getChildren();
		} else {
			return EMPTY;
		}
	}

	/**
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
	 */
	public Object getParent(Object element) {
		return null;
	}

	/**
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
	 */
	public boolean hasChildren(Object element) {
		Object[] children = getChildren(element);
		return children != null && children.length > 0;
	}

	/**
	 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
	 */
	public Object[] getElements(Object inputElement) {
		return input.getDocuments().toArray();
	}
	
	/**
	 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
	 */
	public void dispose() {
		
	}
	
	protected IDocViewInput getInput() {
		return input;
	}
}
