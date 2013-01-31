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
package org.vclipse.bapi.actions.varianttable;

import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.ecore.resource.Resource;
import org.vclipse.bapi.actions.BAPIUtils;
import org.vclipse.bapi.actions.IBAPIActionRunner;
import org.vclipse.vcml.utils.DescriptionHandler;
import org.vclipse.vcml.utils.VcmlUtils;
import org.vclipse.vcml.vcml.Language;
import org.vclipse.vcml.vcml.Option;
import org.vclipse.vcml.vcml.VCObject;
import org.vclipse.vcml.vcml.VariantTable;
import org.vclipse.vcml.vcml.VariantTableArgument;

import com.sap.conn.jco.AbapException;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoParameterList;
import com.sap.conn.jco.JCoTable;

public class VariantTableCreateChangeActionHandler extends BAPIUtils implements IBAPIActionRunner<VariantTable>{

	@Override
	public boolean isEnabled(VariantTable object) {
		return isConnected() && hasBody(object);
	}

	@Override
	public void run(VariantTable object, Resource resource, IProgressMonitor monitor, Map<String, VCObject> seenObjects, List<Option> options) throws JCoException {
		final String name = object.getName();
		beginTransaction();
		JCoFunction function = getJCoFunction("CAMA_TABLE_MAINTAIN_STRUCTURE", monitor);
		// TODO insert ipl
		JCoParameterList tpl = function.getTableParameterList();
		JCoTable varTabBasicData = tpl.getTable("VAR_TAB_BASIC_DATA");
		varTabBasicData.appendRow();
		varTabBasicData.setValue("VAR_TAB", name.toUpperCase());
		varTabBasicData.setValue("STATUS", VcmlUtils.createIntFromStatusVFT(object.getStatus())); // TODO is this the correct status translation?
		varTabBasicData.setValue("VTGROUP", object.getGroup());
		final JCoTable varTabDescriptions = tpl.getTable("VAR_TAB_DESCRIPTIONS");
		new DescriptionHandler() {
			@Override
			public void handleSingleDescription(Language language, String value) {
				varTabDescriptions.appendRow();
				varTabDescriptions.setValue("VAR_TAB", name);
				varTabDescriptions.setValue("DESCRIPT", value);
				varTabDescriptions.setValue("LANGUAGE", VcmlUtils.getLanguageCharacter(language));
				varTabDescriptions.setValue("LANGUAGE_ISO", language.toString());
			}
		}.handleDescription(object.getDescription());
		JCoTable varTabCharacteristics = tpl.getTable("VAR_TAB_CHARACTERISTICS");
		JCoTable varTabValueAssignmentAlt = tpl.getTable("VAR_TAB_VALUE_ASSIGNMENT_ALT");
		for (VariantTableArgument argument : object.getArguments()) {
			varTabCharacteristics.appendRow();
			varTabCharacteristics.setValue("VAR_TAB", name);
			varTabCharacteristics.setValue("CHARACT", argument.getCharacteristic().getName());
			varTabValueAssignmentAlt.appendRow();
			varTabValueAssignmentAlt.setValue("VARTABLE", name);
			varTabValueAssignmentAlt.setValue("VL_ASSG_NO", "0001");
			varTabValueAssignmentAlt.setValue("CHARACT", argument.getCharacteristic().getName());
			varTabValueAssignmentAlt.setValue("KEY_INDIC", argument.isKey() ? "X" : "");
		}
		try {
			execute(function, monitor, name);
			endTransaction();
		} catch (AbapException e) {
			handleAbapException(e);
		}
	}

}	
