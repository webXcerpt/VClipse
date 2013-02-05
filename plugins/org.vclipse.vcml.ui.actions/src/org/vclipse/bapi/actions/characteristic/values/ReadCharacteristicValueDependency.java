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
import org.eclipse.emf.ecore.resource.Resource;
import org.vclipse.bapi.actions.BAPIUtils;
import org.vclipse.bapi.actions.JCoFunctionPerformer;
import org.vclipse.bapi.actions.constraint.ConstraintReader;
import org.vclipse.bapi.actions.precondition.PreconditionReader;
import org.vclipse.bapi.actions.procedure.ProcedureReader;
import org.vclipse.bapi.actions.selectioncondition.SelectionConditionReader;
import org.vclipse.vcml.VCMLFactoryExtension;
import org.vclipse.vcml.VCMLUtilities;
import org.vclipse.vcml.utils.DependencySourceUtils;
import org.vclipse.vcml.vcml.Characteristic;
import org.vclipse.vcml.vcml.CharacteristicOrValueDependencies;
import org.vclipse.vcml.vcml.Dependency;
import org.vclipse.vcml.vcml.Option;
import org.vclipse.vcml.vcml.VCObject;
import org.vclipse.vcml.vcml.VcmlModel;

import com.google.inject.Inject;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoParameterList;
import com.sap.conn.jco.JCoStructure;
import com.sap.conn.jco.JCoTable;

/**
 * Reads procedures being used during the characteristic value sets.
 */
public class ReadCharacteristicValueDependency extends BAPIUtils {
	
	@Inject
	private ProcedureReader procedureReader;
	
	@Inject
	private ConstraintReader constraintReader;
	
	@Inject
	private PreconditionReader preconditionReader;
	
	@Inject
	private SelectionConditionReader selectionConditionReader;
	
	@Inject
	private VCMLUtilities vcmlUtilities;
	
	@Inject
	private VCMLFactoryExtension factoryExtension;
	
	@Inject
	private JCoFunctionPerformer functionPerformer;
	
	/**
	 * BAPI for reading allowed values for a characteristic
	 */
	public String CARD_CHARACTERISTIC_READ = "CARD_CHARACTERISTIC_READ";
	
	/**
	 * BAPI for reading a dependency key for a characteristic value
	 */
	public String CARD_CHAR_VAL_READ_ALLOC = "CARD_CHAR_VAL_READ_ALLOC";
	
	/**
	 * Table containing dependencies
	 */
	public static final String DEP_ASSIGN = "DEP_ASSIGN";
	
	/**
	 * 
	 */
	public void read(Characteristic cstic, VcmlModel vcmlModel, IProgressMonitor monitor, Map<String, VCObject> seenObjects, List<Option> globalOptions, boolean recurse) throws JCoException {
		if(monitor.isCanceled()) {
			return;
		}
		Resource resource = vcmlModel.eResource();
		StringBuffer messageBuffer = new StringBuffer("Extracting procedures for values of the characteristic ").append(cstic.getName());
		SubMonitor submonitor = SubMonitor.convert(monitor, messageBuffer.toString(), IProgressMonitor.UNKNOWN);
		JCoFunction keysValueDependencies = getJCoFunction(CARD_CHAR_VAL_READ_ALLOC, submonitor);
		JCoParameterList ipl = keysValueDependencies.getImportParameterList();
		ipl.setValue(JCoFunctionPerformer.CHARACTERISTIC, cstic.getName());
		ipl.setValue("LIST_ALL_GLOBL", JCoFunctionPerformer.SELECTED);
		ipl.setValue("LIST_ALL_LOCAL", JCoFunctionPerformer.SELECTED);
		Map<String, EObject> name2Value = vcmlUtilities.getNameToValue(cstic.getType());
		for(Entry<String, EObject> entries : name2Value.entrySet()) {
			String value = entries.getKey();
			ipl.setValue("VALUE", value);
			StringBuffer args = new StringBuffer(cstic.getName()).append(".").append(value);
			execute(keysValueDependencies, monitor, args.toString());
			JCoTable table = keysValueDependencies.getTableParameterList().getTable(DEP_ASSIGN);
			EObject csticValue = name2Value.get(value);
			if(csticValue == null) { // does not exist
				csticValue = factoryExtension.newCharacteristicValue(value);
			}
			CharacteristicOrValueDependencies dependencies = vcmlUtilities.processDependencies(csticValue, null);
			dependencies = dependencies == null ? factoryExtension.VCML_FACTORY.createCharacteristicOrValueDependencies() : dependencies;
			EList<Dependency> csticDependencies = dependencies.getDependencies();
			for(int i=0; i<table.getNumRows(); i++) {
				table.setRow(i);
				String dependencyName = (String)table.getValue("DEPENDENCY"); // only procedures are allowed for values
				JCoFunction dependencyTypeFunction = functionPerformer.CARD_DEPENDENCY_READ(dependencyName, submonitor, cstic.getOptions(), globalOptions);
				JCoStructure dependencyData = dependencyTypeFunction.getExportParameterList().getStructure("DEPENDENCY_DATA");
				String dependencyType = dependencyData.getString("DEP_TYPE").toLowerCase();
				Dependency dependency = null;
				if(DependencySourceUtils.EXTENSION_PROCEDURE.equals(dependencyType)) {
					dependency = procedureReader.read(dependencyName, resource, submonitor, seenObjects, globalOptions, recurse);
				} else if(DependencySourceUtils.EXTENSION_CONSTRAINT.equals(dependencyType)) {
					dependency = constraintReader.read(dependencyName, resource, submonitor, seenObjects, globalOptions, recurse);
				} else if(DependencySourceUtils.EXTENSION_PRECONDITION.equals(dependencyType)) {
					dependency = preconditionReader.read(dependencyName, resource, submonitor, seenObjects, globalOptions, recurse);
				} else if(DependencySourceUtils.EXTENSION_SELECTIONCONDITION.equals(dependencyType)) {
					dependency = selectionConditionReader.read(dependencyName, resource, submonitor, seenObjects, globalOptions, recurse);
				}
				if(dependency != null) {
					csticDependencies.add(dependency);					
				}
				if(submonitor.isCanceled()) {
					// do not return in this case -> dependencies are not yet added
					break;
				}
			}
			if(!csticDependencies.isEmpty()) {
				vcmlUtilities.processDependencies(csticValue, dependencies);				
			}
			if(submonitor.isCanceled()) {
				break;
			}
		}
		submonitor.done();
	}
}
