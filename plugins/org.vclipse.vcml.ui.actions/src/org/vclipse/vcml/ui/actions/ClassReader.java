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

import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.ecore.resource.Resource;
import org.vclipse.vcml.vcml.Characteristic;
import org.vclipse.vcml.vcml.Class;
import org.vclipse.vcml.vcml.Model;
import org.vclipse.vcml.utils.VCMLProxyFactory;
import org.vclipse.vcml.utils.VCMLUtils;

import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoParameterList;
import com.sap.conn.jco.JCoStructure;
import com.sap.conn.jco.JCoTable;

public class ClassReader extends BAPIUtils {

	private static final CharacteristicReader CHARACTERISTIC_READER = new CharacteristicReader(); // must not be abstract

	public Class read(String classSpec, Resource resource, IProgressMonitor monitor, Set<String> seenObjects, boolean recurse) throws JCoException {
		if (!seenObjects.add("Class#" + classSpec)) {
			return null;
		}
		Class object = VCML.createClass();
		object.setName(classSpec);
		((Model)resource.getContents().get(0)).getObjects().add(object);
		JCoFunction function = getJCoFunction("BAPI_CLASS_GETDETAIL", monitor);
		JCoParameterList ipl = function.getImportParameterList();
		ipl.setValue("CLASSNUM", VCMLUtils.getClassName(classSpec));
		ipl.setValue("CLASSTYPE", VCMLUtils.getClassType(classSpec));
		execute(function, monitor, classSpec);
		if (processReturnStructure(function)) {
			JCoStructure classBasicData = function.getExportParameterList().getStructure("CLASSBASICDATA");
			object.setStatus(VCMLUtils.createStatusFromInt(classBasicData.getInt("STATUS")));
			object.setGroup(nullIfEmpty(classBasicData.getString("CLASSGROUP")));
			JCoTable classCharacteristics =
				function.getTableParameterList().getTable("CLASSCHARACTERISTICS");
			if (classCharacteristics.getNumRows()>0) {
				List<Characteristic> characteristics = object.getCharacteristics();
				for (int i = 0; i < classCharacteristics.getNumRows(); i++) {
					classCharacteristics.setRow(i);
					String csticName = classCharacteristics.getString("NAME_CHAR");
					// TODO move this read / proxy mechanism to CharacteristicReader
					Characteristic cstic = null;
					if (recurse) {
						cstic = CHARACTERISTIC_READER.read(csticName, resource, monitor, seenObjects, recurse);
					}
					if (cstic==null) {
						cstic = VCMLProxyFactory.createCharacteristicProxy(resource, csticName);
					}
					characteristics.add(cstic);
				}
			}
			object.setDescription(readDescription(function.getTableParameterList().getTable("CLASSDESCRIPTIONS"), "LANGU_ISO", "LANGU", "CATCHWORD"));
		}
		return object;
	}

}
