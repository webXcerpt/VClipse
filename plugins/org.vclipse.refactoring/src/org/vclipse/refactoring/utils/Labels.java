/*******************************************************************************
 * Copyright (c) 2012 webXcerpt Software GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *  
 * Contributors:
 *     webXcerpt Software GmbH - initial creator
 ******************************************************************************/
package org.vclipse.refactoring.utils;

import java.util.List;
import java.util.Set;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.naming.IQualifiedNameProvider;
import org.eclipse.xtext.naming.QualifiedName;
import org.vclipse.base.VClipseStrings;
import org.vclipse.refactoring.IRefactoringContext;
import org.vclipse.refactoring.core.RefactoringType;

import com.google.common.collect.Sets;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class Labels {

	private Extensions extensions;
	
	@Inject
	public Labels(Extensions extensions) {
		this.extensions = extensions;
	}
	
	/*
	 * Text shown in the menu
	 */
	public String getUILabel(IRefactoringContext context) {
		StringBuffer buffer = new StringBuffer();
		RefactoringType type = context.getType();
		buffer.append(type.name());
		buffer.append(" ");
		if(context.getStructuralFeature() != null) {
			String featureName = context.getStructuralFeature().getName();
			appendToBuffer(buffer, featureName, true);
		}
		if(RefactoringType.Replace == type) {
			buffer.append(" by ");
			String typeName = context.getSourceElement().eClass().getName();
			appendToBuffer(buffer, typeName, false);
		}
		return buffer.toString();
	}
	
	public Set<String> namesForEntries(List<EObject> entries) {
		Set<String> names = Sets.newHashSet();
		if(!entries.isEmpty()) {
			EObject object = entries.get(0);
			IQualifiedNameProvider nameProvider = extensions.getInstance(IQualifiedNameProvider.class, object);
			if(nameProvider != null) {
				for(EObject value : entries) {
					QualifiedName qualifiedName = nameProvider.apply(value);
					if(qualifiedName != null) {
						String name = qualifiedName.getLastSegment();
						if(name != null) {
							names.add(name);						
						}						
					}
				}
			}
		}
		return names;
	}
	
	protected void appendToBuffer(StringBuffer buffer, String text, boolean handleLastIndex) {
		List<String> parts = VClipseStrings.splitCamelCase(text);
		for(String part : parts) {
			buffer.append(part.toLowerCase());
			int indexOf = parts.indexOf(part);
			if(indexOf < parts.size()) {
				buffer.append(" ");
			}
		}
	}
}
