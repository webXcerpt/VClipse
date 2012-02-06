package org.vclipse.configscan.views;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class ConfigScanViewTest {

	private ConfigScanView configScanView;
	
	
	@Before
	public void setUp() throws Exception {
//		waitForJobs();
		configScanView = (ConfigScanView) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(
				ConfigScanView.ID);
//		waitForJobs();
		assertNotNull("ConfigScanView must not be null", configScanView);
		delay(3000);
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
	
	/** From eclipse -building commercial-quality plug-ins book
	 * 
	 */
	private void waitForJobs() {
		// TODO Auto-generated method stub
		while(Platform.getJobManager().currentJob() != null) {
			delay(1000);
		}
	}

	@After
	public void tearDown() throws Exception {
		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().hideView(configScanView);
	}

	@Test
	public void testCreatePartControlComposite() {
		fail("Not yet implemented");
	}

	private Document createDoc() {
		// TODO Auto-generated method stub
		Document docLog;
		Element log_session;
		Element log_testgrp0;
		Element log_testgrp1;
		Element child1_0;
		Element child1_1;
		Element child1_2;
		Element child1_3;
		Element child1_4;
		
		DocumentBuilder xmlDocBuilder = null;
		
		try {
			xmlDocBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		docLog = xmlDocBuilder.newDocument();

		log_session = docLog.createElement("log_session");
		log_session.setAttribute("level", "1");
		log_session.setAttribute("status", "E");
		
		log_testgrp0 = docLog.createElement("log_testgrp");
		log_testgrp0.setAttribute("level", "2");
	    log_testgrp0.setAttribute("status", "S");
		
	    log_testgrp1 = docLog.createElement("log_testgrp");
	    log_testgrp1.setAttribute("level", "2");
	    log_testgrp1.setAttribute("status", "E");
	    
	    child1_0 = docLog.createElement("log_msg");
	    child1_0.setAttribute("status", "S");
	    child1_0.setAttribute("level", "3");
	    child1_0.setAttribute("v1", "CONSISTENT");
	    child1_0.setAttribute("v2", "OK");
	    child1_0.setAttribute("cmd", "CHECK ITEM STATUS");
	    child1_0.setAttribute("bompath", "/");
	    
	    
	    child1_1 = docLog.createElement("log_msg");
	    child1_1.setAttribute("status", "E");
	    child1_1.setAttribute("level", "3");
	    child1_1.setAttribute("v1", "COMPLETE");
	    child1_1.setAttribute("v2", "OK");
	    child1_1.setAttribute("cmd", "CHECK ITEM STATUS");
	    child1_1.setAttribute("bompath", "/");
	    
	    
	    child1_2 = docLog.createElement("log_msg");
	    child1_2.setAttribute("status", "E");
	    child1_2.setAttribute("level", "3");
	    child1_2.setAttribute("v1", "1.0");
	    child1_2.setAttribute("v2", "=");
	    child1_2.setAttribute("v3", "1");
	    child1_2.setAttribute("cmd", "CHECK ITEM QUANTITY");
	    child1_2.setAttribute("bompath", "/ NV10023021020");
	    
	    
	    child1_3 = docLog.createElement("log_msg");
	    child1_3.setAttribute("status", "S");
	    child1_3.setAttribute("level", "3");
	    child1_3.setAttribute("v1", "false");
	    child1_3.setAttribute("v2", "S");
	    child1_3.setAttribute("v3", "/ NV1004446");
	    child1_3.setAttribute("cmd", "CHECK ITEM EXIST");
	    child1_3.setAttribute("bompath", "/ NV1004446");
	    
	    child1_4 = docLog.createElement("log_msg");
	    child1_4.setAttribute("status", "E");
	    child1_4.setAttribute("level", "3");
	    child1_4.setAttribute("v1", "NETVIEWER_VERSION_P");
	    child1_4.setAttribute("v2", "10.1");
	    child1_4.setAttribute("v3", "/");
	    child1_4.setAttribute("cmd", "SET CSTIC VALUE");
	    child1_4.setAttribute("bompath", "/");
	    
	    
//	    log_testgrp.setAttribute("root", "true");
	    
	    
	    log_testgrp1.appendChild(child1_0);
	    log_testgrp1.appendChild(child1_1);
	    log_testgrp1.appendChild(child1_2);
	    log_testgrp1.appendChild(child1_3);
	    log_testgrp1.appendChild(child1_4);
	    
	    log_session.appendChild(log_testgrp0);
	    log_session.appendChild(log_testgrp1);
	    
	    docLog.appendChild(log_session);
		
		return docLog;
	}

	@Test
	public void testSetInput() {
		fail("Not yet implemented");
	}

}
