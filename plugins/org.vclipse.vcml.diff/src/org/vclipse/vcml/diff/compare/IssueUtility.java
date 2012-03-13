package org.vclipse.vcml.diff.compare;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.xtext.nodemodel.ICompositeNode;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;
import org.eclipse.xtext.resource.XtextResourceSet;
import org.eclipse.xtext.validation.Issue.IssueImpl;

public class IssueUtility {

	private XtextResourceSet resourceSet = new XtextResourceSet();
	
	public void associate(IssueImpl issue, EObject object) {
		String[] data = issue.getData();
		if(data.length > 0) {
			createMessage(issue, object);
			String featureName = data[data.length - 1];
			EStructuralFeature feature = object.eClass().getEStructuralFeature(featureName);
			Object containedObject = object.eGet(feature);
			if(containedObject instanceof EObject) {
				EObject containedEObject = (EObject)containedObject;
				ICompositeNode node = NodeModelUtils.getNode(containedEObject);
				if(node != null) {
					issue.setLength(node.getLength());
					issue.setLineNumber(node.getStartLine());
					issue.setOffset(node.getTotalOffset());
				}
				URI uri = EcoreUtil.getURI(containedEObject);
				String test = uri.toString();
				issue.setUriToProblem(URI.createURI(test));
			}
		}
	}
	
	public void createMessage(IssueImpl issue, EObject object) {
		String[] data = issue.getData();
		if(data.length > 2) {
			String uriOldStateObject = data[0];
			String uriNewStateObject = data[1];
			String featureName = data[2];
			
			EObject oldStateObject = resourceSet.getEObject(URI.createURI(uriOldStateObject), true);
			EObject newStateObject = resourceSet.getEObject(URI.createURI(uriNewStateObject), true);
			EStructuralFeature feature = object.eClass().getEStructuralFeature(featureName);
			
			EObject containedOldState = (EObject)oldStateObject.eGet(feature);
			EObject containedNewState = (EObject)newStateObject.eGet(feature);
			
			Object containedObject = object.eGet(feature);
			if(containedObject instanceof EObject) {
				StringBuffer messageBuffer = new StringBuffer();
				messageBuffer.append("The change of " + featureName + " from ");
				messageBuffer.append("" + containedOldState.eClass().getName());
				messageBuffer.append(" to " + containedNewState.eClass().getName());
				messageBuffer.append(" for a " + oldStateObject.eClass().getName());
				messageBuffer.append(" is not allowed in SAP system.");		
				issue.setMessage(messageBuffer.toString());
			}
		}
	}
}
