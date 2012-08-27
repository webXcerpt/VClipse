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
package org.vclipse.bapi.actions.dependencynet;

import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.ecore.resource.Resource;
import org.vclipse.bapi.actions.BAPIUtils;
import org.vclipse.bapi.actions.IBAPIActionRunner;
import org.vclipse.vcml.utils.VcmlUtils;
import org.vclipse.vcml.vcml.DependencyNet;
import org.vclipse.vcml.vcml.Option;
import org.vclipse.vcml.vcml.VCObject;

import com.sap.conn.jco.AbapException;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoParameterList;
import com.sap.conn.jco.JCoStructure;

public class DependencyNetCreateChangeActionHandler extends BAPIUtils implements IBAPIActionRunner<DependencyNet> {

	public boolean isEnabled(DependencyNet object) {
		return isConnected() && hasBody(object);
	}

	public void run(DependencyNet object, Resource resource, IProgressMonitor monitor, Map<String, VCObject> seenObjects, List<Option> options) throws JCoException {
		JCoFunction function = getJCoFunction("CAMA_CONSTRAINT_NET_MAINTAIN", monitor);
		JCoParameterList ipl = function.getImportParameterList();
		
		handleOptions(options, ipl, "CHANGE_NO", null);
		
		ipl.setValue("CONSTRAINT_NET", object.getName());
		JCoStructure constraintNetData = ipl.getStructure("CONSTRAINT_NET_DATA");
		constraintNetData.setValue("DEP_TYPE", "CNET");
		constraintNetData.setValue("STATUS", VcmlUtils.createIntFromStatus(object.getStatus()));
		constraintNetData.setValue("GROUP", object.getGroup());
		JCoParameterList tpl = function.getTableParameterList();
		writeDescription(tpl.getTable("DESCRIPTION"), object.getDescription());
		writeDocumentation(tpl.getTable("DOCUMENTATION"), object.getDocumentation());
		try {
			execute(function, monitor, object.getName());
			endTransaction();
		} catch (AbapException e) {
			handleAbapException(e);
		}
	}
}
