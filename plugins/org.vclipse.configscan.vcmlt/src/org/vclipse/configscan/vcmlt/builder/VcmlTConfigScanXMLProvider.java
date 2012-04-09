package org.vclipse.configscan.vcmlt.builder;

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
import org.vclipse.configscan.ITestObjectFilter;
import org.vclipse.configscan.vcmlt.vcmlT.Action;
import org.vclipse.configscan.vcmlt.vcmlT.BomPath;
import org.vclipse.configscan.vcmlt.vcmlT.CheckBomCountItems;
import org.vclipse.configscan.vcmlt.vcmlT.CheckBomItemExists;
import org.vclipse.configscan.vcmlt.vcmlT.CheckBomItemQty;
import org.vclipse.configscan.vcmlt.vcmlT.CheckComplete;
import org.vclipse.configscan.vcmlt.vcmlT.CheckConflict;
import org.vclipse.configscan.vcmlt.vcmlT.CheckDomain;
import org.vclipse.configscan.vcmlt.vcmlT.CheckSingleValue;
import org.vclipse.configscan.vcmlt.vcmlT.CheckStatus;
import org.vclipse.configscan.vcmlt.vcmlT.CsticState;
import org.vclipse.configscan.vcmlt.vcmlT.DomainValue;
import org.vclipse.configscan.vcmlt.vcmlT.Mode;
import org.vclipse.configscan.vcmlt.vcmlT.Model;
import org.vclipse.configscan.vcmlt.vcmlT.NumericInterval;
import org.vclipse.configscan.vcmlt.vcmlT.SetValue;
import org.vclipse.configscan.vcmlt.vcmlT.Status;
import org.vclipse.configscan.vcmlt.vcmlT.TestCase;
import org.vclipse.configscan.vcmlt.vcmlT.TestGroup;
import org.vclipse.configscan.vcmlt.vcmlT.util.VcmlTSwitch;
import org.vclipse.vcml.vcml.Literal;
import org.vclipse.vcml.vcml.NumericLiteral;
import org.vclipse.vcml.vcml.SymbolicLiteral;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.ProcessingInstruction;

public class VcmlTConfigScanXMLProvider extends VcmlTSwitch<Object> implements IConfigScanXMLProvider {

	private Document doc;
	private Map<Element, URI> map;
	private Element current;
	private DocumentBuilder xmlDocBuilder;
	
	private ITestObjectFilter filter;
	
