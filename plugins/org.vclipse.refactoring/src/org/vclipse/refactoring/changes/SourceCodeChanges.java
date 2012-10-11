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
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.eclipse.compare.structuremergeviewer.Differencer;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
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
import org.eclipse.xtext.naming.IQualifiedNameProvider;
import org.eclipse.xtext.naming.QualifiedName;
import org.eclipse.xtext.parser.IParseResult;
import org.eclipse.xtext.parser.IParser;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.resource.impl.ListBasedDiagnosticConsumer;
import org.eclipse.xtext.serializer.ISerializer;
import org.eclipse.xtext.util.concurrent.IUnitOfWork;
import org.vclipse.base.ui.compare.EObjectTypedElement;
import org.vclipse.refactoring.IPreviewProvider;
import org.vclipse.refactoring.IRefactoringUIContext;
import org.vclipse.refactoring.RefactoringPlugin;
import org.vclipse.refactoring.core.DiffNode;
import org.vclipse.refactoring.core.RefactoringRunner;
import org.vclipse.refactoring.ui.UIRefactoringContext;
import org.vclipse.refactoring.utils.RefactoringUtility;

import com.google.common.collect.Lists;
import com.google.common.io.ByteStreams;
import com.google.common.io.InputSupplier;

public class SourceCodeChanges extends CompositeChange implements IPreviewProvider {
	
	private RefactoringRunner runner;
	private RefactoringUtility utility;
	
	private EObject rootOriginal;
	private EObject rootCopy;
	private EObject refactoredRoot;
	
	private DiffNode previewNode;
	private IRefactoringUIContext context;
	
	private final ISerializer serializer;
	private final IQualifiedNameProvider nameProvider;
	private final IParser parser;
	private final ILinker linker;
	
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
		
