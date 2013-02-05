/*******************************************************************************
 * Copyright (c) 2010 - 2013 webXcerpt Software GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     webXcerpt Software GmbH - initial creator
 *     www.webxcerpt.com
 ******************************************************************************/
package org.vclipse.bapi.actions.characteristic.values;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
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
 * Reads dependency objects used by characteristic values.
 */
public class ReadCharacteristicValueDependency extends DependencyReader {
	
	public void read(Characteristic cstic, VcmlModel vcmlModel, IProgressMonitor monitor, Map<String, VCObject> seenObjects, List<Option> globalOptions, boolean recurse) throws JCoException {
		if(monitor.isCanceled()) {
			return;
		}
		
		Resource resource = vcmlModel.eResource();
		StringBuffer messageBuffer = new StringBuffer("Extracting procedures for values of the characteristic ").append(cstic.getName());
		SubMonitor submonitor = SubMonitor.convert(monitor, messageBuffer.toString(), IProgressMonitor.UNKNOWN);
		Map<String, EObject> name2Value = vcmlUtilities.getNameToValue(cstic.getType());
		EReference valueDependenciesReference = factoryExtension.VCML_PACKAGE.getCharacteristicValue_Dependencies();
		for(Entry<String, EObject> entries : name2Value.entrySet()) {
			String value = entries.getKey();
			JCoFunction valueDependencies = functionPerformer.CARD_CHAR_VAL_READ_ALLOC(cstic.getName(), value, submonitor, cstic.getOptions(), globalOptions);
			JCoTable table = valueDependencies.getTableParameterList().getTable(JCoFunctionPerformer.DEP_ASSIGN);
			EObject csticValue = name2Value.get(value);
			if(csticValue == null) { // does not exist
				csticValue = factoryExtension.newCharacteristicValue(value);
			}
			
			CharacteristicOrValueDependencies dependencies = vcmlUtilities.processDependencies(csticValue, valueDependenciesReference, null);
			dependencies = dependencies == null ? factoryExtension.VCML_FACTORY.createCharacteristicOrValueDependencies() : dependencies;
			EList<Dependency> csticDependencies = dependencies.getDependencies();
			for(int i=0; i<table.getNumRows(); i++) {
				table.setRow(i);
				String dependencyName = table.getString(JCoFunctionPerformer.DEPENDENCY); // only procedures are allowed for values
				Dependency dependency = readDependency(dependencyName, submonitor, resource, seenObjects, cstic.getOptions(), globalOptions, recurse);
				if(dependency != null) {
					csticDependencies.add(dependency);					
				}
				if(submonitor.isCanceled()) {
					// do not return in this case -> dependencies are not yet added
					break;
				}
			}
			if(!csticDependencies.isEmpty()) {
				vcmlUtilities.processDependencies(csticValue, valueDependenciesReference, dependencies);				
			}
			if(submonitor.isCanceled()) {
				break;
			}
		}
		submonitor.done();
	}
}
