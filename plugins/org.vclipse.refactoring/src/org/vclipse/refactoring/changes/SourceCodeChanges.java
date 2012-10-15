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

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.List;
import java.util.Map.Entry;

import org.eclipse.compare.structuremergeviewer.Differencer;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.change.ChangeDescription;
import org.eclipse.emf.ecore.change.FeatureChange;
import org.eclipse.emf.ecore.change.util.ChangeRecorder;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.CompositeChange;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.xtext.linking.ILinker;
import org.eclipse.xtext.parser.IParseResult;
import org.eclipse.xtext.parser.IParser;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.resource.impl.ListBasedDiagnosticConsumer;
import org.eclipse.xtext.serializer.ISerializer;
import org.eclipse.xtext.util.concurrent.IUnitOfWork;
import org.vclipse.base.ui.compare.EObjectTypedElement;
import org.vclipse.refactoring.IRefactoringUIContext;
import org.vclipse.refactoring.RefactoringPlugin;
import org.vclipse.refactoring.core.DiffNode;
import org.vclipse.refactoring.core.RefactoringRunner;
import org.vclipse.refactoring.ui.UIRefactoringContext;
import org.vclipse.refactoring.utils.RefactoringUtility;

import com.google.common.collect.Lists;
import com.google.common.io.ByteStreams;
import com.google.common.io.InputSupplier;

public class SourceCodeChanges extends CompositeChange {
	
	private RefactoringRunner runner;
	private RefactoringUtility utility;
	
	private EObject rootOriginal;
	private EObject rootCopy;
	
	private DiffNode previewNode;
	private IRefactoringUIContext context;
	
	private List<EObject> rootContents;
	private List<EObject> copyContents;
	
	private final ISerializer serializer;
	
	private boolean performed = false;
	
	private static class InputStreamProvider implements InputSupplier<InputStream> {

		private DiffNode diffNode;
		
		static InputStreamProvider getInstance(DiffNode diffNode) {
			return new InputStreamProvider(diffNode);
		}	
		
		public InputStreamProvider(DiffNode diffNode) {
			this.diffNode = diffNode;
		}
		
		@Override
		public InputStream getInput() throws IOException {
			try {
				EObjectTypedElement left = (EObjectTypedElement)diffNode.getLeft();
				InputStream contents = left.getContents();
				return contents;
			} catch(CoreException exception) {
				return null;
			}
		}
		
	}
	
	static String getChangeLabel(IRefactoringUIContext context) {
		return context.getSourceElement().eResource().getURI().lastSegment();
	}
	
	public SourceCodeChanges(IRefactoringUIContext context, RefactoringRunner runner, RefactoringUtility utility) {
		super("Changes in " + getChangeLabel(context));
		this.context = context;
		this.runner = runner;
		this.utility = utility;
		
		EObject element = this.context.getSourceElement();
		rootOriginal = EcoreUtil.getRootContainer(element);
		
		rootContents = Lists.newArrayList(rootOriginal.eAllContents());
		rootContents.add(0, rootOriginal);
		
		serializer = utility.getInstance(ISerializer.class, rootOriginal);
		IParser parser = utility.getInstance(IParser.class, rootOriginal);
		ILinker linker = utility.getInstance(ILinker.class, rootOriginal);

		String string = serializer.serialize(rootOriginal);		
		IParseResult parseResult = parser.parse(new StringReader(string));
		Resource resource = rootOriginal.eResource();
		ResourceSet resourceSet = resource.getResourceSet();
		URI uri = resource.getURI();
		uri = URI.createURI(uri.trimFileExtension().toString() + ".refactored." + uri.fileExtension());
		try {
			resource = resourceSet.getResource(uri, true);
		} catch(Exception exception) {
			resource = resourceSet.getResource(uri, true);
		}
		resource.getContents().clear();
		rootCopy = parseResult.getRootASTElement();
		copyContents = Lists.newArrayList(rootCopy.eAllContents());
		copyContents.add(0, rootCopy);
		resource.getContents().add(rootCopy);
		
		linker.linkModel(rootCopy, new ListBasedDiagnosticConsumer());
		
		previewNode = new DiffNode(Differencer.CHANGE);
		previewNode.setLeft(new EObjectTypedElement(rootOriginal, serializer));
	}
	
