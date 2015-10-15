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

import com.sap.conn.idoc.IDocRecord;
import java.util.Collections;
import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.xtext.xbase.lib.CollectionLiterals;
import org.eclipse.xtext.xbase.lib.Conversions;
import org.vclipse.idoc2jcoidoc.views.IDocDocumentPropertySource;

@SuppressWarnings("all")
public class IDocDocumentAdapterFactory implements IAdapterFactory {
  /**
   * @see org.eclipse.core.runtime.IAdapterFactory#getAdapter(java.lang.Object, java.lang.Class)
   */
  @SuppressWarnings("rawtypes")
  @Override
  public Object getAdapter(final Object adaptableObject, final Class adapterType) {
    boolean _and = false;
    if (!(adapterType == IPropertySource.class)) {
      _and = false;
    } else {
      _and = (adaptableObject instanceof IDocRecord);
    }
    if (_and) {
      return new IDocDocumentPropertySource(((IDocRecord) adaptableObject));
    }
    return null;
  }
  
  /**
   * @see org.eclipse.core.runtime.IAdapterFactory#getAdapterList()
   */
  @SuppressWarnings("rawtypes")
  @Override
  public Class[] getAdapterList() {
    return ((Class[]) ((Class[])Conversions.unwrapArray(Collections.<Class<IPropertySource>>unmodifiableList(CollectionLiterals.<Class<IPropertySource>>newArrayList(IPropertySource.class)), Class.class)));
  }
}
