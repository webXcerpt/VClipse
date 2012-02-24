package org.vclipse.sap.deployment.preferences;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.google.inject.Inject;

public class SapDeploymentPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	@Inject
	public SapDeploymentPreferencePage(IPreferenceStore preferenceStore) {
		super(GRID);
		setPreferenceStore(preferenceStore);
		setTitle("SAP Deployment Settings");
	}
	
	@Override
	protected void createFieldEditors() {
		addField(new BooleanFieldEditor(PreferencesInitializer.SAVE_DIFF_FILES, "Save diff files", getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferencesInitializer.SAVE_IDOC_FILES, "Save idoc files", getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferencesInitializer.EXECUTE_SVN_COMMIT, "Commit the new SAP state", getFieldEditorParent()));
	}

	@Override
	public void init(IWorkbench workbench) {
		// not used
	}
}
