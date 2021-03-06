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

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.ecore.resource.Resource;
import org.vclipse.bapi.actions.ContributionReader;
import org.vclipse.bapi.actions.IBAPIActionRunner;
import org.vclipse.connection.IConnectionHandler;

import com.google.inject.Inject;

public class BAPIActionPropertyTester extends PropertyTester {

	public static final String CONNECTED = "connected";
	public static final String HANDLER_AVAILABLE = "handlerType";
	
	public static final String SEPARATOR = ":";
	public static final String EXISTS_STRING = "exists";
	
	@Inject
	protected IConnectionHandler connectionHandler;
	
	@Inject
	protected ContributionReader contributionReader;
	
	@Override
	public boolean test(Object receiver, String property, Object[] args, Object expectedValue) {
		if(CONNECTED.equals(property)) {
			return connectionHandler.getCurrentConnection() != null;
		} else if(HANDLER_AVAILABLE.equals(property)) {
			if(expectedValue instanceof String) {
				return handlerExists(receiver, (String)expectedValue);
			}
		}
		return false;
	}
	
	@SuppressWarnings("rawtypes")
	protected boolean handlerExists(Object receiver, String expectedValue) {
		String[] parts = ((String)expectedValue).split(SEPARATOR);
		if(parts.length == 2) {
			for(IBAPIActionRunner<?> handler : contributionReader.getHandler(parts[1])) {
				try {
					Class<? extends IBAPIActionRunner> handlerClass = handler.getClass();
					Class<?> instanceType = BAPIActionUtils.getInstanceType(receiver);
					handlerClass.getMethod("run", new Class[]{instanceType, Resource.class, IProgressMonitor.class, Map.class, List.class});
					Method enbledMethod = handlerClass.getMethod("isEnabled", new Class[]{instanceType});
					Object invoke = enbledMethod.invoke(handler, receiver);
					return EXISTS_STRING.equals(parts[0]) && (Boolean)invoke;
				} catch (Exception exception) {
					// ignore
				} 
			}
		}
		return false;
	}
}
