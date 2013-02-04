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
package org.vclipse.bapi.actions.procedure;

import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.ecore.resource.Resource;
import org.vclipse.bapi.actions.BAPIUtils;
import org.vclipse.vcml.utils.VcmlUtils;
import org.vclipse.vcml.vcml.Option;
import org.vclipse.vcml.vcml.Procedure;
import org.vclipse.vcml.vcml.ProcedureSource;
import org.vclipse.vcml.vcml.VCObject;
import org.vclipse.vcml.vcml.VcmlModel;

import com.sap.conn.jco.AbapException;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoParameterList;
import com.sap.conn.jco.JCoStructure;

public class ProcedureReader extends BAPIUtils {

	public Procedure read(String procedureName, Resource resource, IProgressMonitor monitor, Map<String, VCObject> seenObjects, List<Option> globalOptions, boolean recurse) throws JCoException {
		if(procedureName == null || monitor.isCanceled()) {
			return null;
		}
		String id = "Procedure/" + procedureName.toUpperCase();
		VCObject seenObject = seenObjects.get(id);
		if (seenObject instanceof Procedure) {
			return (Procedure)seenObject;
		}
		Procedure object = VCML.createProcedure();
		seenObjects.put(id, object);
		object.setName(procedureName);
		VcmlModel vcmlModel = (VcmlModel)resource.getContents().get(0);
		vcmlModel.getObjects().add(object);
		JCoFunction function = getJCoFunction("CARD_DEPENDENCY_READ", monitor);
		JCoParameterList ipl = function.getImportParameterList();
		ipl.setValue("DEPENDENCY", procedureName);
		
		handleOptions(object.getOptions(), globalOptions, ipl, "CHANGE_NO", "DATE");
		
		// if the following flags are not checked, then the function performs just an existence check
		ipl.setValue("FL_WITH_BASIC_DATA", "X");
		ipl.setValue("FL_WITH_DESCRIPTION", "X");
		ipl.setValue("FL_WITH_DOCUMENTATION", "X");
		ipl.setValue("FL_WITH_SOURCE", "X");
		try {
			execute(function, monitor, procedureName);
			JCoStructure dependencyData = function.getExportParameterList().getStructure("DEPENDENCY_DATA");
			String depType = dependencyData.getString("DEP_TYPE");
			if (!"PROC".equals(depType))
				errorStream.println("ERROR: " + procedureName + " is not a procedure - it has dependency type " + depType);
			object.setStatus(VcmlUtils.createStatusFromInt(dependencyData.getInt("STATUS")));
			object.setGroup(nullIfEmpty(dependencyData.getString("GROUP")));
			JCoParameterList tpl = function.getTableParameterList();
			object.setDescription(readDescription(tpl.getTable("DESCRIPTION"), "LANGUAGE_ISO", "LANGUAGE", "DESCRIPT"));
			object.setDocumentation(readMultiLanguageDocumentations(tpl.getTable("DOCUMENTATION")));
			readSource(tpl.getTable("SOURCE"), object);
			
			ProcedureSource procedureSource = sourceUtils.getProcedureSource(object);
			if(procedureSource!=null && recurse) {
				sourceCrossReferenceExtractor.extractFromSource(procedureSource, vcmlModel, monitor, seenObjects, globalOptions);
			}
		} catch (AbapException e) {
			handleAbapException(e);
		}
		vcmlModel.getObjects().add(object);
		return object;
	}

}
