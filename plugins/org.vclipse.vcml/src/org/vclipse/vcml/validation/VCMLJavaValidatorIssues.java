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

import org.eclipse.xtext.diagnostics.Severity;
import org.eclipse.xtext.validation.Check;
import org.eclipse.xtext.validation.CheckType;
import org.eclipse.xtext.validation.Issue.IssueImpl;
import org.vclipse.vcml.vcml.VCObject;

import com.google.common.collect.Maps;

public class VCMLJavaValidatorIssues extends AbstractVCMLJavaValidator {

	private Map<String, IssueImpl> name2Issue;
	
	public VCMLJavaValidatorIssues() {
		name2Issue = Maps.newHashMap();
	}
	
	public void setIssues(Map<String, IssueImpl> issues) {
		name2Issue = issues;
	}
	
	@Check(CheckType.FAST)
	public void checkIssues(VCObject object) {
		String name = object.getName();
		if(name2Issue.containsKey(name)) {
			IssueImpl issue = name2Issue.get(name);
			if(Severity.ERROR == issue.getSeverity()) {
				String[] data = issue.getData();
				error(issue.getMessage(), object, object.eClass().getEStructuralFeature(data[2]), issue.getCode(), data);
			}
		}
	}
}