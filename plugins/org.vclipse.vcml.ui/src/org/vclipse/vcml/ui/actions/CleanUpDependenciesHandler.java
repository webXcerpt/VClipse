package org.vclipse.vcml.ui.actions;

import java.io.IOException;
import java.util.Collection;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.e4.ui.workbench.modeling.ExpressionContext;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.xtext.naming.IQualifiedNameProvider;
import org.eclipse.xtext.resource.SaveOptions;
import org.eclipse.xtext.resource.XtextResourceSet;
import org.eclipse.xtext.ui.editor.XtextEditor;
import org.eclipse.xtext.ui.editor.utils.EditorUtils;
import org.vclipse.vcml.ui.VCMLUiPlugin;
import org.vclipse.vcml.ui.internal.VCMLActivator;
import org.vclipse.vcml.ui.resources.VcmlResourcesState;
import org.vclipse.vcml.utils.DependencySourceUtils;
import org.vclipse.vcml.vcml.Dependency;
import org.vclipse.vcml.vcml.VCObject;
import org.vclipse.vcml.vcml.VcmlModel;

import com.google.common.collect.Sets;
import com.google.inject.Inject;

public class CleanUpDependenciesHandler extends AbstractHandler {

	private static final Logger LOGGER = Logger.getLogger(CleanUpDependenciesHandler.class);
	
	@Inject
	private VcmlResourcesState containerState;
	
	@Inject
	private IQualifiedNameProvider nameProvider;
	
	@Inject
	private DependencySourceUtils sourceUtils;
	
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		Object appContext = event.getApplicationContext();
		if(appContext instanceof ExpressionContext) {
			Object defVariable = ((ExpressionContext)appContext).getDefaultVariable();
			if(defVariable instanceof Collection<?>) {
				final Collection<?> entries = (Collection<?>)defVariable;
				Job job = new Job("Cleanup dependencies job") {
					@Override
					protected IStatus run(IProgressMonitor monitor) {
						monitor.beginTask("Removing not required files with source code.", IProgressMonitor.UNKNOWN);
						for(Object entry : entries) {
							if(monitor.isCanceled()) {
								return Status.CANCEL_STATUS;
							}
							if(entry instanceof IContainer) {
								IContainer container = (IContainer)entry;
								monitor.subTask("Handling " + container.getName());
								handleContainer(container, monitor);
								monitor.worked(1);
							} else if(entry instanceof ITextSelection) {
								XtextEditor xtextEditor = EditorUtils.getActiveXtextEditor(event);
								IResource resource = xtextEditor.getResource();
								if(resource instanceof IFile) {
									monitor.subTask("Handling file " + ((IFile)resource).getName());
									handleFile(resource, monitor);
									monitor.worked(1);
								}
							}
						}
						return Status.OK_STATUS;
					}
				};
				job.schedule();
			} 
		}
		return null;
	}

	protected void handleFile(IResource resource, IProgressMonitor monitor) {
		IFile file = (IFile)resource;
		URI uri = URI.createPlatformResourceURI(file.getFullPath().toString(), true);
		executeOn(uri, monitor);
	}

	protected void handleContainer(Object entry, IProgressMonitor monitor) {
		String folderPath = ((IContainer)entry).getFullPath().toString();
		URI folderUri = URI.createPlatformResourceURI(folderPath, true);
		Collection<URI> containedURIs = containerState.getContainedURIs(folderUri.toString());
		for(URI uri : containedURIs) {
			executeOn(uri, monitor);
		}
	}

	protected void executeOn(URI uri, IProgressMonitor monitor) {
		String fileExtension = uri.fileExtension();
		XtextResourceSet set = new XtextResourceSet();
		if(DependencySourceUtils.EXTENSION_VCML.equals(fileExtension)) {
			Resource resource = set.getResource(uri, true);
			String uriStringNoExtension = uri.trimFileExtension().toString();
			String sourceFolderUri = uriStringNoExtension.concat(DependencySourceUtils.SUFFIX_SOURCEFOLDER);
			Collection<URI> containedURIs = containerState.getContainedURIs(sourceFolderUri);
			EList<EObject> contents = resource.getContents();
			Set<String> dependencyNames = Sets.newHashSet();
			if(!contents.isEmpty()) {
				EObject topObject = contents.get(0);
				if(topObject instanceof VcmlModel) {
					VcmlModel vcmlModel = (VcmlModel)topObject;
					for(VCObject vcobject : vcmlModel.getObjects()) {
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
								LOGGER.error(exception.getMessage());
								VCMLActivator instance = VCMLActivator.getInstance();
								ILog log = instance.getLog();
								String pluginid = VCMLActivator.ORG_VCLIPSE_VCML_VCML.replace("Vcml", ".ui");
								log.log(new Status(IStatus.ERROR, pluginid, exception.getMessage()));
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
					Resource resource = set.getResource(uri, true);
					resource.delete(SaveOptions.defaultOptions().toOptionsMap());
				} catch(IOException exception) {
					LOGGER.error(exception.getMessage());
					VCMLUiPlugin.log(exception.getMessage(), exception);
				}
			}
		}
	}
}
