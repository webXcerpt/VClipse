/** 
 * Copyright (c) 2010 - 2015 webXcerpt Software GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * webXcerpt Software GmbH - initial creator
 * www.webxcerpt.com
 */
package org.vclipse.idoc2jcoidoc

import com.google.inject.Inject
import com.google.inject.name.Named
import com.sap.conn.idoc.IDocDocument
import com.sap.conn.idoc.IDocException
import com.sap.conn.idoc.IDocFactory
import com.sap.conn.idoc.IDocRepository
import com.sap.conn.idoc.IDocSegment
import com.sap.conn.idoc.jco.JCoIDoc
import com.sap.conn.jco.JCoException
import java.util.List
import org.eclipse.core.runtime.AssertionFailedException
import org.eclipse.core.runtime.CoreException
import org.eclipse.core.runtime.IProgressMonitor
import org.eclipse.core.runtime.IStatus
import org.eclipse.core.runtime.Status
import org.eclipse.emf.common.util.EList
import org.eclipse.emf.ecore.EObject
import org.eclipse.jface.preference.IPreferenceStore
import org.eclipse.jface.util.IPropertyChangeListener
import org.eclipse.jface.util.PropertyChangeEvent
import org.eclipse.xtext.diagnostics.ExceptionDiagnostic
import org.vclipse.connection.IConnectionHandler
import org.vclipse.idoc.iDoc.Field
import org.vclipse.idoc.iDoc.IDoc
import org.vclipse.idoc.iDoc.Model
import org.vclipse.idoc.iDoc.NumberField
import org.vclipse.idoc.iDoc.Segment
import org.vclipse.idoc.iDoc.StringField

/** 
 * @author tg
 * transforms an ecore IDoc model into a list of SAP JCo IDocDocuments
 * 
 * The addition of the IDoc segments required for serialization should be implemented during the transformation of a vcml.Model to an idoc.Model.
 */
class DefaultIDoc2JCoIDocProcessor implements IIDoc2JCoIDocProcessor, IPropertyChangeListener{
	IDocRepository iDocRepository
	IDocFactory iDocFactory
	String senderPartnerType
	String senderPartnerNumber
	final IPreferenceStore preferenceStore
	final IConnectionHandler handler

	@Inject new(@Named(IDoc2JCoIDocPlugin.ID)IPreferenceStore preferenceStore, IConnectionHandler connectionHandler) {
		super();
		this.preferenceStore = preferenceStore;
		this.handler = connectionHandler;
		this.preferenceStore.addPropertyChangeListener(this) 
	}

	override protected void finalize() throws Throwable {
		this.preferenceStore.removePropertyChangeListener(this);
		super.finalize; 
	}

	override List<IDocDocument> transform(Model idocModel, IProgressMonitor monitor) throws JCoException, CoreException {
		val List<IDocDocument> idocDocuments = newArrayList 
		if (idocModel !== null) {
			val EList<IDoc> idocs = idocModel.idocs; 
			monitor.beginTask("Running transformation...", idocs.size);
			for (IDoc idoc : idocs) {
				if (monitor.isCanceled)
					return idocDocuments
				else {
					val IDocDocument iDocDocument = transform(idoc, monitor) 
					if (iDocDocument !== null) {
						idocDocuments.add(iDocDocument) 
					}
					monitor.worked(1)
				}
			}
			monitor.done
		}
		idocDocuments 
	}

	def IDocDocument transform(IDoc object, IProgressMonitor monitor) throws JCoException, CoreException {
		senderPartnerType = preferenceStore.getString(IUiConstants.PARTNER_TYPE);
		if (senderPartnerType.nullOrEmpty)
		 	throw new AssertionError("senderPartnerType is not set");
		senderPartnerNumber = preferenceStore.getString(IUiConstants.PARTNER_NUMBER);
		if (senderPartnerNumber.nullOrEmpty)
		 	throw new AssertionError("senderPartnerNumber is not set");
		iDocRepository = handler.IDocRepository
		iDocFactory = JCoIDoc.IDocFactory
		if (iDocRepository == null)
			throw new CoreException(new Status(IStatus.ERROR, IDoc2JCoIDocPlugin.ID, "Could not retrieve the IDoc repository"));
		var IDocDocument iDoc = null 
		try {
			monitor.subTask('''Transforming «object.type»''');
			iDoc = iDocFactory.createIDocDocument(iDocRepository, object.type) => [
				IDocNumber = object.name;
				messageType = object.messageType;
				it.senderPartnerNumber = senderPartnerNumber;
				it.senderPartnerType = senderPartnerType;
			];
			// TODO handle object.getFields() via Java reflection for setXXX(Date)
			for (Field field : object.fields) {
				switch field {
					StringField: {
						try {
							iDoc.class.getMethod('''set«field.name»''', String).invoke(iDoc, field.value);
						} catch (Exception e) {
							handleException(object, e) 
						}
					}
					default: handleException(object, new AssertionFailedException('''illegal field type of field «field»'''))
				}
			}
			for (Segment segment : object.segments)
				transform(iDoc.rootSegment, segment) 
		} catch (IDocException exception) {
			handleException(object, exception) 
		}
		return iDoc 
	}

	def private void transform(IDocSegment containerSegment, Segment segment) {
		var IDocSegment transformedSegment 
		try {
			transformedSegment = containerSegment.addChild(segment.type); 
		} catch (IDocException exception) {
			handleException(segment, exception)
			return;
		}
		for (Field field : segment.fields) {
			try {
				switch field {
					StringField: transformedSegment.setValue(field.name, field.value)
					NumberField: transformedSegment.setValue(field.name, field.value)
					default:     handleException(field, new AssertionFailedException('''illegal field type of field «field»''')) 
				}			
			} catch (IDocException exception) {
				handleException(field, exception)
			}
		}
		for (Segment childSegment : segment.segments) {
			transform(transformedSegment, childSegment) 
		}
		
	}

	def private void handleException(EObject sourceObject, Exception exception) {
		sourceObject.eResource.errors.add(new ExceptionDiagnostic(exception));
		// TODO make this appear in editor and error log
		switch exception {
			AssertionFailedException: IDoc2JCoIDocPlugin.log(exception.message, exception) 
			IDocException:            IDoc2JCoIDocPlugin.log(exception.message, exception)
			default:                  IDoc2JCoIDocPlugin.log("", exception)
		}
	}

	override void propertyChange(PropertyChangeEvent event) {
		switch event.property {
			case IUiConstants.PARTNER_TYPE:   senderPartnerType = event.newValue as String
			case IUiConstants.PARTNER_NUMBER: senderPartnerNumber = event.newValue as String
		}
	}
	
}