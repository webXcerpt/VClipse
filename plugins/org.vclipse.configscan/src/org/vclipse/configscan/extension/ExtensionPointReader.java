package org.vclipse.configscan.extension;

import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.xtext.util.Pair;
import org.eclipse.xtext.util.Tuples;
import org.vclipse.configscan.ConfigScanPlugin;
import org.vclipse.configscan.IConfigScanXMLProvider;

import com.google.common.collect.Maps;

public class ExtensionPointReader {

	// the id of the extension point
	private static final String XML_PROVIDER_EXTENSION_POINT_ID = "org.vclipse.configscan.testLanguageExtension";

	// the name of the configuration element we are interested in
	private static final String CONFIGURATION_ELEMENT_NAME = "languageExtension";

	// attributes provided by the extension point
	private static final String ATTRIBUTE_XML_PROVIDER = "xmlProvider";
	private static final String ATTRIBUTE_LABEL_PROVIDER = "labelProvider";
	private static final String ATTRIBUTE_FILE_EXTENSION = "file_extension";

	private Map<String, Pair<IConfigScanXMLProvider, IBaseLabelProvider>> languageExtensions;

	public Map<String, Pair<IConfigScanXMLProvider, IBaseLabelProvider>> getExtensions() {
		if(languageExtensions == null) {
			languageExtensions = Maps.newHashMapWithExpectedSize(5);
			readXmlProviderExtension();
		}
		return languageExtensions;
	}

	// reads the extension point
	private void readXmlProviderExtension() {
		IExtensionPoint extensionPoint = Platform.getExtensionRegistry().getExtensionPoint(XML_PROVIDER_EXTENSION_POINT_ID);
		for(IExtension extension : extensionPoint.getExtensions()) {
			for(IConfigurationElement configurationElement : extension.getConfigurationElements()) {
				if(CONFIGURATION_ELEMENT_NAME.equals(configurationElement.getName())) {
					String fileExtension = configurationElement.getAttribute(ATTRIBUTE_FILE_EXTENSION);
					if(fileExtension != null && !fileExtension.isEmpty()) {
						// only one xml provider for one file extension is allowed
						if(languageExtensions.containsKey(fileExtension)) {
							continue;
						}

						try {
							// get the xml provider
							Object xmlProviderObject = configurationElement.createExecutableExtension(ATTRIBUTE_XML_PROVIDER);
							if(xmlProviderObject instanceof IConfigScanXMLProvider) {
								IConfigScanXMLProvider xmlProvider = (IConfigScanXMLProvider)xmlProviderObject;

								// get the label provider, its optional
								IBaseLabelProvider labelProvider = null;
								Object labelProviderObject = configurationElement.createExecutableExtension(ATTRIBUTE_LABEL_PROVIDER);
								if(labelProviderObject instanceof IBaseLabelProvider) {
									labelProvider = (IBaseLabelProvider)labelProviderObject;
								}
								languageExtensions.put(fileExtension, Tuples.create(xmlProvider, labelProvider));
							}
						} catch (CoreException exception) {
							// log the error
							ConfigScanPlugin.log(exception.getMessage(), IStatus.ERROR, exception.getCause());

							// handle the next extension
							continue;
						}
					}
				}
			}
		}
	}
}
