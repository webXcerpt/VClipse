package org.vclipse.vcml.ui.actions.varianttable.content;

import java.util.Iterator;
import java.util.List;
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

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
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
			int rowsNumberSap = entries.getNumRows();
			int rowsNumberContent = rowsNumberSap / arguments.size();
			Literal[][] contentEntries = new Literal[rowsNumberContent][arguments.size()];
			for(int numRows=0; numRows<rowsNumberSap; numRows++) {
				entries.setRow(numRows);
				String cstic = entries.getString("VTCHARACT");
				String entryValue = entries.getString("VTVALUE");
				VariantTableArgument argument = getArgument(cstic, arguments);
				Literal literal = getLiteral(entryValue);
				int columnIndex = arguments.indexOf(argument);
				int rowIndex = numRows % rowsNumberContent;
				contentEntries[rowIndex][columnIndex] = literal;
			}
			for(int i=0; i<contentEntries.length; i++) {
				Row row = VCML.createRow();
				EList<Literal> values = row.getValues();
				rows.add(row);
				for(int l=0; l<contentEntries[i].length; l++) {
					values.add(contentEntries[i][l]);
				}
			}
		} catch(AbapException exception) {
			handleAbapException(exception);
		}
		return content;
	}
	
	protected VariantTableArgument getArgument(final String name, Iterable<VariantTableArgument> arguments) {
		return Iterables.find(arguments, new Predicate<VariantTableArgument>() {
			public boolean apply(VariantTableArgument argument) {
				return name.equals(argument.getCharacteristic().getName());
			}
		});
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
