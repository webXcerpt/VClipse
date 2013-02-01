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
package org.vclipse.bapi.actions.variantfunction;

import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.EList;
import org.vclipse.bapi.actions.BAPIUtils;
import org.vclipse.bapi.actions.characteristic.CharacteristicReader;
import org.vclipse.vcml.mm.VCMLProxyFactory;
import org.vclipse.vcml.utils.VcmlUtils;
import org.vclipse.vcml.vcml.Characteristic;
import org.vclipse.vcml.vcml.Option;
import org.vclipse.vcml.vcml.VCObject;
import org.vclipse.vcml.vcml.VariantFunction;
import org.vclipse.vcml.vcml.VariantFunctionArgument;
import org.vclipse.vcml.vcml.VcmlModel;

import com.google.inject.Inject;
import com.sap.conn.jco.AbapException;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoParameterList;
import com.sap.conn.jco.JCoStructure;
import com.sap.conn.jco.JCoTable;

public class VariantFunctionReader extends BAPIUtils {

	@Inject
	private CharacteristicReader csticReader;
	
	public VariantFunction read(String variantFunctionName, VcmlModel vcmlModel, IProgressMonitor monitor, Map<String, VCObject> seenObjects, List<Option> globalOptions, boolean recurse) throws JCoException {
		if(variantFunctionName == null || monitor.isCanceled()) {
			return null;
		}
		String id = "VariantFunction/" + variantFunctionName.toUpperCase();
		VCObject seenObject = seenObjects.get(id);
		if (seenObject instanceof VariantFunction) {
			return (VariantFunction)seenObject;
		}
		VariantFunction object = VCML.createVariantFunction();
		seenObjects.put(id, object);
		object.setName(variantFunctionName);
		vcmlModel.getObjects().add(object);
		JCoFunction function = getJCoFunction("CARD_FUNCTION_READ", monitor);
		JCoParameterList ipl = function.getImportParameterList();
		ipl.setValue("FUNCTION_NAME", variantFunctionName);
		
		handleOptions(object.getOptions(), globalOptions, ipl, null, "DATE");
		
		try {
			execute(function, monitor, variantFunctionName);
			JCoParameterList epl = function.getExportParameterList();
			JCoStructure varFunctionBasicData = epl.getStructure("VAR_FUNCTION_BASIC_DATA");
			object.setStatus(VcmlUtils.createStatusFromIntVFT(varFunctionBasicData.getInt("VFUNC_STATUS")));
			object.setGroup(nullIfEmpty(varFunctionBasicData.getString("VFUNC_GROUP")));
			JCoParameterList tpl = function.getTableParameterList();
			object.setDescription(readDescription(tpl.getTable("VAR_FUNCTION_DESCRIPTIONS"), "LANGUAGE_ISO", null, "DESCRIPT"));
			JCoTable varFunctionParameters = tpl.getTable("VAR_FUNCTION_PARAMETERS");
			JCoTable varFunctionAltInput = tpl.getTable("VAR_FUNCTION_ALT_INPUT");
			EList<VariantFunctionArgument> arguments = object.getArguments();
			for (int i = 0; i < varFunctionParameters.getNumRows(); i++) {
				varFunctionParameters.setRow(i);
				String csticName = varFunctionParameters.getString("CHARACT");
				// TODO move this read / proxy mechanism to CharacteristicReader
				Characteristic cstic = null;
				if (recurse) {
					if(monitor.isCanceled()) {
						return null;
					}
					cstic = csticReader.read(csticName, vcmlModel, monitor, seenObjects, globalOptions, recurse);
				}
				if (cstic==null) {
					cstic = VCMLProxyFactory.createCharacteristicProxy(vcmlModel.eResource(), csticName);
				}
				VariantFunctionArgument variantFunctionArgument = VCML.createVariantFunctionArgument();
				variantFunctionArgument.setCharacteristic(cstic);
				arguments.add(variantFunctionArgument);
				// cstic is input parameter if it occurs in "alt input"
				for (int j = 0; j < varFunctionAltInput.getNumRows(); j++) {
					varFunctionAltInput.setRow(j);
					if (csticName.equals(varFunctionAltInput.getString("CHARACT"))) {
						variantFunctionArgument.setIn(true);
					}
				}
			}
		} catch (AbapException e) {
			handleAbapException(e);
		}
		return object;
	}
}
