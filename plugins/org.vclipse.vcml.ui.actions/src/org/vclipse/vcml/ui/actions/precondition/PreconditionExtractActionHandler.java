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
package org.vclipse.vcml.ui.actions.precondition;

import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.ecore.resource.Resource;
import org.vclipse.vcml.ui.outline.actions.IVcmlOutlineActionHandler;
import org.vclipse.vcml.vcml.Option;
import org.vclipse.vcml.vcml.Precondition;

import com.sap.conn.jco.JCoException;

public class PreconditionExtractActionHandler extends PreconditionReader implements IVcmlOutlineActionHandler<Precondition>{

	public boolean isEnabled(Precondition object) {
		return isConnected();
	}

	public void run(Precondition precondition, Resource resource, IProgressMonitor monitor, Set<String> seenObjects, List<Option> options) throws JCoException {
		read(precondition.getName(), resource, monitor, seenObjects, options, true);
	}

}
