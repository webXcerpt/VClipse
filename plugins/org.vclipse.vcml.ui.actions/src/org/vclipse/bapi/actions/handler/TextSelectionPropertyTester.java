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
package org.vclipse.bapi.actions.handler;

import org.eclipse.jface.text.ITextSelection;
import org.eclipse.xtext.resource.EObjectAtOffsetHelper;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.ui.editor.XtextEditor;
import org.eclipse.xtext.ui.editor.utils.EditorUtils;
import org.vclipse.base.ui.util.EditorUtilsExtensions;
import org.vclipse.vcml.vcml.VCObject;

import com.google.inject.Inject;

public class TextSelectionPropertyTester extends BAPIActionPropertyTester {

	@Inject
	protected EObjectAtOffsetHelper offsetHelper;
	
	@Override
	public boolean test(Object receiver, String property, Object[] args, Object expectedValue) {
		if(receiver instanceof ITextSelection) {
			XtextEditor xtextEditor = EditorUtils.getActiveXtextEditor();
			ITextSelection selection = (ITextSelection)receiver;
			if(xtextEditor != null) {
				XtextResource resource = EditorUtilsExtensions.getXtextResource(xtextEditor);
				VCObject vcObject = BAPIActionUtils.getVCObject(offsetHelper, selection, resource);
				return super.test(vcObject, BAPIActionPropertyTester.HANDLER_AVAILABLE, args, expectedValue);
			}
			
		}
		return false;
	}
}
