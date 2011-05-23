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

import java.util.HashMap;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.WrappedException;
import org.eclipse.emf.ecore.resource.Resource;
import org.vclipse.vcml.vcml.Characteristic;
import org.vclipse.vcml.vcml.CharacteristicValue;
import org.vclipse.vcml.vcml.Description;
import org.vclipse.vcml.vcml.FormattedDocumentationBlock;
import org.vclipse.vcml.vcml.Language;
import org.vclipse.vcml.vcml.Model;
import org.vclipse.vcml.vcml.MultiLanguageDescription;
import org.vclipse.vcml.vcml.MultiLanguageDescriptions;
import org.vclipse.vcml.vcml.MultipleLanguageDocumentation;
import org.vclipse.vcml.vcml.MultipleLanguageDocumentation_LanguageBlock;
import org.vclipse.vcml.vcml.NumericCharacteristicValue;
import org.vclipse.vcml.vcml.NumericType;
import org.vclipse.vcml.vcml.SymbolicType;
import org.vclipse.vcml.utils.DescriptionHandler;
import org.vclipse.vcml.utils.VCMLUtils;

import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoParameterList;
import com.sap.conn.jco.JCoStructure;
import com.sap.conn.jco.JCoTable;

public class CharacteristicReader extends BAPIUtils {

	public Characteristic read(String csticName, Resource resource, final IProgressMonitor monitor, Set<String> seenObjects, boolean recurse) throws JCoException {
		if (!seenObjects.add("Characteristic#" + csticName))
			return null;
		final Characteristic object = VCMLFACTORY.createCharacteristic();
		((Model)resource.getContents().get(0)).getObjects().add(object);
		JCoFunction function = getJCoFunction("BAPI_CHARACT_GETDETAIL", monitor);
		function.getImportParameterList().setValue("CHARACTNAME", csticName);
		execute(function, monitor, csticName);
		if (processReturnTable(function)) {
			JCoParameterList epl = function.getExportParameterList();
			JCoParameterList tpl = function.getTableParameterList();

			JCoStructure charactDetail = epl.getStructure("CHARACTDETAIL");
			object.setName(charactDetail.getString("CHARACT_NAME"));
			String dataType = charactDetail.getString("DATA_TYPE");
			if ("NUM".equals(dataType)) {
				NumericType type = VCMLFACTORY.createNumericType();
				object.setType(type);
				type.setNumberOfChars(charactDetail.getInt("LENGTH"));
				type.setDecimalPlaces(charactDetail.getInt("DECIMALS"));
				type.setNegativeValuesAllowed("X".equals(charactDetail.getString("WITH_SIGN")));
				type.setIntervalValuesAllowed("X".equals(charactDetail.getString("INTERVAL_ALLOWED")));
				type.setUnit(nullIfEmpty(charactDetail.getString("UNIT_OF_MEASUREMENT")));
				EList<NumericCharacteristicValue> values = type.getValues();
				JCoTable charactValuesNum = tpl.getTable("CHARACTVALUESNUM");
				if (charactValuesNum!=null) {
					for (int i = 0; i < charactValuesNum.getNumRows(); i++) {
						charactValuesNum.setRow(i);
						NumericCharacteristicValue value = VCMLFACTORY.createNumericCharacteristicValue();
						value.setName(charactValuesNum.getString("VALUE_FROM"));
						values.add(value);
					}
				}
			} else if ("CHAR".equals(dataType)) {
				SymbolicType type = VCMLFACTORY.createSymbolicType();
				object.setType(type);
				type.setNumberOfChars(charactDetail.getInt("LENGTH"));
				type.setCaseSensitive("X".equals(charactDetail.getValue("CASE_SENSITIV")));
				EList<CharacteristicValue> values = type.getValues();
				JCoTable charactValuesDescr = tpl.getTable("CHARACTVALUESDESCR");
				if (charactValuesDescr!=null) {
					HashMap<String, CharacteristicValue> seenValues = new HashMap<String, CharacteristicValue>(); // for grouping the descriptions by value
					for (int i = 0; i < charactValuesDescr.getNumRows(); i++) {
						charactValuesDescr.setRow(i);
						String name = charactValuesDescr.getString("VALUE_CHAR");
						CharacteristicValue value = seenValues.get(name);
						if (value==null) {
							value = VCMLFACTORY.createCharacteristicValue();
							values.add(value);
							value.setName(name);
							value.setDescription(VCMLFACTORY.createMultiLanguageDescriptions());
							seenValues.put(name, value);
						}
						MultiLanguageDescriptions multiLanguageDescriptions = (MultiLanguageDescriptions)value.getDescription();
						EList<MultiLanguageDescription> descriptions = multiLanguageDescriptions.getDescriptions();
						MultiLanguageDescription multiLanguageDescription = VCMLFACTORY.createMultiLanguageDescription();
						multiLanguageDescription.setLanguage(VCMLUtils.getLanguageByISOString(charactValuesDescr.getString("LANGUAGE_ISO")));
						multiLanguageDescription.setValue(charactValuesDescr.getString("DESCRIPTION"));
						descriptions.add(multiLanguageDescription);
					}
				}
				for (final CharacteristicValue value : values) {
					value.setDescription(simplifyDescription(value.getDescription()));
					MultipleLanguageDocumentation multipleLanguageDocumentation = VCMLFACTORY.createMultipleLanguageDocumentation();
					final EList<MultipleLanguageDocumentation_LanguageBlock> languageBlocks = multipleLanguageDocumentation.getLanguageblocks();
					final Description description = value.getDescription();
					if (description!=null) {
						new DescriptionHandler() {
							@Override
							public void handleSingleDescription(Language language, String descriptionValue) {
								// problem: this extracts only the documentation for languages with description
								// TODO error handling, handle case of no documentation available
								try {
									MultipleLanguageDocumentation_LanguageBlock languageBlock = getLanguageBlock(object, value, language, monitor);
									if (!languageBlock.getFormattedDocumentationBlocks().isEmpty())
										languageBlocks.add(languageBlock);
								} catch (JCoException e) {
									throw new WrappedException(e);
								}
							}
						}.handleDescription(description);
						if (!languageBlocks.isEmpty())
							value.setDocumentation(multipleLanguageDocumentation);
					}
				}

			} else {
				err.println("error: illegal/unknown data type " + dataType);
			}
			object.setMultiValue("M".equals(charactDetail.getString("VALUE_ASSIGNMENT")));
			object.setRequired("X".equals(charactDetail.getString("ENTRY_REQUIRED")));
			object.setNoDisplay("X".equals(charactDetail.getString("NO_DISPLAY")));
			object.setNotReadyForInput("X".equals(charactDetail.getString("NO_ENTRY")));
			object.setAdditionalValues("X".equals(charactDetail.getString("ADDITIONAL_VALUES")));
			object.setRestrictable("R".equals(charactDetail.getString("VALUE_ASSIGNMENT")));
			object.setDisplayAllowedValues("X".equals(charactDetail.getString("DISPLAY_VALUES")));
			object.setStatus(VCMLUtils.createStatusFromInt(charactDetail.getInt("STATUS")));
			object.setGroup(nullIfEmpty(charactDetail.getString("CHARACT_GROUP")));
			JCoTable charactDescr = tpl.getTable("CHARACTDESCR");
			if (charactDescr!=null && charactDescr.getNumRows()>0) {
				MultiLanguageDescriptions multiLanguageDescriptions = VCMLFACTORY.createMultiLanguageDescriptions();
				EList<MultiLanguageDescription> descriptions = multiLanguageDescriptions.getDescriptions();
				MultipleLanguageDocumentation multipleLanguageDocumentation = VCMLFACTORY.createMultipleLanguageDocumentation();
				EList<MultipleLanguageDocumentation_LanguageBlock> languageBlocks = multipleLanguageDocumentation.getLanguageblocks();
				for (int i = 0; i < charactDescr.getNumRows(); i++) {
					charactDescr.setRow(i);
					MultiLanguageDescription multiLanguageDescription = VCMLFACTORY.createMultiLanguageDescription();
					Language language = VCMLUtils.getLanguageByISOString(charactDescr.getString("LANGUAGE_ISO"));
					multiLanguageDescription.setLanguage(language);
					multiLanguageDescription.setValue(charactDescr.getString("DESCRIPTION"));
					descriptions.add(multiLanguageDescription);
					// problem: this extracts only the documentation for languages with description
					// currently unknown whether there might be a documentation without description in SAP
					MultipleLanguageDocumentation_LanguageBlock languageBlock = getLanguageBlock(object, null, language, monitor);
					if (!languageBlock.getFormattedDocumentationBlocks().isEmpty())
						languageBlocks.add(languageBlock);
				}
				if (!descriptions.isEmpty())
					object.setDescription(simplifyDescription(multiLanguageDescriptions));
				if (!languageBlocks.isEmpty())
					object.setDocumentation(multipleLanguageDocumentation);
			}
		}
		return object;
	}

