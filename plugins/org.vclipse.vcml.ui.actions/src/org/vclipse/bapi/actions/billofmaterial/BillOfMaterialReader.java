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
package org.vclipse.bapi.actions.billofmaterial;

import static org.vclipse.vcml.utils.VCMLObjectUtils.mkConfigurationProfileEntry;
import static org.vclipse.vcml.utils.VCMLObjectUtils.sortEntries;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.xtext.util.Strings;
import org.vclipse.bapi.actions.BAPIActionPlugin;
import org.vclipse.bapi.actions.BAPIUtils;
import org.vclipse.bapi.actions.classes.ClassReader;
import org.vclipse.bapi.actions.material.MaterialReader;
import org.vclipse.bapi.actions.preferences.PreferenceNames;
import org.vclipse.bapi.actions.procedure.ProcedureReader;
import org.vclipse.bapi.actions.selectioncondition.SelectionConditionReader;
import org.vclipse.vcml.vcml.BOMItem;
import org.vclipse.vcml.vcml.BOMItem_Class;
import org.vclipse.vcml.vcml.BOMItem_Material;
import org.vclipse.vcml.vcml.BillOfMaterial;
import org.vclipse.vcml.vcml.Material;
import org.vclipse.vcml.vcml.Option;
import org.vclipse.vcml.vcml.Procedure;
import org.vclipse.vcml.vcml.SelectionCondition;
import org.vclipse.vcml.vcml.VCObject;
import org.vclipse.vcml.vcml.VcmlModel;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.sap.conn.jco.AbapException;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoParameterList;
import com.sap.conn.jco.JCoStructure;
import com.sap.conn.jco.JCoTable;

public class BillOfMaterialReader extends BAPIUtils {

	@Inject
	private MaterialReader materialReader;

	@Inject
	private ProcedureReader procedureReader;
	
	@Inject
	private ClassReader classReader;
	
	@Inject
	private SelectionConditionReader selectionConditionReader;
	
	@Inject
	@Named(BAPIActionPlugin.ID)
	private IPreferenceStore preferenceStore;

