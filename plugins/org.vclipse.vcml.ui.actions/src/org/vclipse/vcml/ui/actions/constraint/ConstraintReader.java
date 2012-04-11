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
package org.vclipse.vcml.ui.actions.constraint;

import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.ecore.resource.Resource;
import org.vclipse.vcml.vcml.Constraint;
import org.vclipse.vcml.vcml.Model;
import org.vclipse.vcml.ui.actions.BAPIUtils;
import org.vclipse.vcml.utils.VcmlUtils;

import com.sap.conn.jco.AbapException;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoParameterList;
import com.sap.conn.jco.JCoStructure;

public class ConstraintReader extends BAPIUtils {

	public Constraint read(String constraintName, Resource resource, IProgressMonitor monitor, Set<String> seenObjects, boolean recurse) throws JCoException {
		if (!seenObjects.add("Constraint/" + constraintName)) {
			return null;
		}
		Constraint object = VCML.createConstraint();
		object.setName(constraintName);
		Model model = (Model)resource.getContents().get(0);
		model.getObjects().add(object);
		JCoFunction function = getJCoFunction("CARD_CNET_CONSTRAINT_READ", monitor);
		function.getImportParameterList().setValue("CONSTRAINT", constraintName);
		try {
			execute(function, monitor, constraintName);
			JCoParameterList epl = function.getExportParameterList();
			JCoStructure dependencyData = epl.getStructure("BASIC_DATA");
			object.setStatus(VcmlUtils.createStatusFromInt(dependencyData.getInt("STATUS")));
			object.setGroup(nullIfEmpty(dependencyData.getString("GROUP")));
			JCoParameterList tpl = function.getTableParameterList();
			object.setDescription(readDescription(tpl.getTable("DESCRIPTION"), "LANGUAGE_ISO", "LANGUAGE", "DESCRIPT"));
			object.setDocumentation(readMultiLanguageDocumentations(tpl.getTable("DOCUMENTATION")));
			object.setSource(readConstraintSource(tpl.getTable("SOURCE")));
		} catch (AbapException e) {
			handleAbapException(e);
		} 
		return object;
	}

}
