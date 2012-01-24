package org.vclipse.configscan.extension;

import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionDelta;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IRegistryChangeEvent;
import org.eclipse.core.runtime.IRegistryChangeListener;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.vclipse.configscan.ConfigScanPlugin;
import org.vclipse.configscan.IConfigScanXMLProvider;

import com.google.common.collect.Maps;

public class ExtensionPointReader implements IRegistryChangeListener {

	// the id of the extension point
	private static final String LANGUAGE_EXTENSION_POINT_ID = "org.vclipse.configscan.testLanguageExtension";

	// the name of the configuration element we are interested in
	private static final String CONFIGURATION_ELEMENT_NAME = "languageExtension";

	// attributes provided by the extension point
	private static final String ATTRIBUTE_XML_PROVIDER = "xmlProvider";
	private static final String ATTRIBUTE_LABEL_PROVIDER = "labelProvider";
	private static final String ATTRIBUTE_FILE_EXTENSION = "file_extension";

	private Map<String, IConfigurationElement> extension2Element;

	public ExtensionPointReader() {
		extension2Element = Maps.newHashMap();
		IExtensionPoint point = Platform.getExtensionRegistry().getExtensionPoint(LANGUAGE_EXTENSION_POINT_ID);
		for(IExtension extension : point.getExtensions()) {
			for(IConfigurationElement element : extension.getConfigurationElements()) {
				if(CONFIGURATION_ELEMENT_NAME.equals(element.getName())) {
					extension2Element.put(element.getAttribute(ATTRIBUTE_FILE_EXTENSION), element);
				}
			}
		}
	}
	
	public boolean hasExtensionFor(String fileExtension) {
		return extension2Element.containsKey(fileExtension);
	}
	
	public void registryChanged(IRegistryChangeEvent event) {
		for(IExtensionDelta delta : event.getExtensionDeltas()) {
			if(delta.getExtensionPoint().getUniqueIdentifier().equals(LANGUAGE_EXTENSION_POINT_ID)) {
				if(IExtensionDelta.ADDED == delta.getKind()) {
					for(IConfigurationElement element : delta.getExtension().getConfigurationElements()) {
						if(CONFIGURATION_ELEMENT_NAME.equals(element.getName())) {
							extension2Element.put(element.getAttribute(ATTRIBUTE_FILE_EXTENSION), element);
						}
					}
				} else {
					for(IConfigurationElement element : delta.getExtension().getConfigurationElements()) {
						if(CONFIGURATION_ELEMENT_NAME.equals(element.getName())) {
							extension2Element.remove(element.getAttribute(ATTRIBUTE_FILE_EXTENSION));
						}
					}
				}
			}
		}
	}
	
	public IConfigScanXMLProvider getXmlProvider(String extension) {
		try {
			return (IConfigScanXMLProvider)extension2Element.get(extension).createExecutableExtension(ATTRIBUTE_XML_PROVIDER);
		} catch (CoreException exception) {
			ConfigScanPlugin.log(exception.getMessage(), IStatus.ERROR);
			return null;
		}
	}
	
	public IBaseLabelProvider getLabelProvider(String extension) {
		try {
			return (IBaseLabelProvider)extension2Element.get(extension).createExecutableExtension(ATTRIBUTE_LABEL_PROVIDER);
		} catch (CoreException exception) {
			ConfigScanPlugin.log(exception.getMessage(), IStatus.ERROR);
			return null;
		}
	}

}
