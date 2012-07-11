package org.vclipse.vcml.ui.actions.varianttable.content;

import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.resource.Resource;
import org.vclipse.vcml.ui.actions.BAPIUtils;
import org.vclipse.vcml.ui.outline.actions.IVcmlOutlineActionHandler;
import org.vclipse.vcml.vcml.Characteristic;
import org.vclipse.vcml.vcml.Literal;
import org.vclipse.vcml.vcml.NumericLiteral;
import org.vclipse.vcml.vcml.Option;
import org.vclipse.vcml.vcml.Row;
import org.vclipse.vcml.vcml.SymbolicLiteral;
import org.vclipse.vcml.vcml.VariantTable;
import org.vclipse.vcml.vcml.VariantTableArgument;
import org.vclipse.vcml.vcml.VariantTableContent;

import com.sap.conn.jco.AbapException;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoParameterList;
import com.sap.conn.jco.JCoTable;

public class VariantTableContentCreateActionHandler extends BAPIUtils implements IVcmlOutlineActionHandler<VariantTableContent> {

	@Override
	public boolean isEnabled(VariantTableContent content) {
		return isConnected() && !content.getRows().isEmpty();
	}
	
	@Override
	public void run(VariantTableContent content, Resource resource, IProgressMonitor monitor, Set<String> seenObjects, List<Option> options) throws Exception {
		final String name = content.getTable().getName();
		beginTransaction();
		
		VariantTable table = content.getTable();
		EList<VariantTableArgument> arguments = table.getArguments();
		
		JCoFunction function = getJCoFunction("CAMA_TABLE_MAINTAIN_ENTRIES", monitor);
		JCoParameterList ipl = function.getImportParameterList();
		handleOptions(options, ipl, "CHANGE_NO", "DATE");
		ipl.setValue("VAR_TABLE", table.getName());
		JCoParameterList tpl = function.getTableParameterList();
		JCoTable entries = tpl.getTable("VAR_TAB_ENTRIES");
		EList<Row> rows = content.getRows();
		for(VariantTableArgument argument : arguments) {
			int index = arguments.indexOf(argument);
			Characteristic cstic = argument.getCharacteristic();
			String csticName = cstic.getName();
			for(Row row : rows) {
				entries.appendRow();
				EList<Literal> values = row.getValues();
				Literal literal = values.get(index);
				String value = getValue(literal);
				entries.setValue("VTCHARACT", csticName);
				entries.setValue("VTLINENO", "" + index);
				entries.setValue("VTLINNOINT", rows.indexOf(row));
				entries.setValue("VTVALUE", value);
			}
		}
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
