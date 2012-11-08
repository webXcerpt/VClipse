package org.vclipse.vcml.diff.compare;

import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
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
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.naming.IQualifiedNameProvider;
import org.eclipse.xtext.parser.IParseResult;
import org.eclipse.xtext.resource.SaveOptions;
import org.eclipse.xtext.resource.XtextResourceSet;
import org.vclipse.base.UriUtil;
import org.vclipse.vcml.parser.antlr.VCMLParser;
import org.vclipse.vcml.utils.DependencySourceUtils;
import org.vclipse.vcml.validation.VCMLJavaValidatorIssues;
import org.vclipse.vcml.vcml.Dependency;
import org.vclipse.vcml.vcml.Import;
import org.vclipse.vcml.vcml.Option;
import org.vclipse.vcml.vcml.OptionType;
import org.vclipse.vcml.vcml.VcmlFactory;
import org.vclipse.vcml.vcml.VcmlModel;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.inject.Inject;

public class VcmlCompare {

	private Logger logger = Logger.getLogger(VcmlCompare.class);
	
	private static final VcmlFactory VCML_FACTORY = VcmlFactory.eINSTANCE;
	
	private @Inject DiffModelSwitch diffModelSwitch;
	
	private @Inject VCMLParser vcmlParser;
	
	private @Inject VCMLJavaValidatorIssues issuesValidator;
	
	private @Inject DependencySourceUtils sourceUtils;
	
	private @Inject IQualifiedNameProvider nameProvider;
	
	public void compare(IFile oldFile, IFile newFile, IFile resultFile, IProgressMonitor monitor) throws CoreException, InterruptedException, IOException {
		monitor.subTask("Initialising models for comparison...");
		
		XtextResourceSet set = new XtextResourceSet();
		
		String path = newFile.getFullPath().toString();
		URI uri = URI.createPlatformResourceURI(path, true);
		Resource newResource = set.getResource(uri, true);
		
		path = oldFile.getFullPath().toString();
		uri = URI.createPlatformResourceURI(path, true);
		Resource oldResource = set.getResource(uri, true);
		
		path = resultFile.getFullPath().toString();
		uri = URI.createPlatformResourceURI(path, true);
		Resource resultResource = set.getResource(uri, true);
		
		// clean the result resource if it does exist
		EList<EObject> contents = resultResource.getContents();
		if(!contents.isEmpty()) {
			contents.clear();				
		}
		
		// compare the file contents
		compare(oldResource, newResource, resultResource, monitor);
		
		createMarkers(resultFile, resultResource);
		// refresh the result file
		resultFile.refreshLocal(IResource.DEPTH_ONE, monitor);
	}
	
	public void createMarkers(IFile resultFile, Resource resultResource) throws CoreException, IOException {
		EList<EObject> contents = resultResource.getContents();
		IParseResult parse = vcmlParser.parse(new FileReader(resultFile.getLocation().toFile()));
		EObject rootASTElement = parse.getRootASTElement();
		if(rootASTElement instanceof VcmlModel) {
			contents.clear();
			contents.add(rootASTElement);
		}
		
		issuesValidator.setIssues(diffModelSwitch.getIssues());
		issuesValidator.setResource(resultResource);
	}
	
	public void compare(Resource oldResource, Resource newResource, Resource resultResource, IProgressMonitor monitor) throws InterruptedException, IOException {
		monitor.subTask("Comparing models...");
		Map<String, Object> options = new HashMap<String, Object>();   
		options.put(MatchOptions.OPTION_DISTINCT_METAMODELS, false);
		options.put(MatchOptions.OPTION_IGNORE_ID, false);
		options.put(MatchOptions.OPTION_IGNORE_XMI_ID, false);
		MatchModel matchModel = MatchService.doResourceMatch(newResource, oldResource, options);
		
		monitor.worked(10);
		
		VcmlModel resultModel = VCML_FACTORY.createVcmlModel();	
		EList<EObject> contents = resultResource.getContents();
		if(!contents.isEmpty()) {
			contents.clear();
		}
		contents.add(resultModel);
		
		// get the ups option from the new file and provide it to the results file
		VcmlModel changedModel = VCML_FACTORY.createVcmlModel();
		List<EObject> newModelContent = newResource.getContents();
		if(!newModelContent.isEmpty()) {
			EObject object = newModelContent.get(0);
			if(object instanceof VcmlModel) {
				changedModel = (VcmlModel)newModelContent.get(0);
				for(Option option : changedModel.getOptions()) {
					if(OptionType.UPS.equals(option.getName())) {
						resultModel.getOptions().add(EcoreUtil2.copy(option));
					}
				}
			}
		}
	
		DiffModel diffModel = DiffService.doDiff(matchModel);
		diffModelSwitch.extractDifferences(diffModel, resultModel, changedModel, monitor);
		
		// compute the import uri -> the old file should be imported by the result file
		Import importStatement = VCML_FACTORY.createImport();
		importStatement.setImportURI(new UriUtil().computeImportUri(oldResource, resultResource));
		resultModel.getImports().add(importStatement);
		resultResource.save(SaveOptions.defaultOptions().toOptionsMap());
		
		List<Dependency> changedDependencies = ((VcmlDiffEngine)DiffService.getBestDiffEngine(matchModel)).getChangedDependencies();
		Iterator<Dependency> iterator = Iterables.filter(resultModel.getObjects(), Dependency.class).iterator();
		ResourceSet resourceSet = resultResource.getResourceSet();
		while(iterator.hasNext()) {
			Dependency dependency = iterator.next();
			URI sourceURI = sourceUtils.getSourceURI(dependency);
			final String name = nameProvider.getFullyQualifiedName(dependency).getLastSegment();
			Iterator<Dependency> changedDependencyIterator = Iterables.filter(changedDependencies, new Predicate<Dependency>() {
				@Override
				public boolean apply(Dependency changedDependency) {
					return nameProvider.getFullyQualifiedName(changedDependency).getLastSegment().equals(name);
				}
			}).iterator();
			
			while(changedDependencyIterator.hasNext()) {
				Dependency changedDependency = changedDependencyIterator.next();
				EObject source = sourceUtils.getSource(changedDependency);
				if(source == null) {
					logger.error("The source element for " + nameProvider.getFullyQualifiedName(changedDependency).getLastSegment() + " was null.");
					continue;
				}
				Resource newSourceResource = null;
				try {
					newSourceResource = resourceSet.getResource(sourceURI, true);
				} catch(Exception exception) {
					newSourceResource = resourceSet.getResource(sourceURI, true);
				}
				newSourceResource.getContents().add(EcoreUtil.copy(source));
				newSourceResource.save(SaveOptions.defaultOptions().toOptionsMap());
			}
		}
	}
	
	public boolean reportedProblems() {
		return !diffModelSwitch.getIssues().isEmpty();
	}
}
