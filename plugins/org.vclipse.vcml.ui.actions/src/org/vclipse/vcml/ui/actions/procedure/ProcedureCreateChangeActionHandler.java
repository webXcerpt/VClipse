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
package org.vclipse.vcml.ui.actions.procedure;

import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.ecore.resource.Resource;
import org.vclipse.vcml.ui.actions.BAPIUtils;
import org.vclipse.vcml.ui.outline.actions.IVCMLOutlineActionHandler;
import org.vclipse.vcml.vcml.Procedure;
import org.vclipse.vcml.utils.VcmlUtils;

import com.sap.conn.jco.AbapException;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoParameterList;
import com.sap.conn.jco.JCoStructure;

public class ProcedureCreateChangeActionHandler extends BAPIUtils implements IVCMLOutlineActionHandler<Procedure> {

	public boolean isEnabled(Procedure object) {
		return isConnected();
	}

	public void run(Procedure object, Resource resource, IProgressMonitor monitor, Set<String> seenObjects) throws JCoException {
		beginTransaction();
		JCoFunction function = getJCoFunction("CAMA_DEPENDENCY_MAINTAIN", monitor);
		JCoParameterList ipl = function.getImportParameterList();
		ipl.setValue("DEPENDENCY", object.getName());
		JCoStructure dependencyData = ipl.getStructure("DEPENDENCY_DATA");
		dependencyData.setValue("DEP_TYPE", "PROC");
		dependencyData.setValue("STATUS", VcmlUtils.createIntFromStatus(object.getStatus()));
		dependencyData.setValue("GROUP", object.getGroup());
		JCoParameterList tpl = function.getTableParameterList();
		writeDescription(tpl.getTable("DESCRIPTION"), object.getDescription());
		writeDocumentation(tpl.getTable("DOCUMENTATION"), object.getDocumentation());
		writeSourceCode(tpl.getTable("SOURCE"), object.getSource());
		try {
			execute(function, monitor, object.getName());
			endTransaction();
		} catch (AbapException e) {
			handleAbapException(e);
		}
	}
	
}
