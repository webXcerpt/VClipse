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
package org.vclipse.refactoring.changes;

import java.io.StringReader;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.EMap;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.change.ChangeDescription;
import org.eclipse.emf.ecore.change.FeatureChange;
import org.eclipse.emf.ecore.change.ResourceChange;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.URIConverter;
import org.eclipse.emf.ecore.resource.impl.ExtensibleURIConverterImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.CompositeChange;
import org.eclipse.xtext.linking.ILinker;
import org.eclipse.xtext.parser.IParser;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.resource.impl.ListBasedDiagnosticConsumer;
import org.eclipse.xtext.serializer.ISerializer;
import org.eclipse.xtext.util.concurrent.IUnitOfWork;
import org.vclipse.refactoring.IRefactoringUIContext;
import org.vclipse.refactoring.RefactoringPlugin;
import org.vclipse.refactoring.compare.MultipleEntriesTypedElement;
import org.vclipse.refactoring.core.DiffNode;
import org.vclipse.refactoring.core.RefactoringRunner;
import org.vclipse.refactoring.ui.UIRefactoringContext;
import org.vclipse.refactoring.utils.EntrySearch;
import org.vclipse.refactoring.utils.Extensions;

import com.google.common.collect.Lists;
import com.google.inject.Injector;

public class ModelChange extends CompositeChange {

	private final RefactoringRunner runner;
	private final EntrySearch search;
	private final Extensions extensions;
	
	private EObject rootOriginal;
	private EObject rootRefactored;
	
	private DiffNode previewNode;
	private IRefactoringUIContext context;
	
	private List<EObject> rootContents;
	private List<EObject> copyContents;
	
	private final ISerializer serializer;
	private URIConverter uriConverter;
	
	private boolean performed = false;
	
	static String getResourceName(IRefactoringUIContext context) {
		EObject element = context.getSourceElement();
		Resource resource = element.eResource();
		if(resource == null) {
			throw new IllegalArgumentException("Resource should not be null.");
		}
		return resource.getURI().lastSegment();
	}
	
	public ModelChange(IRefactoringUIContext context) {
		super("Re-factoring changes for " + getResourceName(context));
		this.context = context;
		
		Injector injector = RefactoringPlugin.getInstance().getInjector();
		this.extensions = injector.getInstance(Extensions.class);
		this.search = extensions.getInstance(EntrySearch.class);
		this.runner = injector.getInstance(RefactoringRunner.class);
		
		uriConverter = new ExtensibleURIConverterImpl();
		
		EObject element = this.context.getSourceElement();
		rootOriginal = EcoreUtil.getRootContainer(element);
		rootContents = search.getRootContents(rootOriginal);
		
		Resource resource = rootOriginal.eResource();
		
		serializer = extensions.getInstance(ISerializer.class, rootOriginal);
		IParser parser = extensions.getInstance(IParser.class, rootOriginal);
		ILinker linker = extensions.getInstance(ILinker.class, rootOriginal);
		
		String string = serializer.serialize(rootOriginal);		
		URI uri = resource.getURI();
		StringBuffer uriBuffer = new StringBuffer(uri.trimFileExtension().toString()).append(".refactored.").append(uri.fileExtension());
		URI uriRefactoring = URI.createURI(uriBuffer.toString());
		Map<URI, URI> uriMap = uriConverter.getURIMap();
		uriMap.put(uri, uriRefactoring);
		uriMap.put(uriRefactoring, uri);
		
		
		ResourceSet resourceSet = resource.getResourceSet();
		try {
			resource = resourceSet.getResource(uriRefactoring, true);
		} catch(Exception exception) {
			resource = resourceSet.getResource(uriRefactoring, true);
		}
		EList<EObject> contents = resource.getContents();
		contents.clear();
		rootRefactored = parser.parse(new StringReader(string)).getRootASTElement();
		copyContents = search.getRootContents(rootRefactored);
		contents.add(rootRefactored);	
		linker.linkModel(rootRefactored, new ListBasedDiagnosticConsumer());
		EcoreUtil.resolveAll(resource);
		
		previewNode = new DiffNode();
		previewNode.setLeft(new MultipleEntriesTypedElement(serializer, rootOriginal));
	}
	
