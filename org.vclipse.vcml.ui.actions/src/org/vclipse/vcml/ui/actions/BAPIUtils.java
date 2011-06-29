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

import java.io.PrintStream;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.xtext.IGrammarAccess;
import org.eclipse.xtext.nodemodel.INode;
import org.eclipse.xtext.parser.IParseResult;
import org.eclipse.xtext.parser.IParser;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.serializer.ISerializer;
import org.eclipse.xtext.util.Strings;
import org.vclipse.connection.IConnectionHandler;
import org.vclipse.connection.VClipseConnectionPlugin;
import org.vclipse.console.CMConsolePlugin;
import org.vclipse.console.CMConsolePlugin.Kind;
import org.vclipse.vcml.VCMLRuntimeModule;
import org.vclipse.vcml.formatting.VCMLPrettyPrinter;
import org.vclipse.vcml.services.VCMLGrammarAccess.ConditionSourceElements;
import org.vclipse.vcml.services.VCMLGrammarAccess.ConstraintSourceElements;
import org.vclipse.vcml.services.VCMLGrammarAccess.ProcedureSourceElements;
import org.vclipse.vcml.ui.VCMLUiPlugin;
import org.vclipse.vcml.ui.outline.actions.OutlineActionCanceledException;
import org.vclipse.vcml.utils.DescriptionHandler;
import org.vclipse.vcml.utils.DocumentationHandler;
import org.vclipse.vcml.utils.ISapConstants;
import org.vclipse.vcml.utils.VCMLUtils;
import org.vclipse.vcml.vcml.ConditionSource;
import org.vclipse.vcml.vcml.ConstraintSource;
import org.vclipse.vcml.vcml.Description;
import org.vclipse.vcml.vcml.Documentation;
import org.vclipse.vcml.vcml.FormattedDocumentationBlock;
import org.vclipse.vcml.vcml.Language;
import org.vclipse.vcml.vcml.MultiLanguageDescription;
import org.vclipse.vcml.vcml.MultiLanguageDescriptions;
import org.vclipse.vcml.vcml.MultipleLanguageDocumentation;
import org.vclipse.vcml.vcml.MultipleLanguageDocumentation_LanguageBlock;
import org.vclipse.vcml.vcml.ProcedureSource;
import org.vclipse.vcml.vcml.SimpleDescription;
import org.vclipse.vcml.vcml.VcmlFactory;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.sap.conn.jco.AbapException;
import com.sap.conn.jco.JCoContext;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoStructure;
import com.sap.conn.jco.JCoTable;

public class BAPIUtils {

	private final Logger log = Logger.getLogger(BAPIUtils.class);

	protected PrintStream task; 
	protected PrintStream res; 
	protected PrintStream err; 
	protected PrintStream warning; 
	protected PrintStream info; 
	
	/**
	 * 
	 */
	protected static final VcmlFactory VCML = VcmlFactory.eINSTANCE;

	@Inject
	private IParser parser;
	
	@Inject
	private IGrammarAccess grammarAccess;

	private JCoFunction currentFunction;

	private VCMLPrettyPrinter prettyPrinter;

	private IPreferenceStore preferenceStore;
	
	protected IConnectionHandler connectionHandler;
	
	/**
	 * 
	 */
	public BAPIUtils() {
		task = new PrintStream(CMConsolePlugin.getDefault().getConsole(Kind.Task));
		res = new PrintStream(CMConsolePlugin.getDefault().getConsole(Kind.Result));
		err = new PrintStream(CMConsolePlugin.getDefault().getConsole(Kind.Error));
		warning = new PrintStream(CMConsolePlugin.getDefault().getConsole(Kind.Warning));
		info = new PrintStream(CMConsolePlugin.getDefault().getConsole(Kind.Info));
	
		// injection TODO injection should be simplified
		Injector injector = Guice.createInjector(new VCMLRuntimeModule());
		parser = injector.getInstance(IParser.class);
		grammarAccess = injector.getInstance(IGrammarAccess.class);
		preferenceStore = VCMLUiPlugin.getDefault().getInjector().getInstance(IPreferenceStore.class);
		prettyPrinter = new VCMLPrettyPrinter();
		connectionHandler = VClipseConnectionPlugin.getDefault().getInjector().getInstance(IConnectionHandler.class);
	}
	
