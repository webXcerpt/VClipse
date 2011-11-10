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
package org.vclipse.vcml.ui.actions;

import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.resource.Resource;
import org.vclipse.vcml.vcml.Characteristic;
import org.vclipse.vcml.vcml.Model;
import org.vclipse.vcml.vcml.VariantTable;
import org.vclipse.vcml.vcml.VariantTableArgument;
import org.vclipse.vcml.utils.VCMLProxyFactory;
import org.vclipse.vcml.utils.VcmlUtils;

import com.sap.conn.jco.AbapException;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoParameterList;
import com.sap.conn.jco.JCoStructure;
import com.sap.conn.jco.JCoTable;

public class VariantTableReader extends BAPIUtils {

	private static final CharacteristicReader CHARACTERISTIC_READER = new CharacteristicReader(); // must not be abstract
	
	public VariantTable read(String variantTableName, Resource resource, IProgressMonitor monitor, Set<String> seenObjects, boolean recurse) throws JCoException {
		if (!seenObjects.add("VariantTable#" + variantTableName))
			return null;
		VariantTable object = VCML.createVariantTable();
		object.setName(variantTableName);
		((Model)resource.getContents().get(0)).getObjects().add(object);
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
					cstic = CHARACTERISTIC_READER.read(csticName, resource, monitor, seenObjects, recurse);
				}
				if (cstic==null) {
					cstic = VCMLProxyFactory.createCharacteristicProxy(resource, csticName);
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
