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
package org.vclipse.refactoring.tests;

import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.ui.editor.findrefs.IReferenceFinder;
import org.eclipselabs.xtext.utils.unittesting.XtextTest;
import org.vclipse.refactoring.utils.Configuration;
import org.vclipse.refactoring.utils.EntrySearch;
import org.vclipse.refactoring.utils.Extensions;
import org.vclipse.refactoring.utils.Labels;
import org.vclipse.refactoring.utils.References;

import com.google.common.collect.Lists;

public class RefactoringTest extends XtextTest {

	protected static final String RESOURCES_PATH = "org.vclipse.refactoring.tests/resources/";
	
	protected Configuration configuration;
	protected Extensions extensions;
	protected EntrySearch search;
	protected Labels labels;
	protected References references;
	protected List<EObject> contents;
	
	public RefactoringTest(String testname) {
		super(testname);
		configuration = new Configuration();
		extensions = new Extensions(configuration);
		search = new EntrySearch(extensions);
		labels = new Labels(extensions);
		contents = Lists.newArrayList();
		loadContents("car_description.vcml", contents);
		EObject entry = contents.get(contents.size() / 2);
		IReferenceFinder finder = extensions.getInstance(IReferenceFinder.class, entry);
		references = new References(finder);
	}
	
	protected void loadContents(String filename, List<EObject> contents) {
		String path = RESOURCES_PATH + filename;
		URI uri = URI.createPlatformPluginURI(path, true);
		Resource resource = resourceSet.getResource(uri, true);
		EList<EObject> resourceContents = resource.getContents();
		if(!resourceContents.isEmpty()) {
			EObject object = resourceContents.get(0);
			contents.add(object);
			List<EObject> entries = Lists.newArrayList(object.eAllContents());
			contents.addAll(entries);
		}
	}
}
