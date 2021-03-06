/*******************************************************************************
 * Copyright (c) 2010 - 2013 webXcerpt Software GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *  
 * Contributors:
 *     	webXcerpt Software GmbH - initial creator
 * 		www.webxcerpt.com
 ******************************************************************************/
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
import org.vclipse.base.ui.util.ClasspathAwareImageHelper;
import org.vclipse.configscan.ConfigScanPlugin;
import org.vclipse.configscan.IConfigScanImages;
import org.vclipse.configscan.IConfigScanRemoteConnections.RemoteConnection;
import org.vclipse.configscan.IConfigScanReverseXmlTransformation;
import org.vclipse.configscan.IConfigScanRunner;
import org.vclipse.configscan.IConfigScanXMLProvider;
import org.vclipse.configscan.ITestObjectFilter;
import org.vclipse.configscan.actions.ConfigScanUploadProcessingInstructionExtractor;
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
	public static final String ADD_STYLESHEET = "addStyleSheet";
	
	public static final Set<String> parameterOptions = Sets.newHashSet(KBOBJECT, STOP_ON_ERROR, PERFORMANCE_RUN, BREAKPOINT_ENABLED, TEST_DATE, ROOT_QUANTITY);
	
	@Inject
	private ClasspathAwareImageHelper imageHelper;
	
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
	
	private IFile file;

	public TestRun() {
		super(null);
		options = Maps.newHashMap();
	}
	
	public void setFile(IFile file) {
		this.file = file;
	}
	
	@Override
	public String getTitle() {
		if(file == null) {
			Object object = options.get("name");
			if(object instanceof String) {
				return (String)object;
			} 
			return "Log results";
		} 
		if(connection == null) {
			return file.getName();
		}
		return file.getName() + " on " + connection.getDescription();
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
		return getTitle();
	}

	@Override
	public Object getParent(Object object) {
		return null;
	}
	
	@Override
	public void fetchDeferredChildren(Object object, IElementCollector collector, IProgressMonitor monitor) {
		List<TestCase> testCases = getTestCases();
		if(testCases.isEmpty()) {
			String materialNumber = "";
			Document inputDocument = (Document)getInputElement();
			Map<Element, URI> input2Uri = Maps.newHashMap();
			if(testModel == null) {
				// xml input file or other file
				if(inputDocument == null) {
					// can occur if there is wrong context in the xml file -> load a ConfigScan log file, the content of the file is log-file like 
					throw new IllegalArgumentException("Require an input document for ConfigScan transformation");
				}
				monitor.beginTask("Running tests for " + inputDocument.getDocumentURI() + " and " + connection.getDescription() + " connection", IProgressMonitor.UNKNOWN);
				materialNumber = extractMaterialNumber(inputDocument);
			} else {
				monitor.beginTask("Running tests for " + testModel.eResource().getURI().lastSegment() + " and " + connection.getDescription() + " connection", IProgressMonitor.UNKNOWN);
				Map<Object, Object> transformationOptions = Maps.newHashMap();
				transformationOptions.put(TestRun.SKIP_MATERIAL_TESTS, options.get(TestRun.SKIP_MATERIAL_TESTS));
				transformationOptions.put(TestRun.ADD_STYLESHEET, options.get(TestRun.ADD_STYLESHEET));
				if(xmlProvider == null) {
					if(inputDocument == null) {
						throw new IllegalArgumentException("Require an input document for ConfigScan transformation");
					}
					materialNumber = extractMaterialNumber(inputDocument);
				} else {
					inputDocument = xmlProvider.transform(testModel, filter, input2Uri, transformationOptions);
					materialNumber = xmlProvider.getMaterialNumber(testModel);
					setInputElement(inputDocument);
				}
			}
			
			assert !materialNumber.isEmpty();
			assert inputDocument != null;
			
			String parseResult = documentUtility.serialize(inputDocument);
			try {
				String logFileNamePrefix = getLogFileNamePrefix();
				if(!logFileNamePrefix.isEmpty()) {
					storeLogDocument(monitor, inputDocument, logFileNamePrefix + ".input.xml");					
				}
				Map<Object, Object> configScanRunnerOptions = Maps.newHashMap();
				for (Entry<Object, Object> entry : options.entrySet()) {
					if (parameterOptions.contains(entry.getKey())) {
						configScanRunnerOptions.put(entry.getKey(), entry.getValue());
					}
				}
				
				String result = configScanRunner.execute(parseResult, connection, materialNumber, configScanRunnerOptions);
				Document logDocument = documentUtility.parse(result);
				setLogElement(logDocument);
				if(!logFileNamePrefix.isEmpty()) {
					storeLogDocument(monitor, logDocument, getLogFileNamePrefix() + ".log.xml");				
				}
				
				testCaseFactory.setOptions(options);
				testCaseFactory.setInputUriMap(input2Uri);
				testCaseFactory.setLogInputMap(reverseXmlTransformation.computeConfigScanMap(logDocument, inputDocument));
				addTestCase(testCaseFactory.buildTestCase(logDocument, this));					
			} catch (Exception exception) {
				ConfigScanPlugin.log(exception.getMessage(), IStatus.ERROR);
			}
		}

		for(TestCase childTestCase : getTestCases()) {
			collector.add(childTestCase, monitor);
		}
		monitor.done();
	}

	private String extractMaterialNumber(Node node) {
		if (node.getNodeType() == Node.PROCESSING_INSTRUCTION_NODE) {
			ProcessingInstruction pi = (ProcessingInstruction)node;
			if (ConfigScanUploadProcessingInstructionExtractor.CONFIGSCAN_UPLOAD.equals(pi.getTarget())) {
				return ConfigScanUploadProcessingInstructionExtractor.extract(ConfigScanUploadProcessingInstructionExtractor.MATNR, pi.getData(), null);
			}
		}
		NodeList childNodes = node.getChildNodes();
		for(int i=0; i<childNodes.getLength(); i++) {
			Node childNode = childNodes.item(i);
			String matNr = extractMaterialNumber(childNode);
			if (matNr!=null) {
				return matNr;
			}
		}
		return null;
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
		result = prime * result + ((getTitle() == null) ? 0 : getTitle().hashCode());
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
		if(getTitle() == null) {
			if(other.getTitle() != null) {
				return false;
			}
		} else if(!getTitle().equals(other.getTitle())) {
			return false;
		}
		return true;
	}
	
	private String getLogFileNamePrefix() {
		if(testModel != null) {
			return ResourceUtil.getFile(testModel.eResource()).getName() + "." + connection.getDescription();
		} 
		if(file != null) {
			return file.getName() + "." + connection.getDescription();
		} 
		return "";
	}
	
	private void storeLogDocument(IProgressMonitor monitor, Document document, String fileName) throws CoreException {
		if(testModel != null) {
			file = ResourceUtil.getFile(testModel.eResource());
		}
		if(file != null) {
			String logFilesLocation = (String)options.get(LOG_FILES_LOCATION);
			if(logFilesLocation != null) {
				if(logFilesLocation.isEmpty()) {
					logFilesLocation = LOG_FILES_LOCATION;
				}
				IFolder folder = file.getParent().getFolder(new Path(logFilesLocation));
				if(!folder.exists()) {
					folder.create(true, true, monitor);						
				}
				Files.writeStringIntoFile(folder.getLocation().toString() + "/" + fileName, documentUtility.serialize(document));
				folder.refreshLocal(IResource.DEPTH_ONE, monitor);
			}
		}
	}
}
