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
package org.vclipse.vcml.ui.actions.dependencynet;

import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.ecore.resource.Resource;
import org.vclipse.vcml.ui.actions.BAPIUtils;
import org.vclipse.vcml.ui.outline.actions.IVcmlOutlineActionHandler;
import org.vclipse.vcml.vcml.DependencyNet;
import org.vclipse.vcml.vcml.Option;

import com.sap.conn.jco.AbapException;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoParameterList;

public class DependencyNetDeleteActionHandler extends BAPIUtils implements IVcmlOutlineActionHandler<DependencyNet> {

	public boolean isEnabled(DependencyNet object) {
		return isConnected();
	}

	public void run(DependencyNet object, Resource resource, IProgressMonitor monitor, Set<String> seenObjects, List<Option> options) throws JCoException {
		JCoFunction function = getJCoFunction("CAMA_CONSTRAINT_NET_MAINTAIN", monitor);
		JCoParameterList ipl = function.getImportParameterList();
		
		handleOptions(options, ipl, "CHANGE_NO", null);
		
		ipl.setValue("CONSTRAINT_NET", object.getName());
		ipl.setValue("DELETE_FLAG", "X");
		try {
			execute(function, monitor, "DELETE " + object.getName());
			endTransaction();
		} catch (AbapException e) {
			handleAbapException(e);
		}
	}

}
