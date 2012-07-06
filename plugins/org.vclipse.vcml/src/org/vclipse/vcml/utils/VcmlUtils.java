/*******************************************************************************
 * Copyright (c) 2010 webXcerpt Software GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    webXcerpt Software GmbH - initial creator
 ******************************************************************************/
package org.vclipse.vcml.utils;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.common.util.EList;
import org.vclipse.vcml.VCMLPlugin;
import org.vclipse.vcml.vcml.Assignment;
import org.vclipse.vcml.vcml.BinaryCondition;
import org.vclipse.vcml.vcml.BinaryExpression;
import org.vclipse.vcml.vcml.Characteristic;
import org.vclipse.vcml.vcml.CharacteristicReference_C;
import org.vclipse.vcml.vcml.CharacteristicReference_P;
import org.vclipse.vcml.vcml.Comparison;
import org.vclipse.vcml.vcml.ComparisonOperator;
import org.vclipse.vcml.vcml.CompoundStatement;
import org.vclipse.vcml.vcml.Condition;
import org.vclipse.vcml.vcml.ConditionalConstraintRestriction;
import org.vclipse.vcml.vcml.ConditionalStatement;
import org.vclipse.vcml.vcml.ConstraintObject;
import org.vclipse.vcml.vcml.ConstraintRestriction;
import org.vclipse.vcml.vcml.ConstraintRestrictionFalse;
import org.vclipse.vcml.vcml.DelDefault;
import org.vclipse.vcml.vcml.Expression;
import org.vclipse.vcml.vcml.Fixing;
import org.vclipse.vcml.vcml.FunctionCall;
import org.vclipse.vcml.vcml.FunctionName;
import org.vclipse.vcml.vcml.InCondition_C;
import org.vclipse.vcml.vcml.InCondition_P;
import org.vclipse.vcml.vcml.IsInvisible;
import org.vclipse.vcml.vcml.IsSpecified_C;
import org.vclipse.vcml.vcml.IsSpecified_P;
import org.vclipse.vcml.vcml.Language;
import org.vclipse.vcml.vcml.List;
import org.vclipse.vcml.vcml.Literal;
import org.vclipse.vcml.vcml.Model;
import org.vclipse.vcml.vcml.NumberList;
import org.vclipse.vcml.vcml.NumberListEntry;
import org.vclipse.vcml.vcml.NumericInterval;
import org.vclipse.vcml.vcml.NumericLiteral;
import org.vclipse.vcml.vcml.ObjectCharacteristicReference;
import org.vclipse.vcml.vcml.Option;
import org.vclipse.vcml.vcml.OptionType;
import org.vclipse.vcml.vcml.PFunction;
import org.vclipse.vcml.vcml.PartOfCondition;
import org.vclipse.vcml.vcml.ProcedureLocation;
import org.vclipse.vcml.vcml.SetDefault;
import org.vclipse.vcml.vcml.Statement;
import org.vclipse.vcml.vcml.Status;
import org.vclipse.vcml.vcml.SumParts;
import org.vclipse.vcml.vcml.SymbolList;
import org.vclipse.vcml.vcml.SymbolicLiteral;
import org.vclipse.vcml.vcml.UnaryCondition;
import org.vclipse.vcml.vcml.UnaryExpression;
import org.vclipse.vcml.vcml.UnaryExpressionOperator;
import org.vclipse.vcml.vcml.VariantFunction;
import org.vclipse.vcml.vcml.VcmlFactory;



public class VcmlUtils {

	public static final String DEFAULT_FORMAT = "*";
	
	public static final String DEFAULT_VALIDITY_START = "19000101";

