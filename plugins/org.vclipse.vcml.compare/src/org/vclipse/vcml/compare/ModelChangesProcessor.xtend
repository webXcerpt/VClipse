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
package org.vclipse.vcml.compare

import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import com.google.inject.Singleton
import org.eclipse.emf.common.util.URI
import org.eclipse.emf.compare.DifferenceKind
import org.eclipse.emf.compare.DifferenceSource
import org.eclipse.emf.compare.Match
import org.eclipse.emf.ecore.EAttribute
import org.eclipse.emf.ecore.EObject
import org.eclipse.emf.ecore.EReference
import org.eclipse.emf.ecore.util.EcoreUtil
import org.eclipse.xtext.diagnostics.Severity
import org.eclipse.xtext.nodemodel.util.NodeModelUtils
import org.eclipse.xtext.validation.CheckType
import org.eclipse.xtext.validation.Issue$IssueImpl
import org.eclipse.xtext.validation.ValidationMessageAcceptor
import org.vclipse.vcml.vcml.Characteristic
import org.vclipse.vcml.vcml.OptionType
import org.vclipse.vcml.vcml.VCObject
import org.vclipse.vcml.vcml.VcmlModel

import static org.vclipse.vcml.compare.ModelChangesProcessor.*

/*
 * Implements the handling of reference changes and issue creation if a change does not allowed.
 */
@Singleton
class ModelChangesProcessor extends ResourceChangesProcessor {
	
	public static String VCML_EXTENSION = ".vcml"
	public static String DIFF_VCML_EXTENSION = "_diff.vcml"
	
	// String identifying the compare issue -> required by xtext validation framework
	public static String COMPARE_ISSUE_CODE = "Compare_Issue_Code"
	
	// Contains mapping for VCObject -> issue // TODO use qualified names with type prefix -> vcml allows equally named but differently typed vc objects 
	private Multimap<String, IssueImpl> name2Issue
	
	/*
	 * Initialisation -> should be done before the handling on the changes is executed.
	 */
	override initialize(VcmlModel vcmlModel) {
		super.initialize(vcmlModel)
		name2Issue = HashMultimap::create
	}
	
	/*
	 * Returns the collected issues.
	 */
	def getCollectedIssues() {
		name2Issue
	}
	
	/**
	 * Reaction on an attribute change in a VCObject
	 */
	override attributeChange(Match match, EAttribute attribute, Object value, DifferenceKind kind, DifferenceSource source) {
		val left = match.left
		if(DifferenceSource::LEFT == source) {
			if(left instanceof VCObject) {
				addVCObject(left as VCObject)
			}
		}
	}
	
	/*
	 * Rection on a reference change in the vcml model.
	 */
	override referenceChange(Match match, EReference reference, EObject value, DifferenceKind kind, DifferenceSource source) {
		val left = match.left
		val right = match.right
		
		// handle the options
		if(left instanceof VcmlModel && vcmlPackage.vcmlModel_Options == reference) {
			for(option : (left as VcmlModel).options) {
				if(OptionType::UPS == option.name && !seenObjects.contains(option.eClass.instanceClassName + option.name)) {
					newOptions.add(EcoreUtil::copy(option))
					seenObjects.add(option.eClass.instanceClassName)
				}
			}
			return
		}
		
		// proof if user changed the type of existing characteristic -> the cstic does exist on both sides
		if(DifferenceKind::DELETE == kind && DifferenceSource::LEFT == source && vcmlPackage.characteristic_Type == reference) {
			validate(match.left, match.right, value, reference, kind, source)
			return
		}
		
		// handle the add - case
		reference(left, right, value, reference, kind, source)
	}
	
	/*
	 * Reaction on add operation between 2 models.
	 */
	override reference(EObject left, EObject right, EObject value, EReference reference, DifferenceKind kind, DifferenceSource source) {
		if(DifferenceKind::ADD == kind && DifferenceSource::LEFT == source) {
			// validate the sides
			validate(left, right, value, reference, kind, source)
			 
			// reaction only on the top level reference change
			if(vcmlPackage.vcmlModel_Objects == reference) {
				// add missing vcobjects
				if(left instanceof VcmlModel && value instanceof VCObject) {
					addVCObject(value as VCObject)
					return
				}
			}
			
			// add missing vcobject, but react on attribute/ reference change in object
			if(left instanceof VCObject) {
				// currently objects are added that are similar on both sides
				if(right instanceof VCObject) {
					if(!EcoreUtil::equals(left, right)) {
						addVCObject(left as VCObject)
					}
				} else {
					addVCObject(left as VCObject)
				}
				return
			} 
		}
	}
	
