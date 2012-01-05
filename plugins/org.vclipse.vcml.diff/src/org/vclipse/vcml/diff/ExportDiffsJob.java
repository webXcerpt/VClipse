/**
 * 
 */
package org.vclipse.vcml.diff;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.compare.diff.metamodel.DiffModel;
import org.eclipse.emf.compare.diff.service.DiffService;
import org.eclipse.emf.compare.match.MatchOptions;
import org.eclipse.emf.compare.match.metamodel.MatchModel;
import org.eclipse.emf.compare.match.service.MatchService;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.resource.XtextResourceSet;
import org.eclipse.xtext.util.StringInputStream;
import org.vclipse.base.UriUtil;
import org.vclipse.vcml.vcml.Import;
import org.vclipse.vcml.vcml.Model;
import org.vclipse.vcml.vcml.Option;
import org.vclipse.vcml.vcml.OptionType;
import org.vclipse.vcml.vcml.VcmlFactory;

public class ExportDiffsJob extends Job {

	private IFile newFile;
	private IFile oldFile;
	private IFile resultsFile;
	
	public ExportDiffsJob() {
		super("Export job for differences betweeen 2 vcml files.");
	}
	
	public void setOldFile(IFile file) {
		newFile = file;
	}
	
	public void setNewFile(IFile file) {
		oldFile = file;
	}
	
	public void setResultsFile(IFile file) {
		resultsFile = file;
	}
	
	@Override
	protected IStatus run(IProgressMonitor monitor) {
		assert newFile != null;
		assert oldFile != null;
		assert resultsFile != null;
		
		IStatus jobStatus = Status.OK_STATUS;
		monitor.beginTask("Exporting differences...", 35);
		try  {
			monitor.subTask("Creating export file...");
			if(!resultsFile.exists()) {
				resultsFile.create(new StringInputStream(""), true, monitor);
			} else {
				resultsFile.setContents(new StringInputStream(""), true, true, monitor);
			}
			monitor.worked(5);
			
			monitor.subTask("Initialising models for compare operation...");
			ResourceSet set = new ResourceSetImpl();
			Resource newResource = set.getResource(URI.createURI(newFile.getLocationURI().toString()), true);
			Resource oldResource = set.getResource(URI.createURI(oldFile.getLocationURI().toString()), true);
			
			monitor.subTask("Comparing models...");
			Map<String, Object> options = new HashMap<String, Object>();   
			options.put(MatchOptions.OPTION_DISTINCT_METAMODELS, false);
			MatchModel matchModel = MatchService.doResourceMatch(oldResource, newResource, options);
			
			DiffModel diffModel = DiffService.doDiff(matchModel);
			monitor.worked(10);
			
			monitor.subTask("Saving results...");
			Resource resource = new XtextResourceSet().getResource(URI.createURI(resultsFile.getLocationURI().toString()), true);
			EList<EObject> contents = resource.getContents();
			if(!contents.isEmpty()) {
				contents.clear();				
			}
			
			Model vcmlModel = VcmlFactory.eINSTANCE.createModel();		
			
			// compute the import uri -> the old file should be imported by the result file
			Import imStatement = VcmlFactory.eINSTANCE.createImport();
			imStatement.setImportURI(new UriUtil().computeImportUri(oldResource, resource));
			vcmlModel.getImports().add(imStatement);
		
			// get the ups option from the new file and provide it to the results file
			EList<EObject> contents2 = newResource.getContents();
			if(!contents2.isEmpty()) {
				EObject mainObject = contents2.get(0);
				if(mainObject instanceof Model) {
					EList<Option> options2 = ((Model)mainObject).getOptions();
					if(!options2.isEmpty()) {
						for(Option option : options2) {
							if(OptionType.UPS.equals(option.getName())) {
								vcmlModel.getOptions().add(EcoreUtil2.copy(option));
							}
						}
					}
				}				
			}
			
			contents.add(vcmlModel);
			new DiffsHandlerSwitch(vcmlModel, monitor).doSwitch(diffModel);
			resource.save(Collections.EMPTY_MAP);
			resultsFile.refreshLocal(IResource.DEPTH_ONE, monitor);
			monitor.worked(10);
		} catch(CoreException exception) {
			VcmlDiffPlugin.log(exception.getMessage(), exception);
			jobStatus = Status.CANCEL_STATUS;
		} catch(IOException exception) {
			VcmlDiffPlugin.log(exception.getMessage(), exception);
			jobStatus = Status.CANCEL_STATUS;
		} catch(InterruptedException exception) {
			VcmlDiffPlugin.log(exception.getMessage(), exception);
			jobStatus = Status.CANCEL_STATUS;
		} finally {
			monitor.done();
		}
		return jobStatus;
	}
}
