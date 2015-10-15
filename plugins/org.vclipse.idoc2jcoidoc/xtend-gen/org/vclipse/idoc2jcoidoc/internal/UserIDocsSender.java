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
package org.vclipse.idoc2jcoidoc.internal;

import com.sap.conn.idoc.IDocDocument;
import com.sap.conn.idoc.IDocException;
import com.sap.conn.idoc.IDocFactory;
import com.sap.conn.idoc.jco.JCoIDoc;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoException;
import java.io.PrintStream;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.console.MessageConsoleStream;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.eclipse.xtext.util.Strings;
import org.eclipse.xtext.xbase.lib.Exceptions;
import org.vclipse.connection.IConnectionHandler;
import org.vclipse.console.CMConsolePlugin;
import org.vclipse.idoc2jcoidoc.IDoc2JCoIDocPlugin;
import org.vclipse.idoc2jcoidoc.IDocSenderStatus;
import org.vclipse.idoc2jcoidoc.internal.NumberAssigningJCoIDocPostProcessor;
import org.vclipse.idoc2jcoidoc.internal.NumbersProviderDialog;
import org.vclipse.idoc2jcoidoc.views.IDocsSender;

@SuppressWarnings("all")
public class UserIDocsSender extends IDocsSender {
  private final static boolean reallySendIDocs = true;
  
  private final PrintStream out;
  
  public UserIDocsSender(final Shell shell) {
    super(shell);
    CMConsolePlugin _default = CMConsolePlugin.getDefault();
    MessageConsoleStream _console = _default.getConsole(CMConsolePlugin.Kind.Task);
    PrintStream _printStream = new PrintStream(_console);
    this.out = _printStream;
  }
  
  /**
   * @see org.vclipse.idoc2jcoidoc.IIDocsSender#send(java.util.List, org.vclipse.connection.ISapConnectionHandler, org.eclipse.core.runtime.IProgressMonitor)
   */
  @Override
  public IDocSenderStatus send(final List<IDocDocument> iDocDocuments, final IConnectionHandler handler, final IProgressMonitor monitor) {
    final NumbersProviderDialog dialog = new NumbersProviderDialog(this.parentShell);
    int _open = dialog.open();
    boolean _tripleEquals = (Window.OK == _open);
    if (_tripleEquals) {
      final String upsNumber = dialog.getUPSNumber();
      final BigInteger iDocNumber = dialog.getIDocNumber();
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("UPS number: ");
      _builder.append(upsNumber, "");
      _builder.append(", IDoc number: ");
      _builder.append(iDocNumber, "");
      String _string = _builder.toString();
      this.out.println(_string);
      try {
        NumberAssigningJCoIDocPostProcessor _numberAssigningJCoIDocPostProcessor = new NumberAssigningJCoIDocPostProcessor(upsNumber, iDocNumber);
        _numberAssigningJCoIDocPostProcessor.postprocess(iDocDocuments);
        if ((!UserIDocsSender.reallySendIDocs)) {
          return this.completeStatus;
        }
        final JCoDestination destination = handler.getJCoDestination();
        for (final IDocDocument iDocDocument : iDocDocuments) {
          {
            boolean _isCanceled = monitor.isCanceled();
            if (_isCanceled) {
              this.cancelStatus.setMessage("Send operation cancelled.");
              return this.cancelStatus;
            }
            final String tid = destination.createTID();
            monitor.worked(1);
            StringConcatenation _builder_1 = new StringConcatenation();
            _builder_1.append(" ");
            _builder_1.append("sending IDoc ");
            String _iDocType = iDocDocument.getIDocType();
            _builder_1.append(_iDocType, " ");
            String _string_1 = _builder_1.toString();
            this.out.println(_string_1);
            JCoIDoc.send(iDocDocument, IDocFactory.IDOC_VERSION_DEFAULT, destination, tid);
            monitor.worked(1);
            destination.confirmTID(tid);
            monitor.worked(1);
          }
        }
        StringConcatenation _builder_1 = new StringConcatenation();
        _builder_1.append("sending ");
        int _size = iDocDocuments.size();
        _builder_1.append(_size, "");
        _builder_1.append(" IDoc documents to SAP system - done (");
        Date _date = new Date();
        _builder_1.append(_date, "");
        _builder_1.append(")");
        String _string_1 = _builder_1.toString();
        this.out.println(_string_1);
        boolean _isEmpty = Strings.isEmpty(upsNumber);
        boolean _not = (!_isEmpty);
        if (_not) {
          StringConcatenation _builder_2 = new StringConcatenation();
          _builder_2.append("To process the package in SAP, log into SYSTEM NAME and ");
          _builder_2.newLine();
          _builder_2.append("call transaction UPS and search for package number ");
          _builder_2.append(upsNumber, "");
          String _string_2 = _builder_2.toString();
          this.out.println(_string_2);
        }
      } catch (final Throwable _t) {
        if (_t instanceof IDocException) {
          final IDocException e = (IDocException)_t;
          e.printStackTrace();
          String _message = e.getMessage();
          IDoc2JCoIDocPlugin.log(_message, e);
        } else if (_t instanceof JCoException) {
          final JCoException e_1 = (JCoException)_t;
          e_1.printStackTrace();
          String _message_1 = e_1.getMessage();
          IDoc2JCoIDocPlugin.log(_message_1, e_1);
        } else {
          throw Exceptions.sneakyThrow(_t);
        }
      }
    }
    return this.completeStatus;
  }
}
