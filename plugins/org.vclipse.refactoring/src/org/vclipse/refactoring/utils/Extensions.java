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
package org.vclipse.refactoring.utils;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.EcoreUtil2;
import org.vclipse.refactoring.RefactoringPlugin;

import com.google.inject.ConfigurationException;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;

@Singleton
public class Extensions {

	private Configuration configuration;
	
	@Inject
	public Extensions(Configuration configuration) {
		this.configuration = configuration;
	}
	
	public Injector getInjector(EObject object) {
		if(object == null) {
			return null;
		}
		EObject container = EcoreUtil2.getRootContainer(object);
		if(container == null) {
			return null;
		}
		EClass type = container.eClass();
		return configuration == null ? null : configuration.getInjectors().get(type);
	}
	
	public <T> T getInstance(Class<T> type, EObject object) {
		Injector injector = getInjector(object);
		if(injector == null) {
			return getInstance(type);
		}
		return injector.getInstance(type);
	}
	
	public <T> T getInstance(Class<T> type) {
		try {
			RefactoringPlugin plugin = RefactoringPlugin.getInstance();
			Injector refactoringInjector = plugin.getInjector();
			T instance = refactoringInjector.getInstance(type);
			return instance;
		} catch(ConfigurationException exception) {
			RefactoringPlugin.log(IStatus.ERROR, exception.getMessage());
			return null;
		}
	}
}
