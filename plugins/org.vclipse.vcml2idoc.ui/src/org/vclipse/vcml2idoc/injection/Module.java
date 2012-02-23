/**
 * 
 */
package org.vclipse.vcml2idoc.injection;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.xtext.service.AbstractGenericModule;
import org.vclipse.vcml.ui.VCMLUiPlugin;
import org.vclipse.vcml2idoc.IVcml2IDocTransformation;
import org.vclipse.vcml2idoc.VCML2IDocUIPlugin;
import org.vclipse.vcml2idoc.builder.VCML2IDocSwitch;
import org.vclipse.vcml2idoc.builder.VCML2IDocTransformation;

import com.google.inject.Binder;
import com.google.inject.name.Names;

/**
 *	Injection module implementation for Vcml2IDoc Plug-in
 */
public class Module extends AbstractGenericModule {

	private AbstractUIPlugin plugin;
	
	public Module(AbstractUIPlugin plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public void configure(Binder binder) {
		super.configure(binder);
		binder.bind(IPreferenceStore.class).annotatedWith(Names.named(VCMLUiPlugin.ID)).toInstance(VCMLUiPlugin.getDefault().getInjector().getInstance(IPreferenceStore.class));
        binder.bind(IPreferenceStore.class).annotatedWith(Names.named(VCML2IDocUIPlugin.ID)).toInstance(plugin.getPreferenceStore());
	}

	public IPreferenceStore bindPreferenceStore() {
		return plugin.getPreferenceStore();
	}
	
	public Class<? extends IVcml2IDocTransformation> bindVcml2IDocTransformation() {
		return VCML2IDocTransformation.class;
	}
	
	public Class<? extends VCML2IDocSwitch> bindVcml2IDocSwitch() {
		return VCML2IDocSwitch.class;
	}
}
