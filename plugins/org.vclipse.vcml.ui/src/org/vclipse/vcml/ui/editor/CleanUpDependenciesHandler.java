package org.vclipse.vcml.ui.editor;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.expressions.EvaluationContext;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.xtext.naming.IQualifiedNameProvider;
import org.eclipse.xtext.resource.SaveOptions;
import org.eclipse.xtext.ui.editor.XtextEditor;
import org.eclipse.xtext.ui.editor.utils.EditorUtils;
import org.vclipse.base.ui.BaseUiPlugin;
import org.vclipse.base.ui.util.VClipseResourceUtil;
import org.vclipse.vcml.utils.DependencySourceUtils;
import org.vclipse.vcml.vcml.Dependency;
import org.vclipse.vcml.vcml.Model;
import org.vclipse.vcml.vcml.VCObject;

import com.google.common.collect.Sets;
import com.google.inject.Inject;

public class CleanUpDependenciesHandler extends AbstractHandler {

	private Logger logger = Logger.getLogger(CleanUpDependenciesHandler.class);
	
	@Inject
	private VClipseResourceUtil resourceUtil;
	
	@Inject
	private VcmlResourceContainerState containerState;
	
	@Inject
	private IQualifiedNameProvider nameProvider;
	
	@Inject
	private DependencySourceUtils sourceUtils;
	
	public Object execute(ExecutionEvent event) throws ExecutionException {
		Object appContext = event.getApplicationContext();
		if(appContext instanceof EvaluationContext) {
			Object defVariable = ((EvaluationContext)appContext).getDefaultVariable();
			if(defVariable instanceof List<?>) {
				for(Object entry : (List<?>)defVariable) {
					if(entry instanceof IContainer) {
						handleContainer(entry);
					}
				}
			} else if(defVariable instanceof Set<?>) {
				for(Object entry : ((Set<?>)defVariable)) {
					if(entry instanceof ITextSelection) {
						XtextEditor xtextEditor = EditorUtils.getActiveXtextEditor(event);
						IResource resource = xtextEditor.getResource();
						if(resource instanceof IFile) {
							handleFile(resource);
						}
					}
				}
			}
		}
		return null;
	}

	protected void handleFile(IResource resource) {
		IFile file = (IFile)resource;
		URI uri = URI.createPlatformResourceURI(file.getFullPath().toString(), true);
		executeOn(uri);
	}

	protected void handleContainer(Object entry) {
		String folderPath = ((IContainer)entry).getFullPath().toString();
		URI folderUri = URI.createPlatformResourceURI(folderPath, true);
		Collection<URI> containedURIs = containerState.getContainedURIs(folderUri.toString());
		for(URI uri : containedURIs) {
			executeOn(uri);
		}
	}

	protected void executeOn(URI uri) {
		String fileExtension = uri.fileExtension();
		if(DependencySourceUtils.EXTENSION_VCML.equals(fileExtension)) {
			Resource resource = resourceUtil.getResourceSet().getResource(uri, true);
			String uriStringNoExtension = uri.trimFileExtension().toString();
			String sourceFolderUri = uriStringNoExtension.concat(DependencySourceUtils.SUFFIX_SOURCEFOLDER);
			Collection<URI> containedURIs = containerState.getContainedURIs(sourceFolderUri);
			EList<EObject> contents = resource.getContents();
			Set<String> dependencyNames = Sets.newHashSet();
			if(!contents.isEmpty()) {
				EObject topObject = contents.get(0);
				if(topObject instanceof Model) {
					for(VCObject vcobject : ((Model)topObject).getObjects()) {
						if(vcobject instanceof Dependency) {
							dependencyNames.add(nameProvider.getFullyQualifiedName(vcobject).getLastSegment());
						}
					}
					ResourceSetImpl resourceSet = new ResourceSetImpl();
					for(URI currentDependencyUri : containedURIs) {
						String dependencyFileName = currentDependencyUri.trimFileExtension().lastSegment();
						if(!dependencyNames.contains(dependencyFileName)) {
							Resource resourceToDelete = resourceSet.getResource(currentDependencyUri, true);
							try {
								resourceToDelete.delete(SaveOptions.defaultOptions().toOptionsMap());
							} catch(IOException exception) {
								logger.error(exception.getMessage());
								BaseUiPlugin.log(exception.getMessage(), exception);
							}
						}
					}
				}
			}
		} else if(DependencySourceUtils.EXTENSION_CONSTRAINT.equals(fileExtension) ||
					DependencySourceUtils.EXTENSION_PRECONDITION.equals(fileExtension) ||
						DependencySourceUtils.EXTENSION_PROCEDURE.equals(fileExtension) ||
							DependencySourceUtils.EXTENSION_SELECTIONCONDITION.equals(fileExtension)) {
			VCObject dependency = sourceUtils.getDependency(uri);
			if(dependency == null) {
				try {
					Resource resource = resourceUtil.getResourceSet().getResource(uri, true);
					resource.delete(SaveOptions.defaultOptions().toOptionsMap());
				} catch(IOException exception) {
					logger.error(exception.getMessage());
					BaseUiPlugin.log(exception.getMessage(), exception);
				}
			}
		}
	}
}
