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
package org.vclipse.vcml.ui.actions.classes;

import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.ecore.resource.Resource;
import org.vclipse.vcml.ui.outline.actions.IVCMLOutlineActionHandler;
import org.vclipse.vcml.vcml.Class;
import org.vclipse.vcml.vcml.Model;

import com.sap.conn.jco.JCoException;

public class ClassExtractActionHandler extends ClassReader implements IVCMLOutlineActionHandler<Class> {

	public boolean isEnabled(Class object) {
		return isConnected();
	}

	public void run(Class cls, Resource resource, IProgressMonitor monitor, Set<String> seenObjects) throws JCoException {
		read(cls.getName(), (Model)resource.getContents().get(0), monitor, seenObjects, true);
	}

}
