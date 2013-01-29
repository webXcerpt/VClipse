/*******************************************************************************
 * Copyright (c) 2013 webXcerpt Software GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *		webXcerpt Software GmbH - initial creator
 *		www.webxcerpt.com
 ******************************************************************************/
package org.vclipse.vcml.compare

import com.google.inject.Inject
import java.io.FileWriter
import org.eclipse.core.resources.IFile
import org.eclipse.core.resources.IResource
import org.eclipse.core.runtime.IProgressMonitor
import org.eclipse.core.runtime.Path
import org.eclipse.core.runtime.SubMonitor
import org.eclipse.emf.compare.match.DefaultComparisonFactory
import org.eclipse.emf.compare.match.DefaultMatchEngine
import org.eclipse.emf.compare.match.IEqualityHelperFactory
import org.eclipse.emf.compare.match.eobject.IdentifierEObjectMatcher
import org.eclipse.emf.ecore.resource.Resource
import org.eclipse.xtext.resource.XtextResourceSet
import org.eclipse.xtext.ui.editor.validation.MarkerCreator
import org.eclipse.xtext.ui.util.ResourceUtil
import org.eclipse.xtext.util.StringInputStream
import org.vclipse.base.naming.INameProvider
import org.vclipse.vcml.vcml.VcmlFactory
import org.vclipse.vcml.vcml.VcmlModel

import static org.eclipse.core.resources.IResource.*
import static org.eclipse.core.runtime.IStatus.*
import static org.eclipse.emf.common.util.BasicMonitor.*
import static org.eclipse.emf.common.util.URI.*
import static org.eclipse.xtext.resource.SaveOptions.*
import static org.vclipse.vcml.compare.VCMLCompareOperation.*

/*
 * 
 */
class VCMLCompareOperation {
	
	public static String ERRORS_FILE_EXTENSION = "_errors.txt"
	
	private static VcmlFactory VCML_FACTORY = VcmlFactory::eINSTANCE
	
	@Inject
	private IEqualityHelperFactory equalityHelperFactory
	
	@Inject
	private INameProvider vcmlNameProvider
	
	@Inject
	private ModelChangesProcessor modelChangesProcessor
	
	@Inject
	private ResourceChangesProcessor resourceChangesProcessor
	
	@Inject
	private ModelDifferencesEngine modelDifferencesEngine
	
	@Inject
	private ResourceDifferencesEngine resourceDifferencesEngine
	
	@Inject
	private MarkerCreator markerCreator
	
	private int peakForViewOutput = 10
	
	/**
	 * Compare operation for 2 vcml files. Results are extracted to the result file.
	 */
	def void compare(IFile oldFile, IFile newFile, IFile result, IProgressMonitor monitor) throws Exception {
		monitor.subTask("Initialising models for comparison...")
		
		val set = new XtextResourceSet
		
		var path = newFile.fullPath.toString
		var uri = createPlatformResourceURI(path, true)
		val newResource = set.getResource(uri, true)
		
		path = oldFile.fullPath.toString
		uri = createPlatformResourceURI(path, true)
		val oldResource = set.getResource(uri, true)
		
		path = result.fullPath.toString
		uri = createPlatformResourceURI(path, true)
		val resultResource = set.getResource(uri, true)

		// compare the file contents
		compare(oldResource, newResource, resultResource, monitor)
		
		// provide issues and the resource on validator
		createMarkers(resultResource)
		
		// handle the collected errors
		handleErrors(result, monitor)
		
		// refresh the result file
		result.refreshLocal(DEPTH_ONE, monitor)
	}
	
