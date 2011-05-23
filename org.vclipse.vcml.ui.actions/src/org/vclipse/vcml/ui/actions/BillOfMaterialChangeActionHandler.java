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
package org.vclipse.vcml.ui.actions;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.ecore.resource.Resource;
import org.vclipse.vcml.ui.outline.actions.IVCMLOutlineActionHandler;
import org.vclipse.vcml.vcml.BOMItem;
import org.vclipse.vcml.vcml.BillOfMaterial;
import org.vclipse.vcml.vcml.Material;

import com.sap.conn.jco.AbapException;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoParameterList;
import com.sap.conn.jco.JCoTable;

public class BillOfMaterialChangeActionHandler extends BAPIUtils implements IVCMLOutlineActionHandler<BillOfMaterial>{

	public boolean isEnabled(BillOfMaterial object) {
		return isConnected();
	}

	public void run(BillOfMaterial billOfMaterial, Resource resource, IProgressMonitor monitor) throws JCoException {
		String materialNumber = ((Material)billOfMaterial.eContainer()).getName();
		String plant = getPlant();
		String bomUsage = getBomUsage();

		// TODO compute delta (BOM items are just added to the BOM)
		JCoFunction function = getJCoFunction("CSAP_MAT_BOM_MAINTAIN", monitor);
		JCoParameterList ipl = function.getImportParameterList();
		ipl.setValue("MATERIAL", materialNumber);
		ipl.setValue("PLANT", plant);
		ipl.setValue("BOM_USAGE", bomUsage);
		ipl.setValue("VALID_FROM", "01012010");
		JCoParameterList tpl = function.getTableParameterList();
		JCoTable tSTPO = tpl.getTable("T_STPO");
		for(BOMItem item : billOfMaterial.getItems()) {
			tSTPO.appendRow();
			tSTPO.setValue("ITEM_CATEG", "N");
			tSTPO.setValue("ITEM_NO", item.getItemnumber());
			tSTPO.setValue("COMPONENT", item.getMaterial().getName());
			tSTPO.setValue("COMP_QTY", 1);
			tSTPO.setValue("COMP_UNIT", "PC");
		}
		try {
			execute(function, monitor, materialNumber + " " + plant + " " + bomUsage);
		} catch (AbapException e) {
			handleAbapException(e);
		} 

	}

}