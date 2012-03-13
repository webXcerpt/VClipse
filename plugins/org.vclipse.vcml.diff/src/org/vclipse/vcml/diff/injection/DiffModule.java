package org.vclipse.vcml.diff.injection;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.xtext.ui.editor.validation.MarkerCreator;
import org.vclipse.vcml.VCMLRuntimeModule;
import org.vclipse.vcml.diff.VcmlDiffPlugin;
import org.vclipse.vcml.diff.compare.DiffValidationMessageAcceptor;

import com.google.inject.Provider;

public class DiffModule extends VCMLRuntimeModule {

	private VcmlDiffPlugin plugin;
	
	public DiffModule(VcmlDiffPlugin plugin) {
		this.plugin = plugin;
	}

	public AbstractUIPlugin bindPlugin() {
		return plugin;
	}
	
	public IPreferenceStore bindPreferenceStore() {
		return plugin.getPreferenceStore();
	}

	public Class<? extends MarkerCreator> bindMarkerCreator() {
		return MarkerCreator.class;
	}
	
	public Class<? extends DiffValidationMessageAcceptor> bindIssueCreator() {
		return DiffValidationMessageAcceptor.class;
	}
	
	public Provider<DiffValidationMessageAcceptor> registerDiffValiadationMessageAcceptor() {
		return new Provider<DiffValidationMessageAcceptor>() {
			@Override
			public DiffValidationMessageAcceptor get() {
				return plugin.getInjector().getInstance(DiffValidationMessageAcceptor.class);
			}
		};
	}
}
