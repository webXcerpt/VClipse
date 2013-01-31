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
package org.vclipse.bapi.actions.handler;

import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.e4.ui.workbench.modeling.ExpressionContext;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.xtext.resource.EObjectAtOffsetHelper;
import org.eclipse.xtext.resource.SaveOptions;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.resource.XtextResourceSet;
import org.eclipse.xtext.ui.editor.outline.impl.EObjectNode;
import org.eclipse.xtext.ui.util.ResourceUtil;
import org.eclipse.xtext.util.SimpleAttributeResolver;
import org.eclipse.xtext.util.StringInputStream;
import org.vclipse.bapi.actions.BAPIException;
import org.vclipse.base.ui.util.EditorUtilsExtensions;
import org.vclipse.vcml.ui.IUiConstants;
import org.vclipse.vcml.vcml.Import;
import org.vclipse.vcml.vcml.Option;
import org.vclipse.vcml.vcml.VCObject;
import org.vclipse.vcml.vcml.VariantTableContent;
import org.vclipse.vcml.vcml.VcmlFactory;
import org.vclipse.vcml.vcml.VcmlModel;

import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.google.inject.name.Named;

public class BAPIActionHandler extends AbstractHandler {

	protected static final String EXTRACTED_FILENAME_ADDON = "extracted.";
	
	protected static final VcmlFactory VCML = VcmlFactory.eINSTANCE;

	private XtextResourceSet resourceSet;
	
	@Inject
	private EObjectAtOffsetHelper offsetHelper;
	
	@Inject
	protected IPreferenceStore preferenceStore;
	
	@Inject
	@Named("Task")
	protected PrintStream task; 
	
	@Inject
	@Named("Error")
	protected PrintStream errorStream; 
	
	@Inject
	@Named("Warning")
	protected PrintStream warningStream; 
	
	@Inject
	@Named("Info")
	protected PrintStream infoStream; 
	
	@Inject
	@Named("Result")
	private PrintStream resultStream;
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		resourceSet = new XtextResourceSet();
		final boolean fileOutput = preferenceStore.getBoolean(IUiConstants.OUTPUT_TO_FILE);
		final Map<String, VCObject> seenObjects = Maps.newHashMap();
		final Object appContext = event.getApplicationContext();
		final Collection<?> entries = getEntries(appContext);
		final XtextResource source = getSourceResource(entries, event);
		final VcmlModel vcmlSourceModel = (VcmlModel)source.getContents().get(0);
		final EList<Option> options = vcmlSourceModel.getOptions();
		
