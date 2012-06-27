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
package org.vclipse.vcml.ui.actions.characteristic;

import java.text.ParseException;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.WrappedException;
import org.eclipse.emf.ecore.resource.Resource;
import org.vclipse.vcml.ui.actions.BAPIUtils;
import org.vclipse.vcml.ui.outline.actions.IVcmlOutlineActionHandler;
import org.vclipse.vcml.vcml.Characteristic;
import org.vclipse.vcml.vcml.CharacteristicValue;
import org.vclipse.vcml.vcml.DateCharacteristicValue;
import org.vclipse.vcml.vcml.DateType;
import org.vclipse.vcml.vcml.Documentation;
import org.vclipse.vcml.vcml.FormattedDocumentationBlock;
import org.vclipse.vcml.vcml.Language;
import org.vclipse.vcml.vcml.MultipleLanguageDocumentation;
import org.vclipse.vcml.vcml.MultipleLanguageDocumentation_LanguageBlock;
import org.vclipse.vcml.vcml.NumberListEntry;
import org.vclipse.vcml.vcml.NumericCharacteristicValue;
import org.vclipse.vcml.vcml.NumericInterval;
import org.vclipse.vcml.vcml.NumericLiteral;
import org.vclipse.vcml.vcml.NumericType;
import org.vclipse.vcml.vcml.Option;
import org.vclipse.vcml.vcml.SimpleDocumentation;
import org.vclipse.vcml.vcml.SymbolicType;
import org.vclipse.vcml.vcml.util.VcmlSwitch;
import org.vclipse.vcml.utils.DescriptionHandler;
import org.vclipse.vcml.utils.VcmlUtils;

import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoParameterList;
import com.sap.conn.jco.JCoTable;

public class CharacteristicCreateChangeActionHandler extends BAPIUtils implements IVcmlOutlineActionHandler<Characteristic> {
	