	// see http://help.sap.com/saphelp_nw04/helpdata/en/c1/ae563cd2ad4f0ce10000000a11402f/content.htm
	private static Map<Language, Character> languageIso2languageMap = new HashMap<Language, Character>();
	static {
		languageIso2languageMap.put(Language.AF, 'a');
		languageIso2languageMap.put(Language.AR, 'A');
		languageIso2languageMap.put(Language.BG, 'W');
		languageIso2languageMap.put(Language.CA, 'c');
		languageIso2languageMap.put(Language.CS, 'C');
		languageIso2languageMap.put(Language.DA, 'K');
		languageIso2languageMap.put(Language.DE, 'D');
		languageIso2languageMap.put(Language.EL, 'G');
		languageIso2languageMap.put(Language.EN, 'E');
		languageIso2languageMap.put(Language.ES, 'S');
		languageIso2languageMap.put(Language.ET, '9');
		languageIso2languageMap.put(Language.FI, 'U');
		languageIso2languageMap.put(Language.FR, 'F');
		languageIso2languageMap.put(Language.HE, 'B');
		languageIso2languageMap.put(Language.HR, '6');
		languageIso2languageMap.put(Language.HU, 'H');
		languageIso2languageMap.put(Language.ID, 'i');
		languageIso2languageMap.put(Language.IS, 'b');
		languageIso2languageMap.put(Language.IT, 'I');
		languageIso2languageMap.put(Language.JA, 'J');
		languageIso2languageMap.put(Language.KO, '3');
		languageIso2languageMap.put(Language.LT, 'X');
		languageIso2languageMap.put(Language.LV, 'Y');
		languageIso2languageMap.put(Language.MS, '7');
		languageIso2languageMap.put(Language.NL, 'N');
		languageIso2languageMap.put(Language.NO, 'O');
		languageIso2languageMap.put(Language.PL, 'L');
		languageIso2languageMap.put(Language.PT, 'P');
		languageIso2languageMap.put(Language.RO, '4');
		languageIso2languageMap.put(Language.RU, 'R');
		languageIso2languageMap.put(Language.SH, 'd');
		languageIso2languageMap.put(Language.SK, 'Q');
		languageIso2languageMap.put(Language.SL, '5');
		languageIso2languageMap.put(Language.SR, '0');
		languageIso2languageMap.put(Language.SV, 'V');
		languageIso2languageMap.put(Language.TH, '2');
		languageIso2languageMap.put(Language.TR, 'T');
		languageIso2languageMap.put(Language.UK, '8');
		languageIso2languageMap.put(Language.Z1, 'Z');
		languageIso2languageMap.put(Language.ZF, 'M');
		languageIso2languageMap.put(Language.ZH, '1');
	}
	
	private static Map<Language, String> language2descriptionMap = new HashMap<Language, String>();
	static {
		language2descriptionMap.put(Language.AF, "Afrikaans");
		language2descriptionMap.put(Language.AR, "Arabic");
		language2descriptionMap.put(Language.BG, "Bulgarian");
		language2descriptionMap.put(Language.CA, "Catalan");
		language2descriptionMap.put(Language.CS, "Czech");
		language2descriptionMap.put(Language.DA, "Danish");
		language2descriptionMap.put(Language.DE, "German");
		language2descriptionMap.put(Language.EL, "Greek");
		language2descriptionMap.put(Language.EN, "English");
		language2descriptionMap.put(Language.ES, "Spanish");
		language2descriptionMap.put(Language.ET, "Estonian");
		language2descriptionMap.put(Language.FI, "Finnish");
		language2descriptionMap.put(Language.FR, "French");
		language2descriptionMap.put(Language.HE, "Hebrew");
		language2descriptionMap.put(Language.HR, "Croatian");
		language2descriptionMap.put(Language.HU, "Hungarian");
		language2descriptionMap.put(Language.ID, "Indonesian");
		language2descriptionMap.put(Language.IS, "Icelandic");
		language2descriptionMap.put(Language.IT, "Italian");
		language2descriptionMap.put(Language.JA, "Japanese");
		language2descriptionMap.put(Language.KO, "Korean");
		language2descriptionMap.put(Language.LT, "Lithuanian");
		language2descriptionMap.put(Language.LV, "Latvian");
		language2descriptionMap.put(Language.MS, "Malaysian");
		language2descriptionMap.put(Language.NL, "Dutch");
		language2descriptionMap.put(Language.NO, "Norwegian");
		language2descriptionMap.put(Language.PL, "Polish");
		language2descriptionMap.put(Language.PT, "Portuguese");
		language2descriptionMap.put(Language.RO, "Romanian");
		language2descriptionMap.put(Language.RU, "Russian");
		language2descriptionMap.put(Language.SH, "Serbian (Latin)");
		language2descriptionMap.put(Language.SK, "Slovakian");
		language2descriptionMap.put(Language.SL, "Slovenian");
		language2descriptionMap.put(Language.SR, "Serbian");
		language2descriptionMap.put(Language.SV, "Swedish");
		language2descriptionMap.put(Language.TH, "Thai");
		language2descriptionMap.put(Language.TR, "Turkish");
		language2descriptionMap.put(Language.UK, "Ukrainian");
		language2descriptionMap.put(Language.Z1, "Customer reserve");
		language2descriptionMap.put(Language.ZF, "Chinese trad.");
		language2descriptionMap.put(Language.ZH, "Chinese");
	}
	
