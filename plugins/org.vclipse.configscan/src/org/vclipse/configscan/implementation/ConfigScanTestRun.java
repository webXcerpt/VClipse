package org.vclipse.configscan.implementation;

import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.progress.IElementCollector;
import org.eclipse.xtext.ui.util.ResourceUtil;
import org.vclipse.configscan.ConfigScanImageHelper;
import org.vclipse.configscan.ConfigScanPlugin;
import org.vclipse.configscan.IConfigScanImages;
import org.vclipse.configscan.IConfigScanRemoteConnections.RemoteConnection;
import org.vclipse.configscan.IConfigScanReverseXmlTransformation;
import org.vclipse.configscan.IConfigScanRunner;
import org.vclipse.configscan.IConfigScanTestObject;
import org.vclipse.configscan.IConfigScanXMLProvider;
import org.vclipse.configscan.utils.DocumentUtility;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.sap.conn.jco.JCoException;

public final class ConfigScanTestRun implements IConfigScanTestObject {

	@Inject
	private ConfigScanImageHelper imageHelper;
	
	@Inject
	private DocumentUtility documentUtility;
	
	@Inject
	private IConfigScanReverseXmlTransformation reverseXmlTransformation;
	
	@Inject
	private IConfigScanRunner runner;
	
	private RemoteConnection connection;
	
	private EObject testModel;
	
	private IConfigScanXMLProvider xmlProvider;
	
	private List<IConfigScanTestObject> testCases;
	
	public ConfigScanTestRun() {
		testCases = Lists.newArrayList();
	}
	
	public void setTestModel(EObject testModel) {
		this.testModel = testModel;
	}
	
	public void setXmlProvider(IConfigScanXMLProvider provider) {
		this.xmlProvider = provider;
	}
	
	public void setConnection(RemoteConnection connection) {
		this.connection = connection;
	}

	public EObject getTestModel() {
		return testModel;
	}
	
	public Object[] getChildren(Object parent) {
		return testCases.toArray();
	}

	public ImageDescriptor getImageDescriptor(Object object) {
		return imageHelper.getImageDescriptor(IConfigScanImages.TESTS);
	}
	
	public String getLabel(Object object) {
		return testModel.eResource().getURI().lastSegment() + " on " +  connection.getDescription();
	}

	public Object getParent(Object object) {
		return null;
	}

	public void fetchDeferredChildren(Object object, IElementCollector collector, IProgressMonitor monitor) {
		// this is computed each time the tree is new constructed, we should probably check if xmlLogDocument is null
		// if it is not null, do not run the whole code beneath
		testCases = Lists.newArrayList();
		monitor.beginTask("Running tests for " + testModel.eResource().getURI().lastSegment() + " and " + connection.getDescription() + " connection", IProgressMonitor.UNKNOWN);
		
		Map<Element, URI> inputToUriMap = Maps.newHashMap();
		Document xmlInputDocument = xmlProvider.transform(testModel, inputToUriMap);
		
		// log on disk -> asks the preference store if logging is required
		documentUtility.exportXmlToDisk(xmlInputDocument);
		
		String parseResult = documentUtility.parse(xmlInputDocument);
		String materialNumber = xmlProvider.getMaterialNumber(testModel);
		
		try {
			String result = runner.execute(parseResult, connection, materialNumber, ResourceUtil.getFile(testModel.eResource()));
			Document xmlLogDocument = documentUtility.parse(result);
			documentUtility.exportXmlToDisk(xmlLogDocument);
			Map<Element, Element> mapLogInput = reverseXmlTransformation.computeConfigScanMap(xmlLogDocument, xmlInputDocument);
			Node nextSibling = xmlLogDocument.getDocumentElement().getFirstChild().getNextSibling();
			NodeList childNodes = nextSibling.getChildNodes();
			for(int i=0; i<childNodes.getLength(); i++) {
				Node item = childNodes.item(i);
				if((item.getNodeType() == Node.ELEMENT_NODE)) { 
					Element logElement = (Element)item;
					if(documentUtility.passesFilter(logElement)) {
						ConfigScanTestCase testCase = new ConfigScanTestCase(this, inputToUriMap, mapLogInput);
						testCase.setLogElement(logElement);
						Element inputElement = mapLogInput.get(logElement);
						testCase.setInputElement(inputElement);
						testCase.setTestStatementUri(inputToUriMap.get(inputElement));
						testCase.setImageHelper(imageHelper);
						testCase.setDocumentUtility(documentUtility);
						testCases.add(testCase);
					}
				}
			}
			for(IConfigScanTestObject testCase : testCases) {
				collector.add(testCase, monitor);
			}
		} catch (JCoException e) {
			ConfigScanPlugin.log(e.getMessage(), IStatus.ERROR);
		} catch (CoreException e) {
			ConfigScanPlugin.log(e.getMessage(), IStatus.ERROR);
		}
		monitor.done();
	}
	
	public boolean isContainer() {
		return true;
	}

	public ISchedulingRule getRule(Object object) {
		return null;
	}
}
