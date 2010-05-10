/**
 * 
 */
package org.vclipse.vcml.diff;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.compare.diff.metamodel.DiffModel;
import org.eclipse.emf.compare.diff.service.DiffService;
import org.eclipse.emf.compare.match.MatchOptions;
import org.eclipse.emf.compare.match.metamodel.MatchModel;
import org.eclipse.emf.compare.match.service.MatchService;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.xtext.resource.XtextResourceSet;
import org.eclipse.xtext.util.StringInputStream;
import org.vclipse.vcml.vcml.Model;
import org.vclipse.vcml.vcml.VcmlFactory;

/**
 *
 */
public class ExportDiffsOperation implements IWorkspaceRunnable {

	private IFile left;
	
	private IFile right;
	
	private IFile export;
	
	/**
	 * @param leftFile
	 * @param rightFile
	 * @param exportFile
	 */
	public ExportDiffsOperation(IFile leftFile, IFile rightFile, IFile exportFile) {
		left = leftFile;
		right = rightFile;
		export = exportFile;
	}
	
	@Override
	public void run(final IProgressMonitor monitor) throws CoreException {
		monitor.beginTask("Comparing 2 files, extracting differences", IProgressMonitor.UNKNOWN);
		if(!export.exists()) {
			export.create(new StringInputStream(""), true, monitor);
		} else {
			export.setContents(new StringInputStream(""), true, true, monitor);
		}
		ResourceSet set = new XtextResourceSet();
		Resource resource_one = set.getResource(URI.createURI(left.getLocationURI().toString()), true);
		Resource resource_two = set.getResource(URI.createURI(right.getLocationURI().toString()), true);
		
		Model left_model = null;
		if(resource_one.getContents().isEmpty()) {
			left_model = VcmlFactory.eINSTANCE.createModel();
			resource_one.getContents().add(left_model);
		} else {
			left_model = (Model)resource_one.getContents().get(0);
		}
		
		Model right_model = null;
		if(resource_two.getContents().isEmpty()) {
			right_model = VcmlFactory.eINSTANCE.createModel();
			resource_two.getContents().add(right_model);
		} else {
			right_model = (Model)resource_two.getContents().get(0);
		}
		try {
			Map<String, Object> options = new HashMap<String, Object>();   
			options.put(MatchOptions.OPTION_DISTINCT_METAMODELS, false);
			MatchModel matchModel = MatchService.doContentMatch(left_model, right_model, options);
			DiffModel diffModel = DiffService.doDiff(matchModel);
			final Resource resource = new XtextResourceSet().getResource(URI.createURI(export.getLocationURI().toString()), true);
			final Model vcmlModel = VcmlFactory.eINSTANCE.createModel();
			resource.getContents().add(vcmlModel);			
			new DiffsHandlerSwitch(vcmlModel).doSwitch(diffModel);
			try {
				resource.save(Collections.EMPTY_MAP);
			} catch(final IOException exception) {
				
			}
		} catch(final InterruptedException exception) {
			System.out.println(exception);
		}
		monitor.done();
	}
}