	/**
	 * @param function
	 * @return true if no error occured
	 */
	protected boolean processReturnTable(JCoFunction function) {
//		boolean retval = true;
//		JCoTable table =
//			function.getTableParameterList().getTable("RETURN");
//		for (int i = 0; i < table.getNumRows(); i++) {
//			table.setRow(i);
//			String type = table.getString("TYPE");
//			err.println("/* " + type + "\t" + table.getValue("NUMBER") + "\t" + table.getString("MESSAGE") + "*/");
//			if ("E".equals(type)) {
//				retval = false;
//			}
//		}
		JCoTable table = function.getTableParameterList().getTable("RETURN");
		boolean retval = true;
		boolean printThisMessage = false;
		for (int i = 0; i < table.getNumRows(); i++) {
			table.setRow(i);
			String type = table.getString("TYPE");
			if (!printThisMessage && type!=null && (!"I".equals(type) || table.isLastRow())) {
				printThisMessage = true;
			}
			if (printThisMessage) {
				switch (type.charAt(0)) {
				case 'A': 
					err.println("// ABORT: " + table.getString("MESSAGE"));
					retval = false;
					break;
				case 'E': 
					err.println("// ERROR: " + table.getString("MESSAGE"));
					retval = false;
					break;
				case 'W': 
					warning.println("// WARNING: " + table.getString("MESSAGE"));
					break;
				case 'S': 
					info.println("// SUCCESS: " + table.getString("MESSAGE"));
					break;
				case 'I': 
					info.println("// INFO: " + table.getString("MESSAGE"));
					break;
				default:
					err.println("/* unknown format of table row: " + table.toXML() + " */");
					retval = false;
				}
			}
		}
		if (!printThisMessage) {
			err.println("/* unknown format of message table: " + table.toXML() + " */");
			retval = false;
		}
		return retval;
	}
	
	/**
	 * @param function
	 * @return true if no error occured
	 */
	protected boolean processReturnStructure(JCoFunction function) {
		JCoStructure returnStructure = function.getExportParameterList().getStructure("RETURN");
		String type = returnStructure.getString("TYPE");
		if (!Strings.isEmpty(type)) {
			switch (type.charAt(0)) {
			case 'A': 
				err.println("// ABORT: " + returnStructure.getString("MESSAGE"));
				return false;
			case 'E': 
				err.println("// ERROR: " + returnStructure.getString("MESSAGE"));
				return false;
			case 'W': 
				warning.println("// WARNING: " + returnStructure.getString("MESSAGE"));
				return true;
			case 'S': 
				info.println("// SUCCESS: " + returnStructure.getString("MESSAGE"));
				return true;
			case 'I': 
				info.println("// INFO: " + returnStructure.getString("MESSAGE"));
				return true;
			default:
				err.println("// unknown format of return structure: " + returnStructure.toXML());
				return false;
			}
			// NUMBER is not a member of BAPIRETURN (only BAPIRETURN2?)
		} else {
			return true;
		}
	}

	protected void commit(IProgressMonitor monitor) throws JCoException {
		if (monitor.isCanceled()) 
			throw new OutlineActionCanceledException();
		JCoFunction function = getJCoFunction("BAPI_TRANSACTION_COMMIT", monitor);
		execute(function, monitor, "");
		processReturnStructure(function);
		monitor.worked(1);
	}

