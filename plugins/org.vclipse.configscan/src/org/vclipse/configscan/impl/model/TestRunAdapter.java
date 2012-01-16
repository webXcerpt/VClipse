package org.vclipse.configscan.impl.model;

import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.progress.IDeferredWorkbenchAdapter;
import org.eclipse.ui.progress.IElementCollector;
import org.eclipse.xtext.ui.util.ResourceUtil;
import org.vclipse.configscan.ConfigScanImageHelper;
import org.vclipse.configscan.ConfigScanPlugin;
import org.vclipse.configscan.IConfigScanImages;
import org.vclipse.configscan.IConfigScanRemoteConnections.RemoteConnection;
import org.vclipse.configscan.IConfigScanReverseXmlTransformation;
import org.vclipse.configscan.IConfigScanRunner;
import org.vclipse.configscan.IConfigScanXMLProvider;
import org.vclipse.configscan.utils.DocumentUtility;
import org.vclipse.configscan.utils.TestCaseUtility;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.sap.conn.jco.JCoException;

public class TestRunAdapter implements IDeferredWorkbenchAdapter {

	public static final String SKIP_MATERIAL_TESTS = "SkipMaterialTests";
	
	@Inject
	private ConfigScanImageHelper imageHelper;
	
	@Inject
	private DocumentUtility documentUtility;
	
	@Inject
	private IConfigScanReverseXmlTransformation reverseXmlTransformation;
	
	@Inject
	private IConfigScanRunner runner;
	
	@Inject
	private TestCaseUtility testCaseUtility;
	
	private RemoteConnection connection;
	
	private EObject testModel;
	
	private IConfigScanXMLProvider xmlProvider;
	
	private TestCase testCase;
	
	private Map<String, Object> options;
	
	private Document inputDocument;
	
	private Document logDocument;
	
	public TestRunAdapter() {
		options = Maps.newHashMap();
	}
	
	public void setTestCase(TestCase testCase) {
		this.testCase = testCase;
	}
	
	public void setOptions(Map<String, Object> options) {
		if(options != null) {
			this.options = options;			
		}
	}
	
	public TestCase getTestCase() {
		return testCase;
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
	
	public Document getLogDocument() {
		return logDocument;
	}
	
	public Document getInputDocument() {
		return inputDocument;
	}
	
	public Object[] getChildren(Object object) {
		return testCase.getChildren().toArray();
	}

	public ImageDescriptor getImageDescriptor(Object object) {
		return imageHelper.getImageDescriptor(IConfigScanImages.TESTS);
	}

	public String getLabel(Object object) {
		return testCase.getTitle();
	}

	public Object getParent(Object object) {
		return testCase.getParent();
	}

	public void fetchDeferredChildren(Object object, IElementCollector collector, IProgressMonitor monitor) {
		if(testCase.getChildren().isEmpty()) {
			monitor.beginTask("Running tests for " + testModel.eResource().getURI().lastSegment() + " and " + connection.getDescription() + " connection", IProgressMonitor.UNKNOWN);

			Map<Element, URI> inputToUriMap = Maps.newHashMap();
			inputDocument = xmlProvider.transform(testModel, inputToUriMap);

			// log on disk -> asks the preference store if logging is required
			documentUtility.exportXmlToDisk(inputDocument);

			String parseResult = documentUtility.parse(inputDocument);
			String materialNumber = xmlProvider.getMaterialNumber(testModel);

			try {
				String result = runner.execute(parseResult, connection, materialNumber, ResourceUtil.getFile(testModel.eResource()));
				logDocument = documentUtility.parse(result);
				documentUtility.exportXmlToDisk(logDocument);
				Map<Element, Element> mapLogInput = reverseXmlTransformation.computeConfigScanMap(logDocument, inputDocument);
				Node nextSibling = logDocument.getDocumentElement().getFirstChild().getNextSibling();
				
				NodeList childNodes = nextSibling.getChildNodes();
				for(int i=0; i<childNodes.getLength(); i++) {
					Node item = childNodes.item(i);
					if(item.getNodeType() == Node.ELEMENT_NODE) { 
						TestCase childTestCase = 
								testCaseUtility.createTestCase((Element)item, testCase, inputToUriMap, mapLogInput);
						if(childTestCase == null) {
							continue;
						}
						testCase.addTestCase(childTestCase);
					}
				}
				
			} catch (JCoException exception) {
				ConfigScanPlugin.log(exception.getMessage(), IStatus.ERROR);
			} catch (CoreException exception) {
				ConfigScanPlugin.log(exception.getMessage(), IStatus.ERROR);
			}
		}

		for(TestCase childTestCase : testCase.getChildren()) {
			collector.add(childTestCase, monitor);
		}
		monitor.done();
	}
	
	public boolean isContainer() {
		return true;
	}

	public ISchedulingRule getRule(Object object) {
		// not used
		return null;
	}
}
