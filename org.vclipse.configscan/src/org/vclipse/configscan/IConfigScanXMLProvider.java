package org.vclipse.configscan;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public interface IConfigScanXMLProvider {
	public Document transform(EObject model,  Map<Element, URI> map);
	public HashMap<Element, Element> computeConfigScanMap(Document xmlLog, Document xmlInput);
	public String getMaterialNumber(EObject model);
	public String getBomApplication(EObject model);
}
