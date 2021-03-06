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
package org.vclipse.vcml2idoc.transformation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import org.eclipse.emf.common.util.EList;
import org.eclipse.jface.preference.IPreferenceStore;
import org.vclipse.idoc.iDoc.IDoc;
import org.vclipse.idoc.iDoc.IDocFactory;
import org.vclipse.idoc.iDoc.NumberField;
import org.vclipse.idoc.iDoc.Segment;
import org.vclipse.idoc.iDoc.StringField;
import org.vclipse.vcml.ui.VCMLUiPlugin;
import org.vclipse.vcml.utils.DependencySourceUtils;
import org.vclipse.vcml.utils.DescriptionHandler;
import org.vclipse.vcml.utils.DocumentationHandler;
import org.vclipse.vcml.utils.ISapConstants;
import org.vclipse.vcml.utils.VcmlUtils;
import org.vclipse.vcml.vcml.BOMItem;
import org.vclipse.vcml.vcml.BOMItem_Class;
import org.vclipse.vcml.vcml.BOMItem_Material;
import org.vclipse.vcml.vcml.BillOfMaterial;
import org.vclipse.vcml.vcml.Characteristic;
import org.vclipse.vcml.vcml.CharacteristicGroup;
import org.vclipse.vcml.vcml.CharacteristicOrValueDependencies;
import org.vclipse.vcml.vcml.CharacteristicValue;
import org.vclipse.vcml.vcml.Class;
import org.vclipse.vcml.vcml.Classification;
import org.vclipse.vcml.vcml.ConfigurationProfile;
import org.vclipse.vcml.vcml.ConfigurationProfileEntry;
import org.vclipse.vcml.vcml.Constraint;
import org.vclipse.vcml.vcml.DateCharacteristicValue;
import org.vclipse.vcml.vcml.DateType;
import org.vclipse.vcml.vcml.Dependency;
import org.vclipse.vcml.vcml.DependencyNet;
import org.vclipse.vcml.vcml.Description;
import org.vclipse.vcml.vcml.Documentation;
import org.vclipse.vcml.vcml.InterfaceDesign;
import org.vclipse.vcml.vcml.Language;
import org.vclipse.vcml.vcml.Literal;
import org.vclipse.vcml.vcml.Material;
import org.vclipse.vcml.vcml.MultiLanguageDescription;
import org.vclipse.vcml.vcml.MultiLanguageDescriptions;
import org.vclipse.vcml.vcml.NumberListEntry;
import org.vclipse.vcml.vcml.NumericCharacteristicValue;
import org.vclipse.vcml.vcml.NumericInterval;
import org.vclipse.vcml.vcml.NumericLiteral;
import org.vclipse.vcml.vcml.NumericType;
import org.vclipse.vcml.vcml.Option;
import org.vclipse.vcml.vcml.Precondition;
import org.vclipse.vcml.vcml.Procedure;
import org.vclipse.vcml.vcml.Row;
import org.vclipse.vcml.vcml.SelectionCondition;
import org.vclipse.vcml.vcml.SimpleDescription;
import org.vclipse.vcml.vcml.Status;
import org.vclipse.vcml.vcml.SymbolicLiteral;
import org.vclipse.vcml.vcml.SymbolicType;
import org.vclipse.vcml.vcml.VCObject;
import org.vclipse.vcml.vcml.ValueAssignment;
import org.vclipse.vcml.vcml.VariantFunction;
import org.vclipse.vcml.vcml.VariantFunctionArgument;
import org.vclipse.vcml.vcml.VariantTable;
import org.vclipse.vcml.vcml.VariantTableArgument;
import org.vclipse.vcml.vcml.VariantTableContent;
import org.vclipse.vcml.vcml.VcmlModel;
import org.vclipse.vcml.vcml.util.VcmlSwitch;
import org.vclipse.vcml2idoc.VCML2IDocPlugin;
import org.vclipse.vcml2idoc.preferences.IVCML2IDocPreferences;

import com.google.common.base.Strings;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.inject.Inject;
import com.google.inject.name.Named;

/**
 *
 */
public class VCML2IDocSwitch extends VcmlSwitch<List<IDoc>> {

	private static final IDocFactory IDOC = IDocFactory.eINSTANCE;

	private static final SimpleDateFormat DATEFORMAT_SAP = new SimpleDateFormat("yyyyMMdd");
	private static final SimpleDateFormat DATEFORMAT_VCML = new SimpleDateFormat("dd.MM.yyyy");
	
	// hierarchy levels for different IDoc types
	private static final int HIELEV_MATMAS = 2;
	private static final int HIELEV_CHRMAS = 3;
	private static final int HIELEV_VTAMAS = 4; // Variant tables (structure)
	private static final int HIELEV_VTMMAS = 5; // Variant tables (contents)
	private static final int HIELEV_VFNMAS = 6;
	private static final int HIELEV_KNOMAS = 7;
	private static final int HIELEV_CLSMAS = 8;
	private static final int HIELEV_DEPNET = 9;
	private static final int HIELEV_CLFMAS = 11;
	private static final int HIELEV_CNPMAS = 14;
	private static final int HIELEV_BOMMAT = 15;
	private static final int HIELEV_VCUI_SAVEM = 15;

	// constants defined at time of each transformation
	private String today;

	// global IDoc segment
	private Segment containerUPSITM;

	// global counter for iDoc number
	private int iDocNumber;

	// options from model
	private String ecm;
	private String ups;

	// insertion levels for IDocs of a specific type (counters for IDocs)
	private int inslev_MATMAS;
	private int inslev_CHRMAS;
//	private int inslev_VFNMAS;
	private int inslev_KNOMAS;
	private int inslev_CLSMAS;
	private int inslev_DEPNET;
	private int inslev_CLFMAS;
	private int inslev_CNPMAS;
	private int inslev_BOMMAT;
//	private int inslev_VCUI_SAVEM;

	private int sublev_BOMMAT;

	@Inject
	@Named(VCML2IDocPlugin.ID)
	private IPreferenceStore preferenceStore;

	@Inject
	@Named(VCMLUiPlugin.ID)
	private IPreferenceStore vcmlPreferenceStore;

	@Inject
	private DependencySourceUtils sourceUtils;
	
	/**
	 * not reentrant!
	 */
	public org.vclipse.idoc.iDoc.Model vcml2IDocs(final VcmlModel vcmlModel) {
		if(vcmlModel != null) {
			setTransformationTimeConstants();
			setOptionsFromModel(vcmlModel);
			initializeVariables();
			// transform all objects to IDocs
			final org.vclipse.idoc.iDoc.Model iDocModel = IDOC.createModel();
			final List<IDoc> idocs = iDocModel.getIdocs();
			IDoc upsIDoc = null;
			if (haveUPS()) {
				upsIDoc = addUPS(iDocModel, vcmlModel);
			}
			for(final VCObject vcObject : vcmlModel.getObjects()) {
				if(vcObject != null && !vcObject.eIsProxy()) {
					final List<IDoc> iDoc = doSwitch(vcObject);
					if(iDoc != null) {
						idocs.addAll(iDoc);
					}
				}
			}
			if(upsIDoc != null) {
				idocs.add(upsIDoc);
			}
			return iDocModel;
		}
		return null;
	}

	private void initializeVariables() {
		iDocNumber = 1;

		inslev_MATMAS = 1;
		inslev_CHRMAS = 1;
//		inslev_VFNMAS = 1;
		inslev_KNOMAS = 1;
		inslev_CLSMAS = 1;
		inslev_DEPNET = 1;
		inslev_CLFMAS = 1;
		inslev_CNPMAS = 1;
		inslev_BOMMAT = 1;
//		inslev_VCUI_SAVEM = 1;
		sublev_BOMMAT = 1;
	}

	private IDoc addUPS(final org.vclipse.idoc.iDoc.Model iDocModel, final VcmlModel vcmlModel) {
		// construct serialization
		// group IDocs by IDoc type
		final Map<String, List<IDoc>> groupedIDocs = new HashMap<String, List<IDoc>>();
		for(final IDoc iDocDocument : iDocModel.getIdocs()) {
			final String iDocType = iDocDocument.getType();
			List<IDoc> iDocList = groupedIDocs.get(iDocType);
			if(iDocList==null) {
				iDocList = new ArrayList<IDoc>();
				groupedIDocs.put(iDocType, iDocList);
			}
			iDocList.add(iDocDocument);
		}
		/* UPSMAS */
		final IDoc segmentRoot = createIDocRootSegment("UPSMAS01", "UPSMAS");

		final Segment segmentE1UPSHDR = addChildSegment(segmentRoot, "E1UPSHDR");
		// UPSNAM // will be filled later
		setValue(segmentE1UPSHDR, "UPSTYP", getUPSTYP());
		setValue(segmentE1UPSHDR, "UPSMODE", "O"); // letter O (original packet or correction packet) instead of number 0 !!!
		setValue(segmentE1UPSHDR, "TEXT", vcmlModel.eResource().getURI().lastSegment() + "  " + System.getProperty("user.name") + "  " + DateFormat.getDateTimeInstance().format(new Date()));

		containerUPSITM = segmentE1UPSHDR;

		/* predecessor is not used
		 * TODO add predecessor
		IDocSegment segmentE1UPSPRE = segmentE1UPSHDR.addChild("E1UPSPRE");
		segmentE1UPSPRE.setValue("UPSNAM", "...");
		*/

		final Segment segmentE1UPSSRC = addChildSegment(segmentE1UPSHDR, "E1UPSSRC");
		setValue(segmentE1UPSSRC, "OBJTYP", "MAT");
		setValue(segmentE1UPSSRC, "OBJID", ups);

		return segmentRoot;
	}

	private boolean haveUPS() {
		return ups!=null && generateIDocsFor(IVCML2IDocPreferences.UPSMAS);
	}

	private void setOptionsFromModel(final VcmlModel model) {
		ecm = null;
		ups = null;
		for(final Option option : model.getOptions()) {
			switch (option.getName()) {
				case ECM: ecm = option.getValue(); break;
				case UPS: ups = option.getValue(); break;
				default : break;
			}
		}
	}

