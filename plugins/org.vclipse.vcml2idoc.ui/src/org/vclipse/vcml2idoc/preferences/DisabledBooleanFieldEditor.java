/*******************************************************************************
 * Copyright (c) 2010 - 2013 webXcerpt Software GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *  
 * Contributors:
 *     	webXcerpt Software GmbH - initial creator
 * 		www.webxcerpt.com
 ******************************************************************************/
package org.vclipse.vcml2idoc.preferences;

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
