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
package org.vclipse.vcml.ui.actions.selectioncondition;

import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.ecore.resource.Resource;
import org.vclipse.vcml.ui.outline.actions.IVCMLOutlineActionHandler;
import org.vclipse.vcml.vcml.SelectionCondition;

import com.sap.conn.jco.JCoException;

public class SelectionConditionExtractActionHandler extends SelectionConditionReader implements IVCMLOutlineActionHandler<SelectionCondition>{

	public boolean isEnabled(SelectionCondition object) {
		return isConnected();
	}

	public void run(SelectionCondition selectionCondition, Resource resource, IProgressMonitor monitor, Set<String> seenObjects) throws JCoException {
		read(selectionCondition.getName(), resource, monitor, seenObjects, true);
	}

}
