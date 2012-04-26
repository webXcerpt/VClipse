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
package org.vclipse.vcml.ui.actions.billofmaterial;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.util.Strings;
import org.vclipse.vcml.vcml.BOMItem;
import org.vclipse.vcml.vcml.BillOfMaterial;
import org.vclipse.vcml.vcml.Material;
import org.vclipse.vcml.ui.actions.BAPIUtils;
import org.vclipse.vcml.ui.actions.material.MaterialReader;
import org.vclipse.vcml.utils.VCMLProxyFactory;

import com.google.inject.Inject;
import com.sap.conn.jco.AbapException;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoParameterList;
import com.sap.conn.jco.JCoTable;

public class BillOfMaterialReader extends BAPIUtils {

	private final Logger log = Logger.getLogger(BillOfMaterialReader.class);

	@Inject
	private MaterialReader materialReader;
	
	public void read(Material containerMaterial, Resource resource, IProgressMonitor monitor, Set<String> seenObjects, boolean recurse) throws JCoException {
		if(monitor.isCanceled()) {
			return;
		}
		String materialNumber = containerMaterial.getName();
		if (!seenObjects.add("BillOfMaterial/" + materialNumber)) {
			return;
		}
		JCoFunction function = getJCoFunction("CSAP_MAT_BOM_READ", monitor);
		JCoParameterList ipl = function.getImportParameterList();
		String plant = getPlant();
		String bomUsage = getBomUsage();
		ipl.setValue("MATERIAL", materialNumber);
		ipl.setValue("PLANT", plant); // TODO move plant and usage to VCML language? Perhaps features in the language could also overwrite preferences settings.
		ipl.setValue("BOM_USAGE", bomUsage);
		try {
			execute(function, monitor, materialNumber + " " + plant + " " + bomUsage);

			JCoParameterList tpl = function.getTableParameterList();
			
			JCoTable tSTKO = tpl.getTable("T_STKO"); // "St�cklisten-Kopf"
			Map<String, BillOfMaterial> billOfMaterialByName = new HashMap<String, BillOfMaterial>();
			for (int i = 0; i < tSTKO.getNumRows(); i++) {
				tSTKO.setRow(i);
				String bomNo = tSTKO.getString("BOM_NO");
				BillOfMaterial object = VCML.createBillOfMaterial();
				containerMaterial.getBillofmaterials().add(object);
				billOfMaterialByName.put(bomNo, object);
				// object.setStatus(VCMLUtils.createStatus(tSTKO.getInt("BOM_STATUS"))); // TODO BOM should have a status
			}

			JCoTable tSTPO = tpl.getTable("T_STPO"); // "St�cklisten-Position"
			for (int i = 0; i < tSTPO.getNumRows(); i++) {
				tSTPO.setRow(i);
				String bomNo = tSTPO.getString("BOM_NO");
				BillOfMaterial bom = billOfMaterialByName.get(bomNo);
				List<BOMItem> bomItems = bom.getItems();
				// String itemNode = tSTPO.getString("ITEM_NODE");
				BOMItem bomItem = VCML.createBOMItem();
				bomItems.add(bomItem);
				bomItem.setItemnumber(tSTPO.getInt("ITEM_NO"));
				String component = tSTPO.getString("COMPONENT");
				if (!Strings.isEmpty(component)) {
					Material bomMaterial = null;
					if (recurse) {
						if(monitor.isCanceled()) {
							return;
						}
						bomMaterial = materialReader.read(component, resource, monitor, seenObjects, recurse);
					}
					if (bomMaterial==null) {
						bomMaterial = VCMLProxyFactory.createMaterialProxy(resource, component);
					}
					bomItem.setMaterial(bomMaterial);
				} else {
					if (log.isTraceEnabled()) {
						log.trace("BOM item with emtpy COMPONENT for material " + materialNumber + ":\n"
								+ tSTPO.getValue("ITEM_CATEG") + "\t"
								+ tSTPO.getValue("ITEM_NO") + "\t"
								+ tSTPO.getValue("COMPONENT") + "\t"
								+ tSTPO.getValue("COMP_QTY") + "\t"
								+ tSTPO.getValue("COMP_UNIT") + "\t"
								+ tSTPO.getValue("SORTSTRING"));
					}
				}
			}
		} catch (AbapException e) {
			handleAbapException(e);
		} 
	}

}
