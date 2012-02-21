/*******************************************************************************
 * Copyright (c) 2012 webXcerpt Software GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    webXcerpt Software GmbH - initial creator
 ******************************************************************************/
package org.vclipse.configscan.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.vclipse.configscan.IConfigScanReverseXmlTransformation;
import org.vclipse.configscan.IConfigScanXMLProvider;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.traversal.DocumentTraversal;
import org.w3c.dom.traversal.NodeFilter;
import org.w3c.dom.traversal.TreeWalker;

import com.google.common.collect.Lists;

/**
 * Default implementation for the {@link IConfigScanXMLProvider} interface.
 * 
 */
public class ConfigScanReverseXmlTransformation implements IConfigScanReverseXmlTransformation {

	/** This method creates the mapping between xmlLog and xmlInput. The key is xmlLog.
	 * 
	 */
	public HashMap<Element, Element> computeConfigScanMap(Document xmlLog, Document xmlInput) {
		HashMap<Element, Element> configScanMap = new HashMap<Element, Element>();		// map which we fill and return

		Element rootLog = xmlLog.getDocumentElement();
		Element rootInput = xmlInput.getDocumentElement();
		
		if (rootLog!=null && rootInput!=null) { // document element is null if ConfigScan did not deliver a result, i.e., when the product does not exist on the respective system
			ArrayList<Element> nodeListLog = Lists.newArrayList();					// Node-List with nodes with wrong mapping (e.g. "CHECK ITEM EXIST")
			ArrayList<Element> nodeListInput = Lists.newArrayList();				// Node-List with nodes with wrong mapping (e.g. checkitemexist)

			Element logSession = findString(rootLog, "log_session");
			Element inputSession = findString(rootInput, "session");
			if(inputSession == null) {
				inputSession = findString(rootInput, "testCase");
			}
			transformNode(logSession, inputSession, configScanMap, nodeListInput, nodeListLog);

			Collections.sort(nodeListLog, new BompathComparator());
			Collections.sort(nodeListInput, new BompathComparator());

			for(int i = 0; i < nodeListLog.size() && i < nodeListInput.size(); i++) {
				Element key = nodeListLog.get(i);
				Element value = nodeListInput.get(i);
				configScanMap.put(key, value);
			}
		}
		
		return configScanMap;
	}




	
	private Element findString(Node root, String search) {
	    Document document = root.getOwnerDocument();
		DocumentTraversal traversable = (DocumentTraversal) document;
	    TreeWalker walker = traversable.createTreeWalker(document.getDocumentElement(), NodeFilter.SHOW_ELEMENT, null /* new ExampleFilter() */, true);
	    
	    Element result = processWalker(walker, search);
	    
	    return result;
	}
	
	private Element processWalker(TreeWalker tw, String search) {
		Node n = tw.getCurrentNode();
		Element mem = null;
		for(Node child = tw.firstChild(); child != null; child = tw.nextSibling()) {
			Element el = (Element) child;
			if((search.equalsIgnoreCase(el.getTagName()))) {
				return el;
			}
			mem = processWalker(tw, search);
			if(mem != null)
				return mem;
		}
		tw.setCurrentNode(n);
		return null;
	}
	
	private void processWalkerAssignTree(TreeWalker twInput, TreeWalker twLog, HashMap<Element, Element> configScanMap, ArrayList<Element> nodeListInput, ArrayList<Element> nodeListLog) {
		Node n1 = twLog.getCurrentNode();
		Node n2 = twInput.getCurrentNode();
		for(Node child1 = twLog.firstChild(), child2 = twInput.firstChild(); child1 != null; child1 = twLog.nextSibling(), child2 = twInput.nextSibling()) {
			if(child1 != null) {
				Element el1Log = (Element) child1;
				Element el2Input = (Element) child2;
				boolean skip = false;
				if(el2Input != null && "checkitemexist".equalsIgnoreCase(el2Input.getTagName())) {
					nodeListInput.add(el2Input);
					skip = true;
				}
				if(el1Log != null && "CHECK ITEM EXIST".equalsIgnoreCase(el1Log.getAttribute("cmd"))) {
					nodeListLog.add(el1Log);
					skip = true;
				}
				if(!skip) {
					configScanMap.put(el1Log, el2Input);
				}
			}   
			processWalkerAssignTree(twInput, twLog, configScanMap, nodeListInput, nodeListLog);
		}
		twLog.setCurrentNode(n1);
		twInput.setCurrentNode(n2);
	}
	
	
	
