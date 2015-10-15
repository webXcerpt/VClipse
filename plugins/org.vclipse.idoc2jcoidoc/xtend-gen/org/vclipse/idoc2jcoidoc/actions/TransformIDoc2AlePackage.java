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
package org.vclipse.idoc2jcoidoc.actions;

import java.util.Iterator;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.resources.IFile;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.xtext.xbase.lib.Exceptions;
import org.vclipse.base.ui.FileListHandler;
import org.vclipse.idoc2jcoidoc.IDoc2JCoIDocPlugin;
import org.vclipse.idoc2jcoidoc.views.IDocView;

@SuppressWarnings("all")
public class TransformIDoc2AlePackage extends FileListHandler {
  @Override
  public void handleListVariable(final Iterable<IFile> collection, final ExecutionEvent event) {
    Iterator<IFile> iterator = collection.iterator();
    boolean _hasNext = iterator.hasNext();
    if (_hasNext) {
      IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindow(event);
      if ((window != null)) {
        try {
          IWorkbenchPage _activePage = window.getActivePage();
          IViewPart _showView = _activePage.showView(IDocView.ID);
          IDocView view = ((IDocView) _showView);
          IFile file = iterator.next();
          view.setInput(file);
        } catch (final Throwable _t) {
          if (_t instanceof PartInitException) {
            final PartInitException exception = (PartInitException)_t;
            String _message = exception.getMessage();
            IDoc2JCoIDocPlugin.log(_message, exception);
          } else {
            throw Exceptions.sneakyThrow(_t);
          }
        }
      }
    }
  }
}
