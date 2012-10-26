/*******************************************************************************
 * Copyright (c) 2012 webXcerpt Software GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *  
 * Contributors:
 *     webXcerpt Software GmbH - initial creator
 ******************************************************************************/
package org.vclipse.refactoring.utils;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.EcoreUtil2;
import org.vclipse.refactoring.RefactoringPlugin;

import com.google.inject.ConfigurationException;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;

@Singleton
public class Extensions {

	@Inject
	private Configuration configuration;
	
	public Injector getInjector(EObject object) {
		if(object == null) {
			return null;
		}
		EObject container = EcoreUtil2.getRootContainer(object);
		if(container == null) {
			return null;
		}
		Injector injector = configuration.getInjectors().get(container.eClass());
		return injector;
	}
	
	public <T> T getInstance(Class<T> type, EObject object) {
		try {
			Injector injector = getInjector(object);
			if(injector == null) {
				injector = RefactoringPlugin.getInstance().getInjector();
			}
			return injector.getInstance(type);
		} catch(ConfigurationException exception) {
			try {
				T instance = RefactoringPlugin.getInstance().getInjector().getInstance(type);
				return instance;
			} catch(ConfigurationException nextConfiguration) {
				RefactoringPlugin.log(exception.getMessage(), exception);
				return null;
			}
		}
	}
}
