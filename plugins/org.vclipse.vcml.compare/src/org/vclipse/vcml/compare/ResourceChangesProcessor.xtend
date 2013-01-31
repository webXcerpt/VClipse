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

import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import com.google.common.io.ByteStreams
import com.google.inject.Inject
import com.google.inject.Singleton
import java.util.Set
import org.eclipse.core.resources.IWorkspaceRoot
import org.eclipse.core.runtime.IStatus
import org.eclipse.core.runtime.NullProgressMonitor
import org.eclipse.emf.common.util.EList
import org.eclipse.emf.compare.DifferenceKind
import org.eclipse.emf.compare.DifferenceSource
import org.eclipse.emf.compare.Match
import org.eclipse.emf.compare.diff.DiffBuilder
import org.eclipse.emf.ecore.EObject
import org.eclipse.emf.ecore.EReference
import org.eclipse.emf.ecore.util.EcoreUtil
import org.eclipse.xtext.naming.IQualifiedNameProvider
import org.eclipse.xtext.util.StringInputStream
import org.vclipse.base.ImportUriExtractor
import org.vclipse.base.compare.DefaultInputSupplier
import org.vclipse.vcml.utils.DependencySourceUtils
import org.vclipse.vcml.vcml.Dependency
import org.vclipse.vcml.vcml.Import
import org.vclipse.vcml.vcml.Option
import org.vclipse.vcml.vcml.VCObject
import org.vclipse.vcml.vcml.VcmlFactory
import org.vclipse.vcml.vcml.VcmlModel
import org.vclipse.vcml.vcml.VcmlPackage

/**
 * Proccessor that is suited for changes handling on the resource level.
 * 
 * It means that it handles the import and option statements.
 */
@Singleton
class ResourceChangesProcessor extends DiffBuilder {
	
	// The new vcml model
	protected VcmlModel newVcmlModel
	protected EList<Import> newImports
	protected EList<VCObject> newObjects
	protected EList<Option> newOptions
	
	// marks if the path to the new resource was already added as an import statement 
	protected boolean importsHandled
	
	// filter for the objects -> the changes can be reported many times -> filter out objects already handled
	protected Set<String> seenObjects
	
	// the package instance for getting references/ classes/ a.s.o.
	protected VcmlPackage vcmlPackage
	protected VcmlFactory vcmlFactory
	
	// Excpetions that are occured during the compare operation -> f.e. dependency file does not exist(the output to the error log ties the ui up)
	private Multimap<Integer, Exception> compareErrors
	
	@Inject
	protected ImportUriExtractor uriUtility // computation of the import path
	
	@Inject
	protected DependencySourceUtils sourceUtils // provides stuff concerning the dependency files
	
	@Inject
	protected IQualifiedNameProvider nameProvider // provides qualified names for vc objects
	
	@Inject
	protected IWorkspaceRoot root // required for dependency container creation
	
	@Inject
	protected FeatureFilter featureFilter
	
	/**
	 * 
	 */
	def void initialize(VcmlModel vcmlModel) {
		vcmlPackage = VcmlPackage::eINSTANCE
		vcmlFactory = VcmlFactory::eINSTANCE
		
		newVcmlModel = vcmlModel
		newImports = vcmlModel.imports
		newObjects = vcmlModel.objects
		newOptions = vcmlModel.options
		
		seenObjects = newHashSet
		importsHandled = false
		
		compareErrors = HashMultimap::create
	}
	
	/*
	 * Returns the errors.
	 */
	def getErrors() {
		compareErrors
	}
	
	/**
	 * 
	 */
	override referenceChange(Match match, EReference reference, EObject value, DifferenceKind kind, DifferenceSource source) {
		val left = match.left
		val right = match.right
		
		// the new resource is always imported
		if(!importsHandled) {
			if(left instanceof VcmlModel) {
				
				// handle existing import statements
				for(^import : (left as VcmlModel).imports) {
					if(!seenObjects.contains(vcmlPackage.^import.instanceClassName + ^import.importURI)) {
						val importStmt = VcmlFactory::eINSTANCE.createImport
						importStmt.setImportURI(^import.importURI)
						newImports.add(importStmt)
					}
				}
				
				// import the resource with new content
				val importUri = uriUtility.getImportUri(left.eResource, newVcmlModel.eResource)
				if(!seenObjects.contains(vcmlPackage.^import.instanceClassName + importUri)) {
					val importStmt = VcmlFactory::eINSTANCE.createImport
					importStmt.setImportURI(importUri)
					newImports.add(importStmt)
				}
				
				// import statements are handled only once 
				importsHandled = true
			}
		}
		
		reference(left, right, value, reference, kind, source)
	}
	
	/**
	 * 
	 */
	def protected void reference(EObject left, EObject right, EObject value, EReference reference, DifferenceKind kind, DifferenceSource source) {
		if(left instanceof Dependency) {
			dependencyChange(left as Dependency, right as Dependency, kind, source)
		}
	}
	
	/**
	 * Handle the dependency changes directly.
	 */
	def void dependencyChange(Dependency left, Dependency right, DifferenceKind kind, DifferenceSource source) {
		if(left == null) {	// the left one can not be null
			return
		}
		if(DifferenceKind::ADD == kind && DifferenceSource::LEFT == source) {
			try {
				val leftSupplier = new DefaultInputSupplier(sourceUtils.getInputStream(left))
				
				// the sap state can be empty -> added a dependency file in the vcml state
				val rightSupplier = new DefaultInputSupplier(
					if(right == null) 
						new StringInputStream("") 
					else 
						sourceUtils.getInputStream(right)
					)
					
				// dont bother if they are equal
				if(!ByteStreams::equal(leftSupplier, rightSupplier)) {
					val name = nameProvider.getFullyQualifiedName(left).toString
					if(!seenObjects.contains(name)) { // haven't seem such one yet
						// add the object itself
						val depcopy = EcoreUtil::copy(left)
						newObjects.add(depcopy as VCObject)
						val uri = sourceUtils.getSourceURI(depcopy)
								
						// and create folders/files for dependencies, copy the streams
						val projectName = uri.segmentsList.subList(1, 4)
						val newDepFolder = root.getProject(projectName.get(0)).getFolder(projectName.get(1)).getFolder(projectName.get(2))
						val monitor = new NullProgressMonitor
						if(!newDepFolder.exists) {
							newDepFolder.create(true, true, monitor)
						}
						val newDepFile = newDepFolder.getFile(uri.lastSegment)
						if(!newDepFile.exists) {
							newDepFile.create(sourceUtils.getInputStream(left), true, monitor)
						}
						seenObjects.add(name)
					}
				}
			} catch(Exception exception) {
				compareErrors.put(IStatus::ERROR, exception)
			}
		}
	}
}