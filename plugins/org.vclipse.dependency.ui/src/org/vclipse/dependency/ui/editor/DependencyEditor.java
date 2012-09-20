package org.vclipse.dependency.ui.editor;

import org.eclipse.xtext.ui.editor.XtextEditor;

public class DependencyEditor extends XtextEditor {

	// tabs to space conversion must be enabled in dependency code since SAP does not recognize tabs
	@Override
	protected boolean isTabsToSpacesConversionEnabled() {
		return true;
	}
}
