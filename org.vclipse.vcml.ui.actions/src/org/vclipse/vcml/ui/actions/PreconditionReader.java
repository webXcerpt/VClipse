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

import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.ecore.resource.Resource;
import org.vclipse.vcml.vcml.Model;
import org.vclipse.vcml.vcml.Precondition;
import org.vclipse.vcml.utils.VCMLUtils;

import com.sap.conn.jco.AbapException;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoParameterList;
import com.sap.conn.jco.JCoStructure;

public class PreconditionReader extends BAPIUtils {

	public Precondition read(String preconditionName, Resource resource, IProgressMonitor monitor, Set<String> seenObjects, boolean recurse) throws JCoException {
		if (!seenObjects.add("Precondition#" + preconditionName))
			return null;
		Precondition object = VCML.createPrecondition();
		object.setName(preconditionName);
		((Model)resource.getContents().get(0)).getObjects().add(object);
		JCoFunction function = getJCoFunction("CARD_DEPENDENCY_READ", monitor);
		JCoParameterList ipl = function.getImportParameterList();
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
				err.println("ERROR: " + preconditionName + " is not a precondition - it has dependency type " + depType);
			object.setStatus(VCMLUtils.createStatusFromInt(dependencyData.getInt("STATUS")));
			object.setGroup(nullIfEmpty(dependencyData.getString("GROUP")));
			JCoParameterList tpl = function.getTableParameterList();
			object.setDescription(readDescription(tpl.getTable("DESCRIPTION"), "LANGUAGE_ISO", "LANGUAGE", "DESCRIPT"));
			object.setDocumentation(readMultiLanguageDocumentations(tpl.getTable("DOCUMENTATION")));
			object.setSource(readConditionSource(tpl.getTable("SOURCE")));
		} catch (AbapException e) {
			handleAbapException(e);
		}
		return object;
	}

}
