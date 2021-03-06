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
package org.vclipse.bapi.actions.classes;

import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.vclipse.bapi.actions.BAPIUtils;
import org.vclipse.bapi.actions.characteristic.CharacteristicReader;
import org.vclipse.vcml.utils.VcmlUtils;
import org.vclipse.vcml.vcml.Characteristic;
import org.vclipse.vcml.vcml.Class;
import org.vclipse.vcml.vcml.Option;
import org.vclipse.vcml.vcml.VCObject;
import org.vclipse.vcml.vcml.VcmlModel;

import com.google.inject.Inject;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoParameterList;
import com.sap.conn.jco.JCoStructure;
import com.sap.conn.jco.JCoTable;

public class ClassReader extends BAPIUtils {

	@Inject
	private CharacteristicReader csticReader;
	
	public Class read(String classSpec, VcmlModel model, IProgressMonitor monitor, Map<String, VCObject> seenObjects, List<Option> globalOptions, boolean recurse) throws JCoException {
		int classType = VcmlUtils.getClassType(classSpec);
		String className = VcmlUtils.getClassName(classSpec);
		String newClassSpec = "(" + classType + ")" + className;
		String id = "Class/" + newClassSpec.toUpperCase();
		VCObject seenObject = seenObjects.get(id);
		if (seenObject instanceof Class) {
			return (Class)seenObject;
		}
		Class object = VCML.createClass();
		seenObjects.put(id, object);
		object.setName(newClassSpec);
		model.getObjects().add(object);
		{
			JCoFunction function = getJCoFunction("BAPI_CLASS_GETDETAIL", monitor);
			JCoParameterList ipl = function.getImportParameterList();
			
			handleOptions2(object.getOptions(), globalOptions, ipl, null, "KEYDATE");
			
			ipl.setValue("CLASSTYPE", classType);
			ipl.setValue("CLASSNUM", className);
			execute(function, monitor, newClassSpec);
			if (processReturnStructure(function)) {
				JCoStructure classBasicData = function.getExportParameterList().getStructure("CLASSBASICDATA");
				object.setStatus(VcmlUtils.createStatusFromInt(classBasicData.getInt("STATUS")));
				object.setGroup(nullIfEmpty(classBasicData.getString("CLASSGROUP")));
				JCoTable classCharacteristics =
						function.getTableParameterList().getTable("CLASSCHARACTERISTICS");
				if (classCharacteristics.getNumRows()>0) {
					List<Characteristic> characteristics = object.getCharacteristics();
					for (int i = 0; i < classCharacteristics.getNumRows(); i++) {
						classCharacteristics.setRow(i);
						String csticName = classCharacteristics.getString("NAME_CHAR");
						String charInherited = classCharacteristics.getString("CHAR_INHERITED");
						if (!"X".equals(charInherited)) {
							// TODO move this read / proxy mechanism to CharacteristicReader
							Characteristic cstic = null;
							if(recurse) {
								if(monitor.isCanceled()) {
									return null;
								}
								cstic = csticReader.read(csticName, model, monitor, seenObjects, globalOptions, recurse);
							}
							if (cstic==null) {
								cstic = vcmlProxyFactory.characteristicProxy(csticName, model.eResource());
							}
							characteristics.add(cstic);
						}
					}
				}
				object.setDescription(readDescription(function.getTableParameterList().getTable("CLASSDESCRIPTIONS"), "LANGU_ISO", "LANGU", "CATCHWORD"));
			}
		}
		{
			JCoFunction function = getJCoFunction("BAPI_HIERA_GETSUPERCLASSALLOCS", monitor);
			JCoParameterList ipl = function.getImportParameterList();
			ipl.setValue("CLASSTYPE", classType);
			ipl.setValue("CLASSNUM", className);
			try {
				execute(function, monitor, newClassSpec);
				JCoTable classList = function.getTableParameterList().getTable("SUPERCLASSESLIST");
				for (int i = 0; i < classList.getNumRows(); i++) {
					classList.setRow(i);
					String superclassName = classList.getString("CLASSNAME");
					String superclassType = classList.getString("CLASSTYPE");
					String superclassSpec = "(" + superclassType + ")" + superclassName;
					Class superclass = null;
					if(recurse) {
						if(monitor.isCanceled()) {
							return null;
						}
						superclass = read(superclassSpec, model, monitor, seenObjects, globalOptions, recurse);
					}
					if (superclass==null) {
						superclass = vcmlProxyFactory.classProxy(superclassSpec, model.eResource());
					}
					object.getSuperClasses().add(superclass);
				}
			} catch (JCoException ex) {
				ex.printStackTrace();
				// FIXME remove try/catch when permissions in customer's SAP system are correct
			}
		}
		
		return object;
	}

}
