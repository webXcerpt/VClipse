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

import static org.vclipse.vcml.utils.VCMLObjectUtils.getObjectsByNameAndType;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.EList;
import org.vclipse.bapi.actions.varianttable.VariantTableReader;
import org.vclipse.vcml.vcml.Characteristic;
import org.vclipse.vcml.vcml.CharacteristicType;
import org.vclipse.vcml.vcml.Literal;
import org.vclipse.vcml.vcml.NumericLiteral;
import org.vclipse.vcml.vcml.NumericType;
import org.vclipse.vcml.vcml.Option;
import org.vclipse.vcml.vcml.Row;
import org.vclipse.vcml.vcml.SymbolicLiteral;
import org.vclipse.vcml.vcml.VCObject;
import org.vclipse.vcml.vcml.VariantTable;
import org.vclipse.vcml.vcml.VariantTableArgument;
import org.vclipse.vcml.vcml.VariantTableContent;
import org.vclipse.vcml.vcml.VcmlModel;

import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.sap.conn.jco.AbapException;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoParameterList;
import com.sap.conn.jco.JCoTable;

public class VariantTableContentReader extends VariantTableContentDeleteActionHandler {

	@Inject
	private VariantTableReader variantTableReader;
	
	public VariantTableContent read(String variantTableName, VcmlModel vcmlModel, IProgressMonitor monitor, Map<String, VCObject> seenObjects, List<Option> globalOptions, boolean recurse) throws JCoException {
		if(variantTableName == null || monitor.isCanceled()) {
			return null;
		}
		String id = "VariantTableContent/" + variantTableName.toUpperCase();
		VCObject seenObject = seenObjects.get(id);
		if (seenObject instanceof VariantTableContent) {
			return (VariantTableContent)seenObject;
		}
		VariantTable variantTable = null;
		if(recurse) {
			variantTable = variantTableReader.read(variantTableName, vcmlModel, monitor, seenObjects, globalOptions, recurse);
		} else {
			Iterator<VariantTable> iterator = getObjectsByNameAndType(variantTableName, vcmlModel, VariantTable.class).iterator();
			if(iterator.hasNext()) {
				variantTable = iterator.next();
			}
		}
		if(variantTable == null) {
			return null;
		}
		VariantTableContent content = VCML.createVariantTableContent();
		seenObjects.put(id, content);
		content.setTable(variantTable);
		vcmlModel.getObjects().add(content);
		JCoFunction function = getJCoFunction("CARD_TABLE_READ_ENTRIES", monitor);
		JCoParameterList ipl = function.getImportParameterList();
		handleOptions(content.getOptions(), globalOptions, ipl, "CHANGE_NO", "DATE");
		ipl.setValue("VAR_TABLE", variantTableName.toUpperCase());
		try {
			execute(function, monitor, variantTableName);
			JCoTable entries = function.getTableParameterList().getTable("VAR_TAB_ENTRIES");
			EList<VariantTableArgument> arguments = variantTable.getArguments();
			EList<Row> rows = content.getRows();
			
			Map<String, Integer> name2RowNum = Maps.newHashMap();
			Map<String, Characteristic> name2Cstic = Maps.newHashMap();
			for(VariantTableArgument argument : arguments) {
				Characteristic characteristic = argument.getCharacteristic();
				String name = characteristic.getName();
				name2RowNum.put(name, arguments.indexOf(argument));
				name2Cstic.put(name, characteristic);
			}
			
			Map<String, Row> line2Row = Maps.newHashMap();
			for(int curRow=0; curRow<entries.getNumRows(); curRow++) {
				entries.setRow(curRow);
				String cstic = entries.getString("VTCHARACT");
				CharacteristicType type = name2Cstic.get(cstic).getType();
				Literal literal = null;
				if(type instanceof NumericType) {
					NumericLiteral numLit = VCML.createNumericLiteral();
					numLit.setValue(entries.getString("VTVALUE").replace(",","."));
					literal = numLit;
				} else {
					SymbolicLiteral symLit = VCML.createSymbolicLiteral();
					symLit.setValue(entries.getString("VTVALUE"));
					literal = symLit;
				}
				String line = entries.getString("VTLINENO");
				Row row = line2Row.get(line);
				if(row == null) {
					row = VCML.createRow();
					rows.add(row);
					line2Row.put(line, row);
				} 
				List<Literal> values = row.getValues();
				int columnIndex = name2RowNum.get(cstic);
				values.add(columnIndex, literal);
			}
		} catch(AbapException exception) {
			handleAbapException(exception);
		}
		return content;
	}

}
