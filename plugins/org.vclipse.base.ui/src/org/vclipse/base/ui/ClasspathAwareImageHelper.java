/*******************************************************************************
 * Copyright (c) 2010 webXcerpt Software GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    webXcerpt Software GmbH - initial creator
 ******************************************************************************/
package org.vclipse.base.ui;

import java.net.URL;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.xtext.ui.PluginImageHelper;

/**
 * Searches for images not only in the current bundle, but in all resources on the classpath.
 * 
 * copied and adapted from Xtext source code
 *
 */
public class ClasspathAwareImageHelper extends PluginImageHelper {

	@Override
	public Image getImage(String imageName) {
		String imgname = imageName == null ? getDefaultImage() : imageName;
		if (imgname != null) {
			Image result = null;
			URL imgUrl = getPlugin().getBundle().getResource(getPathSuffix() + imgname); // getEntry has been changed to getResource such that classpath is searched for
			if (imgUrl != null) {
				ImageDescriptor id = null;
				result = getPlugin().getImageRegistry().get(imgUrl.toExternalForm());
				if (result == null) {
					id = ImageDescriptor.createFromURL(imgUrl);
					if (id != null) {
						result = id.createImage();
						getPlugin().getImageRegistry().put(imgUrl.toExternalForm(), result);
					}
				}
				return result;
			}
			String notFound = getNotFound();
			if (!imgname.equals(notFound)) {
				return getImage(notFound);
			}
		}
		return null;
	}
	
	public ImageDescriptor getImageDescriptor(String name) {
		return getImageDescriptor(getImage(name));
	}
	
	public ImageDescriptor getImageDescriptor(Image image) {
		return image == null ? null : ImageDescriptor.createFromImage(image);
	}
}
