/**
 * 
 */
package org.vclipse.idoc2jcoidoc.injection;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.xtext.service.AbstractGenericModule;
import org.vclipse.connection.IConnectionHandler;
import org.vclipse.connection.VClipseConnectionPlugin;
import org.vclipse.idoc2jcoidoc.IIDoc2JCoIDocProcessor;
import org.vclipse.idoc2jcoidoc.internal.DefaultIDoc2JCoIDocProcessor;

/**
 * @author as
 *
 */
public class Module extends AbstractGenericModule {

	/**
	 * 
	 */
	private AbstractUIPlugin plugin;
	
	/**
	 * @param activator
	 */
	public Module(AbstractUIPlugin activator) {
		plugin = activator;
	}
	
	/**
	 * @return
	 */
	public IPreferenceStore bindPreferenceStore() {
		return plugin.getPreferenceStore();
	}
	
	/**
	 * @return
	 */
	public IConnectionHandler bindConnectionHandler() {
		return VClipseConnectionPlugin.getDefault().getInjector().getInstance(IConnectionHandler.class);
	}
	
	/**
	 * @return
	 */
	public Class<? extends IIDoc2JCoIDocProcessor> bindIDoc2JCoIDocProcessor() {
		return DefaultIDoc2JCoIDocProcessor.class;
	}
}
