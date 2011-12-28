package org.vclipse.configscan.views;

import org.vclipse.configscan.Activator;

public class Config {

	/** The View-ID.
	 * 
	 */
	public static final String CONFIGSCAN_VIEW_ID = Activator.PLUGIN_ID + ".views.XmlView";
	
	/** If true: Writes the xml-data which is created in RAM to disc.
	 * 
	 */
	public static final boolean WRITE_MEMORY_TO_DISC_AS_XML = true; 
	
	/** The date-format to be used (e.g. when saving to disc)
	 * 
	 */
	public static final String DATE_FORMAT = "yyyyMMdd'T'HH-mm-ss";

	/** The default-depth the viewer will expand.
	 * 
	 */
	public static final int EXPAND_LEVEL = 2;
}
