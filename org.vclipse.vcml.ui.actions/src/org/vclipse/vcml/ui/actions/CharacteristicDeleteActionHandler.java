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
import org.vclipse.vcml.vcml.Characteristic;

import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoFunction;

public class CharacteristicDeleteActionHandler extends BAPIUtils implements IVCMLOutlineActionHandler<Characteristic> {
	
	public boolean isEnabled(Characteristic object) {
		return isConnected() && 
			!(object.getName().startsWith("GEN_") || object.getName().startsWith("IPGEN_"));
	}

	public void run(Characteristic object, Resource resource, IProgressMonitor monitor) throws JCoException {
		beginTransaction();
		JCoFunction function = getJCoFunction("BAPI_CHARACT_DELETE", monitor);
		function.getImportParameterList().setValue("CHARACTNAME", object.getName());
		execute(function, monitor, object.getName());
		if (processReturnTable(function)) {
			commit(monitor);
		}
		endTransaction();
	}


}
