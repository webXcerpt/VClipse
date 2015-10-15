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

import com.sap.conn.idoc.IDocDocument;
import com.sap.conn.idoc.IDocException;
import com.sap.conn.idoc.IDocFactory;
import com.sap.conn.idoc.jco.JCoIDoc;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoParameterList;
import java.io.PrintStream;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.ui.console.MessageConsoleStream;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.eclipse.xtext.xbase.lib.Exceptions;
import org.vclipse.connection.IConnection;
import org.vclipse.connection.IConnectionHandler;
import org.vclipse.console.CMConsolePlugin;
import org.vclipse.idoc2jcoidoc.IDoc2JCoIDocPlugin;
import org.vclipse.idoc2jcoidoc.IDocSenderStatus;
import org.vclipse.idoc2jcoidoc.IUiConstants;
import org.vclipse.idoc2jcoidoc.internal.NumberAssigningJCoIDocPostProcessor;
import org.vclipse.idoc2jcoidoc.views.IDocsSender;

@SuppressWarnings("all")
public class RFCIDocsSender extends IDocsSender {
  private final static boolean reallySendIDocs = true;
  
  private final PrintStream out;
  
  private final PrintStream result;
  
  private final PrintStream err;
  
  private final PrintStream info;
  
  public RFCIDocsSender() {
    super();
    CMConsolePlugin _default = CMConsolePlugin.getDefault();
    MessageConsoleStream _console = _default.getConsole(CMConsolePlugin.Kind.Task);
    PrintStream _printStream = new PrintStream(_console);
    this.out = _printStream;
    CMConsolePlugin _default_1 = CMConsolePlugin.getDefault();
    MessageConsoleStream _console_1 = _default_1.getConsole(CMConsolePlugin.Kind.Result);
    PrintStream _printStream_1 = new PrintStream(_console_1);
    this.result = _printStream_1;
    CMConsolePlugin _default_2 = CMConsolePlugin.getDefault();
    MessageConsoleStream _console_2 = _default_2.getConsole(CMConsolePlugin.Kind.Error);
    PrintStream _printStream_2 = new PrintStream(_console_2);
    this.err = _printStream_2;
    CMConsolePlugin _default_3 = CMConsolePlugin.getDefault();
    MessageConsoleStream _console_3 = _default_3.getConsole(CMConsolePlugin.Kind.Error);
    PrintStream _printStream_3 = new PrintStream(_console_3);
    this.info = _printStream_3;
  }
  
