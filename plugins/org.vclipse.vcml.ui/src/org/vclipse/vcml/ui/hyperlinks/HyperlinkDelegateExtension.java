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
package org.vclipse.vcml.ui.hyperlinks;

import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.xtext.ui.editor.hyperlinking.IHyperlinkHelper;

import com.google.common.collect.Lists;

public class HyperlinkDelegateExtension {

	private List<IHyperlinkHelper> elements;
	
	public List<IHyperlinkHelper> getExtensions() {
		if(elements == null) {
			elements = Lists.newArrayList();			
		} else if(elements.isEmpty()) {
			IExtensionRegistry extensionRegistry = Platform.getExtensionRegistry();
			IExtensionPoint extensionPoint = extensionRegistry.getExtensionPoint("org.vclipse.vcml.ui.hyperlinksDelegate");
			for(IExtension extension : extensionPoint.getExtensions()) {
				for(IConfigurationElement element : extension.getConfigurationElements()) {
					try {
						Object object = element.createExecutableExtension("class");
						if(object instanceof IHyperlinkHelper) {
							elements.add((IHyperlinkHelper)object);
						}
					} catch (CoreException e) {
						continue;
					}
				}
			}
		}
		return Collections.unmodifiableList(elements);
	}
}
