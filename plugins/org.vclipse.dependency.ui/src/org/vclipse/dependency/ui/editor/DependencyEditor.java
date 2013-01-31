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
package org.vclipse.dependency.ui.editor;

import org.eclipse.xtext.ui.editor.XtextEditor;

public class DependencyEditor extends XtextEditor {

	// tabs to space conversion must be enabled in dependency code since SAP does not recognize tabs
	@Override
	protected boolean isTabsToSpacesConversionEnabled() {
		return true;
	}
}
