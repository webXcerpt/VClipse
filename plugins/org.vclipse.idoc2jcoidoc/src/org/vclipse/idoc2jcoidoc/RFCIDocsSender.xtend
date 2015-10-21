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

import java.io.PrintStream
import java.math.BigInteger
import java.util.Date
import java.util.List
import org.eclipse.core.runtime.IProgressMonitor
import org.eclipse.core.runtime.Platform
import org.eclipse.core.runtime.preferences.IPreferencesService
import org.vclipse.connection.IConnectionHandler
import org.vclipse.console.CMConsolePlugin
import org.vclipse.console.CMConsolePlugin.Kind
import org.vclipse.idoc2jcoidoc.internal.NumberAssigningJCoIDocPostProcessor
import org.vclipse.idoc2jcoidoc.views.IDocsSender
import com.sap.conn.idoc.IDocDocument
import com.sap.conn.idoc.IDocException
import com.sap.conn.idoc.IDocFactory
import com.sap.conn.idoc.jco.JCoIDoc
import com.sap.conn.jco.JCoDestination
import com.sap.conn.jco.JCoException
import com.sap.conn.jco.JCoFunction

class RFCIDocsSender extends IDocsSender {
	static final boolean reallySendIDocs = true // for debugging 

	final PrintStream out
	final PrintStream result
	final PrintStream err
	final PrintStream info

	new() {
		super();
		out    = new PrintStream(CMConsolePlugin.^default.getConsole(Kind.Task));
		result = new PrintStream(CMConsolePlugin.^default.getConsole(Kind.Result));
		err    = new PrintStream(CMConsolePlugin.^default.getConsole(Kind.Error));
		info   = new PrintStream(CMConsolePlugin.^default.getConsole(Kind.Error)); 
	}
	
	override IDocSenderStatus send(List<IDocDocument> iDocDocuments, IConnectionHandler handler, IProgressMonitor monitor) {
		try {
			val String sapSystem = handler.currentConnection.systemName; 
			monitor.beginTask('''Sending «iDocDocuments.size» IDoc documents to SAP system «sapSystem»''', 3 * iDocDocuments.size() + 2 * 2) /* 2x2 transactions for number RFCs */
			out.println('''sending «iDocDocuments.size()» IDoc documents to SAP system «sapSystem» - started («new Date()»)''');
			completeStatus.setSapSystem(sapSystem);
			val IPreferencesService preferences = Platform.preferencesService 
			var String upsNumber = null;
			if (monitor.isCanceled) {
				cancelStatus.setMessage("Send operation cancelled");
				return cancelStatus 
			}
			monitor.subTask("retrieving IDoc numbers");
			result.println(" retrieving IDoc numbers");
			val String idocFunctionName = preferences.getString(IDoc2JCoIDocPlugin.ID, IUiConstants.RFC_FOR_IDOC_NUMBERS, "", null); 
			val JCoFunction functionIDOC = handler.getJCoFunction(idocFunctionName); 
			monitor.worked(1);
			if (functionIDOC === null) {
				// TODO use exceptions
				var String errorMessage = '''Error: Function «idocFunctionName» not available on SAP system'''; 
				err.println(errorMessage);
				cancelStatus.setMessage(errorMessage);
				return cancelStatus; 
			}
			functionIDOC.importParameterList.setValue("QUANTITY", iDocDocuments.size); // evtl. als 20-stelligen String codieren
			if (monitor.isCanceled) {
				cancelStatus.setMessage("Send operation cancelled");
				return cancelStatus; 
			}
			functionIDOC.execute(handler.JCoDestination);
			monitor.worked(1);
			result.println(''' retrieving IDoc numbers - done: «functionIDOC.exportParameterList.getBigInteger("NUMBER")»'''); // neccessary to retrieve UPS number?

			val boolean retrieveUPSNumber = iDocDocuments.exists[IDocType == "UPSMAS01"]; 
			if (retrieveUPSNumber) {
				if (monitor.isCanceled) {
					cancelStatus.setMessage("Send operation cancelled");
					return cancelStatus; 
				}
				monitor.subTask("retrieving UPS number");
				result.println(" retrieving UPS number");
				val String upsFunctionName = preferences.getString(IDoc2JCoIDocPlugin.ID, IUiConstants.RFC_FOR_UPS_NUMBERS, "", null); 
				val JCoFunction functionUPS = handler.getJCoFunction(upsFunctionName) 
				if (functionUPS === null) {
					// TODO use exceptions
					var String errorMessage = '''Error: Function «upsFunctionName» not available on SAP system'''; 
					err.println(errorMessage);
					cancelStatus.setMessage(errorMessage);
					return cancelStatus; 
				}
				if (monitor.isCanceled) {
					cancelStatus.setMessage("Send operation cancelled");
					return cancelStatus; 
				}
				functionUPS.execute(handler.JCoDestination);
				monitor.worked(1);
				upsNumber=functionUPS.exportParameterList.getString("NUMBER");
				val String prefix = preferences.getString(IDoc2JCoIDocPlugin.ID, IUiConstants.PARTNER_NUMBER, "", null); 
				upsNumber = prefix + upsNumber.substring(3);
				completeStatus.upsNumber = upsNumber;
				result.println(''' retrieving UPS number - done: «upsNumber»'''); 
			}
			val BigInteger iDocNumber = functionIDOC.exportParameterList.getBigInteger("NUMBER").subtract(BigInteger.valueOf(iDocDocuments.size));
			new NumberAssigningJCoIDocPostProcessor(upsNumber, iDocNumber).postprocess(iDocDocuments);
			if (!reallySendIDocs)
				return completeStatus; // send IDocs
			val JCoDestination destination = handler.JCoDestination; 
			for (IDocDocument iDocDocument : iDocDocuments) {
				if (monitor.isCanceled) {
					cancelStatus.setMessage("Send operation cancelled");
					return cancelStatus 
				}
				val String tid = destination.createTID; 
				monitor.worked(1);
				result.println(''' sending IDoc «iDocDocument.getIDocType()»''');
				monitor.subTask('''sending IDoc «iDocDocument.getIDocType()»''');
				JCoIDoc.send(iDocDocument, IDocFactory.IDOC_VERSION_DEFAULT, destination, tid);
				monitor.worked(1);
				destination.confirmTID(tid);
				monitor.worked(1); 
			}
			result.println('''sending «iDocDocuments.size» IDoc documents to SAP system - done («new Date»)''');
			 if (retrieveUPSNumber) {
				info.println('''To process the package in SAP, log into «sapSystem» and 
call transaction UPS and search for package number «upsNumber»''') 
			}
		} catch (IDocException exception) {
			exception.printStackTrace;
			cancelStatus.setMessage(exception.message);
			return cancelStatus;
		} catch (JCoException exception) {
			exception.printStackTrace;
			cancelStatus.setMessage(exception.message);
			return cancelStatus;
		}
		return completeStatus 
	}
	
}