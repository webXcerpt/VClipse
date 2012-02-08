package org.vclipse.configscan.views;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TreeItem;
import org.junit.Before;
import org.junit.Test;
import org.vclipse.configscan.ConfigScanImageHelper;
import org.vclipse.configscan.ConfigScanPlugin;
import org.vclipse.configscan.JUnitTestUtils;
import org.vclipse.configscan.extension.ExtensionPointReader;
import org.vclipse.configscan.impl.model.TestCase;
import org.vclipse.configscan.impl.model.TestGroup;
import org.vclipse.configscan.impl.model.TestRun;
import org.vclipse.configscan.injection.TestConfigScanModule;
import org.vclipse.configscan.utils.DocumentUtility;
import org.vclipse.configscan.utils.FailureTreeTraverser;
import org.vclipse.configscan.utils.TestCaseFactory;
import org.vclipse.configscan.utils.TypeTreeTraverser;

import com.google.common.collect.Lists;
import com.google.inject.Injector;

public class TreeTraverserTest {

	JUnitTestUtils utilities;
	
	ConfigScanPlugin plugin;
	
	DocumentUtility documentUtility;
	
	TestCaseFactory testCaseUtility;
	
	ConfigScanImageHelper imageHelper;
	
	List<ConfigScanViewInput> inputs = Lists.newArrayList();
	
	@Before
	public void setUp() throws IOException {
		plugin = ConfigScanPlugin.getDefault();
		
		Injector injector = plugin.getInjector(new TestConfigScanModule(plugin));
		documentUtility = injector.getInstance(DocumentUtility.class);
		testCaseUtility = injector.getInstance(TestCaseFactory.class);
		utilities = injector.getInstance(JUnitTestUtils.class);
		imageHelper = injector.getInstance(ConfigScanImageHelper.class);
	}
	
	@Test
	public void testGetNextFailure() throws IOException {
		URL resource = plugin.getBundle().getResource("/files//treetraverser/treeTraverserTest.xml");
		TestRunsHistory history = new TestRunsHistory(plugin, documentUtility, testCaseUtility);
		history.load(resource.openStream());
		inputs = history.getHistory();
		
		Shell shell = new Shell(Display.getDefault());

		JobAwareTreeViewer treeViewer = new JobAwareTreeViewer(shell, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.VIRTUAL);
		treeViewer.setContentProvider(new ContentProvider());
		treeViewer.setLabelProvider(new DefaultLabelProvider(new ExtensionsHandlingLabelProvider(new ExtensionPointReader()), treeViewer));
		treeViewer.setInput(inputs.get(0));

		TreeItem item = treeViewer.getTree().getItem(0);
		Object data = item.getData();
		assertTrue(data instanceof TestRun);

		// loading over the history! another label than if you would be loading this file over import action
		TestRun testRun = (TestRun)data;
		assertTrue(testRun.getTitle().equals("Testrun on Tue, 31 Jan 2012 14:21:06 with configuration_one"));
		treeViewer.setSelection(new StructuredSelection(testRun));

		List<TestCase> testCases = testRun.getTestCases();
		assertEquals("1 session element", 1, testCases.size());
		TestCase testCase = testCases.get(0);
		assertTrue(testCase instanceof TestGroup);
		TestGroup session = (TestGroup)testCase;
		treeViewer.setSelection(new StructuredSelection(session));

		FailureTreeTraverser failureTreeTraverser = new FailureTreeTraverser();
		TestCase nextNode = failureTreeTraverser.getNextItem(session);
		assertTrue(nextNode != null);
		assertTrue(nextNode instanceof TestCase);

		testCase = (TestCase)nextNode;
		assertEquals("Test 1", testCase.getTitle());

		nextNode = failureTreeTraverser.getNextItem(testCase);
		assertTrue(nextNode != null);
		assertEquals("Test 1", nextNode.getTitle());

		nextNode = failureTreeTraverser.getNextItem(nextNode);
		assertTrue(nextNode != null);
		assertEquals("Test 2", nextNode.getTitle());

		nextNode = failureTreeTraverser.getNextItem(nextNode);
		assertTrue(nextNode != null);
		assertEquals("Test 5", nextNode.getTitle());

		nextNode = failureTreeTraverser.getNextItem(nextNode);
		assertTrue(nextNode != null);
		assertEquals("Test 1", nextNode.getTitle());

		nextNode = failureTreeTraverser.getNextItem(nextNode);
		assertTrue(nextNode != null);
		assertEquals("Test 4", nextNode.getTitle());
	}

