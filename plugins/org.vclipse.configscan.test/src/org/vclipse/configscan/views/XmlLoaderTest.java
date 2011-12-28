package org.vclipse.configscan.views;

import static org.junit.Assert.assertNotNull;

import org.eclipse.core.runtime.CoreException;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.vclipse.configscan.IConfigScanRunner;
import org.vclipse.configscan.MockConfigScanRunner;
import org.vclipse.configscan.views.XmlLoader;
import org.w3c.dom.Document;

import com.sap.conn.jco.JCoException;


public class XmlLoaderTest {

	private XmlLoader xmlLoader;

	@Before
	public void setUp() throws Exception {
		xmlLoader = new XmlLoader();
	}

	@After
	public void tearDown() throws Exception {
		xmlLoader = null;
	}

	@Ignore("not implemented yet")
	@Test
	public void testParseXml() {
		// TODO: do I have to test build-in methods? exception-testing
		Document xml = null;
//		xml = xmlLoader.parseXml(ConfigScanView.XML_FILENAME);
		
		assertNotNull("xml must not be null", xml);
	}
	
	@Ignore
	@Test
	public void testParseXml2() throws JCoException, CoreException {
		IConfigScanRunner csr = new MockConfigScanRunner();

//		IWorkspaceRoot workspace = ResourcesPlugin.getWorkspace().getRoot();
//		IProject project = workspace.getProject("TestProject");
//		if(!project.exists()) {
//			project.create(null);
//			project.open(null);
//			
//		}
//		
//		IFile file = project.getFile("");
//		if(!file.exists()) {
//			file.create(new ByteArrayInputStream("".getBytes()), true, null);
//		}
//		
		String xmlStr = csr.execute(null, null, null, null, null);	// FIXME
		Document xml = xmlLoader.parseXmlString(xmlStr);
	}
	
}
