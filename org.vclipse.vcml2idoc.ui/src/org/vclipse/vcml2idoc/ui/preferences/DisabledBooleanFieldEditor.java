/**
 * 
 */
package org.vclipse.vcml2idoc.ui.preferences;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

/**
 *
 */
final class DisabledBooleanFieldEditor extends BooleanFieldEditor {
	
	/**
	 * @param preference
	 * @param label
	 * @param parent
	 */
	public DisabledBooleanFieldEditor(final String preference, final String label, final Composite parent) {
		super(preference, label, parent);
	}

	/**
	 * @see org.eclipse.jface.preference.BooleanFieldEditor#getChangeControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected Button getChangeControl(final Composite parent) {
		final Button button = super.getChangeControl(parent);
		button.setEnabled(false);
		return button;
	}
}
