/*******************************************************************************
 * Copyright (c) 2010 webXcerpt Software GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    webXcerpt Software GmbH - initial creator
 ******************************************************************************/
package org.vclipse.vcml.validation;

import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.diagnostics.Severity;
import org.eclipse.xtext.nodemodel.INode;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;
import org.eclipse.xtext.util.Strings;
import org.eclipse.xtext.validation.Check;
import org.eclipse.xtext.validation.CheckType;
import org.eclipse.xtext.validation.Issue.IssueImpl;
import org.vclipse.vcml.vcml.VCObject;

import com.google.common.collect.Maps;

public class VCMLJavaValidatorIssues extends AbstractVCMLJavaValidator {

	private Map<String, IssueImpl> name2Issue;
	private Resource resource;
	
	public VCMLJavaValidatorIssues() {
		name2Issue = Maps.newHashMap();
	}
	
	public void setIssues(Map<String, IssueImpl> issues) {
		name2Issue = issues;
	}
	
	public void setResource(Resource resultResource) {
		this.resource = resultResource;
	}
	
	@Check(CheckType.FAST)
	public void checkIssues(VCObject object) {
		String name = object.getName();
		if(name2Issue.containsKey(name) && resource != null) {
			IssueImpl issue = name2Issue.get(name);
			if(Severity.ERROR == issue.getSeverity()) {
				String[] data = issue.getData();
				if(data.length == 3) {
					error(issue.getMessage(), object, object.eClass().getEStructuralFeature(data[2]), issue.getCode(), data);										
				} else if(data.length == 4) {
					EObject contained = resource.getResourceSet().getEObject(URI.createURI(data[3]), true);
					EStructuralFeature feature = contained.eClass().getEStructuralFeature(data[2]);
					String value = feature.getName() + " " + contained.eGet(feature).toString();
					for(INode currentNode : NodeModelUtils.getNode(object).getChildren()) {
						String text = currentNode.getText();
						if(text.contains(value)) {
							int indexOf = text.indexOf(value);
							getMessageAcceptor().acceptError(issue.getMessage(), object, 
									(currentNode.getOffset() + indexOf) - (Strings.countLines(text.substring(0, indexOf)) + 1) , value.length(), issue.getCode(), data);							
						}
					}
				}
			}
		}
	}
}