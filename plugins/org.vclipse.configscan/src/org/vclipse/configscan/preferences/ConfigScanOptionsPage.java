package org.vclipse.configscan.preferences;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.vclipse.configscan.IConfigScanConfiguration;

import com.google.inject.Inject;

public class ConfigScanOptionsPage extends FieldEditorPreferencePage  implements IWorkbenchPreferencePage {

	@Inject
	public ConfigScanOptionsPage(IPreferenceStore preferenceStore) {
		super(GRID);
		setPreferenceStore(preferenceStore);
	}

	@Override
	public void init(IWorkbench workbench) {
		
	}

	@Override
	protected void createFieldEditors() {
		Composite fieldEditorParent = getFieldEditorParent();
		addField(new BooleanFieldEditor(IConfigScanConfiguration.EXPAND_TREE_ON_INPUT, "Expand tree on input", fieldEditorParent));
		addField(new BooleanFieldEditor(IConfigScanConfiguration.EXPORT_XML_INPUT_TO_DISK, "Export xml input document to disk", fieldEditorParent));
		addField(new BooleanFieldEditor(IConfigScanConfiguration.SAVE_HISTORY, "Save history for test runs", fieldEditorParent));
		addField(new IntegerFieldEditor(IConfigScanConfiguration.HISTORY_ENTRIES_NUMBER, "Number of history entries", fieldEditorParent));
	}

}
