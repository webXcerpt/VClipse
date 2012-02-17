package org.vclipse.configscan.actions;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConfigScanUploadProcessingInstructionExtractor {

	final public static String CONFIGSCAN_UPLOAD = "configscan-upload";
	final public static Pattern MATNR = Pattern.compile("^materialid\\s+(.+)$");
	final public static Pattern DOCNUMBER = Pattern.compile("^documentname\\s+(.+)$");
	final public static Pattern DOCDESCR = Pattern
			.compile("^documentdescription\\s+(.+)$");
	final public static Pattern DOCVERSION = Pattern
			.compile("^documentversion\\s+(.+)$");
	final public static Pattern DOCPART = Pattern.compile("^documentpart\\s+(.+)$");

	public static String extract(Pattern p, String s, String nomatch) {
		Matcher m = p.matcher(s);
		if (m.matches()) {
			return m.group(1).trim();
		} else {
			return nomatch;
		}
	}
}