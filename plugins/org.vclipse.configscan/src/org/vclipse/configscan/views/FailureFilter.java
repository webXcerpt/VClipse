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
import org.vclipse.configscan.utils.DocumentUtility;
import org.w3c.dom.Element;

import com.google.inject.Inject;


/** FailureFilter is a ViewerFilter and is used to filter out the elements which have status="E".
 *  Returns always true, except the status is "S".
 * 
 * @author kulig
 *
 */
class FailureFilter extends ViewerFilter {

	@Inject
	private DocumentUtility documentUtility;
	
	/** The select method returns true if element has not status="S".
	 *  
	 */
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		return element instanceof Element && !documentUtility.isSuccess((Element)element);
	}
	
	
	/** not implemented (always false)
	 * 
	 */
	public boolean isFilterProperty(Object element, String property) {
		return false;	
	}
}