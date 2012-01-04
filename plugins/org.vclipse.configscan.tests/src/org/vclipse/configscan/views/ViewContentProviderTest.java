package org.vclipse.configscan.views;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class ViewContentProviderTest {

	private Document doc;
	private Element root;
	private Element child1;
	private Element child2;
	private Element child3;
	
	private ConfigScanView configScanView;
	private ViewContentProvider vcp;
	
	
	@Before
	public void setUp() throws Exception {
//		waitForJobs();
		configScanView = (ConfigScanView) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(
				ConfigScanView.ID);
//		waitForJobs();
		assertNotNull("ConfigScanView must not be null", configScanView);
		
		delay(3000);
		
		DocumentBuilder xmlDocBuilder;
		
		xmlDocBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		
		doc = xmlDocBuilder.newDocument();

	    root = doc.createElement("log_testgrp");
	    child1 = doc.createElement("log_msg");
	    child1.setAttribute("status", "S");
	    child2 = doc.createElement("log_msg");
	    child2.setAttribute("status", "E");
	    child3 = doc.createElement("log_msg");
	    child3.setAttribute("status", "E");
	    
	    root.setAttribute("root", "true");
	    
	    child2.appendChild(child3);
	    root.appendChild(child1);
	    root.appendChild(child2);
	    root.setAttribute("status", "E");
	    
	    doc.appendChild(root);

	    
		
		vcp = new ViewContentProvider(doc); 
//		configScanView.getTreeViewer().setContentProvider(new ViewContentProvider());			
	}

	/** From eclipse -building commercial-quality plug-ins book
	 * 
	 * @param waitTimeMillis
	 */
	private void delay(long waitTimeMillis) {
		// TODO Auto-generated method stub
		Display display = Display.getCurrent();
		if(display != null) {
			long endTimeMillis = System.currentTimeMillis() + waitTimeMillis;
			while(System.currentTimeMillis() < endTimeMillis) {
				if(!display.readAndDispatch()) {
					display.sleep();
				}
			}
			display.update();
		}
		else {
			try {
				Thread.sleep(waitTimeMillis);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}
	
	@After
	public void tearDown() throws Exception {
		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().hideView(configScanView);
		vcp = null;
		doc = null;
	
	}

	@Ignore
	@Test
	public void testInputChanged() {
		System.out.println(configScanView.getTreeViewer().getInput());
		vcp = new ViewContentProvider(doc); 
		configScanView.getTreeViewer().setContentProvider(vcp);
		System.out.println(configScanView.getTreeViewer().getInput());
	}

	@Test
	public void testGetElements() {
//		configScanView.getTreeViewer().setContentProvider(vcp);
		Object[] objs = vcp.getElements(doc.getDocumentElement());
		assertEquals("objs[] size must be 2", 2, objs.length);
		Element retrieve1 = (Element) objs[0];
		Element retrieve2 = (Element) objs[1];
		assertEquals(retrieve1, child1);
		assertEquals(retrieve2, child2);
	}

	@Test
	public void testGetParent() {
		assertNotNull("Parent must not be null", vcp.getParent(child2));
		assertEquals(root, vcp.getParent(child2));
	}

	@Test
	public void testGetChildren() {
		assertEquals("#Children must be 2", 2, vcp.getChildren(root).length);
	}

	@Test
	public void testHasChildren() {
		assertTrue("root must have children", vcp.hasChildren(root));
	}
	
	@Test
	public void testGetNumberOfFailure() {
		assertEquals("#Failures should be 2", 2, vcp.getNumberOfFailure());
	}
	
	@Test
	public void testGetNumberOfSuccess(){
		assertEquals("#Success should be 1", 1, vcp.getNumberOfSuccess());
	}
	
	@Test
	public void testGetNumberOfRuns() {
		assertEquals("#Runs should be #Success + #Failures", 3, vcp.getNumberOfRuns());
	}

}
