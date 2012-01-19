package org.vclipse.configscan.views;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.vclipse.configscan.ConfigScanPlugin;
import org.vclipse.configscan.IConfigScanConfiguration;
import org.vclipse.configscan.impl.model.TestCase;
import org.vclipse.configscan.impl.model.TestRunAdapter;
import org.vclipse.configscan.utils.DocumentUtility;
import org.vclipse.configscan.utils.TestCaseUtility;
import org.vclipse.configscan.views.JobAwareTreeViewer.ITreeViewerLockListener;
import org.vclipse.configscan.views.JobAwareTreeViewer.TreeViewerLockEvent;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.google.common.collect.Lists;
import com.google.inject.Inject;

public class TestRunsHistory implements IConfigScanConfiguration, ITreeViewerLockListener {

	private class PreferenceStoreListener implements IPropertyChangeListener {
		@Override
		public void propertyChange(PropertyChangeEvent event) {
			if(HISTORY_ENTRIES_NUMBER.equals(event.getProperty())) {
				historyEntriesNumber = preferenceStore.getInt(HISTORY_ENTRIES_NUMBER);				
			}
		}
	}
	
	private PreferenceStoreListener preferenceStoreListener;
	
	private final AbstractUIPlugin plugin;
	
	private final DocumentUtility documentUtility;
	
	private final IPreferenceStore preferenceStore;
	
	private final TestCaseUtility testCaseUtility;
	
	private LinkedList<ConfigScanViewInput> history;
	
	private int historyEntriesNumber;
	
	@Inject
	public TestRunsHistory(AbstractUIPlugin plugin, DocumentUtility utility, TestCaseUtility testCaseUtility) {
		history = Lists.newLinkedList();
		this.documentUtility = utility;
		this.plugin = plugin;
		this.preferenceStore = this.plugin.getPreferenceStore();
		this.historyEntriesNumber = this.preferenceStore.getInt(HISTORY_ENTRIES_NUMBER);
		preferenceStoreListener = new PreferenceStoreListener();
		this.preferenceStore.addPropertyChangeListener(preferenceStoreListener);
		this.testCaseUtility = testCaseUtility;
	}
	
	public List<ConfigScanViewInput> getHistory() {
		return Collections.unmodifiableList(history);
	}
	
	@Override
	protected void finalize() throws Throwable {
		this.preferenceStore.removePropertyChangeListener(preferenceStoreListener);
		super.finalize();
	}

	public void save() {
		try {
			Path outputPath = new Path(plugin.getStateLocation().append(IConfigScanConfiguration.HISTORY_FILE_NAME).toString());
			Document historyDocument = documentUtility.newDocument();
			
			Element logResults = historyDocument.createElement(DocumentUtility.LOG_RESULTS);
			historyDocument.appendChild(logResults);
			
			for(ConfigScanViewInput input : history) {
				Element inputElement = historyDocument.createElement(DocumentUtility.INPUT);
				inputElement.setAttribute(DocumentUtility.NAME, input.getConfigurationName());
				inputElement.setAttribute(DocumentUtility.DATE, input.getDate());
				logResults.appendChild(inputElement);
				for(TestCase testCase : input.getTestCases()) {
					Object adapter = testCase.getAdapter(TestRunAdapter.class);
					if(adapter != null) {
						TestRunAdapter testRun = (TestRunAdapter)adapter;
						Node logResult = documentUtility.getLogResult(testRun.getLogDocument());
						Node importedLogResult = historyDocument.importNode(logResult, true);
						inputElement.appendChild(importedLogResult);
					}
				}
			}
			String string = documentUtility.parse(historyDocument);
			System.out.println(string);
			File historyFile = outputPath.toFile();
			if(!historyFile.exists()) {
				historyFile.createNewFile();
			}
			BufferedWriter out = new BufferedWriter(new FileWriter(historyFile));
			out.write(string);
			out.close();
		} catch (IOException exception) {
			ConfigScanPlugin.log(exception.getMessage(), IStatus.ERROR);
		}
	}
	
	public void load() {
		Path outputPath = new Path(plugin.getStateLocation().append(IConfigScanConfiguration.HISTORY_FILE_NAME).toString());
		try {
			File historyFile = outputPath.toFile();
			if(historyFile.exists()) {
				FileInputStream fileInputStream = new FileInputStream(historyFile);
				Document document = documentUtility.parse(fileInputStream);
				if(document != null) {
					NodeList childNodes = document.getDocumentElement().getChildNodes();
					for(int i=0; i<childNodes.getLength(); i++) {
						Node item = childNodes.item(i);
						// handle input
						if(Node.ELEMENT_NODE == item.getNodeType() && DocumentUtility.INPUT.equals(item.getNodeName())) {
							NamedNodeMap attributes = item.getAttributes();
							String date = attributes.getNamedItem(DocumentUtility.DATE).getNodeValue();
							String name = attributes.getNamedItem(DocumentUtility.NAME).getNodeValue();
							
							ConfigScanViewInput input = new ConfigScanViewInput();
							input.setConfigurationName(name);
							input.setDate(date);
							
							List<TestCase> testCases = Lists.newArrayList();
							Node firstChild = item.getFirstChild();
							if(firstChild != null) {
								// handle log_result
								NodeList childNodes2 = firstChild.getNextSibling().getChildNodes();
								for(int k=0; k<childNodes2.getLength(); k++) {
									Node item2 = childNodes2.item(k);
									if(Node.ELEMENT_NODE == item2.getNodeType()) {
										Element element = (Element)item2;
										if(documentUtility.passesFilter(element)) {
											TestCase createTestCase = testCaseUtility.createTestCase(element, null);
											if(createTestCase != null) {
												testCases.add(createTestCase);										
											}
										}
									}
								}
								input.setTestCases(testCases);
								history.add(input);
							}
						}
					}
				}
			}
		} catch(IOException exception) {
			ConfigScanPlugin.log(exception.getMessage(), IStatus.ERROR);
		}
	}

	public void available(TreeViewerLockEvent event) {
		Object input = event.getViewer().getInput();
		if(input instanceof ConfigScanViewInput) {
			ConfigScanViewInput viewInput = (ConfigScanViewInput)input;
			if(!history.contains(viewInput)) {
				if(history.size() == historyEntriesNumber) {
					history.removeLast();
				}
				history.add(viewInput);				
			}
		}
	}

	public void locked(TreeViewerLockEvent event) {
		// not used
	}
}
