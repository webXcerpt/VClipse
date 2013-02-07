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

import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.vclipse.bapi.actions.BAPIException;
import org.vclipse.bapi.actions.JCoFunctionPerformer;
import org.vclipse.vcml.vcml.Characteristic;
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
public class ReadCharacteristicsValuesDependency extends DependencyReader {
	
	public void read(Characteristic cstic, VcmlModel vcmlModel, IProgressMonitor monitor, Map<String, VCObject> seenObjects, boolean recurse) throws JCoException {
		if(monitor.isCanceled()) {
			monitor.done();
			return;
		}
		EList<Option> modelOptions = vcmlModel.getOptions();
		Resource resource = vcmlModel.eResource();
		StringBuffer messageBuffer = new StringBuffer("Extracting dependencies for the values of the characteristic").append(cstic.getName());
		SubMonitor submonitor = SubMonitor.convert(monitor, messageBuffer.toString(), IProgressMonitor.UNKNOWN);
		if(cstic.getType() == null) {
			submonitor.done();
			throw new BAPIException("Characteristic " + cstic.getName() + " has no type.");
		}
		Map<String, EObject> name2Value = vcmlUtilities.getNameToValue(cstic.getType());
		for(Entry<String, EObject> entries : name2Value.entrySet()) {
			String value = entries.getKey();
			JCoFunction valueDependencies = functionPerformer.CARD_CHAR_VAL_READ_ALLOC(cstic.getName(), value, submonitor, cstic.getOptions(), modelOptions);
			JCoTable table = valueDependencies.getTableParameterList().getTable(JCoFunctionPerformer.DEP_ASSIGN);
			EObject csticValue = name2Value.get(value);
			if(csticValue == null) { 
				csticValue = factoryExtension.newCharacteristicValue(value);
			}
			EList<Dependency> csticDependencies = vcmlUtilities.getDependencies(csticValue);
			for(int i=0; i<table.getNumRows(); i++) {
				table.setRow(i);
				String dependencyName = table.getString(JCoFunctionPerformer.DEPENDENCY);
				Dependency dependency = readDependency(dependencyName, submonitor, resource, seenObjects, cstic.getOptions(), modelOptions, recurse);
				if(dependency != null) {
					csticDependencies.add(dependency);					
				}
				if(submonitor.isCanceled()) {
					break;
				}
			}
		}
		submonitor.done();
	}
}