	@Test
	public void testGetPreviousFailure() throws IOException {
		URL resource = plugin.getBundle().getResource("/files//treetraverser/treeTraverserTest.xml");
		TestRunsHistory history = new TestRunsHistory(plugin, documentUtility, testCaseUtility);
		history.load(resource.openStream());
		inputs = history.getHistory();
		
		Shell shell = new Shell(Display.getDefault());

		JobAwareTreeViewer treeViewer = new JobAwareTreeViewer(shell, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.VIRTUAL);
		treeViewer.setContentProvider(new ContentProvider());
		treeViewer.setLabelProvider(new DefaultLabelProvider(
				new ExtensionsHandlingLabelProvider(new ExtensionPointReader()), treeViewer));
		treeViewer.setInput(inputs.get(0));
		
		TreeItem item = treeViewer.getTree().getItem(0);
		Object data = item.getData();
		assertTrue(data instanceof TestRun);

		// loading over the history! another label than if you would be loading this file over import action
		TestRun testRun = (TestRun)data;
		assertTrue(testRun.getTitle().equals("Testrun on Tue, 31 Jan 2012 14:21:06 with configuration_one"));
		treeViewer.setSelection(new StructuredSelection(testRun));
		
		FailureTreeTraverser failureTreeTraverser = new FailureTreeTraverser();
		TestCase nextNode = failureTreeTraverser.getPreviousItem(testRun);
		assertTrue(nextNode != null);
		assertTrue(nextNode instanceof TestCase);
		
		nextNode = (TestCase)nextNode;
		assertEquals("Test 4", nextNode.getTitle());
		
		nextNode = failureTreeTraverser.getPreviousItem(nextNode);
		assertEquals("Test 1", nextNode.getTitle());
		
		nextNode = failureTreeTraverser.getPreviousItem(nextNode);
		assertEquals("Test 5", nextNode.getTitle());
		
		nextNode = failureTreeTraverser.getPreviousItem(nextNode);
		assertEquals("Test 2", nextNode.getTitle());
		
		nextNode = failureTreeTraverser.getPreviousItem(nextNode);
		assertEquals("Test 1", nextNode.getTitle());
		
		nextNode = failureTreeTraverser.getPreviousItem(nextNode);
		assertEquals("Test 1", nextNode.getTitle());
		
		nextNode = failureTreeTraverser.getPreviousItem(nextNode);
		assertEquals("Test 4", nextNode.getTitle());
	}

	@Test
	public void testGetNextTestCase() throws IOException {
		URL resource = plugin.getBundle().getResource("/files//treetraverser/treeTraverserTest.xml");
		TestRunsHistory history = new TestRunsHistory(plugin, documentUtility, testCaseUtility);
		history.load(resource.openStream());
		inputs = history.getHistory();

		Shell shell = new Shell(Display.getDefault());

		JobAwareTreeViewer treeViewer = new JobAwareTreeViewer(shell, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.VIRTUAL);
		treeViewer.setContentProvider(new ContentProvider());
		treeViewer.setLabelProvider(new DefaultLabelProvider(new ExtensionsHandlingLabelProvider(new ExtensionPointReader()), treeViewer));
		treeViewer.setInput(inputs.get(0));

		TreeItem item = treeViewer.getTree().getItem(0);
		Object data = item.getData();
		assertTrue(data instanceof TestRun);

		// loading over the history! another label than if you would be loading this file over import action
		TestRun testRun = (TestRun)data;
		assertTrue(testRun.getTitle().equals("Testrun on Tue, 31 Jan 2012 14:21:06 with configuration_one"));
		treeViewer.setSelection(new StructuredSelection(testRun));

		List<TestCase> testCases = testRun.getTestCases();
		assertEquals("1 session element", 1, testCases.size());
		TestCase testCase = testCases.get(0);
		assertTrue(testCase instanceof TestGroup);
		TestGroup session = (TestGroup)testCase;
		treeViewer.setSelection(new StructuredSelection(session));

		TypeTreeTraverser typeTreeTraverser = new TypeTreeTraverser(TestCase.class);
		TestCase nextNode = typeTreeTraverser.getNextItem(session);
		assertTrue(nextNode != null);
		assertTrue(nextNode instanceof TestCase);

		testCase = (TestCase)nextNode;
		assertEquals("Test 1", testCase.getTitle());

		nextNode = typeTreeTraverser.getNextItem(testCase);
		assertTrue(nextNode != null);
		assertEquals("Test 2", nextNode.getTitle());

		nextNode = typeTreeTraverser.getNextItem(nextNode);
		assertTrue(nextNode != null);
		assertEquals("Test 3", nextNode.getTitle());

		nextNode = typeTreeTraverser.getNextItem(nextNode);
		assertTrue(nextNode != null);
		assertEquals("Test 4", nextNode.getTitle());

		nextNode = typeTreeTraverser.getNextItem(nextNode);
		assertTrue(nextNode != null);
		assertEquals("Test 5", nextNode.getTitle());

		nextNode = typeTreeTraverser.getNextItem(nextNode);
		assertTrue(nextNode != null);
		assertEquals("Test 1", nextNode.getTitle());
	}
}
