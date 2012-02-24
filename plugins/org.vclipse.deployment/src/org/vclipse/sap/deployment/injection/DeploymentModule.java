/**
 * 
 */
package org.vclipse.sap.deployment.injection;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.xtext.generator.IOutputConfigurationProvider;
import org.eclipse.xtext.service.AbstractGenericModule;
import org.vclipse.connection.IConnectionHandler;
import org.vclipse.connection.VClipseConnectionPlugin;
import org.vclipse.idoc2jcoidoc.DefaultIDoc2JCoIDocProcessor;
import org.vclipse.idoc2jcoidoc.IIDoc2JCoIDocProcessor;
import org.vclipse.sap.deployment.OneClickWorkflow;
import org.vclipse.vcml.ui.VCMLUiPlugin;
import org.vclipse.vcml.diff.Comparison;
import org.vclipse.vcml2idoc.VCML2IDocUIPlugin;
import org.vclipse.vcml2idoc.builder.VCML2IDocSwitch;

import com.google.inject.Binder;
import com.google.inject.name.Names;
import com.webxcerpt.cm.nsn.cml.generator.CmlOutputConfigurationProvider;

/**
 *
 */
public class DeploymentModule extends AbstractGenericModule {

	private AbstractUIPlugin plugin;
	
	public DeploymentModule(AbstractUIPlugin activator) {
		plugin = activator;
	}
	
	public IPreferenceStore bindPreferenceStore() {
		return plugin.getPreferenceStore();
	}
	
	@Override
	public void configure(Binder binder) {
		super.configure(binder);
		binder.bind(IPreferenceStore.class).annotatedWith(Names.named(VCMLUiPlugin.ID)).toInstance(VCMLUiPlugin.getDefault().getInjector().getInstance(IPreferenceStore.class));
		binder.bind(IPreferenceStore.class).annotatedWith(Names.named(VCML2IDocUIPlugin.ID)).toInstance(VCML2IDocUIPlugin.getDefault().getInjector().getInstance(IPreferenceStore.class));
	}

	public IConnectionHandler bindConnectionHandler() {
		return VClipseConnectionPlugin.getDefault().getInjector().getInstance(IConnectionHandler.class);
	}
	
	public Class<? extends IIDoc2JCoIDocProcessor> bindIDoc2JCoIDocProcessor() {
		return DefaultIDoc2JCoIDocProcessor.class;
	}

	public VCML2IDocSwitch bindVCML2IDocSwitch() {
		return VCML2IDocUIPlugin.getDefault().getInjector().getInstance(VCML2IDocSwitch.class);
	}
	
	public Class<? extends IOutputConfigurationProvider> bindOutputConfigurationProvider() {
		return CmlOutputConfigurationProvider.class;
	}
	
	public Class<? extends OneClickWorkflow> bindOneClickWorkflow() {
		return OneClickWorkflow.class;
	}
	
	public Class<? extends Comparison> bindComparison() {
		return Comparison.class;
	}
}