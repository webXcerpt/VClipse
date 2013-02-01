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
import org.eclipse.emf.common.util.EList;
import org.vclipse.bapi.actions.BAPIUtils;
import org.vclipse.bapi.actions.characteristic.CharacteristicReader;
import org.vclipse.bapi.actions.varianttable.content.VariantTableContentReader;
import org.vclipse.vcml.mm.VCMLProxyFactory;
import org.vclipse.vcml.utils.VcmlUtils;
import org.vclipse.vcml.vcml.Characteristic;
import org.vclipse.vcml.vcml.Option;
import org.vclipse.vcml.vcml.VCObject;
import org.vclipse.vcml.vcml.VariantTable;
import org.vclipse.vcml.vcml.VariantTableArgument;
import org.vclipse.vcml.vcml.VcmlModel;

import com.google.inject.Inject;
import com.sap.conn.jco.AbapException;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoParameterList;
import com.sap.conn.jco.JCoStructure;
import com.sap.conn.jco.JCoTable;

public class VariantTableReader extends BAPIUtils {

	@Inject
	private CharacteristicReader csicReader;
	
	@Inject
	private VariantTableContentReader contentReader;
	
	public VariantTable read(String variantTableName, VcmlModel vcmlModel, IProgressMonitor monitor, Map<String, VCObject> seenObjects, List<Option> globalOptions, boolean recurse) throws JCoException {
		if(variantTableName == null || monitor.isCanceled()) {
			return null;
		}
		String id = "VariantTable/" + variantTableName.toUpperCase();
		VCObject seenObject = seenObjects.get(id);
		if (seenObject instanceof VariantTable) {
			return (VariantTable)seenObject;
		}
		VariantTable object = VCML.createVariantTable();
		seenObjects.put(id, object);
		object.setName(variantTableName);
		vcmlModel.getObjects().add(object);
		JCoFunction function = getJCoFunction("CARD_TABLE_READ_STRUCTURE", monitor);
		JCoParameterList ipl = function.getImportParameterList();
		
		handleOptions(object.getOptions(), globalOptions, ipl, "CHANGE_NO", "DATE");
		
		ipl.setValue("VAR_TAB", variantTableName.toUpperCase());
		try {
			execute(function, monitor, variantTableName);
			JCoParameterList epl = function.getExportParameterList();
			JCoStructure basicData = epl.getStructure("BASIC_DATA");
			object.setStatus(VcmlUtils.createStatusFromIntVFT(basicData.getInt("STATUS")));
			object.setGroup(nullIfEmpty(basicData.getString("VTGROUP")));
			JCoParameterList tpl = function.getTableParameterList();
			object.setDescription(readDescription(tpl.getTable("DESCRIPTIONS"), "LANGUAGE_ISO", "LANGUAGE", "DESCRIPT"));
			JCoTable characteristics = tpl.getTable("CHARACTERISTICS");
			JCoTable valueAssignmentAlt = tpl.getTable("VALUE_ASSIGNMENT_ALT");
			EList<VariantTableArgument> arguments = object.getArguments();
			for (int i = 0; i < characteristics.getNumRows(); i++) {
				characteristics.setRow(i);
				String csticName = characteristics.getString("CHARACT");
				// TODO move this read / proxy mechanism to CharacteristicReader
				Characteristic cstic = null;
				if (recurse) {
					if(monitor.isCanceled()) {
						return null;
					}
					cstic = csicReader.read(csticName, vcmlModel, monitor, seenObjects, globalOptions, recurse);
				}
				if (cstic==null) {
					cstic = VCMLProxyFactory.createCharacteristicProxy(vcmlModel.eResource(), csticName);
				}
				VariantTableArgument variantTableArgument = VCML.createVariantTableArgument();
				variantTableArgument.setCharacteristic(cstic);
				arguments.add(variantTableArgument);
				// cstic is input parameter if it occurs in "alt input"
				for (int j = 0; j < valueAssignmentAlt.getNumRows(); j++) {
					valueAssignmentAlt.setRow(j);
					if (csticName.equals(valueAssignmentAlt.getString("CHARACT"))) {
						variantTableArgument.setKey(true);
					}
				}
			}
			if(recurse) {
				contentReader.read(variantTableName, vcmlModel, monitor, seenObjects, globalOptions, false);
			}
		} catch (AbapException e) {
			handleAbapException(e);
		}
		return object;
	}

}
