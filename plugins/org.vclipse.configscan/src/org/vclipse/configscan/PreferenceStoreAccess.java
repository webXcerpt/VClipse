package org.vclipse.configscan;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.editors.text.EditorsUI;
import org.eclipse.ui.texteditor.ChainedPreferenceStore;
import org.eclipse.xtext.ui.editor.preferences.PreferenceStoreAccessImpl;
import org.vclipse.vcml.ui.VCMLUiPlugin;

import com.google.inject.Singleton;

@Singleton
public class PreferenceStoreAccess extends PreferenceStoreAccessImpl{

	public IPreferenceStore getPreferenceStore() {
		lazyInitialize();
		return new ChainedPreferenceStore(new IPreferenceStore[] {
				getWritablePreferenceStore(),
				Activator.getDefault().getPreferenceStore(), 
				VCMLUiPlugin.getDefault().getInjector().getInstance(IPreferenceStore.class), 
				EditorsUI.getPreferenceStore() });
	}

	public IPreferenceStore getContextPreferenceStore(Object context) {
		lazyInitialize();
		return new ChainedPreferenceStore(new IPreferenceStore[] { 
				getWritablePreferenceStore(context),
				Activator.getDefault().getPreferenceStore(),
				VCMLUiPlugin.getDefault().getInjector().getInstance(IPreferenceStore.class), 
				EditorsUI.getPreferenceStore()});
	}

}
