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
package org.vclipse.vcml.ui.actions.varianttable;

import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.EList;
import org.vclipse.vcml.ui.actions.BAPIUtils;
import org.vclipse.vcml.ui.actions.characteristic.CharacteristicReader;
import org.vclipse.vcml.utils.VCMLProxyFactory;
import org.vclipse.vcml.utils.VcmlUtils;
import org.vclipse.vcml.vcml.Characteristic;
import org.vclipse.vcml.vcml.Model;
import org.vclipse.vcml.vcml.VariantTable;
import org.vclipse.vcml.vcml.VariantTableArgument;

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
	
	public VariantTable read(String variantTableName, Model vcmlModel, IProgressMonitor monitor, Set<String> seenObjects, boolean recurse) throws JCoException {
		if (!seenObjects.add("VariantTable/" + variantTableName)) {
			return null;
		}
		VariantTable object = VCML.createVariantTable();
		object.setName(variantTableName);
		vcmlModel.getObjects().add(object);
		JCoFunction function = getJCoFunction("CARD_TABLE_READ_STRUCTURE", monitor);
		JCoParameterList ipl = function.getImportParameterList();
		ipl.setValue("VAR_TAB", variantTableName);
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
					cstic = csicReader.read(csticName, vcmlModel, monitor, seenObjects, recurse);
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
		} catch (AbapException e) {
			handleAbapException(e);
		}
		return object;
	}

}
