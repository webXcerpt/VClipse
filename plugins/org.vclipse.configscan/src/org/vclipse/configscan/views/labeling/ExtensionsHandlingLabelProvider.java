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
package org.vclipse.configscan.views.labeling;

import java.lang.reflect.Method;
import java.util.Map;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.vclipse.configscan.extension.ExtensionPointReader;
import org.vclipse.configscan.impl.model.TestCase;
import org.vclipse.configscan.impl.model.TestRun;

import com.google.common.collect.Maps;
import com.google.inject.Inject;

public class ExtensionsHandlingLabelProvider extends AbstractLabelProvider {

	private boolean extensionEnabled;
	
	private final ExtensionPointReader extensionPointReader;
	
	private Map<String, IBaseLabelProvider> extension2LabelProvider;
	
	@Inject
	public ExtensionsHandlingLabelProvider(ExtensionPointReader reader) {
		extensionPointReader = reader;
		extension2LabelProvider = Maps.newHashMap();
	}
	
	public void enableExtension(boolean enable) {
		extensionEnabled = enable;
	}
	
	public boolean isExtensionEnabled() {
		return extensionEnabled;
	}
	
 	protected IBaseLabelProvider getLabelProviderExtension(TestCase testCase) {
 		TestCase root = testCase.getRoot();
 		if(root instanceof TestRun) {
 			EObject testModel = ((TestRun)root).getTestModel();
 			if(testModel != null) {
 				String fileExtension = testModel.eResource().getURI().fileExtension();
 	 			if(!extension2LabelProvider.containsKey(fileExtension)) {
 	 	 			IBaseLabelProvider labelProvider = extensionPointReader.getLabelProvider(fileExtension);
 	 				extension2LabelProvider.put(fileExtension, labelProvider);
 	 				return labelProvider;
 	 	 		} 
 	 	 		return extensionPointReader.getLabelProvider(fileExtension);
 			}
 		}
 		return null;
 	}
 	
 	protected Object extensionCall(IBaseLabelProvider labelProvider, String methodName, Object object) {
		if(labelProvider == null || object == null) {
			return null;
		} 
		try {
			Method method = labelProvider.getClass().getMethod(methodName, Object.class);
			return method.invoke(labelProvider, object);
		} catch (Exception exception) {
			return null;
		}
	}
}
