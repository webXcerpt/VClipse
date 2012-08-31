package org.vclipse.refactoring;

import java.lang.reflect.Method;
import java.util.List;

import org.eclipse.core.internal.registry.osgi.OSGIUtils;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IContributor;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EPackage.Registry;
import org.eclipse.xtext.ui.guice.AbstractGuiceAwareExecutableExtensionFactory;
import org.eclipse.xtext.util.Strings;
import org.osgi.framework.Bundle;
import org.vclipse.base.BasePlugin;
import org.vclipse.refactoring.core.Refactoring;
import org.vclipse.refactoring.core.RefactoringCustomisation;
import org.vclipse.refactoring.ui.RefactoringUICustomisation;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.inject.Injector;
import com.google.inject.Singleton;

@Singleton
public class ExtensionsReader {

	private static final Registry PACKAGE_REGISTRY = Registry.INSTANCE;

	private static final String ID = "org.vclipse.refactoring";
	
	private static final String ELEMENT_REFACTORING = "refactoring";
	
	private static final String ATTRIBUTE_CLASS = "class";
	private static final String ATTRIBUTE_PACKAGE = "package";
	private static final String ATTRIBUTE_EXECUTOR = "executor";
	private static final String ATTRIBUTE_CUSTOMISATION = "customisation";
	private static final String ATTRIBUTE_UICUSTOMISATION = "uicustomisation";
	
	private Multimap<EClassifier, EPackage> classifier;
	private Multimap<EClassifier, Injector> injectors;
	private Multimap<EClassifier, RefactoringCustomisation> customisation;
	private Multimap<EClassifier, RefactoringUICustomisation> uicustomisation;
	private Multimap<EClassifier, Refactoring> refactorings;
		
	private void readExtensions() {
		classifier = HashMultimap.create();
		injectors = HashMultimap.create();
		customisation = HashMultimap.create();
		uicustomisation = HashMultimap.create();
		refactorings = HashMultimap.create();
		
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IExtensionPoint point = registry.getExtensionPoint(ID);
		for(IExtension extension : point.getExtensions()) {
			for(IConfigurationElement element : extension.getConfigurationElements()) {
				IContributor contributor = element.getContributor();
				String contributorName = contributor.getName();
				Bundle bundle = OSGIUtils.getDefault().getBundle(contributorName);
				String name = element.getName();
				if(ELEMENT_REFACTORING.equals(name)) {
					String nsUri = element.getAttribute(ATTRIBUTE_PACKAGE);
					EPackage ePackage = PACKAGE_REGISTRY.getEPackage(nsUri);
					
					String classString = element.getAttribute(ATTRIBUTE_CLASS);
					List<String> split = Strings.split(classString, ".");
					classString = split.get(split.size() - 1);
					EClassifier eclassifier = ePackage.getEClassifier(classString);
					classifier.put(eclassifier, ePackage);
					
					try {
						readCustomisation(element, bundle, eclassifier, ATTRIBUTE_CUSTOMISATION, customisation);
						readCustomisation(element, bundle, eclassifier, ATTRIBUTE_UICUSTOMISATION, uicustomisation);
						
						Object object = element.createExecutableExtension(ATTRIBUTE_EXECUTOR);
						if(object instanceof Refactoring) {
							refactorings.put(eclassifier, (Refactoring)object);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	private <T extends RefactoringCustomisation> void readCustomisation(IConfigurationElement element, Bundle bundle, 
			EClassifier eClassifier, String attribute, Multimap<EClassifier, T> customisations) throws Exception {
		String value = element.getAttribute(attribute);
		String[] parts = value.split(":");
		if(parts.length != 2) {
			BasePlugin.log("Instantiation of this extension point should contain executable extension part.", IStatus.ERROR);
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
	
	public Multimap<EClassifier, EPackage> getClassifier() {
		if(classifier == null) {
			readExtensions();
		}
		return Multimaps.unmodifiableMultimap(classifier);
	}
	
	public Multimap<EClassifier, RefactoringCustomisation> getCustomisation() {
		if(customisation == null) {
			readExtensions();
		}
		return Multimaps.unmodifiableMultimap(customisation);
	}
	
	public Multimap<EClassifier, RefactoringUICustomisation> getUICustomisation() {
		if(uicustomisation == null) {
			readExtensions();
		}
		return Multimaps.unmodifiableMultimap(uicustomisation);
	}
	
	public Multimap<EClassifier, Refactoring> getRefactorings() {
		if(refactorings == null) {
			readExtensions();
		}
		return Multimaps.unmodifiableMultimap(refactorings);
	}
	
	public Multimap<EClassifier, Injector> getInjector() {
		if(injectors == null) {
			readExtensions();
		}
		return Multimaps.unmodifiableMultimap(injectors);
	}
}
