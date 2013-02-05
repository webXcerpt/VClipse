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

import org.eclipse.core.runtime.IProgressMonitor;
import org.vclipse.vcml.vcml.Option;

import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoParameterList;

/**
 *
 */
public class JCoFunctionPerformer extends BAPIUtils {

	public static final String SELECTED = "X";
	
	/**
	 * Values
	 */
	public static final String CHARACTERISTIC = "CHARACTERISTIC";
	public static final String DEPENDENCY = "DEPENDENCY";
	public static final String DEP_TYPE = "DEP_TYPE";
	public static final String DEPENDENCY_DATA = "DEPENDENCY_DATA";
	
	/**
	 * Tables
	 */
	public static final String DEP_ASSIGN = "DEP_ASSIGN";
	
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
		
		ipl.setValue(JCoFunctionPerformer.CHARACTERISTIC, csticName);
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
		ipl.setValue(JCoFunctionPerformer.CHARACTERISTIC, csticName);
		ipl.setValue("LIST_ALL_GLOBL", JCoFunctionPerformer.SELECTED);
		ipl.setValue("LIST_ALL_LOCAL", JCoFunctionPerformer.SELECTED);
		ipl.setValue("VALUE", valueName);
		
		handleOptions(modelOptions, objectOptions, ipl, "CHANGE_NO", "DATE");
		execute(function, monitor, new StringBuffer(csticName).append(".").append(valueName).toString());
		return function;
	}
}
