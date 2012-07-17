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
package org.vclipse.bapi.actions.selectioncondition;

import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.ecore.resource.Resource;
import org.vclipse.bapi.actions.IBAPIActionRunner;
import org.vclipse.vcml.vcml.Option;
import org.vclipse.vcml.vcml.SelectionCondition;

import com.sap.conn.jco.JCoException;

public class SelectionConditionDisplayActionHandler extends SelectionConditionReader implements IBAPIActionRunner<SelectionCondition>{

	public boolean isEnabled(SelectionCondition object) {
		return isConnected();
	}

	public void run(SelectionCondition selectionCondition, Resource resource, IProgressMonitor monitor, Set<String> seenObjects, List<Option> options) throws JCoException {
		read(selectionCondition.getName(), resource, monitor, seenObjects, options, false);
	}

}