	public void run(final Characteristic object, Resource resource, final IProgressMonitor monitor, Set<String> seenObjects, List<Option> options) throws JCoException {
		final DocumentationHandler documentationHandler = new DocumentationHandler(monitor);
		beginTransaction();
		final JCoFunction function = getJCoFunction("BAPI_CHARACT_CHANGE", monitor);
		JCoParameterList ipl = function.getImportParameterList();
		ipl.setValue("CHARACTNAME", object.getName());
		
		handleOptions(options, ipl, "CHANGENUMBER", "KEYDATE");
		
		final JCoParameterList tpl = function.getTableParameterList();
		final JCoTable charactDetail = tpl.getTable("CHARACTDETAILNEW");
		charactDetail.appendRow();
		charactDetail.setValue("CHARACT_NAME", object.getName());
		new VcmlSwitch<Object>() {
			@Override
			public Object caseNumericType(NumericType ty) {
				charactDetail.setValue("DATA_TYPE", "NUM");
				charactDetail.setValue("LENGTH", ty.getNumberOfChars());
				charactDetail.setValue("DECIMALS", ty.getDecimalPlaces());
				if (ty.isNegativeValuesAllowed()) {
					charactDetail.setValue("WITH_SIGN", "X");
				}
				if (ty.isIntervalValuesAllowed()) {
					charactDetail.setValue("INTERVAL_ALLOWED", "X");
				}
				String unit = ty.getUnit();
				charactDetail.setValue("UNIT_OF_MEASUREMENT", unit);
				JCoTable charactValuesNum = tpl.getTable("CHARACTVALUESNUMNEW");
				charactValuesNum.appendRows(ty.getValues().size());
				for (NumericCharacteristicValue value : ty.getValues()) {
					String flv, flb, relation;
					NumberListEntry entry = value.getEntry();
					if (entry instanceof NumericLiteral) {
						flv = ((NumericLiteral)entry).getValue();
						flb = flv;
						relation = "1"; // means EQ
					} else if (entry instanceof NumericInterval) {
						flv = ((NumericInterval)entry).getLowerBound();
						flb = ((NumericInterval)entry).getUpperBound();
						relation = "3"; // means GE LE
					} else {
						throw new IllegalArgumentException("unknown NumberListEntry " + entry);
					}
					charactValuesNum.setValue("VALUE_FROM", flv);
					charactValuesNum.setValue("VALUE_TO", flb);
					charactValuesNum.setValue("UNIT_FROM", unit);
					charactValuesNum.setValue("UNIT_TO", unit);
					charactValuesNum.setValue("VALUE_RELATION", relation);
					charactValuesNum.nextRow();
				}
				return this;
			}
			@Override
			public Object caseSymbolicType(SymbolicType ty) {
				charactDetail.setValue("DATA_TYPE", "CHAR");
				charactDetail.setValue("LENGTH", ty.getNumberOfChars());
				boolean caseSensitive = ty.isCaseSensitive();
				if (caseSensitive) {
					charactDetail.setValue("CASE_SENSITIV", "X");
				}
				JCoTable charactValuesChar = tpl.getTable("CHARACTVALUESCHARNEW");
				charactValuesChar.appendRows(ty.getValues().size());
				final JCoTable charactValuesDescr = tpl.getTable("CHARACTVALUESDESCRNEW");
				for (CharacteristicValue value : ty.getValues()) {
					final String valueName = caseSensitive ? value.getName() : value.getName().toUpperCase();
					charactValuesChar.setValue("VALUE_CHAR", valueName);
					charactValuesChar.nextRow();
					new DescriptionHandler() {
						@Override
						public void handleSingleDescription(Language language, String descrText) {
							charactValuesDescr.appendRow();
							charactValuesDescr.setValue("VALUE_CHAR", valueName);
							charactValuesDescr.setValue("DESCRIPTION", descrText);
							charactValuesDescr.setValue("LANGUAGE_INT", VcmlUtils.getLanguageCharacter(language));
							charactValuesDescr.setValue("LANGUAGE_ISO", language.toString());
						}
					}.handleDescription(value.getDescription());
				}
				return this;
			}
			@Override
			public Object caseDateType(DateType ty) {
				charactDetail.setValue("DATA_TYPE", "DATE");
				if (ty.isIntervalValuesAllowed()) {
					charactDetail.setValue("INTERVAL_ALLOWED", "X");
				}
				JCoTable charactValuesNum = tpl.getTable("CHARACTVALUESNUMNEW");
				charactValuesNum.appendRows(ty.getValues().size());
				for (DateCharacteristicValue value : ty.getValues()) {
					String flv, relation;
					String from = value.getFrom();
					String to = value.getTo();
					try {
						flv = CharacteristicReader.DATEFORMAT_SAP.format(CharacteristicReader.DATEFORMAT_VCML.parse(from));
					} catch (ParseException e) {
						flv = "00.00.0000";
					}
					charactValuesNum.setValue("VALUE_FROM", flv);
					if (to==null) {
						charactValuesNum.setValue("VALUE_TO", 0);
						relation = "1"; // means EQ
					} else {
						String flb;
						try {
							flb = CharacteristicReader.DATEFORMAT_SAP.format(CharacteristicReader.DATEFORMAT_VCML.parse(to));
						} catch (ParseException e) {
							flb = "00.00.0000";
						}
						charactValuesNum.setValue("VALUE_TO", flb);
						relation = "3"; // means GE LE
					}
					charactValuesNum.setValue("VALUE_RELATION", relation);
					charactValuesNum.nextRow();
				}
				return this;
			}
		}.doSwitch(object.getType());
		charactDetail.setValue("STATUS", VcmlUtils.createIntFromStatus(object.getStatus()));
		charactDetail.setValue("CHARACT_GROUP", object.getGroup());
		// a cstic can be S (single value), R (single value & restricted), M (multi value)
		if (object.isMultiValue()) {
			charactDetail.setValue("VALUE_ASSIGNMENT", "M");
		} else if (object.isRestrictable()) {
			charactDetail.setValue("VALUE_ASSIGNMENT", "R");
		}
		if (object.isRequired()) {
			charactDetail.setValue("ENTRY_REQUIRED", "X");
		}
		if (object.isAdditionalValues()) {
			charactDetail.setValue("ADDITIONAL_VALUES", "X");
		}
		if (object.isNoDisplay()) {
			charactDetail.setValue("NO_DISPLAY", "X");
		}
		if (object.isNotReadyForInput()) {
			charactDetail.setValue("NO_ENTRY", "X");
		}
		if (object.isDisplayAllowedValues()) {
			charactDetail.setValue("DISPLAY_VALUES", "X");
		}
		if (object.getTable()!=null || object.getField()!=null) {
			JCoTable charactReferences = tpl.getTable("CHARACTREFERENCESNEW");
			charactReferences.appendRow();
			charactReferences.setValue("REFERENCE_TABLE", object.getTable());
			charactReferences.setValue("REFERENCE_FIELD", object.getField());
		}
		final JCoTable charactDescr = tpl.getTable("CHARACTDESCRNEW");
		new DescriptionHandler() {
			@Override
			public void handleSingleDescription(Language language, String value) {
				charactDescr.appendRow();
				charactDescr.setValue("DESCRIPTION", value);
				charactDescr.setValue("LANGUAGE_INT", VcmlUtils.getLanguageCharacter(language));
				charactDescr.setValue("LANGUAGE_ISO", language.toString());
			}
		}.handleDescription(object.getDescription());
		execute(function, monitor, object.getName());
		if (processReturnTable(function)) {
			commit(monitor);
		}
		endTransaction();

		// documentation for cstic
		try {
			documentationHandler.handleDocumentation(object);
		} catch (WrappedException e) {
			Exception innerException = e.exception();
			if (innerException instanceof JCoException) {
				throw (JCoException)innerException;
			} else {
				throw e;
			}
		}
		
		// documentation for cstic values
		try {
			new VcmlSwitch<Object>() {
				// TODO allow documentation for numeric values (also in language)
				@Override
				public Object caseSymbolicType(SymbolicType ty) {
					for (CharacteristicValue value : ty.getValues()) {
						documentationHandler.handleDocumentation(value, object);
					}
					return this;
				}
			}.doSwitch(object.getType());
		} catch (WrappedException e) {
			Exception innerException = e.exception();
			if (innerException instanceof JCoException) {
				throw (JCoException)innerException;
			} else {
				throw e;
			}
		}
	}


