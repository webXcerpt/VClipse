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
import org.vclipse.configscan.impl.model.TestRun;
import org.vclipse.configscan.utils.DocumentUtility;
import org.vclipse.configscan.utils.TestCaseFactory;
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

	private TestCaseFactory testCaseUtility;
	
	private LinkedList<ConfigScanViewInput> history;
	
	private int historyEntriesNumber;
	
	@Inject
	public TestRunsHistory(AbstractUIPlugin plugin, DocumentUtility utility, TestCaseFactory testCaseUtility) {
		history = Lists.newLinkedList();
		this.documentUtility = utility;
		this.plugin = plugin;
		this.preferenceStore = this.plugin.getPreferenceStore();
		this.historyEntriesNumber = this.preferenceStore.getInt(HISTORY_ENTRIES_NUMBER);
		preferenceStoreListener = new PreferenceStoreListener();
		this.preferenceStore.addPropertyChangeListener(preferenceStoreListener);
		this.testCaseUtility = testCaseUtility;
	}
	
	public void clear() {
		history.clear();
		
	}
	
	public List<ConfigScanViewInput> getHistory() {
		return Collections.unmodifiableList(history);
	}
	
	@Override
	protected void finalize() throws Throwable {
		preferenceStore.removePropertyChangeListener(preferenceStoreListener);
		super.finalize();
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
	
	public void save(String path) {
		if(path == null || path.isEmpty()) {
			ConfigScanPlugin.log("Can not save history, no path is provided.", IStatus.WARNING);
		} else {
			try {
				Document historyDocument = documentUtility.newDocument();
				Element logResults = historyDocument.createElement(DocumentUtility.LOG_RESULTS);
				historyDocument.appendChild(logResults);
				
				for(ConfigScanViewInput input : history) {
					Element inputElement = historyDocument.createElement(DocumentUtility.INPUT);
					inputElement.setAttribute(DocumentUtility.NAME, input.getConfigurationName());
					inputElement.setAttribute(DocumentUtility.DATE, input.getDate());
					logResults.appendChild(inputElement);
					
					for(TestRun testRun : input.getTestRuns()) {
						Node logResult = documentUtility.getLogResult((Document)testRun.getLogElement());
						if(logResult == null) {
							continue;
						}
						inputElement.appendChild(historyDocument.importNode(logResult, true));
					}
				}
				File historyFile = new Path(path).toFile();
				if(!historyFile.exists()) {
					historyFile.createNewFile();
				}
				BufferedWriter out = new BufferedWriter(new FileWriter(historyFile));
				out.write(documentUtility.parse(historyDocument));
				out.close();
			} catch (IOException exception) {
				ConfigScanPlugin.log(exception.getMessage(), IStatus.ERROR);
			}
		}
	}
	
	public void load(String path) {
		if(path == null || path.isEmpty()) {
			ConfigScanPlugin.log("Can not load history, no path is provided.", IStatus.WARNING);
		} else {
			try {
				File historyFile = new Path(path).toFile();
				if(historyFile.exists()) {
					Document document = documentUtility.parse(new FileInputStream(historyFile));
					if(document != null) {
						NodeList childNodes = document.getDocumentElement().getChildNodes();
						for(int i=0; i<childNodes.getLength(); i++) {
							Node item = childNodes.item(i);
							// handle input
							if(Node.ELEMENT_NODE == item.getNodeType() && DocumentUtility.INPUT.equals(item.getNodeName())) {
								NamedNodeMap attributes = item.getAttributes();
								String date = attributes.getNamedItem(DocumentUtility.DATE).getNodeValue();
								String name = attributes.getNamedItem(DocumentUtility.NAME).getNodeValue();
								TestRun testRun = testCaseUtility.buildTestRun("Testrun on " + date + " with " + name, null, null, null);
								testRun.setLogElement(document);
								ConfigScanViewInput input = new ConfigScanViewInput();
								input.setConfigurationName(name);
								input.setDate(date);
								input.setTestRuns(Lists.newArrayList(testRun));
								Node firstChild = item.getFirstChild();
								if(firstChild != null) {
									// handle log_result
									NodeList childNodes2 = firstChild.getNextSibling().getChildNodes();
									for(int k=0; k<childNodes2.getLength(); k++) {
										Node item2 = childNodes2.item(k);
										if(Node.ELEMENT_NODE == item2.getNodeType()) {
											testRun.addTestCase(testCaseUtility.buildTestCase((Element)item2, testRun));										
										}
									}
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
	}
}