	/**
	 * Compare operation for 2 vcml resources. Results are extracted to the result resource.
	 */
	def void compare(Resource oldResource, Resource newResource, Resource resultResource, IProgressMonitor monitor) throws Exception {
		val submonitor = SubMonitor::convert(monitor, "Comparing 2 vcml resources...", IProgressMonitor::UNKNOWN)
		
		val newContents = newResource.contents
		
		val resultModel = VCML_FACTORY.createVcmlModel
		val contents = resultResource.contents
		if(!contents.empty) {
			contents.clear
		}
		contents.add(resultModel)
		
		/*
		 * Persit the result resource to get the right uri handling, is required by dependency handling.
		 */
		resultResource.save(defaultOptions().toOptionsMap())
		
		val newModel = newContents.get(0) as VcmlModel
		val oldModel = oldResource.contents.get(0) as VcmlModel
		
		resourceChangesProcessor.initialize(resultModel)
		
		val objectMatcher = new IdentifierEObjectMatcher(vcmlNameProvider)
		val comparisonFactory = new DefaultComparisonFactory(equalityHelperFactory)
		val matchEngine = new DefaultMatchEngine(objectMatcher, comparisonFactory)
		val emfMonitor = toMonitor(monitor)
		val vcmlScope = new VCMLResourceScope(newModel, oldModel)
		
		// compare the vcml models
		compare(oldModel, newModel, resultModel, monitor)
		
		resourceDifferencesEngine.diff(matchEngine.match(vcmlScope, emfMonitor), emfMonitor)
		
		/*
		 * Persist the result after the differences extraction.
		 */ 
		resultResource.save(defaultOptions().toOptionsMap())
		submonitor.done
	}
	
	/**
	 * Compare operation for 2 vcml models.
	 */
	def void compare(VcmlModel oldModel, VcmlModel newModel, VcmlModel resultModel, IProgressMonitor monitor) throws Exception {
		val submonitor = SubMonitor::convert(monitor, "Comparing 2 vcml models...", IProgressMonitor::UNKNOWN)
		modelChangesProcessor.initialize(resultModel)
		val objectMatcher = new IdentifierEObjectMatcher(vcmlNameProvider)
		val comparisonFactory = new DefaultComparisonFactory(equalityHelperFactory)
		val matchEngine = new DefaultMatchEngine(objectMatcher, comparisonFactory)
		val emfMonitor = toMonitor(monitor)
		val vcmlScope = new VCMLModelScope(newModel, oldModel)
		modelDifferencesEngine.diff(matchEngine.match(vcmlScope, emfMonitor), emfMonitor)
		submonitor.done
	}
	
	/*
	 * Provide issues and target resource on validator
	 */
	def createMarkers(Resource result) {
		val file = ResourceUtil::getFile(result)
		file.deleteMarkers("org.eclipse.core.resources.problemmarker", false, IResource::DEPTH_ONE)
		for(issueEntry : modelChangesProcessor.collectedIssues.entries) {
			markerCreator.createMarker(issueEntry.value, file, "org.eclipse.core.resources.problemmarker")
		}
	}
	
	/**
	 * Returns true if there were some problems during compare operation, false otherwise.
	 */
	def boolean reportedProblems() {
		val issues = modelChangesProcessor.collectedIssues
		return !issues.empty
	}

	/*
	 * Handles the errors provided by the changes processor. The output is streamed
	 * to the error log view, if the amount is above peakForViewOutput, the stream is
	 * switched to a file.
	 */
	def protected handleErrors(IFile result, IProgressMonitor monitor) {
		// write out errors to a file
		val errors = modelChangesProcessor.errors
		if(errors.size < peakForViewOutput) {
			for(error : errors.get(ERROR)) {
				VCMLComparePlugin::log(ERROR, error.message)
			}
		} else {
			val fileName = result.name.toString.replace(ModelChangesProcessor::DIFF_VCML_EXTENSION, ERRORS_FILE_EXTENSION)
			val errorsFile = result.parent.getFile(new Path(fileName))
			val stream = new StringInputStream("")
			if(errorsFile.accessible) {
				errorsFile.setContents(stream, true, true, monitor)
			} else {
				errorsFile.create(stream, IResource::DERIVED, monitor)
			}
			val fileWriter = new FileWriter(errorsFile.location.toFile)
			for(error : errors.get(ERROR)) {
				fileWriter.append(error.message)
			}
			fileWriter.flush
			errorsFile.refreshLocal(DEPTH_ONE, monitor)
		}
	}
}