	private void setTransformationTimeConstants() {
		today = new SimpleDateFormat("yyyyMMdd").format(new Date());
	}

	@Override
	public List<IDoc> caseBillOfMaterial(final BillOfMaterial object) {
		if(!generateIDocsFor(IVCML2IDocPreferences.BOMMAT)) {
			return Collections.emptyList();
		}

		final String alternativeBOM = "01";
		final IDoc iDoc = createIDocRootSegment("BOMMAT04", "BOMMAT");
		final Segment segmentE1STZUM = addChildSegment(iDoc, "E1STZUM");
		setValue(segmentE1STZUM, "MSGFN", "005");
		setValue(segmentE1STZUM, "STLTY", "M"); // Material BOM
		// setValue(segmentE1STZUM, "STLNR", ""); // Stuecklistennummer

		final String bomUsage = getBomUsage();
		setValue(segmentE1STZUM, "STLAN", toUpperCase(bomUsage));
		// setValue(segmentE1STZUM, "EXSTL", "1.000"); // BOM group
		setValue(segmentE1STZUM, "KBAUS", "X"); // Indicator: configurable BOM

		/* not mandatory
		Segment segmentE1SZUTH = addChildSegment(segmentE1STZUM, "E1SZUTH");
		setValue(segmentE1SZUTH, "MSGFN", "005");
		setValue(segmentE1SZUTH, "TDNAME", "70 chars");
		setValue(segmentE1SZUTH, "TDID", "xxxx");
		setValue(segmentE1SZUTH, "TDSPRAS", "E");
		setValue(segmentE1SZUTH, "TDSPRAS_ISO", "EN");
		*/

		Material material = object.getMaterial();
		if(material == null) {
			System.err.println(object.getClass().getSimpleName() + " " + object.getName() + " has no assigned material.");
		} else {
			final String materialNumber = material.getName();
			final String plant = getPlant();


			final Segment segmentE1MASTM = addChildSegment(segmentE1STZUM, "E1MASTM");
			setValue(segmentE1MASTM, "MSGFN", "005");
			setValue(segmentE1MASTM, "MATNR", toUpperCase(materialNumber));
			setValue(segmentE1MASTM, "WERKS", toUpperCase(plant));
			setValue(segmentE1MASTM, "STLAN", toUpperCase(bomUsage));
			// setValue(segmentE1MASTM, "STLNR", ""); // Stuecklistennummer?
			setValue(segmentE1MASTM, "STLAL", alternativeBOM); // Alternative BOM

			final Segment segmentE1STKOM = addChildSegment(segmentE1STZUM, "E1STKOM");
			setValue(segmentE1STKOM, "MSGFN", "005");
			setValue(segmentE1STKOM, "STLAL", alternativeBOM); // Alternative BOM
			setValue(segmentE1STKOM, "BMEIN", "PCE"); // unit of measure for base quantity
			setValue(segmentE1STKOM, "STLST", "01"); // BOM status

			for(final BOMItem item : object.getItems()) {
				final Segment segmentE1STPOM = addChildSegment(segmentE1STZUM, "E1STPOM");
				setValue(segmentE1STPOM, "MSGFN", "005");
				// setValue(segmentE1STPOM, "STLKN", "00000001"); // BOM item code number
				if (item instanceof BOMItem_Material) {
					setValue(segmentE1STPOM, "IDNRK", ((BOMItem_Material)item).getMaterial().getName());
				} else if (item instanceof BOMItem_Class) {
					String classSpec = ((BOMItem_Class)item).getCls().getName();
					int classType = VcmlUtils.getClassType(classSpec);
					String className = VcmlUtils.getClassName(classSpec);
					setValue(segmentE1STPOM, "CLASS", className);
					setValue(segmentE1STPOM, "KLART", classType);
				} else {
					throw new IllegalArgumentException("unknown type of BOMItem: " + item);
				}

				setValue(segmentE1STPOM, "POSTP", "N");
				setValue(segmentE1STPOM, "POSNR", item.getItemnumber());
				setValue(segmentE1STPOM, "MEINS", "PCE");
				setValue(segmentE1STPOM, "MENGE_C", "1");
				setValue(segmentE1STPOM, "RVREL", "X"); // Indicator: item relevant to sales

				final SelectionCondition selectionCondition = item.getSelectionCondition();
				if(selectionCondition != null) {
					addSegmentE1CUKBM(segmentE1STPOM, selectionCondition.getName(), "SEL", selectionCondition.getGroup(), selectionCondition.getStatus(), null);
				}
				for(final ConfigurationProfileEntry entry : item.getEntries()) {
					final Procedure procedure = entry.getDependency();
					if(procedure != null) {
						addSegmentE1CUKBM(segmentE1STPOM, toUpperCase(procedure.getName()), "PROC", toUpperCase(procedure.getGroup()), procedure.getStatus(), String.format("%1$04d", entry.getSequence()));
					}
				}
			}
			final String objid = String.format("%1$-18s%2$4s%3$1s%4$2s", materialNumber, plant, bomUsage, alternativeBOM);
			addSegmentE1UPSLINK(iDoc, objid, VcmlUtils.DEFAULT_VALIDITY_START);
			addSegmentE1UPSITM(iDoc, "BOMMAT", "MBOM", objid, HIELEV_BOMMAT, inslev_BOMMAT, sublev_BOMMAT++);
		}
		return Collections.singletonList(iDoc);
	}

	@Override
	public List<IDoc> caseCharacteristic(final Characteristic object) {
		if(!generateIDocsFor(IVCML2IDocPreferences.CHRMAS) || object.getDescription() == null) {
			return Collections.emptyList();
		}
		final IDoc iDoc = createIDocRootSegment("CHRMAS04", "CHRMAS");
		// Master Characteristic Basic Data
		final Segment segmentE1CABNM = addChildSegment(iDoc, "E1CABNM");
		setValue(segmentE1CABNM, "MSGFN", "004");
		setValue(segmentE1CABNM, "ATNAM", toUpperCase(object.getName()));

		new VcmlSwitch<Object>() {
			@Override
			public Object caseNumericType(final NumericType numericType) {
				setValue(segmentE1CABNM, "ATFOR", "NUM");
				setValue(segmentE1CABNM, "ANZST", numericType.getNumberOfChars());
				setValue(segmentE1CABNM, "ANZDZ", numericType.getDecimalPlaces());
				setValue(segmentE1CABNM, "ATVOR", numericType.isNegativeValuesAllowed() ? "X" : null);
				setValue(segmentE1CABNM, "ATINT", numericType.isIntervalValuesAllowed() ? "X" : null);
				final String unit = toUpperCase(numericType.getUnit());
				setValue(segmentE1CABNM, "MSEHI", unit);
				// setValue(segmentE1CABNM, "ATDIM", "0"); // exponent display
				// setValue(segmentE1CABNM, "ATDEX", "0"); // exponent display format
				if(!numericType.getValues().isEmpty()) { // TODO add separator?
					setValue(segmentE1CABNM, "ATSCH", "__________________".substring(0, numericType.getNumberOfChars()-numericType.getDecimalPlaces()) + (numericType.getDecimalPlaces()>0 ? "." + "_______________".substring(0, numericType.getDecimalPlaces()) : ""));
				}
				// Master characteristic value
				int counter = 0;
				for(final NumericCharacteristicValue value : numericType.getValues()) {
					counter++;
					addSegmentE1CAWNM(segmentE1CABNM, counter, value, unit);
				}
				return this;
			}
			@Override
			public Object caseSymbolicType(final SymbolicType symbolicType) {
				setValue(segmentE1CABNM, "ATFOR", "CHAR");
				setValue(segmentE1CABNM, "ANZST", symbolicType.getNumberOfChars());
				final boolean caseSensitive = symbolicType.isCaseSensitive();
				setValue(segmentE1CABNM, "ATKLE", caseSensitive ? "X" : null);
				// Master characteristic value
				int counter = 0;
				for(final CharacteristicValue value : symbolicType.getValues()) {
					counter++;
					addSegmentE1CAWNM(segmentE1CABNM, counter, value, caseSensitive);
				}
				return this;
			}
			@Override
			public Object caseDateType(final DateType dateType) {
				setValue(segmentE1CABNM, "ATFOR", "DATE");
				setValue(segmentE1CABNM, "ATINT", dateType.isIntervalValuesAllowed() ? "X" : null);
				// Master characteristic value
				int counter = 0;
				for(final DateCharacteristicValue value : dateType.getValues()) {
					counter++;
					addSegmentE1CAWNM(segmentE1CABNM, counter, value);
				}
				return this;
			}
		}.doSwitch(object.getType());

		setValue(segmentE1CABNM, "ATMST", VcmlUtils.createIntFromStatus(object.getStatus()));
		setValue(segmentE1CABNM, "ATKLA", object.getGroup());

		setValue(segmentE1CABNM, "ATSON", object.isAdditionalValues() ? "X" : null);
		setValue(segmentE1CABNM, "ATERF", object.isRequired() ? "X" : null);
		setValue(segmentE1CABNM, "ATGLA", object.isRestrictable() ? "X" : null);
		setValue(segmentE1CABNM, "ATVIE", object.isNoDisplay() ? "X" : null);
		setValue(segmentE1CABNM, "ATINP", object.isNotReadyForInput() ? "X" : null);
		setValue(segmentE1CABNM, "ATEIN", object.isMultiValue() ? null : "X");
		setValue(segmentE1CABNM, "ATWRD", object.isDisplayAllowedValues() ? "X" : null);

		setValue(segmentE1CABNM, "DATUV", "00000000");
		
		// TODO use DescriptionHandler for this purpose?
		// Master Characteristic Language-Dependent Name
		new VcmlSwitch<Object>() {
			@Override
			public Object caseMultiLanguageDescription(final MultiLanguageDescription description) {
				addSegmentE1CABTM(description.getLanguage(), description.getValue());
				return this;
			}
			@Override
			public Object caseMultiLanguageDescriptions(final MultiLanguageDescriptions description) {
				for(final MultiLanguageDescription mld : description.getDescriptions()) {
					doSwitch(mld);
				}
				return this;
			}
			@Override
			public Object caseSimpleDescription(final SimpleDescription description) {
				addSegmentE1CABTM(VcmlUtils.getDefaultLanguage(), description.getValue());
				return this;
			}
			private void addSegmentE1CABTM(final Language language, final String value) {
				final Segment segmentE1CABTM = addChildSegment(segmentE1CABNM, "E1CABTM");
				setValue(segmentE1CABTM, "MSGFN", "004");
				setValue(segmentE1CABTM, "SPRAS", VcmlUtils.getLanguageCharacter(language));
				setValue(segmentE1CABTM, "ATBEZ", value.replace("\"", "\\\"")); // TODO use a value converter for this purpose
				setValue(segmentE1CABTM, "SPRAS_ISO", language.toString());
			}
		}.doSwitch(object.getDescription());

		// Long Text Lines
		addSegmentsForDocumentation(segmentE1CABNM, "E1TEXTL", object.getDocumentation());

		if (object.getTable()!=null || object.getField()!=null) {
			final Segment segmentE1CABZM = addChildSegment(segmentE1CABNM, "E1CABZM");
			setValue(segmentE1CABZM, "MSGFN", "004");
			setValue(segmentE1CABZM, "REF_TABLE", object.getTable());
			setValue(segmentE1CABZM, "REF_FIELD", object.getField());
		}

		addSegmentE1DATEM(segmentE1CABNM, object);
		addSegmentE1UPSLINK(iDoc, toUpperCase(object.getName()), VcmlUtils.DEFAULT_VALIDITY_START);

		addSegmentE1UPSITM(iDoc, "CHRMAS", "CHR", toUpperCase(object.getName()), HIELEV_CHRMAS, inslev_CHRMAS++, 1);

		return Collections.singletonList(iDoc);
	}

	
	
