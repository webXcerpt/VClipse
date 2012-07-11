package org.vclipse.vcml.ui.actions.varianttable.content;

import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.ecore.resource.Resource;
import org.vclipse.vcml.ui.actions.BAPIUtils;
import org.vclipse.vcml.ui.outline.actions.IVcmlOutlineActionHandler;
import org.vclipse.vcml.vcml.Option;
import org.vclipse.vcml.vcml.VariantTable;
import org.vclipse.vcml.vcml.VariantTableContent;

import com.sap.conn.jco.AbapException;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoParameterList;
import com.sap.conn.jco.JCoTable;

public class VariantTableContentDeleteActionHandler extends BAPIUtils implements IVcmlOutlineActionHandler<VariantTableContent> {

	@Override
	public boolean isEnabled(VariantTableContent object) {
		return isConnected();
	}
	
	@Override
	public void run(VariantTableContent content, Resource resource, IProgressMonitor monitor, Set<String> seenObjects, List<Option> options) throws Exception {
		VariantTable table = content.getTable();
		String name = table.getName();
		beginTransaction();
		JCoFunction function = getJCoFunction("CAMA_TABLE_MAINTAIN_ENTRIES", monitor);
		JCoParameterList ipl = function.getImportParameterList();
		handleOptions(options, ipl, "CHANGE_NO", "DATE");
		ipl.setValue("VAR_TABLE", table.getName());
		JCoParameterList tpl = function.getTableParameterList();
		JCoTable entries = tpl.getTable("VAR_TAB_ENTRIES");
		try {
			for(int i=0; i<entries.getNumRows(); i++) {
				entries.setRow(i);
				entries.setValue("FLDELETE", "X");			
			}
			execute(function, monitor, "DELETE " + name);
			endTransaction();
		} catch (AbapException e) {
			handleAbapException(e);
		}
	}
}
