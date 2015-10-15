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

import com.sap.conn.idoc.IDocException;
import com.sap.conn.idoc.IDocRecord;
import com.sap.conn.idoc.IDocRecordMetaData;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.eclipse.xtext.xbase.lib.Exceptions;

@SuppressWarnings("all")
public class IDocDocumentPropertySource implements IPropertySource {
  private static SimpleDateFormat DATEFORMAT = new SimpleDateFormat("yyyy-MM-dd");
  
  private static SimpleDateFormat TIMEFORMAT = new SimpleDateFormat("HH:mm:ss");
  
  private final IDocRecord rec;
  
  /**
   * @param rec
   */
  public IDocDocumentPropertySource(final IDocRecord record) {
    this.rec = record;
  }
  
  /**
   * @see org.eclipse.ui.views.properties.IPropertySource#getEditableValue()
   */
  @Override
  public Object getEditableValue() {
    return this;
  }
  
  /**
   * @see org.eclipse.ui.views.properties.IPropertySource#getPropertyDescriptors()
   */
  @Override
  public IPropertyDescriptor[] getPropertyDescriptors() {
    IDocRecordMetaData iDocRecordMetaData = this.rec.getRecordMetaData();
    int _numFields = this.rec.getNumFields();
    PropertyDescriptor[] pds = new PropertyDescriptor[_numFields];
    for (int i = 0; (i < this.rec.getNumFields()); i++) {
      try {
        String attName = this.rec.getName(i);
        {
          final PropertyDescriptor[] _wrVal_pds = pds;
          final int _wrIndx_pds = i;
          PropertyDescriptor _propertyDescriptor = new PropertyDescriptor(attName, attName);
          _wrVal_pds[_wrIndx_pds] = _propertyDescriptor;
        }
        PropertyDescriptor _xblockexpression = null;
        {
          final int _rdIndx_pds = i;
          _xblockexpression = pds[_rdIndx_pds];
        }
        StringConcatenation _builder = new StringConcatenation();
        String _description = iDocRecordMetaData.getDescription(attName);
        _builder.append(_description, "");
        _builder.append(" [type: ");
        String _typeAsString = iDocRecordMetaData.getTypeAsString(attName);
        _builder.append(_typeAsString, "");
        _builder.append(", length: ");
        int _length = iDocRecordMetaData.getLength(attName);
        _builder.append(_length, "");
        _builder.append(", domain: ");
        String _domainName = iDocRecordMetaData.getDomainName(attName);
        _builder.append(_domainName, "");
        _builder.append("]");
        String _string = _builder.toString();
        _xblockexpression.setDescription(_string);
      } catch (final Throwable _t) {
        if (_t instanceof IDocException) {
          final IDocException e = (IDocException)_t;
          e.printStackTrace();
        } else {
          throw Exceptions.sneakyThrow(_t);
        }
      }
    }
    return pds;
  }
  
  /**
   * @see org.eclipse.ui.views.properties.IPropertySource#getPropertyValue(java.lang.Object)
   */
  @Override
  public Object getPropertyValue(final Object id) {
    try {
      IDocRecordMetaData iDocRecordMetaData = this.rec.getRecordMetaData();
      String _string = id.toString();
      int _type = iDocRecordMetaData.getType(_string);
      switch (_type) {
        case IDocRecordMetaData.TYPE_DATE:
          String _string_1 = id.toString();
          Date _date = this.rec.getDate(_string_1);
          return IDocDocumentPropertySource.DATEFORMAT.format(_date);
        case IDocRecordMetaData.TYPE_TIME:
          String _string_2 = id.toString();
          Date _time = this.rec.getTime(_string_2);
          return IDocDocumentPropertySource.TIMEFORMAT.format(_time);
        default:
          String _string_3 = id.toString();
          return this.rec.getString(_string_3);
      }
    } catch (final Throwable _t) {
      if (_t instanceof IDocException) {
        final IDocException e = (IDocException)_t;
        e.printStackTrace();
        return null;
      } else {
        throw Exceptions.sneakyThrow(_t);
      }
    }
  }
  
  /**
   * @see org.eclipse.ui.views.properties.IPropertySource#isPropertySet(java.lang.Object)
   */
  @Override
  public boolean isPropertySet(final Object id) {
    return false;
  }
  
  /**
   * @see org.eclipse.ui.views.properties.IPropertySource#resetPropertyValue(java.lang.Object)
   */
  @Override
  public void resetPropertyValue(final Object id) {
  }
  
  /**
   * @see org.eclipse.ui.views.properties.IPropertySource#setPropertyValue(java.lang.Object, java.lang.Object)
   */
  @Override
  public void setPropertyValue(final Object id, final Object value) {
  }
}
