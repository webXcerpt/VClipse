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
package org.vclipse.idoc.ui;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.xtext.ui.IImageHelper;
import org.vclipse.base.ui.util.ClasspathAwareImageHelper;

/**
 * Use this class to register components to be used within the IDE.
 */
public class IDocUiModule extends org.vclipse.idoc.ui.AbstractIDocUiModule {
	public IDocUiModule(AbstractUIPlugin plugin) {
		super(plugin);
	}

	@Override
	public Class<? extends IImageHelper> bindIImageHelper() {
		return ClasspathAwareImageHelper.class;
	}
	
}
