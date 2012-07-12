package org.vclipse.vcml.ui.actions.varianttable.content;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.EList;
import org.vclipse.vcml.ui.actions.BAPIUtils;
import org.vclipse.vcml.ui.actions.varianttable.VariantTableReader;
import org.vclipse.vcml.utils.VCMLObjectUtils;
import org.vclipse.vcml.vcml.Literal;
import org.vclipse.vcml.vcml.Model;
import org.vclipse.vcml.vcml.NumericLiteral;
import org.vclipse.vcml.vcml.Option;
import org.vclipse.vcml.vcml.Row;
import org.vclipse.vcml.vcml.SymbolicLiteral;
import org.vclipse.vcml.vcml.VariantTable;
import org.vclipse.vcml.vcml.VariantTableArgument;
import org.vclipse.vcml.vcml.VariantTableContent;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.sap.conn.jco.AbapException;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoParameterList;
import com.sap.conn.jco.JCoTable;

public class VariantTableContentReader extends BAPIUtils {

	@Inject
	private VariantTableReader variantTableReader;
	
	public VariantTableContent read(String variantTableName, Model vcmlModel, IProgressMonitor monitor, Set<String> seenObjects, List<Option> options, boolean recurse) throws JCoException {
		if(variantTableName == null || !seenObjects.add("VariantTableContent/" + variantTableName.toUpperCase()) || monitor.isCanceled()) {
			return null;
		}
		VariantTable variantTable = null;
		if(recurse) {
			variantTable = variantTableReader.read(variantTableName, vcmlModel, monitor, seenObjects, options, recurse);
		} else {
			Iterator<VariantTable> iterator = VCMLObjectUtils.getObjectsByNameAndType(variantTableName, vcmlModel, VariantTable.class).iterator();
			if(iterator.hasNext()) {
				variantTable = iterator.next();
			}
		}
		if(variantTable == null) {
			return null;
		}
		VariantTableContent content = VCML.createVariantTableContent();
		content.setTable(variantTable);
		vcmlModel.getObjects().add(content);
		JCoFunction function = getJCoFunction("CARD_TABLE_READ_ENTRIES", monitor);
		JCoParameterList ipl = function.getImportParameterList();
		handleOptions(options, ipl, "CHANGE_NO", "DATE");
		ipl.setValue("VAR_TABLE", variantTableName);
		try {
			execute(function, monitor, variantTableName);
			JCoTable entries = function.getTableParameterList().getTable("VAR_TAB_ENTRIES");
			EList<VariantTableArgument> arguments = variantTable.getArguments();
			EList<Row> rows = content.getRows();
			Map<String, Integer> name2Argument = Maps.newHashMap();
			for(VariantTableArgument argument : arguments) {
				name2Argument.put(argument.getCharacteristic().getName(), arguments.indexOf(argument));
			}
			Map<String, Row> line2Row = Maps.newHashMap();
			for(int curRow=0; curRow<entries.getNumRows(); curRow++) {
				entries.setRow(curRow);
				String cstic = entries.getString("VTCHARACT");
				String value = entries.getString("VTVALUE");
				String line = entries.getString("VTLINENO");
				Row row = line2Row.get(line);
				List<Literal> values = Lists.newArrayList();
				if(row == null) {
					row = VCML.createRow();
					values = row.getValues();
					rows.add(row);
					line2Row.put(line, row);
				}
				int columnIndex = name2Argument.get(cstic);
				Literal literal = getLiteral(value);
				values.add(columnIndex, literal);
			}
		} catch(AbapException exception) {
			handleAbapException(exception);
		}
		return content;
	}

	protected Literal getLiteral(String value) {
		try {
			Integer.parseInt(value);
			NumericLiteral numLit = VCML.createNumericLiteral();
			numLit.setValue(value);
			return numLit;
		} catch(NumberFormatException exception) {
			SymbolicLiteral symLit = VCML.createSymbolicLiteral();
			symLit.setValue(value);
			return symLit;
		}
	}
}
