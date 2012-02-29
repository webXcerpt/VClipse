package org.vclipse.configscan.swtbot;

import java.io.InputStream;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotToolbarDropDownButton;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.vclipse.base.ui.BaseUiPlugin;
import org.vclipse.configscan.ConfigScanPlugin;
import org.vclipse.configscan.injection.TestConfigScanModule;
import org.vclipse.configscan.utils.DocumentUtility;
import org.vclipse.configscan.utils.TestCaseFactory;
import org.vclipse.configscan.views.TestRunsHistory;

import com.google.inject.Injector;

@RunWith(SWTBotJunit4ClassRunner.class)
public class SWTBotTest {
	
	private static SWTWorkbenchBot bot;
	
	private static InputStream historyStream;
	
	private static BaseUiPlugin plugin;
	
	private static DocumentUtility documentUtility;
	
	private static TestCaseFactory testCaseFactory;
	
	@BeforeClass
	public static void beforeClass() throws Exception {
		bot = new SWTWorkbenchBot();
		bot.perspectiveByLabel("Java").activate();
		
		// close the welcome view
		SWTBotView welcomeView = bot.viewByTitle("Welcome");
		if(welcomeView != null) {
			welcomeView.close();
		}
		
		// close the welcome view
		SWTBotView taskListView = bot.viewByTitle("Task List");
		if(taskListView != null) {
			taskListView.close();
		}
		
		plugin = ConfigScanPlugin.getDefault();
		
		Injector injector = plugin.getInjector(new TestConfigScanModule(plugin));
		
		documentUtility = injector.getInstance(DocumentUtility.class);
		testCaseFactory = injector.getInstance(TestCaseFactory.class);
		
		historyStream = plugin.getBundle().getResource("/files/history/history.xml").openStream();
	}
	
	@AfterClass
	public static void sleep() throws Exception {
		bot.sleep(1000);
	}
	
	@Test
	public void test_ShowConfigScanView() {
		
		// ConfigScan view is available in the menu Vclipse
		bot.menu("Window").menu("Show View").menu("Other...").click();
		bot.shell("Show View").activate();
		bot.tree().expandNode("VClipse").select("ConfigScan");
		bot.button("OK").click();
	}
	
	@Test
	public void test_LoadHistory() {
		// load a test file into the history
		TestRunsHistory history = new TestRunsHistory(plugin, documentUtility, testCaseFactory);
		history.load(historyStream);
		
		// show the ConfigScan view
		SWTBotView configScanView = bot.viewByTitle("ConfigScan");
		configScanView.show();
		
		// click the export menu
		SWTBotToolbarDropDownButton historyButton = configScanView.toolbarDropDownButton("Test run history");
		historyButton.menuItem("Export ...").click();
		
		
		
		// save the history content with save as dialog		
//		SWTBotShell saveDialog = bot.shell("Save As");
//		saveDialog.activate();
//		saveDialog.close();
//		
//		// clear the history 
// 		history.clear();
//		
// 		// there should be a file on the disk now which can be loaded!
//		historyButton.menuItem("Import ...").click();
//		SWTBotShell openDialog = bot.shell("Open");
//		openDialog.activate();
//		bot.textWithLabel("File name:").setText("testHistory.xml");
//		bot.button("Open").click();
//		
//		List<ConfigScanViewInput> entries = history.getHistory();
//		assertEquals(1, entries.size());
//		ConfigScanViewInput input = entries.get(0);
//		assertEquals("New_configuration", input.getConfigurationName());
//		List<TestRun> testRuns = input.getTestRuns();
//		assertEquals(1, testRuns.size());
		
	}
	
//	@Test
//	public void testRunAsConfigturationConfigScan() {
//		
//		SWTBotView packageExplorer = bot.viewByTitle("Package Explorer");
//		SWTBotTree tree = packageExplorer.bot().tree();
//		packageExplorer.show();
//		packageExplorer.setFocus();
//		SWTBotTreeItem item = tree.getTreeItem("NetViewer [trunk/NetViewer]");
//		item.setFocus();
//		item.expand();
//				
//		item = item.getNode("NetViewer-ok");
//		item.expand();
//		item = item.getNode("NetViewer-ok.cmlt");
//		item.select();
//		
//		item.contextMenu("Run As").menu("Run Configurations...").click();
//		SWTBotShell shell = bot.activeShell();
//		assertEquals("Title should be \"Run Configurations\"", "Run Configurations", shell.getText());
//		bot.button("Run").click();
//		assertFalse(shell.isOpen());
//		
//	}
//
//	
//	
//	@Test
//	public void testHasConfigScanBeLoaded() {
////		try {
////			SWTBotTest.sleep();
////		} catch (Exception e) {
////			// TODO Auto-generated catch block
////			e.printStackTrace();
////		}
//		SWTBotView configScan = bot.viewByTitle("ConfigScan");
//		configScan.setFocus();
//		SWTBotLabel label = bot.label("Runs: 10");
//		assertNotNull(label);
//	}
}