	/**
	 * @param parentSegment
	 * @param counter
	 * @param value
	 * @param caseSensitive
	 */
	private void addSegmentE1CAWNM(final Segment parentSegment, final int counter, final CharacteristicValue value, final boolean caseSensitive) {
		final Segment segmentE1CAWNM = addChildSegment(parentSegment, "E1CAWNM");
		setValue(segmentE1CAWNM, "MSGFN", "004");
		setValue(segmentE1CAWNM, "ATZHL", String.format("%1$04d", counter)); // internal counter
		setValue(segmentE1CAWNM, "ATWRT", caseSensitive ? value.getName() : value.getName().toUpperCase());
		setValue(segmentE1CAWNM, "ATZHH", "0000"); // internal counter for value hierarchy
		CharacteristicOrValueDependencies dependencies = value.getDependencies();
		if (dependencies!=null) {
			for (Dependency dep : dependencies.getDependencies()) {
				// TODO this could be simplified after a model refactoring
				if (dep instanceof Precondition) {
					Precondition precondition = (Precondition)dep;
					addSegmentE1CUKB1(segmentE1CAWNM, precondition.getName(), "PRE", precondition.getGroup(), precondition.getStatus(), null);
				} else if (dep instanceof Procedure) {
					Procedure procedure = (Procedure)dep;
					addSegmentE1CUKB1(segmentE1CAWNM, procedure.getName(), "PROC", procedure.getGroup(), procedure.getStatus(), null);
				} else if (dep instanceof SelectionCondition) {
					SelectionCondition selectionCondition = (SelectionCondition)dep;
					addSegmentE1CUKB1(segmentE1CAWNM, selectionCondition.getName(), "SEL", selectionCondition.getGroup(), selectionCondition.getStatus(), null);
				} else {
					throw new IllegalArgumentException("illegal dependency type for cstic values: " + dep);
				} 
			}
		}
		// Master Characteristic Value Language-Dependent Name
		final Description description = value.getDescription();
		if (description!=null) {
			new VcmlSwitch<Object>() {
				int counter = 0;
				@Override
				public Object caseMultiLanguageDescription(final MultiLanguageDescription description) {
					addSegmentE1CABTM(description.getLanguage(), description.getValue());
					return this;
				}
				@Override
				public Object caseMultiLanguageDescriptions(final MultiLanguageDescriptions description) {
					for(final MultiLanguageDescription mld : description.getDescriptions()) {
						doSwitch(mld);
					}
					return this;
				}
				@Override
				public Object caseSimpleDescription(final SimpleDescription description) {
					addSegmentE1CABTM(VcmlUtils.getDefaultLanguage(), description.getValue());
					return this;
				}
				private void addSegmentE1CABTM(final Language language, final String value) {
					counter++;
					final Segment segmentE1CAWTM = addChildSegment(segmentE1CAWNM, "E1CAWTM");
					setValue(segmentE1CAWTM, "MSGFN", "004");
					setValue(segmentE1CAWTM, "ATZHL", String.format("%1$04d", counter));
					setValue(segmentE1CAWTM, "SPRAS_ISO", language.toString());
					setValue(segmentE1CAWTM, "SPRAS", VcmlUtils.getLanguageCharacter(language));
					setValue(segmentE1CAWTM, "ATWTB", value);
				}
			}.doSwitch(description);
		}
		// documentation for characteristic values
		addSegmentsForDocumentation(segmentE1CAWNM, "E1TXTL1", value.getDocumentation());
		if(value.isDefault()) {
			setValue(segmentE1CAWNM, "ATSTD", "X");			
		}
	}

	/**
	 * @param parentSegment
	 * @param counter
	 * @param value
	 * @param unit
	 */
	private void addSegmentE1CAWNM(final Segment parentSegment, final int counter, final NumericCharacteristicValue value, final String unit) {
		final Segment segmentE1CAWNM = addChildSegment(parentSegment, "E1CAWNM");
		setValue(segmentE1CAWNM, "MSGFN", "004");
		setValue(segmentE1CAWNM, "ATZHL", String.format("%1$04d", counter)); // internal counter
		// NUM objects use ATFLV. Some other attributes have to be set.
		String flv, flb;
		String atcod;
		NumberListEntry entry = value.getEntry();
		if (entry instanceof NumericLiteral) {
			flv = ((NumericLiteral)entry).getValue();
			flb = flv;
			atcod = "1"; // for EQ
		} else if (entry instanceof NumericInterval) {
			flv = ((NumericInterval)entry).getLowerBound();
			flb = ((NumericInterval)entry).getUpperBound();
			atcod = "3";
		} else {
			throw new IllegalArgumentException("unknown NumberListEntry " + entry);
		}
		/*
		 * the atcod coding means the following:
'1'. " EQ
'2'. " GE LT
'3'. " GE LE
'4'. " GT LT
'5'. " GT LE
'6'. " LT
'7'. "    LE
'8'. " GT
'9'. " GE
		 */
		
		setValue(segmentE1CAWNM, "ATFLV", flv);
		setValue(segmentE1CAWNM, "ATFLB", flb);
		setValue(segmentE1CAWNM, "ATTLV", 0); // ToLeranz Von
		setValue(segmentE1CAWNM, "ATTLB", 0); // ToLeranz Bis
		setValue(segmentE1CAWNM, "ATINC", 0);
		setValue(segmentE1CAWNM, "ATCOD", atcod);
		setValue(segmentE1CAWNM, "ATAW1", unit);
		setValue(segmentE1CAWNM, "ATAWE", unit);
		setValue(segmentE1CAWNM, "TXTNR", "0000");
		setValue(segmentE1CAWNM, "DATUV", "00000000");
		setValue(segmentE1CAWNM, "ATZHH", "0000"); // internal counter for value hierarchy
	}

	private void addSegmentE1CAWNM(final Segment parentSegment, final int counter, final DateCharacteristicValue value) {
		final Segment segmentE1CAWNM = addChildSegment(parentSegment, "E1CAWNM");
		setValue(segmentE1CAWNM, "MSGFN", "004");
		setValue(segmentE1CAWNM, "ATZHL", String.format("%1$04d", counter)); // internal counter
		// NUM objects use ATFLV. Some other attributes have to be set.
		String flv;
		String atcod;
		String from = value.getFrom();
		String to = value.getTo();
		try {
			flv = DATEFORMAT_SAP.format(DATEFORMAT_VCML.parse(from));
		} catch (ParseException e) {
			flv = "00.00.0000";
		}
		setValue(segmentE1CAWNM, "ATFLV", flv);
		if (to==null) {
			setValue(segmentE1CAWNM, "ATFLB", 0);
			atcod = "1"; // means EQ
		} else {
			String flb;
			try {
				flb = DATEFORMAT_SAP.format(DATEFORMAT_VCML.parse(to));
			} catch (ParseException e) {
				flb = "0";
			}
			setValue(segmentE1CAWNM, "ATFLB", flb);
			atcod = "3"; // means GE LE
		}
		setValue(segmentE1CAWNM, "ATTLV", 0); // ToLeranz Von
		setValue(segmentE1CAWNM, "ATTLB", 0); // ToLeranz Bis
		setValue(segmentE1CAWNM, "ATINC", 0);
		setValue(segmentE1CAWNM, "ATCOD", atcod);
		setValue(segmentE1CAWNM, "TXTNR", "0000");
		setValue(segmentE1CAWNM, "DATUV", "00000000");
		setValue(segmentE1CAWNM, "ATZHH", "0000"); // internal counter for value hierarchy
	}

	/**
	 * @param parentSegment
	 * @param segmentType
	 * @param documentation
	 */
	private void addSegmentsForDocumentation(final Segment parentSegment, final String segmentType, final Documentation documentation) {
		new DocumentationHandler() {
			@Override
			public void addDocumentationEntry(final Language language, final String text, final String format) {
				boolean firstLine = true;
				int i = 0;
				while (i<text.length()) {
					String docText = text.substring(i, Math.min(text.length(), i+132));
					final Segment segmentE1TEXTx = addChildSegment(parentSegment, segmentType);
					setValue(segmentE1TEXTx, "MSGFN", "004");
					setValue(segmentE1TEXTx, "TDFORMAT", firstLine ? format : "="); // = means to append to the previous line
					setValue(segmentE1TEXTx, "TDLINE", docText);
					setValue(segmentE1TEXTx, "LANGUAGE_ISO", language.toString());
					firstLine = false;
					i += 132;
				}
			}
		}.handleDocumentation(documentation);
	}

