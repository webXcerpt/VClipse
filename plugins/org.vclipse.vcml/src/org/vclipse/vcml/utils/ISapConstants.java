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
package org.vclipse.vcml.utils;

import org.vclipse.vcml.VCMLPlugin;

public interface ISapConstants {

	/**
	 * 
	 */
	public static final String DEFAULT_LANGUAGE = "defaultLanguage";
	
	/**
	 * Preference name for storing the default plant
	 */
	// BOM
	public static final String PLANT = "SAPSetting_PLANT";
	public static final String BOM_USAGE = "SAPSetting_BOM_USAGE";
	public static final String INDUSTRY_SECTOR = "SAPSetting_INDUSTRY_SECTOR";
	// Material
	public static final String TRANSPORTATION_GROUP = "SAPSetting_TRANSPORATION_GROUP";
	public static final String LOADING_GROUP = "SAPSetting_LOADING_GROUP";
	public static final String SALES_ORGANISATION = "SAPSetting_SALES_ORGANISATION";
	public static final String DISTRIBUTION_CHANNEL = "SAPSetting_DISTRIBUTION_CHANNEL";

	/**
	 * Preference name for serializer-option
	 */
	public static final String USE_PRETTY_PRINTER = VCMLPlugin.ID + ".usePrettyPrinter";;

	/**
	 * Preference name for line length option of the pretty printer
	 */
	public static final String PP_LINE_LENGTH = VCMLPlugin.ID + ".ppLineLength";

}