	/*
	 * adds a vc object to the objects lists
	 */
	def protected addVCObject(VCObject vcobject) {
		var theObject = vcobject
		val name = nameProvider.getFullyQualifiedName(theObject).toString
		if(!seenObjects.contains(name)) {
			theObject = EcoreUtil::copy(theObject)
			newObjects.add(theObject)
			seenObjects.add(name)
		}
		theObject
	}
	
	/*
	 * Validation of the change, the errors are reported as issues in the xtext editor
	 */
	def protected validate(EObject left, EObject right, EObject value, EReference reference, DifferenceKind kind, DifferenceSource source) {
		// the type of a characteristic has changed
		if(left instanceof Characteristic && right instanceof Characteristic) {
			if(vcmlPackage.characteristic_Type == reference) {
				val leftType = (left as Characteristic).type
				val rightType = (right as Characteristic).type
				if((leftType != null && rightType != null) && (leftType.eClass != rightType.eClass)) {
					val cstic = left as Characteristic
					name2Issue.put(cstic.name, createCharacteristicTypeIssue(left, right, leftType))
					addVCObject(cstic)
				}
			}
		}
	}
	
	/*
	 * Creates a new cstic type issue - one with properties set a.s.o.
	 */
	def protected IssueImpl createCharacteristicTypeIssue(EObject newContainer, EObject oldContainer, EObject newObject) {
		val containment = newObject.eContainmentFeature
		val message = createTypeChangeErrorMessage(oldContainer, newContainer, containment.featureID, ValidationMessageAcceptor::INSIGNIFICANT_INDEX)
		val issue = newIssue(message, COMPARE_ISSUE_CODE, CheckType::FAST, Severity::ERROR)
		val newContainerUri = EcoreUtil::getURI(newContainer).toString
		val oldContainerUri = EcoreUtil::getURI(oldContainer).toString
		setProperties(newObject, issue, newContainerUri, oldContainerUri, containment.name)
		issue
	}

	/*
 	 * Creates a new issue instance. 
 	 */
	def protected IssueImpl newIssue(String message, String code, CheckType type, Severity severity) {
		val issue = new IssueImpl
		issue.type = type
		issue.severity = severity
		issue.message = message
		issue.code = code
		issue
	}
	
	/*
	 * Creates a type chage error message.
	 */
	def protected String createTypeChangeErrorMessage(EObject newState, EObject oldState, int featureID, int index) {
		val feature = newState.eClass.getEStructuralFeature(featureID)
		var entryNewState = newState.eGet(feature) as EObject
		val messageBuffer = new StringBuffer
		messageBuffer.append("The change between ")
		messageBuffer.append(entryNewState.eClass.name)
		if(oldState != null) {
			messageBuffer.append(" and ")
			var entryOldState = oldState.eGet(feature) as EObject
			messageBuffer.append(entryOldState.eClass.name)
		}
		messageBuffer.append(" for a ")
		messageBuffer.append(newState.eClass.name)
		messageBuffer.append(" is not allowed in SAP system.")
		messageBuffer.toString
	}
	
	/*
	 * Sets the properties of an issue that are valueable for the xtext editor
	 */
	def protected void setProperties(EObject newState, IssueImpl issue, String ... issueData) {
		val node = NodeModelUtils::getNode(newState)
		if(node != null) {
			issue.length = node.length
			issue.lineNumber = node.startLine
			issue.offset = node.offset
			var uriAsString = EcoreUtil::getURI(newState).toString
			uriAsString = uriAsString.replace(VCML_EXTENSION, DIFF_VCML_EXTENSION)
			issue.uriToProblem = URI::createURI(uriAsString)
		}
		issue.data = issueData
	}
}