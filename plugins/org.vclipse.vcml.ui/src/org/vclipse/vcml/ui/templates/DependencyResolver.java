/*******************************************************************************
 * Copyright (c) 2010 - 2013 webXcerpt Software GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *  
 * Contributors:
 *     	webXcerpt Software GmbH - initial creator
 * 		www.webxcerpt.com
 ******************************************************************************/
package org.vclipse.vcml.ui.templates;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.text.templates.TemplateVariable;
import org.eclipse.xtext.resource.IResourceDescription;
import org.eclipse.xtext.resource.IResourceDescriptions;
import org.eclipse.xtext.ui.editor.templates.AbstractTemplateVariableResolver;
import org.eclipse.xtext.ui.editor.templates.XtextTemplateContext;
import org.vclipse.vcml.utils.DependencySourceUtils;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.inject.Inject;

public class DependencyResolver extends AbstractTemplateVariableResolver {
	
	private static final String DEFAULT_VALUE_IF_EMPTY = "${Name}";

	public static final String VARIABLE_NAME = "Dependency";
	
	@Inject
	private IResourceDescriptions index;

	public DependencyResolver() {
		super(VARIABLE_NAME, "Values for a dependency variable");
	}
	
	@Override
	public List<String> resolveValues(TemplateVariable variable, XtextTemplateContext xtextTemplateContext) {
		List<String> values = Lists.newArrayList();
		EObject rootModel = xtextTemplateContext.getContentAssistContext().getCurrentModel();
		if(rootModel == null) {
			return values;
		}
		final URI fileUri = URI.createURI(rootModel.eResource().getURI().trimFileExtension().toString().concat(DependencySourceUtils.SUFFIX_SOURCEFOLDER));
				
		final HashSet<String> interestingExtensions = Sets.newHashSet(DependencySourceUtils.EXTENSION_CONSTRAINT, DependencySourceUtils.EXTENSION_PRECONDITION, 
				DependencySourceUtils.EXTENSION_PROCEDURE, DependencySourceUtils.EXTENSION_SELECTIONCONDITION);
		
		Iterator<IResourceDescription> filter = Iterables.filter(index.getAllResourceDescriptions(), new Predicate<IResourceDescription>() {
			public boolean apply(IResourceDescription input) {
				URI uri = input.getURI();
				String fileExtension = uri.fileExtension();
				uri = uri.trimSegments(1);
				return interestingExtensions.contains(fileExtension) && fileUri.equals(uri);
			}
		}).iterator();
		
		while(filter.hasNext()) {
			values.add(filter.next().getURI().trimFileExtension().lastSegment());
		}
		if(values.isEmpty()) {
			values.add(DEFAULT_VALUE_IF_EMPTY);
		}
		return values;
	}
}
