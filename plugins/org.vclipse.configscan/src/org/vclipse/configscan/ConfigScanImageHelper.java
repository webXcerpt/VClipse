package org.vclipse.configscan;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.xtext.ui.IImageHelper;

import com.google.inject.Inject;

public class ConfigScanImageHelper {

	@Inject
	private IImageHelper imageHelper;
	
	public ImageDescriptor getImageDescriptor(String name) {
		Image image = imageHelper.getImage(name);
		return image == null ? null : ImageDescriptor.createFromImage(image);
	}
}
