package org.vclipse.configscan.vcmlt.builder;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.vclipse.configscan.IConfigScanXMLProvider;
import org.vclipse.configscan.vcmlt.vcmlT.BomPath;
import org.vclipse.configscan.vcmlt.vcmlT.CheckBomItemQty;
import org.vclipse.configscan.vcmlt.vcmlT.CheckSingleValue;
import org.vclipse.configscan.vcmlt.vcmlT.CsticState;
import org.vclipse.configscan.vcmlt.vcmlT.Model;
import org.vclipse.configscan.vcmlt.vcmlT.TestGroup;
import org.vclipse.configscan.vcmlt.vcmlT.Action;
import org.vclipse.configscan.vcmlt.vcmlT.SetValue;
import org.vclipse.configscan.vcmlt.vcmlT.util.VcmlTSwitch;
import org.vclipse.vcml.vcml.Literal;
import org.vclipse.vcml.vcml.Material;
import org.vclipse.vcml.vcml.NumericCharacteristicValue;
import org.vclipse.vcml.vcml.NumericLiteral;
import org.vclipse.vcml.vcml.ObjectType;
import org.vclipse.vcml.vcml.SymbolicLiteral;
import org.vclipse.vcml.vcml.TypeOf;
import org.vclipse.vcml.vcml.impl.NumericCharacteristicValueImpl;
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
		action.setAttribute("value", getValue(object.getValue()));
		action.setAttribute("bompath", getChildPath(object.getBompath()));
		
		map.put(action, EcoreUtil.getURI(object));
		current.appendChild(action);
		return this;
	}

	@Override
	// ToDo: NumericLiteral
	// ToDo: NumericInterval
	// ToDo: re-think vcmlt design: '=' in check is redundant
	public Object caseCheckSingleValue (final CheckSingleValue object) {
		EList<CsticState> st = object.getStatus();
		if (st != null) {
			Iterator<CsticState> e = object.getStatus().iterator();
			if (e.hasNext()) {
				Element checkstatus = doc.createElement("checkstatus");
				checkstatus.setAttribute("name", (object.getCstic()).getName());
				checkstatus.setAttribute("bompath", getChildPath(object.getBompath()));
				map.put(checkstatus, EcoreUtil.getURI(object));
				current.appendChild(checkstatus);			
				while (e.hasNext()) {
					String s = e.next().getName();
					if (s.equals("invisible"))
						s = "hidden";
					Element status = doc.createElement("status");
					status.setTextContent(s);
					map.put(status, EcoreUtil.getURI(object));
					checkstatus.appendChild(status);			
				}
			}			
		}

		if (object.getValue() != null) {
			Element checksinglevalue = doc.createElement("checksinglevalue");
			checksinglevalue.setAttribute("name", (object.getCstic()).getName());
			// ToDo: strip off surrounding quotes
			checksinglevalue.setAttribute("value", getValue((SymbolicLiteral) object.getValue()));
			checksinglevalue.setAttribute("bompath", getChildPath(object.getBompath()));
			map.put(checksinglevalue, EcoreUtil.getURI(object));
			current.appendChild(checksinglevalue);
		}
		return this;
	}

	@Override
	// ToDo: doublecheck operator correctness
	public Object caseCheckBomItemQty (final CheckBomItemQty object) {
		Element checkbomquantity = doc.createElement("checkbomquantity");
		checkbomquantity.setAttribute("quantity", Integer.toString(object.getAmount()));
		checkbomquantity.setAttribute("operator", object.getOperator().getName());
		checkbomquantity.setAttribute("bompath", getChildPath(object.getBompath()));
		
		map.put(checkbomquantity, EcoreUtil.getURI(object));
		current.appendChild(checkbomquantity);
		return this;
	}

	// ToDo: strip off surrounding quotes
	private String getValue(Literal lit) {
		if (lit instanceof SymbolicLiteral) {
		    return ((SymbolicLiteral)lit).getValue();
		} else if (lit instanceof NumericLiteral) {
		    return ((NumericLiteral)lit).getValue();
		} else {
		    throw new IllegalArgumentException("unknown literal type: " + lit);
		}
	}

	private String getChildPath(BomPath bompath) {
		String ret = "/ ";
		if (bompath != null && bompath.getMaterial() != null) {
			if (bompath.getPosition() != 0) {
				ret = ret + "(" + bompath.getPosition() + ") ";				
			}
			ret = ret + bompath.getMaterial().getName() + " ";
			if (bompath.getChild() != null)
				ret = ret + getChildPath(bompath.getChild());
		}			
		return (ret.trim());
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
