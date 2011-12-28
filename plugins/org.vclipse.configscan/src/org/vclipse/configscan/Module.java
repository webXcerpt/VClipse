/**
 * 
 */
package org.vclipse.configscan;

import org.eclipse.xtext.service.DefaultRuntimeModule;
import org.eclipse.xtext.ui.editor.preferences.IPreferenceStoreAccess;
import org.vclipse.connection.IConnectionHandler;
import org.vclipse.connection.VClipseConnectionPlugin;

/**
 *
 */
public class Module extends DefaultRuntimeModule {

	public IConnectionHandler bindConnectionHandler() {
		return VClipseConnectionPlugin.getDefault().getInjector().getInstance(IConnectionHandler.class);
	}
	
	public Class<? extends IPreferenceStoreAccess> bindPreferenceStoreAccess() {
		return PreferenceStoreAccess.class;
	}
	
//	// TODO check whether this can be removed
//	public Class<? extends AdapterFactoryLabelProvider> bindAdapterFactoryLabelProvider() {
//		return InjectableAdapterFactoryLabelProvider.class;
//	}
//	
//	public Class<? extends AdapterFactory> bindAdapterFactory() {
//		return CmltAdapterFactory.class;
//	}
	
}