		WorkspaceJob workspaceJob = new WorkspaceJob("Executing SAP action") {
			@Override
			public IStatus runInWorkspace(IProgressMonitor monitor) throws CoreException {
				resultStream.println("Task started (" + new Date() + ")");
				Resource result = getResultResource(source, seenObjects, fileOutput);
				String taskName = "Executing rfc call on SAP system";
				monitor.beginTask(taskName, IProgressMonitor.UNKNOWN);
				VcmlModel vcmlModel = (VcmlModel)result.getContents().get(0);
				for(Object entry : entries) {
					if(monitor.isCanceled()) {
						monitor.done();
						resultStream.println("Task cancelled: " + taskName + " (" + new Date() + ")");
						return Status.CANCEL_STATUS;
					}
					EObject current = null;
					if(entry instanceof ITextSelection) {
						current = BAPIActionUtils.getVCObject(offsetHelper, (ITextSelection)entry, source);
					} else if(entry instanceof VCObject) {
						current = (EObject)entry;
					} else if(entry instanceof EObjectNode) {
						current = (EObject)((EObjectNode)entry).getAdapter(VCObject.class);
					}
					if(current == null) {
						continue;
					}
					Class<? extends BAPIActionHandler> bapiClass = BAPIActionHandler.this.getClass();
					String simpleName = bapiClass.getSimpleName();
					EObject eobject = current instanceof VariantTableContent ? ((VariantTableContent)current).getTable() : current;
					String objectName = SimpleAttributeResolver.NAME_RESOLVER.apply(eobject);
					taskName = "Executing " + simpleName + " for " + objectName;
					monitor.setTaskName(taskName);
					if(monitor.isCanceled()) {
						monitor.done();
						resultStream.println("Task cancelled: " + taskName + " (" + new Date() + ")");
						return Status.CANCEL_STATUS;
					}
					try {
						Method method = bapiClass.getMethod("run", new Class[]{BAPIActionUtils.getInstanceType(current), Resource.class, IProgressMonitor.class, Map.class, List.class});
						method.invoke(BAPIActionHandler.this, new Object[]{current, result, monitor, seenObjects, options});
					} catch (InvocationTargetException e) {
						if(e.getTargetException() instanceof BAPIException) {
							errorStream.println("// canceled");
							break;
						} else {
							e.printStackTrace();
							e.getTargetException().printStackTrace(errorStream); // display original cause in VClipse console
						}
					} catch(Exception exception) {
						exception.printStackTrace(errorStream); // this can be a JCoException or an AbapExeption
					}
				}
				if(monitor.isCanceled()) {
					monitor.done();
					resultStream.println("Task cancelled: " + taskName + " (" + new Date() + ")");
					return Status.CANCEL_STATUS;
				}
				persistResultResource(fileOutput, result, vcmlModel, monitor);
				resultStream.println("Task finished: " + taskName + " (" + new Date() + ")");
				return Status.OK_STATUS;
			}
		};
		workspaceJob.setPriority(Job.LONG);
		workspaceJob.schedule();
		return null;
	}

	protected XtextResource getSourceResource(Collection<?> entries, ExecutionEvent event) {
		for(Object entry : entries) {
			if(entry instanceof EObjectNode) {
				Object adapter = ((EObjectNode)entry).getAdapter(VCObject.class);
				if(adapter instanceof VCObject) {
					return (XtextResource)((VCObject)adapter).eResource();
				}
			} else if(entry instanceof ITextSelection) {
				return EditorUtilsExtensions.getXtextResource(HandlerUtil.getActiveEditor(event));
			} else if(entry instanceof VCObject) {
				return (XtextResource)((VCObject)entry).eResource();
			}
		}
		return null;
	}
	
	protected Resource getResultResource(Resource source, Map<String, VCObject> seenObjects, boolean fileOutput) {
		URI sourceUri = source.getURI();
		URI resultUri = sourceUri.trimFileExtension().appendFileExtension(EXTRACTED_FILENAME_ADDON + sourceUri.fileExtension());
		Resource result = resourceSet.createResource(resultUri, "UTF-8");
		result.getContents().add(VCML.createVcmlModel());
		collectImportedObjects(seenObjects, source, result);
		createResultFile(result);
		return result;
	}
	
	protected void persistResultResource(boolean outputToFile, final Resource finalSourceResource, final VcmlModel vcmlModel, IProgressMonitor monitor) {
		try {
			if(outputToFile) {
				finalSourceResource.save(SaveOptions.defaultOptions().toOptionsMap());
			} else {
				if (!vcmlModel.getObjects().isEmpty()) {
					resultStream.println("\n");
					resultStream.println(((XtextResource)finalSourceResource).getSerializer().serialize(vcmlModel));
				}
				finalSourceResource.delete(SaveOptions.defaultOptions().toOptionsMap());
			}
		} catch (Exception exception) {
			// currently, there can be exceptions if objects are not completeley initialized or linking fails or IOExceptions
			exception.printStackTrace(errorStream);
		}
		
		IFile file = ResourceUtil.getFile(finalSourceResource);
		if(file != null && file.isAccessible()) {
			try {
				file.refreshLocal(IResource.DEPTH_ONE, monitor);
			} catch(CoreException exception) {
				exception.printStackTrace();
			}
		}
	}
	
	protected void collectImportedObjects(Map<String, VCObject> seenObjects, Resource sourceResource, Resource targetResource) {
		VcmlModel targetModel = null;
		EList<EObject> targetContents = targetResource.getContents();
		if(!targetContents.isEmpty()) {
			targetModel = (VcmlModel)targetContents.get(0);
		}
		
		URI uri = sourceResource.getURI();
		String platformString = uri.toPlatformString(true);
		String lastSegment = uri.lastSegment();
		EList<EObject> contents = sourceResource.getContents();
		if(!contents.isEmpty()) {
			VcmlModel vcmlModel = (VcmlModel)contents.get(0);
			for(Import importStatement : vcmlModel.getImports()) {
				String importURI = importStatement.getImportURI();
				String importResourcePath = platformString.replace(lastSegment, importURI);
				Resource loadedResource = resourceSet.getResource(URI.createURI(importResourcePath), true);
				EList<EObject> loadedContents = loadedResource.getContents();
				if(!loadedContents.isEmpty()) {
					if(targetModel != null) {
						targetModel.getImports().add(EcoreUtil.copy(importStatement));
					}
					EObject topLevelObject = loadedContents.get(0);
					if(topLevelObject instanceof VcmlModel) {
						VcmlModel model = (VcmlModel)topLevelObject;
						for(VCObject vcobject : model.getObjects()) {
							seenObjects.put(vcobject.eClass().getName() + "/" +vcobject.getName(), vcobject);
						}
					}
				}
			}
		}
	}
	
	protected void createResultFile(Resource resource) {
		IFile resultfile = ResourceUtil.getFile(resource);
		if(!resultfile.exists()) {
			try {
				resultfile.create(new StringInputStream(""), true, new NullProgressMonitor());
			} catch(CoreException exception) {
				exception.printStackTrace();
			}
		}
	}
	
	protected Collection<?> getEntries(Object appContext) {
		if(appContext instanceof ExpressionContext) {
			Object defVariable = ((ExpressionContext)appContext).getDefaultVariable();
			if(defVariable instanceof Collection<?>) {
				return (Collection<?>)defVariable;
			}
		}
		return Collections.EMPTY_LIST;
	}
}