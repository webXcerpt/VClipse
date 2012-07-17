/*******************************************************************************
 * Copyright (c) 2010 webXcerpt Software GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     webXcerpt Software GmbH - initial creator
 ******************************************************************************/
package org.vclipse.bapi.actions.variantfunction;

import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.ecore.resource.Resource;
import org.vclipse.bapi.actions.BAPIUtils;
import org.vclipse.bapi.actions.IBAPIActionRunner;
import org.vclipse.vcml.vcml.Language;
import org.vclipse.vcml.vcml.Option;
import org.vclipse.vcml.vcml.VariantFunction;
import org.vclipse.vcml.vcml.VariantFunctionArgument;
import org.vclipse.vcml.utils.DescriptionHandler;
import org.vclipse.vcml.utils.VcmlUtils;

import com.sap.conn.jco.AbapException;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoParameterList;
import com.sap.conn.jco.JCoTable;

public class VariantFunctionCreateChangeActionHandler extends BAPIUtils implements IBAPIActionRunner<VariantFunction>{

	@Override
	public boolean isEnabled(VariantFunction object) {
		return isConnected() && hasBody(object);
	}

	@Override
	public void run(VariantFunction object, Resource resource, IProgressMonitor monitor, Set<String> seenObjects, List<Option> options) throws JCoException {
		final String name = object.getName();
		beginTransaction();
		JCoFunction function = getJCoFunction("CAMA_FUNCTION_MAINTAIN", monitor);
		// TODO insert ipl
		JCoParameterList tpl = function.getTableParameterList();
		JCoTable varFunctionBasicData = tpl.getTable("VAR_FUNCTION_BASIC_DATA");
		varFunctionBasicData.appendRow();
		varFunctionBasicData.setValue("VFUNC_NAME", name);
		varFunctionBasicData.setValue("VFUNC_STATUS", VcmlUtils.createIntFromStatusVFT(object.getStatus())); // TODO is this the correct status translation?
		varFunctionBasicData.setValue("VFUNC_GROUP", object.getGroup());
		final JCoTable varFunctionDescriptions = tpl.getTable("VAR_FUNCTION_DESCRIPTIONS");
		new DescriptionHandler() {
			@Override
			public void handleSingleDescription(Language language, String value) {
				varFunctionDescriptions.appendRow();
				varFunctionDescriptions.setValue("VFUNC_NAME", name);
				varFunctionDescriptions.setValue("DESCRIPT", value);
				varFunctionDescriptions.setValue("LANGUAGE_ISO", language.toString());
			}
		}.handleDescription(object.getDescription());
		JCoTable varFunctionParameters = tpl.getTable("VAR_FUNCTION_PARAMETERS");
		JCoTable varFunctionAltInput = tpl.getTable("VAR_FUNCTION_ALT_INPUT");
		for (VariantFunctionArgument argument : object.getArguments()) {
			varFunctionParameters.appendRow();
			varFunctionParameters.setValue("VFUNC_NAME", name);
			varFunctionParameters.setValue("CHARACT", argument.getCharacteristic().getName());
			varFunctionAltInput.appendRow();
			varFunctionAltInput.setValue("VFUNC_NAME", name);
			varFunctionAltInput.setValue("VFUNC_ALT", "0001");
			varFunctionAltInput.setValue("CHARACT", argument.getCharacteristic().getName());
			varFunctionAltInput.setValue("VFUNC_INPUT", argument.isIn() ? "X" : "");
		}
		try {
			execute(function, monitor, name);
			endTransaction();
		} catch (AbapException e) {
			handleAbapException(e);
		}
	}

}	