	private MultipleLanguageDocumentation_LanguageBlock getLanguageBlock(Characteristic cstic, CharacteristicValue value, Language language, IProgressMonitor monitor) throws JCoException {
		JCoFunction function = getJCoFunction("BAPI_CHARACT_GETLONGTEXT", monitor);
		JCoParameterList ipl = function.getImportParameterList();
		ipl.setValue("CHARACTNAME", cstic.getName());
		if (value!=null)
			ipl.setValue("VALUE_CHAR", value.getName());
		ipl.setValue("LANGUAGE_ISO", language.toString());
		execute(function, monitor, cstic.getName() + " " + (value==null ? "" : value.getName() + " ") + language);
		MultipleLanguageDocumentation_LanguageBlock lb = VCMLFACTORY.createMultipleLanguageDocumentation_LanguageBlock();
		lb.setLanguage(language);
		EList<FormattedDocumentationBlock> fdbs = lb.getFormattedDocumentationBlocks();
		JCoParameterList tpl = function.getTableParameterList();
		JCoTable longText = tpl.getTable("LONGTEXT");
		for (int i = 0; i < longText.getNumRows(); i++) {
			longText.setRow(i);
			FormattedDocumentationBlock fdb = VCMLFACTORY.createFormattedDocumentationBlock();
			fdbs.add(fdb);
			fdb.setValue(longText.getString("TDLINE"));
			fdb.setFormat(nullIfEquals(VCMLUtils.DEFAULT_FORMAT, longText.getString("TDFORMAT")));
		}
		return lb;
	}
}