	/** assignment of log-nodes to input-nodes
	 * 
	 * @param log: the log_session-Element in XMLLog
	 * @param input: the input session-Element in XMLInput 
	 * @param configScanMap: mapping which is created
	 * @param nodeListInput: nodes with wrong mapping
	 * @param nodeListLog: nodes with wrong mapping
	 */
	private void transformNode(Element log, Element input,
			HashMap<Element, Element> configScanMap,
			ArrayList<Element> nodeListInput, ArrayList<Element> nodeListLog) {
		if(log == null || input == null /* || !log.hasChildNodes() || !input.hasChildNodes() */) {
			return;
		}
		configScanMap.put(log, input);
		Document docInput = input.getOwnerDocument();
		Document docLog = log.getOwnerDocument();
		DocumentTraversal traversableInput = (DocumentTraversal) docInput;
		DocumentTraversal traversableLog = (DocumentTraversal) docLog;
	    TreeWalker walkerInput = traversableInput.createTreeWalker(input, NodeFilter.SHOW_ELEMENT, null, true);
	    TreeWalker walkerLog = traversableLog.createTreeWalker(log, NodeFilter.SHOW_ELEMENT, null, true);
		processWalkerAssignTree(walkerInput, walkerLog, configScanMap, nodeListInput, nodeListLog);
	}

