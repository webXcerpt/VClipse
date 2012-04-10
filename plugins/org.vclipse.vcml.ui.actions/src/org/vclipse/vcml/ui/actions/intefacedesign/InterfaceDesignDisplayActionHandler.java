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
package org.vclipse.vcml.ui.actions.intefacedesign;

import java.util.HashSet;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.ecore.resource.Resource;
import org.vclipse.vcml.ui.outline.actions.IVCMLOutlineActionHandler;
import org.vclipse.vcml.vcml.InterfaceDesign;

import com.sap.conn.jco.JCoException;

public class InterfaceDesignDisplayActionHandler extends InterfaceDesignReader implements IVCMLOutlineActionHandler<InterfaceDesign> {

	public boolean isEnabled(InterfaceDesign object) {
		return isConnected();
	}

	public void run(InterfaceDesign interfaceDesign, Resource resource, IProgressMonitor monitor) throws JCoException {
		read(interfaceDesign.getName(), resource, monitor, new HashSet<String>(), false);
	}

}
