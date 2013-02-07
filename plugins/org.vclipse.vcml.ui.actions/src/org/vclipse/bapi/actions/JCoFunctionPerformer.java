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
package org.vclipse.bapi.actions;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.vclipse.base.naming.INameProvider;
import org.vclipse.vcml.SAPFormattingUtility;
import org.vclipse.vcml.VCMLUtilities;
import org.vclipse.vcml.vcml.Characteristic;
import org.vclipse.vcml.vcml.Dependency;
import org.vclipse.vcml.vcml.Option;

import com.google.inject.Inject;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoParameterList;
import com.sap.conn.jco.JCoTable;

/**
 *
 */
public class JCoFunctionPerformer extends BAPIUtils {

	/**
	 * 
	 */
	public static final String VALUE = "VALUE";

	public static final String SELECTED = "X";
	
	/**
	 * Values
	 */
	public static final String CHARACTERISTIC[] = new String[]{"CHARACTERISTIC", "CHARACT"};
	public static final String DEPENDENCY = "DEPENDENCY";
	public static final String DEP_TYPE = "DEP_TYPE";
	public static final String DEPENDENCY_DATA = "DEPENDENCY_DATA";
	
	public static final String CHANGE_NO = "CHANGE_NO";
	public static final String DATE = "DATE";
	
	/**
	 * Tables
	 */
	public static final String DEP_ASSIGN = "DEP_ASSIGN";
	
	@Inject
	private VCMLUtilities vcmlUtilities;
	
	@Inject
	private INameProvider nameProvider;
	
	@Inject
	private SAPFormattingUtility sapFormatter;
	
	/**
	 * Assign global dependencies to the values of a characteristic.
	 */
	public void CAMA_CHAR_VAL_ALLOCAT_GLOB_DEP(Characteristic cstic, IProgressMonitor monitor, EList<Option> global, EList<Option> local) throws Exception {
		if(monitor.isCanceled()) {
			throw new BAPIException("Function call CAMA_CHAR_VAL_ALLOCAT_GLOB_DEP cancelled by the user.");
		}
		JCoFunction function = getJCoFunction("CAMA_CHAR_VAL_ALLOCAT_GLOB_DEP", monitor);
		JCoParameterList ipl = function.getImportParameterList();
		String csticName = cstic.getName();
		applyOptions(global, local, ipl, JCoFunctionPerformer.CHANGE_NO, JCoFunctionPerformer.DATE);
		JCoTable dependenciesTable = function.getTableParameterList().getTable("CHAR_VAL_DEP_ASSIGN");
		Map<String, EObject> nameToValue = vcmlUtilities.getNameToValue(cstic.getType());
		for(int i=0; i<dependenciesTable.getNumRows(); i++) {
			dependenciesTable.setRow(i);
			String csticEntry = dependenciesTable.getString(CHARACTERISTIC[1]);
			if(csticEntry.equals(csticName)) {
				String valueEntry = dependenciesTable.getString(VALUE);
				EObject type = nameToValue.get(valueEntry);
				if(type != null) {
					dependenciesTable.deleteRow(i);
				}				
			}
		}
		for(Entry<String, EObject> entry : nameToValue.entrySet()) {
			EObject value = entry.getValue();
			EList<Dependency> dependencies = vcmlUtilities.getDependencies(value);
			for(Dependency dependency : dependencies) {
				dependenciesTable.appendRow();
				dependenciesTable.setValue(CHARACTERISTIC[1], csticName);
				dependenciesTable.setValue(VALUE, sapFormatter.toString(value));
				String dependencyName = nameProvider.getName(dependency);
				dependenciesTable.setValue(DEPENDENCY, dependencyName);
			}
		}
		commit(monitor);
	}
	
