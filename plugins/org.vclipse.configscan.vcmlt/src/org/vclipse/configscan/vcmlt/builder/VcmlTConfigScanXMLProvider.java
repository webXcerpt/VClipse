package org.vclipse.configscan.vcmlt.builder;

import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.vclipse.configscan.IConfigScanXMLProvider;
import org.vclipse.configscan.vcmlt.vcmlT.Model;
import org.vclipse.configscan.vcmlt.vcmlT.TestGroup;
import org.vclipse.configscan.vcmlt.vcmlT.Action;
import org.vclipse.configscan.vcmlt.vcmlT.SetValue;
import org.vclipse.configscan.vcmlt.vcmlT.util.VcmlTSwitch;
import org.w3c.dom.Document;
import org.w3c.dom.Element;



public class VcmlTConfigScanXMLProvider extends VcmlTSwitch<Object> implements
		IConfigScanXMLProvider {

	private Document doc;
	private Map<Element, URI> map;
	private Element current;
	private DocumentBuilder xmlDocBuilder;

	@Override
	public Document transform(EObject model, Map<Element, URI> map) {
		try {
			this.xmlDocBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		this.doc = xmlDocBuilder.newDocument();
		this.map = map;
		this.current = null;
		
		doSwitch(model);
		
		return doc;
	}

	@Override
	public Object caseModel(Model model) {
		Element root = doc.createElement("configtest");
		map.put(root, EcoreUtil.getURI(model));
		root.setAttribute("version", "1.0");
		Element session = doc.createElement("session");
		
		root.appendChild(session);
		doc.appendChild(root);
		current = session;
		
		for (final TestGroup tg : model.getTestgroups()) {
			
			doSwitch(tg);
			current = session;
		}
		
		return this; // caseModel was successful
	}



	@Override
	public Object caseTestGroup(final TestGroup testGroup) {
		Element tg = doc.createElement("testgroup");
		final String name = testGroup.getName();
		tg.setAttribute("id", name);
		final String description = testGroup.getDescription();
		tg.setAttribute("desc", description!=null ? description : name);
		tg.setAttribute("testmode", testGroup.getTestmode().toString()); // evtl mit geigneter Methode in Attributwert abbilden
		
		map.put(tg, EcoreUtil.getURI(testGroup));
		current.appendChild(tg);
		current = tg;

		// ...
		
	    for (final Action action : testGroup.getActions()) {
			doSwitch(action);
		}
		return this;
	}

	@Override
	public Object caseSetValue(final SetValue object) {
		Element action = doc.createElement("command");
		action.setAttribute("action", "setvalue");

		action.setAttribute("name", (object.getCstic()).getName());
		
		map.put(action, EcoreUtil.getURI(object));
		current.appendChild(action);
		return this;
	}


	@Override
	public HashMap<Element, Element> computeConfigScanMap(Document xmlLog,
			Document xmlInput) {
		// will be removed from here
		return null;
	}

	@Override
	public String getMaterialNumber(EObject model) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getBomApplication(EObject model) {
		// will be removed from here
		return null;
	}

}
