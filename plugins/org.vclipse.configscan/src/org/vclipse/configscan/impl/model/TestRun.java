package org.vclipse.configscan.impl.model;

import java.util.Collections;
import java.util.List;
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
import org.vclipse.configscan.ITestObjectFilter;
import org.vclipse.configscan.utils.DocumentUtility;
import org.vclipse.configscan.utils.TestCaseFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.sap.conn.jco.JCoException;

public class TestRun extends TestGroup implements IDeferredWorkbenchAdapter {

	// names for options
	public static final String SKIP_MATERIAL_TESTS = "SkipMaterialTests";
	public static final String KBOBJECT = "kbobject";
	public static final String RTV = "rtv";
	
	@Inject
	private ConfigScanImageHelper imageHelper;
	
	@Inject
	private DocumentUtility documentUtility;
	
	@Inject
	private IConfigScanReverseXmlTransformation reverseXmlTransformation;
	
	@Inject
	private IConfigScanRunner configScanRunner;
	
	@Inject
	private ITestObjectFilter filter;
	
	@Inject
	private TestCaseFactory testCaseFactory;
	
	private RemoteConnection connection;
	
	private EObject testModel;
	
	private IConfigScanXMLProvider xmlProvider;
	
	private Map<String, Object> options;
	
	private String title;

	public TestRun() {
		super(null);
		options = Maps.newHashMap();
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	@Override
	public String getTitle() {
		return title;
	}

	public void setTestModel(EObject testModel) {
		this.testModel = testModel;
	}

	public void setXmlProvider(IConfigScanXMLProvider xmlProvider) {
		this.xmlProvider = xmlProvider;
	}
	
	public void setRemoteConnection(RemoteConnection remoteConnection) {
		this.connection = remoteConnection;
	}
	
	public void setFilter(ITestObjectFilter filter) {
		this.filter = filter;
	}
	
	public void setOptions(Map<String, Object> options) {
		this.options = options;
	}
	
	public Map<String, Object> getOptions() {
		return Collections.unmodifiableMap(options);
	}

	public EObject getTestModel() {
		return testModel;
	}

	@Override
	public Object[] getChildren(Object o) {
		addTestCase(testCaseFactory.buildTestCase(this));
		return getTestCases().toArray();
	}

	@Override
	public ImageDescriptor getImageDescriptor(Object object) {
		return imageHelper.getImageDescriptor(IConfigScanImages.TESTS);
	}

	@Override
	public String getLabel(Object object) {
		return title;
	}

	@Override
	public Object getParent(Object object) {
		return null;
	}

	@Override
	public void fetchDeferredChildren(Object object, IElementCollector collector, IProgressMonitor monitor) {
		List<TestCase> testCases = getTestCases();
		if(testCases.isEmpty()) {
			monitor.beginTask("Running tests for " + testModel.eResource().getURI().lastSegment() + " and " + connection.getDescription() + " connection", IProgressMonitor.UNKNOWN);
			
			Map<Element, URI> input2Uri = Maps.newHashMap();
			Document inputDocument = xmlProvider.transform(testModel, filter, input2Uri);
			setInputElement(inputDocument);
			
			if(monitor.isCanceled()) {
				monitor.done();
				return;
			}
			
			String parseResult = documentUtility.parse(inputDocument);
			String materialNumber = xmlProvider.getMaterialNumber(testModel);
			
			try {
				if(monitor.isCanceled()) {
					monitor.done();
					return;
				}
				Document logDocument = documentUtility.parse(configScanRunner.execute(parseResult, connection, 
						materialNumber, ResourceUtil.getFile(testModel.eResource())));
				setLogElement(logDocument);
				
				if(monitor.isCanceled()) {
					monitor.done();
					return;
				}
				
				testCaseFactory.setOptions(options);
				Map<Element, Element> log2Input = reverseXmlTransformation.computeConfigScanMap(logDocument, inputDocument);
				testCaseFactory.setInputUriMap(input2Uri);
				testCaseFactory.setLogInputMap(log2Input);
				addTestCase(testCaseFactory.buildTestCase(logDocument, this));					
			} catch (JCoException exception) {
				ConfigScanPlugin.log(exception.getMessage(), IStatus.ERROR);
			} catch (CoreException exception) {
				ConfigScanPlugin.log(exception.getMessage(), IStatus.ERROR);
			}
		}
		if(monitor.isCanceled()) {
			monitor.done();
			return;
		}
		for(TestCase childTestCase : getTestCases()) {
			collector.add(childTestCase, monitor);
		}
		monitor.done();
	}

	@Override
	public boolean isContainer() {
		return true;
	}

	@Override
	public ISchedulingRule getRule(Object object) {
		// not used
		return null;
	}
}
