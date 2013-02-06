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
package org.vclipse.bapi.actions.characteristic;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.WrappedException;
import org.vclipse.bapi.actions.BAPIUtils;
import org.vclipse.bapi.actions.characteristic.values.ReadCharacteristicDependency;
import org.vclipse.bapi.actions.characteristic.values.ReadCharacteristicValueDependency;
import org.vclipse.vcml.SAPFormattingUtility;
import org.vclipse.vcml.utils.DescriptionHandler;
import org.vclipse.vcml.utils.VcmlUtils;
import org.vclipse.vcml.vcml.Characteristic;
import org.vclipse.vcml.vcml.CharacteristicValue;
import org.vclipse.vcml.vcml.DateCharacteristicValue;
import org.vclipse.vcml.vcml.DateType;
import org.vclipse.vcml.vcml.Description;
import org.vclipse.vcml.vcml.FormattedDocumentationBlock;
import org.vclipse.vcml.vcml.Language;
import org.vclipse.vcml.vcml.MultiLanguageDescription;
import org.vclipse.vcml.vcml.MultiLanguageDescriptions;
import org.vclipse.vcml.vcml.MultipleLanguageDocumentation;
import org.vclipse.vcml.vcml.MultipleLanguageDocumentation_LanguageBlock;
import org.vclipse.vcml.vcml.NumericCharacteristicValue;
import org.vclipse.vcml.vcml.NumericInterval;
import org.vclipse.vcml.vcml.NumericLiteral;
import org.vclipse.vcml.vcml.NumericType;
import org.vclipse.vcml.vcml.Option;
import org.vclipse.vcml.vcml.SymbolicType;
import org.vclipse.vcml.vcml.VCObject;
import org.vclipse.vcml.vcml.VcmlModel;

import com.google.inject.Inject;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoParameterList;
import com.sap.conn.jco.JCoStructure;
import com.sap.conn.jco.JCoTable;

public class CharacteristicReader extends BAPIUtils {

	@Inject
	private ReadCharacteristicValueDependency valuesDependenciesReader;
	
	@Inject
	private ReadCharacteristicDependency csticDependenciesReader;
	
