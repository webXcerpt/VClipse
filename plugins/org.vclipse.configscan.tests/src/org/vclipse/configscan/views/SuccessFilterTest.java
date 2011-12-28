package org.vclipse.configscan.views;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.vclipse.configscan.views.SuccessFilter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class SuccessFilterTest {

	private Document doc;


	@Before
	public void setUp() throws Exception {
		DocumentBuilder xmlDocBuilder;
		
		xmlDocBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		
		doc = xmlDocBuilder.newDocument();

	    Element root = doc.createElement("root");
	    Element child1 = doc.createElement("child1");
	    child1.setAttribute("status", "E");
	    Element child2 = doc.createElement("child2");
	    child2.setAttribute("status", "E");
	    Element child3 = doc.createElement("child3");
	    child3.setAttribute("status", "E");
	    
	    root.setAttribute("root", "true");
	    
	    child2.appendChild(child3);
	    root.appendChild(child1);
	    root.appendChild(child2);
	    root.setAttribute("status", "E");
	    
	    doc.appendChild(root);
	    
	}

	@After
	public void tearDown() throws Exception {
		doc = null;
	}

	
	@Test
	public void testIsAnythingSuccessUnderNodeTrue() {
		Object el = doc.getDocumentElement();
		Element successChild = doc.createElement("SuccessChild");
		successChild.setAttribute("status", "S");
		doc.getDocumentElement().appendChild(successChild);
		
		assertTrue("All leafs have status \"E\" or error in implementation", new SuccessFilter().select(null, null, el));
	}
	
	@Test
	public void testIsAnythingSuccessUnderNodeFalse() {
		Object el = doc.getDocumentElement();
		
		assertFalse("At least one leaf has status \"S\" or error in implementation", new SuccessFilter().select(null, null, el));
	}
	
	@Ignore
	@Test
	public void testIsFilterPropertyObjectString() {
		fail("Not in use");
	}

}
