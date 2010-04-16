/*******************************************************************************
 * Copyright (c) 2010 webXcerpt Software GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    webXcerpt Software GmbH - initial creator
 *******************************************************************************/
package org.vclipse.vcml.editor;

import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.xtext.ui.core.editor.XtextSourceViewerConfiguration;

import com.google.inject.Inject;

public class VCMLSourceViewerConfiguration extends XtextSourceViewerConfiguration {

	@Inject
	ITextHover textHover;
	
	@Override
	public ITextHover getTextHover(ISourceViewer sourceViewer, String contentType) {
		return textHover;
	}
	
	

}