	public HashMap<Element, Element> computeConfigScanMap2(Document xmlLog, Document xmlInput) {
		// TODO: forgotten elements?
		Map<Node, NodeList> logMap = new HashMap<Node, NodeList>();		// TestGroups mapped to children
		Map<Node, NodeList> inputMap = new HashMap<Node, NodeList>();	// TestGroups mapped to children
		
		NodeList listGroupsLog = xmlLog.getElementsByTagName("log_testgrp");
		System.out.println("we have " + listGroupsLog.getLength() + " groups in log");
		for(int i = 0; i < listGroupsLog.getLength(); i++) {			// fill logMap
			Node parentLog = listGroupsLog.item(i) ;
			if(parentLog.hasChildNodes()) {
				NodeList children = parentLog.getChildNodes();
				logMap.put(parentLog, children);
			}
			else {
				// nothing to do here
			}
		}
		
		NodeList listGroupsInput = xmlInput.getElementsByTagName("testgroup");
		System.out.println("we have " + listGroupsInput.getLength() + " groups in input");
		for(int i = 0; i < listGroupsInput.getLength(); i++) {			// fill inputMap
			Node parentInput = listGroupsInput.item(i) ;
			if(parentInput.hasChildNodes()) {
				NodeList children = parentInput.getChildNodes();
				inputMap.put(parentInput, children);
			}
			else {
				// nothing to do here
			}
		}
		
		HashMap<Element, Element> elementMap = new HashMap<Element, Element>();		// map which we fill and return
		int count = 0;
		for(Node nodeLog : logMap.keySet()) {									// for each testgroup in log
			for(Node nodeInput : inputMap.keySet()) {							// for each testgroup in input
				Element elementLog = (Element) nodeLog;							// cast to Element
				String groupLog = elementLog.getAttribute("v2");				// group-name in log
				
				Element elementInput = (Element) nodeInput;						// cast to Element
				String groupInput = elementInput.getAttribute("id");			// group-name in input
				
				if(groupLog.equalsIgnoreCase(groupInput)) {						// we have found same testgroups
					NodeList nodesLog = logMap.get(nodeLog);					// get NodeList for testgroup
					NodeList nodesInput = inputMap.get(nodeInput);				// get NodeList for testgroup
					
					
					// mapping between elements
					for(int i = 0; i < nodesLog.getLength(); i++) {
						Node logNode = nodesLog.item(i);
						
						
						
						
						if(logNode instanceof Element) {			// !!! (no text-nodes)
						
							Element log = (Element) logNode;
							String cmd = log.getAttribute("cmd");
							
							for(int j = 0; j < nodesInput.getLength(); j++) {
								Node inputNode = nodesInput.item(j);
								if(inputNode instanceof Element) {
									Element input = (Element) inputNode;
									
									if(cmd.equals("CHECK ITEM STATUS")) {
										if("CONSISTENT".equals(log.getAttribute("v1")) && input.getTagName().equals("checkinststatus")) {
											if(input.getAttribute("isconsistent") != null) {
												elementMap.put(log, input);
											
												break;
											}
										}
										else if(log.getAttribute("v1").equals("COMPLETE") && input.getTagName().equals("checkinststatus")) {
											if(input.getAttribute("iscomplete") != null) {
												elementMap.put(log, input);
											
												break;
											}
										}

									}
									else if(cmd.equals("CHECK ITEM QUANTITY")) {
										if(input.getTagName().equals("checkbomquantity") && log.getAttribute("bompath").equals(input.getAttribute("bompath"))) {
											if(input.getAttribute("isconsistent") != null) {
												elementMap.put(log, input);
											
												break;
											}
										}
									}
									else if(cmd.equals("CHECK ITEM EXIST")) {
										if(input.getTagName().equals("checkitemexist") && log.getAttribute("bompath").equals(input.getAttribute("bompath"))) {
											elementMap.put(log, input);
											
											break;
										}
										else {
										}
									}
									else if(cmd.equals("SET CSTIC VALUE")) {
										
										if(input.getTagName().equals("command") && input.getAttribute("action").equals("unsetallvalues") && log.getAttribute("bompath").equals(input.getAttribute("bompath"))) {
											elementMap.put(log, input);
											
											break;
										}
										else if(input.getTagName().equals("command") && (input.getAttribute("action").equals("setvalue")) && log.getAttribute("cstic").equals(input.getAttribute("name")) && log.getAttribute("bompath").equals(input.getAttribute("bompath"))) {
											elementMap.put(log, input);
											
											break;
										}
									}
									else if(cmd.equals("CHECK CSTIC STATUS")) {
										 if(input.getAttribute("name").equals(log.getAttribute("cstic"))) {
											 
											elementMap.put(log, input);
												
											break;
											 
										 }
									}
									else if(cmd.equals("DEL CSTIC VALUE")) {
										if(input.getTagName().equals("command") && input.getAttribute("action").equals("unsetvalue") && log.getAttribute("cstic").equals(input.getAttribute("name")) && log.getAttribute("bompath").equals(input.getAttribute("bompath"))) {
											elementMap.put(log, input);
											
											break;
										}
									}
									else if(cmd.equals("RESET ALL CONFIG")) {
										if(input.getTagName().equals("command") && input.getAttribute("action").equals("resetallconfig")) {
											elementMap.put(log, input);
											
											break;
										}
									}
									else if(cmd.equals("CHECK SINGLE VALUE")) {
										if(input.getTagName().equals("checksinglevalue") && input.getAttribute("name").equals(log.getAttribute("cstic")) && log.getAttribute("bompath").equals(input.getAttribute("bompath"))) {
											elementMap.put(log, input);
											
											break;
										}
									}
									
								}
								else {
									
								}
							}
							if(!"".equals(cmd) && elementMap.get(log) == null) {				// no mapping found
								NamedNodeMap nnm = log.getAttributes();
								String tooltip = "";
								for(int k = 0; k < nnm.getLength(); k++) {
									Node node = nnm.item(k);
									tooltip += " " + node.getNodeName() + "=\"" + node.getNodeValue() + "\"";
								}
								System.err.println("not found " + cmd + " " + count++ + " " + tooltip);
							}
						} 
						else {
						}
							
					}
						
						
				}
				
			
			}
		}
		return elementMap;
	}
}
