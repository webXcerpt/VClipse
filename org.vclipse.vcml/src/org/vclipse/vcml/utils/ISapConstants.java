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

import org.vclipse.vcml.VCMLPlugin;


/**
 * 
 */
public interface ISapConstants {

	/**
	 * 
	 */
	public static final String DEFAULT_LANGUAGE = "defaultLanguage";
	
	/**
	 * Preference name for storing the default plant
	 */
	// BOM
	public static final String PLANT = VCMLPlugin.ID + ".plant";
	public static final String BOM_USAGE = VCMLPlugin.ID + ".bomUsage";
	public static final String INDUSTRY_SECTOR = VCMLPlugin.ID + ".industrySector";
	// Material
	public static final String TRANSPORTATION_GROUP = VCMLPlugin.ID + ".itransportationGroup";
	public static final String LOADING_GROUP = VCMLPlugin.ID + ".loadingGroup";
	public static final String SALES_ORGANISATION = VCMLPlugin.ID + ".salesOrganisation";
	public static final String DISTRIBUTION_CHANNEL = VCMLPlugin.ID + ".distributionChannel";

	/**
	 * Preference name for serializer-option
	 */
	public static final String USE_PRETTY_PRINTER = VCMLPlugin.ID + ".usePrettyPrinter";;

	/**
	 * Preference name for line length option of the pretty printer
	 */
	public static final String PP_LINE_LENGTH = VCMLPlugin.ID + ".ppLineLength";

}
