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
package org.vclipse.bapi.actions.classes;

import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.ecore.resource.Resource;
import org.vclipse.bapi.actions.BAPIUtils;
import org.vclipse.bapi.actions.IBAPIActionRunner;
import org.vclipse.vcml.utils.VcmlUtils;
import org.vclipse.vcml.vcml.Class;
import org.vclipse.vcml.vcml.Option;
import org.vclipse.vcml.vcml.VCObject;

import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoParameterList;

public class ClassDeleteActionHandler extends BAPIUtils implements IBAPIActionRunner<Class> {
	
	public boolean isEnabled(Class object) {
		return isConnected();
	}

	public void run(Class object, Resource resource, IProgressMonitor monitor, Map<String, VCObject> seenObjects, List<Option> globalOptions) throws JCoException {
		beginTransaction();
		JCoFunction function = getJCoFunction("BAPI_CLASS_DELETE", monitor);	
		JCoParameterList ipl = function.getImportParameterList();
		String classSpec = object.getName();
		String className = VcmlUtils.getClassName(classSpec);
		int classType = VcmlUtils.getClassType(classSpec);
		
		handleOptions(object.getOptions(), globalOptions, ipl, "CHANGENUMBER", null);
		
		ipl.setValue("CLASSNUM", className);
		ipl.setValue("CLASSTYPE", classType);
		execute(function, monitor, object.getName());
		if (processReturnTable(function)) {
			commit(monitor);
		}
		endTransaction();
	}

}
