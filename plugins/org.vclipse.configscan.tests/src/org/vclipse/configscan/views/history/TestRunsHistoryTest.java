package org.vclipse.configscan.views.history;

import static org.junit.Assert.assertEquals;
import static org.vclipse.configscan.TestCaseAssert.testComplete;
import static org.vclipse.configscan.TestCaseAssert.testValues;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.jface.preference.IPreferenceStore;
import org.junit.Before;
import org.junit.Test;
import org.vclipse.configscan.ConfigScanPlugin;
import org.vclipse.configscan.IConfigScanConfiguration;
import org.vclipse.configscan.impl.model.TestCase;
import org.vclipse.configscan.impl.model.TestGroup;
import org.vclipse.configscan.impl.model.TestRun;
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
	
	private TestRunsHistory testHistory;
	
	private Injector injector;
	
	@Before
	public void setUp() throws IOException {
		plugin = ConfigScanPlugin.getDefault();
		
		filesPath = FileLocator.getBundleFile(plugin.getBundle()).toString().replace("configscan", "configscan.tests") + "/files";
		
		injector = plugin.getInjector();
		documentUtility = injector.getInstance(DocumentUtility.class);
		testCaseFactory = injector.getInstance(TestCaseFactory.class);
		
		testHistory = new TestRunsHistory(plugin, documentUtility, testCaseFactory);
		
		clean(filesPath + "/temp.xml");
	}
	
	private void clean(String path) throws IOException {
		BufferedWriter out = new BufferedWriter(new FileWriter(path));
		out.write("");
		out.close();
	}
	
	@Test
	public void testLoadHistory() throws IOException {
		URL resource = plugin.getBundle().getResource("/files/history.xml");
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
		testValues(testRun, "Testrun on Fri, 27 Jan 2012 11:39:46 with New_configuration", 
				"Begin session 133-056132.00 version:0.1 build (* ) IPC IPC D75", 
				1);
	}
	
	@Test
	public void testSaveHistory() throws IOException {
		URL history2xmlUrl = plugin.getBundle().getResource("/files/logResult.xml");
		Document document = documentUtility.parse(history2xmlUrl.openConnection().getInputStream());
		
		TestRun testRun = testCaseFactory.buildTestRun("test_run", null, null, null);
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
		
		testHistory.addEntry(input);
		testHistory.save(filesPath + "/historySaved.xml");
		
		assertEquals("History not cleared after save", 1, testHistory.getHistory().size());
		testHistory.clear();
		assertEquals("History cleared", 0, testHistory.getHistory().size());
		
		TestRunsHistory history2 = new TestRunsHistory(plugin, documentUtility, testCaseFactory);
		history2xmlUrl = plugin.getBundle().getResource("/files/historySaved.xml");
		history2.load(history2xmlUrl.openConnection().getInputStream());
		List<ConfigScanViewInput> history = history2.getHistory();
		assertEquals(1, history.size());
	}

	@Test
	public void testMultipleLoadHistory() throws IOException {
		URL resource = plugin.getBundle().getResource("/files/history.xml");
		InputStream inputStream = resource.openConnection().getInputStream();
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
		List<ConfigScanViewInput> inputs = createConfigScanViewInputs("first", "second");
		for(ConfigScanViewInput input : inputs) {
			testHistory.addEntry(input);
		}
		testHistory.save(filesPath + "/temp.xml");
		testHistory.clear();
		testHistory.load(filesPath + "/temp.xml");
		List<ConfigScanViewInput> history = testHistory.getHistory();
		assertEquals(0, history.size());
		
		// history should be saved/loaded
		store.setValue(IConfigScanConfiguration.SAVE_HISTORY, true);
		testHistory = new TestRunsHistory(plugin, documentUtility, testCaseFactory);
		inputs = createConfigScanViewInputs("first", "second");
		for(ConfigScanViewInput input : inputs) {
			testHistory.addEntry(input);
		}
		testHistory.save(filesPath + "/temp.xml");
		
		testHistory.clear();
		testHistory.load(filesPath + "/temp.xml");
		history = testHistory.getHistory();
		assertEquals(2, history.size());
		
		
	}
	
	protected List<ConfigScanViewInput> createConfigScanViewInputs(String ... names) {
		List<ConfigScanViewInput> inputs = Lists.newArrayList();
		for(String name : names) {
			ConfigScanViewInput input = new ConfigScanViewInput();
			input.setConfigurationName(name);
			input.setDate(null, IConfigScanConfiguration.DATE_FORMAT_UI_ENTRIES);
			input.setTestRuns(new ArrayList<TestRun>());
			inputs.add(input);
		}
		return inputs;
	}
}
