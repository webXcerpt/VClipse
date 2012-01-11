package org.vclipse.configscan;

import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public interface IConfigScanReverseXmlTransformation {

	public Map<Element, Element> computeConfigScanMap(Document xmlLogDocument, Document xmlInputDocument);

}
