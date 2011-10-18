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
import org.vclipse.vcml.vcml.InterfaceDesign;

import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoFunction;

public class InterfaceDesignDeleteActionHandler extends BAPIUtils implements IVCMLOutlineActionHandler<InterfaceDesign> {

	public boolean isEnabled(InterfaceDesign object) {
		return isConnected();
	}

	public void run(InterfaceDesign object, Resource resource, IProgressMonitor monitor) throws JCoException {
		JCoFunction function = getJCoFunction("BAPI_UI_DELETE", monitor);	
		function.getImportParameterList().setValue("DESIGNNAME", object.getName());
		execute(function, monitor, object.getName());
		if (processReturnStructure(function)) {
			commit(monitor);
		}
	}

}
