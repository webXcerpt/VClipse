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
package org.vclipse.bapi.actions.precondition;

import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.ecore.resource.Resource;
import org.vclipse.bapi.actions.BAPIUtils;
import org.vclipse.vcml.utils.VcmlUtils;
import org.vclipse.vcml.vcml.ConditionSource;
import org.vclipse.vcml.vcml.Option;
import org.vclipse.vcml.vcml.Precondition;
import org.vclipse.vcml.vcml.VCObject;
import org.vclipse.vcml.vcml.VcmlModel;

import com.sap.conn.jco.AbapException;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoParameterList;
import com.sap.conn.jco.JCoStructure;

public class PreconditionReader extends BAPIUtils {
	
	public Precondition read(String preconditionName, Resource resource, IProgressMonitor monitor, Map<String, VCObject> seenObjects, List<Option> options, boolean recurse) throws JCoException {
		if(preconditionName == null || monitor.isCanceled()) {
			return null;
		}
		String id = "Precondition/" + preconditionName.toUpperCase();
		VCObject seenObject = seenObjects.get(id);
		if (seenObject instanceof Precondition) {
			return (Precondition)seenObject;
		}
		Precondition object = VCML.createPrecondition();
		seenObjects.put(id, object);
		object.setName(preconditionName);
		VcmlModel model = (VcmlModel)resource.getContents().get(0);
		model.getObjects().add(object);
		JCoFunction function = getJCoFunction("CARD_DEPENDENCY_READ", monitor);
		JCoParameterList ipl = function.getImportParameterList();
		
		handleOptions(options, ipl, "CHANGE_NO", "DATE");
		
		ipl.setValue("DEPENDENCY", preconditionName);
		// if the following flags are not checked, then the function performs just an existence check
		ipl.setValue("FL_WITH_BASIC_DATA", "X");
		ipl.setValue("FL_WITH_DESCRIPTION", "X");
		ipl.setValue("FL_WITH_DOCUMENTATION", "X");
		ipl.setValue("FL_WITH_SOURCE", "X");
		try {
			execute(function, monitor, preconditionName);
			JCoStructure dependencyData = function.getExportParameterList().getStructure("DEPENDENCY_DATA");
			String depType = dependencyData.getString("DEP_TYPE");
			if (!"PRE".equals(depType))
				errorStream.println("ERROR: " + preconditionName + " is not a precondition - it has dependency type " + depType);
			object.setStatus(VcmlUtils.createStatusFromInt(dependencyData.getInt("STATUS")));
			object.setGroup(nullIfEmpty(dependencyData.getString("GROUP")));
			JCoParameterList tpl = function.getTableParameterList();
			object.setDescription(readDescription(tpl.getTable("DESCRIPTION"), "LANGUAGE_ISO", "LANGUAGE", "DESCRIPT"));
			object.setDocumentation(readMultiLanguageDocumentations(tpl.getTable("DOCUMENTATION")));
			readSource(tpl.getTable("SOURCE"), object);
			
			ConditionSource conditionSource = sourceUtils.getPreconditionSource(object);
			if(conditionSource!=null && recurse) {
				sourceCrossReferenceExtractor.extractFromSource(conditionSource, model, monitor, seenObjects, options);
			}
		} catch (AbapException e) {
			handleAbapException(e);
		}
		return object;
	}

}