	@Override
	public Object getModifiedElement() {
		return rootCopy;
	}
	
	public DiffNode getDiffNode() {
		return previewNode;
	}
	
	public IRefactoringUIContext getContext() {
		return context;
	}
	
	@Override
 	public Change perform(IProgressMonitor pm) throws CoreException {
		if(!performed) {
			pm.subTask("Initialising re-factoring operation");

			// creates a copy of a model and sets the source element to an equal one in the copied model
			final IRefactoringUIContext refactoringContext = createRefactoringContext(context);
			
			// executes re-factoring on the copy of the model
			context.getDocument().modify(new IUnitOfWork<Void, XtextResource>() {
				@Override
				public java.lang.Void exec(XtextResource resource) throws Exception {
					runner.refactor(refactoringContext);
					return null;
				}
			});
			
			previewNode.setRight(new EObjectTypedElement(rootCopy, serializer));
		
			recordSourceCodeChanges(pm, refactoringContext);
			
			if(getChildren().length == 0) {
				add(new NoChange(null));
			}
			performed = true;
			pm.worked(1);
		}
		return null;
	}
	
	public RefactoringStatus refactor(final IProgressMonitor pm) throws CoreException {
		if(isEnabled()) {
			pm.subTask("Executing re-factoring operation.");
			context.getDocument().modify(new IUnitOfWork<EObject, XtextResource>() {
				@Override
				public EObject exec(XtextResource state) throws Exception {
					List<Change> changes = Lists.newArrayList(getChildren());
					for(Change change : changes) {
						if(change instanceof SourceCodeChange) {
							SourceCodeChange currentChange = (SourceCodeChange)change;
							if(currentChange.isEnabled()) {
								try {
									currentChange.refactor(pm);							
								} catch(CoreException exception) {
									RefactoringPlugin.log(exception.getMessage(), exception);
									continue;
								}
							}
							pm.worked(1);
						}
					}
					return null;
				}
			});
		}
		return RefactoringStatus.create(Status.OK_STATUS);
	}
	
	private void recordSourceCodeChanges(IProgressMonitor pm, IRefactoringUIContext previewContext) {
		pm.subTask("Recording source code changes.");
		ChangeRecorder changeRecorder = runner.getChangeRecorder();
		ChangeDescription endRecording = changeRecorder.endRecording();

		List<SourceCodeChange> sourceCodeChanges = Lists.newArrayList();
		InputStreamProvider streamRootNode = InputStreamProvider.getInstance(previewNode);
		for(Entry<EObject, EList<FeatureChange>> entry : endRecording.getObjectChanges().entrySet()) {
			EObject changed = entry.getKey();
			if(changed.eContainer() instanceof ChangeDescription) {
				continue;
			}
			EObject existingEntry = utility.findEntry(changed, rootContents);
			SourceCodeChange scc = new SourceCodeChange(utility, existingEntry, changed, entry.getValue());
			DiffNode preview = scc.getDiffNode();
			try {
				InputStreamProvider currentStream = InputStreamProvider.getInstance(preview);
				if(ByteStreams.equal(streamRootNode, currentStream)) {
					markAsSynthetic();
					sourceCodeChanges.add(0, scc);
					continue;
				}
			} catch(IOException exception) {
				RefactoringPlugin.log(exception.getMessage(), exception);
			}
			sourceCodeChanges.add(scc);
			pm.worked(5);
		}
		for(SourceCodeChange change : sourceCodeChanges) {
			add(change);
			pm.worked(1);
		}
	}
	
	private IRefactoringUIContext createRefactoringContext(final IRefactoringUIContext context) {
		EObject element = context.getSourceElement();
		List<EObject> entries = Lists.newArrayList(rootCopy.eAllContents());
		entries.add(0, rootCopy);
		EObject entry = utility.findEntry(element, entries);
		if(entry != null) {
			UIRefactoringContext uicontext = (UIRefactoringContext)context;
			IRefactoringUIContext changedContext = uicontext.copy();
			changedContext.setSourceElement(entry);
			return changedContext;			
		}
		return context;
	}
}
