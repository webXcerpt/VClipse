package org.vclipse.configscan.utils;

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
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class DocumentUtility {

	public static final String LOG_SESSION = "log_session";
	
	public static final String LOG_TEST_GRP = "log_testgrp";
	
	public static final String NODE_NAME_LOG_MSG = "log_msg";
	
	public static final String LOG_HEADER = "log_header";
	
	public static final String LOG_UNDOTG = "log_undotg";
	
	public static final String END_SESSION = "End session";
	
	public static final String TITLE = "title";
	
	public static final String ATTRIBUTE_STATUS = "status";
	
	public static final String ATTRIBUTE_LEVEL = "level";
	
	public static final String ATTRIBUTE_TITLE = "title";
	
	public static final String ATTRIBUTE_CMD = "cmd";
	
	public static final String STATUS_SUCCESS = "S";
	
	//@Inject
	private DocumentBuilder documentBuilder;

	public static final String ATTRIBUTE_VALUE_E = "E";

	public static final String ATTRIBUTE_VALUE_ONE = "1";
	
	public DocumentUtility() {
		try {
			documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public boolean isSuccess(Element element) {
		return STATUS_SUCCESS.equals(element.getAttribute(ATTRIBUTE_STATUS));
	}
	
	public Document parse(String xmlString) {
		try {
			return documentBuilder.parse(new org.xml.sax.InputSource(new StringReader(xmlString)));
		} catch (SAXException e) {
			return documentBuilder.newDocument();
		} catch (IOException e) {
			return documentBuilder.newDocument();
		}
	}
	
	public String parse(Document document) {
		try {		
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.transform(new DOMSource(document.getDocumentElement()), 
					new StreamResult(new OutputStreamWriter(outputStream)));			
			return outputStream.toString();
		} catch (TransformerException e) {
			return null;
		}
	}

	public int getNumberOfFailure(Document document) {
		int failure = 0;
		NodeList list = document.getElementsByTagName(NODE_NAME_LOG_MSG);
		for(int i = 0; i<list.getLength(); i++) {
			Node node = list.item(i);
			if(node instanceof Element) {
				if(!isSuccess((Element)node)) {
					failure++;
				}
			}
		}
		return failure;
	}
	
	public int getNumberOfRuns(Document document) {
		return document.getElementsByTagName(NODE_NAME_LOG_MSG).getLength();
	}
	
	public int getNumberOfSuccess(Document document) {
		int success = 0;
		NodeList list = document.getElementsByTagName(NODE_NAME_LOG_MSG);
		for(int i = 0; i<list.getLength(); i++) {
			Node node = list.item(i);
			if(node instanceof Element) {
				if(isSuccess((Element) node)) {
					success++;
				}
			}
		}
		return success;
	}
}
