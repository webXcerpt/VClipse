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
package org.vclipse.bapi.actions.interfacedesign;

import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.ecore.resource.Resource;
import org.vclipse.bapi.actions.BAPIUtils;
import org.vclipse.bapi.actions.IBAPIActionRunner;
import org.vclipse.vcml.utils.DescriptionHandler;
import org.vclipse.vcml.utils.VcmlUtils;
import org.vclipse.vcml.vcml.Characteristic;
import org.vclipse.vcml.vcml.CharacteristicGroup;
import org.vclipse.vcml.vcml.InterfaceDesign;
import org.vclipse.vcml.vcml.Language;
import org.vclipse.vcml.vcml.Option;
import org.vclipse.vcml.vcml.VCObject;

import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoParameterList;
import com.sap.conn.jco.JCoTable;

public class InterfaceDesignCreateChangeActionHandler extends BAPIUtils implements IBAPIActionRunner<InterfaceDesign> {

	public boolean isEnabled(InterfaceDesign object) {
		return isConnected() && hasBody(object);
	}

	public void run(InterfaceDesign object, Resource resource, IProgressMonitor monitor, Map<String, VCObject> seenObjects, List<Option> options) throws JCoException {
		JCoFunction function = getJCoFunction("BAPI_UI_SAVEM", monitor);
		JCoParameterList tpl = function.getTableParameterList();

		JCoTable designname = tpl.getTable("DESIGNNAME");
		designname.appendRow();
		designname.setValue("DESIGNNAME", object.getName());
		
		JCoTable charGroups = tpl.getTable("CHARGROUPS");
		final JCoTable charGroupsLang = tpl.getTable("CHARGROUPSLANG");
		JCoTable chars = tpl.getTable("CHARS");
			
		for (CharacteristicGroup group : object.getCharacteristicGroups()) {
			charGroups.appendRow();
			charGroups.setValue("DESIGNNAME", object.getName());
			final String groupName = group.getName();
			charGroups.setValue("GROUP_NAME", groupName);
			new DescriptionHandler() {
				@Override
				public void handleSingleDescription(Language language, String value) {
					charGroupsLang.appendRow();
					charGroupsLang.setValue("GROUP_NAME", groupName);
					charGroupsLang.setValue("GROUP_TEXT", value);
					charGroupsLang.setValue("LANGUAGE_INT", VcmlUtils.getLanguageCharacter(language));
					charGroupsLang.setValue("LANGUAGE_ISO", language.toString());
				}
			}.handleDescription(group.getDescription());
			for (Characteristic cstic : group.getCharacteristics()) {
				chars.appendRow();
				chars.setValue("GROUP_NAME", groupName);
				chars.setValue("DESCR_OR_VALUE", "2");
				chars.setValue("NAME_CHAR", cstic.getName());
				// chars.setValue("LINE", arg1);
			}
		}
			
		execute(function, monitor, object.getName());
		if (processReturnStructureTable(function)) {
			commit(monitor);

		}
	}
	


}
