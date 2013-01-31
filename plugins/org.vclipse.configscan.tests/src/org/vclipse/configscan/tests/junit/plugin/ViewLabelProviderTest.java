/*******************************************************************************
 * Copyright (c) 2010 - 2013 webXcerpt Software GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *  
 * Contributors:
 *     	webXcerpt Software GmbH - initial creator
 * 		www.webxcerpt.com
 ******************************************************************************/
package org.vclipse.configscan.tests.junit.plugin;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.ui.PlatformUI;
import org.eclipse.xtext.resource.XtextResourceSet;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.vclipse.configscan.views.ConfigScanView;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

//import static org.mockito.Mockito.*;

public class ViewLabelProviderTest {
	
	private static final String TEST_CMLT = "C:/eclipse/runtime-New_configuration(2)/JUnit_TestCmlt.cmlt";

	
	private Document docLog;
	private Document docInput;
	private Element root;
	private Element child1_0;
	private Element child1_1;
	private Element child1_2;
	private Element child1_3;
	private Element child1_4;
	private Element rootInput;
	private Element child1_0Input;
	private Element child1_1Input;
	private Element child1_2Input;
	private Element child1_3Input;
	private Element child1_4Input;
	 
	//private ViewLabelProvider vlp;
	
	private ConfigScanView configScanView;
	
	private Map<Element, URI> inputToUri; 
	
	private Resource res;
	
	private File file;
	
//	private static CmltFactory CMLT = CmltFactory.eINSTANCE;
	
	@Before
	public void setUp() throws Exception {
		configScanView = (ConfigScanView) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(
				ConfigScanView.ID);
		
		file = new File(TEST_CMLT);
		
		res = new XtextResourceSet().getResource(URI.createURI(file.toURI().toString()), true);
		
		inputToUri = new HashMap<Element, URI>();
		
		DocumentBuilder xmlDocBuilder;
		
		xmlDocBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		
		docLog = xmlDocBuilder.newDocument();

	    root = docLog.createElement("log_testgrp");
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
	    
	    
	    root.setAttribute("root", "true");
	    
	    
	    root.appendChild(child1_0);
	    root.appendChild(child1_1);
	    root.appendChild(child1_2);
	    root.appendChild(child1_3);
	    root.appendChild(child1_4);
	    root.setAttribute("status", "E");
	    
	    docLog.appendChild(root);
	
	    
	    
	    docInput = xmlDocBuilder.newDocument();
	    
	    rootInput = docInput.createElement("testgroup");
	    child1_0Input = docInput.createElement("checkinststatus");
	    child1_0Input.setAttribute("isconsistent", "TRUE");
	    child1_0Input.setAttribute("bompath", "/");
	    
	    child1_1Input = docInput.createElement("checkinststatus");
	    child1_1Input.setAttribute("iscomplete", "TRUE");
	    child1_1Input.setAttribute("bompath", "/");
	    
	    child1_2Input = docInput.createElement("checkbomquantity");
	    child1_2Input.setAttribute("quantity", "1");
	    child1_2Input.setAttribute("operator", "=");
	    child1_2Input.setAttribute("bompath", "/ NV10023021020");
	    
	    child1_3Input = docInput.createElement("checkitemexist");
	    child1_3Input.setAttribute("value", "FALSE");
	    child1_3Input.setAttribute("bompath", "/ NV1004446");
	    
	    child1_4Input = docInput.createElement("command");
	    child1_4Input.setAttribute("action", "setvalue");
	    child1_4Input.setAttribute("name", "NETVIEWER_VERSION_P");
	    child1_4Input.setAttribute("value", "10.1");
	    child1_4Input.setAttribute("bompath", "/");
	    
	    rootInput.appendChild(child1_0Input);
	    rootInput.appendChild(child1_1Input);
	    rootInput.appendChild(child1_2Input);
	    rootInput.appendChild(child1_3Input);
	    rootInput.appendChild(child1_4Input);
	    
	    
	    Map<Element, Element> elMap = new HashMap<Element, Element>();
	    Map<Element, EObject> eObjectMap = new HashMap<Element, EObject>();
	    
	    elMap.put(child1_0, child1_0Input);
	    elMap.put(child1_1, child1_1Input);
	    elMap.put(child1_2, child1_2Input);
	    elMap.put(child1_3, child1_3Input);
	    elMap.put(child1_4, child1_4Input);
	    
	    
	    
	    
	   // vlp = (ViewLabelProvider) configScanView.getTreeViewer().getLabelProvider();
	    
	    // vlp = new ViewLabelProvider(elMap, eObjectMap);
	
	}

	@After
	public void tearDown() throws Exception {
		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().hideView(configScanView);
//		CheckComplete complete = CMLT.createCheckComplete();
//		complete.setComplete(value)
	}

	@Test
	public void getTooltipText() {
		
	}
	
	@Test
	public void getToolTipDisplayDelayTime() {
		
	}
	
	@Test
	public void getToolTipTimeDisplayed() {
		
	}
	
	@Test
	public void computeTooltipText() {
		
	}
	
	@Test
	public void getEObjectForUri() {
		
	}
	
//	@Test
//	public void testGetTextObject() {
//		vlp = new ViewLabelProvider();
//		assertEquals("", vlp.getText(child1_0));
//	}
//
//	@Test
//	public void testGetImageObject() {
//		System.out.println("Image is " + vlp.getImage(child1_0));
//	}

}
