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
package org.vclipse.vcml.ui.actions.intefacedesign;

import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.EList;
import org.eclipse.xtext.util.Strings;
import org.vclipse.vcml.ui.actions.BAPIUtils;
import org.vclipse.vcml.ui.actions.characteristic.CharacteristicReader;
import org.vclipse.vcml.utils.VCMLProxyFactory;
import org.vclipse.vcml.utils.VcmlUtils;
import org.vclipse.vcml.vcml.Characteristic;
import org.vclipse.vcml.vcml.CharacteristicGroup;
import org.vclipse.vcml.vcml.InterfaceDesign;
import org.vclipse.vcml.vcml.Language;
import org.vclipse.vcml.vcml.Model;
import org.vclipse.vcml.vcml.MultiLanguageDescription;
import org.vclipse.vcml.vcml.MultiLanguageDescriptions;

import com.google.inject.Inject;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoParameterList;
import com.sap.conn.jco.JCoTable;

public class InterfaceDesignReader extends BAPIUtils {

	@Inject
	private CharacteristicReader csticReader;
	
	public InterfaceDesign read(String interfaceDesignName, Model model, IProgressMonitor monitor, Set<String> seenObjects, boolean recurse) throws JCoException {
		if (!seenObjects.add("InterfaceDesign/" + interfaceDesignName)) {
			return null;
		}
		if(monitor.isCanceled()) {
			return null;
		}
		InterfaceDesign object = VCML.createInterfaceDesign();
		object.setName(interfaceDesignName);
		model.getObjects().add(object);

			/** 
	 * documentation of BAPI: http://abap.wikiprog.com/wiki/BAPI_UI_GETDETAIL
	 * 
	 * 
Name:   BAPI_UI_GETDETAIL
Input:
|------------------|
| PARAMETERS 'INPUT'
|------------------|
|DESIGNNAME        |
|------------------|
|012345678901234567|
|------------------|
|                  |
|------------------|

Changing:
null
Output:
|------|
| PARAMETERS 'OUTPUT'
|------|
|RETURN|
|------|
|      |
|------|
|RETURN|
|------|

Tables:
|----------|--------------|-----|------|----------|
| PARAMETERS 'TABLES'
|----------|--------------|-----|------|----------|
|CHARGROUPS|CHARGROUPSLANG|CHARS|FRAMES|FRAMESLANG|
|----------|--------------|-----|------|----------|
|          |              |     |      |          |
|----------|--------------|-----|------|----------|
|CHARGROUPS|CHARGROUPSLANG|CHARS|FRAMES|FRAMESLANG|
|----------|--------------|-----|------|----------|


===== RETURN =====
TYPE	E
ID	2C
NUMBER	008
MESSAGE	Oberfl�chendesign 'E8300004000012' nicht vorhanden.
LOG_NO	00000000000002249807
LOG_MSG_NO	000001
MESSAGE_V1	E8300004000012
MESSAGE_V2	
MESSAGE_V3	
MESSAGE_V4	
PARAMETER	
ROW	0
FIELD	
SYSTEM	

===== CHARGROUPS =====
INT_NUM_UI	0000000000
GROUP_NAME	
DESIGNNAME	
CREATED_BY	
CR_ON	null
CHANGED_BY	
CH_ON	null
COL_FROM	000
LINE_FROM	000
COL_TO	000
LINE_TO	000
CHAR_COMPRESS	
PUSHBUTTON	
TABSTRIP	
CHAR_SEQ	
SEQ_SALES	
SEQ_ENGINEERING	
PRINT_SALES	
PRINT_PURCHASE	
PRINT_ENGINEER	
===== CHARGROUPSLANG =====
INT_NUM_UI	0000000000
GROUP_NAME	
LANGUAGE_INT	
LANGUAGE_ISO	
GROUP_TEXT	
PUSHBUTTON_TEXT	
===== CHARS =====
INT_NUM_UI	0000000000
GROUP_NAME	
LINE	0000000000
DESCR_OR_VALUE	
START_ROW	0000000000
END_ROW	0000000000
LENGTH	0000000000
NAME_CHAR	
NEW_LINE	
===== FRAMES =====
INT_NUM_UI	0000000000
GROUP_NAME	
FRAME_NAME	
START_LINE	0000000000
START_ROW	0000000000
END_LINE	0000000000
END_ROW	0000000000
===== FRAMESLANG =====
INT_NUM_UI	0000000000
GROUP_NAME	
FRAME_NAME	
LANGUAGE_INT	
LANGUAGE_ISO	
FRAME_TEXT	

	 */
			JCoFunction function = getJCoFunction("BAPI_UI_GETDETAIL", monitor);	
			JCoParameterList ipl = function.getImportParameterList();
			ipl.setValue("DESIGNNAME", interfaceDesignName);
			execute(function, monitor, interfaceDesignName);
			if (processReturnStructure(function)) {
				JCoParameterList tpl = function.getTableParameterList();
				JCoTable charGroups = tpl.getTable("CHARGROUPS");
				JCoTable charGroupsLang = tpl.getTable("CHARGROUPSLANG");
				JCoTable chars = tpl.getTable("CHARS");
				List<CharacteristicGroup> characteristicGroups = object.getCharacteristicGroups();
				for (int g = 0; g < charGroups.getNumRows(); g++) {
					charGroups.setRow(g);
					// TODO sort groups by groupName
					String groupName = charGroups.getString("GROUP_NAME");
					CharacteristicGroup group = VCML.createCharacteristicGroup();
					group.setName(groupName);
					characteristicGroups.add(group);
					
					// read description
					MultiLanguageDescriptions multiLanguageDescriptions = VCML.createMultiLanguageDescriptions();
					EList<MultiLanguageDescription> descriptions = multiLanguageDescriptions.getDescriptions();
					for (int i = 0; i < charGroupsLang.getNumRows(); i++) {
						charGroupsLang.setRow(i);
						if (groupName.equals(charGroupsLang.getValue("GROUP_NAME"))) {
							MultiLanguageDescription multiLanguageDescription = VCML.createMultiLanguageDescription();
							Language language; 
							String languageISO = charGroupsLang.getString("LANGUAGE_ISO");
							if (Strings.isEmpty(languageISO)) {
								language = VcmlUtils.getLanguageByCharacter(charGroupsLang.getChar("LANGUAGE_INT"));
							} else {
								language = VcmlUtils.getLanguageByISOString(languageISO);
							}
							multiLanguageDescription.setLanguage(language);
							multiLanguageDescription.setValue(charGroupsLang.getString("GROUP_TEXT"));
							descriptions.add(multiLanguageDescription);
						}
					}
					group.setDescription(simplifyDescription(multiLanguageDescriptions));

					List<Characteristic> characteristics = group.getCharacteristics();
					// read characteristics
					for (int i = 0; i < chars.getNumRows(); i++) {
						chars.setRow(i);
						// TODO sort chars by LINE
						if (groupName.equals(chars.getValue("GROUP_NAME")) && "2".equals(chars.getValue("DESCR_OR_VALUE"))) {
							String csticName = chars.getString("NAME_CHAR");
							Characteristic cstic = null;
							if (recurse) {
								if(monitor.isCanceled()) {
									return null;
								}
								cstic = csticReader.read(csticName, model, monitor, seenObjects, recurse);
							}
							if (cstic==null) {
								cstic = VCMLProxyFactory.createCharacteristicProxy(model.eResource(), csticName);
							}
							characteristics.add(cstic);
						}
					}

				}

					/*
					
					
					if (true || "X".equals(charGroups.getValue("TABSTRIP"))) { // we have a tab and not a pushbutton
						for (int gl = 0; gl < charGroupsLang.getNumRows(); gl++) {
							charGroupsLang.setRow(gl);
							if (groupName.equals(charGroupsLang.getValue("GROUP_NAME"))) {
								err.println(groupName + "\t" + charGroupsLang.getValue("LANGUAGE_ISO") + "\t" + charGroupsLang.getValue("GROUP_TEXT"));
							}
						}
						for (int c = 0; c < chars.getNumRows(); c++) {
							chars.setRow(c);
							// TODO sort chars by LINE
							if (groupName.equals(chars.getValue("GROUP_NAME")) && "2".equals(chars.getValue("DESCR_OR_VALUE"))) {
								err.println("\t" + chars.getValue("LINE") + "\t" + chars.getValue("NAME_CHAR"));
							}
						}
					}
					*/

			}
		return object;
	}

	/*
00_CUSTOMER	DE	Kunde
00_CUSTOMER	EN	Customer
	0000000001	IP_VBAK_KUNNR
	0000000002	IP_VBPA_AG_LAND1
	0000000003	IP_COUNTRY_MANUAL
	0000000004	IP_CUSTOMER_TYPE
01_PRODUCTS	DE	Produkte
01_PRODUCTS	EN	Products
	0000000001	IP_PRODUCT_SELECTION
	0000000002	IP_PROD_COUNTER
02_CONTRACT	DE	Vertrag
02_CONTRACT	EN	Contract
	0000000001	IP_DURATION_TYPE
	0000000002	IP_DURATION
	0000000003	IP_DATE_START
	0000000004	IP_DATE_END
	0000000005	IP_BILLING_PERIOD
	0000000006	IP_PAYMENT_METHOD
99_MISC	DE	Diverses
99_MISC	EN	Misc.
	0000000001	IP_VKOND
	0000000002	IP_INVISIBLE
	0000000003	IP_NO_INPUT

	 * 
	 * 
	 */
	
	
	
}
