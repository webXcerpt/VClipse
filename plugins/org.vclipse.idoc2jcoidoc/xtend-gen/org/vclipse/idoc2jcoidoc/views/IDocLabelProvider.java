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
import org.eclipse.jface.viewers.BaseLabelProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.vclipse.idoc2jcoidoc.IDoc2JCoIDocPlugin;
import org.vclipse.idoc2jcoidoc.IUiConstants;

@SuppressWarnings("all")
public class IDocLabelProvider extends BaseLabelProvider implements ILabelProvider {
  /**
   * @see org.eclipse.jface.viewers.ILabelProvider#getImage(java.lang.Object)
   */
  @Override
  public Image getImage(final Object object) {
    if ((object instanceof IDocDocument)) {
      return IDoc2JCoIDocPlugin.getImage(IUiConstants.IDOC_DOCUMENT_IMAGE);
    } else {
      if ((object instanceof IDocSegment)) {
        return IDoc2JCoIDocPlugin.getImage(IUiConstants.IDOC_SEGMENT_IMAGE);
      } else {
        return null;
      }
    }
  }
  
  /**
   * @see org.eclipse.jface.viewers.ILabelProvider#getText(java.lang.Object)
   */
  @Override
  public String getText(final Object object) {
    if ((object instanceof IDocDocument)) {
      final IDocDocument iDocDocument = ((IDocDocument) object);
      StringConcatenation _builder = new StringConcatenation();
      String _iDocNumber = iDocDocument.getIDocNumber();
      _builder.append(_iDocNumber, "");
      _builder.append(" ");
      String _iDocType = iDocDocument.getIDocType();
      _builder.append(_iDocType, "");
      _builder.append(" ");
      String _messageType = iDocDocument.getMessageType();
      _builder.append(_messageType, "");
      return _builder.toString();
    } else {
      if ((object instanceof IDocSegment)) {
        final IDocSegment iDocSegment = ((IDocSegment) object);
        StringConcatenation _builder_1 = new StringConcatenation();
        String _definition = iDocSegment.getDefinition();
        _builder_1.append(_definition, "");
        _builder_1.append(" (");
        String _description = iDocSegment.getDescription();
        _builder_1.append(_description, "");
        _builder_1.append(")");
        return _builder_1.toString();
      } else {
        return object.toString();
      }
    }
  }
}
