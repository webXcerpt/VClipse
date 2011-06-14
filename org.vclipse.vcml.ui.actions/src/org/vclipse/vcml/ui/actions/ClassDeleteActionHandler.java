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

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.ecore.resource.Resource;
import org.vclipse.vcml.ui.outline.actions.IVCMLOutlineActionHandler;
import org.vclipse.vcml.vcml.Class;
import org.vclipse.vcml.utils.VCMLUtils;

import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoParameterList;

public class ClassDeleteActionHandler extends BAPIUtils implements IVCMLOutlineActionHandler<Class> {
	
	public boolean isEnabled(Class object) {
		return isConnected();
	}

	public void run(Class object, Resource resource, IProgressMonitor monitor) throws JCoException {
		beginTransaction();
		JCoFunction function = getJCoFunction("BAPI_CLASS_DELETE", monitor);	
		JCoParameterList ipl = function.getImportParameterList();
		String classSpec = object.getName();
		String className = VCMLUtils.getClassName(classSpec);
		int classType = VCMLUtils.getClassType(classSpec);
		ipl.setValue("CLASSNUM", className);
		ipl.setValue("CLASSTYPE", classType);
		execute(function, monitor, object.getName());
		if (processReturnTable(function)) {
			commit(monitor);
		}
		endTransaction();
	}

}
