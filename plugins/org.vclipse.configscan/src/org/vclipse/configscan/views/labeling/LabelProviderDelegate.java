package org.vclipse.configscan.views.labeling;

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
import org.vclipse.configscan.impl.model.TestRun;
import org.w3c.dom.NamedNodeMap;

import com.google.inject.Inject;

public class LabelProviderDelegate extends ExtensionsHandlingLabelProvider {

	@Inject
	public LabelProviderDelegate(ExtensionPointReader reader) {
		super(reader);
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
		if(isExtensionEnabled()) {
			IBaseLabelProvider labelProvider = getLabelProviderExtension(testCase);
			Object result = extensionCall(labelProvider, "getToolTipText", testCase);
			if(result instanceof String) {
				return (String)result;
			}
		} else {
			StringBuffer stringBuffer = new StringBuffer();
			NamedNodeMap attributes = testCase.getLogElement().getAttributes();
			for(String current : new String[]{"cstic", "item", "i_status"}) {
				String text = extractText(attributes, "ConfigScan XML log for " + 
						(current.equals("i_status") ? " status statement " : ""), current);
				if(text == null) {
					continue;
				} else {
					stringBuffer.append(text);
				}
			}
			stringBuffer.append("\n");
			stringBuffer.append("Test run: " + testCase.getRoot().getTitle());
			stringBuffer.append("\n");
			stringBuffer.append("Test group: " + testCase.getParent().getTitle());
			stringBuffer.append("\n");
			stringBuffer.append(extractText(attributes, "Bompath: ", "bompath"));
			stringBuffer.append("\n");
			stringBuffer.append(extractText(attributes, "Command: ", "cmd"));
			stringBuffer.append("\n");
			stringBuffer.append(extractText(attributes, "Execution time: ", "ttime"));
			return stringBuffer.toString();
		}
		return null;
	}
	
	protected Image image(TestCase testCase) {
		if(isExtensionEnabled()) {
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
	
	protected StyledString styledText(TestCase testCase) {
		IBaseLabelProvider labelProvider = getLabelProviderExtension(testCase);
		if(isExtensionEnabled()) {
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
			String description = (String)extensionCall(labelProvider, "getText", testCase);
			if(description != null && !description.isEmpty()) {
				styledString.append(new StyledString("  " + description, StyledString.DECORATIONS_STYLER));
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
}
