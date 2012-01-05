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
import org.w3c.dom.NodeList;

/** SuccessFilter is a ViewerFilter and is used to filter out the elements which have status="S".
 *  If a Element has any child below it with status "S", it is filtered too (displayed).
 * 
 * @author kulig
 *
 */
class SuccessFilter extends ViewerFilter {
	


	/** Constructor does nothing
	 * 
	 */
	SuccessFilter() {
	}

	/** The select method returns true if element or any Element or Child-Element has an attribute
	 *  named status with a value of "S"
	 *  
	 */
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		if(element instanceof Element) {
			if (isAnythingSuccessUnderNode((Element)element)) {
				return true;
			}
		}
		return false;
	}
	
	/** not implemented (always false)
	 * 
	 */
	public boolean isFilterProperty(Object element, String property) {
		return false;	
	}

	/** This is a recursive method which checks the whole tree under an element till it finds an
	 *  element with attribute status="S"
	 *  
	 * @param element
	 * @return
	 */
	private boolean isAnythingSuccessUnderNode(Element element) {
		if (Util.isSuccess(element)) {
			return true;
		}
		NodeList nodes = element.getChildNodes();
		for(int i = 0; i < nodes.getLength(); i++) {
			if (nodes.item(i) instanceof Element) {
				if(isAnythingSuccessUnderNode((Element)nodes.item(i))) {
					return true;
				}
			}
		}
		return false;
	}
}