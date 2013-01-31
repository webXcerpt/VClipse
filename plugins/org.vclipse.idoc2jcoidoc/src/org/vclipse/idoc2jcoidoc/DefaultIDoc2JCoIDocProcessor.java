/*******************************************************************************
 * Copyright (c) 2010 - 2013 webXcerpt Software GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *  
 * Contributors:
 *     	webXcerpt Software GmbH - initial creator
 * 		www.webxcerpt.com
 ******************************************************************************/
package org.vclipse.idoc2jcoidoc;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.AssertionFailedException;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.xtext.diagnostics.ExceptionDiagnostic;
import org.vclipse.connection.IConnectionHandler;
import org.vclipse.idoc.iDoc.Field;
import org.vclipse.idoc.iDoc.IDoc;
import org.vclipse.idoc.iDoc.Model;
import org.vclipse.idoc.iDoc.NumberField;
import org.vclipse.idoc.iDoc.Segment;
import org.vclipse.idoc.iDoc.StringField;
import org.vclipse.idoc.iDoc.util.IDocSwitch;

import com.google.common.base.Strings;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.sap.conn.idoc.IDocDocument;
import com.sap.conn.idoc.IDocException;
import com.sap.conn.idoc.IDocFactory;
import com.sap.conn.idoc.IDocRepository;
import com.sap.conn.idoc.IDocSegment;
import com.sap.conn.idoc.jco.JCoIDoc;
import com.sap.conn.jco.JCoException;

/**
 * 
 * @author tg
 * 
 * transforms an ecore IDoc model into a list of SAP JCo IDocDocuments
 * 
 * Note: this implements the same as model.accept(new SAPModelIDocConverter(model)) from SAPModelIDocConverter.java from trunk).
 * The addition of the IDoc segments required for serialization should be implemented during the transformation of a vcml.Model to an idoc.Model.
 *
 */
public class DefaultIDoc2JCoIDocProcessor extends IDocSwitch<Object> implements IIDoc2JCoIDocProcessor, IPropertyChangeListener {

	private IDocRepository iDocRepository;
	private IDocFactory iDocFactory;
		
	private String senderPartnerType;
	private String senderPartnerNumber;
	
	/**
	 * 
	 */
	private final IPreferenceStore preferenceStore;
	
	/**
	 * 
	 */
	private final IConnectionHandler handler;
	
	/**
	 * 
	 */
	@Inject
	public DefaultIDoc2JCoIDocProcessor(@Named(IDoc2JCoIDocPlugin.ID) IPreferenceStore preferenceStore, IConnectionHandler connectionHandler) {
		super();
		this.preferenceStore = preferenceStore;
		this.handler = connectionHandler;
		this.preferenceStore.addPropertyChangeListener(this);
	}
	
	/**
	 * @see java.lang.Object#finalize()
	 */
	@Override
	protected void finalize() throws Throwable {
		this.preferenceStore.removePropertyChangeListener(this);
		super.finalize();
	}
	
	/**
	 * @param idocModel
	 * @param monitor
	 * @return
	 * @throws CoreException
	 */
	public List<IDocDocument> transform(final Model idocModel, final IProgressMonitor monitor) throws JCoException, CoreException {
		final List<IDocDocument> idocDocuments = new ArrayList<IDocDocument>();
		if(idocModel != null) {
			final EList<IDoc> idocs = idocModel.getIdocs();
			monitor.beginTask("Running transformation...", idocs.size());
			for (IDoc idoc : idocs) {
				if(monitor.isCanceled()) {
					break;
				} else {
					final IDocDocument iDocDocument = transform(idoc, monitor);
					if (iDocDocument!=null) {
						idocDocuments.add(iDocDocument);
					}
					monitor.worked(1);
				}
			}
			monitor.done();
		} 
		return idocDocuments;
	}
		
