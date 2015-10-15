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
package org.vclipse.idoc2jcoidoc.views;

import com.sap.conn.idoc.IDocDocument;
import com.sap.conn.idoc.IDocSegment;
import java.util.List;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.vclipse.idoc2jcoidoc.views.IDocView;

@SuppressWarnings("all")
public class IDocContentProvider implements ITreeContentProvider {
  private final static Object[] EMPTY = new Object[0];
  
  private IDocView.IDocViewInput input;
  
  /**
   * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
   */
  @Override
  public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput) {
    if ((newInput instanceof IDocView.IDocViewInput)) {
      this.input = ((IDocView.IDocViewInput) newInput);
    }
  }
  
  /**
   * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
   */
  @Override
  public Object[] getChildren(final Object parentElement) {
    if ((parentElement instanceof IDocDocument)) {
      IDocSegment _rootSegment = ((IDocDocument) parentElement).getRootSegment();
      return _rootSegment.getChildren();
    } else {
      if ((parentElement instanceof IDocSegment)) {
        return ((IDocSegment) parentElement).getChildren();
      } else {
        return IDocContentProvider.EMPTY;
      }
    }
  }
  
  /**
   * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
   */
  @Override
  public Object getParent(final Object element) {
    return null;
  }
  
  /**
   * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
   */
  @Override
  public boolean hasChildren(final Object element) {
    Object[] children = this.getChildren(element);
    boolean _and = false;
    if (!(children != null)) {
      _and = false;
    } else {
      int _length = children.length;
      boolean _greaterThan = (_length > 0);
      _and = _greaterThan;
    }
    return _and;
  }
  
  /**
   * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
   */
  @Override
  public Object[] getElements(final Object inputElement) {
    List<IDocDocument> _documents = this.input.getDocuments();
    return _documents.toArray();
  }
  
  /**
   * @see org.eclipse.jface.viewers.IContentProvider#dispose()
   */
  @Override
  public void dispose() {
  }
  
  protected IDocView.IDocViewInput getInput() {
    return this.input;
  }
}