	// Master Dependency Documentation
	/**
	 * @param parentSegment
	 * @param segmentType
	 * @param documentation
	 */
	private void addSegmentsForDependencyDocumentation(final Segment parentSegment, final String segmentType, final Documentation documentation) {
		new DocumentationHandler() {
			@Override
			public void addDocumentationEntry(final Language language, final String text, final String format) {
				final Segment segmentE1TEXTx = addChildSegment(parentSegment, segmentType);
				setValue(segmentE1TEXTx, "MSGFN", ""); // TODO set MSGFN?
				setValue(segmentE1TEXTx, "LANGUAGE", VcmlUtils.getLanguageCharacter(language));
				setValue(segmentE1TEXTx, "TXT_FORM", format);
				setValue(segmentE1TEXTx, "TXT_LINE", text);
				setValue(segmentE1TEXTx, "LANGUAGE_ISO", language.toString());
			}
		}.handleDocumentation(documentation);
	}


	// TODO extend segments for Class
	@Override
	public List<IDoc> caseClass(final org.vclipse.vcml.vcml.Class object) {
		List<IDoc> result = Lists.newArrayList();
		final String classSpec = toUpperCase(object.getName());
		final int classType = VcmlUtils.getClassType(classSpec);
		final String className = VcmlUtils.getClassName(classSpec);
		final IDoc iDoc = createIDocRootSegment("CLSMAS04", "CLSMAS");
		if(generateIDocsFor(IVCML2IDocPreferences.CLSMAS) && object.getDescription()!=null) {
			result.add(iDoc);
			// Master Class Basic Data
			final Segment segmentE1KLAHM = addChildSegment(iDoc, "E1KLAHM");
			setValue(segmentE1KLAHM, "MSGFN", "004");
			setValue(segmentE1KLAHM, "KLART", classType);
			setValue(segmentE1KLAHM, "CLASS", toUpperCase(className));
			setValue(segmentE1KLAHM, "STATU", VcmlUtils.createIntFromStatus(object.getStatus()));
			setValue(segmentE1KLAHM, "KLAGR", toUpperCase(object.getGroup()));
			setValue(segmentE1KLAHM, "VONDT", withDefault(VcmlUtils.DEFAULT_VALIDITY_START, null)); // must be set manually for classes // TODO check possible attributes in object
			// Class Descriptions
			// Segment segmentE1KLATM = addChildSegment(segmentE1KLAHM, "E1KLATM");
			// Class: long text lines
			// Segment segmentE1TEXTL = addChildSegment(segmentE1KLAHM, "E1TEXTL");
			// Master Class Characteristics for Class
			int counter = 0;
			for(final Characteristic cstic : object.getCharacteristics()) {
				counter++;
				final Segment segmentE1KSMLM = addChildSegment(segmentE1KLAHM, "E1KSMLM");
				setValue(segmentE1KSMLM, "MSGFN", "004");
				setValue(segmentE1KSMLM, "ATNAM", toUpperCase(cstic.getName()));
				setValue(segmentE1KSMLM, "POSNR", String.format("%1$03d", counter));
			}
			// Master Class Keywords
			new DescriptionHandler() {
				@Override
				public void handleSingleDescription(final Language language, final String value) {
					final Segment segmentE1SWORM = addChildSegment(segmentE1KLAHM, "E1SWORM");
					setValue(segmentE1SWORM, "MSGFN", "004");
					setValue(segmentE1SWORM, "SPRAS", VcmlUtils.getLanguageCharacter(language));
					setValue(segmentE1SWORM, "KLPOS", "01");
					setValue(segmentE1SWORM, "KSCHL", value);
					setValue(segmentE1SWORM, "SPRAS_ISO", language.toString());
				}
			}.handleDescription(object.getDescription());

			addSegmentE1DATEM(segmentE1KLAHM, object);
			addSegmentE1UPSLINK(iDoc, toUpperCase(className), VcmlUtils.DEFAULT_VALIDITY_START);

			addSegmentE1UPSITM(iDoc, "CLSMAS", "CLS", classType + toUpperCase(className), HIELEV_CLSMAS, inslev_CLSMAS++, 1);
		}

		List<Class> superClasses = object.getSuperClasses();
		if(generateIDocsFor(IVCML2IDocPreferences.CLFMAS) && !superClasses.isEmpty()) {
			final IDoc clfmas_iDoc = createIDocRootSegment("CLFMAS02", "CLFMAS");
			result.add(clfmas_iDoc);
			// Master Object Classification
			final Segment segmentE1OCLFM = addChildSegment(clfmas_iDoc, "E1OCLFM");
			setValue(segmentE1OCLFM, "MSGFN", "004");
			setValue(segmentE1OCLFM, "OBTAB", "MARA");
			setValue(segmentE1OCLFM, "OBJEK", toUpperCase(className));
			setValue(segmentE1OCLFM, "KLART", classType);
			setValue(segmentE1OCLFM, "MAFID", "K");
			setValue(segmentE1OCLFM, "OBJECT_TABLE", "MARA");
			for (Class superClass : superClasses) {
				// Distribution Classification: Object Class Assignment
				final Segment segmentE1KSSKM = addChildSegment(segmentE1OCLFM, "E1KSSKM");
				setValue(segmentE1KSSKM, "MSGFN", "004");
				setValue(segmentE1KSSKM, "CLASS", VcmlUtils.getClassName(toUpperCase(superClass.getName())));
				setValue(segmentE1KSSKM, "DATUV", "00000000");
				setValue(segmentE1KSSKM, "STATU", VcmlUtils.createIntFromStatus(superClass.getStatus())); // TODO is status neccessary here?
			}
			addSegmentE1DATEM(segmentE1OCLFM, object);
			addSegmentE1UPSLINK(clfmas_iDoc, classType + toUpperCase(className), VcmlUtils.DEFAULT_VALIDITY_START);
			
			addSegmentE1UPSITM(clfmas_iDoc, "CLSMAS", "SUBCLS", classType + toUpperCase(className), HIELEV_CLFMAS, inslev_CLFMAS++, 1);
		}
		return result;
	}

	/**
	 * @param iDoc
	 * @param mestyp
	 * @param objtyp
	 * @param objid
	 * @param hielev
	 * @param inslev
	 * @param sublev
	 */
	private void addSegmentE1UPSITM(final IDoc iDoc, final String mestyp, final String objtyp, final String objid, final int hielev, final int inslev, final int sublev) {
		if(haveUPS()) {
			final Segment segmentE1UPSITM =  addChildSegment(containerUPSITM, "E1UPSITM");
			setValue(segmentE1UPSITM, "MESTYP", toUpperCase(mestyp));
			setValue(segmentE1UPSITM, "OBJTYP", toUpperCase(objtyp));
			setValue(segmentE1UPSITM, "OBJID", toUpperCase(objid));
			setValue(segmentE1UPSITM, "OBJVAL", "VCUI_SAVEM".equals(mestyp) ? null : VcmlUtils.DEFAULT_VALIDITY_START);
			setValue(segmentE1UPSITM, "HIELEV", String.format("%1$04d", hielev));
			setValue(segmentE1UPSITM, "INSLEV", String.format("%1$06d", inslev));
			setValue(segmentE1UPSITM, "SUBLEV", String.format("%1$06d", sublev));
			setValue(segmentE1UPSITM, "FUNCTION", "I");
			setValue(segmentE1UPSITM, "MESCOD", "MOD");
			setValue(segmentE1UPSITM, "SNDDOC", toUpperCase(iDoc.getName()));
		}
	}

