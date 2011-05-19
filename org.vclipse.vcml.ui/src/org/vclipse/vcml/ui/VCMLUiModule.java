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
/*
 * generated by Xtext
 */
package org.vclipse.vcml.ui;

import java.io.PrintStream;

import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;
import org.eclipse.xtext.ui.IImageHelper;
import org.eclipse.xtext.ui.editor.XtextSourceViewerConfiguration;
import org.eclipse.xtext.ui.editor.outline.IOutlineTreeProvider;
import org.eclipse.xtext.ui.editor.syntaxcoloring.AbstractAntlrTokenToAttributeIdMapper;
import org.vclipse.base.ui.ClasspathAwareImageHelper;
import org.vclipse.vcml.ui.editor.VCMLInformationControlCreator;
import org.vclipse.vcml.ui.editor.VCMLSourceViewerConfiguration;
import org.vclipse.vcml.ui.outline.VCMLOutlinePage;
import org.vclipse.vcml.ui.outline.VCMLOutlineTreeProvider;
import org.vclipse.vcml.ui.syntaxcoloring.VCMLAntlrTokenToAttributeIdMapper;

/**
 * Use this class to register components to be used within the IDE.
 */
public class VCMLUiModule extends org.vclipse.vcml.ui.AbstractVCMLUiModule {
	public VCMLUiModule(AbstractUIPlugin plugin) {
		super(plugin);
	}
	
	@Override
	public Class<? extends IContentOutlinePage> bindIContentOutlinePage() {
		return VCMLOutlinePage.class;
	}

	public Class<? extends XtextSourceViewerConfiguration> bindSourceViewerConfiguration() {
		return VCMLSourceViewerConfiguration.class;
	}

//	public Class<? extends ITextHover> bindTextHover() {
//		return VCMLTextHover.class;
//	}

	public Class<? extends IInformationControlCreator> bindInformationControlCreator() {
		return VCMLInformationControlCreator.class;
	}
	
	public PrintStream bindPrintStream() {
		return System.out;
	}
	
	public Class<? extends AbstractAntlrTokenToAttributeIdMapper> bindAbstractAntlrTokenToAttributeIdMapper() {
		return VCMLAntlrTokenToAttributeIdMapper.class;
	}

	public com.google.inject.Provider<org.eclipse.xtext.resource.containers.IAllContainersState> provideIAllContainersState() {
		return org.eclipse.xtext.ui.shared.Access.getWorkspaceProjectsState();
	}

	@Override
	public Class<? extends IImageHelper> bindIImageHelper() {
		return ClasspathAwareImageHelper.class;
	}

	@Override
	public Class<? extends IOutlineTreeProvider> bindIOutlineTreeProvider() {
		return VCMLOutlineTreeProvider.class;
	}

}
