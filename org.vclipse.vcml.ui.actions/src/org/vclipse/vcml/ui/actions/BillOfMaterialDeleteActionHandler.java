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
import org.vclipse.vcml.vcml.BillOfMaterial;
import org.vclipse.vcml.vcml.Material;

import com.sap.conn.jco.AbapException;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoParameterList;

public class BillOfMaterialDeleteActionHandler extends BillOfMaterialReader implements IVCMLOutlineActionHandler<BillOfMaterial>{

	public boolean isEnabled(BillOfMaterial object) {
		return isConnected();
	}

	public void run(BillOfMaterial billOfMaterial, Resource resource, IProgressMonitor monitor) throws JCoException {
		JCoFunction function = getJCoFunction("CSAP_MAT_BOM_DELETE", monitor);
		JCoParameterList ipl = function.getImportParameterList();
		String materialNumber = ((Material)billOfMaterial.eContainer()).getName();
		String plant = getPlant();
		String bomUsage = getBomUsage();
		ipl.setValue("MATERIAL", materialNumber);
		ipl.setValue("PLANT", plant);
		ipl.setValue("BOM_USAGE", bomUsage);
		try {
			execute(function, monitor, materialNumber + " " + plant + " " + bomUsage);
		} catch (AbapException e) {
			handleAbapException(e);
		} 
	}

}
