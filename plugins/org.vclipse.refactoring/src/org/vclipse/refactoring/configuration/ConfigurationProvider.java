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
package org.vclipse.refactoring.configuration;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.eclipse.core.internal.registry.osgi.OSGIUtils;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EPackage.Registry;
import org.eclipse.xtext.ui.guice.AbstractGuiceAwareExecutableExtensionFactory;
import org.eclipse.xtext.util.Strings;
import org.osgi.framework.Bundle;
import org.vclipse.refactoring.RefactoringPlugin;
import org.vclipse.refactoring.RefactoringStatus;
import org.vclipse.refactoring.core.RefactoringCustomisation;
import org.vclipse.refactoring.core.RefactoringExecuter;
import org.vclipse.refactoring.ui.RefactoringUICustomisation;

import com.google.common.collect.Maps;
import com.google.inject.Injector;
import com.google.inject.Singleton;

@Singleton
public class ConfigurationProvider {

	private static final Registry PACKAGE_REGISTRY = Registry.INSTANCE;

	private static final String ID = "org.vclipse.refactoring";
	
	private static final String ELEMENT_REFACTORING = "refactoring";
	
	private static final String ATTRIBUTE_CLASS = "class";
	private static final String ATTRIBUTE_PACKAGE = "package";
	private static final String ATTRIBUTE_EXECUTER = "executer";
	private static final String ATTRIBUTE_CUSTOMISATION = "customisation";
	private static final String ATTRIBUTE_UICUSTOMISATION = "uicustomisation";
	
	private Map<EClassifier, Injector> injectors;
	private Map<EClassifier, RefactoringCustomisation> customisation;
	private Map<EClassifier, RefactoringUICustomisation> uicustomisation;
	private Map<EClassifier, RefactoringExecuter> refactorings;
		
	public Map<EClassifier, RefactoringCustomisation> getCustomisation() {
		if(customisation == null) {
			readExtensions();
		}
		return Collections.unmodifiableMap(customisation);
	}
	
	public Map<EClassifier, RefactoringUICustomisation> getUICustomisation() {
		if(uicustomisation == null) {
			readExtensions();
		}
		return Collections.unmodifiableMap(uicustomisation);
	}
	
	public Map<EClassifier, RefactoringExecuter> getRefactorings() {
		if(refactorings == null) {
			readExtensions();
		}
		return Collections.unmodifiableMap(refactorings);
	}
	
	public Map<EClassifier, Injector> getInjector() {
		if(injectors == null) {
			readExtensions();
		}
		return Collections.unmodifiableMap(injectors);
	}

	private void readExtensions() {
		injectors = Maps.newHashMap();
		customisation = Maps.newHashMap();
		uicustomisation = Maps.newHashMap();
		refactorings = Maps.newHashMap();
		
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IExtensionPoint point = registry.getExtensionPoint(ID);
		for(IExtension extension : point.getExtensions()) {
			for(IConfigurationElement element : extension.getConfigurationElements()) {
				String name = element.getContributor().getName();
				Bundle bundle = OSGIUtils.getDefault().getBundle(name);
				if(ELEMENT_REFACTORING.equals(element.getName())) {
					String nsUri = element.getAttribute(ATTRIBUTE_PACKAGE);
					EPackage ePackage = PACKAGE_REGISTRY.getEPackage(nsUri);
					
					String classString = element.getAttribute(ATTRIBUTE_CLASS);
					List<String> split = Strings.split(classString, ".");
					classString = split.get(split.size() - 1);
					EClassifier eclassifier = ePackage.getEClassifier(classString);

					try {
						readCustomisation(element.getAttribute(ATTRIBUTE_CUSTOMISATION), bundle, eclassifier, customisation);
						readCustomisation(element.getAttribute(ATTRIBUTE_UICUSTOMISATION), bundle, eclassifier, uicustomisation);
						
						Object object = element.createExecutableExtension(ATTRIBUTE_EXECUTER);
						if(object instanceof RefactoringExecuter) {
							refactorings.put(eclassifier, (RefactoringExecuter)object);
						}
					} catch(Exception exception) {
						RefactoringPlugin.log(exception.getMessage(), exception);
					}
				}
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	private <T extends RefactoringCustomisation> void readCustomisation(String attrValue, Bundle bundle, EClassifier eClassifier, Map<EClassifier, T> customisations) throws Exception {
		String[] parts = attrValue.split(":");
		if(parts.length != 2) {
			RefactoringStatus status = RefactoringStatus.getConfigurationError();
			RefactoringPlugin.log(status);
		}
		Class<?> loadClass = bundle.loadClass(parts[0]);
		Object instance = loadClass.newInstance();
		if(instance instanceof AbstractGuiceAwareExecutableExtensionFactory) {
			AbstractGuiceAwareExecutableExtensionFactory extensionFactory = (AbstractGuiceAwareExecutableExtensionFactory)instance;
			Method method = extensionFactory.getClass().getDeclaredMethod("getInjector");
			method.setAccessible(true);
			injectors.put(eClassifier, (Injector)method.invoke(extensionFactory));
			
			extensionFactory.setInitializationData(null, null, parts[1]);
			Object object = extensionFactory.create();
			if(object instanceof RefactoringCustomisation) {
				customisations.put(eClassifier, (T)object);
			}
		}
	}
}