	// FIXME adapt to output format of processReturn...
	// FIXME only one output print function for the 3 callers!
	public void handleAbapException(AbapException e) {
		String messageTemplate = null;
		int messageNumber = Integer.parseInt(e.getMessageNumber());
		String messageClass = e.getMessageClass();
		if ("29".equals(messageClass)) {
			switch (messageNumber) {
			case 001: messageTemplate = "BOM not found for this material/plant/usage"; break;
			case 037: messageTemplate = "BOM already exists for & & &"; break;
			case 061: messageTemplate = "Check effectivity data"; break;
			case 102: messageTemplate = "Material & not maintained in plant &"; break;
			case 853: messageTemplate = "Data is incomplete"; break;
			case 885: messageTemplate = "BOM usage & not defined (check your entry)"; break;
			case 887: messageTemplate = "Plant & not defined (please check your entry)"; break;
			}
			
		} else if ("CL".equals(messageClass)) {
			switch (messageNumber) {
			case 768: messageTemplate = ">>> End of processing in API: &"; break;
			}
		} else if ("CU".equals(messageClass)) {
			switch (messageNumber) {
			case 002: messageTemplate = "Dependency & does not exist"; break;
			case 034: messageTemplate = "Dependency status has been set to &"; break;
			case 040: messageTemplate = "Rules and constraints can only be maintained from the dependency net"; break;
			case 041: messageTemplate = "Dependency net cannot be maintained here"; break;
			case 701: messageTemplate = "Configuration profile for material & not yet created"; break;
			case 703: messageTemplate = "Material & has not yet been created"; break;
			case 704: messageTemplate = "Material & is not defined for variant configuration"; break;
			case 759: messageTemplate = "& not defined as a configurable object"; break;
			}
		} else if ("CUMTX".equals(messageClass)) {
			switch (messageNumber) {
			case 066: messageTemplate = "Language & is not defined"; break; 
			case 197: messageTemplate = "& & has not been created"; break;
			case 199: messageTemplate = "& & does not exist"; break;
			case 200: messageTemplate = "& & is being processed by &"; break;
			case 235: messageTemplate = "Enter &"; break;
			case 240: messageTemplate = "Released status of function has to be canceled"; break;
			}
		} else if ("M3".equals(messageClass)) {
			switch (messageNumber) {
			case 305: messageTemplate = "The material & does not exist or is not activated"; break;
			case 351: messageTemplate = "Material & not maintained in plant &"; break;
			case 752: messageTemplate = "No material number transferred"; break;
			}
		}
		if (messageTemplate!=null) {
			StringBuffer instantiatedMessage = new StringBuffer(messageTemplate);
			for(String p : e.getMessageParameters()) {
				if (p==null) break; // no more parameters
				int ampPosition = instantiatedMessage.indexOf("&");
				if (ampPosition==-1) break; // no & in instantiatedMessage
				instantiatedMessage.replace(ampPosition, ampPosition+1, p);
			}
			err.println("// " + e.getLocalizedMessage() + ": " + instantiatedMessage);
			return;
		}
		err.println("### failed: " + currentFunction.toXML());
		err.println("### AbapException");
		err.println("key: " + e.getKey());
		for(String p : e.getMessageParameters()) {
			err.println("parameter: " + p);
		}
		err.println("message: " + e.getMessage());
		err.println("messageNumber: " + messageNumber);
		err.println("group: " + e.getGroup());
		err.println("messageClass: " + messageClass);
		err.println("messageText: " + e.getMessageText());
		err.println("messageType: " + e.getMessageType());
		err.println("localizedMessage : " + e.getLocalizedMessage());
		// err.println(e.getCause());
		err.println("Use SE91 to query error message using message class " + messageClass + " and message number " + e.getMessageNumber());
	}

	
	protected Description readDescription(JCoTable table, String languageFieldISO, String languageField, String descriptionField) {
		return simplifyMultiLanguageDescriptions(readMultiLanguageDescriptions(table, languageFieldISO, languageField, descriptionField));
	}

	protected MultiLanguageDescriptions readMultiLanguageDescriptions(JCoTable table, String languageFieldISO, String languageField, String descriptionField) {
		MultiLanguageDescriptions multiLanguageDescriptions = VCML.createMultiLanguageDescriptions();
		EList<MultiLanguageDescription> descriptions = multiLanguageDescriptions.getDescriptions();
		for (int i = 0; i < table.getNumRows(); i++) {
			table.setRow(i);
			MultiLanguageDescription multiLanguageDescription = VCML.createMultiLanguageDescription();
			Language language; 
			String languageISO = table.getString(languageFieldISO);
			if (languageISO==null || languageISO.length()!=2) { // heuristics to test whether languageISO is a valid ISO language: must be a string of length 2
				language = VCMLUtils.getLanguageByCharacter(table.getChar(languageField));
			} else {
				language = VCMLUtils.getLanguageByISOString(languageISO);
			}
			multiLanguageDescription.setLanguage(language);
			multiLanguageDescription.setValue(table.getString(descriptionField));
			descriptions.add(multiLanguageDescription);
		}
		return multiLanguageDescriptions;
	}

	protected Description simplifyDescription(Description description) {
		if (description instanceof MultiLanguageDescriptions)
			return simplifyMultiLanguageDescriptions((MultiLanguageDescriptions)description);
		else
			return description;
	}
	
	private Description simplifyMultiLanguageDescriptions(MultiLanguageDescriptions multiLanguageDescriptions) {
		switch (multiLanguageDescriptions.getDescriptions().size()) {
		case 0: 
			return null;
		case 1: 
			MultiLanguageDescription multiLanguageDescription = multiLanguageDescriptions.getDescriptions().get(0);
			if (VCMLUtils.getDefaultLanguage().equals(multiLanguageDescription.getLanguage())) {
				SimpleDescription description = VCML.createSimpleDescription();
				description.setValue(multiLanguageDescription.getValue());
				return description;
			}
		}
		return multiLanguageDescriptions;
	}

