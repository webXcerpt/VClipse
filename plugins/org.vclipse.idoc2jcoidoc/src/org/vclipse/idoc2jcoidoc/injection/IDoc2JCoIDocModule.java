/**
 * 
 */
package org.vclipse.idoc2jcoidoc.injection;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.xtext.service.AbstractGenericModule;
import org.vclipse.connection.IConnectionHandler;
import org.vclipse.connection.VClipseConnectionPlugin;
import org.vclipse.idoc2jcoidoc.DefaultIDoc2JCoIDocProcessor;
import org.vclipse.idoc2jcoidoc.IDoc2JCoIDocPlugin;
import org.vclipse.idoc2jcoidoc.IIDoc2JCoIDocProcessor;
import org.vclipse.vcml.ui.VCMLUiPlugin;
import org.vclipse.vcml2idoc.VCML2IDocPlugin;
import org.vclipse.vcml2idoc.transformation.VCML2IDocSwitch;

import com.google.inject.Binder;
import com.google.inject.Injector;
import com.google.inject.name.Names;

public class IDoc2JCoIDocModule extends AbstractGenericModule {

	private AbstractUIPlugin plugin;
	
	public IDoc2JCoIDocModule(AbstractUIPlugin activator) {
		plugin = activator;
	}
	
	public IPreferenceStore bindPreferenceStore() {
		return plugin.getPreferenceStore();
	}
	
	@Override
	public void configure(Binder binder) {
		super.configure(binder);
		
		Injector vcmlInjector = VCMLUiPlugin.getInjector();
		Injector vcml2IDocInjector = VCML2IDocPlugin.getDefault().getInjector();
		Injector connectionInjector = VClipseConnectionPlugin.getDefault().getInjector();
		
		binder.bind(IPreferenceStore.class).annotatedWith(Names.named(VCMLUiPlugin.ID)).toInstance(vcmlInjector.getInstance(IPreferenceStore.class));
		binder.bind(IPreferenceStore.class).annotatedWith(Names.named(VCML2IDocPlugin.ID)).toInstance(vcml2IDocInjector.getInstance(IPreferenceStore.class));
		binder.bind(IPreferenceStore.class).annotatedWith(Names.named(IDoc2JCoIDocPlugin.ID)).toInstance(plugin.getPreferenceStore());
		
		binder.bind(VCML2IDocSwitch.class).toInstance(vcml2IDocInjector.getInstance(VCML2IDocSwitch.class));
		binder.bind(IConnectionHandler.class).toInstance(connectionInjector.getInstance(IConnectionHandler.class));
	}

	public Class<? extends IIDoc2JCoIDocProcessor> bindIDoc2JCoIDocProcessor() {
		return DefaultIDoc2JCoIDocProcessor.class;
	}
}
