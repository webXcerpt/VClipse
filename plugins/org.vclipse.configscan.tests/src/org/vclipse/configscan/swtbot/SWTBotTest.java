package org.vclipse.configscan.swtbot;

import java.awt.Label;
import java.util.Properties;
import java.util.regex.Matcher;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotLabel;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotText;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.eclipse.swtbot.swt.finder.matchers.WidgetMatcherFactory.*;
import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;


@RunWith(SWTBotJunit4ClassRunner.class)
public class SWTBotTest {
	private static SWTWorkbenchBot bot;
	
	@BeforeClass
	public static void beforeClass() throws Exception {
		bot = new SWTWorkbenchBot();
	}
	@AfterClass
	public static void sleep() throws Exception {
		bot.sleep(10000);
	}
	@Test
	public void testShowView1() {
		bot.menu("Window").menu("Show View").menu("Other...").click();
		
		SWTBotShell shell = bot.shell("Show View");
		shell.activate();
		bot.tree().expandNode("Sample Category").select("ConfigScan");
		bot.button("OK").click();
	}
	@Test
	public void testShowView2() {
		bot.menu("Window").menu("Show View").menu("Other...").click();
		
		SWTBotShell shell = bot.shell("Show View");
		shell.activate();
		bot.tree().expandNode("Java").select("Package Explorer");
		bot.button("OK").click();
	}
	@Test
	public void testRunAsConfigturationConfigScan() {
		
		SWTBotView packageExplorer = bot.viewByTitle("Package Explorer");
		SWTBotTree tree = packageExplorer.bot().tree();
		packageExplorer.show();
		packageExplorer.setFocus();
		SWTBotTreeItem item = tree.getTreeItem("NetViewer [trunk/NetViewer]");
		item.setFocus();
		item.expand();
				
		item = item.getNode("NetViewer-ok");
		item.expand();
		item = item.getNode("NetViewer-ok.cmlt");
		item.select();
		
		item.contextMenu("Run As").menu("Run Configurations...").click();
		SWTBotShell shell = bot.activeShell();
		assertEquals("Title should be \"Run Configurations\"", "Run Configurations", shell.getText());
		bot.button("Run").click();
		assertFalse(shell.isOpen());
		
	}

	
	
	@Test
	public void testHasConfigScanBeLoaded() {
		try {
			SWTBotTest.sleep();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		SWTBotView configScan = bot.viewByTitle("ConfigScan");
		configScan.setFocus();
		SWTBotLabel label = bot.label("Runs: 10");
		assertNotNull(label);
	}
}
