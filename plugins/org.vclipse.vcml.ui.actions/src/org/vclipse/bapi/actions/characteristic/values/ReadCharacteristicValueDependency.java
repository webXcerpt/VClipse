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

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.emf.common.util.EList;
import org.vclipse.bapi.actions.BAPIActionPlugin;
import org.vclipse.bapi.actions.BAPIUtils;
import org.vclipse.bapi.actions.procedure.ProcedureReader;
import org.vclipse.vcml.mm.VCMLFactoryExtension;
import org.vclipse.vcml.mm.VCMLUtilities;
import org.vclipse.vcml.vcml.Characteristic;
import org.vclipse.vcml.vcml.CharacteristicOrValueDependencies;
import org.vclipse.vcml.vcml.CharacteristicValue;
import org.vclipse.vcml.vcml.Dependency;
import org.vclipse.vcml.vcml.Option;
import org.vclipse.vcml.vcml.Procedure;
import org.vclipse.vcml.vcml.VCObject;
import org.vclipse.vcml.vcml.VcmlModel;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoParameterList;
import com.sap.conn.jco.JCoTable;

/**
 * Reads preconditions being used during the characteristic value sets.
 */
public class ReadCharacteristicValueDependency extends BAPIUtils {
	
	@Inject
	private ProcedureReader procedureReader;
	
	@Inject
	private VCMLUtilities vcmlUtilities;
	
	@Inject
	private VCMLFactoryExtension factoryExtension;
	
	/**
	 * BAPI for reading allowed values for a characteristic
	 */
	public String CARD_CHARACTERISTIC_READ = "CARD_CHARACTERISTIC_READ";
	
	/**
	 * BAPI for reading a dependency key for a characteristic value
	 */
	public String CARD_CHAR_VAL_READ_ALLOC = "CARD_CHAR_VAL_READ_ALLOC";
	
	/**
	 * 
	 */
	public void read(Characteristic cstic, VcmlModel vcmlModel, final IProgressMonitor monitor, Map<String, VCObject> seenObjects, List<Option> globalOptions, boolean recurse) throws JCoException {
		StringBuffer messageBuffer = new StringBuffer("Extracting preconditions for values of the characteristic ");
		messageBuffer.append(cstic.getName());
		SubMonitor submonitor = SubMonitor.convert(monitor, messageBuffer.toString(), IProgressMonitor.UNKNOWN);
		JCoFunction keysValueDependencies = getJCoFunction(CARD_CHAR_VAL_READ_ALLOC, submonitor);
		JCoParameterList ipl = keysValueDependencies.getImportParameterList();
		ipl.setValue("CHARACTERISTIC", cstic.getName());
		ipl.setValue("LIST_ALL_GLOBL", "X");
		ipl.setValue("LIST_ALL_LOCAL", "X");
		Map<String, CharacteristicValue> name2Value = vcmlUtilities.getCharacteristicValues(cstic);
		for(String value : getAllowedValues(cstic, monitor)) { // execute only for allowed values
			ipl.setValue("VALUE", value);
			StringBuffer args = new StringBuffer(cstic.getName()).append(".").append(value);
			execute(keysValueDependencies, monitor, args.toString());
			JCoTable table = keysValueDependencies.getTableParameterList().getTable("DEP_ASSIGN");
			// TODO how are intervalls handled by the sap system ?
			CharacteristicValue csticValue = name2Value.get(value);
			if(csticValue == null) { // does not exist
				csticValue = factoryExtension.newCharacteristicValue(value);
			}
			CharacteristicOrValueDependencies dependencies = csticValue.getDependencies();
			dependencies = dependencies == null ? factoryExtension.VCML_FACTORY.createCharacteristicOrValueDependencies() : dependencies;
			EList<Dependency> csticDependencies = dependencies.getDependencies();
			for(int i=0; i<table.getNumRows(); i++) {
				table.setRow(i);
				String procedureName = (String)table.getValue("DEPENDENCY"); // only procedures are allowed on values
				Procedure procedure = procedureReader.read(procedureName, vcmlModel.eResource(), submonitor, seenObjects, globalOptions, recurse);
				csticDependencies.add(procedure);
			}
			csticValue.setDependencies(dependencies);
		}
		submonitor.done();
	}
	
	/**
	 * Reads values allowed for the requested characteristic.
	 */
	protected List<String> getAllowedValues(Characteristic cstic, IProgressMonitor monitor) {
		List<String> values = Lists.newArrayList();
		try {
			JCoFunction allowedCsticValues = getJCoFunction(CARD_CHARACTERISTIC_READ, monitor);
			JCoParameterList ipl = allowedCsticValues.getImportParameterList();
			ipl.setValue("CHARACTERISTIC", cstic.getName());
			ipl.setValue("WITH_VALUES", "X");
			execute(allowedCsticValues, monitor, cstic.getName());
			JCoTable valuesTable = allowedCsticValues.getTableParameterList().getTable("ALLOWED_VALUES");
			for(int i=0; i<valuesTable.getNumRows(); i++) {
				valuesTable.setRow(i);
				String currentValue = (String)valuesTable.getValue("VALUE");
				if(Strings.isNullOrEmpty(currentValue)) {
					// TODO better error report
					errorStream.println("Error during function execution: " + allowedCsticValues);
					continue;
				}
				values.add(currentValue);
			}
		} catch(JCoException exception) {
			BAPIActionPlugin.log(IStatus.ERROR, exception.getMessage());
		}
		return values;
	}
}
