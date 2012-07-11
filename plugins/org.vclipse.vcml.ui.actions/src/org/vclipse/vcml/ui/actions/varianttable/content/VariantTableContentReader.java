package org.vclipse.vcml.ui.actions.varianttable.content;

import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.EList;
import org.vclipse.vcml.ui.actions.BAPIUtils;
import org.vclipse.vcml.ui.actions.varianttable.VariantTableReader;
import org.vclipse.vcml.vcml.Literal;
import org.vclipse.vcml.vcml.Model;
import org.vclipse.vcml.vcml.NumericLiteral;
import org.vclipse.vcml.vcml.Option;
import org.vclipse.vcml.vcml.Row;
import org.vclipse.vcml.vcml.SymbolicLiteral;
import org.vclipse.vcml.vcml.VariantTable;
import org.vclipse.vcml.vcml.VariantTableContent;

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
		VariantTable variantTable = variantTableReader.read(variantTableName, vcmlModel, monitor, seenObjects, options, recurse);
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
			EList<Row> rows = content.getRows();
			Row row = VCML.createRow();
			EList<Literal> values = row.getValues();
			int numOfColumns = variantTable.getArguments().size();
			int columnIndex = 0;
			for(int numRows=0; numRows<entries.getNumRows(); numRows++) {
				entries.setRow(numRows);
				if(columnIndex == numOfColumns) {
					row = VCML.createRow();
					values = row.getValues();
					rows.add(row);
				}
				String value = entries.getString("VTVALUE");
				try {
					Integer.parseInt(value);
					NumericLiteral numLit = VCML.createNumericLiteral();
					numLit.setValue(value);
					values.add(numLit);
				} catch(NumberFormatException exception) {
					SymbolicLiteral symLit = VCML.createSymbolicLiteral();
					symLit.setValue(value);
					values.add(symLit);
				}
				columnIndex++;
			}
		} catch(AbapException exception) {
			handleAbapException(exception);
		}
		return content;
	}
}
