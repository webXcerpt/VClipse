package org.vclipse.configscan.utils;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.preference.IPreferenceStore;
import org.vclipse.configscan.ConfigScanPlugin;
import org.vclipse.configscan.views.IConfigScanConfiguration;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.google.inject.Inject;

public class DocumentUtility {

	public static final String LOG_RESULTS = "log_results";
	
	public static final String LOG_RESULT = "log_result";
	
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
	
	// do not use injection for document builder -> there is a NullPointerException
	// during fetching the deferred nodes in the tree viewer
	private DocumentBuilder documentBuilder;
	
	public static final String ATTRIBUTE_VALUE_E = "E";
	
	public static final String ATTRIBUTE_VALUE_ONE = "1";
	
	@Inject
	private IPreferenceStore preferenceStore;

	public DocumentUtility() {
		try {
			documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		} catch (ParserConfigurationException exception) {
			ConfigScanPlugin.log(exception.getMessage(), IStatus.ERROR);
		}
	}
	
	public boolean hasSuccessStatus(Element element) {
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

	public int getNumberOfFailure(Element element) {
		int failure = 0;
		NodeList list = element.getElementsByTagName(NODE_NAME_LOG_MSG);
		for(int i = 0; i<list.getLength(); i++) {
			Node node = list.item(i);
			if(node instanceof Element) {
				if(!hasSuccessStatus((Element)node)) {
					failure++;
				}
			}
		}
		return failure;
	}
	
	public int getNumberOfRuns(Element element) {
		return element.getElementsByTagName(NODE_NAME_LOG_MSG).getLength();
	}
	
	public int getNumberOfSuccess(Element element) {
		int success = 0;
		NodeList list = element.getElementsByTagName(NODE_NAME_LOG_MSG);
		for(int i = 0; i<list.getLength(); i++) {
			Node node = list.item(i);
			if(node instanceof Element) {
				if(hasSuccessStatus((Element) node)) {
					success++;
				}
			}
		}
		return success;
	}

	public void exportXmlToDisk(Document document) {
		if(preferenceStore.getBoolean(IConfigScanConfiguration.EXPORT_XML_INPUT_TO_DISK)) {
			SimpleDateFormat sdf = new SimpleDateFormat(IConfigScanConfiguration.DATE_FORMAT);
		    Calendar calendar = Calendar.getInstance(); // today
		    String today = sdf.format(calendar.getTime());
			String currentFilename = "XML_LOG_" + today + ".xml";
			String xmlLog = parse(document);
			try {
				BufferedWriter out = new BufferedWriter(new FileWriter(currentFilename));
				out.write(xmlLog);
				out.close();
			} catch (IOException exception) {
				ConfigScanPlugin.log(exception.getMessage(), IStatus.ERROR);
			}
		}
	}

	public boolean passesFilter(Element element) {
		// we do not want see following elements in the tree
		String name = element.getNodeName();
		String title = element.getAttribute(TITLE);
		if(LOG_RESULTS.equals(name) 
				|| LOG_HEADER.equals(name) 
					|| LOG_UNDOTG.equals(name) 
						|| END_SESSION.equals(name) 
							|| END_SESSION.equals(title)) {
			return false;
		}
		return true;
	}
}
