package org.vclipse.configscan.views;

import org.eclipse.emf.common.util.URI;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.IFontProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.w3c.dom.Element;

public interface IConfigScanLabelProvider extends IFontProvider, IColorProvider, ILabelProvider {

	/**
	 * 
	 * @param logElement
	 * @return
	 */
	public Element getIputElement(Element logElement);
	
	/**
	 * 
	 * @param inputElement
	 * @return
	 */
	public Element getLogElement(Element inputElement);
	
	/**
	 * 
	 * @param inputElement
	 * @return
	 */
	public URI getInputUri(Element inputElement);
	
	/**
	 * 
	 * @param uri
	 * @return
	 */
	public Element getInputElement(URI uri);
	
}
