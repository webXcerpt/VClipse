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
package org.vclipse.bapi.actions.constraint;

import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.ecore.resource.Resource;
import org.vclipse.bapi.actions.BAPIUtils;
import org.vclipse.vcml.utils.VcmlUtils;
import org.vclipse.vcml.vcml.Constraint;
import org.vclipse.vcml.vcml.ConstraintSource;
import org.vclipse.vcml.vcml.Option;
import org.vclipse.vcml.vcml.VcmlModel;

import com.sap.conn.jco.AbapException;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoParameterList;
import com.sap.conn.jco.JCoStructure;

public class ConstraintReader extends BAPIUtils {

	public Constraint read(String constraintName, Resource resource, IProgressMonitor monitor, Set<String> seenObjects, List<Option> options, boolean recurse) throws JCoException {
		if(constraintName == null || !seenObjects.add("Constraint/" + constraintName.toUpperCase()) || monitor.isCanceled()) {
			return null;
		}
		Constraint object = VCML.createConstraint();
		object.setName(constraintName);
		VcmlModel model = (VcmlModel)resource.getContents().get(0);
		model.getObjects().add(object);
		JCoFunction function = getJCoFunction("CARD_CNET_CONSTRAINT_READ", monitor);
		JCoParameterList ipl = function.getImportParameterList();
		ipl.setValue("CONSTRAINT", constraintName);
		
		handleOptions(options, ipl, "CHANGE_NO", "DATE");
		
		try {
			execute(function, monitor, constraintName);
			JCoParameterList epl = function.getExportParameterList();
			JCoStructure dependencyData = epl.getStructure("BASIC_DATA");
			object.setStatus(VcmlUtils.createStatusFromInt(dependencyData.getInt("STATUS")));
			object.setGroup(nullIfEmpty(dependencyData.getString("GROUP")));
			JCoParameterList tpl = function.getTableParameterList();
			object.setDescription(readDescription(tpl.getTable("DESCRIPTION"), "LANGUAGE_ISO", "LANGUAGE", "DESCRIPT"));
			object.setDocumentation(readMultiLanguageDocumentations(tpl.getTable("DOCUMENTATION")));
			readSource(tpl.getTable("SOURCE"), object);
			
			ConstraintSource constraintSource = sourceUtils.getConstraintSource(object);
			if(constraintSource!=null) {
				sapProxyResolver.extractFromSource(constraintSource, model, monitor, seenObjects, options);
			}
		} catch (AbapException e) {
			handleAbapException(e);
		} 
		return object;
	}

}
