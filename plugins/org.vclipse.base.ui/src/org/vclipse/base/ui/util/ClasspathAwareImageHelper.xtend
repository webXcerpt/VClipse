/** 
 * Copyright (c) 2010 - 2015 webXcerpt Software GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * webXcerpt Software GmbH - initial creator
 * www.webxcerpt.com
 */
package org.vclipse.base.ui.util

import java.net.URL
import org.eclipse.jface.resource.ImageDescriptor
import org.eclipse.swt.graphics.Image
import org.eclipse.xtext.ui.PluginImageHelper

/** 
 * Searches for images not only in the current bundle, but in all resources on the classpath.
 * copied and adapted from Xtext source code
 */
class ClasspathAwareImageHelper extends PluginImageHelper implements IExtendedImageHelper {
	override Image getImage(String imageName) {
		var String imgname = imageName ?: getDefaultImage
		if (imgname !== null) {
			val URL imgUrl = plugin.bundle.getResource(pathSuffix + imgname)
			// getEntry has been changed to getResource such that classpath is searched for
			if (imgUrl !== null) {
				var Image result = plugin.imageRegistry.get(imgUrl.toExternalForm)
				if (result === null) {
					val ImageDescriptor id = ImageDescriptor.createFromURL(imgUrl)
					if (id !== null) {
						result = id.createImage
						plugin.imageRegistry.put(imgUrl.toExternalForm, result)
					}
				}
				return result
			}
			val String notFound = getNotFound
			if (!imgname.equals(notFound))
				return getImage(notFound)
		}
		return null
	}

	override ImageDescriptor getImageDescriptor(String name) {
		return getImageDescriptor(getImage(name))
	}

	override ImageDescriptor getImageDescriptor(Image image) {
		return if (image === null) null else ImageDescriptor.createFromImage(image)
	}

}