	@Override
	public Document transform(EObject model, ITestObjectFilter filter, Map<Element, URI> map, Map<Object, Object> options) {
		try {
			this.xmlDocBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		this.doc = xmlDocBuilder.newDocument();
		this.map = map;
		this.current = null;
		this.filter = filter;
		doSwitch(model);
		
		return doc;
	}

	@Override
	public Object caseModel(Model model) {
		Element root = doc.createElement("configtest");
		String materialid = model.getTestcase().getItem().getName();
		String document = model.getTestcase().getDocument();
		String documentname = (document != null) ? document : materialid;
		String documentdescription = model.getTestcase().getDescription();
		String documentdescr = (documentdescription != null) ? documentdescription : documentname;
	// ToDo: append material description to comment
		Comment comment = doc.createComment(" " + materialid + " ");
		root.appendChild(doc.appendChild(comment));
		ProcessingInstruction pi1 = doc.createProcessingInstruction("configscan-upload", "materialid " + materialid);
		ProcessingInstruction pi2 = doc.createProcessingInstruction("configscan-upload", "documentname " + documentname);
		ProcessingInstruction pi3 = doc.createProcessingInstruction("configscan-upload", "documentdescription " + documentdescr);
		root.appendChild(doc.appendChild(pi1));
		root.appendChild(doc.appendChild(pi2));
		root.appendChild(doc.appendChild(pi3));
//		Node docElement = doc.getDocumentElement();
//		System.out.print(doc.getFirstChild().toString());
//		System.out.print(docElement);
//		System.out.print(root.getParentNode());  // null
//		element.insertBefore(pi, element);  // null
//		root.getParentNode().insertBefore(pi, root);
//		root.insertBefore(pi, node1);
//		root.insertBefore(node1, root);
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
		tg.setAttribute("status", toXML(testGroup.getStatus()));
		tg.setAttribute("testmode", toXML(testGroup.getMode()));
		
		map.put(tg, EcoreUtil.getURI(testGroup));
		current.appendChild(tg);
		current = tg;

		// ...
		
	    for (final Action action : testGroup.getActions()) {
			doSwitch(action);
			current = tg;
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
	// ToDo: NumericInterval (less important, rarely used)
	// ToDo: operator 'NE'
	public Object caseCheckSingleValue (final CheckSingleValue object) {
		if (object.getValue() != null) {
			Element ec = doc.createElement("checksinglevalue");
			ec.setAttribute("name", (object.getCstic()).getName());
			String prefix = "";
			if ((Literal)object.getValue() instanceof NumericLiteral) {
				String op = object.getOperator().getName();
				if (op.equals("LT")) {
					prefix = "< ";				
				}
				else if (op.equals("LE")) {
					prefix = "<= ";				
				}
				else if (op.equals("GT")) {
					prefix = "> ";				
				}
				else if (op.equals("GE")) {
					prefix = ">= ";				
				}
			}
			else if (object.getValue() instanceof NumericInterval) {
				return this;
			}
			ec.setAttribute("value", prefix + getValue((Literal)object.getValue()));
			ec.setAttribute("bompath", getChildPath(object.getBompath()));
			map.put(ec, EcoreUtil.getURI(object));
			current.appendChild(ec);
		}
		return this;
	}
	
	@Override
	public Object caseCheckStatus(final CheckStatus object) {
		Element ec = doc.createElement("checkstatus");
		ec.setAttribute("name", (object.getCstic()).getName());
		ec.setAttribute("bompath", getChildPath(object.getBompath()));
		
		map.put(ec, EcoreUtil.getURI(object));
		current.appendChild(ec);
		
		for (final CsticState status : object.getStatus()) {
			Element cs = doc.createElement("status");
			cs.appendChild(doc.createTextNode(toXML(status)));
			map.put(cs, EcoreUtil.getURI(object));
			ec.appendChild(cs);
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
	public Object caseCheckBomItemExists (final CheckBomItemExists object) {
		Element e = doc.createElement("checkitemexist");
		if (object.isExists())
			e.setAttribute("value", "TRUE");
		else
			e.setAttribute("value", "FALSE");
		e.setAttribute("bompath", getChildPath(object.getBompath()));
		
		map.put(e, EcoreUtil.getURI(object));
		current.appendChild(e);
		return this;
	}

	// ToDo: "mode" "out" must not be applied with CheckDomain strict (applies also to cmlt)
	@Override
	public Object caseCheckDomain (final CheckDomain object) {
		Element e = doc.createElement("checkdomain");
		e.setAttribute("name", object.getCstic().getName());
		e.setAttribute("bompath", getChildPath(object.getBompath()));
		e.setAttribute("strict", object.isStrict() ? "TRUE" : "FALSE");
		EList<DomainValue> vs = object.getValues();
		if (vs != null) {
			Iterator<DomainValue> vsi = vs.iterator();
			while (vsi.hasNext()) {
				Element ev = doc.createElement("values");
				DomainValue val = vsi.next();
				ev.setAttribute("value", getValue(val.getLiteral()));
				if (!object.isStrict()) {
					if (val.isNo())
						ev.setAttribute("mode", "out");
					else
						ev.setAttribute("mode", "in");
				}
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

	private String getValue(Literal lit) {
		if (lit instanceof SymbolicLiteral) {
			String str = ((SymbolicLiteral)lit).getValue();
			// strips also off surrounding quotes
			return (str);
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
	public String getMaterialNumber(EObject obj) {
		if (!(obj instanceof Model)) {
			throw new IllegalArgumentException("not a Model: " + obj);
		}
		Model model = (Model) obj;
		TestCase testcase = model.getTestcase();
		if (testcase==null) {
			throw new IllegalArgumentException("no testcase element found");
		}
		return testcase.getItem().getName();
	}

	// ToDo: common toXML methods for cmlt and vcmlt
	private String toXML(final Mode mode) {
		switch (mode) {
		case SUCCESS:
			return "success";
		case FAILURE:
			return "failure";
		}
		return null;
	}
	
	private String toXML(final Status status) {
		switch (status) {
		case IN_PREPARATION:
			return "0";
		case RELEASED:
			return "1";
		case LOCKED:
			return "2";
		}
		return null;
	}

	private String toXML(final CsticState status) {
		if (status.equals(CsticState.INVISIBLE)) {
			return "HIDDEN";
		}
		return status.toString();
	}
}
