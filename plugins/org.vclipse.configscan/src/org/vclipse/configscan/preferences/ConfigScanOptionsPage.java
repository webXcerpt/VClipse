package org.vclipse.configscan.preferences;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.vclipse.configscan.views.IConfigScanConfiguration;

import com.google.inject.Inject;

public class ConfigScanOptionsPage extends FieldEditorPreferencePage  implements IWorkbenchPreferencePage {

	@Inject
	public ConfigScanOptionsPage(IPreferenceStore preferenceStore) {
		super(GRID);
		setPreferenceStore(preferenceStore);
		setDescription("Options for ConfigScan view.");
	}

	@Override
	public void init(IWorkbench workbench) {
		
	}

	@Override
	protected void createFieldEditors() {
		addField(new BooleanFieldEditor(IConfigScanConfiguration.EXPAND_TREE_ON_INPUT, "Expand tree on input", getFieldEditorParent()));
		addField(new BooleanFieldEditor(IConfigScanConfiguration.EXPORT_XML_INPUT_TO_DISK, "Export xml input document to disk", getFieldEditorParent()));
	}

}
