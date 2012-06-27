package org.vclipse.vcml.diff.injection;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.xtext.ui.editor.validation.MarkerCreator;
import org.vclipse.vcml.VCMLRuntimeModule;
import org.vclipse.vcml.diff.VcmlDiffPlugin;

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
}