	private static Map<Character, Language> languageCharacter2language = new HashMap<Character, Language>();
	static {
		for (Map.Entry<Language, Character> entry : languageIso2languageMap.entrySet())
			languageCharacter2language.put(entry.getValue(), entry.getKey());
	}
	
	private static Map<String, Language> languageISO2language = new HashMap<String, Language>();
	static {
		for (Language language : Language.VALUES)
			languageISO2language.put(language.toString(), language);
	}
	
	public static char getLanguageCharacter(Language language) {
		return languageIso2languageMap.get(language);
	}
	
	public static Language getLanguageByISOString(String isoLanguageString) {
		return languageISO2language.get(isoLanguageString);
	}
	
	public static Language getLanguageByCharacter(char languageCharacter) {
		return languageCharacter2language.get(languageCharacter);
	}

	public static String getLanguageDescription(Language language) {
		return language2descriptionMap.get(language);
	}
	
	public static Language getDefaultLanguage() {
		return Language.get(Platform.getPreferencesService().getString(VCMLPlugin.PREFERENCES_ID, ISapConstants.DEFAULT_LANGUAGE, Language.EN.getLiteral(), null));
	}
	
	public static Status createStatusFromInt(int status) {
		switch (status) {
		case 1:	return Status.RELEASED;
		case 2:	return Status.IN_PREPARATION;
		case 3:	return Status.LOCKED;
		default: return null;
		}
	}

	// for variant functions and tables
	public static Status createStatusFromIntVFT(int status) {
		switch (status) {
		case 0:	return Status.IN_PREPARATION;
		case 1:	return Status.RELEASED;
		case 2:	return Status.LOCKED;
		default: return null;
		}
	}

	public static int createIntFromStatus(Status status) {
		switch (status) {
		case RELEASED: return 1;
		case IN_PREPARATION: return 2;
		case LOCKED: return 3;
		default: return 0;
		}
	}
	
	// for variant functions and tables
	public static int createIntFromStatusVFT(Status status) {
		switch (status) {
		case IN_PREPARATION: return 0;
		case RELEASED: return 1;
		case LOCKED: return 2;
		default: return 0;
		}
	}
	
	static private Pattern CLASSSPEC_PATTERN = Pattern.compile("\\(\\s*(\\d+)\\s*\\)\\s*(\\S+)"); // (INT)ID, capturing group 1 is INT, capturing group 2 is ID
	
	public static int getClassType(String classSpec) {
		 Matcher m = CLASSSPEC_PATTERN.matcher(classSpec);
		 if (m.matches()) {
			 return Integer.parseInt(m.group(1));
		 } else {
			 throw new IllegalArgumentException(classSpec + " is not a class specification with format '(INT)ID'");
		 }
	}
	
	public static String getClassName(String classSpec) {
		 Matcher m = CLASSSPEC_PATTERN.matcher(classSpec);
		 if (m.matches()) {
			 return m.group(2);
		 } else {
			 throw new IllegalArgumentException(classSpec + " is not a class specification with format '(INT)ID'");
		 }
	}
	
	public static String getLiteralName(Literal literal) {
		if(literal instanceof SymbolicLiteral) {
			return ((SymbolicLiteral)literal).getValue();
		} else if(literal instanceof NumericLiteral) {
			return ((NumericLiteral)literal).getValue();
		} else {
			throw new IllegalArgumentException(literal.toString());
		}
	}

	
	private static final VcmlFactory VCML = VcmlFactory.eINSTANCE;

	static public Model mkModel() {
		return VCML.createModel();
	}

	static public Option mkOption(final OptionType type, final String value) {
		final Option option = VCML.createOption();
		option.setName(type);
		option.setValue(value);
		return option;
	}

