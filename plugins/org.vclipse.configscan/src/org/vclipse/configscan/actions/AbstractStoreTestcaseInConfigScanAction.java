/*******************************************************************************
 * Copyright (c) 2012 webXcerpt Software GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    webXcerpt Software GmbH - initial creator
 ******************************************************************************/
package org.vclipse.configscan.actions;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.emf.common.util.WrappedException;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.vclipse.connection.IConnection;
import org.vclipse.connection.IConnectionHandler;

import com.google.inject.Inject;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoParameterList;
import com.sap.conn.jco.JCoStructure;

/**
 *
 */
public abstract class AbstractStoreTestcaseInConfigScanAction implements IObjectActionDelegate {

	private IStructuredSelection selection;

	@Inject
	private IConnectionHandler handler;
	
	public void run(IAction action) {
		Job job = new Job("Store test cases in ConfigScan") {
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				monitor.beginTask(
						"Storing test cases in ConfigScan",
						selection.size());
				for (Object s : selection.toList()) {
					if (monitor.isCanceled()) {
						return Status.CANCEL_STATUS;
					}
					if (s instanceof IFile) {
						IFile file = (IFile)s;
						monitor.subTask(file.getName());
						try {
							storeTestcaseFileInConfigScan(file);
						} catch (JCoException ex) {
							throw new WrappedException(ex);
						}
						monitor.worked(1);
					}
					
				}
				monitor.done();
				return Status.OK_STATUS;
			}
		};
		job.schedule();
	}

	abstract protected void storeTestcaseFileInConfigScan(IFile file) throws JCoException;
		
	/*
	FUNCTION zfsb_add_tc_string_to_kmat .
	*"----------------------------------------------------------------------
	*"*"Local Interface:
	*"  IMPORTING
	*"     VALUE(IV_XML_STRING) TYPE  STRING
	*"     VALUE(IV_WSAPPLICATION) TYPE  DAPPL
	*"     VALUE(IV_DOCUMENT) TYPE  ZFSB_ST_DOCUMENT
	*"     VALUE(IV_STORAGE_CAT) TYPE  DTTRG
	*"  EXPORTING
	*"     VALUE(RETURN) TYPE  BAPIRET2
	*/

	protected void storeTestcaseInConfigScan(String xmlString, String matNr,
			String docNumber, String docDescr, String docVersion, String docPart)
			throws JCoException {
		if (docDescr == null) {
			docDescr = docNumber;
		}
		if (docVersion == null) {
			docVersion = "01";
		}
		if (docPart == null) {
			docPart = "001";
		}
		IConnection currentConnection = handler.getCurrentConnection();
		if (currentConnection==null) {
			throw new IllegalArgumentException("not connected");
		}
		JCoFunction function = handler.getJCoFunction("ZFSB_ADD_TC_STRING_TO_KMAT");
		JCoParameterList importParameterList = function.getImportParameterList();
		importParameterList.setValue("IV_XML_STRING", xmlString);
		importParameterList.setValue("IV_WSAPPLICATION", "XML");
		importParameterList.setValue("IV_STORAGE_CAT", "SAP-SYSTEM");
		JCoStructure structure = importParameterList.getStructure("IV_DOCUMENT");
		structure.setValue("MATNR", matNr);
		structure.setValue("DOC_TYPE", "ZCF"); // 3 characters
		structure.setValue("DOC_NUMBER", docNumber); // 25 characters
		structure.setValue("DOC_DESCR", docDescr); // 40 characters
		structure.setValue("DOC_VERSION", docVersion); // 2 characters
		structure.setValue("DOC_PART", docPart); // 3 characters
		function.execute(handler.getJCoDestination());
	}

	public void selectionChanged(IAction action, ISelection selection) {
		this.selection = (IStructuredSelection) selection;
	}

	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
	}

}