	public Characteristic read(String csticName, VcmlModel vcmlModel, final IProgressMonitor monitor, Map<String, VCObject> seenObjects, List<Option> globalOptions, boolean recurse) throws JCoException {
		if(csticName == null || monitor.isCanceled()) {
			return null;			
		}
		String id = "Characteristic/" + csticName.toUpperCase();
		VCObject seenObject = seenObjects.get(id);
		if (seenObject instanceof Characteristic) {
			return (Characteristic)seenObject;
		}
		final Characteristic newCstic = VCML.createCharacteristic();
		seenObjects.put(id, newCstic);
		JCoFunction function = getJCoFunction("BAPI_CHARACT_GETDETAIL", monitor);
		JCoParameterList ipl = function.getImportParameterList();
		ipl.setValue("CHARACTNAME", csticName);
		
		handleOptions2(newCstic.getOptions(), globalOptions, ipl, null, "KEYDATE");
		
		execute(function, monitor, csticName);
		if(processReturnTable(function)) {
			JCoParameterList epl = function.getExportParameterList();
			JCoParameterList tpl = function.getTableParameterList();

			JCoStructure charactDetail = epl.getStructure("CHARACTDETAIL");
			newCstic.setName(charactDetail.getString("CHARACT_NAME"));
			String dataType = charactDetail.getString("DATA_TYPE");
			if ("NUM".equals(dataType)) {
				NumericType type = VCML.createNumericType();
				newCstic.setType(type);
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
						NumericCharacteristicValue value = VCML.createNumericCharacteristicValue();
						String from = charactValuesNum.getString("VALUE_FROM");
						String to = charactValuesNum.getString("VALUE_TO");
						String relation = charactValuesNum.getString("VALUE_RELATION");
						String defaultString = charactValuesNum.getString("DEFAULT_VALUE");
						value.setDefault("X".equals(defaultString));
						if ("1".equals(relation)) {
							NumericLiteral literal = VCML.createNumericLiteral();
							literal.setValue(new BigDecimal(from).toPlainString());
							value.setEntry(literal);
						} else {
							// TODO handle interval bound (include / exclude)
							NumericInterval interval = VCML.createNumericInterval();
							interval.setLowerBound(new BigDecimal(from).toPlainString());
							interval.setUpperBound(new BigDecimal(to).toPlainString());
							value.setEntry(interval);
							
						}
						values.add(value);
					}
				}
			} else if ("CHAR".equals(dataType)) {
				SymbolicType type = VCML.createSymbolicType();
				newCstic.setType(type);
				type.setNumberOfChars(charactDetail.getInt("LENGTH"));
				type.setCaseSensitive("X".equals(charactDetail.getValue("CASE_SENSITIV")));
				EList<CharacteristicValue> values = type.getValues();
				HashMap<String, CharacteristicValue> seenValues = new HashMap<String, CharacteristicValue>(); // for grouping the descriptions by value
				JCoTable charactValuesChar = tpl.getTable("CHARACTVALUESCHAR");
				if (charactValuesChar!=null) {
					for (int i = 0; i < charactValuesChar.getNumRows(); i++) {
						charactValuesChar.setRow(i);
						String name = charactValuesChar.getString("VALUE_CHAR");
						CharacteristicValue value = seenValues.get(name);
						if (value==null) {
							value = VCML.createCharacteristicValue();
							values.add(value);
							value.setName(name);
							seenValues.put(name, value);
							value.setDefault("X".equals(charactValuesChar.getString("DEFAULT_VALUE")));
						}
					}
				}
				JCoTable charactValuesDescr = tpl.getTable("CHARACTVALUESDESCR");
				if (charactValuesDescr!=null) {
					for (int i = 0; i < charactValuesDescr.getNumRows(); i++) {
						charactValuesDescr.setRow(i);
						String name = charactValuesDescr.getString("VALUE_CHAR");
						CharacteristicValue value = seenValues.get(name);
						if (value==null) {
							value = VCML.createCharacteristicValue();
							values.add(value);
							value.setName(name);
							seenValues.put(name, value);
						}
						value.setDescription(VCML.createMultiLanguageDescriptions());
						MultiLanguageDescriptions multiLanguageDescriptions = (MultiLanguageDescriptions)value.getDescription();
						if (multiLanguageDescriptions!=null) {
							EList<MultiLanguageDescription> descriptions = multiLanguageDescriptions.getDescriptions();
							MultiLanguageDescription multiLanguageDescription = VCML.createMultiLanguageDescription();
							multiLanguageDescription.setLanguage(VcmlUtils.getLanguageByISOString(charactValuesDescr.getString("LANGUAGE_ISO")));
							multiLanguageDescription.setValue(charactValuesDescr.getString("DESCRIPTION"));
							descriptions.add(multiLanguageDescription);
						}
					}
				}
				for (final CharacteristicValue value : values) {
					value.setDescription(simplifyDescription(value.getDescription()));
					MultipleLanguageDocumentation multipleLanguageDocumentation = VCML.createMultipleLanguageDocumentation();
					final EList<MultipleLanguageDocumentation_LanguageBlock> languageBlocks = multipleLanguageDocumentation.getLanguageblocks();
					final Description description = value.getDescription();
					if (description==null) {
						try {
							MultipleLanguageDocumentation_LanguageBlock languageBlock = getLanguageBlock(newCstic, value, null, monitor);
							if (!languageBlock.getFormattedDocumentationBlocks().isEmpty())
								languageBlocks.add(languageBlock);
						} catch (JCoException e) {
							throw new WrappedException(e);
						}
					} else  {
						new DescriptionHandler() {
							@Override
							public void handleSingleDescription(Language language, String descriptionValue) {
								try {
									MultipleLanguageDocumentation_LanguageBlock languageBlock = getLanguageBlock(newCstic, value, language, monitor);
									if (!languageBlock.getFormattedDocumentationBlocks().isEmpty())
										languageBlocks.add(languageBlock);
								} catch (JCoException e) {
									throw new WrappedException(e);
								}
							}
						}.handleDescription(description);
					}
					if (!languageBlocks.isEmpty())
						value.setDocumentation(multipleLanguageDocumentation);
				}

			} else if ("DATE".equals(dataType)) {
				DateType type = VCML.createDateType();
				newCstic.setType(type);
				type.setIntervalValuesAllowed("X".equals(charactDetail.getString("INTERVAL_ALLOWED")));
				EList<DateCharacteristicValue> values = type.getValues();
				JCoTable charactValuesNum = tpl.getTable("CHARACTVALUESNUM");
				if (charactValuesNum!=null) {
					for (int i = 0; i < charactValuesNum.getNumRows(); i++) {
						charactValuesNum.setRow(i);
						DateCharacteristicValue value = VCML.createDateCharacteristicValue();
						try {
							long fromValue = charactValuesNum.getLong("VALUE_FROM");
							Date date = SAPFormattingUtility.DATEFORMAT_SAP.parse(String.valueOf(fromValue));
							String formatted = SAPFormattingUtility.DATEFORMAT_VCML.format(date);
							value.setFrom(formatted);
							
							long valueTo = charactValuesNum.getLong("VALUE_TO");
							if(valueTo != 0) { // is not an interval
								date = SAPFormattingUtility.DATEFORMAT_SAP.parse(String.valueOf(valueTo));
								formatted = SAPFormattingUtility.DATEFORMAT_VCML.format(date);
								value.setTo(formatted);								
							}
						} catch (ParseException e) {
							value.setFrom(SAPFormattingUtility.DEFAULT_DATE);
						}
						value.setDefault("X".equals(charactValuesNum.getString("DEFAULT_VALUE")));
						values.add(value);
					}
				}
			} else {
				errorStream.println("error: illegal/unknown data type " + dataType);
			}
			newCstic.setMultiValue("M".equals(charactDetail.getString("VALUE_ASSIGNMENT")));
			newCstic.setRequired("X".equals(charactDetail.getString("ENTRY_REQUIRED")));
			newCstic.setNoDisplay("X".equals(charactDetail.getString("NO_DISPLAY")));
			newCstic.setNotReadyForInput("X".equals(charactDetail.getString("NO_ENTRY")));
			newCstic.setAdditionalValues("X".equals(charactDetail.getString("ADDITIONAL_VALUES")));
			newCstic.setRestrictable("R".equals(charactDetail.getString("VALUE_ASSIGNMENT")));
			newCstic.setDisplayAllowedValues("X".equals(charactDetail.getString("DISPLAY_VALUES")));
			newCstic.setStatus(VcmlUtils.createStatusFromInt(charactDetail.getInt("STATUS")));
			newCstic.setGroup(nullIfEmpty(charactDetail.getString("CHARACT_GROUP")));
			JCoTable charactDescr = tpl.getTable("CHARACTDESCR");
			if (charactDescr!=null && charactDescr.getNumRows()>0) {
				MultiLanguageDescriptions multiLanguageDescriptions = VCML.createMultiLanguageDescriptions();
				EList<MultiLanguageDescription> descriptions = multiLanguageDescriptions.getDescriptions();
				MultipleLanguageDocumentation multipleLanguageDocumentation = VCML.createMultipleLanguageDocumentation();
				EList<MultipleLanguageDocumentation_LanguageBlock> languageBlocks = multipleLanguageDocumentation.getLanguageblocks();
				for (int i = 0; i < charactDescr.getNumRows(); i++) {
					charactDescr.setRow(i);
					MultiLanguageDescription multiLanguageDescription = VCML.createMultiLanguageDescription();
					Language language = VcmlUtils.getLanguageByISOString(charactDescr.getString("LANGUAGE_ISO"));
					multiLanguageDescription.setLanguage(language);
					multiLanguageDescription.setValue(charactDescr.getString("DESCRIPTION"));
					descriptions.add(multiLanguageDescription);
					// problem: this extracts only the documentation for languages with description
					// currently unknown whether there might be a documentation without description in SAP
					MultipleLanguageDocumentation_LanguageBlock languageBlock = getLanguageBlock(newCstic, null, language, monitor);
					if (!languageBlock.getFormattedDocumentationBlocks().isEmpty())
						languageBlocks.add(languageBlock);
				}
				if (!descriptions.isEmpty())
					newCstic.setDescription(simplifyDescription(multiLanguageDescriptions));
				if (!languageBlocks.isEmpty())
					newCstic.setDocumentation(multipleLanguageDocumentation);
			}
			JCoTable charactReferences= tpl.getTable("CHARACTREFERENCES");
			switch (charactReferences.getNumRows()) {
			case 0: break;
			case 1: 
				charactReferences.setRow(0);
				newCstic.setTable(charactReferences.getString("REFERENCE_TABLE"));
				newCstic.setField(charactReferences.getString("REFERENCE_FIELD"));
				break;
			default: 
				throw new IllegalArgumentException("table CHARACTREFERENCES has more than 1 row: " + charactReferences);
			}
		} else {
			newCstic.setName(csticName);
		}
		// read the dependencies used by the cstic
		csticDependenciesReader.read(newCstic, vcmlModel, monitor, seenObjects, recurse);
		// read the dependencies used by the cstics values
		valuesDependenciesReader.read(newCstic, vcmlModel, monitor, seenObjects, recurse);
		vcmlModel.getObjects().add(newCstic);
		return newCstic;
	}
		
	private MultipleLanguageDocumentation_LanguageBlock getLanguageBlock(Characteristic cstic, CharacteristicValue value, Language language, IProgressMonitor monitor) throws JCoException {
		JCoFunction function = getJCoFunction("BAPI_CHARACT_GETLONGTEXT", monitor);
		JCoParameterList ipl = function.getImportParameterList();
		ipl.setValue("CHARACTNAME", cstic.getName());
		if (value!=null)
			ipl.setValue("VALUE_CHAR", value.getName());
		if (language!=null) {
			ipl.setValue("LANGUAGE_ISO", language.toString());
		}
		execute(function, monitor, cstic.getName() + " " + (value==null ? "" : value.getName() + " ") + (language!=null ? language : "(no language)"));
		MultipleLanguageDocumentation_LanguageBlock lb = VCML.createMultipleLanguageDocumentation_LanguageBlock();
		lb.setLanguage(language==null ? VcmlUtils.getDefaultLanguage() : language);
		EList<FormattedDocumentationBlock> fdbs = lb.getFormattedDocumentationBlocks();
		JCoParameterList tpl = function.getTableParameterList();
		JCoTable longText = tpl.getTable("LONGTEXT");
		for (int i = 0; i < longText.getNumRows(); i++) {
			longText.setRow(i);
			FormattedDocumentationBlock fdb = VCML.createFormattedDocumentationBlock();
			fdbs.add(fdb);
			fdb.setValue(longText.getString("TDLINE"));
			fdb.setFormat(nullIfEquals(VcmlUtils.DEFAULT_FORMAT, longText.getString("TDFORMAT")));
		}
		return lb;
	}
}

