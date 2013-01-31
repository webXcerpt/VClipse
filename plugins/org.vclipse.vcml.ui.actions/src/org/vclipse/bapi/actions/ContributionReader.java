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
package org.vclipse.bapi.actions;

import java.lang.reflect.Method;
import java.util.Collection;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;

public class ContributionReader {

	public static final String VCLIPSE_SAP_COMMANDS_PREFIX = "org.vclipse.sap.commands";
	
	public static final String HANDLER_EP_ID = "org.eclipse.ui.handlers";
	
	private static final String COMMAND_ATTRIBUTE = "commandId";
	private static final String CLASS_ATTRIBUTE = "class";
	private static final String ATTRIBUTE_HANDLER = "handler";
	
	private final Multimap<String, IBAPIActionRunner<?>> type2Action;
	
	public ContributionReader() {
		type2Action = HashMultimap.create();
	}
	
	protected void read() {
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IExtensionPoint point = registry.getExtensionPoint(HANDLER_EP_ID);
		for(IExtension extension : point.getExtensions()) {
			for(IConfigurationElement element : extension.getConfigurationElements()) {
				String elementName = element.getName();
				if(ATTRIBUTE_HANDLER.equals(elementName)) {
					String attribute = element.getAttribute(COMMAND_ATTRIBUTE);
					if(attribute.startsWith(VCLIPSE_SAP_COMMANDS_PREFIX)) {
						try {
							Object object = element.createExecutableExtension(CLASS_ATTRIBUTE);
							if(object instanceof IBAPIActionRunner<?>) {
								IBAPIActionRunner<?> handler = (IBAPIActionRunner<?>)object;
								for(Method method : handler.getClass().getMethods()) {
									if(method.getName().equals("run")) {
										Class<?>[] parameterTypes = method.getParameterTypes();
										if(parameterTypes.length > 0) {
											type2Action.put(parameterTypes[0].getCanonicalName(), handler);											
										}
									}
									break;
								}
								type2Action.put(handler.getClass().getName(), handler);
							}
						} catch (CoreException exception) {
							BAPIActionPlugin.log(exception.getMessage(), exception);
						}
					}
				}
			}
		}
	}
	
	public Collection<IBAPIActionRunner<?>> getHandler(String type) {
		if(type2Action.isEmpty()) {
			read();
		}
		return type2Action.get(type);
	}
	
	public Multimap<String, IBAPIActionRunner<?>> getExtensions() {
		if(type2Action.isEmpty()) {
			read();
		}
		return Multimaps.unmodifiableMultimap(type2Action);
	}
}
