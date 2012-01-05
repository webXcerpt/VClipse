/*******************************************************************************
 * Copyright (c) 2012 webXcerpt Software GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    webXcerpt Software GmbH - initial creator
 ******************************************************************************/
package org.vclipse.configscan.views;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.w3c.dom.Element;

public class RemoveLogHeaderFilter extends ViewerFilter {

	
	/** This Filter removes leaves from tree, which are not necessary.
	 * 
	 */
	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		return element instanceof Element && !"log_header".equals(((Element)element).getNodeName()) 
				&& !"End session".equals(((Element) element).getAttribute("title")) && !"log_undotg".equals(((Element)element).getNodeName());
	}

}
