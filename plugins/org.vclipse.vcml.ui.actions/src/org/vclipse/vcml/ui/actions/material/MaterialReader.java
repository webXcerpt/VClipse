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
package org.vclipse.vcml.ui.actions.material;


import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.ecore.resource.Resource;
import org.vclipse.vcml.vcml.Class;
import org.vclipse.vcml.vcml.Classification;
import org.vclipse.vcml.vcml.Material;
import org.vclipse.vcml.vcml.Model;
import org.vclipse.vcml.vcml.Option;
import org.vclipse.vcml.vcml.SimpleDescription;
import org.vclipse.vcml.ui.actions.BAPIUtils;
import org.vclipse.vcml.ui.actions.billofmaterial.BillOfMaterialReader;
import org.vclipse.vcml.ui.actions.classes.ClassReader;
import org.vclipse.vcml.ui.actions.configurationprofile.ConfigurationProfileReader;
import org.vclipse.vcml.utils.VCMLProxyFactory;

import com.google.inject.Inject;
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
	
	public Material read(String materialName, Resource resource, IProgressMonitor monitor, Set<String> seenObjects, List<Option> options, boolean recurse) throws JCoException {
		if(materialName == null || !seenObjects.add("Material/" + materialName.toUpperCase()) || monitor.isCanceled() ) {
			return null;
		}
		Material object = VCML.createMaterial();
		object.setName(materialName);
		Model model = (Model)resource.getContents().get(0);
		model.getObjects().add(object);
		JCoFunction function = getJCoFunction("BAPI_MATERIAL_GET_DETAIL", monitor);
		JCoParameterList ipl = function.getImportParameterList();
		
		handleOptions(options, ipl, null, null);
		
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
		if(monitor.isCanceled()) {
			return null;
		}
		configurationProfileReader.readAll(object, resource, monitor, seenObjects, options, recurse);

		if(monitor.isCanceled()) {
			return null;
		}
		// BAPI_MAT_BOM_EXISTENCE_CHECK
		bomReader.read(object, resource, monitor, seenObjects, options, recurse);

		JCoFunction functionGetClasses = getJCoFunction("BAPI_OBJCL_GETCLASSES", monitor); // BAPI_OBJCL_GET_KEY_OF_OBJECT
		JCoParameterList iplGetClasses = functionGetClasses.getImportParameterList();
		iplGetClasses.setValue("CLASSTYPE_IMP", 300); // TODO what about class type 200?
		iplGetClasses.setValue("OBJECTKEY_IMP", materialName);
		iplGetClasses.setValue("OBJECTTABLE_IMP", "MARA");
		execute(functionGetClasses, monitor, materialName + " 300");
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
							return null;
						}
						cls = classReader.read(className, model, monitor, seenObjects, options, recurse);
					}
					if (cls==null) {
						cls = VCMLProxyFactory.createClassProxy(resource, className);
					}
					Classification classification = VCML.createClassification();
					classification.setCls(cls);
					classifications.add(classification);
				}
			}
		}
		return object;
	}
	
}
