/*******************************************************************************
 * Copyright (c) 2010 webXcerpt Software GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    webXcerpt Software GmbH - initial creator
 *******************************************************************************/
package org.vclipse.vcml.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.Platform;
import org.vclipse.vcml.VCMLPlugin;
import org.vclipse.vcml.vcml.Language;
import org.vclipse.vcml.vcml.Status;


public class VCMLUtils {

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
		String literal = Platform.getPreferencesService().getString(VCMLPlugin.ID, ISapConstants.DEFAULT_LANGUAGE, "", null);
		return Language.get(literal);
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
	
	static private Pattern CLASSSPEC_PATTERN = Pattern.compile("\\((\\d+)\\)(.+)"); // (INT)ID, capturing group 1 is INT, capturing group 2 is ID
	
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
	
	

}
