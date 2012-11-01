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
package org.vclipse.bapi.actions.material;

import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.ecore.resource.Resource;
import org.vclipse.bapi.actions.BAPIUtils;
import org.vclipse.bapi.actions.IBAPIActionRunner;
import org.vclipse.vcml.utils.DescriptionHandler;
import org.vclipse.vcml.vcml.Language;
import org.vclipse.vcml.vcml.Material;
import org.vclipse.vcml.vcml.Option;
import org.vclipse.vcml.vcml.VCObject;

import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoParameterList;
import com.sap.conn.jco.JCoStructure;
import com.sap.conn.jco.JCoTable;

public class MaterialCreateChangeActionHandler extends BAPIUtils implements IBAPIActionRunner<Material> {
	
	public boolean isEnabled(Material object) {
		return isConnected() && hasBody(object);
	}
	
	public void run(Material object, Resource resource, IProgressMonitor monitor, Map<String, VCObject> seenObjects, List<Option> globalOptions) throws JCoException {
		beginTransaction();
		JCoFunction function = getJCoFunction("BAPI_MATERIAL_SAVEDATA", monitor);
		JCoParameterList ipl = function.getImportParameterList();
		
		handleOptions(object.getOptions(), globalOptions, ipl, null, null);
		
		JCoStructure headData = ipl.getStructure("HEADDATA");
		headData.setValue("MATERIAL", object.getName());
		headData.setValue("IND_SECTOR", getIndustrySector());
		headData.setValue("MATL_TYPE", object.getType());
		JCoStructure clientData = ipl.getStructure("CLIENTDATA");
		clientData.setValue("BASE_UOM", "ST");
		clientData.setValue("BASE_UOM_ISO", "ST");
		// clientData.setValue("MATL_GROUP", );
		JCoStructure clientDataX = ipl.getStructure("CLIENTDATAX");
		clientDataX.setValue("BASE_UOM", "X");
		clientDataX.setValue("BASE_UOM_ISO", "X");
		// clientData.setValue("MATL_GROUP", "X");
		JCoParameterList tpl = function.getTableParameterList();
		final JCoTable materialDescription = tpl.getTable("MATERIALDESCRIPTION");
		new DescriptionHandler() {
			@Override
			public void handleSingleDescription(Language language, String value) {
				materialDescription.appendRow();
				materialDescription.setValue("LANGU_ISO", language.toString());
				materialDescription.setValue("MATL_DESC", value);
			}
		}.handleDescription(object.getDescription());

		execute(function, monitor, object.getName());
		
		if (processReturnStructure(function))
			commit(monitor);
		endTransaction();
	}
}
