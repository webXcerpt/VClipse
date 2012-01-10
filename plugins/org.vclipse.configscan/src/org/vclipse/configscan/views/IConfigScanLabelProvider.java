package org.vclipse.configscan.views;

import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.IFontProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.w3c.dom.Element;

public interface IConfigScanLabelProvider extends IFontProvider, IColorProvider, ILabelProvider {

	public void setElementMap(Map<Element, Element> element2ElementMap);
	
	public void setInputEObjectMap(Map<Element, URI> inputToEObject);
	
	public void setResourceSet(ResourceSet resourceSet);
}
