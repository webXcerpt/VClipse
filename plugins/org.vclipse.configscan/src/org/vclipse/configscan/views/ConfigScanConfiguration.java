/*******************************************************************************
 * Copyright (c) 2012 webXcerpt Software GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    webXcerpt Software GmbH - initial creator
 ******************************************************************************/
package org.vclipse.configscan.views;

import org.vclipse.configscan.ConfigScanPlugin;


public class ConfigScanConfiguration {

	public static final String EXPAND_TREE_ON_INPUT = ConfigScanPlugin.ID + ".expand_tree_on_input";
	
	public static final int DEFAULT_EXPAND_LEVEL = 2;
	
	public static final String EXPORT_XML_INPUT_TO_DISK = ConfigScanPlugin.ID + "export_xml_input_to_disk";
	
	/** If true: Writes the xml-data which is created in RAM to disc.
	 * 
	 */
	public static final boolean WRITE_MEMORY_TO_DISC_AS_XML = false; 
	
	/** The date-format to be used (e.g. when saving to disc)
	 * 
	 */
	public static final String DATE_FORMAT = "yyyyMMdd'T'HH-mm-ss";

	
}
