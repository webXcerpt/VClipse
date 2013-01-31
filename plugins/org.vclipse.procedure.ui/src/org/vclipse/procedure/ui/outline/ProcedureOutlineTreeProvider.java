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
package org.vclipse.procedure.ui.outline;

import org.eclipse.jface.preference.IPreferenceStore;
import org.vclipse.vcml.ui.outline.VCMLOutlineTreeProvider;

import com.google.inject.Inject;

/**
 * customization of the default outline structure
 * 
 */
public class ProcedureOutlineTreeProvider extends VCMLOutlineTreeProvider {

	@Inject
	public ProcedureOutlineTreeProvider(IPreferenceStore preferenceStore) {
		super(preferenceStore);
	}
}
