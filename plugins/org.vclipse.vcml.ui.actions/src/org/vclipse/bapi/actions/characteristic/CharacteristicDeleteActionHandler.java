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
package org.vclipse.bapi.actions.characteristic;

import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.ecore.resource.Resource;
import org.vclipse.bapi.actions.BAPIUtils;
import org.vclipse.bapi.actions.IBAPIActionRunner;
import org.vclipse.bapi.actions.characteristic.values.DeleteCharacteristicsDependencies;
import org.vclipse.bapi.actions.characteristic.values.DeleteCharacteristicsValuesDependencies;
import org.vclipse.vcml.vcml.Characteristic;
import org.vclipse.vcml.vcml.Option;
import org.vclipse.vcml.vcml.VCObject;
import org.vclipse.vcml.vcml.VcmlModel;

import com.google.inject.Inject;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoParameterList;

public class CharacteristicDeleteActionHandler extends BAPIUtils implements IBAPIActionRunner<Characteristic> {
	
	@Inject
	private DeleteCharacteristicsDependencies deleteCharacteristicsDependencies;
	
	@Inject
	private DeleteCharacteristicsValuesDependencies deleteCharacteristicsValuesDependencies;
	
	public boolean isEnabled(Characteristic object) {
		return isConnected();
	}

	public void run(Characteristic object, Resource resource, IProgressMonitor monitor, Map<String, VCObject> seenObjects, List<Option> globalOptions) throws JCoException {
		beginTransaction();
		JCoFunction function = getJCoFunction("BAPI_CHARACT_DELETE", monitor);
		JCoParameterList ipl = function.getImportParameterList();
		ipl.setValue("CHARACTNAME", object.getName());
		
		handleOptions(object.getOptions(), globalOptions, ipl, "CHANGENUMBER", "KEYDATE");
		
		execute(function, monitor, object.getName());
		if (processReturnTable(function)) {
			commit(monitor);
		}
		endTransaction();
		
		VcmlModel vcmlModel = (VcmlModel)resource.getContents().get(0);
		if(deleteCharacteristicsDependencies.enabled(object)) {
			try {
				deleteCharacteristicsDependencies.run(object, vcmlModel, monitor, seenObjects);
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		
		if(deleteCharacteristicsValuesDependencies.enabled(object)) {
			try {
				deleteCharacteristicsValuesDependencies.run(object, vcmlModel, monitor, seenObjects);
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
}
