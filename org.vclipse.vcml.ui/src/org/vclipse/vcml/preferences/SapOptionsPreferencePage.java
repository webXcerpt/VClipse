/**
 * 
 */
package org.vclipse.vcml.preferences;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.vclipse.vcml.IUiConstants;
import org.vclipse.vcml.CombinedPreferenceStore;
import org.vclipse.vcml.VCMLPlugin;
import org.vclipse.vcml.VCMLUiPlugin;
import org.vclipse.vcml.utils.ISapConstants;
import org.vclipse.vcml.vcml.Language;

/**
 *
 */
public class SapOptionsPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	/**
	 * index 0 & 1 are for "Output to file" & "Overwrite" options
	 * index 2 is for the "Use pretty printer" option
	 */
	private final Button[] booleanButtons = new Button[3];

	/**
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(final IWorkbench workbench) {
		setPreferenceStore(VCMLUiPlugin.getDefault().getPreferenceStore());
	}

	/**
	 * @see org.eclipse.jface.preference.FieldEditorPreferencePage#createFieldEditors()
	 */
	@Override
	protected void createFieldEditors() {
		final Composite parent = getFieldEditorParent();
		
		addField(new ComboFieldEditor(VCMLPlugin.ID, ISapConstants.DEFAULT_LANGUAGE, "Default language: ", parent, Language.VALUES));
		
		Group group = new Group(parent, SWT.NONE);
		group.setText(" Values for handling output ");
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 2;	
		group.setLayoutData(gridData);
		group.setLayout(new GridLayout(2, false));

		addField(createBooleanFieldEditor(IUiConstants.OUTPUT_TO_FILE, "Output to a file", group));
		addField(createBooleanFieldEditor(IUiConstants.OVERWRITE, "Overwrite existing file", group));
		booleanButtons[0].addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				booleanButtons[1].setEnabled(((Button)e.widget).getSelection());
			}
		});
		booleanButtons[1].setEnabled(getPreferenceStore().getBoolean(IUiConstants.OUTPUT_TO_FILE));
		
		group = new Group(parent, SWT.NONE);
		group.setText(" Pretty printer options ");
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 2;	
		group.setLayoutData(gridData);
		group.setLayout(new GridLayout(2, false));
		
		addField(new BooleanFieldEditor(ISapConstants.USE_PRETTY_PRINTER, "Use pretty printer", group) {
			@Override
			protected Button getChangeControl(Composite parent) {
				if(booleanButtons[2] == null) {
					booleanButtons[2] = super.getChangeControl(parent);
					return booleanButtons[2];
				} else {
					return super.getChangeControl(parent);
				}
			}
			@Override
			protected void createControl(Composite parent) {
				GridLayout layout = new GridLayout();
				layout.numColumns = getNumberOfControls();
				layout.marginWidth = 10;
				layout.marginHeight = 5;
				layout.horizontalSpacing = HORIZONTAL_GAP;
				parent.setLayout(layout);
				doFillIntoGrid(parent, 2);
			}
		});
		
		addField(new IntegerFieldEditor(ISapConstants.PP_LINE_LENGTH, "Line length:", group, 3) {
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
		});
		
	}

	/**
	 * @param prefName
	 * @param label
	 * @param parent
	 * @return
	 */
	private BooleanFieldEditor createBooleanFieldEditor(String prefName, String label, Composite parent) {
		return new BooleanFieldEditor(prefName, label, parent) {
			@Override
			protected Button getChangeControl(Composite parent) {
				if(booleanButtons[0] == null) {
					booleanButtons[0] = super.getChangeControl(parent);
					return booleanButtons[0];
				} else if(booleanButtons[1] == null){
					booleanButtons[1] = super.getChangeControl(parent);
					return booleanButtons[1];
				} else {
					return super.getChangeControl(parent);
				}
			}

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
