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
package org.vclipse.idoc2jcoidoc;

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
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.eclipse.xtext.diagnostics.ExceptionDiagnostic;
import org.eclipse.xtext.xbase.lib.Exceptions;
import org.vclipse.connection.IConnectionHandler;
import org.vclipse.idoc.iDoc.Field;
import org.vclipse.idoc.iDoc.IDoc;
import org.vclipse.idoc.iDoc.Model;
import org.vclipse.idoc.iDoc.NumberField;
import org.vclipse.idoc.iDoc.Segment;
import org.vclipse.idoc.iDoc.StringField;
import org.vclipse.idoc.iDoc.util.IDocSwitch;
import org.vclipse.idoc2jcoidoc.IDoc2JCoIDocPlugin;
import org.vclipse.idoc2jcoidoc.IIDoc2JCoIDocProcessor;
import org.vclipse.idoc2jcoidoc.IUiConstants;

/**
 * @author tg
 * transforms an ecore IDoc model into a list of SAP JCo IDocDocuments
 * Note: this implements the same as model.accept(new SAPModelIDocConverter(model)) from SAPModelIDocConverter.java from trunk).
 * The addition of the IDoc segments required for serialization should be implemented during the transformation of a vcml.Model to an idoc.Model.
 */
@SuppressWarnings("all")
public class DefaultIDoc2JCoIDocProcessor extends IDocSwitch<Object> implements IIDoc2JCoIDocProcessor, IPropertyChangeListener {
  private IDocRepository iDocRepository;
  
  private IDocFactory iDocFactory;
  
  private String senderPartnerType;
  
  private String senderPartnerNumber;
  
  private final IPreferenceStore preferenceStore;
  
  private final IConnectionHandler handler;
  
