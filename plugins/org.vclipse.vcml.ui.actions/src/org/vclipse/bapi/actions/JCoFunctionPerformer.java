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
	
	public static final String CHARACTERISTIC = "CHARACTERISTIC";
	
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
	 * Reads a dependency key for a characteristic value.
	 */
	public JCoFunction CARD_CHAR_VAL_READ_ALLOC(String csticName, String valueName, IProgressMonitor monitor, List<Option> objectOptions, List<Option> modelOptions) throws JCoException {
		JCoFunction keysValueDependencies = getJCoFunction("CARD_CHAR_VAL_READ_ALLOC", monitor);
		JCoParameterList ipl = keysValueDependencies.getImportParameterList();
		
		ipl.setValue(JCoFunctionPerformer.CHARACTERISTIC, csticName);
		ipl.setValue("LIST_ALL_GLOBL", JCoFunctionPerformer.SELECTED);
		ipl.setValue("LIST_ALL_LOCAL", JCoFunctionPerformer.SELECTED);
		ipl.setValue("VALUE", valueName);
		
		execute(keysValueDependencies, monitor, new StringBuffer(csticName).append(".").append(valueName).toString());
		return keysValueDependencies;
	}
}
