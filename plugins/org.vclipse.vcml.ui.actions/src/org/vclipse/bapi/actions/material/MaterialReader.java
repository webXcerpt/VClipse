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
package org.vclipse.bapi.actions.material;


import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.jface.preference.IPreferenceStore;
import org.vclipse.bapi.actions.BAPIActionPlugin;
import org.vclipse.bapi.actions.BAPIUtils;
import org.vclipse.bapi.actions.billofmaterial.BillOfMaterialReader;
import org.vclipse.bapi.actions.classes.ClassReader;
import org.vclipse.bapi.actions.configurationprofile.ConfigurationProfileReader;
import org.vclipse.bapi.actions.preferences.PreferenceNames;
import org.vclipse.vcml.vcml.Class;
import org.vclipse.vcml.vcml.Classification;
import org.vclipse.vcml.vcml.Material;
import org.vclipse.vcml.vcml.Option;
import org.vclipse.vcml.vcml.SimpleDescription;
import org.vclipse.vcml.vcml.VCObject;
import org.vclipse.vcml.vcml.VcmlModel;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoParameterList;
import com.sap.conn.jco.JCoStructure;
import com.sap.conn.jco.JCoTable;

public class MaterialReader extends BAPIUtils {

	@Inject
	private ConfigurationProfileReader configurationProfileReader;
	
	@Inject
	private ClassReader classReader;
	
	@Inject
	private BillOfMaterialReader bomReader;

	@Inject
	@Named(BAPIActionPlugin.ID)
	private IPreferenceStore preferenceStore;

	public Material read(String materialName, Resource resource, IProgressMonitor monitor, Map<String, VCObject> seenObjects, List<Option> globalOptions, boolean recurse) throws JCoException {
		if(materialName == null || monitor.isCanceled() ) {
			return null;
		}
		String id = "Material/" + materialName.toUpperCase();
		VCObject seenObject = seenObjects.get(id);
		if (seenObject instanceof Material) {
			return (Material)seenObject;
		}
		Material object = VCML.createMaterial();
		seenObjects.put(id, object);
		object.setName(materialName);
		VcmlModel model = (VcmlModel)resource.getContents().get(0);
		model.getObjects().add(object);
		JCoFunction function = getJCoFunction("BAPI_MATERIAL_GET_DETAIL", monitor);
		JCoParameterList ipl = function.getImportParameterList();
		
		handleOptions(object.getOptions(), globalOptions, ipl, null, null);
		
		ipl.setValue("MATERIAL", materialName);
		ipl.setValue("PLANT", getPlant());
		execute(function, monitor, materialName);
		JCoStructure returnStructure = function.getExportParameterList().getStructure("RETURN");
		String type = returnStructure.getString("TYPE");
		if ("S".equals(type)) { // S: SUCCESS
			JCoParameterList epl = function.getExportParameterList();
			JCoStructure materialGeneralData = epl.getStructure("MATERIAL_GENERAL_DATA");
			// TODO how to read multi-language descriptions for materials?
			SimpleDescription description = VCML.createSimpleDescription(); 
			description.setValue(materialGeneralData.getString("MATL_DESC"));
			object.setDescription(description);
			object.setType(materialGeneralData.getString("MATL_TYPE"));
		}
		
		for (String classType : preferenceStore.getString(PreferenceNames.MAT_CLASSTYPES).split("\\s+")) {
			readClassesForMaterial(materialName, classType, resource, monitor,	seenObjects, globalOptions, recurse, object, model);
			if(monitor.isCanceled()) {
				return null;
			}
		}
		configurationProfileReader.read(object, null /* all profiles */, resource, monitor, seenObjects, globalOptions, recurse);

		if(monitor.isCanceled()) {
			return null;
		}
		// BAPI_MAT_BOM_EXISTENCE_CHECK
		bomReader.read(object, resource, monitor, seenObjects, globalOptions, recurse);

		return object;
	}

	private void readClassesForMaterial(String materialName, String classType,
			Resource resource, IProgressMonitor monitor,
			Map<String, VCObject> seenObjects, List<Option> globalOptions,
			boolean recurse, Material object, VcmlModel model)
			throws JCoException {
		JCoFunction functionGetClasses = getJCoFunction("BAPI_OBJCL_GETCLASSES", monitor);
		JCoParameterList iplGetClasses = functionGetClasses.getImportParameterList();
		iplGetClasses.setValue("CLASSTYPE_IMP", classType);
		iplGetClasses.setValue("OBJECTKEY_IMP", materialName);
		iplGetClasses.setValue("OBJECTTABLE_IMP", "MARA");
		execute(functionGetClasses, monitor, materialName + " " + classType);
		if (processReturnTable(functionGetClasses)) {
			JCoTable allocList = functionGetClasses.getTableParameterList().getTable("ALLOCLIST");
			if (allocList.getNumRows()>0) {
				List<Classification> classifications = object.getClassifications();
				for (int i = 0; i < allocList.getNumRows(); i++) {
					allocList.setRow(i);
					String className = "(" + allocList.getInt("CLASSTYPE") + ")" + allocList.getString("CLASSNUM");
					Class cls = null;
					if (recurse) {
						if(monitor.isCanceled()) {
							return;
						}
						cls = classReader.read(className, model, monitor, seenObjects, globalOptions, recurse);
					}
					if (cls==null) {
						cls = vcmlProxyFactory.classProxy(className, resource);
					}
					Classification classification = VCML.createClassification();
					classification.setCls(cls);
					classifications.add(classification);
				}
			}
		}
	}
	
}
