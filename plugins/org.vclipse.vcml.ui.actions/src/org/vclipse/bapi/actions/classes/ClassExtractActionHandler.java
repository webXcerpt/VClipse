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
package org.vclipse.bapi.actions.classes;

import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.ecore.resource.Resource;
import org.vclipse.bapi.actions.IBAPIActionRunner;
import org.vclipse.vcml.vcml.Class;
import org.vclipse.vcml.vcml.Option;
import org.vclipse.vcml.vcml.VCObject;
import org.vclipse.vcml.vcml.VcmlModel;

import com.sap.conn.jco.JCoException;

public class ClassExtractActionHandler extends ClassReader implements IBAPIActionRunner<Class> {

	public boolean isEnabled(Class object) {
		return isConnected();
	}

	public void run(Class cls, Resource resource, IProgressMonitor monitor, Map<String, VCObject> seenObjects, List<Option> options) throws JCoException {
		read(cls.getName(), (VcmlModel)resource.getContents().get(0), monitor, seenObjects, options, true);
	}

}