	public void read(Material containerMaterial, Resource resource, IProgressMonitor monitor, Map<String, VCObject> seenObjects, List<Option> globalOptions, boolean recurse) throws JCoException {
		String materialNumber = containerMaterial.getName();
		if(materialNumber == null || monitor.isCanceled()) {
			return;
		}
		String id = "BillOfMaterial/" + materialNumber.toUpperCase();
		VCObject seenObject = seenObjects.get(id);
		if (seenObject instanceof BillOfMaterial) {
			return;
		}
		VcmlModel model = (VcmlModel)resource.getContents().get(0);
		JCoFunction function = getJCoFunction("CSAP_MAT_BOM_READ", monitor);
		JCoParameterList ipl = function.getImportParameterList();
		String plant = getPlant();
		String bomUsage = getBomUsage();
		
		handleOptions(containerMaterial.getOptions(), globalOptions, ipl, "CHANGE_NO", "VALID_FROM");
		
		ipl.setValue("MATERIAL", materialNumber);
		ipl.setValue("PLANT", plant); // TODO move plant and usage to VCML language? Perhaps features in the language could also overwrite preferences settings.
		ipl.setValue("BOM_USAGE", bomUsage);
		try {
			execute(function, monitor, materialNumber + " " + plant + " " + bomUsage);

			JCoParameterList tpl = function.getTableParameterList();
			
			JCoTable tSTKO = tpl.getTable("T_STKO"); // BOM headers
			Map<String, BillOfMaterial> billOfMaterialByName = new HashMap<String, BillOfMaterial>();
			for (int i = 0; i < tSTKO.getNumRows(); i++) {
				tSTKO.setRow(i);
				String bomNo = tSTKO.getString("BOM_NO");
				BillOfMaterial object = VCML.createBillOfMaterial();
				object.setName(materialNumber);
				containerMaterial.getBillofmaterials().add(object);
				model.getObjects().add(object);
				seenObjects.put(id, object);
				billOfMaterialByName.put(bomNo, object);
				// object.setStatus(VCMLUtils.createStatus(tSTKO.getInt("BOM_STATUS"))); // TODO BOM should have a status
			}

			JCoTable tSTPO = tpl.getTable("T_STPO"); // BOM items
			JCoTable tT_DEP_DATA = tpl.getTable("T_DEP_DATA"); // Object dependencies: basic data
			JCoTable tT_DEP_ORDER = tpl.getTable("T_DEP_ORDER"); // Object dependencies: sort sequence
			
			for (int i = 0; i < tSTPO.getNumRows(); i++) {
				tSTPO.setRow(i);
				String bomNo = tSTPO.getString("BOM_NO");
				BillOfMaterial bom = billOfMaterialByName.get(bomNo);
				List<BOMItem> bomItems = bom.getItems();
				String component = tSTPO.getString("COMPONENT");
				if (!Strings.isEmpty(component)) {
					BOMItem_Material bomItem = VCML.createBOMItem_Material();
					bomItems.add(bomItem);
					bomItem.setItemnumber(tSTPO.getInt("ITEM_NO"));
					Material bomMaterial = null;
					if (recurse) {
						if(monitor.isCanceled()) {
							return;
						}
						bomMaterial = materialReader.read(component, resource, monitor, seenObjects, globalOptions, recurse);
					}
					if (bomMaterial==null) {
						bomMaterial = vcmlProxyFactory.materialProxy(component, resource);
					}
					bomItem.setMaterial(bomMaterial);
					readDependencies(bomItem, resource, monitor, seenObjects,
							globalOptions, recurse, tSTPO, tT_DEP_DATA,
							tT_DEP_ORDER);
					
				} else {
					String className = tSTPO.getString("CLASS");
					if (!Strings.isEmpty(className)) {
						String classType = tSTPO.getString("CLASS_TYPE");
						String classSpec = "(" + classType + ")" + className;
						BOMItem_Class bomItem = VCML.createBOMItem_Class();
						bomItems.add(bomItem);
						bomItem.setItemnumber(tSTPO.getInt("ITEM_NO"));
						org.vclipse.vcml.vcml.Class cls = null;
						if (recurse) {
							if(monitor.isCanceled()) {
								return;
							}
							cls = classReader.read(classSpec, model, monitor, seenObjects, globalOptions, recurse);
						}
						if (cls==null) {
							cls = vcmlProxyFactory.classProxy(classSpec, resource);
						}
						bomItem.setCls(cls);
						
						if (preferenceStore.getBoolean(PreferenceNames.CLASSNODES_MATERIALS)) {
							JCoFunction classificationFunction = getJCoFunction("BAPI_OBJCL_GETOBJECTS", monitor);
							JCoParameterList classificationIpl = classificationFunction.getImportParameterList();
							classificationIpl.setValue("CLASSTYPE", classType);
							JCoParameterList classificationTpl = classificationFunction.getTableParameterList();
							JCoTable classNumRange = classificationTpl.getTable("CLASSNUMRANGE");
							classNumRange.appendRow();
							classNumRange.setValue("SIGN", "I");
							classNumRange.setValue("OPTION", "EQ");
							classNumRange.setValue("LOW", className);
							execute(classificationFunction, monitor, classSpec);
							JCoTable allocList = classificationTpl.getTable("ALLOCLIST");
							for (int j= 0; j < allocList.getNumRows(); j++) {
								allocList.setRow(j);
								if ("MARA".equals(allocList.getString("OBJTYP"))) {
									String classifiedMaterialName = allocList.getString("OBJECT");
									Material classifiedMaterial = null;
									if (recurse) {
										if(monitor.isCanceled()) {
											return;
										}
										classifiedMaterial = materialReader.read(classifiedMaterialName, resource, monitor, seenObjects, globalOptions, recurse);
									}
									if (classifiedMaterial==null) {
										classifiedMaterial = vcmlProxyFactory.materialProxy(component, resource);
									}
								}
							}
						}

						readDependencies(bomItem, resource, monitor, seenObjects,
								globalOptions, recurse, tSTPO, tT_DEP_DATA,
								tT_DEP_ORDER);
					} else {
						System.err.println("BOM item with emtpy COMPONENT or CLASS for material " + materialNumber + ":\n"
								+ tSTPO.getValue("ITEM_CATEG") + "\t"
								+ tSTPO.getValue("ITEM_NO") + "\t"
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

	private void readDependencies(BOMItem bomItem, Resource resource,
			IProgressMonitor monitor, Map<String, VCObject> seenObjects,
			List<Option> globalOptions, boolean recurse, JCoTable tSTPO,
			JCoTable tT_DEP_DATA, JCoTable tT_DEP_ORDER) throws JCoException {
		String itemNode = tSTPO.getString("ITEM_NODE");
		for (int j = 0; j < tT_DEP_DATA.getNumRows(); j++) {
			tT_DEP_DATA.setRow(j);
			if (Strings.equal(itemNode, tT_DEP_DATA.getString("ITEM_NODE"))) {
				String depType = tT_DEP_DATA.getString("DEP_TYPE");
				String depName = tT_DEP_DATA.getString("DEP_INTERN");
				if ("7".equals(depType)) {
					Procedure proc = null;
					if (recurse) {
						if(monitor.isCanceled()) {
							return;
						}
						proc = procedureReader.read(depName, resource, monitor, seenObjects, globalOptions, recurse);
					}
					if (proc==null) {
						proc = vcmlProxyFactory.procedureProxy(depName, resource);
					}
					int seq = getSequenceNumber(tT_DEP_ORDER, itemNode, depName);
					bomItem.getEntries().add(mkConfigurationProfileEntry(seq, proc));
				} else if ("5".equals(depType)) {
					SelectionCondition cond = null;
					if (recurse) {
						if(monitor.isCanceled()) {
							return;
						}
						cond = selectionConditionReader.read(depName, resource, monitor, seenObjects, globalOptions, recurse);
					}
					if (cond==null) {
						cond = vcmlProxyFactory.selectionConditionProxy(depName, resource);
					}
					bomItem.setSelectionCondition(cond);
				} else {
					throw new IllegalArgumentException("unknown dependency type in BOM: " + depName + " of type " + depType);
				}
				
			}
		}
		sortEntries(bomItem.getEntries());
	}

	private int getSequenceNumber(JCoTable tOrder, String itemIdent, String depName) {
		for (int i = 0; i < tOrder.getNumRows(); i++) {
			tOrder.setRow(i);
			if (Strings.equal(itemIdent,tOrder.getString("ITEM_NODE")) && 
				Strings.equal(depName, tOrder.getString("DEP_INTERN"))) {
				return tOrder.getInt("DEP_LINENO");
			}
		}
		return 0;
	}

}