	@Override
	public List<IDoc> caseConfigurationProfile(final ConfigurationProfile profile) {
		if(!generateIDocsFor(IVCML2IDocPreferences.CNPMAS)) {
			return Collections.emptyList();
		}
		// Master Configuration Profile + E1UPSLINK
		final IDoc iDoc = createIDocRootSegment("CNPMAS03", "CNPMAS");
		// Master configuration profile object type
		final Segment segmentE1CUTY = addChildSegment(iDoc, "E1CUTY");
		setValue(segmentE1CUTY, "MSGFN", "004");
		setValue(segmentE1CUTY, "OBJECT_TYP", "MARA");
		addSegmentE1DATEM(segmentE1CUTY, profile);

		// Master configuration profile object ID
		final Segment segmentE1CUID = addChildSegment(segmentE1CUTY, "E1CUID");
		setValue(segmentE1CUID, "MSGFN", "004");
		setValue(segmentE1CUID, "KEY_FELD", "MATNR");
		setValue(segmentE1CUID, "KPARA_VALU", toUpperCase(profile.getName()));

		// Master configuration profile configuration parameter
		final Segment segmentE1CUCOM = addChildSegment(segmentE1CUTY, "E1CUCOM");
		setValue(segmentE1CUCOM, "MSGFN", "004");
		setValue(segmentE1CUCOM, "C_PROFILE", toUpperCase(profile.getName()));
		setValue(segmentE1CUCOM, "CLASSTYPE", "300");
		setValue(segmentE1CUCOM, "STATUS", VcmlUtils.createIntFromStatus(profile.getStatus()));
		if(profile.getBomapplication() != null) {
			setValue(segmentE1CUCOM, "BOMAPPL", profile.getBomapplication());			
		}
		setValue(segmentE1CUCOM, "BOMEXPL", "4");
		setValue(segmentE1CUCOM, "INITSCREEN", "1");
		setValue(segmentE1CUCOM, "FLRESULT", "X");
		setValue(segmentE1CUCOM, "FLMDATA", "X");
		setValue(segmentE1CUCOM, "FLCASONLY", "X");
		final InterfaceDesign design = profile.getUidesign();
		if(design!=null) {
			setValue(segmentE1CUCOM, "DESIGN", toUpperCase(design.getName()));
		}
		setValue(segmentE1CUCOM, "NEUTR", "1");
		setValue(segmentE1CUCOM, "CHAR_VALU", "3");
		setValue(segmentE1CUCOM, "SCOPE_CHAR", "1");
		setValue(segmentE1CUCOM, "SCOPE_VALU", "1");
		setValue(segmentE1CUCOM, "DISPLAY", "1");
		setValue(segmentE1CUCOM, "PRICING", "1");
		setValue(segmentE1CUCOM, "CONFIGUR", "1");
		setValue(segmentE1CUCOM, "DEFVALU_DE", "2");
		setValue(segmentE1CUCOM, "DEFVALU_CC", "1");
		setValue(segmentE1CUCOM, "TYPM_SEL", "1");
		setValue(segmentE1CUCOM, "TYPM_STRA", "1");
		setValue(segmentE1CUCOM, "MULTIL_STRU", "1");
		setValue(segmentE1CUCOM, "PRIO", "00");
		setValue(segmentE1CUCOM, "UMBEW", "X");
		setValue(segmentE1CUCOM, "FLBROWSER", "X");

		for(final DependencyNet net : profile.getDependencyNets()) {
			addSegmentE1CUKBM(segmentE1CUCOM, toUpperCase(net.getName()), "CNET", toUpperCase(net.getGroup()), net.getStatus(), "0000");
		}
		for(final ConfigurationProfileEntry entry : profile.getEntries()) {
			final Procedure procedure = entry.getDependency();
			if(procedure != null) {
				addSegmentE1CUKBM(segmentE1CUCOM, toUpperCase(procedure.getName()), "PROC", procedure.getGroup(), procedure.getStatus(), String.format("%1$04d", entry.getSequence()));
			}
		}

		setValue(addChildSegment(segmentE1CUCOM, "E1KSSKM"), "MSGFN", "023");
		addSegmentE1UPSLINK(iDoc, toUpperCase(profile.getName()), VcmlUtils.DEFAULT_VALIDITY_START);
		addSegmentE1UPSITM(iDoc, "CNPMAS", "CPM", String.format("%1$-18s%2$-30s%3$3s", toUpperCase(profile.getName()), toUpperCase(profile.getName()), "300"), HIELEV_CNPMAS, inslev_CNPMAS++, 1);
		// FIXME the object key in CNPMAS might include the material name and the configuration profile name.
		return Collections.singletonList(iDoc);
	}

	/**
	 * @param parentSegment
	 * @param name
	 * @param type
	 * @param group
	 * @param status
	 * @param lineno
	 * @return
	 */
	private Segment addSegmentE1CUKBM(final Segment parentSegment, final String name, final String type, final String group, final Status status, final String lineno) {
		final Segment segmentE1CUKBM = addChildSegment(parentSegment, "E1CUKBM");
		setValue(segmentE1CUKBM, "MSGFN", "004");
		setValue(segmentE1CUKBM, "DEP_INTERN", toUpperCase(name));
		setValue(segmentE1CUKBM, "DEP_TYPE", type);
		setValue(segmentE1CUKBM, "STATUS", VcmlUtils.createIntFromStatus(status));
		if (!Strings.isNullOrEmpty(group)) {
			setValue(segmentE1CUKBM, "GROUP", toUpperCase(group));
		}
		setValue(segmentE1CUKBM, "DEP_LINENO", lineno);
		return segmentE1CUKBM;
	}

	private Segment addSegmentE1CUKB1(final Segment parentSegment, final String name, final String type, final String group, final Status status, final String lineno) {
		final Segment segmentE1CUKB1 = addChildSegment(parentSegment, "E1CUKB1");
		setValue(segmentE1CUKB1, "MSGFN", "004");
		setValue(segmentE1CUKB1, "DEP_INTERN", toUpperCase(name));
		setValue(segmentE1CUKB1, "DEP_TYPE", type);
		setValue(segmentE1CUKB1, "STATUS", VcmlUtils.createIntFromStatus(status));
		if (!Strings.isNullOrEmpty(group)) {
			setValue(segmentE1CUKB1, "GROUP", toUpperCase(group));
		}
		setValue(segmentE1CUKB1, "DEP_LINENO", lineno);
		return segmentE1CUKB1;
	}

	/**
	 * @see org.vclipse.vcml.vcml.util.VcmlSwitch#caseDependencyNet(org.vclipse.vcml.vcml.DependencyNet)
	 */
	@Override
	public List<IDoc> caseDependencyNet(final DependencyNet dnet) {
		if (!generateIDocsFor(IVCML2IDocPreferences.DEPNET) || dnet.getDescription() == null) {
			return Collections.emptyList();
		}

		final IDoc iDoc = createIDocRootSegment("DEPNET02", "DEPNET");

		// Master Characteristic Basic Data
		final Segment segmentE1CUKBM = addChildSegment(iDoc, "E1CUKBM");
		setValue(segmentE1CUKBM, "MSGFN", "004");
		setValue(segmentE1CUKBM, "DEP_INTERN", toUpperCase(dnet.getName()));
		setValue(segmentE1CUKBM, "DEP_TYPE", "6");
		setValue(segmentE1CUKBM, "STATUS", VcmlUtils.createIntFromStatus(dnet.getStatus()));
		setValue(segmentE1CUKBM, "GROUP", toUpperCase(dnet.getGroup()));

		addSegmentsForDescription(segmentE1CUKBM, "E1CUKBT", "DESCRIPT", dnet.getDescription());
		addSegmentsForDependencyDocumentation(segmentE1CUKBM, "E1CUTXM", dnet.getDocumentation());

		for(final Constraint constraint : dnet.getConstraints()) {
			if(!constraint.eIsProxy() && // do not transform proxy objects
				constraint.getDescription()!=null) {
				// Basic data of dependency of dependency net
				final Segment segmentE1CUKB5 = addChildSegment(segmentE1CUKBM, "E1CUKB5");
				setValue(segmentE1CUKB5, "MSGFN", "004");
				setValue(segmentE1CUKB5, "DEP_INTERN", toUpperCase(constraint.getName()));
				setValue(segmentE1CUKB5, "STATUS", VcmlUtils.createIntFromStatus(constraint.getStatus()));
				setValue(segmentE1CUKB5, "DEP_LINENO", "0000"); // TODO number lines?

				addSegmentsForDescription(segmentE1CUKB5, "E1CUKB2", "DESCRIPT", constraint.getDescription());
				addSegmentsForSource(segmentE1CUKB5, constraint);
				addSegmentsForDependencyDocumentation(segmentE1CUKB5, "E1CUTX1", constraint.getDocumentation());
			}
		}
		addSegmentE1DATEM(segmentE1CUKBM, dnet);
		addSegmentE1UPSLINK(iDoc, toUpperCase(dnet.getName()), VcmlUtils.DEFAULT_VALIDITY_START);
		addSegmentE1UPSITM(iDoc, "DEPNET", "DEP", toUpperCase(dnet.getName()), HIELEV_DEPNET, inslev_DEPNET++, 1);
		return Collections.singletonList(iDoc);
	}

