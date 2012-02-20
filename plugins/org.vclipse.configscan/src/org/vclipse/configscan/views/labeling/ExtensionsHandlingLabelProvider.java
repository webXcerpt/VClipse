package org.vclipse.configscan.views.labeling;

import java.lang.reflect.InvocationTargetException;
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
 	 	 		} else {
 	 	 			return extensionPointReader.getLabelProvider(fileExtension);
 	 	 		}
 			}
 		}
 		return null;
 	}
 	
 	protected Object extensionCall(IBaseLabelProvider labelProvider, String methodName, Object object) {
		if(labelProvider == null || object == null) {
			return null;
		} else {
			try {
				Method method = labelProvider.getClass().getMethod(methodName, Object.class);
				return method.invoke(labelProvider, object);
			} catch (SecurityException exception) {
				return null;
			} catch (NoSuchMethodException exception) {
				return null;
			} catch (IllegalArgumentException exception) {
				return null;
			} catch (IllegalAccessException exception) {
				return null;
			} catch (InvocationTargetException exception) {
				return null;
			}
		}
	}
}