	/**
	 * Assign global dependencies to a characteristic.
	 */
	public JCoFunction CAMA_CHAR_ALLOCATE_GLOB_DEP(Characteristic cstic, IProgressMonitor monitor, List<Option> global, List<Option> local) throws Exception {
		if(monitor.isCanceled()) {
			throw new BAPIException("Function call CAMA_CHAR_ALLOCATE_GLOB_DEP cancelled by the user.");
		}
		JCoFunction function = getJCoFunction("CAMA_CHAR_ALLOCATE_GLOB_DEP", monitor);
		JCoParameterList ipl = function.getImportParameterList();
		applyOptions(global, local, ipl, JCoFunctionPerformer.CHANGE_NO, JCoFunctionPerformer.DATE);
		JCoTable dependenciesTable = function.getTableParameterList().getTable("CHAR_DEP_ASSIGN");
		String csticName = cstic.getName();
		for(int i=0; i<dependenciesTable.getNumRows(); i++) {
			dependenciesTable.setRow(i);
			String csticEntry = dependenciesTable.getString(CHARACTERISTIC[1]);
			if(csticEntry.equals(csticName)) {
				dependenciesTable.deleteRow(i);
			}
		}
		for(Dependency dependency : cstic.getDependencies().getDependencies()) {
			dependenciesTable.appendRow();
			dependenciesTable.setValue(CHARACTERISTIC[1], csticName);
			String dependencyName = nameProvider.getName(dependency);
			dependenciesTable.setValue(DEPENDENCY, dependencyName);
		}
		execute(function, monitor, "Persisting dependencies for cstic " + csticName);
		commit(monitor);
		return function;
	}
	
	/**
	 * Reads global available dependencies.
	 */
	public JCoFunction CARD_DEPENDENCY_READ(String dependencyName, IProgressMonitor monitor, List<Option> objectOptions, List<Option> modelOptions) throws JCoException {
		JCoFunction function = getJCoFunction("CARD_DEPENDENCY_READ", monitor);
		JCoParameterList ipl = function.getImportParameterList();
		ipl.setValue("DEPENDENCY", dependencyName);
		
		handleOptions(objectOptions, modelOptions, ipl, "CHANGE_NO", "DATE");
		
		// if the following flags are not checked, then the function performs just an existence check
		ipl.setValue("FL_WITH_BASIC_DATA", SELECTED);
		ipl.setValue("FL_WITH_DESCRIPTION", SELECTED);
		ipl.setValue("FL_WITH_DOCUMENTATION", SELECTED);
		ipl.setValue("FL_WITH_SOURCE", SELECTED);
		
		execute(function, monitor, dependencyName);
		return function;
	}
	
	/**
	 * Reads dependency entries for a characteristic.
	 */
	public JCoFunction CARD_CHAR_READ_ALLOC(String csticName, IProgressMonitor monitor, List<Option> objectOptions, List<Option> modelOptions) throws JCoException {
		JCoFunction function = getJCoFunction("CARD_CHAR_READ_ALLOC", monitor);
		JCoParameterList ipl = function.getImportParameterList();
		
		ipl.setValue(CHARACTERISTIC[0], csticName);
		ipl.setValue("LIST_ALL_GLOBL", JCoFunctionPerformer.SELECTED);
		ipl.setValue("LIST_ALL_LOCAL", JCoFunctionPerformer.SELECTED);
				
		handleOptions(modelOptions, objectOptions, ipl, "CHANGE_NO", "DATE");
		execute(function, monitor, new StringBuffer(csticName).toString());
		return function;
	}
	
	/**
	 * Reads dependency entries for a characteristic value.
	 */
	public JCoFunction CARD_CHAR_VAL_READ_ALLOC(String csticName, String valueName, IProgressMonitor monitor, List<Option> objectOptions, List<Option> modelOptions) throws JCoException {
		JCoFunction function = getJCoFunction("CARD_CHAR_VAL_READ_ALLOC", monitor);
		JCoParameterList ipl = function.getImportParameterList();
		ipl.setValue(CHARACTERISTIC[0], csticName);
		ipl.setValue("LIST_ALL_GLOBL", JCoFunctionPerformer.SELECTED);
		ipl.setValue("LIST_ALL_LOCAL", JCoFunctionPerformer.SELECTED);
		ipl.setValue(VALUE, valueName);
		
		handleOptions(modelOptions, objectOptions, ipl, "CHANGE_NO", "DATE");
		execute(function, monitor, new StringBuffer(csticName).append(".").append(valueName).toString());
		return function;
	}
}