	// Master Dependency Source Code
	/**
	 * @param parentSegment
	 * @param source
	 */
	private void addSegmentsForSource(final Segment parentSegment, final Dependency dependency) {
		try {
			InputStream is = sourceUtils.getInputStream(dependency);;
			BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
			String line;
			while ((line = br.readLine()) != null) {
				final Segment segmentE1CUKNM = addChildSegment(parentSegment, "E1CUKNM");
				setValue(segmentE1CUKNM, "MSGFN", "004");
				setValue(segmentE1CUKNM, "LINE", whitespacePattern.matcher(line).replaceAll(" "));
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static Pattern whitespacePattern = Pattern.compile("\\s");

	/**
	 * @param parentSegment
	 * @param segmentType
	 * @param descriptionAttribute
	 * @param description
	 */
	private void addSegmentsForDescription(final Segment parentSegment, final String segmentType, final String descriptionAttribute, final Description description) {
		new DescriptionHandler() {
			@Override
			public void handleSingleDescription(final Language language, final String value) {
				final Segment segment = addChildSegment(parentSegment, segmentType);
				setValue(segment, "MSGFN", "004");
				setValue(segment, "LANGUAGE", VcmlUtils.getLanguageCharacter(language));
				setValue(segment, descriptionAttribute, value);
				setValue(segment, "LANGUAGE_ISO", language.toString());
			}
		}.handleDescription(description);
	}

	@Override
	public List<IDoc> caseProcedure(final Procedure procedure) {
		if(!generateIDocsFor(IVCML2IDocPreferences.KNOMAS) || procedure.getDescription() == null) {
			return Collections.emptyList();
		}

		final IDoc iDoc = createIDocRootSegment("KNOMAS02", "KNOMAS");

		// Master Characteristic Basic Data
		final Segment segmentE1CUKBM = addChildSegment(iDoc, "E1CUKBM");
		setValue(segmentE1CUKBM, "MSGFN", "004");
		setValue(segmentE1CUKBM, "DEP_INTERN", toUpperCase(procedure.getName()));
		setValue(segmentE1CUKBM, "DEP_TYPE", "7");
		setValue(segmentE1CUKBM, "STATUS", VcmlUtils.createIntFromStatus(procedure.getStatus()));
		setValue(segmentE1CUKBM, "GROUP", toUpperCase(procedure.getGroup()));

		addSegmentE1DATEM(segmentE1CUKBM, procedure);

		addSegmentsForDescription(segmentE1CUKBM, "E1CUKBT", "DESCRIPT", procedure.getDescription());
		addSegmentsForSource(segmentE1CUKBM, procedure);
		addSegmentsForDependencyDocumentation(segmentE1CUKBM, "E1CUTXM", procedure.getDocumentation());

		addSegmentE1UPSLINK(iDoc, toUpperCase(procedure.getName()), VcmlUtils.DEFAULT_VALIDITY_START);

		addSegmentE1UPSITM(iDoc, "KNOMAS", "KNO", toUpperCase(procedure.getName()), HIELEV_KNOMAS, inslev_KNOMAS++, 1);

		return Collections.singletonList(iDoc);
	}

	/**
	 * @see org.vclipse.vcml.vcml.util.VcmlSwitch#casePrecondition(org.vclipse.vcml.vcml.Precondition)
	 */
	@Override
	public List<IDoc> casePrecondition(final Precondition precondition) {
		if(!generateIDocsFor(IVCML2IDocPreferences.KNOMAS) || precondition.getDescription() == null) {
			return Collections.emptyList();
		}

		final IDoc iDoc = createIDocRootSegment("KNOMAS02", "KNOMAS");

		// Master Characteristic Basic Data
		final Segment segmentE1CUKBM = addChildSegment(iDoc, "E1CUKBM");
		setValue(segmentE1CUKBM, "MSGFN", "004");
		setValue(segmentE1CUKBM, "DEP_INTERN", toUpperCase(precondition.getName()));
		setValue(segmentE1CUKBM, "DEP_TYPE", "2");
		setValue(segmentE1CUKBM, "STATUS", VcmlUtils.createIntFromStatus(precondition.getStatus()));
		setValue(segmentE1CUKBM, "GROUP", toUpperCase(precondition.getGroup()));

		addSegmentE1DATEM(segmentE1CUKBM, precondition);

		addSegmentsForDescription(segmentE1CUKBM, "E1CUKBT", "DESCRIPT", precondition.getDescription());
		addSegmentsForSource(segmentE1CUKBM, precondition);
		addSegmentsForDependencyDocumentation(segmentE1CUKBM, "E1CUTXM", precondition.getDocumentation());

		addSegmentE1UPSLINK(iDoc, toUpperCase(precondition.getName()), VcmlUtils.DEFAULT_VALIDITY_START);

		addSegmentE1UPSITM(iDoc, "KNOMAS", "KNO", toUpperCase(precondition.getName()), HIELEV_KNOMAS, inslev_KNOMAS++, 1);

		return Collections.singletonList(iDoc);
	}

	@Override
	public List<IDoc> caseSelectionCondition(final SelectionCondition condition) {
		if(!generateIDocsFor(IVCML2IDocPreferences.KNOMAS) || condition.getDescription() == null) {
			return Collections.emptyList();
		}
		final IDoc iDoc = createIDocRootSegment("KNOMAS02", "KNOMAS");
		// Master Characteristic Basic Data
		final Segment segmentE1CUKBM = addChildSegment(iDoc, "E1CUKBM");
		setValue(segmentE1CUKBM, "MSGFN", "004");
		setValue(segmentE1CUKBM, "DEP_INTERN", toUpperCase(condition.getName()));
		setValue(segmentE1CUKBM, "DEP_TYPE", "5");
		setValue(segmentE1CUKBM, "STATUS", VcmlUtils.createIntFromStatus(condition.getStatus()));
		setValue(segmentE1CUKBM, "GROUP", toUpperCase(condition.getGroup()));
		addSegmentE1DATEM(segmentE1CUKBM, condition);
		addSegmentsForDescription(segmentE1CUKBM, "E1CUKBT", "DESCRIPT", condition.getDescription());
		addSegmentsForSource(segmentE1CUKBM, condition);
		addSegmentsForDependencyDocumentation(segmentE1CUKBM, "E1CUTXM", condition.getDocumentation());
		addSegmentE1UPSLINK(iDoc, toUpperCase(condition.getName()), VcmlUtils.DEFAULT_VALIDITY_START);
		addSegmentE1UPSITM(iDoc, "KNOMAS", "KNO", toUpperCase(condition.getName()), HIELEV_KNOMAS, inslev_KNOMAS++, 1);
		return Collections.singletonList(iDoc);
	}

	@Override
	public List<IDoc> caseInterfaceDesign(final InterfaceDesign idesign) {
		if(!generateIDocsFor(IVCML2IDocPreferences.VCUI_SAVEM) || idesign.getCharacteristicGroups().isEmpty()) {
			return Collections.emptyList();
		}
		final IDoc iDoc = createIDocRootSegment("VCUI_SAVEM02", "VCUI_SAVEM");
		// Design Name
		final Segment segmentE1BP_DESIGNNAME = addChildSegment(iDoc, "E1BP_DESIGNNAME");
		setValue(segmentE1BP_DESIGNNAME, "DESIGNNAME", toUpperCase(idesign.getName()));
		for(final CharacteristicGroup group : idesign.getCharacteristicGroups()) {
			// Basic Data of Characteristics Groups in Interface Design
			final Segment segmentE1BP_CHARGROUPS = addChildSegment(iDoc, "E1BP_CHARGROUPS");
			setValue(segmentE1BP_CHARGROUPS, "GROUP_NAME", toUpperCase(group.getName()));
			setValue(segmentE1BP_CHARGROUPS, "DESIGNNAME", toUpperCase(idesign.getName()));
			// TODO adjust values to correct values
			setValue(segmentE1BP_CHARGROUPS, "COL_FROM", "003");
			setValue(segmentE1BP_CHARGROUPS, "LINE_FROM", "001");
			setValue(segmentE1BP_CHARGROUPS, "COL_TO", "080");
			setValue(segmentE1BP_CHARGROUPS, "LINE_TO", "023");
			setValue(segmentE1BP_CHARGROUPS, "TABSTRIP", "X");
		}
		for(final CharacteristicGroup group : idesign.getCharacteristicGroups()) {
			// Language-Dependent Description of Characteristics Groups
			new DescriptionHandler() {
				@Override
				public void handleSingleDescription(final Language language, final String value) {
					final Segment segmentE1BP_CHARGROUPS_LANG = addChildSegment(iDoc, "E1BP_CHARGROUPS_LANG");
					setValue(segmentE1BP_CHARGROUPS_LANG, "GROUP_NAME", toUpperCase(group.getName()));
					setValue(segmentE1BP_CHARGROUPS_LANG, "LANGUAGE_INT", VcmlUtils.getLanguageCharacter(language));
					setValue(segmentE1BP_CHARGROUPS_LANG, "LANGUAGE_ISO", language.toString());
					setValue(segmentE1BP_CHARGROUPS_LANG, "GROUP_TEXT", value);

				};
			}.handleDescription(group.getDescription());
		}
		for(final CharacteristicGroup group : idesign.getCharacteristicGroups()) {
			int line = 1;
			// Basic Data of Characteristics in Characteristics Groups
			final Segment segmentE1BP_CHARS = addChildSegment(iDoc, "E1BP_CHARS");
			setValue(segmentE1BP_CHARS, "GROUP_NAME", toUpperCase(group.getName()));
			setValue(segmentE1BP_CHARS, "LINE", String.format("%1$010d", line));
			setValue(segmentE1BP_CHARS, "START_ROW", "0000000000");
			setValue(segmentE1BP_CHARS, "END_ROW", "0000000000");
			setValue(segmentE1BP_CHARS, "LENGTH", "0000000000");
			setValue(segmentE1BP_CHARS, "NEW_LINE", "X");
			for(final Characteristic cstic : group.getCharacteristics()) {
				line++;
				final Segment segmentE1BP_CHARS1 = addChildSegment(iDoc, "E1BP_CHARS");
				setValue(segmentE1BP_CHARS1, "GROUP_NAME", toUpperCase(group.getName()));
				setValue(segmentE1BP_CHARS1, "DESCR_OR_VALUE", 1);
				setValue(segmentE1BP_CHARS1, "LINE", String.format("%1$010d", line));
				// TODO use correct values
				setValue(segmentE1BP_CHARS1, "START_ROW", "0000000002");
				setValue(segmentE1BP_CHARS1, "END_ROW", "0000000031");
				setValue(segmentE1BP_CHARS1, "LENGTH", "0000000030");
				setValue(segmentE1BP_CHARS1, "NAME_CHAR", toUpperCase(cstic.getName()));
				setValue(segmentE1BP_CHARS1, "NEW_LINE", "X");

				final Segment segmentE1BP_CHARS2 = addChildSegment(iDoc, "E1BP_CHARS");
				setValue(segmentE1BP_CHARS2, "GROUP_NAME", toUpperCase(group.getName()));
				setValue(segmentE1BP_CHARS2, "LINE", String.format("%1$010d", line));
				setValue(segmentE1BP_CHARS2, "DESCR_OR_VALUE", 2);
				setValue(segmentE1BP_CHARS2, "START_ROW", "0000000033");
				setValue(segmentE1BP_CHARS2, "END_ROW", "0000000062");
				setValue(segmentE1BP_CHARS2, "LENGTH", "0000000030");
				setValue(segmentE1BP_CHARS2, "NAME_CHAR", toUpperCase(cstic.getName()));
			}
		}

		// TODO how to set UPSLINK for interface designs?
		// BAPI Ref. Structure for Referring Object to Superord. UPS
		final Segment segmentE1BP_UPSLINK_CORE = addChildSegment(iDoc, "E1BP_UPSLINK_CORE");
		// segmentE1BP_UPSLINK_CORE.setValue("MESTYP", "");
		setValue(segmentE1BP_UPSLINK_CORE, "OBJID", toUpperCase(idesign.getName()));
		setValue(segmentE1BP_UPSLINK_CORE, "OBJVAL", null);
		addSegmentE1UPSITM(iDoc, "VCUI_SAVEM", "VCUI", toUpperCase(idesign.getName()), HIELEV_VCUI_SAVEM, 1, 1); // new inslev per UI design
		return Collections.singletonList(iDoc);
	}

	/**
	 * @see org.vclipse.vcml.vcml.util.VcmlSwitch#caseMaterial(org.vclipse.vcml.vcml.Material)
	 */
	@Override
	public List<IDoc> caseMaterial(final Material material) {
		final List<IDoc> result = new ArrayList<IDoc>();
		if(generateIDocsFor(IVCML2IDocPreferences.MATMAS) && material.getDescription() != null) {
			final IDoc iDoc = createIDocRootSegment("MATMAS05", "MATMAS");
			final Segment segmentE1MARAM = addChildSegment(iDoc, "E1MARAM");
			setValue(segmentE1MARAM, "MSGFN", "004");
			setValue(segmentE1MARAM, "MATNR", toUpperCase(material.getName()));
			setValue(segmentE1MARAM, "MTART", material.getType());
			setValue(segmentE1MARAM, "MBRSH", getIndustrySector());
			setValue(segmentE1MARAM, "MEINS", "PCE");
			setValue(segmentE1MARAM, "TRAGR", getTransportationGroup());

			new DescriptionHandler() {
				@Override
				public void handleSingleDescription(final Language language, final String value) {
					final Segment segmentE1MAKTM = addChildSegment(segmentE1MARAM, "E1MAKTM");
					setValue(segmentE1MAKTM, "MSGFN", "004");
					setValue(segmentE1MAKTM, "SPRAS", VcmlUtils.getLanguageCharacter(language));
					setValue(segmentE1MAKTM, "SPRAS_ISO", language.toString());
					setValue(segmentE1MAKTM, "MAKTX", value);
				}
			}.handleDescription(material.getDescription());

			final Segment segmentE1MARCM = addChildSegment(segmentE1MARAM, "E1MARCM");
			setValue(segmentE1MARCM, "MSGFN", "004");
			setValue(segmentE1MARCM, "WERKS", getPlant());
			setValue(segmentE1MARCM, "LADGR", "Z001");

			final Segment segmentE1MARMM = addChildSegment(segmentE1MARAM, "E1MARMM");
			setValue(segmentE1MARMM, "MSGFN", "004");
			setValue(segmentE1MARMM, "MEINH", "PCE");

			final Segment segmentE1MVKEM = addChildSegment(segmentE1MARAM, "E1MVKEM");
			setValue(segmentE1MVKEM, "MSGFN", "004");
			setValue(segmentE1MVKEM, "VKORG", getSalesOrganisation());
			setValue(segmentE1MVKEM, "VTWEG", getDistributionChannel());

			final Segment segmentE1MLANM = addChildSegment(segmentE1MARAM, "E1MLANM");
			setValue(segmentE1MLANM, "MSGFN", "004");
			setValue(segmentE1MLANM, "ALAND", "DE"); // TODO make this preferences?
			setValue(segmentE1MLANM, "TATY1", "MWST");
			setValue(segmentE1MLANM, "TAXM1", "0");

			addSegmentE1UPSLINK(iDoc, toUpperCase(material.getName()), VcmlUtils.DEFAULT_VALIDITY_START);
			addSegmentE1UPSITM(iDoc, "MATMAS", "MAT", toUpperCase(material.getName()), HIELEV_MATMAS, inslev_MATMAS++, 1);
			result.add(iDoc);
		}
		result.addAll(getIDocsCLFMAS(material));
		return result;
	}

	/**
	 * @param material
	 * @return
	 */
	private List<IDoc> getIDocsCLFMAS(final Material material) {
		final List<IDoc> result = new ArrayList<IDoc>();
		if(generateIDocsFor(IVCML2IDocPreferences.CLFMAS)) {
			// group classes by type
			final Multimap<Integer, Classification> classesByType = ArrayListMultimap.create();
			for(final Classification classification : material.getClassifications()) {
				Class cls = classification.getCls();
				classesByType.put(VcmlUtils.getClassType(toUpperCase(cls.getName())), classification);
			}
			// create CLFMAS IDoc for each class
			for(final Entry<Integer, Collection<Classification>> entry : classesByType.asMap().entrySet()) {
				final int classType = entry.getKey();
				final IDoc iDoc = createIDocRootSegment("CLFMAS02", "CLFMAS");
				// Master Object Classification
				final Segment segmentE1OCLFM = addChildSegment(iDoc, "E1OCLFM");
				setValue(segmentE1OCLFM, "MSGFN", "004");
				setValue(segmentE1OCLFM, "OBTAB", "MARA");
				setValue(segmentE1OCLFM, "OBJEK", toUpperCase(material.getName()));
				setValue(segmentE1OCLFM, "KLART", classType);
				setValue(segmentE1OCLFM, "MAFID", "O");
				setValue(segmentE1OCLFM, "OBJECT_TABLE", "MARA");
				addSegmentE1DATEM(segmentE1OCLFM, material);
				// Distribution Classification: Object Class Assignment
				for(final Classification classification : entry.getValue()) {
					Class cls = classification.getCls();
					final Segment segmentE1KSSKM = addChildSegment(segmentE1OCLFM, "E1KSSKM");
					setValue(segmentE1KSSKM, "MSGFN", "004");
					setValue(segmentE1KSSKM, "CLASS", VcmlUtils.getClassName(toUpperCase(cls.getName())));
					setValue(segmentE1KSSKM, "DATUV", "00000000");
					setValue(segmentE1KSSKM, "STATU", VcmlUtils.createIntFromStatus(cls.getStatus())); // TODO is status neccessary here?
				}
				// Distribution of classification: assigned char. values
				for(final Classification classification : entry.getValue()) {
					for (final ValueAssignment va : classification.getValueAssignments()) {
						for (final Literal literal : va.getValues()) {
							final Segment segmentE1AUSPM = addChildSegment(segmentE1OCLFM, "E1AUSPM");
							setValue(segmentE1AUSPM, "MSGFN", "004");
							Characteristic characteristic = va.getCharacteristic();
							setValue(segmentE1AUSPM, "ATNAM", characteristic.getName());
							// AENNR: Change number
							if (literal instanceof NumericLiteral) {
								BigDecimal value = new BigDecimal(((NumericLiteral)literal).getValue());
								setValue(segmentE1AUSPM, "ATFLV", value.toString()); 
								setValue(segmentE1AUSPM, "ATFLB", value.toString()); 
							} else if (literal instanceof SymbolicLiteral) {
								setValue(segmentE1AUSPM, "ATWRT", ((SymbolicLiteral)literal).getValue());
							} else {
								throw new IllegalArgumentException(literal.toString());
							}
							setValue(segmentE1AUSPM, "ATCOD", 1);
							setValue(segmentE1AUSPM, "ATTLV", 0);
							setValue(segmentE1AUSPM, "ATTLB", 0); 
							setValue(segmentE1AUSPM, "ATINC", 0); 
						}
					}
				}
				addSegmentE1UPSLINK(iDoc, "MARA", VcmlUtils.DEFAULT_VALIDITY_START);
				addSegmentE1UPSITM(iDoc, "CLFMAS", "CLF", String.format("%1$-30s%2$-30s%3$24s", "MARA", toUpperCase(material.getName()), classType + "*"), HIELEV_CLFMAS, inslev_CLFMAS++, 1);
				result.add(iDoc);
			}
		}
		return result;
	}


	/**
	 * @see org.vclipse.vcml.vcml.util.VcmlSwitch#caseVariantFunction(org.vclipse.vcml.vcml.VariantFunction)
	 */
	@Override
	public List<IDoc> caseVariantFunction(final VariantFunction varfunc) {
		if(!generateIDocsFor(IVCML2IDocPreferences.VFNMAS) || varfunc.getDescription() == null) {
			return Collections.emptyList();
		}
		final String name = varfunc.getName();
		final IDoc iDoc = createIDocRootSegment("VFNMAS02", "VFNMAS");

		final Segment segmentE1CUVFM = addChildSegment(iDoc, "E1CUVFM");
		setValue(segmentE1CUVFM, "MSGFN", "004");
		setValue(segmentE1CUVFM, "FUNCTION_NAME", toUpperCase(name));
		setValue(segmentE1CUVFM, "STATUS", VcmlUtils.createIntFromStatusVFT(varfunc.getStatus()));
		setValue(segmentE1CUVFM, "GROUP", toUpperCase(varfunc.getGroup()));

		addSegmentE1DATEM(segmentE1CUVFM, varfunc);

		for(final VariantFunctionArgument argument : varfunc.getArguments()) {
			final Segment segmentE1CUFCM = addChildSegment(segmentE1CUVFM, "E1CUFCM");
			setValue(segmentE1CUFCM, "MSGFN", "004");
			setValue(segmentE1CUFCM, "CHARACT", toUpperCase(argument.getCharacteristic().getName()));
		}

		for(final VariantFunctionArgument argument : varfunc.getArguments()) {
			if(argument.isIn()) {
				final Segment segmentE1CUFIM = addChildSegment(segmentE1CUVFM, "E1CUFIM");
				setValue(segmentE1CUFIM, "MSGFN", "004");
				setValue(segmentE1CUFIM, "ALT_INPUT", "0001");
				setValue(segmentE1CUFIM, "CHARACT", toUpperCase(argument.getCharacteristic().getName()));
			}
		}

		new DescriptionHandler() {
			@Override
			public void handleSingleDescription(final Language language, final String value) {
				final Segment segmentE1CUKBT = addChildSegment(segmentE1CUVFM, "E1CUVFT");
				setValue(segmentE1CUKBT, "MSGFN", "004");
				setValue(segmentE1CUKBT, "DESCRIPTION", value);
				setValue(segmentE1CUKBT, "LANGUAGE_ISO", language.toString()); // segment E1CUVFT has not non-ISO language attribute
			}
		}.handleDescription(varfunc.getDescription());

		addSegmentE1UPSLINK(iDoc, toUpperCase(name), VcmlUtils.DEFAULT_VALIDITY_START);
		addSegmentE1UPSITM(iDoc, "VFNMAS", "VFN", toUpperCase(name), HIELEV_VFNMAS, 1, 1); // new inslev per variant function
		return Collections.singletonList(iDoc);
	}

	/**
	 * @see org.vclipse.vcml.vcml.util.VcmlSwitch#caseVariantTable(org.vclipse.vcml.vcml.VariantTable)
	 */
	@Override
	public List<IDoc> caseVariantTable(final VariantTable vartab) {
		if(!generateIDocsFor(IVCML2IDocPreferences.VTAMAS) || vartab.getDescription() == null) {
			return Collections.emptyList();
		}

		final String name = vartab.getName();
		final IDoc iDoc = createIDocRootSegment("VTAMAS02", "VTAMAS");

		final Segment segmentE1CUVTM = addChildSegment(iDoc, "E1CUVTM");
		setValue(segmentE1CUVTM, "MSGFN", "004");
		setValue(segmentE1CUVTM, "VAR_TAB", toUpperCase(name));
		setValue(segmentE1CUVTM, "STATUS", VcmlUtils.createIntFromStatusVFT(vartab.getStatus()));
		setValue(segmentE1CUVTM, "VTGROUP", toUpperCase(vartab.getGroup()));

		addSegmentE1DATEM(segmentE1CUVTM, vartab);

		for(final VariantTableArgument argument : vartab.getArguments()) {
			final Segment segmentE1CUVCM = addChildSegment(segmentE1CUVTM, "E1CUVCM");
			setValue(segmentE1CUVCM, "MSGFN", "004");
			setValue(segmentE1CUVCM, "CHARACT", toUpperCase(argument.getCharacteristic().getName()));
		}

		for(final VariantTableArgument argument : vartab.getArguments()) {
			if(argument.isKey()) {
				final Segment segmentE1CUVAM = addChildSegment(segmentE1CUVTM, "E1CUVAM");
				setValue(segmentE1CUVAM, "MSGFN", "004");
				setValue(segmentE1CUVAM, "VL_ASSG_NO", "0001"); // TODO verify assignment alternative for variant tables
				setValue(segmentE1CUVAM, "CHARACT", toUpperCase(argument.getCharacteristic().getName()));
			}
		}

		addSegmentsForDescription(segmentE1CUVTM, "E1CUVTT", "DESCRIPT", vartab.getDescription());
		addSegmentE1UPSLINK(iDoc, toUpperCase(name), VcmlUtils.DEFAULT_VALIDITY_START);
		addSegmentE1UPSITM(iDoc, "VTAMAS", "VTA", toUpperCase(vartab.getName()), HIELEV_VTAMAS, 1, 1); // new inslev per variant table
		return Collections.singletonList(iDoc);
	}
	
	@Override
	public List<IDoc> caseVariantTableContent(VariantTableContent tableContent) {
		if(!generateIDocsFor(IVCML2IDocPreferences.VTMMAS)) {
			return Collections.emptyList();
		}
		VariantTable table = tableContent.getTable();
		IDoc iDoc = createIDocRootSegment("VTMMAS02", "VTMMAS");
		
		Segment segmentE1CUVTM = addChildSegment(iDoc, "E1CUVTM");
		setValue(segmentE1CUVTM, "MSGFN", "004");
		String tableName = toUpperCase(table.getName());
		setValue(segmentE1CUVTM, "VAR_TAB", tableName);
		
		addSegmentE1DATEM(segmentE1CUVTM, tableContent);
		
		EList<Row> rows = tableContent.getRows();
		EList<VariantTableArgument> arguments = table.getArguments();
		
		for(Row row : rows) {
			for(VariantTableArgument arg : arguments) {
				int argIndex = arguments.indexOf(arg);
				EList<Literal> rowValues = row.getValues();
				if(argIndex < arguments.size() && argIndex < rowValues.size()) {
					Segment segmentE1CUV1M = addChildSegment(segmentE1CUVTM, "E1CUV1M");
					setValue(segmentE1CUV1M, "MSGFN", "004");
					setValue(segmentE1CUV1M, "VTLINENO", rows.indexOf(row) + 1);
					setValue(segmentE1CUV1M, "VTCHARACT", toUpperCase(arg.getCharacteristic().getName()));
					Literal literal = rowValues.get(argIndex);
					if (literal instanceof NumericLiteral) {
						BigDecimal value = new BigDecimal(((NumericLiteral)literal).getValue());
						setValue(segmentE1CUV1M, "ATFLV", value.toString()); 
						setValue(segmentE1CUV1M, "ATFLB", value.toString()); 
					} else if (literal instanceof SymbolicLiteral) {
						setValue(segmentE1CUV1M, "ATWRT", ((SymbolicLiteral)literal).getValue());
					} else {
						throw new IllegalArgumentException(literal.toString());
					}
					setValue(segmentE1CUV1M, "ATCOD", 1);
					setValue(segmentE1CUV1M, "ATTLV", 0);
					setValue(segmentE1CUV1M, "ATTLB", 0); 
					setValue(segmentE1CUV1M, "ATINC", 0); 
					setValue(segmentE1CUV1M, "VTLINENO5", rows.indexOf(row) + 1);
				}
			}
		}
		addSegmentE1UPSLINK(iDoc, tableName, VcmlUtils.DEFAULT_VALIDITY_START);
		addSegmentE1UPSITM(iDoc, "VTMMAS", "VTM", tableName, HIELEV_VTMMAS, 1, 1);
		return Collections.singletonList(iDoc);
	}
	
	/**
	 * @param parentSegment
	 * @param vcObject TODO
	 * @return
	 */
	private Segment addSegmentE1DATEM(final Segment parentSegment, VCObject vcObject) {
		// Master Key Date and Change Number
		final Segment segmentE1DATEM = addChildSegment(parentSegment, "E1DATEM");
		setValue(segmentE1DATEM, "KEY_DATE", today);
		setValue(segmentE1DATEM, "AENNR", getEcm(vcObject));
		return segmentE1DATEM;
	}

	private String getEcm(VCObject vcObject) {
		for (Option o : vcObject.getOptions()) {
			switch (o.getName()) {
				case ECM: return o.getValue();
				default : return ecm;
			}
		}
		return ecm;
	}

	/**
	 * @param iDoc
	 * @param objId
	 * @param objVal TODO
	 */
	private void addSegmentE1UPSLINK(final IDoc iDoc, final String objId, final String objVal) {
		if(haveUPS()) {
			// Reference from Object to Superior UPS
			final Segment segmentE1UPSLINK = addChildSegment(iDoc, "E1UPSLINK");
			setValue(segmentE1UPSLINK, "OBJID", toUpperCase(objId));
			setValue(segmentE1UPSLINK, "OBJVAL", objVal);
		}
	}

	// helper methods
	/**
	 * @param <T>
	 * @param defaultt
	 * @param value
	 * @return
	 */
	private <T> T withDefault(final T defaultt, final T value) {
		return value == null ? defaultt : value;
	}

	/**
	 * @param segment
	 * @param name
	 * @param value
	 */
	private void setValue(final Segment segment, final String name, final String value) {
		if(value != null) {
			final StringField field = IDOC.createStringField();
			field.setName(toUpperCase(name));
			field.setValue(value);
			segment.getFields().add(field);
		}
	}

	/**
	 * @param segment
	 * @param name
	 * @param value
	 */
	private void setValue(final Segment segment, final String name, final char value) {
		final StringField field = IDOC.createStringField();
		field.setName(toUpperCase(name));
		field.setValue("" + value);
		segment.getFields().add(field);
	}

	/**
	 * @param segment
	 * @param name
	 * @param value
	 */
	private void setValue(final Segment segment, final String name, final int value) {
		final NumberField field = IDOC.createNumberField();
		field.setName(toUpperCase(name));
		field.setValue(value);
		segment.getFields().add(field);
	}

	/**
	 * @param iDoc
	 * @param type
	 * @return
	 */
	private Segment addChildSegment(final IDoc iDoc, final String type) {
		final Segment child = IDOC.createSegment();
		child.setType(type);
		iDoc.getSegments().add(child);
		return child;
	}

	/**
	 * @param segment
	 * @param type
	 * @return
	 */
	private Segment addChildSegment(final Segment segment, final String type) {
		final Segment child = IDOC.createSegment();
		child.setType(type);
		segment.getSegments().add(child);
		return child;
	}

	/**
	 * @param type
	 * @param messageType
	 * @return
	 */
	private IDoc createIDocRootSegment(final String type, final String messageType) {
		final IDoc iDoc = IDOC.createIDoc();
		iDoc.setName(iDocNumber++ + "");
		iDoc.setType(type);
		iDoc.setMessageType(messageType);
		return iDoc;
	}

	/**
	 * @return
	 */
	protected String getPlant() {
		return vcmlPreferenceStore.getString(ISapConstants.PLANT);
	}

	/**
	 * @return
	 */
	protected String getBomUsage() {
		return vcmlPreferenceStore.getString(ISapConstants.BOM_USAGE);
	}

	/**
	 * @return
	 */
	protected String getIndustrySector() {
		return vcmlPreferenceStore.getString(ISapConstants.INDUSTRY_SECTOR);
	}

	/**
	 * @param idocType
	 * @return
	 */
	protected boolean generateIDocsFor(final String idocType) {
		return preferenceStore.getBoolean(idocType);
	}

	/**
	 * @return
	 */
	protected String getUPSTYP() {
		return preferenceStore.getString(IVCML2IDocPreferences.UPSTYPE);
	}

	/**
	 * @return
	 */
	protected String getTransportationGroup() {
		return vcmlPreferenceStore.getString(ISapConstants.TRANSPORTATION_GROUP);
	}

	/**
	 * @return
	 */
	protected String getLoadingGroup() {
		return vcmlPreferenceStore.getString(ISapConstants.LOADING_GROUP);
	}

	/**
	 * @return
	 */
	protected String getSalesOrganisation() {
		return vcmlPreferenceStore.getString(ISapConstants.SALES_ORGANISATION);
	}

	/**
	 * @return
	 */
	protected String getDistributionChannel() {
		return vcmlPreferenceStore.getString(ISapConstants.DISTRIBUTION_CHANNEL);
	}

	/**
	 * @param text
	 * @return
	 */
	private String toUpperCase(final String text) {
		return text == null ? "" : text.toUpperCase(Locale.ENGLISH);
	}
}
