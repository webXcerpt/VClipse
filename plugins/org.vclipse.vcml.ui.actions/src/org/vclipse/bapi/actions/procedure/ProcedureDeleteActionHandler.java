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
package org.vclipse.bapi.actions.procedure;

import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.ecore.resource.Resource;
import org.vclipse.bapi.actions.IBAPIActionRunner;
import org.vclipse.bapi.actions.dependencynet.AbstractDependencyDeleteActionHandler;
import org.vclipse.vcml.vcml.Option;
import org.vclipse.vcml.vcml.Procedure;
import org.vclipse.vcml.vcml.VCObject;

import com.sap.conn.jco.JCoException;

public class ProcedureDeleteActionHandler extends AbstractDependencyDeleteActionHandler implements IBAPIActionRunner<Procedure> {

	public boolean isEnabled(Procedure object) {
		return isConnected();
	}

	public void run(Procedure object, Resource resource, IProgressMonitor monitor, Map<String, VCObject> seenObjects, List<Option> options) throws JCoException {
		super.run(object, resource, monitor, options);
	}

}
