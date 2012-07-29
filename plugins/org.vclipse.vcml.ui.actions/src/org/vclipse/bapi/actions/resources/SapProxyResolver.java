package org.vclipse.bapi.actions.resources;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.CrossReference;
import org.eclipse.xtext.nodemodel.ICompositeNode;
import org.eclipse.xtext.nodemodel.INode;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;
import org.vclipse.bapi.actions.characteristic.CharacteristicReader;
import org.vclipse.bapi.actions.classes.ClassReader;
import org.vclipse.bapi.actions.variantfunction.VariantFunctionReader;
import org.vclipse.bapi.actions.varianttable.VariantTableReader;
import org.vclipse.vcml.vcml.Function;
import org.vclipse.vcml.vcml.Option;
import org.vclipse.vcml.vcml.PFunction;
import org.vclipse.vcml.vcml.Table;
import org.vclipse.vcml.vcml.VcmlModel;
import org.vclipse.vcml.vcml.VcmlPackage;

import com.google.inject.Inject;
import com.sap.conn.jco.JCoException;

public class SapProxyResolver {

	@Inject
	private CharacteristicReader csticReader;

	@Inject
	private ClassReader classReader;

	@Inject
	private VariantFunctionReader variantFunctionReader;

	@Inject
	private VariantTableReader variantTableReader;

	public void extractFromSource(EObject source, VcmlModel model, IProgressMonitor monitor, Set<String> seenObjects, List<Option> options) throws JCoException {
		TreeIterator<EObject> iterator = source.eAllContents();
		while (iterator.hasNext()) {
			EObject eObject = iterator.next();
			if (eObject instanceof Table || eObject instanceof Function || eObject instanceof PFunction) {
				ICompositeNode node = NodeModelUtils.getNode(eObject);
				Iterator<INode> nodeIterator = node.getChildren().iterator();
				while(nodeIterator.hasNext()) {
					INode childNode = nodeIterator.next();
					if (childNode.getGrammarElement() instanceof CrossReference) {
						CrossReference ref = (CrossReference) childNode.getGrammarElement();
						String name = NodeModelUtils.getTokenText(childNode);
						EClassifier classifier = ref.getType().getClassifier();
						if (VcmlPackage.Literals.CHARACTERISTIC == classifier) {
							csticReader.read(name, model, monitor, seenObjects, options, true);
						} else if (VcmlPackage.Literals.CLASS == classifier) {
							classReader.read(name, model, monitor, seenObjects, options, true);
						} else if (VcmlPackage.Literals.VARIANT_FUNCTION == classifier) {
							variantFunctionReader.read(name, model, monitor, seenObjects, options, true);
						} else if (VcmlPackage.Literals.VARIANT_TABLE == classifier) {
							variantTableReader.read(name, model, monitor, seenObjects, options, true);
						}
					}
				}
			}
		}
	}
	
}
