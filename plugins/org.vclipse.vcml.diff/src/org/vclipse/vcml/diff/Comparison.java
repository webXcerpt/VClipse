package org.vclipse.vcml.diff;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
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
import org.eclipse.xtext.resource.SaveOptions;
import org.vclipse.base.UriUtil;
import org.vclipse.vcml.vcml.Import;
import org.vclipse.vcml.vcml.Model;
import org.vclipse.vcml.vcml.Option;
import org.vclipse.vcml.vcml.OptionType;
import org.vclipse.vcml.vcml.VcmlFactory;

public class Comparison {

	private static final VcmlFactory VCML_FACTORY = VcmlFactory.eINSTANCE;

	public void compare(IFile oldFile, IFile newFile, IFile resultFile, IProgressMonitor monitor) throws CoreException, InterruptedException, IOException {
		
		monitor.subTask("Initialising models for comparison...");
		
		// create resources from files
		ResourceSet set = new ResourceSetImpl();
		Resource newResource = set.getResource(URI.createURI(newFile.getLocationURI().toString()), true);
		Resource oldResource = set.getResource(URI.createURI(oldFile.getLocationURI().toString()), true);
		Resource resultResource = set.getResource(URI.createURI(resultFile.getLocationURI().toString()), true);
		
		// clean the result resource if it does exist
		EList<EObject> contents = resultResource.getContents();
		if(!contents.isEmpty()) {
			contents.clear();				
		}
		
		// compare the file contents
		compare(oldResource, newResource, resultResource, monitor);
		
		// refresh the result file
		resultResource.save(SaveOptions.defaultOptions().toOptionsMap());
		resultFile.refreshLocal(IResource.DEPTH_ONE, monitor);
	}
	
	public void compare(Resource oldResource, Resource newResource, Resource resultResource, IProgressMonitor monitor) throws InterruptedException, IOException {
		monitor.subTask("Comparing models...");
		Map<String, Object> options = new HashMap<String, Object>();   
		options.put(MatchOptions.OPTION_DISTINCT_METAMODELS, false);
		options.put(MatchOptions.OPTION_IGNORE_ID, false);
		options.put(MatchOptions.OPTION_IGNORE_XMI_ID, false);
		MatchModel matchModel = MatchService.doResourceMatch(newResource, oldResource, options);
		
		DiffModel diffModel = DiffService.doDiff(matchModel);
		monitor.worked(10);
		
		Model resultModel = VCML_FACTORY.createModel();		
		
		// compute the import uri -> the old file should be imported by the result file
		Import importStatement = VCML_FACTORY.createImport();
		importStatement.setImportURI(new UriUtil().computeImportUri(oldResource, resultResource));
		resultModel.getImports().add(importStatement);
	
		// get the ups option from the new file and provide it to the results file
		Model changedModel = VCML_FACTORY.createModel();
		List<EObject> newModelContent = newResource.getContents();
		if(!newModelContent.isEmpty()) {
			EObject object = newModelContent.get(0);
			if(object instanceof Model) {
				changedModel = (Model)newModelContent.get(0);
				for(Option option : changedModel.getOptions()) {
					if(OptionType.UPS.equals(option.getName())) {
						resultModel.getOptions().add(EcoreUtil2.copy(option));
					}
				}
			}
		}
		
		List<EObject> contents = resultResource.getContents();
		contents.add(resultModel);
		new DiffsHandlerSwitch(resultModel, changedModel, monitor).doSwitch(diffModel);
		
		// resources contain same objects
		monitor.worked(10);
	}
}
