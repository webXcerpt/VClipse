package org.vclipse.configscan.views;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.w3c.dom.Element;


/** FailureFilter is a ViewerFilter and is used to filter out the elements which have status="E".
 *  Returns always true, except the status is "S".
 * 
 * @author kulig
 *
 */
class FailureFilter extends ViewerFilter {

	/** Constructor does nothing
	 * 
	 */
	FailureFilter() {
	}

	
	/** The select method returns true if element has not status="S".
	 *  
	 */
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		return element instanceof Element && !Util.isSuccess((Element)element);
	}
	
	
	/** not implemented (always false)
	 * 
	 */
	public boolean isFilterProperty(Object element, String property) {
		return false;	
	}
}