	/**
	 * @param object
	 * @param monitor
	 * @return
	 * @throws CoreException
	 */
	public IDocDocument transform(final IDoc object, final IProgressMonitor monitor) throws JCoException, CoreException {
		senderPartnerType = preferenceStore.getString(IUiConstants.PARTNER_TYPE);
		assert !Strings.isNullOrEmpty(senderPartnerType) : "senderPartnerType is not set"; 
		senderPartnerNumber = preferenceStore.getString(IUiConstants.PARTNER_NUMBER);		
		assert !Strings.isNullOrEmpty(senderPartnerNumber) : "senderPartnerNumber is not set"; 
		iDocRepository = handler.getIDocRepository();
		iDocFactory = JCoIDoc.getIDocFactory();
		if(iDocRepository == null) {
			throw new CoreException(new Status(IStatus.ERROR, IDoc2JCoIDocPlugin.ID, "Could not retrieve the IDoc repository"));
		} else {
			IDocDocument iDoc = null;
			try {
				monitor.subTask("Transforming " + object.getType());
				iDoc = iDocFactory.createIDocDocument(iDocRepository, object.getType());
				iDoc.setIDocNumber(object.getName());
				iDoc.setMessageType(object.getMessageType());
				iDoc.setSenderPartnerNumber(senderPartnerNumber);
				iDoc.setSenderPartnerType(senderPartnerType);
				// TODO handle object.getFields() via Java reflection for setXXX(Date)
				for (Field field : object.getFields()) {
					if (field instanceof StringField) {
						final StringField stringField = (StringField)field;
						Method method;
						try {
							method = iDoc.getClass().getMethod("set" + stringField.getName(), java.lang.String.class);
							method.invoke(iDoc, stringField.getValue());
						} catch (Exception e) {
							handleException(object, e);
						}
					} else {
						handleException(object, new AssertionFailedException("illegal field type of field " + field));
					}
				}
				
				for (Segment segment : object.getSegments()) {
					transform(iDoc.getRootSegment(), segment);
				}
			} catch (IDocException exception) {
				handleException(object, exception);
			}
			return iDoc;
		}
	}

	/**
	 * @param containerSegment
	 * @param segment
	 */
	private void transform(final IDocSegment containerSegment, final Segment segment) {
		IDocSegment transformedSegment;
		try {
			transformedSegment = containerSegment.addChild(segment.getType());
		} catch (IDocException exception) {
			handleException(segment, exception);
			return;
		}
		for (Field field : segment.getFields()) {
			if (field instanceof StringField) {
				final StringField stringField = (StringField)field;
				try {
					transformedSegment.setValue(stringField.getName(), stringField.getValue());
				} catch (IDocException exception) {
					handleException(field, exception);
				}
			} else if (field instanceof NumberField) {
				final NumberField numberField = (NumberField)field;
				try {
					transformedSegment.setValue(numberField.getName(), numberField.getValue());
				} catch (IDocException e) {
					handleException(field, e);
				}
			} else {
				handleException(field, new AssertionFailedException("illegal field type of field " + field));
			}
		}
		for (Segment childSegment : segment.getSegments()) {
			transform(transformedSegment, childSegment);
		}
	}

	/**
	 * @param sourceObject
	 * @param exception
	 */
	private void handleException(final EObject sourceObject, final Exception exception) {
		sourceObject.eResource().getErrors().add(new ExceptionDiagnostic(exception)); // TODO make this appear in editor and error log
		if(exception instanceof AssertionFailedException) {
			IDoc2JCoIDocPlugin.log(((AssertionFailedException)exception).getMessage(), exception);
		} else if(exception instanceof IDocException) {
			IDoc2JCoIDocPlugin.log(((IDocException)exception).getMessage(), exception);
		} else {
			IDoc2JCoIDocPlugin.log("", exception);
		}
	}

	/**
	 * @see org.eclipse.jface.util.IPropertyChangeListener#propertyChange(org.eclipse.jface.util.PropertyChangeEvent)
	 */
	@Override
	public void propertyChange(final PropertyChangeEvent event) {
		if(IUiConstants.PARTNER_TYPE.equals(event.getProperty())) {
			senderPartnerType = (String)event.getNewValue();
		}
		if(IUiConstants.PARTNER_NUMBER.equals(event.getProperty())) {
			senderPartnerNumber = (String)event.getNewValue();
		}
	}
}
