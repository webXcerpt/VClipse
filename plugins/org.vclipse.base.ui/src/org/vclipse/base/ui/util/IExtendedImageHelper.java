package org.vclipse.base.ui.util;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.xtext.ui.IImageHelper;

public interface IExtendedImageHelper extends IImageHelper {

	public ImageDescriptor getImageDescriptor(String name);
	
	public ImageDescriptor getImageDescriptor(Image image);
	
}
