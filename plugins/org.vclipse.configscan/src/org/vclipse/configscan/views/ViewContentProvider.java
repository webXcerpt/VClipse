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
package org.vclipse.configscan.views;

import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.IViewSite;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;



/*
 * The content provider class is responsible for
 * providing objects to the view. It can wrap
 * existing objects in adapters or simply return
 * objects as-is. These objects may be sensitive
 * to the current input of the view, or ignore
 * it and always show the same content 
 * (like Task List, for example).
 */
class ViewContentProvider implements ITreeContentProvider {
		/**
		 * 
		 */
		private Node invisibleRoot;
		private Document doc;
		
		/** This is only used at initialization
		 * 
		 */
		ViewContentProvider() {
			
			DocumentBuilder xmlDocBuilder = null;
			Document doc = null;
			
			try {
				xmlDocBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			} catch (ParserConfigurationException e) {
				e.printStackTrace();
			}
			doc = xmlDocBuilder.newDocument();
			
			
			invisibleRoot = doc.createElement("root");
		}
		
		/** This is used if you import a xml file
		 * 
		 */
		ViewContentProvider(String xmlContent) {
			doc = new XmlLoader().parseXmlString(xmlContent);
			invisibleRoot = doc.getDocumentElement().getFirstChild().getNextSibling();		// skip "log_result"
		}
		
		/** This is used if you get a remote xml file.
		 * 
		 * @param xmlLogDoc
		 * @param xmlContent
		 */
		ViewContentProvider(Document xmlLogDoc) {
			doc = xmlLogDoc;
			invisibleRoot = doc.getDocumentElement().getFirstChild().getNextSibling();		// skip "log_result"
		}
		
		public void dispose() {
		}
		
		public Object[] getElements(Object parent) {
			if (parent instanceof IViewSite) {
				if (invisibleRoot==null) {
					throw new NullPointerException("root-element is null");
				}
				return getChildren(invisibleRoot);
			}
			return getChildren(parent);
		}
		
		public Object getParent(Object child) {
			if (child instanceof Node) {
				return ((Node)child).getParentNode();
			}
			return null;
		}
		
		public Object [] getChildren(Object parent) {
			ArrayList<Object> ret = new ArrayList<Object>();
			if (parent instanceof Node) {
				NodeList nodes = ((Node)parent).getChildNodes();
				for(int i = 0; i < nodes.getLength(); i++) {
					Node item = nodes.item(i);
					if((item.getNodeType() == Node.ELEMENT_NODE)) { 	// throw Nodes out which have other type than ELEMENT_NODE
						ret.add(item);
					}
				}
			}
			return ret.toArray();
		}
		
		public boolean hasChildren(Object parent) {
			if (parent instanceof Node) {
				NodeList nodes = ((Node)parent).getChildNodes();
				for(int i = 0; i < nodes.getLength(); i++) {
					Node item = nodes.item(i);
					if((item.getNodeType() == Node.ELEMENT_NODE)) { 	// throw Nodes out which have other type than ELEMENT_NODE
						return true;
					}
				}
			}
			return false;
		}


		@Override
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			viewer.refresh();
		}
		

		public int getNumberOfSuccess() {
			int success = 0;
			NodeList list = doc.getElementsByTagName("log_msg");
			
			for(int i = 0; i < list.getLength(); i++) {
				Node node = list.item(i);
				if(node instanceof Element) {
					Element el = (Element) node;
					if(Util.isSuccess(el)) {
						success++;
					}
				}
			}
			return success;
		}
		
		public int getNumberOfFailure() {
			int failure = 0;
			NodeList list = doc.getElementsByTagName("log_msg");
			
			for(int i = 0; i < list.getLength(); i++) {
				Node node = list.item(i);
				if(node instanceof Element) {
					Element el = (Element) node;
					if(!Util.isSuccess(el)) {
						failure++;
					}
				}
			}
			return failure;
		}
		
		public int getNumberOfRuns() {
			NodeList list = doc.getElementsByTagName("log_msg");
			return list.getLength();
		}
			
		
		public Document getLogDocument() {
			return doc;
		}
		
	}