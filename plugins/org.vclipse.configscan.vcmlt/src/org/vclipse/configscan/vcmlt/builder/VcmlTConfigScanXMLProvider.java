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
import org.vclipse.configscan.vcmlt.vcmlT.CheckBomCountItems;
import org.vclipse.configscan.vcmlt.vcmlT.CheckBomItemQty;
import org.vclipse.configscan.vcmlt.vcmlT.CheckComplete;
import org.vclipse.configscan.vcmlt.vcmlT.CheckConflict;
import org.vclipse.configscan.vcmlt.vcmlT.CheckDomain;
import org.vclipse.configscan.vcmlt.vcmlT.CheckDomainStrict;
import org.vclipse.configscan.vcmlt.vcmlT.CheckSingleValue;
import org.vclipse.configscan.vcmlt.vcmlT.CsticState;
import org.vclipse.configscan.vcmlt.vcmlT.DomainStrictValue;
import org.vclipse.configscan.vcmlt.vcmlT.DomainValue;
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
		Element e = doc.createElement("command");
		e.setAttribute("action", "setvalue");
		e.setAttribute("name", (object.getCstic()).getName());
		e.setAttribute("value", getValue(object.getValue()));
		e.setAttribute("bompath", getChildPath(object.getBompath()));
		
		map.put(e, EcoreUtil.getURI(object));
		current.appendChild(e);
		return this;
	}

	@Override
	// ToDo: NumericLiteral
	// ToDo: NumericInterval
	// ToDo: re-think vcmlt design: '=' in check is redundant
	public Object caseCheckSingleValue (final CheckSingleValue object) {
		EList<CsticState> st = object.getStatus();
		if (st != null) {
			Iterator<CsticState> sti = object.getStatus().iterator();
			if (sti.hasNext()) {
				Element es = doc.createElement("checkstatus");
				es.setAttribute("name", (object.getCstic()).getName());
				es.setAttribute("bompath", getChildPath(object.getBompath()));
				map.put(es, EcoreUtil.getURI(object));
				current.appendChild(es);			
				while (sti.hasNext()) {
					String s = sti.next().getName();
					if (s.equals("invisible"))
						s = "hidden";
					Element estat = doc.createElement("status");
					estat.setTextContent(s);
					map.put(estat, EcoreUtil.getURI(object));
					es.appendChild(estat);			
				}
			}			
		}

		if (object.getValue() != null) {
			Element ec = doc.createElement("checksinglevalue");
			ec.setAttribute("name", (object.getCstic()).getName());
			// ToDo: strip off surrounding quotes
			ec.setAttribute("value", getValue((SymbolicLiteral) object.getValue()));
			ec.setAttribute("bompath", getChildPath(object.getBompath()));
			map.put(ec, EcoreUtil.getURI(object));
			current.appendChild(ec);
		}
		return this;
	}

	@Override
	// ToDo: doublecheck operator correctness
	public Object caseCheckBomItemQty (final CheckBomItemQty object) {
		Element e = doc.createElement("checkbomquantity");
		e.setAttribute("quantity", Integer.toString(object.getAmount()));
		e.setAttribute("operator", object.getOperator().getName());
		e.setAttribute("bompath", getChildPath(object.getBompath()));
		
		map.put(e, EcoreUtil.getURI(object));
		current.appendChild(e);
		return this;
	}

	@Override
	public Object caseCheckDomain (final CheckDomain object) {
		Element e = doc.createElement("checkdomain");
		e.setAttribute("name", object.getCstic().getName());
		e.setAttribute("bompath", getChildPath(object.getBompath()));
		EList<DomainValue> vs = object.getValues();
		if (vs != null) {
			Iterator<DomainValue> vsi = vs.iterator();
			while (vsi.hasNext()) {
				Element ev = doc.createElement("values");
				DomainValue val = vsi.next();
				ev.setAttribute("value", getValue(val.getLiteral()));
				if (val.isNot())
					ev.setAttribute("mode", "out");
				else
					ev.setAttribute("mode", "in");
				map.put(ev, EcoreUtil.getURI(object));
				e.appendChild(ev);			
			}
		}
		
		map.put(e, EcoreUtil.getURI(object));
		current.appendChild(e);
		return this;
	}

	@Override
	public Object caseCheckDomainStrict (final CheckDomainStrict object) {
		Element e = doc.createElement("checkdomainstrict");
		e.setAttribute("name", object.getCstic().getName());
		e.setAttribute("bompath", getChildPath(object.getBompath()));
		EList<DomainStrictValue> vs = object.getValues();
		if (vs != null) {
			Iterator<DomainStrictValue> vsi = vs.iterator();
			while (vsi.hasNext()) {
				Element ev = doc.createElement("values");
				ev.setAttribute("value", getValue(vsi.next().getLiteral()));
				map.put(ev, EcoreUtil.getURI(object));
				e.appendChild(ev);			
			}
		}
		
		map.put(e, EcoreUtil.getURI(object));
		current.appendChild(e);
		return this;
	}

	@Override
	public Object caseCheckBomCountItems (final CheckBomCountItems object) {
		Element e = doc.createElement("countitems");
		e.setAttribute("quantity", Integer.toString(object.getValue()));
		e.setAttribute("bompath", getChildPath(object.getBompath()));
		
		map.put(e, EcoreUtil.getURI(object));
		current.appendChild(e);
		return this;
	}

	@Override
	public Object caseCheckComplete (final CheckComplete object) {
		Element e = doc.createElement("checkinststatus");
		if (object.isComplete())
			e.setAttribute("iscomplete", "TRUE");
		else
			e.setAttribute("iscomplete", "FALSE");
		e.setAttribute("bompath", getChildPath(object.getBompath()));
		
		map.put(e, EcoreUtil.getURI(object));
		current.appendChild(e);
		return this;
	}

	@Override
	public Object caseCheckConflict (final CheckConflict object) {
		Element e = doc.createElement("checkinststatus");
		if (object.isConflict())
			e.setAttribute("isconsistent", "FALSE");
		else
			e.setAttribute("isconsistent", "TRUE");
		e.setAttribute("bompath", getChildPath(object.getBompath()));
		
		map.put(e, EcoreUtil.getURI(object));
		current.appendChild(e);
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
