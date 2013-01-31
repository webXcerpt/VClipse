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
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.resource.Resource;
import org.vclipse.vcml.vcml.Literal;
import org.vclipse.vcml.vcml.NumericLiteral;
import org.vclipse.vcml.vcml.Option;
import org.vclipse.vcml.vcml.Row;
import org.vclipse.vcml.vcml.SymbolicLiteral;
import org.vclipse.vcml.vcml.VCObject;
import org.vclipse.vcml.vcml.VariantTable;
import org.vclipse.vcml.vcml.VariantTableArgument;
import org.vclipse.vcml.vcml.VariantTableContent;

import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoTable;

public class VariantTableContentCreateActionHandler extends VariantTableContentDeleteActionHandler {

	@Override
	public boolean isEnabled(VariantTableContent content) {
		return isConnected() && !content.getRows().isEmpty();
	}
	
	@Override
	public void run(VariantTableContent content, Resource resource, IProgressMonitor monitor, Map<String, VCObject> seenObjects, List<Option> options) throws Exception {
		VariantTable table = content.getTable();
		JCoFunction deletefunc = maintainEntries(content, monitor, options, true);
		executeTransaction(monitor, "DELETE " + table.getName(), deletefunc);
		JCoFunction createfunc = maintainEntries(content, monitor, options, false);
		JCoTable entries = createfunc.getTableParameterList().getTable("VAR_TAB_ENTRIES");
		EList<Row> rows = content.getRows();
		List<VariantTableArgument> arguments = table.getArguments();
		for(Row row : rows) {
			for(VariantTableArgument arg : arguments) {
				String cstic = arg.getCharacteristic().getName();
				int index = arguments.indexOf(arg);
				entries.appendRow();
				Literal literal = row.getValues().get(index);
				entries.setValue("VTCHARACT", cstic);
				entries.setValue("VTLINENO", "" + rows.indexOf(row));
				entries.setValue("VTVALUE", getValue(literal));
			}
		}
		executeTransaction(monitor, "CREATE/CHANGE " + table.getName(), createfunc);
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
