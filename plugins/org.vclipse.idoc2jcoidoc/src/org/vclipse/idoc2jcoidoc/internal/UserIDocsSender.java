/*******************************************************************************
 * Copyright (c) 2010 webXcerpt Software GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     webXcerpt Software GmbH - initial creator
 ******************************************************************************/
package org.vclipse.idoc2jcoidoc.internal;

import java.io.PrintStream;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.xtext.util.Strings;
import org.vclipse.connection.IConnectionHandler;
import org.vclipse.console.CMConsolePlugin;
import org.vclipse.console.CMConsolePlugin.Kind;
import org.vclipse.idoc2jcoidoc.IDoc2JCoIDocPlugin;
import org.vclipse.idoc2jcoidoc.IDocSenderStatus;
import org.vclipse.idoc2jcoidoc.views.IDocsSender;

import com.sap.conn.idoc.IDocDocument;
import com.sap.conn.idoc.IDocException;
import com.sap.conn.idoc.IDocFactory;
import com.sap.conn.idoc.jco.JCoIDoc;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoException;

/**
 * 
 */
public class UserIDocsSender extends IDocsSender {

	private static final boolean reallySendIDocs = true;

	/**
	 * 
	 */
	private final PrintStream out;
	
	/**
	 * 
	 */
	public UserIDocsSender(final Shell shell) {
		super(shell);
		out = new PrintStream(CMConsolePlugin.getDefault().getConsole(Kind.Task));
	}

	/**
	 * @see org.vclipse.idoc2jcoidoc.IIDocsSender#send(java.util.List, org.vclipse.connection.ISapConnectionHandler, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public IDocSenderStatus send(final List<IDocDocument> iDocDocuments, final IConnectionHandler handler, final IProgressMonitor monitor) {
		final NumbersProviderDialog dialog = new NumbersProviderDialog(parentShell);
		if(Window.OK == dialog.open()) {
			final String upsNumber = dialog.getUPSNumber();
			final BigInteger iDocNumber = dialog.getIDocNumber();
			out.println("UPS number: " + upsNumber + ", IDoc number: " + iDocNumber);
			try {
				new NumberAssigningJCoIDocPostProcessor(upsNumber, iDocNumber).postprocess(iDocDocuments);
				if (!reallySendIDocs) return completeStatus;
				// send IDocs
				final JCoDestination destination = handler.getJCoDestination();
				for(IDocDocument iDocDocument : iDocDocuments) {
					if (monitor.isCanceled()) {
						cancelStatus.setMessage("Send operation cancelled.");
						return cancelStatus;
					}
					final String tid = destination.createTID();
					monitor.worked(1);
					out.println(" sending IDoc " + iDocDocument.getIDocType()); // TODO print to console
					JCoIDoc.send(iDocDocument, IDocFactory.IDOC_VERSION_DEFAULT, destination, tid);
					monitor.worked(1);
					destination.confirmTID(tid);
					monitor.worked(1);
				}
				out.println("sending " + iDocDocuments.size() + " IDoc documents to SAP system - done (" + new Date() + ")");
				if (!Strings.isEmpty(upsNumber)){
					out.println("To process the package in SAP, log into " + "SYSTEM NAME" + " and \ncall transaction UPS and search for package number " + upsNumber);
				}
			} catch (IDocException e) {
				e.printStackTrace();
				IDoc2JCoIDocPlugin.log(e.getMessage(), e);
			} catch (JCoException e) {
				e.printStackTrace();
				IDoc2JCoIDocPlugin.log(e.getMessage(), e);
			}
		}		
		return completeStatus;
	}

}
