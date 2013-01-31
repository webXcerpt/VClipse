/*******************************************************************************
 * Copyright (c) 2010 - 2013 webXcerpt Software GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *  
 * Contributors:
 *     	webXcerpt Software GmbH - initial creator
 * 		www.webxcerpt.com
 ******************************************************************************/
package org.vclipse.configscan.impl;

import java.util.Comparator;

import org.w3c.dom.Element;

public class BompathComparator implements Comparator<Element> {

	public int compare(Element o1, Element o2) {
		return o1.getAttribute("bompath").compareTo(o2.getAttribute("bompath"));
	}
	
}
