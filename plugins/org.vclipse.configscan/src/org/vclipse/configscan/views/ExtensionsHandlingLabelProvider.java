package org.vclipse.configscan.views;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.DecorationOverlayIcon;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.progress.PendingUpdateAdapter;
import org.vclipse.configscan.ConfigScanPlugin;
import org.vclipse.configscan.IConfigScanImages;
import org.vclipse.configscan.extension.ExtensionPointReader;
import org.vclipse.configscan.impl.model.TestCase;
import org.vclipse.configscan.impl.model.TestCase.Status;
import org.vclipse.configscan.impl.model.TestRunAdapter;

import com.google.common.collect.Maps;
import com.google.inject.Inject;

public final class ExtensionsHandlingLabelProvider extends AbstractLabelProvider {

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
	
	protected Image decorateImage(Image image, TestCase testCase) {
		if(Status.FAILURE == testCase.getStatus()) {
			ImageData imageData = image.getImageData();
			ImageDescriptor[] overlay = new ImageDescriptor[5];
			overlay[2] = imageHelper.getImageDescriptor(IConfigScanImages.ERROR_OVERLAY);
			return new DecorationOverlayIcon(image, overlay, new Point(imageData.width, imageData.height)).createImage();
		} else {
			ImageData imageData = image.getImageData();
			ImageDescriptor[] overlay = new ImageDescriptor[5];
			overlay[2] = imageHelper.getImageDescriptor(IConfigScanImages.SUCCESS_OVERLAY);
			return new DecorationOverlayIcon(image, overlay, new Point(imageData.width, imageData.height)).createImage();
		}
	}
	
	protected String toolTip(TestCase testCase) {
		if(extensionEnabled) {
			IBaseLabelProvider labelProvider = getLabelProviderExtension(testCase);
			Object result = extensionCall(labelProvider, "getToolTipText", testCase);
			if(result instanceof String) {
				return (String)result;
			}
		} else {
			if(testCase.getAdapter(TestRunAdapter.class) == null) {
				String tooltip = "ConfigScan XML LOG: " + testCase.getTitle();
				
				tooltip += "\n\nTestrun : " + testCaseUtility.getRoot(testCase).getTitle();
				tooltip += "\nTestgroup : " + testCase.getParent().getTitle();
				
				// TODO which info is interesting for the user ?
				
				// 	 	 			NamedNodeMap namedNodeMap = inputElement.getAttributes();
				// 	 	 			for(int i = 0; i < namedNodeMap.getLength(); i++) {
				// 	 	 				Node node = namedNodeMap.item(i);
				// 	 	 				tooltip += " " + node.getNodeName() + "=\"" + node.getNodeValue() + "\"";
				// 	 	 			}
				// 	 	 			URI uri = testCase.getSourceUri();
				// 	 	 			TestCase root = testCaseUtility.getRoot(testCase);
				// 	 	 			EObject testModel = ((TestRunAdapter)root.getAdapter(TestRunAdapter.class)).getTestModel();
				// 	 	 			EObject eObject = testModel.eResource().getResourceSet().getEObject(uri, true);
				// 	 	 			if(eObject != null && extensionEnabled && getLabelProviderExtension(testCase) != null) {
				// 	 	 				//tooltip += "\n" + labelProviderExtension.getText(eObject);
				// 	 	 			}
				return tooltip;
			}
		}
		return null;
	}
	
	protected StyledString styledText(TestCase testCase) {
		IBaseLabelProvider labelProvider = getLabelProviderExtension(testCase);
		if(extensionEnabled) {
			Object result = extensionCall(labelProvider, "getStyledText", testCase);
			if(result instanceof StyledString) {
				return (StyledString)result;
			} 
			result = extensionCall(labelProvider, "getText", testCase);
			if(result instanceof String) {
				return new StyledString((String)result);
			}
		} else {
			StyledString styledString = new StyledString(testCase.getTitle()).append(getStatistics(testCase));	
			String extensionCall = (String)extensionCall(labelProvider, "getText", getReferencedEObject(testCase));
			if(extensionCall != null && !extensionCall.isEmpty()) {
				styledString.append(new StyledString("     Descriprion: " + extensionCall, StyledString.DECORATIONS_STYLER));
			} 
			return styledString;
		}
		return new StyledString(EMPTY);
	}

	protected StyledString styledText(TestRunAdapter testRun) {
		return new StyledString(testRun.getLabel(null));
	}
	
	protected StyledString styledText(PendingUpdateAdapter adapter) {
		return new StyledString(adapter.getLabel(null));
	}
	
	protected Image image(TestCase testCase) {
		if(extensionEnabled) {
			return decorateImage(
					(Image)extensionCall(getLabelProviderExtension(testCase), "getImage", testCase), testCase);
		} else {
			return testCase.getStatus() == Status.SUCCESS 
					? imageHelper.getImage(IConfigScanImages.SUCCESS) 
							: imageHelper.getImage(IConfigScanImages.ERROR);
		}
	}
	
	protected Image image(TestRunAdapter adapter) {
		return imageHelper.getImage(adapter.getImageDescriptor(null));
	}
	
	protected Image image(PendingUpdateAdapter adapter) {
		return imageHelper.getImage(IConfigScanImages.HOURGLASS);
	}
	
 	private IBaseLabelProvider getLabelProviderExtension(TestCase testCase) {
 		Object adapter = testCase.getAdapter(TestRunAdapter.class);
 		if(adapter == null) {
 			adapter = testCaseUtility.getRoot(testCase).getAdapter(TestRunAdapter.class);
 		}
 		String fileExtension = ((TestRunAdapter)adapter).getTestModel().eResource().getURI().fileExtension();
 		if(!extension2LabelProvider.containsKey(fileExtension)) {
 			IBaseLabelProvider labelProvider = extensionPointReader.getLabelProvider(fileExtension);
			extension2LabelProvider.put(fileExtension, labelProvider);
			return labelProvider;
 		} else {
 			return extensionPointReader.getLabelProvider(fileExtension);
 		}
 	}
 	
 	private Object extensionCall(IBaseLabelProvider labelProvider, String methodName, Object object) {
		if(labelProvider == null) {
			return null;
		} else {
			try {
				Method method = labelProvider.getClass().getMethod(methodName, Object.class);
				return method.invoke(labelProvider, object);
			} catch (SecurityException exception) {
				ConfigScanPlugin.log(exception.getMessage(), IStatus.ERROR);
			} catch (NoSuchMethodException exception) {
				ConfigScanPlugin.log(exception.getMessage(), IStatus.ERROR);
			} catch (IllegalArgumentException exception) {
				ConfigScanPlugin.log(exception.getMessage(), IStatus.ERROR);
			} catch (IllegalAccessException exception) {
				ConfigScanPlugin.log(exception.getMessage(), IStatus.ERROR);
			} catch (InvocationTargetException exception) {
				ConfigScanPlugin.log(exception.getMessage(), IStatus.ERROR);
			}
		}
		return null;
	}
}
