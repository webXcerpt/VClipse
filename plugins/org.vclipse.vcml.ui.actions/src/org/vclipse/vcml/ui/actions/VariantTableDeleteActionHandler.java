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
import org.vclipse.vcml.vcml.VariantTable;

import com.sap.conn.jco.AbapException;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoTable;

public class VariantTableDeleteActionHandler extends BAPIUtils implements IVCMLOutlineActionHandler<VariantTable>{

	@Override
	public boolean isEnabled(VariantTable object) {
		return isConnected();
	}

	@Override
	public void run(VariantTable object, Resource resource, IProgressMonitor monitor) throws JCoException {
		String name = object.getName();
		beginTransaction();
		JCoFunction function = getJCoFunction("CAMA_TABLE_MAINTAIN_STRUCTURE", monitor);
		JCoTable varTabBasicData = function.getTableParameterList().getTable("VAR_TAB_BASIC_DATA");
		varTabBasicData.appendRow();
		varTabBasicData.setValue("VAR_TAB", name);
		varTabBasicData.setValue("FLDELETE", "X");
		try {
			execute(function, monitor, "DELETE " + name);
			endTransaction();
		} catch (AbapException e) {
			handleAbapException(e);
		}
	}

}	
