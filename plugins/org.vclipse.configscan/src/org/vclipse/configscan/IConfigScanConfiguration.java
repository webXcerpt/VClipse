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
package org.vclipse.configscan;


public interface IConfigScanConfiguration {

	// preferences names
	public static final String EXPAND_TREE_ON_INPUT = ConfigScanPlugin.ID + ".expand_tree_on_input";
	
	public static final String EXPORT_XML_INPUT_TO_DISK = ConfigScanPlugin.ID + ".export_xml_input_to_disk";
	
	public static final String SAVE_HISTORY = ConfigScanPlugin.ID + ".saveHistory";
	
	public static final String HISTORY_ENTRIES_NUMBER = ConfigScanPlugin.ID + ".historyEntriesNumber";
	
	// preferences values
	public static final int DEFAULT_EXPAND_LEVEL = 2;
	
	public static final boolean WRITE_MEMORY_TO_DISC_AS_XML = false; 
	
	// other values used in code
	public static final String DATE_FORMAT = "yyyyMMdd'T'HH-mm-ss";
	
	public static final String HISTORY_FILE_NAME = "history.xml";

}
