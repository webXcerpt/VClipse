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

import java.util.Iterator;

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
import org.vclipse.vcml.vcml.DependencyNet;
import org.vclipse.vcml.vcml.VCObject;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

public class VCMLJavaValidatorIssues extends AbstractVCMLJavaValidator {

	private Multimap<String, IssueImpl> name2Issue;
	private Resource resource;
	
	public VCMLJavaValidatorIssues() {
		name2Issue = HashMultimap.create();
	}
	
	public void setIssues(Multimap<String, IssueImpl> issues) {
		name2Issue = issues;
	}
	
	public void setResource(Resource resultResource) {
		this.resource = resultResource;
	}
	
	@Check(CheckType.FAST)
	public void checkIssues(VCObject object) {
		if(resource != null) {
			String name = object.getName();
			if(name2Issue.containsKey(name)) {
				if(object.eResource().getURI().equals(resource.getURI())) {
					for(String key : name2Issue.keySet()) {
						if(key.equals(name)) {
							Iterator<IssueImpl> iterator = name2Issue.get(key).iterator();
							while(iterator.hasNext()) {
								IssueImpl issueImpl = iterator.next();
								if(Severity.ERROR == issueImpl.getSeverity()) {
									String[] data = issueImpl.getData();
									if(data.length == 3) {
										error(issueImpl.getMessage(), object, object.eClass().getEStructuralFeature(data[2]), issueImpl.getCode(), data);										
									} else if(data.length == 4) {
										EObject newStateObject = resource.getResourceSet().getEObject(URI.createURI(data[1]), true);
										try {
											int index = Integer.parseInt(data[3]);
											getMessageAcceptor().acceptError(issueImpl.getMessage(), object,
													newStateObject.eClass().getEStructuralFeature(data[2]),
														index, issueImpl.getCode(), issueImpl.getData());
										} catch(Exception exception) {
											URI createURI = URI.createURI(data[3]);
											EObject contained = resource.getResourceSet().getEObject(createURI, true);
											EStructuralFeature feature = contained.eClass().getEStructuralFeature(data[2]);
											String value = feature.getName();
											for(INode currentNode : NodeModelUtils.getNode(object).getChildren()) {
												String text = currentNode.getText();
												if(text.contains(value)) {
													int indexOf = text.indexOf(value);
													getMessageAcceptor().acceptError(issueImpl.getMessage(), object, 
															(currentNode.getOffset() + indexOf) - (Strings.countLines(text.substring(0, indexOf)) + 1) , value.length(), issueImpl.getCode(), data);							
												}
											}
										}
									}
								}
								if(!(object instanceof DependencyNet)) {
									break;									
								}
							}
						}
					}
				}
			}
		}
	}
}