package org.vclipse.configscan.views;

import static org.junit.Assert.assertEquals;
import static org.vclipse.configscan.TestCaseAssert.testComplete;
import static org.vclipse.configscan.TestCaseAssert.testValues;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.jface.preference.IPreferenceStore;
import org.junit.Before;
import org.junit.Test;
import org.vclipse.configscan.ConfigScanPlugin;
import org.vclipse.configscan.IConfigScanConfiguration;
import org.vclipse.configscan.IConfigScanXMLProvider;
import org.vclipse.configscan.JUnitTestUtils;
import org.vclipse.configscan.impl.model.TestCase;
import org.vclipse.configscan.impl.model.TestGroup;
import org.vclipse.configscan.impl.model.TestRun;
import org.vclipse.configscan.injection.TestConfigScanModule;
import org.vclipse.configscan.utils.DocumentUtility;
import org.vclipse.configscan.utils.TestCaseFactory;
import org.vclipse.configscan.views.ConfigScanViewInput;
import org.vclipse.configscan.views.TestRunsHistory;
import org.w3c.dom.Document;

import com.google.common.collect.Lists;
import com.google.inject.Injector;

// JUnit plug-in test
public class TestRunsHistoryTest {

	private ConfigScanPlugin plugin;
	
	private DocumentUtility documentUtility;
	private TestCaseFactory testCaseFactory;
	
	private String filesPath;
	
	private Injector injector;
	
	private JUnitTestUtils utils;
	
	@Before
	public void setUp() throws IOException {
		plugin = ConfigScanPlugin.getDefault();
		
		filesPath = FileLocator.getBundleFile(plugin.getBundle()).toString().replace("configscan", "configscan.tests") + "/files";
		
		injector = plugin.getInjector(new TestConfigScanModule(plugin));
		documentUtility = injector.getInstance(DocumentUtility.class);
		testCaseFactory = injector.getInstance(TestCaseFactory.class);
		utils = injector.getInstance(JUnitTestUtils.class);
		
		utils.clean(filesPath + "/history/temp.xml");
	}
	
	@Test
	public void testLoadHistory() throws IOException {
		URL resource = plugin.getBundle().getResource("/files/history/history.xml");
		TestRunsHistory testHistory = new TestRunsHistory(plugin, documentUtility, testCaseFactory);
		testHistory.load(resource.openConnection().getInputStream());
		List<ConfigScanViewInput> history = testHistory.getHistory();
		assertEquals("History has one entry", 1, history.size());
		
		ConfigScanViewInput input = history.get(0);
		assertEquals("New_configuration", input.getConfigurationName());
		assertEquals("Fri, 27 Jan 2012 11:39:46", input.getDate());
		
		List<TestRun> testRuns = input.getTestRuns();
		assertEquals(1, testRuns.size());
		TestRun testRun = testRuns.get(0);
		testComplete(testRun, true, true, TestRun.class);
		testValues(testRun, 
				"Testrun on Fri, 27 Jan 2012 11:39:46 with New_configuration", 
				"Begin session 133-056132.00 version:0.1 build (* ) IPC IPC D75", 
				1);
	}
	
	@Test
	public void testSaveHistory() throws IOException {
		URL history2xmlUrl = plugin.getBundle().getResource("/files/history/logResult.xml");
		Document document = documentUtility.parse(history2xmlUrl.openConnection().getInputStream());
		
		TestRun testRun = testCaseFactory.buildTestRun("test_run", null, (IConfigScanXMLProvider)null, null);
		testRun.setLogElement(document);
		TestCase testCase = testCaseFactory.buildTestCase(document, testRun);
		testRun.addTestCase(testCase);
		
		List<TestCase> testCases = testRun.getTestCases();
		assertEquals("There is one session element", 1, testCases.size());
		
		TestCase sessionTestCase = testCases.get(0);
		testComplete(sessionTestCase, true, false, TestGroup.class);
		testCases = ((TestGroup)sessionTestCase).getTestCases();
		assertEquals("There is one test group", 1, testCases.size());
		
		TestCase testGroup = testCases.get(0);
		testComplete(sessionTestCase, true, false, TestGroup.class);
		
		testCases = ((TestGroup)testGroup).getTestCases();
		assertEquals("There are 5 tests", 5, testCases.size());
		
		ConfigScanViewInput input = new ConfigScanViewInput();
		input.setConfigurationName("configuration_one");
		input.setDate(null, IConfigScanConfiguration.DATE_FORMAT_UI_ENTRIES);
		input.setTestRuns(Lists.newArrayList(testRun));
		
		TestRunsHistory testHistory = new TestRunsHistory(plugin, documentUtility, testCaseFactory);
		
		testHistory.addEntry(input);
		testHistory.save(filesPath + "/history/historySaved.xml");
		
		assertEquals("History not cleared after save", 1, testHistory.getHistory().size());
		testHistory.clear();
		assertEquals("History cleared", 0, testHistory.getHistory().size());
		
		TestRunsHistory history2 = new TestRunsHistory(plugin, documentUtility, testCaseFactory);
		history2xmlUrl = plugin.getBundle().getResource("/files/history/historySaved.xml");
		history2.load(history2xmlUrl.openConnection().getInputStream());
		List<ConfigScanViewInput> history = history2.getHistory();
		assertEquals(1, history.size());
	}

