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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class XmlLoader  {

	 
	
	public Document parseXml(String filename)  {
		DocumentBuilder xmlDocBuilder;
		Document doc = null;
		try {
			xmlDocBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		
			doc = xmlDocBuilder.parse(filename);
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}		
		
				
		return doc;
	}
		
		

	public Document parseXmlString(String xmlStr) {
		
		StringReader reader = new StringReader(xmlStr);
		org.xml.sax.InputSource src = new org.xml.sax.InputSource(reader);
		
		DocumentBuilder xmlDocBuilder;
		Document doc = null;
		try {
			xmlDocBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		
			doc = xmlDocBuilder.parse(src);
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}		
		
				
		return doc;
	}
	
	
	public String parseXmlToString(Document xml) {
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		try {		
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			DOMSource source = new DOMSource(xml.getDocumentElement());
			
			      
			bout = new ByteArrayOutputStream();
			OutputStreamWriter outstream = null;
			outstream = new OutputStreamWriter(bout);
			StreamResult out = new StreamResult(outstream);
			      
			     
			transformer.transform(source, out);
			
		} catch (TransformerException e) {
			e.printStackTrace();
		}
		return bout.toString();
	}
	
}