	protected MultipleLanguageDocumentation readMultiLanguageDocumentations(JCoTable table) {
		if (table.isEmpty())
			return null;
		Multimap<Language, Integer> language2Rows = ArrayListMultimap.create();
		for (int i = 0; i < table.getNumRows(); i++) {
			table.setRow(i);
			Language language; 
			String languageISO = table.getString("LANGUAGE_ISO");
			if (Strings.isEmpty(languageISO)) {
				language = VCMLUtils.getLanguageByCharacter(table.getChar("LANGUAGE"));
			} else {
				language = VCMLUtils.getLanguageByISOString(languageISO);
			}
			language2Rows.put(language, i);
		}
		
		MultipleLanguageDocumentation multipleLanguageDocumentation = VCML.createMultipleLanguageDocumentation();
		EList<MultipleLanguageDocumentation_LanguageBlock> languageBlocks = multipleLanguageDocumentation.getLanguageblocks();
		for(java.util.Map.Entry<Language, Collection<Integer>> entry : language2Rows.asMap().entrySet()) {
			MultipleLanguageDocumentation_LanguageBlock languageBlock = VCML.createMultipleLanguageDocumentation_LanguageBlock();
			languageBlocks.add(languageBlock);
			languageBlock.setLanguage(entry.getKey());
			EList<FormattedDocumentationBlock> formattedDocumentationBlocks = languageBlock.getFormattedDocumentationBlocks();
			Collection<Integer> indices = entry.getValue();
			for (int i : indices) {
				table.setRow(i);
				FormattedDocumentationBlock fdb = VCML.createFormattedDocumentationBlock();
				formattedDocumentationBlocks.add(fdb);
				fdb.setFormat(table.getString("TXT_FORM"));
				fdb.setValue(table.getString("TXT_LINE"));
			}
		}
		return multipleLanguageDocumentation;
	}

	protected void writeDocumentation(JCoTable table, Documentation documentation) {
		new BAPIDocumentationHandler(table).doSwitch(documentation);
	}
	
	protected void writeDescription(final JCoTable table, Description description) {
		new DescriptionHandler() {
			@Override
			public void handleSingleDescription(Language language, String value) {
				table.appendRow();
				table.setValue("DESCRIPT", value);
				table.setValue("LANGUAGE", VCMLUtils.getLanguageCharacter(language));
				table.setValue("LANGUAGE_ISO", language.toString());
			}
		}.handleDescription(description);
	}

	protected void writeSourceCode(JCoTable table, EObject source) {
		if(source==null) {
			return;
		} else {
			String sourceCode = "";
			if(preferenceStore.getBoolean(ISapConstants.USE_PRETTY_PRINTER)) {
				sourceCode = prettyPrinter.prettyPrint(source);
			} else {
				ISerializer serializer = ((XtextResource)(source.eResource())).getSerializer();
				sourceCode = serializer.serialize(source);
			}
			int lineNo = 1;
			String[] lines = sourceCode.split("\n");
			table.appendRows(lines.length);
			for(String line : lines) {
				table.setValue("LINE_NO", lineNo++);
				table.setValue("LINE", line);
				table.nextRow();
			}
		}
	}
	
	protected ProcedureSource readProcedureSource(JCoTable table) {
		StringBuffer source = readSourceLines(table);
		ProcedureSourceElements elements = ((org.vclipse.vcml.services.VCMLGrammarAccess)grammarAccess).getProcedureSourceAccess();
    	IParseResult result = parser.parse(elements.getRule(), new StringReader(source.toString()));
    	Iterator<INode> iterator = result.getSyntaxErrors().iterator();
    	if(iterator.hasNext()) {
    		printParseErrors(iterator, source);
			err.println(source);
			return null;
    	} else {
    		return (ProcedureSource)result.getRootASTElement();
    	}
	}

	protected ConditionSource readConditionSource(JCoTable table) {
		StringBuffer source = readSourceLines(table);
		ConditionSourceElements elements = ((org.vclipse.vcml.services.VCMLGrammarAccess)grammarAccess).getConditionSourceAccess();
    	IParseResult result = parser.parse(elements.getRule(), new StringReader(source.toString()));
    	Iterator<INode> iterator = result.getSyntaxErrors().iterator();
    	if(iterator.hasNext()) {
    		printParseErrors(iterator, source);
    		return null;
    	} else {
    		return (ConditionSource)result.getRootASTElement();
    	}
	}
	
