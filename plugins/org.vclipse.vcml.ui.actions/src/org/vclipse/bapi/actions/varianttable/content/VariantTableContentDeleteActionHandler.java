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
package org.vclipse.bapi.actions.varianttable.content;

import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.ecore.resource.Resource;
import org.vclipse.bapi.actions.BAPIUtils;
import org.vclipse.bapi.actions.IBAPIActionRunner;
import org.vclipse.vcml.vcml.Literal;
import org.vclipse.vcml.vcml.NumericLiteral;
import org.vclipse.vcml.vcml.Option;
import org.vclipse.vcml.vcml.SymbolicLiteral;
import org.vclipse.vcml.vcml.VCObject;
import org.vclipse.vcml.vcml.VariantTable;
import org.vclipse.vcml.vcml.VariantTableContent;

import com.sap.conn.jco.AbapException;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoParameterList;

public class VariantTableContentDeleteActionHandler extends BAPIUtils implements IBAPIActionRunner<VariantTableContent> {

	@Override
	public boolean isEnabled(VariantTableContent object) {
		return isConnected();
	}
	
	@Override
	public void run(VariantTableContent content, Resource resource, IProgressMonitor monitor, Map<String, VCObject> seenObjects, List<Option> options) throws Exception {
		String name = content.getTable().getName();
		JCoFunction function = maintainEntries(content, monitor, options, true);
		executeTransaction(monitor, "DELETE " + name, function);
	}

	protected JCoFunction maintainEntries(VariantTableContent content, IProgressMonitor monitor, List<Option> globalOptions, boolean delete) throws JCoException {
		beginTransaction();
		VariantTable table = content.getTable();
		JCoFunction function = getJCoFunction("CAMA_TABLE_MAINTAIN_ENTRIES", monitor);
		JCoParameterList ipl = function.getImportParameterList();
		handleOptions(content.getOptions(), globalOptions, ipl, "CHANGE_NO", "DATE");
		ipl.setValue("VAR_TABLE", table.getName().toUpperCase());
		if(delete) {
			ipl.setValue("FLDELETE", "X");			
		}
		return function;
	}
	
	protected void executeTransaction(IProgressMonitor monitor, String name, JCoFunction function) throws JCoException {
		try {
			execute(function, monitor, name);
			endTransaction();
		} catch (AbapException e) {
			handleAbapException(e);
		}
	}
	
	protected String getValue(Literal literal) {
		if(literal instanceof NumericLiteral) {
			return ((NumericLiteral)literal).getValue();
		} else if(literal instanceof SymbolicLiteral) {
			return ((SymbolicLiteral)literal).getValue();
		}
		return "";
	}
}