	static public ComparisonOperator getComparisonOperator(final String text) {
		if("==".equals(text)) {
			return ComparisonOperator.EQ;
		} else if("!=".equals(text)) {
			return ComparisonOperator.NE;
		} else if(">=".equals(text)) {
			return ComparisonOperator.GE;
		} else if(">".equals(text)) {
			return ComparisonOperator.GT;
		} else if("<=".equals(text)) {
			return ComparisonOperator.LE;
		} else if("<".equals(text)) {
			return ComparisonOperator.LT;
		} else {
			throw new IllegalArgumentException("Invalid comparison operator " + text);
		}
	}

	static public Assignment mkAssignment(final Characteristic cstic, final Expression result) {
		final Assignment object = VCML.createAssignment();
		object.setCharacteristic(cstic);
		object.setExpression(result);
		return object;
	}

	static public BinaryCondition mkBinaryCondition(final String operator, final Condition left, final Condition right) {
		final BinaryCondition object = VCML.createBinaryCondition();
		object.setOperator(operator);
		object.setLeft(left);
		object.setRight(right);
		return object;
	}

	static public BinaryExpression mkBinaryExpression(final String operator, final Expression left, final Expression right) {
		final BinaryExpression object = VCML.createBinaryExpression();
		object.setLeft(left);
		object.setOperator(operator);
		object.setRight(right);
		return object;
	}

	static public CharacteristicReference_P mkCharacteristicReference(final Characteristic cstic, final ProcedureLocation location) {
		final CharacteristicReference_P object = VCML.createCharacteristicReference_P();
		object.setCharacteristic(cstic);
		object.setLocation(location);
		return object;
	}

	static public Comparison mkComparison(final ComparisonOperator operator, final Expression left, final Expression right) {
		final Comparison object = VCML.createComparison();
		object.setLeft(left);
		object.setOperator(operator);
		object.setRight(right);
		return object;
	}

	static public CompoundStatement mkCompoundStatement() {
		return VCML.createCompoundStatement();
	}

	static public ConditionalConstraintRestriction mkConditionalConstraintRestriction(final Condition condition, final ConstraintRestriction restriction) {
		final ConditionalConstraintRestriction object = VCML.createConditionalConstraintRestriction();
		object.setCondition(condition);
		object.setRestriction(restriction);
		return object;
	}

	static public ConditionalStatement mkConditionalStatement(final Statement statement, final Condition condition) {
		final ConditionalStatement object = VCML.createConditionalStatement();
		object.setStatement(statement);
		object.setCondition(condition);
		return object;
	}

	static public ConditionalConstraintRestriction mkConstraintRestriction(final ConstraintRestriction restriction, final Condition condition) {
		final ConditionalConstraintRestriction ccRestriction = VCML.createConditionalConstraintRestriction();
		ccRestriction.setRestriction(restriction);
		ccRestriction.setCondition(condition);
		return ccRestriction;
	}

	static public ConstraintRestrictionFalse mkConstraintRestrictionFalse() {
		return VCML.createConstraintRestrictionFalse();
	}

	static public DelDefault mkDelDefault(final Characteristic cstic, final Expression expression) {
		 final DelDefault object = VCML.createDelDefault();
		 object.setCharacteristic(cstic);
		 object.setExpression(expression);
		 return object;
	}

	static public FunctionCall mkFunctionCall(final FunctionName functionName, final Expression expression) {
		final FunctionCall call = VCML.createFunctionCall();
		call.setFunction(functionName);
		call.setArgument(expression);
		return call;
	}

	static public InCondition_P mkInCondition(final CharacteristicReference_P ref) {
		final InCondition_P object = VCML.createInCondition_P();
		object.setCharacteristic(ref);
		return object;
	}

	static public InCondition_P mkInCondition(final CharacteristicReference_P ref, final List list) {
		final InCondition_P object = VCML.createInCondition_P();
		object.setCharacteristic(ref);
		object.setList(list);
		return object;
	}

	static public InCondition_C mkInCondition_C(final CharacteristicReference_C ref) {
		final InCondition_C object = VCML.createInCondition_C();
		object.setCharacteristic(ref);
		return object;
	}

	static public InCondition_C mkInCondition_C(final CharacteristicReference_C ref, final List list) {
		final InCondition_C object = mkInCondition_C(ref);
		object.setList(list);
		return object;
	}