	public boolean isEnabled(Characteristic object) {
		return isConnected() && hasBody(object); 
	}

	private class DocumentationHandler extends VcmlSwitch<Object> {

		private Characteristic cstic;
		private CharacteristicValue value;
		private IProgressMonitor monitor;
		
		private JCoFunction function;
		private JCoTable longText;
		
		public DocumentationHandler(IProgressMonitor monitor) {
			this.monitor = monitor;
		}
		
		public void handleDocumentation(Characteristic cstic) {
			Documentation documentation = cstic.getDocumentation();
			this.cstic = cstic;
			this.value = null;
			if (documentation!=null) {
				doSwitch(documentation);
			}
		}
		public void handleDocumentation(CharacteristicValue value, Characteristic cstic) {
			Documentation documentation = value.getDocumentation();
			this.cstic = cstic;
			this.value = value;
			if (documentation!=null) {
				doSwitch(documentation);
			}
		}
		
		private void before(Language language) throws JCoException {
			beginTransaction();
			function = getJCoFunction("BAPI_CHARACT_ADDLONGTEXT", monitor);
			JCoParameterList ipl = function.getImportParameterList();
			ipl.setValue("CHARACTNAME", cstic.getName());
			if (value!=null) {
				ipl.setValue("VALUE_CHAR", value.getName());
			}
			ipl.setValue("LANGUAGE_ISO", language.toString());
			JCoParameterList tpl = function.getTableParameterList();
			longText = tpl.getTable("LONGTEXT");
		}
		private void after(Language language) throws JCoException {
			execute(function, monitor, cstic.getName() + " " + (value==null ? "" : value.getName() + " ") + language.toString());
			if (processReturnTable(function)) {
				commit(monitor);
			}
			endTransaction();
		}
		@Override
		public Object caseMultipleLanguageDocumentation(MultipleLanguageDocumentation mld) {
			for(MultipleLanguageDocumentation_LanguageBlock lb : mld.getLanguageblocks()) {
				try {
					Language language = lb.getLanguage();
					before(language);
					for(FormattedDocumentationBlock fdb : lb.getFormattedDocumentationBlocks()) {
						longText.appendRow();
						longText.setValue("TDFORMAT", withDefault(VcmlUtils.DEFAULT_FORMAT, fdb.getFormat())); 
						longText.setValue("TDLINE", fdb.getValue());
					}
					after(language);
				} catch (JCoException e) {
					throw new WrappedException(e); // wraps JCoException in a RuntimeException which does not have to be declared
				}
			}
			return this;
		}
		@Override
		public Object caseSimpleDocumentation(SimpleDocumentation sd) {
			try {
				before(VcmlUtils.getDefaultLanguage());
				longText.appendRow();
				longText.setValue("TDFORMAT", VcmlUtils.DEFAULT_FORMAT); 
				longText.setValue("TDLINE", sd.getValue());
				after(VcmlUtils.getDefaultLanguage());
			} catch (JCoException e) {
				throw new WrappedException(e); // wraps JCoException in a RuntimeException which does not have to be declared
			}
			return this;
		}

	}
	
}