  @Inject
  public DefaultIDoc2JCoIDocProcessor(@Named(IDoc2JCoIDocPlugin.ID) final IPreferenceStore preferenceStore, final IConnectionHandler connectionHandler) {
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
  @Override
  public List<IDocDocument> transform(final Model idocModel, final IProgressMonitor monitor) throws JCoException, CoreException {
    final List<IDocDocument> idocDocuments = new ArrayList<IDocDocument>();
    if ((idocModel != null)) {
      final EList<IDoc> idocs = idocModel.getIdocs();
      int _size = idocs.size();
      monitor.beginTask("Running transformation...", _size);
      for (final IDoc idoc : idocs) {
        boolean _isCanceled = monitor.isCanceled();
        if (_isCanceled) {
        } else {
          final IDocDocument iDocDocument = this.transform(idoc, monitor);
          if ((iDocDocument != null)) {
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
    try {
      String _string = this.preferenceStore.getString(IUiConstants.PARTNER_TYPE);
      this.senderPartnerType = _string;
      boolean _isNullOrEmpty = Strings.isNullOrEmpty(this.senderPartnerType);
      boolean _not = (!_isNullOrEmpty);
      boolean _not_1 = (!_not);
      if (_not_1) {
        throw new AssertionError("senderPartnerType is not set");
      }
      String _string_1 = this.preferenceStore.getString(IUiConstants.PARTNER_NUMBER);
      this.senderPartnerNumber = _string_1;
      boolean _isNullOrEmpty_1 = Strings.isNullOrEmpty(this.senderPartnerNumber);
      boolean _not_2 = (!_isNullOrEmpty_1);
      boolean _not_3 = (!_not_2);
      if (_not_3) {
        throw new AssertionError("senderPartnerNumber is not set");
      }
      IDocRepository _iDocRepository = this.handler.getIDocRepository();
      this.iDocRepository = _iDocRepository;
      IDocFactory _iDocFactory = JCoIDoc.getIDocFactory();
      this.iDocFactory = _iDocFactory;
      if ((this.iDocRepository == null)) {
        Status _status = new Status(IStatus.ERROR, IDoc2JCoIDocPlugin.ID, "Could not retrieve the IDoc repository");
        throw new CoreException(_status);
      } else {
        IDocDocument iDoc = null;
        try {
          StringConcatenation _builder = new StringConcatenation();
          _builder.append("Transforming ");
          String _type = object.getType();
          _builder.append(_type, "");
          monitor.subTask(_builder.toString());
          String _type_1 = object.getType();
          IDocDocument _createIDocDocument = this.iDocFactory.createIDocDocument(this.iDocRepository, _type_1);
          iDoc = _createIDocDocument;
          String _name = object.getName();
          iDoc.setIDocNumber(_name);
          String _messageType = object.getMessageType();
          iDoc.setMessageType(_messageType);
          iDoc.setSenderPartnerNumber(this.senderPartnerNumber);
          iDoc.setSenderPartnerType(this.senderPartnerType);
          EList<Field> _fields = object.getFields();
          for (final Field field : _fields) {
            if ((field instanceof StringField)) {
              final StringField stringField = ((StringField) field);
              Method method = null;
              try {
                Class<? extends IDocDocument> _class = iDoc.getClass();
                StringConcatenation _builder_1 = new StringConcatenation();
                _builder_1.append("set");
                String _name_1 = stringField.getName();
                _builder_1.append(_name_1, "");
                Method _method = _class.getMethod(_builder_1.toString(), String.class);
                method = _method;
                String _value = stringField.getValue();
                method.invoke(iDoc, _value);
              } catch (final Throwable _t) {
                if (_t instanceof Exception) {
                  final Exception e = (Exception)_t;
                  this.handleException(object, e);
                } else {
                  throw Exceptions.sneakyThrow(_t);
                }
              }
            } else {
              StringConcatenation _builder_2 = new StringConcatenation();
              _builder_2.append("illegal field type of field ");
              _builder_2.append(field, "");
              AssertionFailedException _assertionFailedException = new AssertionFailedException(_builder_2.toString());
              this.handleException(object, _assertionFailedException);
            }
          }
          EList<Segment> _segments = object.getSegments();
          for (final Segment segment : _segments) {
            IDocSegment _rootSegment = iDoc.getRootSegment();
            this.transform(_rootSegment, segment);
          }
        } catch (final Throwable _t_1) {
          if (_t_1 instanceof IDocException) {
            final IDocException exception = (IDocException)_t_1;
            this.handleException(object, exception);
          } else {
            throw Exceptions.sneakyThrow(_t_1);
          }
        }
        return iDoc;
      }
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  /**
   * @param containerSegment
   * @param segment
   */
  private void transform(final IDocSegment containerSegment, final Segment segment) {
    IDocSegment transformedSegment = null;
    try {
      String _type = segment.getType();
      IDocSegment _addChild = containerSegment.addChild(_type);
      transformedSegment = _addChild;
    } catch (final Throwable _t) {
      if (_t instanceof IDocException) {
        final IDocException exception = (IDocException)_t;
        this.handleException(segment, exception);
        return;
      } else {
        throw Exceptions.sneakyThrow(_t);
      }
    }
    EList<Field> _fields = segment.getFields();
    for (final Field field : _fields) {
      if ((field instanceof StringField)) {
        final StringField stringField = ((StringField) field);
        try {
          String _name = stringField.getName();
          String _value = stringField.getValue();
          transformedSegment.setValue(_name, _value);
        } catch (final Throwable _t_1) {
          if (_t_1 instanceof IDocException) {
            final IDocException exception_1 = (IDocException)_t_1;
            this.handleException(field, exception_1);
          } else {
            throw Exceptions.sneakyThrow(_t_1);
          }
        }
      } else {
        if ((field instanceof NumberField)) {
          final NumberField numberField = ((NumberField) field);
          try {
            String _name_1 = numberField.getName();
            int _value_1 = numberField.getValue();
            transformedSegment.setValue(_name_1, _value_1);
          } catch (final Throwable _t_2) {
            if (_t_2 instanceof IDocException) {
              final IDocException e = (IDocException)_t_2;
              this.handleException(field, e);
            } else {
              throw Exceptions.sneakyThrow(_t_2);
            }
          }
        } else {
          StringConcatenation _builder = new StringConcatenation();
          _builder.append("illegal field type of field ");
          _builder.append(field, "");
          AssertionFailedException _assertionFailedException = new AssertionFailedException(_builder.toString());
          this.handleException(field, _assertionFailedException);
        }
      }
    }
    EList<Segment> _segments = segment.getSegments();
    for (final Segment childSegment : _segments) {
      this.transform(transformedSegment, childSegment);
    }
  }
  
  /**
   * @param sourceObject
   * @param exception
   */
  private void handleException(final EObject sourceObject, final Exception exception) {
    Resource _eResource = sourceObject.eResource();
    EList<Resource.Diagnostic> _errors = _eResource.getErrors();
    ExceptionDiagnostic _exceptionDiagnostic = new ExceptionDiagnostic(exception);
    _errors.add(_exceptionDiagnostic);
    if ((exception instanceof AssertionFailedException)) {
      String _message = ((AssertionFailedException) exception).getMessage();
      IDoc2JCoIDocPlugin.log(_message, exception);
    } else {
      if ((exception instanceof IDocException)) {
        String _message_1 = ((IDocException) exception).getMessage();
        IDoc2JCoIDocPlugin.log(_message_1, exception);
      } else {
        IDoc2JCoIDocPlugin.log("", exception);
      }
    }
  }
  
  /**
   * @see org.eclipse.jface.util.IPropertyChangeListener#propertyChange(org.eclipse.jface.util.PropertyChangeEvent)
   */
  @Override
  public void propertyChange(final PropertyChangeEvent event) {
    String _property = event.getProperty();
    boolean _equals = IUiConstants.PARTNER_TYPE.equals(_property);
    if (_equals) {
      Object _newValue = event.getNewValue();
      this.senderPartnerType = ((String) _newValue);
    }
    String _property_1 = event.getProperty();
    boolean _equals_1 = IUiConstants.PARTNER_NUMBER.equals(_property_1);
    if (_equals_1) {
      Object _newValue_1 = event.getNewValue();
      this.senderPartnerNumber = ((String) _newValue_1);
    }
  }
}
