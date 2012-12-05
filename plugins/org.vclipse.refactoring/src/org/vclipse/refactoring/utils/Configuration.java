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

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.core.internal.registry.osgi.OSGIUtils;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.xtext.ui.guice.AbstractGuiceAwareExecutableExtensionFactory;
import org.osgi.framework.Bundle;
import org.vclipse.refactoring.IRefactoringExecuter;
import org.vclipse.refactoring.RefactoringPlugin;
import org.vclipse.refactoring.RefactoringStatus;

import com.google.common.collect.Maps;
import com.google.inject.ConfigurationException;
import com.google.inject.Injector;
import com.google.inject.Singleton;

@Singleton
public class Configuration {

	private static final String ID = "org.vclipse.refactoring";
	
	private static final String ELEMENT_REFACTORING = "refactoring";
	private static final String ATTRIBUTE_EXECUTER = "executer";
	
	private Map<EClassifier, Injector> injectors;
	
	public Map<EClassifier, Injector> getInjectors() {
		if(injectors == null) {
			readExtensions();
		}
		return Collections.unmodifiableMap(injectors);
	}
	
	public Map<EClassifier, IRefactoringExecuter> getRefactorings() {
		if(injectors == null) {
			readExtensions();
		}
		Map<EClassifier, IRefactoringExecuter> executers = Maps.newHashMap();
		for(Entry<EClassifier, Injector> entry : injectors.entrySet()) {
			EClassifier type = entry.getKey();
			Injector injector = entry.getValue();
			try {
				IRefactoringExecuter executer = injector.getInstance(IRefactoringExecuter.class);
				executers.put(type, executer);				
			} catch(ConfigurationException exception) {
				RefactoringPlugin.log(exception.getMessage(), exception);
			}
		}
		return Collections.unmodifiableMap(executers);
	}

	private void readExtensions() {
		injectors = Maps.newHashMap();
		for(IExtension extension : Platform.getExtensionRegistry().getExtensionPoint(ID).getExtensions()) {
			for(IConfigurationElement element : extension.getConfigurationElements()) {
				if(ELEMENT_REFACTORING.equals(element.getName())) {
					try {
						Object executableExtension = element.createExecutableExtension(ATTRIBUTE_EXECUTER);
						if(executableExtension instanceof IRefactoringExecuter) {
							String executerPath = element.getAttribute(ATTRIBUTE_EXECUTER);
							String[] parts = executerPath.split(":");
							if(parts.length != 2) {
								RefactoringStatus status = RefactoringStatus.getConfigurationError();
								RefactoringPlugin.log(status);
								continue;
							} else {
								String name = element.getContributor().getName();
								Bundle bundle = OSGIUtils.getDefault().getBundle(name);
								Class<?> loadClass = bundle.loadClass(parts[0]);
								Object instance = loadClass.newInstance();
								IRefactoringExecuter executer = (IRefactoringExecuter)executableExtension;
								Set<EClass> topLevelTypes = executer.getTopLevelTypes();
								for(EClass eclass : topLevelTypes) {
									if(instance instanceof AbstractGuiceAwareExecutableExtensionFactory) {
										AbstractGuiceAwareExecutableExtensionFactory extensionFactory = (AbstractGuiceAwareExecutableExtensionFactory)instance;
										Method method = extensionFactory.getClass().getDeclaredMethod("getInjector");
										method.setAccessible(true);
										Injector injector = (Injector)method.invoke(extensionFactory);
										injectors.put(eclass, injector);
									}
								}
							}
						}
					} catch(Exception exception) {
						RefactoringStatus status = RefactoringStatus.getConfigurationError();
						RefactoringPlugin.log(status);
						continue;
					}
				}
			}
		}
	}
}