	static public IsInvisible mkIsInvisible(final Characteristic cstic) {
		final IsInvisible object = VCML.createIsInvisible();
		object.setCharacteristic(cstic);
		return object;
	}

	static public IsSpecified_C mkIsSpecified_C(final CharacteristicReference_C ref) {
		final IsSpecified_C object = VCML.createIsSpecified_C();
		object.setCharacteristic(ref);
		return object;
	}

	static public IsSpecified_P mkIsSpecified_P(final CharacteristicReference_P ref) {
		final IsSpecified_P object = VCML.createIsSpecified_P();
		object.setCharacteristic(ref);
		return object;
	}

	static public NumberList mkNumberList() {
		return VCML.createNumberList();
	}

	static public NumberList mkNumberListBigDecimal(final EList<BigDecimal> values) {
		final NumberList list = mkNumberList();
		final EList<NumberListEntry> entries = list.getEntries();
		for(final BigDecimal value : values) {
			entries.add(mkNumericLiteral(value));
		}
		return list;
	}

	static public NumberList mkNumberListIntervalValue(final NumericInterval numericInterval) {
		final NumberList list = mkNumberList();
		list.getEntries().add(numericInterval);
		return list;
	}

	static public NumericInterval mkNumericInterval(final String lowerBound, final String upperBound) {
		final NumericInterval interval = VCML.createNumericInterval();
		interval.setLowerBound(lowerBound);
		interval.setUpperBound(upperBound);
		return interval;
	}

	static public NumericLiteral mkNumericLiteral(final BigDecimal value) {
		final NumericLiteral object = VCML.createNumericLiteral();
		object.setValue(value.toPlainString());
		return object;
	}

	static public NumericLiteral mkNumericLiteral(final int value) {
		final NumericLiteral object = VCML.createNumericLiteral();
		object.setValue(Integer.toString(value));
		return object;
	}

	static public ObjectCharacteristicReference mkObjectCharacteristicReference(
			final ConstraintObject location,
			final Characteristic characteristic) {
		final ObjectCharacteristicReference object = VCML.createObjectCharacteristicReference();
		object.setLocation(location);
		object.setCharacteristic(characteristic);
		return object;
	}

	static public PartOfCondition mkPartOfCondition(final ConstraintObject child,
			final ConstraintObject parent) {
		final PartOfCondition object = VCML.createPartOfCondition();
		object.setChild(child);
		object.setParent(parent);
		return object;
	}

	static public PFunction mkPFunction(final VariantFunction variantFunction) {
		final PFunction object = VCML.createPFunction();
		object.setFunction(variantFunction);
		return object;
	}

	static public SetDefault mkSetDefault(final Characteristic cstic, final Expression expression) {
		 final SetDefault object = VCML.createSetDefault();
		 object.setCharacteristic(cstic);
		 object.setExpression(expression);
		 return object;
	}

	static public SumParts mkSumParts(final ProcedureLocation location, final Characteristic cstic) {
		final SumParts sumParts = VCML.createSumParts();
		sumParts.setLocation(location);
		sumParts.setCharacteristic(cstic);
		return sumParts;
	}

	static public SymbolicLiteral mkSymbolicLiteral(final String value) {
		final SymbolicLiteral object = VCML.createSymbolicLiteral();
		object.setValue(value);
		return object;
	}

	static public SymbolList mkSymbolList() {
		return VCML.createSymbolList();
	}

	static public SymbolList mkSymbolList_Literals(final java.util.List<SymbolicLiteral> literals) {
		final SymbolList list = mkSymbolList();
		final java.util.List<SymbolicLiteral> entries = list.getEntries();
		for(final SymbolicLiteral value : literals) {
			entries.add(mkSymbolicLiteral(value.getValue()));
		}
		return list;
	}

	static public UnaryCondition mkUnaryCondition(final Condition condition) {
		final UnaryCondition object = VCML.createUnaryCondition();
		object.setCondition(condition);
		return object;
	}

	static public UnaryExpression mkUnaryExpression(final UnaryExpressionOperator operator, final Expression expression) {
		final UnaryExpression object = VCML.createUnaryExpression();
		object.setOperator(operator);
		object.setExpression(expression);
		return object;
	}

	public static Fixing createFixingFromInt(int int1) {
		// TODO Auto-generated method stub
		return null;
	}
}
