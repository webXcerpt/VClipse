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

import java.util.List;

import javax.xml.parsers.DocumentBuilder;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.IViewSite;
import org.vclipse.configscan.utils.DocumentUtility;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.google.common.collect.Lists;
import com.google.inject.Inject;

/*
 * The content provider class is responsible for
 * providing objects to the view. It can wrap
 * existing objects in adapters or simply return
 * objects as-is. These objects may be sensitive
 * to the current input of the view, or ignore
 * it and always show the same content 
 * (like Task List, for example).
 */
class ContentProvider implements ITreeContentProvider {
	
	private static final String INVISIBLE_ROOT_NAME = "root";

	@Inject
	private DocumentBuilder documentBuilder;
	
	@Inject
	private DocumentUtility documentUtility;
	
	private Document document;
	
	private Node invisibleRoot;
	
	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		if(newInput instanceof Document) {
			invisibleRoot = ((Document)newInput).getDocumentElement().getFirstChild().getNextSibling(); // skip "log_result"
		} else if(newInput instanceof String) {
			document = documentUtility.parse((String)newInput);
			invisibleRoot = document.getDocumentElement().getFirstChild().getNextSibling(); // skip "log_result" 
		} else {
			document = documentBuilder.newDocument();
			invisibleRoot = document.createElement(INVISIBLE_ROOT_NAME);
		}
	}

	public Document getInput() {
		return document;
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
		if(child instanceof Node) {
			return ((Node)child).getParentNode();
		}
		return null;
	}

	public Object [] getChildren(Object parent) {
		List<Object> children = Lists.newArrayList();
		if(parent instanceof Node) {
			NodeList nodes = ((Node)parent).getChildNodes();
			for(int i = 0; i<nodes.getLength(); i++) {
				Node item = nodes.item(i);
				if((item.getNodeType() == Node.ELEMENT_NODE)) { 	// throw Nodes out which have other type than ELEMENT_NODE
					children.add(item);
				}
			}
		}
		return children.toArray();
	}

	public boolean hasChildren(Object parent) {
		if(parent instanceof Node) {
			NodeList nodes = ((Node)parent).getChildNodes();
			for(int i = 0; i<nodes.getLength(); i++) {
				if((nodes.item(i).getNodeType() == Node.ELEMENT_NODE)) { 	// throw Nodes out which have other type than ELEMENT_NODE
					return true;
				}
			}
		}
		return false;
	}
	
	public void dispose() {
		
	}
}