	@Test
	public void testMultipleLoadHistory() throws IOException {
		URL resource = plugin.getBundle().getResource("/files/history/history.xml");
		InputStream inputStream = resource.openConnection().getInputStream();
		TestRunsHistory testHistory = new TestRunsHistory(plugin, documentUtility, testCaseFactory);
		testHistory.load(inputStream);
		testHistory.load(inputStream);
		testHistory.load(inputStream);
		testHistory.load(inputStream);
		testHistory.load(inputStream);
		assertEquals("History has one entry", 1, testHistory.getHistory().size());
	}

	@Test
	public void testPreferencesBehavior() throws IOException {
		IPreferenceStore store = ConfigScanPlugin.getDefault().getPreferenceStore();
		
		// history should not be saved/loaded
		store.setValue(IConfigScanConfiguration.SAVE_HISTORY, false);
		TestRunsHistory testHistory = new TestRunsHistory(plugin, documentUtility, testCaseFactory);
		List<ConfigScanViewInput> inputs = utils.createConfigScanViewInputs("first", "second");
		for(ConfigScanViewInput input : inputs) {
			testHistory.addEntry(input);
		}
		testHistory.save(filesPath + "/history/temp.xml");
		testHistory.clear();
		testHistory.load(filesPath + "/history/temp.xml");
		List<ConfigScanViewInput> history = testHistory.getHistory();
		assertEquals(0, history.size());
		
		// history should be saved/loaded
		store.setValue(IConfigScanConfiguration.SAVE_HISTORY, true);
		testHistory = new TestRunsHistory(plugin, documentUtility, testCaseFactory);
		inputs = utils.createConfigScanViewInputs("first", "second");
		for(ConfigScanViewInput input : inputs) {
			testHistory.addEntry(input);
		}
		testHistory.save(filesPath + "/history/temp.xml");
		
		testHistory.clear();
		testHistory.load(filesPath + "/history/temp.xml");
		history = testHistory.getHistory();
		assertEquals(2, history.size());
		
		// the amount of entries does not exceed the preset value
		store.setValue(IConfigScanConfiguration.HISTORY_ENTRIES_NUMBER, 5);
		testHistory = new TestRunsHistory(plugin, documentUtility, testCaseFactory);
		store.addPropertyChangeListener(testHistory);
		inputs = utils.createConfigScanViewInputs("1", "2", "3", "4", "5", "6");
		for(ConfigScanViewInput input : inputs) {
			testHistory.addEntry(input);
		}
		inputs = testHistory.getHistory();
		assertEquals(5, testHistory.getHistory().size());
		assertEquals("2", inputs.get(0).getConfigurationName());
		assertEquals("3", inputs.get(1).getConfigurationName());
		assertEquals("4", inputs.get(2).getConfigurationName());
		assertEquals("5", inputs.get(3).getConfigurationName());
		assertEquals("6", inputs.get(4).getConfigurationName());
		
		// the first 2 values are lost if we set the amount of values to 3
		store.setValue(IConfigScanConfiguration.HISTORY_ENTRIES_NUMBER, 3);
		inputs = testHistory.getHistory();
		assertEquals(3, inputs.size());
		assertEquals("4", inputs.get(0).getConfigurationName());
		assertEquals("5", inputs.get(1).getConfigurationName());
		assertEquals("6", inputs.get(2).getConfigurationName());
		
		store.setValue(IConfigScanConfiguration.HISTORY_ENTRIES_NUMBER, 1);
		assertEquals(1, inputs.size());
		assertEquals("6", inputs.get(0).getConfigurationName());
	}
}
