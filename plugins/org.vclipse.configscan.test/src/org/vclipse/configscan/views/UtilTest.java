package org.vclipse.configscan.views;

import static org.junit.Assert.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.vclipse.configscan.views.Util;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class UtilTest {

	private Document doc;

	@Before
	public void setUp() throws Exception {
		DocumentBuilder xmlDocBuilder;
		doc = null;
		
		xmlDocBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		
		doc = xmlDocBuilder.newDocument();
		
	
	}

	@After
	public void tearDown() throws Exception {
		doc = null;
	
	}

	@Test
	public void testIsSuccess1() {
		Element root = doc.createElement("root");
		
		root.setAttribute("status", "E");
		
		assertFalse("Attribute status is not available or is \"success\"", Util.isSuccess(root));
	}

	@Test
	public void testIsSuccess2() {
		Element root = doc.createElement("root");
		
		root.setAttribute("status", "S");
		
		assertTrue("Attribute status is not available or is \"error\"", Util.isSuccess(root));
	}
	
}
