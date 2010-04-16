/**
 * 
 */
package org.vclipse.vcml.preferences;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.vclipse.vcml.CombinedPreferenceStore;
import org.vclipse.vcml.VCMLUiPlugin;
import org.vclipse.vcml.utils.ISapConstants;

/**
 *
 */
public class SapDefaultSettingsPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	/**
	 * 
	 */
	public SapDefaultSettingsPreferencePage()  {
		super(GRID);
	}
	
	/**
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench) {
		setPreferenceStore(VCMLUiPlugin.getDefault().getPreferenceStore());
	}
	
	/**
	 * @see org.eclipse.xtext.ui.core.editor.preferences.LanguageRootPreferencePage#createFieldEditors()
	 */
	@Override
	protected void createFieldEditors() {
		final Composite parent = getFieldEditorParent();
		Group sapValuesGroup = new Group(parent, SWT.NONE);
		sapValuesGroup.setText(" Default values for SAP materials and BOMs ");
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 2;	
		sapValuesGroup.setLayoutData(gridData);
		sapValuesGroup.setLayout(new GridLayout(2, false));

		addField(createStringFieldEditor(ISapConstants.PLANT, "Plant:", sapValuesGroup));
		addField(createStringFieldEditor(ISapConstants.BOM_USAGE, "BOM usage:", sapValuesGroup));
		addField(createStringFieldEditor(ISapConstants.INDUSTRY_SECTOR, "Industry sector:", sapValuesGroup));
		addField(createStringFieldEditor(ISapConstants.TRANSPORTATION_GROUP, "Transportation group:", sapValuesGroup));
		addField(createStringFieldEditor(ISapConstants.LOADING_GROUP, "Loading group:", sapValuesGroup));
		addField(createStringFieldEditor(ISapConstants.SALES_ORGANISATION, "Sales organization:", sapValuesGroup));
		addField(createStringFieldEditor(ISapConstants.DISTRIBUTION_CHANNEL, "Distribution channel:", sapValuesGroup));
	}

	/**
	 * @param prefName
	 * @param label
	 * @param parent
	 * @return
	 */
	private StringFieldEditor createStringFieldEditor(String prefName, String label, Composite parent) {
		return new StringFieldEditor(prefName, label, 10, StringFieldEditor.VALIDATE_ON_KEY_STROKE, parent) {
			@Override
			protected void createControl(Composite parent) {
				GridLayout layout = new GridLayout();
				layout.numColumns = getNumberOfControls();
				layout.marginWidth = 10;
				layout.marginHeight = 5;
				layout.horizontalSpacing = HORIZONTAL_GAP;
				parent.setLayout(layout);
				doFillIntoGrid(parent, layout.numColumns);
			}
		};
	}

	/**
	 * @see org.eclipse.jface.preference.FieldEditorPreferencePage#performOk()
	 */
	@Override
	public boolean performOk() {
		super.performOk();
		((CombinedPreferenceStore)getPreferenceStore()).storePreferences();
		return true;
	}

	/**
	 * @see org.eclipse.jface.preference.PreferencePage#performApply()
	 */
	@Override
	protected void performApply() {
		super.performApply();
		((CombinedPreferenceStore)getPreferenceStore()).storePreferences();
	}
}
