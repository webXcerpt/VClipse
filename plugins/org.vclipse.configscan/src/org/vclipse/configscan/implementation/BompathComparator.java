package org.vclipse.configscan.implementation;

import java.util.Comparator;

import org.w3c.dom.Element;

public class BompathComparator implements Comparator<Element> {

	public int compare(Element o1, Element o2) {
		return o1.getAttribute("bompath").compareTo(o2.getAttribute("bompath"));
	}
	
}
