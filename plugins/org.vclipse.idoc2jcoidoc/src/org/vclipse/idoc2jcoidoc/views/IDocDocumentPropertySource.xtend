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

import java.text.SimpleDateFormat
import org.eclipse.ui.views.properties.IPropertyDescriptor
import org.eclipse.ui.views.properties.IPropertySource
import org.eclipse.ui.views.properties.PropertyDescriptor
import com.sap.conn.idoc.IDocException
import com.sap.conn.idoc.IDocRecord
import com.sap.conn.idoc.IDocRecordMetaData

/** 
 */
class IDocDocumentPropertySource implements IPropertySource {
	/** 
	 */
	static SimpleDateFormat DATEFORMAT = new SimpleDateFormat("yyyy-MM-dd")
	/** 
	 */
	static SimpleDateFormat TIMEFORMAT = new SimpleDateFormat("HH:mm:ss")
	/** 
	 */
	final IDocRecord rec

	/** 
	 * @param rec
	 */
	new(IDocRecord record) {
		rec = record
	}

	/** 
	 * @see org.eclipse.ui.views.properties.IPropertySource#getEditableValue()
	 */
	override Object getEditableValue() {
		return this
	}

	/** 
	 * @see org.eclipse.ui.views.properties.IPropertySource#getPropertyDescriptors()
	 */
	override IPropertyDescriptor[] getPropertyDescriptors() {
		var IDocRecordMetaData iDocRecordMetaData = rec.getRecordMetaData()
		var PropertyDescriptor[] pds = newArrayOfSize(rec.getNumFields())

		for (var int i = 0; i < rec.getNumFields(); i++) {
			try {
				var String attName = rec.getName(i)
				{
					val _wrVal_pds = pds
					val _wrIndx_pds = i
					_wrVal_pds.set(_wrIndx_pds, new PropertyDescriptor(attName, '''«attName» («rec.recordMetaData.getDescription(attName)»)'''))
				}
				{
					val _rdIndx_pds = i
					pds.get(_rdIndx_pds)
				}.setDescription(
					'''«iDocRecordMetaData.getDescription(attName)» [type: «iDocRecordMetaData.getTypeAsString(attName)», length: «iDocRecordMetaData.getLength(attName)», domain: «iDocRecordMetaData.getDomainName(attName)»]'''.
						toString)
					} catch (IDocException e) {
						e.printStackTrace()
					}

				}
				return pds
			}

			/** 
			 * @see org.eclipse.ui.views.properties.IPropertySource#getPropertyValue(java.lang.Object)
			 */
			override Object getPropertyValue(Object id) {
				try {
					var IDocRecordMetaData iDocRecordMetaData = rec.getRecordMetaData()

					switch (iDocRecordMetaData.getType(id.toString())) {
						case IDocRecordMetaData::TYPE_DATE: {
							return DATEFORMAT.format(rec.getDate(id.toString()))
						}
						case IDocRecordMetaData::TYPE_TIME: {
							return TIMEFORMAT.format(rec.getTime(id.toString()))
						}
						default: {
							return rec.getString(id.toString())
						}
					}
				} catch (IDocException e) {
					e.printStackTrace()
					return null
				}

			}

			/** 
			 * @see org.eclipse.ui.views.properties.IPropertySource#isPropertySet(java.lang.Object)
			 */
			override boolean isPropertySet(Object id) {
				// currently not needed
				return false
			}

			/** 
			 * @see org.eclipse.ui.views.properties.IPropertySource#resetPropertyValue(java.lang.Object)
			 */
			override void resetPropertyValue(Object id) {
				// currently not needed
			}

			/** 
			 * @see org.eclipse.ui.views.properties.IPropertySource#setPropertyValue(java.lang.Object, java.lang.Object)
			 */
			override void setPropertyValue(Object id, Object value) {
				// currently not needed
			}

		}
		