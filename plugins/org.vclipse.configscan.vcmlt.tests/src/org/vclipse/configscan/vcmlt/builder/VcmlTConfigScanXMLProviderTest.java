package org.vclipse.configscan.vcmlt.builder;

import java.util.HashMap;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.junit4.InjectWith;
import org.eclipselabs.xtext.utils.unittesting.XtextRunner2;
import org.eclipselabs.xtext.utils.unittesting.XtextTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.vclipse.configscan.vcmlt.VcmlTInjectorProviderWithVCML;
import org.vclipse.configscan.views.XmlLoader;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.google.common.collect.Maps;
import com.google.inject.Inject;

@RunWith(XtextRunner2.class)
@InjectWith(VcmlTInjectorProviderWithVCML.class)
public class VcmlTConfigScanXMLProviderTest extends XtextTest {

	@Inject
	VcmlTConfigScanXMLProvider csXmlProvider;
	
	@Inject
	XmlLoader xmlLoader; // might be refactored in future
	
	@Test
	public void transformToXML() {
		ignoreSerializationDifferences();
		testFile("simpleTestFile.vcmlt", "ABC.vcml");
		EObject root = getModelRoot();
		HashMap<Element, URI> map = Maps.newHashMap();
		Document document = csXmlProvider.transform(root, map);
		System.err.println(xmlLoader.parseXmlToString(document));
	}
}
