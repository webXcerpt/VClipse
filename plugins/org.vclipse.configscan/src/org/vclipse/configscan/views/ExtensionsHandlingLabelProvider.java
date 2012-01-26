package org.vclipse.configscan.views;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.DecorationOverlayIcon;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.progress.PendingUpdateAdapter;
import org.vclipse.configscan.IConfigScanImages;
import org.vclipse.configscan.extension.ExtensionPointReader;
import org.vclipse.configscan.impl.model.TestCase;
import org.vclipse.configscan.impl.model.TestCase.Status;
import org.vclipse.configscan.impl.model.TestGroup;
import org.vclipse.configscan.impl.model.TestRun;

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
		if(image == null) {
			return image;
		} else if(Status.FAILURE == testCase.getStatus()) {
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
		} else if(!(testCase instanceof TestGroup)) {
			String tooltip = "ConfigScan XML LOG: " + testCase.getTitle();
			tooltip += "\n\nTestrun : " + testCase.getRoot().getTitle();
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
			String extensionCall = (String)extensionCall(labelProvider, "getText", testCase);
			if(extensionCall != null && !extensionCall.isEmpty()) {
				styledString.append(new StyledString("     Descriprion: " + extensionCall, StyledString.DECORATIONS_STYLER));
			} 
			return styledString;
		}
		return new StyledString(EMPTY);
	}

	protected StyledString styledText(TestRun testRun) {
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
	
	protected Image image(TestRun testRun) {
		return imageHelper.getImage(testRun.getImageDescriptor(null));
	}

	protected Image image(PendingUpdateAdapter adapter) {
		return imageHelper.getImage(IConfigScanImages.HOURGLASS);
	}
	
 	private IBaseLabelProvider getLabelProviderExtension(TestCase testCase) {
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
 	
 	private Object extensionCall(IBaseLabelProvider labelProvider, String methodName, Object object) {
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
