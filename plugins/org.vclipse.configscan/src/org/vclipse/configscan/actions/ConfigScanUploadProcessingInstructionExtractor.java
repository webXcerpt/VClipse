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