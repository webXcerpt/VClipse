/*******************************************************************************
 * Copyright (c) 2009 webXcerpt Software GmbH (http://www.webxcerpt.com).
 * All rights reserved.
 *******************************************************************************/
package org.vclipse.idoc2jcoidoc.views;

import java.text.SimpleDateFormat;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.PropertyDescriptor;

import com.sap.conn.idoc.IDocException;
import com.sap.conn.idoc.IDocRecord;
import com.sap.conn.idoc.IDocRecordMetaData;

/**
 * 
 */
public class IDocDocumentPropertySource implements IPropertySource {

	/**
	 * 
	 */
	private static SimpleDateFormat DATEFORMAT = new SimpleDateFormat("yyyy-MM-dd");
	
	/**
	 * 
	 */
	private static SimpleDateFormat TIMEFORMAT = new SimpleDateFormat("HH:mm:ss");
	
	/**
	 * 
	 */
	private final IDocRecord rec;
	
	/**
	 * @param rec
	 */
	public IDocDocumentPropertySource(IDocRecord record) {
		rec = record;
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
		IDocRecordMetaData iDocRecordMetaData = rec.getRecordMetaData();
		PropertyDescriptor[] pds = new PropertyDescriptor[rec.getNumFields()];
			for (int i = 0; i < rec.getNumFields(); i++) {
				try {
					String attName = rec.getName(i);
					pds[i] = new PropertyDescriptor(attName, attName);
					pds[i].setDescription(iDocRecordMetaData.getDescription(attName) + 
				              " [type: "   + iDocRecordMetaData.getTypeAsString(attName) + 
				              ", length: " + iDocRecordMetaData.getLength(attName) + 
				              ", domain: " + iDocRecordMetaData.getDomainName(attName) + 
							  "]");
				} catch (IDocException e) {
					e.printStackTrace();
				}
			}
			return pds;
	}

	/**
	 * @see org.eclipse.ui.views.properties.IPropertySource#getPropertyValue(java.lang.Object)
	 */
	@Override
	public Object getPropertyValue(Object id) {
		try {
			IDocRecordMetaData iDocRecordMetaData = rec.getRecordMetaData();
			switch (iDocRecordMetaData.getType(id.toString())) {
				case IDocRecordMetaData.TYPE_DATE:
					return DATEFORMAT.format(rec.getDate(id.toString()));
				case IDocRecordMetaData.TYPE_TIME:
					return TIMEFORMAT.format(rec.getTime(id.toString()));
				default:
					return rec.getString(id.toString());
			}
		} catch (IDocException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * @see org.eclipse.ui.views.properties.IPropertySource#isPropertySet(java.lang.Object)
	 */
	@Override
	public boolean isPropertySet(Object id) {
		// currently not needed
		return false;
	}

	/**
	 * @see org.eclipse.ui.views.properties.IPropertySource#resetPropertyValue(java.lang.Object)
	 */
	@Override
	public void resetPropertyValue(Object id) {
		// currently not needed
	}

	/**
	 * @see org.eclipse.ui.views.properties.IPropertySource#setPropertyValue(java.lang.Object, java.lang.Object)
	 */
	@Override
	public void setPropertyValue(Object id, Object value) {
		// currently not needed
	}

}
