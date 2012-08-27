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
package org.vclipse.bapi.actions.interfacedesign;

import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.ecore.resource.Resource;
import org.vclipse.bapi.actions.BAPIUtils;
import org.vclipse.bapi.actions.IBAPIActionRunner;
import org.vclipse.vcml.vcml.InterfaceDesign;
import org.vclipse.vcml.vcml.Option;
import org.vclipse.vcml.vcml.VCObject;

import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoParameterList;

public class InterfaceDesignDeleteActionHandler extends BAPIUtils implements IBAPIActionRunner<InterfaceDesign> {

	public boolean isEnabled(InterfaceDesign object) {
		return isConnected();
	}

	public void run(InterfaceDesign object, Resource resource, IProgressMonitor monitor, Map<String, VCObject> seenObjects, List<Option> options) throws JCoException {
		JCoFunction function = getJCoFunction("BAPI_UI_DELETE", monitor);	
		JCoParameterList ipl = function.getImportParameterList();
		ipl.setValue("DESIGNNAME", object.getName());
		
		handleOptions(options, ipl, null, null);
		
		execute(function, monitor, object.getName());
		if (processReturnStructure(function)) {
			commit(monitor);
		}
	}

}
