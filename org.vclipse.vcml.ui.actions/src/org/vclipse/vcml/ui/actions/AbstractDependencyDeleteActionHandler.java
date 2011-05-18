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
import org.vclipse.vcml.vcml.VCObject;

import com.sap.conn.jco.AbapException;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoParameterList;

public abstract class AbstractDependencyDeleteActionHandler extends BAPIUtils {

	public void run(VCObject object, Resource resource, IProgressMonitor monitor) throws JCoException {
		beginTransaction();
		JCoFunction function = getJCoFunction("CAMA_DEPENDENCY_MAINTAIN", monitor);
		JCoParameterList ipl = function.getImportParameterList();
		ipl.setValue("DEPENDENCY", object.getName());
		ipl.setValue("FL_DELETE", "X");
		try {
			execute(function, monitor, "DELETE " + object.getName());
			endTransaction();
		} catch (AbapException e) {
			handleAbapException(e);
		}
	}
	
	

}