	public URIConverter getURIConverter() {
		return uriConverter;
	}
	
	@Override
	public Object getModifiedElement() {
		return rootRefactored;
	}
	
	public DiffNode getDiffNode() {
		return previewNode;
	}
	
	public IRefactoringUIContext getContext() {
		return context;
	}
	
	@Override
 	public Change perform(final IProgressMonitor pm) throws CoreException {
		if(!performed) {
			StringBuffer taskBuffer = new StringBuffer("Initialising re-factoring operation for ");
			taskBuffer.append(context.getLabel());
			final SubMonitor sm = SubMonitor.convert(pm, taskBuffer.toString(), 60);

			// creates a copy of a model and sets the source element to an equal one in the copied model
			EObject element = context.getSourceElement();
			EObject entry = search.findEntry(element, copyContents);
			if(entry != null) {
				UIRefactoringContext uicontext = (UIRefactoringContext)context;
				final IRefactoringUIContext refactoringContext = uicontext.copy();
				refactoringContext.setSourceElement(entry);
				runner.refactor(refactoringContext);
				previewNode.setRight(new MultipleEntriesTypedElement(serializer, rootRefactored));
				sm.worked(10);
				recordSourceCodeChanges(sm, refactoringContext);
				sm.worked(20);
				if(getChildren().length == 0) {
					add(new DefaultChange());
				}
				sm.worked(10);
			}
			performed = true;
		} else {
			if(isEnabled()) {
				StringBuffer taskBuffer = new StringBuffer("Executing re-factoring:");
				taskBuffer.append(context.getLabel());
				final List<Change> changes = Lists.newArrayList(getChildren());
				final SubMonitor sm = SubMonitor.convert(pm, taskBuffer.toString(), changes.size());
				context.getDocument().modify(new IUnitOfWork<Object, XtextResource>() {
					@Override
					public Object exec(XtextResource state) throws Exception {
						for(Change change : changes) {
							if(change.isEnabled()) {
								if(change instanceof ModelChangeEntry) {
									ModelChangeEntry entryChange = (ModelChangeEntry)change;
									entryChange.perform(pm);
									entryChange.dispose();
									sm.worked(1);
								}							
							}
						}
						return null;
					}
				});	
			}
			pm.done();
		}
		return null;
	}
	
	private void recordSourceCodeChanges(IProgressMonitor pm, IRefactoringUIContext previewContext) {
		StringBuffer taskBuffer = new StringBuffer("Recording source code changes for re-factoring ");
		taskBuffer.append(previewContext.getLabel());
		ChangeDescription changeDescription = runner.getChangeRecorder().endRecording();
		EMap<EObject, EList<FeatureChange>> objectChanges = changeDescription.getObjectChanges();
		SubMonitor sm = SubMonitor.convert(pm, taskBuffer.toString(), objectChanges.size());
		for(Entry<EObject, EList<FeatureChange>> entry : objectChanges.entrySet()) {
			EObject refactored = entry.getKey();
			if(refactored.eContainer() instanceof ChangeDescription) {
				sm.worked(1);
				continue;
			}
			EList<FeatureChange> featureChanges = entry.getValue();
			search.refactoringConditions(true);
			for(FeatureChange featureChange : featureChanges) {
				EObject existingEntry = search.findEntry(refactored, rootContents);
				ModelChangeEntry scc = new ModelChangeEntry();
				scc.addChange(existingEntry, refactored, featureChange);
				scc.initializeValidationData(pm);
				add(scc);
			}
			search.refactoringConditions(false);
		}
		for(ResourceChange change : changeDescription.getResourceChanges()) {
			org.vclipse.refactoring.changes.ResourceChange resourceChange = new org.vclipse.refactoring.changes.ResourceChange(change.getResource());
			Change parent = getParent();
			if(parent instanceof CompositeChange) {
				CompositeChange compositeParent = (CompositeChange)parent;
				compositeParent.add(resourceChange);
			}
		}
	}
}
