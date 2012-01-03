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
import org.vclipse.vcml.vcml.Import;
import org.vclipse.vcml.vcml.Model;
import org.vclipse.vcml.vcml.Option;
import org.vclipse.vcml.vcml.OptionType;
import org.vclipse.vcml.vcml.VcmlFactory;

/**
 *
 */
public class ExportDiffsJob extends Job {

	/**
	 * 
	 */
	private IFile leftFile;
	
	/**
	 * 
	 */
	private IFile rightFile;
	
	/**
	 * 
	 */
	private IFile exportFile;
	
	/**
	 * @param name
	 */
	public ExportDiffsJob() {
		super("Export job for differences betweeen 2 vcml files.");
	}

	/**
	 * @param file
	 */
	public void setLeftFile(IFile file) {
		leftFile = file;
	}
	
	/**
	 * @param file
	 */
	public void setRightFile(IFile file) {
		rightFile = file;
	}
	
	/**
	 * @param file
	 */
	public void setExportFile(IFile file) {
		exportFile = file;
	}
	
	/**
	 * @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	protected IStatus run(final IProgressMonitor monitor) {
		assert leftFile != null;
		assert rightFile != null;
		assert exportFile != null;
		
		IStatus jobStatus = Status.OK_STATUS;
		monitor.beginTask("Exporting differences...", 35);
		try  {
			monitor.subTask("Creating export file...");
			if(!exportFile.exists()) {
				exportFile.create(new StringInputStream(""), true, monitor);
			} else {
				exportFile.setContents(new StringInputStream(""), true, true, monitor);
			}
			monitor.worked(5);
			
			monitor.subTask("Initialising models for compare operation...");
			ResourceSet set = new ResourceSetImpl();
			Resource resource_one = set.getResource(URI.createURI(leftFile.getLocationURI().toString()), true);
			Resource resource_two = set.getResource(URI.createURI(rightFile.getLocationURI().toString()), true);
			
			monitor.subTask("Comparing models...");
			Map<String, Object> options = new HashMap<String, Object>();   
			options.put(MatchOptions.OPTION_DISTINCT_METAMODELS, false);
			MatchModel matchModel = MatchService.doResourceMatch(resource_one, resource_two, options);
			
			DiffModel diffModel = DiffService.doDiff(matchModel);
			monitor.worked(10);
			
			monitor.subTask("Saving results...");
			Resource resource = new XtextResourceSet().getResource(URI.createURI(exportFile.getLocationURI().toString()), true);
			EList<EObject> contents = resource.getContents();
			if(!contents.isEmpty()) {
				contents.clear();				
			}
			
			Model vcmlModel = VcmlFactory.eINSTANCE.createModel();		
			
			Import imStatement = VcmlFactory.eINSTANCE.createImport();
			imStatement.setImportURI(resource_one.getURI().lastSegment());
			vcmlModel.getImports().add(imStatement);
		
			EList<EObject> contents2 = resource_two.getContents();
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
			exportFile.refreshLocal(IResource.DEPTH_ONE, monitor);
			monitor.worked(10);
		} catch(final CoreException exception) {
			VcmlDiffPlugin.log(exception.getMessage(), exception);
			jobStatus = Status.CANCEL_STATUS;
		} catch(final IOException exception) {
			VcmlDiffPlugin.log(exception.getMessage(), exception);
			jobStatus = Status.CANCEL_STATUS;
		} catch(final InterruptedException exception) {
			VcmlDiffPlugin.log(exception.getMessage(), exception);
			jobStatus = Status.CANCEL_STATUS;
		} finally {
			monitor.done();
		}
		return jobStatus;
	}
}
