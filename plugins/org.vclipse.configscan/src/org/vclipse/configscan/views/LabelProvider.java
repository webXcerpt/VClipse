package org.vclipse.configscan.views;

import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.progress.IDeferredWorkbenchAdapter;
import org.eclipse.ui.progress.PendingUpdateAdapter;
import org.eclipse.xtext.util.Pair;
import org.vclipse.configscan.ConfigScanImageHelper;
import org.vclipse.configscan.IConfigScanXMLProvider;
import org.vclipse.configscan.extension.ExtensionPointReader;
import org.vclipse.configscan.implementation.ConfigScanTestCase;
import org.vclipse.configscan.implementation.ConfigScanTestRun;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import com.google.inject.Inject;

public final class LabelProvider extends ColumnLabelProvider  {

	private ConfigScanImageHelper imageHelper;
	
	private static final String EMPTY = "";
	
	private boolean shouldDelegate;
	
	private Map<String, Pair<IConfigScanXMLProvider, ILabelProvider>> extensions;
	
	@Inject
	public LabelProvider(ConfigScanImageHelper imageHelper, ExtensionPointReader reader) {
		extensions = reader.getExtensions();
		this.imageHelper = imageHelper;
	}
	
	public void enableExtension(boolean delegate) {
		this.shouldDelegate = delegate;
	}
	
	@Override
	public String getText(Object object) {
		if(shouldDelegate && object instanceof ConfigScanTestCase) {
			ILabelProvider labelProvider = getLabelProviderExtension((ConfigScanTestCase)object);
			if(labelProvider != null) {
				return labelProvider.getText(object);
			}
		} else if(object instanceof PendingUpdateAdapter) {
			return ((PendingUpdateAdapter)object).getLabel(object);
		} if(object instanceof IDeferredWorkbenchAdapter) {
			return ((IDeferredWorkbenchAdapter)object).getLabel(null);
		}
		return EMPTY;
	}
	
	@Override
	public Image getImage(Object object) {
		if(shouldDelegate && object instanceof ConfigScanTestCase) {
			ILabelProvider labelProvider = getLabelProviderExtension((ConfigScanTestCase)object);
			if(labelProvider != null) {
				return labelProvider.getImage(object);
			}
		} else if(object instanceof IDeferredWorkbenchAdapter) {
			return imageHelper.getImage(((IDeferredWorkbenchAdapter)object).getImageDescriptor(null));
		}
		return null;
	}

	@Override
	public int getToolTipDisplayDelayTime(Object object) {
		return 500;
	}
	
	@Override
	public int getToolTipTimeDisplayed(Object object) {
 		return 10000;
 	}
	
 	public String getToolTipText(Object object) {
 		if(object instanceof ConfigScanTestCase) {
 			ConfigScanTestCase testCase = (ConfigScanTestCase)object;
 			if(!testCase.isContainer()) {
 				ILabelProvider labelProviderExtension = getLabelProviderExtension(testCase);
 				Element inputElement = testCase.getInputElement();
 	 			String tooltip = "ConfigScan XML LOG: " + inputElement.getTagName();
 	 			NamedNodeMap namedNodeMap = inputElement.getAttributes();
 	 			for(int i = 0; i < namedNodeMap.getLength(); i++) {
 	 				Node node = namedNodeMap.item(i);
 	 				tooltip += " " + node.getNodeName() + "=\"" + node.getNodeValue() + "\"";
 	 			}
 	 			URI uri = testCase.getTestStatementUri();
 	 			EObject testModel = testCase.getTestRun().getTestModel();
 	 			EObject eObject = testModel.eResource().getResourceSet().getEObject(uri, true);
 	 			if(eObject != null && shouldDelegate && labelProviderExtension != null) {
 	 				tooltip += "\n" + labelProviderExtension.getText(eObject);
 	 			}
 	 			return tooltip;
 			}
 		}
 		return null;
 	}

 	private boolean canHandleExtension(ConfigScanTestCase testObject, String targetExtension) {
 		ConfigScanTestRun testRun = ((ConfigScanTestCase)testObject).getTestRun();
 		String fileExtension = testRun.getTestModel().eResource().getURI().fileExtension();
 		return targetExtension.equals(fileExtension);
 	}
 	
 	private ILabelProvider getLabelProviderExtension(ConfigScanTestCase testCase) {
 		for(String fileExtension : extensions.keySet()) {
			if(canHandleExtension(testCase, fileExtension)) {
				return extensions.get(fileExtension).getSecond();
			}
		}
 		return null;
 	}
}
