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
