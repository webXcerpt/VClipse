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
package org.vclipse.bapi.actions.characteristic.values;

import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.resource.Resource;
import org.vclipse.bapi.actions.JCoFunctionPerformer;
import org.vclipse.vcml.vcml.Characteristic;
import org.vclipse.vcml.vcml.CharacteristicOrValueDependencies;
import org.vclipse.vcml.vcml.Dependency;
import org.vclipse.vcml.vcml.Option;
import org.vclipse.vcml.vcml.VCObject;
import org.vclipse.vcml.vcml.VcmlModel;

import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoTable;

/**
 * Reads dependency objects used by characteristics.
 */
public class ReadCharacteristicDependency extends DependencyReader {

	public void read(Characteristic cstic, VcmlModel vcmlModel, final IProgressMonitor monitor, Map<String, VCObject> seenObjects, boolean recurse) throws JCoException {
		if(monitor.isCanceled()) {
			return;
		}
		Resource resource = vcmlModel.eResource();
		EList<Option> modelOptions = vcmlModel.getOptions();
		EList<Option> objectOptions = cstic.getOptions();
		StringBuffer messageBuffer = new StringBuffer("Extracting dependencies for the characteristic").append(cstic.getName());
		SubMonitor submonitor = SubMonitor.convert(monitor, messageBuffer.toString(), IProgressMonitor.UNKNOWN);
		
		EReference csticDependenciesReference = factoryExtension.VCML_PACKAGE.getCharacteristic_Dependencies();
		CharacteristicOrValueDependencies dependencies = vcmlUtilities.processDependencies(cstic, csticDependenciesReference, null);
		dependencies = dependencies == null ? factoryExtension.VCML_FACTORY.createCharacteristicOrValueDependencies() : dependencies;
		EList<Dependency> csticDependencies = dependencies.getDependencies();
		
		JCoFunction function = functionPerformer.CARD_CHAR_READ_ALLOC(cstic.getName(), submonitor, cstic.getOptions(), vcmlModel.getOptions());
		JCoTable table = function.getTableParameterList().getTable(JCoFunctionPerformer.DEP_ASSIGN);
		for(int rowIndex=0; rowIndex<table.getNumRows(); rowIndex++) {
			table.setRow(rowIndex);
			String dependencyName = table.getString(JCoFunctionPerformer.DEPENDENCY);
			Dependency dependency = readDependency(dependencyName, submonitor, resource, seenObjects, objectOptions, modelOptions, recurse);
			if(dependency != null) {
				csticDependencies.add(dependency);
			}
			if(submonitor.isCanceled()) {
				// do not return in this case -> dependencies are not yet added
				break;
			}
		}
		if(!csticDependencies.isEmpty()) {
			vcmlUtilities.processDependencies(cstic, csticDependenciesReference, dependencies);				
		}
		submonitor.done();
	}
}
