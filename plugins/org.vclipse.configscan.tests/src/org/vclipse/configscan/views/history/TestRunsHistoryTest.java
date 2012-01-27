package org.vclipse.configscan.views.history;

import static org.junit.Assert.assertEquals;
import static org.vclipse.configscan.TestCaseAssert.checkComplete;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

import org.eclipse.core.runtime.FileLocator;
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
	}
	
	@Test
	public void testLoadHistory() throws IOException {
		URL resource = plugin.getBundle().getResource("/files/history.xml");
		testHistory.load(resource.openConnection().getInputStream());
		assertEquals("History has one entry", 1, testHistory.getHistory().size());
		
		
	}
	
	@Test
	public void testSaveHistory() throws IOException {
		URL history2xmlUrl = plugin.getBundle().getResource("/files/history2.xml");
		Document document = documentUtility.parse(history2xmlUrl.openConnection().getInputStream());
		
		TestRun testRun = testCaseFactory.buildTestRun("test_run", null, null, null);
		TestCase testCase = testCaseFactory.buildTestCase(document, testRun);
		testRun.addTestCase(testCase);
		
		List<TestCase> testCases = testRun.getTestCases();
		assertEquals("There is one session element", 1, testCases.size());
		
		TestCase sessionTestCase = testCases.get(0);
		checkComplete(sessionTestCase, true, false, TestGroup.class);
		testCases = ((TestGroup)sessionTestCase).getTestCases();
		assertEquals("There is one test group", 1, testCases.size());
		
		TestCase testGroup = testCases.get(0);
		checkComplete(sessionTestCase, true, false, TestGroup.class);
		
		testCases = ((TestGroup)testGroup).getTestCases();
		assertEquals("There are 5 tests", 5, testCases.size());
		
		ConfigScanViewInput input = new ConfigScanViewInput();
		input.setConfigurationName("configuration_one");
		input.setDate(null, IConfigScanConfiguration.DATE_FORMAT_UI_ENTRIES);
		input.setTestRuns(Lists.newArrayList(testRun));
		
		testHistory.addEntry(input);
		testHistory.save(filesPath + "/historySaved.xml");
		
		assertEquals("History not cleared after save", 1, testHistory.getHistory().size());
		
		TestRunsHistory history2 = new TestRunsHistory(plugin, documentUtility, testCaseFactory);
		history2xmlUrl = plugin.getBundle().getResource("/files/historySaved.xml");
		history2.load(history2xmlUrl.openConnection().getInputStream());
		assertEquals(1, testHistory.getHistory().size());
	}

	@Test
	public void testDoubleLoadHistory() throws IOException {
		URL resource = plugin.getBundle().getResource("/files/history.xml");
		InputStream inputStream = resource.openConnection().getInputStream();
		testHistory.load(inputStream);
		testHistory.load(inputStream);
		testHistory.load(inputStream);
		assertEquals("History has one entry", 1, testHistory.getHistory().size());
	}
}
