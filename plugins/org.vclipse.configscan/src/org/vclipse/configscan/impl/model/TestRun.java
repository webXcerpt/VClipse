package org.vclipse.configscan.impl.model;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.progress.IDeferredWorkbenchAdapter;
import org.eclipse.ui.progress.IElementCollector;
import org.eclipse.xtext.ui.util.ResourceUtil;
import org.eclipse.xtext.util.Files;
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
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ProcessingInstruction;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.inject.Inject;
import com.sap.conn.jco.JCoException;

public class TestRun extends TestGroup implements IDeferredWorkbenchAdapter {

	// names for options
	public static final String SKIP_MATERIAL_TESTS = "SkipMaterialTests";
	
	// parameter options
	public static final String KBOBJECT = "IV_IPC_KBID";
	public static final String STOP_ON_ERROR = "IV_STOP_ON_ERROR";
	public static final String PERFORMANCE_RUN = "IV_PERFROMANCE_RUN"; // NOTE: the typo in the string is intentional
	public static final String BREAKPOINT_ENABLED = "IV_BREAKPOINT_ENABLED";
	public static final String TEST_DATE = "IV_TEST_DATE";
	public static final String ROOT_QUANTITY = "IV_ROOT_QTY";
	
	public static final String LOG_FILES_LOCATION = "LogFiles";
	public static final String LOGGING_ENABLED = "loggingEnabled";
	
	public static final Set<String> parameterOptions = Sets.newHashSet(KBOBJECT, STOP_ON_ERROR, PERFORMANCE_RUN, BREAKPOINT_ENABLED, TEST_DATE, ROOT_QUANTITY);
	
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
	
	private Map<Object, Object> options;
	
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
	
	public void setOptions(Map<Object, Object> options) {
		this.options = options;
	}
	
	public Map<Object, Object> getOptions() {
		return Collections.unmodifiableMap(options);
	}

	public EObject getTestModel() {
		return testModel;
	}

	@Override
	public Object[] getChildren(Object o) {
		if(getTestCases().isEmpty()) {
			addTestCase(testCaseFactory.buildTestCase(this));			
		}
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
			if(testModel == null) {
				throw new IllegalArgumentException("Testmodel can not be null.");
			}
			monitor.beginTask("Running tests for " + testModel.eResource().getURI().lastSegment() + " and " + connection.getDescription() + " connection", IProgressMonitor.UNKNOWN);
			
			Map<Element, URI> input2Uri = Maps.newHashMap();
			Map<Object, Object> transformationOptions = Maps.newHashMap();
			transformationOptions.put(TestRun.SKIP_MATERIAL_TESTS, options.get(TestRun.SKIP_MATERIAL_TESTS));
			
			String materialNumber = "";
			Document inputDocument = null;
			if(xmlProvider != null) {
				inputDocument = xmlProvider.transform(testModel, filter, input2Uri, transformationOptions);
				materialNumber = xmlProvider.getMaterialNumber(testModel);
				setInputElement(inputDocument);		
			} else {
				inputDocument = (Document)getInputElement();
				NodeList childNodes = inputDocument.getFirstChild().getChildNodes();
				for(int i=0; i<childNodes.getLength(); i++) {
					Node item = childNodes.item(i);
					if(item != null && item.getNodeType() == Node.PROCESSING_INSTRUCTION_NODE) {
						ProcessingInstruction pi = (ProcessingInstruction)item;
						String data = pi.getData();
						if(data.startsWith("materialid")) {
							materialNumber = data.replace("materialid", "").trim();
						}
					}
				}
			}
			if(monitor.isCanceled()) {
				monitor.done();
				return;
			}
			String parseResult = documentUtility.serialize(inputDocument);
			try {
				if(monitor.isCanceled()) {
					monitor.done();
					return;
				}
				Map<Object, Object> configScanRunnerOptions = Maps.newHashMap();
				for (Entry<Object, Object> entry : options.entrySet()) {
					if (parameterOptions.contains(entry.getKey())) {
						configScanRunnerOptions.put(entry.getKey(), entry.getValue());
					}
				}
				Document logDocument = documentUtility.parse(configScanRunner.execute(parseResult, connection, materialNumber, configScanRunnerOptions));
				setLogElement(logDocument);
				
				String logFilesLocation = (String)options.get(LOG_FILES_LOCATION);
				if(logFilesLocation != null) {
					if(logFilesLocation.isEmpty()) {
						logFilesLocation = LOG_FILES_LOCATION;
					}
					IFile file = ResourceUtil.getFile(testModel.eResource());
					String name = file.getName() + "." + connection.getDescription() + ".";
					IFolder folder = file.getParent().getFolder(new Path(logFilesLocation));
					if(!folder.exists()) {
						folder.create(true, true, monitor);						
					}
					Files.writeStringIntoFile(folder.getLocation().toString() + "/" + name + "input.xml", documentUtility.serialize(inputDocument));
					Files.writeStringIntoFile(folder.getLocation().toString() + "/" + name + "log.xml", documentUtility.serialize(logDocument));
					folder.refreshLocal(IResource.DEPTH_ONE, monitor);
				}
		
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

	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((testModel == null) ? 0 : testModel.hashCode());
		result = prime * result + ((title == null) ? 0 : title.hashCode());
		return result;
	}

	
	@Override
	public boolean equals(Object object) {
		if(this == object)  {
			return true;			
		} else if(!super.equals(object)) {
			return false;
		} else if(getClass() != object.getClass()) {
			return false;
		}
		TestRun other = (TestRun) object;
		if(testModel == null) {
			if(other.testModel != null) {
				return false;
			}
		} else if(!testModel.equals(other.testModel)) {
			return false;
		}
		if(title == null) {
			if(other.title != null) {
				return false;
			}
		} else if(!title.equals(other.title)) {
			return false;
		}
		return true;
	}
}
