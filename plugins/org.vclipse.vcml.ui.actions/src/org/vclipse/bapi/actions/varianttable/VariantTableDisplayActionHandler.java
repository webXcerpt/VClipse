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
package org.vclipse.bapi.actions.varianttable;

import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.ecore.resource.Resource;
import org.vclipse.bapi.actions.IBAPIActionRunner;
import org.vclipse.vcml.vcml.Option;
import org.vclipse.vcml.vcml.VCObject;
import org.vclipse.vcml.vcml.VariantTable;
import org.vclipse.vcml.vcml.VcmlModel;

import com.sap.conn.jco.JCoException;

public class VariantTableDisplayActionHandler extends VariantTableReader implements IBAPIActionRunner<VariantTable>{

	public boolean isEnabled(VariantTable object) {
		return isConnected();
	}

	public void run(VariantTable variantTable, Resource resource, IProgressMonitor monitor, Map<String, VCObject> seenObjects, List<Option> options) throws JCoException {
		read(variantTable.getName(), (VcmlModel)resource.getContents().get(0), monitor, seenObjects, options, false);
	}

}
