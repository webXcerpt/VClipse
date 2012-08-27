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
import org.vclipse.bapi.actions.IBAPIActionRunner;
import org.vclipse.vcml.utils.VcmlUtils;
import org.vclipse.vcml.vcml.Option;
import org.vclipse.vcml.vcml.Precondition;
import org.vclipse.vcml.vcml.VCObject;

import com.sap.conn.jco.AbapException;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoParameterList;
import com.sap.conn.jco.JCoStructure;

public class PreconditionCreateChangeActionHandler extends BAPIUtils implements IBAPIActionRunner<Precondition> {

	public boolean isEnabled(Precondition object) {
		return isConnected() && hasBody(object);
	}

	public void run(Precondition object, Resource resource, IProgressMonitor monitor, Map<String, VCObject> seenObjects, List<Option> options) throws JCoException {
		beginTransaction();
		JCoFunction function = getJCoFunction("CAMA_DEPENDENCY_MAINTAIN", monitor);
		JCoParameterList ipl = function.getImportParameterList();
		ipl.setValue("DEPENDENCY", object.getName());
		
		handleOptions(options, ipl, "CHANGE_NO", null);
		
		JCoStructure dependencyData = ipl.getStructure("DEPENDENCY_DATA");
		dependencyData.setValue("DEP_TYPE", "PRE");
		dependencyData.setValue("STATUS", VcmlUtils.createIntFromStatus(object.getStatus()));
		dependencyData.setValue("GROUP", object.getGroup());
		JCoParameterList tpl = function.getTableParameterList();
		writeDescription(tpl.getTable("DESCRIPTION"), object.getDescription());
		writeDocumentation(tpl.getTable("DOCUMENTATION"), object.getDocumentation());
		writeSource(tpl.getTable("SOURCE"), object);
		try {
			execute(function, monitor, object.getName());
			endTransaction();
		} catch (AbapException e) {
			handleAbapException(e);
		}
	}
}
