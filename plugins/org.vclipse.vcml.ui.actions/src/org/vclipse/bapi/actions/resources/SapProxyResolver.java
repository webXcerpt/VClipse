package org.vclipse.bapi.actions.resources;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.AbstractElement;
import org.eclipse.xtext.CrossReference;
import org.eclipse.xtext.GrammarUtil;
import org.eclipse.xtext.nodemodel.BidiTreeIterator;
import org.eclipse.xtext.nodemodel.ICompositeNode;
import org.eclipse.xtext.nodemodel.INode;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;
import org.vclipse.bapi.actions.ContributionReader;
import org.vclipse.bapi.actions.IBAPIActionRunner;
import org.vclipse.bapi.actions.handler.BAPIActionUtils;
import org.vclipse.vcml.vcml.Option;
import org.vclipse.vcml.vcml.VcmlPackage;

import com.google.inject.Inject;

public class SapProxyResolver {

	@Inject
	private ContributionReader extensionPointReader;
	
	public void resolveProxies(EObject object, Set<String> seenObjects, Resource output, List<Option> options) {
		ICompositeNode node = NodeModelUtils.getNode(object);
		if(node != null) {
			for(INode childNode : node.getChildren()) {
				EObject grammarElement = childNode.getGrammarElement();
				if(grammarElement instanceof AbstractElement) {
					BidiTreeIterator<INode> iterator = childNode.getAsTreeIterable().iterator();
					while(iterator.hasNext()) {
						INode nextLevelChildNode = iterator.next();
						EObject nextLevelGrammarElement = nextLevelChildNode.getGrammarElement();
						if(nextLevelGrammarElement instanceof CrossReference) {
							EObject semanticElement = nextLevelChildNode.getSemanticElement();
							CrossReference crossRef = (CrossReference)nextLevelGrammarElement;
							EReference reference = GrammarUtil.getReference(crossRef, semanticElement.eClass());
							Object referenceValue = semanticElement.eGet(reference);
							if(referenceValue instanceof EObject) {
								resolve(object, seenObjects, output, options, nextLevelChildNode, referenceValue);
							} else if(referenceValue instanceof List<?>) {
								for(Object curObject : (List<?>)referenceValue) {
									resolve(object, seenObjects, output, options, nextLevelChildNode, curObject);
								}
							}
						}
					}
				}
			}
		}
	}

	protected void resolve(EObject object, Set<String> seenObjects,Resource output, List<Option> options, INode nextLevelChildNode, Object referenceValue) {
		EObject eobject = (EObject)referenceValue;
		eobject.eSet(VcmlPackage.eINSTANCE.getVCObject_Name(), nextLevelChildNode.getText().trim());
		for(IBAPIActionRunner<?> handler : extensionPointReader.getHandler(eobject.eClass().getInstanceClassName())) {
			if(handler.getClass().getSimpleName().contains("Extract")) {
				try {
					Method method = handler.getClass().getMethod("run", new Class[]{BAPIActionUtils.getInstanceType(eobject), Resource.class, IProgressMonitor.class, Set.class, List.class});
					method.invoke(handler, new Object[]{eobject, output == null ? object.eResource() : output, new NullProgressMonitor(), seenObjects, options});
				} catch(InvocationTargetException exception) {
					if(exception.getTargetException() instanceof BAPIException) {
						break;
					} else {
						exception.printStackTrace();
					}
				} catch(Exception exception) {
					exception.printStackTrace();
				}
			}
		}
	}
}