		serializer = utility.getInstance(rootOriginal, ISerializer.class);
		nameProvider = utility.getInstance(rootOriginal, IQualifiedNameProvider.class);
		parser = utility.getInstance(rootOriginal, IParser.class);
		linker = utility.getInstance(rootOriginal, ILinker.class);
	}
	
	@Override
	public Object getModifiedElement() {
		return refactoredRoot;
	}
	
	public DiffNode getPreview() {
		return previewNode;
	}
	
	@Override
 	public Change perform(IProgressMonitor pm) throws CoreException {
		if(!performed) {
			pm.beginTask("Initialising re-factoring operation", IProgressMonitor.UNKNOWN);
			
			// create new DiffNode, remove ChangeNodes
			previewNode = new DiffNode(Differencer.CHANGE);
			this.clear();
				
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
			
			EObject changed = refactoringContext.getSourceElement();
			refactoredRoot = EcoreUtil.getRootContainer(changed);
			
			previewNode.setLeft(
					new EObjectTypedElement(rootOriginal, serializer));
			
			previewNode.setRight(
					new EObjectTypedElement(refactoredRoot, serializer));
			
			handleChanges(refactoringContext);
			
			if(getChildren().length == 0) {
				add(new NoChange(null));
			}
			pm.done();
		}
		performed = true;
		return null;
	}
	
	public RefactoringStatus refactor(final IProgressMonitor pm) throws CoreException {
		if(isEnabled()) {
			pm.beginTask("Executing re-factoring", IProgressMonitor.UNKNOWN);
			context.getDocument().modify(new IUnitOfWork<EObject, XtextResource>() {
				@Override
				public EObject exec(XtextResource state) throws Exception {
					for(Change change : Lists.newArrayList(getChildren())) {
						if(change instanceof SourceCodeChange) {
							SourceCodeChange fragment = (SourceCodeChange)change;
							if(fragment.isEnabled()) {
								try {
									fragment.refactor(pm);							
								} catch(CoreException exception) {
									exception.printStackTrace();
									RefactoringPlugin.log(exception.getMessage(), exception);
									continue;
								}
							}					
						}
					}
					return null;
				}
			});
			pm.done();
		}
		return RefactoringStatus.create(Status.OK_STATUS);
	}
	
	protected void handleChanges(IRefactoringUIContext previewContext) {
		ChangeRecorder changeRecorder = runner.getChangeRecorder();
		ChangeDescription endRecording = changeRecorder.endRecording();

		List<EObject> entries = Lists.newArrayList(rootOriginal.eAllContents());
		entries.add(0, rootOriginal);
		
		InputStreamProvider streamRootNode = InputStreamProvider.getInstance(previewNode);
		for(Entry<EObject, EList<FeatureChange>> entry : endRecording.getObjectChanges().entrySet()) {
			EObject changed = entry.getKey();
			if(changed.eContainer() instanceof ChangeDescription) {
				continue;
			}
			EObject existingEntry = 
					getEqualOriginal(entries, changed);
			
			SourceCodeChange scc = 
					new SourceCodeChange(utility, existingEntry, changed, entry.getValue());
			
			DiffNode preview = scc.getPreview();
			try {
				InputStreamProvider streamCurrentPreviewNode = InputStreamProvider.getInstance(preview);
				if(ByteStreams.equal(streamRootNode, streamCurrentPreviewNode)) {
					markAsSynthetic();
				}
			} catch(IOException exception) {
				RefactoringPlugin.log(exception.getMessage(), exception);
			}
			add(scc);
		}
	}
	
	protected EObject getEqualOriginal(List<EObject> entries, EObject changeOnObject) {
		// search by name and type
		EObject existingEntry = null;
		EClass eclass = changeOnObject.eClass();
		QualifiedName qualifiedName = nameProvider.getFullyQualifiedName(changeOnObject);
		if(qualifiedName == null) {
			Iterator<EObject> iterator = utility.getEntry(entries, eclass).iterator();
			if(iterator.hasNext()) {
				existingEntry = iterator.next();
			}
		} else {
			String segment = qualifiedName.getLastSegment();
			existingEntry = utility.getEntry(entries, segment, eclass);
		}

		// search by type and container type
		if(existingEntry == null) {
			Iterator<EObject> iterator = utility.getEntry(entries, eclass).iterator();
			while(iterator.hasNext()) {
				EObject next = iterator.next();
				if(utility.equalTypeWithContainerType(next, context.getSourceElement())) {
					existingEntry = next;
					break;
				}
			}								
		}
		return existingEntry;
	}

	protected IRefactoringUIContext createRefactoringContext(IRefactoringUIContext context) {
		EObject element = context.getSourceElement();
		rootCopy = rootContainerCopy(element);
		
		List<EObject> entries = Lists.newArrayList(rootCopy.eAllContents());
		entries.add(0, rootCopy);
		QualifiedName qualifiedName = nameProvider.getFullyQualifiedName(element);
		if(qualifiedName == null) {
			EClass eclass = element.eClass();
			Iterator<EObject> iterator = utility.getEntry(entries, eclass).iterator();
			while(iterator.hasNext()) {
				EObject next = iterator.next();
				if(utility.equalTypeWithContainerType(element, next)) {
					element = next;
				}
			}
		} else {
			String name = qualifiedName.getLastSegment();
			element = utility.getEntry(entries, name, element.eClass());
		}
		IRefactoringUIContext changedContext = ((UIRefactoringContext)context).copy();
		if(element != null) {
			changedContext.setSourceElement(element);
			return changedContext;
		}
		return context;
	}
	
	private EObject rootContainerCopy(EObject object) {
		EObject container = EcoreUtil.getRootContainer(object);
		String string = serializer.serialize(container);		
		IParseResult parseResult = parser.parse(new StringReader(string));
		Resource resource = container.eResource();
		ResourceSet resourceSet = resource.getResourceSet();
		URI uri = resource.getURI();
		uri = URI.createURI(uri.toString() + ".preview." + uri.fileExtension());
		try {
			resource = resourceSet.getResource(uri, true);
		} catch(Exception exception) {
			resource = resourceSet.getResource(uri, true);
		}
		resource.getContents().clear();
		resource.getContents().add(parseResult.getRootASTElement());
		final ListBasedDiagnosticConsumer consumer = new ListBasedDiagnosticConsumer();
		linker.linkModel(parseResult.getRootASTElement(), consumer);
		return parseResult.getRootASTElement();
	}
}