	protected ConstraintSource readConstraintSource(JCoTable table) {
		StringBuffer source = readSourceLines(table);
		ConstraintSourceElements elements = ((org.vclipse.vcml.services.VCMLGrammarAccess)grammarAccess).getConstraintSourceAccess();
    	IParseResult result = parser.parse(elements.getRule(), new StringReader(source.toString()));
    	Iterator<INode> it = result.getSyntaxErrors().iterator();
    	if(it.hasNext()) {
    		printParseErrors(it, source);
			return null;
    	} else {
    		return (ConstraintSource)result.getRootASTElement();
    	}
	}

	private StringBuffer readSourceLines(JCoTable table) {
		StringBuffer source = new StringBuffer();
		if (table.getNumRows() > 0) {
			for (int i = 0; i < table.getNumRows(); i++) {
				table.setRow(i);
				source.append("\n"); // must start with newline since SAP comments start with newline!
				source.append(table.getString("LINE"));
			}
		}
		return source;
	}
	
	private void printParseErrors(Iterator<INode> parseErrors, StringBuffer source) {
		while(parseErrors.hasNext()) {
			INode node = parseErrors.next();
			err.println("// syntax error: " + node.getText() + " in line " + node.getStartLine());
		}
		err.println("/*");
		err.println(source);
		err.println("*/");
	}
	
	public boolean isConnected() {
		return connectionHandler.getCurrentConnection() != null;
	}

	protected String getPlant() {
		return preferenceStore.getString(ISapConstants.PLANT);
	}

	protected String getBomUsage() {
		return preferenceStore.getString(ISapConstants.BOM_USAGE);
	}

	protected String getIndustrySector() {
		return preferenceStore.getString(ISapConstants.INDUSTRY_SECTOR);
	}

	protected String getTransportationGroup() {
		return preferenceStore.getString(ISapConstants.TRANSPORTATION_GROUP);
	}
	
	protected String getLoadingGroup() {
		return preferenceStore.getString(ISapConstants.LOADING_GROUP);
	}
	
	protected String getSalesOrganisation() {
		return preferenceStore.getString(ISapConstants.SALES_ORGANISATION);
	}
	
	protected String getDistributionChannel() {
		return preferenceStore.getString(ISapConstants.DISTRIBUTION_CHANNEL);
	}

	protected JCoFunction getJCoFunction(String name, IProgressMonitor monitor)	throws JCoException {
		if (monitor.isCanceled()) 
			throw new OutlineActionCanceledException();
		monitor.subTask("retrieving metadata for " +  name);
		JCoFunction function = connectionHandler.getJCoFunction(name);
		monitor.worked(1);
		currentFunction = function;
		return function;
	}

	protected void execute(JCoFunction function, IProgressMonitor monitor, String arguments)
			throws JCoException {
		if (monitor.isCanceled()) 
			throw new OutlineActionCanceledException();
		monitor.subTask("executing " + function.getName() + " " + arguments);
		task.println("// " + function.getName() + " " + arguments);
		function.execute(connectionHandler.getJCoDestination());
		if (log.isTraceEnabled()) {
			log.trace(function.toXML());
		};
		monitor.worked(1);
	}

	protected void endTransaction() throws JCoException {
		JCoContext.end(connectionHandler.getJCoDestination());
	}

	protected void beginTransaction() throws JCoException {
		JCoContext.begin(connectionHandler.getJCoDestination());
	}

	// helper methods
	protected <T> T withDefault(T dflt, T value) {
		return value==null ? dflt : value;
	}
	
	protected String nullIfEmpty(String string) {
		return string == null ? null : "".equals(string) ? null : string;
	}
	
	protected String nullIfEquals(String dflt, String string) {
		return string == null ? null : dflt.equals(string) ? null : string;
	}

	protected String getToday() {
		return new SimpleDateFormat("yyyyMMdd").format(new Date());
	}
	
	private class BAPIDocumentationHandler extends DocumentationHandler {

		private JCoTable documentationTable;
		private int lineNo;
		
		public BAPIDocumentationHandler(JCoTable documentationTable) {
			this.documentationTable = documentationTable;
			this.lineNo = 1;
		}

		@Override
		public void addDocumentationEntry(Language language, String text,
				String format) {
			documentationTable.appendRow();
			documentationTable.setValue("LINE_NO", lineNo++);
			documentationTable.setValue("TXT_FORM", format);
			documentationTable.setValue("TXT_LINE", text);
			documentationTable.setValue("LANGUAGE", VCMLUtils.getLanguageCharacter(language));
			documentationTable.setValue("LANGUAGE_ISO", language.toString());
		}

	}
	
}

// http://help.sap.com/saphelp_45b/helpdata/de/92/58b469417011d189ec0000e81ddfac/frameset.htm (APIs der Logistik)