  @Override
  public IDocSenderStatus send(final List<IDocDocument> iDocDocuments, final IConnectionHandler handler, final IProgressMonitor monitor) {
    try {
      IConnection _currentConnection = handler.getCurrentConnection();
      final String sapSystem = _currentConnection.getSystemName();
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("Sending ");
      int _size = iDocDocuments.size();
      _builder.append(_size, "");
      _builder.append(" IDoc documents to SAP system ");
      _builder.append(sapSystem, "");
      int _size_1 = iDocDocuments.size();
      int _multiply = (3 * _size_1);
      int _plus = (_multiply + (2 * 2));
      monitor.beginTask(_builder.toString(), _plus);
      StringConcatenation _builder_1 = new StringConcatenation();
      _builder_1.append("sending ");
      int _size_2 = iDocDocuments.size();
      _builder_1.append(_size_2, "");
      _builder_1.append(" IDoc documents to SAP system ");
      _builder_1.append(sapSystem, "");
      _builder_1.append(" - started (");
      Date _date = new Date();
      _builder_1.append(_date, "");
      _builder_1.append(")");
      this.out.println(_builder_1);
      this.completeStatus.setSapSystem(sapSystem);
      final IPreferencesService preferences = Platform.getPreferencesService();
      String upsNumber = null;
      boolean _isCanceled = monitor.isCanceled();
      if (_isCanceled) {
        this.cancelStatus.setMessage("Send operation cancelled");
        return this.cancelStatus;
      }
      monitor.subTask("retrieving IDoc numbers");
      this.result.println(" retrieving IDoc numbers");
      final String idocFunctionName = preferences.getString(IDoc2JCoIDocPlugin.ID, IUiConstants.RFC_FOR_IDOC_NUMBERS, "", null);
      final JCoFunction functionIDOC = handler.getJCoFunction(idocFunctionName);
      monitor.worked(1);
      if ((functionIDOC == null)) {
        StringConcatenation _builder_2 = new StringConcatenation();
        _builder_2.append("Error: Function ");
        _builder_2.append(idocFunctionName, "");
        _builder_2.append(" not available on SAP system");
        String errorMessage = _builder_2.toString();
        this.err.println(errorMessage);
        this.cancelStatus.setMessage(errorMessage);
        return this.cancelStatus;
      }
      JCoParameterList _importParameterList = functionIDOC.getImportParameterList();
      int _size_3 = iDocDocuments.size();
      _importParameterList.setValue("QUANTITY", _size_3);
      boolean _isCanceled_1 = monitor.isCanceled();
      if (_isCanceled_1) {
        this.cancelStatus.setMessage("Send operation cancelled");
        return this.cancelStatus;
      }
      JCoDestination _jCoDestination = handler.getJCoDestination();
      functionIDOC.execute(_jCoDestination);
      monitor.worked(1);
      StringConcatenation _builder_3 = new StringConcatenation();
      _builder_3.append(" ");
      _builder_3.append("retrieving IDoc numbers - done: ");
      JCoParameterList _exportParameterList = functionIDOC.getExportParameterList();
      BigInteger _bigInteger = _exportParameterList.getBigInteger("NUMBER");
      _builder_3.append(_bigInteger, " ");
      this.result.println(_builder_3);
      boolean retrieveUPSNumber = false;
      for (final IDocDocument iDocDocument : iDocDocuments) {
        String _iDocType = iDocDocument.getIDocType();
        boolean _equals = "UPSMAS01".equals(_iDocType);
        if (_equals) {
          retrieveUPSNumber = true;
        }
      }
      if (retrieveUPSNumber) {
        boolean _isCanceled_2 = monitor.isCanceled();
        if (_isCanceled_2) {
          this.cancelStatus.setMessage("Send operation cancelled");
          return this.cancelStatus;
        }
        monitor.subTask("retrieving UPS number");
        this.result.println(" retrieving UPS number");
        final String upsFunctionName = preferences.getString(IDoc2JCoIDocPlugin.ID, IUiConstants.RFC_FOR_UPS_NUMBERS, "", null);
        final JCoFunction functionUPS = handler.getJCoFunction(upsFunctionName);
        if ((functionUPS == null)) {
          StringConcatenation _builder_4 = new StringConcatenation();
          _builder_4.append("Error: Function ");
          _builder_4.append(upsFunctionName, "");
          _builder_4.append(" not available on SAP system");
          String errorMessage_1 = _builder_4.toString();
          this.err.println(errorMessage_1);
          this.cancelStatus.setMessage(errorMessage_1);
          return this.cancelStatus;
        }
        boolean _isCanceled_3 = monitor.isCanceled();
        if (_isCanceled_3) {
          this.cancelStatus.setMessage("Send operation cancelled");
          return this.cancelStatus;
        }
        JCoDestination _jCoDestination_1 = handler.getJCoDestination();
        functionUPS.execute(_jCoDestination_1);
        monitor.worked(1);
        JCoParameterList _exportParameterList_1 = functionUPS.getExportParameterList();
        String _string = _exportParameterList_1.getString("NUMBER");
        upsNumber = _string;
        final String prefix = preferences.getString(IDoc2JCoIDocPlugin.ID, IUiConstants.PARTNER_NUMBER, "", null);
        String _substring = upsNumber.substring(3);
        String _plus_1 = (prefix + _substring);
        upsNumber = _plus_1;
        this.completeStatus.setUpsNumber(upsNumber);
        StringConcatenation _builder_5 = new StringConcatenation();
        _builder_5.append(" ");
        _builder_5.append("retrieving UPS number - done: ");
        _builder_5.append(upsNumber, " ");
        this.result.println(_builder_5);
      }
      JCoParameterList _exportParameterList_2 = functionIDOC.getExportParameterList();
      BigInteger _bigInteger_1 = _exportParameterList_2.getBigInteger("NUMBER");
      int _size_4 = iDocDocuments.size();
      BigInteger _valueOf = BigInteger.valueOf(_size_4);
      final BigInteger iDocNumber = _bigInteger_1.subtract(_valueOf);
      NumberAssigningJCoIDocPostProcessor _numberAssigningJCoIDocPostProcessor = new NumberAssigningJCoIDocPostProcessor(upsNumber, iDocNumber);
      _numberAssigningJCoIDocPostProcessor.postprocess(iDocDocuments);
      if ((!RFCIDocsSender.reallySendIDocs)) {
        return this.completeStatus;
      }
      final JCoDestination destination = handler.getJCoDestination();
      for (final IDocDocument iDocDocument_1 : iDocDocuments) {
        {
          boolean _isCanceled_4 = monitor.isCanceled();
          if (_isCanceled_4) {
            this.cancelStatus.setMessage("Send operation cancelled");
            return this.cancelStatus;
          }
          final String tid = destination.createTID();
          monitor.worked(1);
          StringConcatenation _builder_6 = new StringConcatenation();
          _builder_6.append(" ");
          _builder_6.append("sending IDoc ");
          String _iDocType_1 = iDocDocument_1.getIDocType();
          _builder_6.append(_iDocType_1, " ");
          this.result.println(_builder_6);
          StringConcatenation _builder_7 = new StringConcatenation();
          _builder_7.append("sending IDoc ");
          String _iDocType_2 = iDocDocument_1.getIDocType();
          _builder_7.append(_iDocType_2, "");
          monitor.subTask(_builder_7.toString());
          JCoIDoc.send(iDocDocument_1, IDocFactory.IDOC_VERSION_DEFAULT, destination, tid);
          monitor.worked(1);
          destination.confirmTID(tid);
          monitor.worked(1);
        }
      }
      StringConcatenation _builder_6 = new StringConcatenation();
      _builder_6.append("sending ");
      int _size_5 = iDocDocuments.size();
      _builder_6.append(_size_5, "");
      _builder_6.append(" IDoc documents to SAP system - done (");
      Date _date_1 = new Date();
      _builder_6.append(_date_1, "");
      _builder_6.append(")");
      this.result.println(_builder_6);
      if (retrieveUPSNumber) {
        StringConcatenation _builder_7 = new StringConcatenation();
        _builder_7.append("To process the package in SAP, log into ");
        _builder_7.append(sapSystem, "");
        _builder_7.append(" and ");
        _builder_7.newLineIfNotEmpty();
        _builder_7.append("call transaction UPS and search for package number ");
        _builder_7.append(upsNumber, "");
        this.info.println(_builder_7);
      }
    } catch (final Throwable _t) {
      if (_t instanceof IDocException) {
        final IDocException exception = (IDocException)_t;
        exception.printStackTrace();
        String _message = exception.getMessage();
        this.cancelStatus.setMessage(_message);
        return this.cancelStatus;
      } else if (_t instanceof JCoException) {
        final JCoException exception_1 = (JCoException)_t;
        exception_1.printStackTrace();
        String _message_1 = exception_1.getMessage();
        this.cancelStatus.setMessage(_message_1);
        return this.cancelStatus;
      } else {
        throw Exceptions.sneakyThrow(_t);
      }
    }
    return this.completeStatus;
  }
}
