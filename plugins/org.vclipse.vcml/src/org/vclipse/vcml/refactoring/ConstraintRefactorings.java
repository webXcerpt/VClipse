/*******************************************************************************
 * Copyright (c) 2012 webXcerpt Software GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *  
 * Contributors:
 *     webXcerpt Software GmbH - initial creator
 ******************************************************************************/
package org.vclipse.vcml.refactoring;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.URIConverter;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.xtext.EcoreUtil2;
import org.vclipse.base.ui.util.EditorUtilsExtensions;
import org.vclipse.refactoring.IRefactoringContext;
import org.vclipse.refactoring.changes.ModelChange;
import org.vclipse.refactoring.changes.RootChange;
import org.vclipse.refactoring.core.DefaultRefactoringExecuter;
import org.vclipse.refactoring.core.RefactoringTask;
import org.vclipse.vcml.utils.ConstraintRestrictionExtensions;
import org.vclipse.vcml.utils.DependencySourceUtils;
import org.vclipse.vcml.utils.VCMLObjectUtils;
import org.vclipse.vcml.vcml.Characteristic;
import org.vclipse.vcml.vcml.CharacteristicReference_C;
import org.vclipse.vcml.vcml.Comparison;
import org.vclipse.vcml.vcml.Condition;
import org.vclipse.vcml.vcml.ConditionalConstraintRestriction;
import org.vclipse.vcml.vcml.Constraint;
import org.vclipse.vcml.vcml.ConstraintObject;
import org.vclipse.vcml.vcml.ConstraintRestriction;
import org.vclipse.vcml.vcml.ConstraintSource;
import org.vclipse.vcml.vcml.Description;
import org.vclipse.vcml.vcml.ObjectCharacteristicReference;
import org.vclipse.vcml.vcml.ShortVarReference;
import org.vclipse.vcml.vcml.SimpleDescription;
import org.vclipse.vcml.vcml.VCObject;
import org.vclipse.vcml.vcml.VcmlFactory;
import org.vclipse.vcml.vcml.VcmlPackage;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.inject.Inject;

public class ConstraintRefactorings extends DefaultRefactoringExecuter {
 
	@Inject
	private ConstraintRestrictionExtensions cre;
	
	@Inject
	private DependencySourceUtils sourceUtils;
	
	protected final VcmlFactory VCML_FACTORY = VcmlFactory.eINSTANCE;
	protected final VcmlPackage VCML_PACKAGE = VcmlPackage.eINSTANCE;
	
	public static final int MIN_CONSTRAINTS_AMOUNT = 10;
	public static final int MINIMUM_SUBLISTS = 2;
	
	public EPackage getPackage() {
		return VCML_PACKAGE;
	}

	public Set<EClass> getTopLevelTypes() {
		return Sets.newHashSet(VCML_PACKAGE.getVcmlModel());
	}
	
	/*
	 * Re-factoring: extracts common conditions from restrictions to a 
	 * condition part of a constraint source. 
	 */
	public void refactoring_Extract_ConstraintSource(IRefactoringContext context) {
		EObject sourceElement = context.getSourceElement();
		sourceElement = EcoreUtil2.getContainerOfType(sourceElement, ConstraintSource.class);
		if(sourceElement instanceof ConstraintSource) {
			ConstraintSource source = (ConstraintSource)sourceElement;
			extract_Condition(source);
			context.setSourceElement(source);
		}
	}
	
	// TODO look at the re-factoring one more time and re-implement
	@SuppressWarnings("unchecked")
	public void refactoring_Extract_ConstraintSource(IRefactoringContext context, ConstraintSource source) throws Exception {
		EList<ConstraintRestriction> restrictions = source.getRestrictions();
		URI refactoredUri = source.eResource().getURI();
		RefactoringTask refactoringTask = extensions.getInstance(RefactoringTask.class);
		IProgressMonitor pm = EditorUtilsExtensions.getProgressMonitor();
		RootChange change = refactoringTask.getChange(pm);
		ModelChange modelChange = (ModelChange)change.getChildren()[0];
		URIConverter uriConverter = modelChange.getURIConverter();
		Map<URI, URI> uriMapping = uriConverter.getURIMap();
		URI sourceUri = uriMapping.get(refactoredUri);
		VCObject vcobject = sourceUtils.getDependency(sourceUri);
		EList<CharacteristicReference_C> inferences = source.getInferences();
		if(vcobject instanceof Constraint) {
			Constraint constraint = (Constraint)vcobject;
			Condition condition = source.getCondition();
			EList<ConstraintObject> objects = source.getObjects();
			EList<VCObject> vcobjects = (EList<VCObject>)constraint.eContainer().eGet(VCML_PACKAGE.getVcmlModel_Objects());
			Resource resource = source.eResource();
			ResourceSet resourceSet = resource.getResourceSet();
			String newUriPart = sourceUri.trimFileExtension().toString();
			String fileExtension = resource.getURI().fileExtension();

			Map<?, ?> attributes = context.getAttributes();
			Object object = attributes.get(DefaultRefactoringExecuter.TEXT_FIELD_ENTRY);
			if(object instanceof String) {
				try {
					Integer constraintsAmount = Integer.parseInt((String)object);
					Collection<ConstraintRestriction> restrictionsCopy = EcoreUtil.copyAll(restrictions);
					for(int index=1, endIndex=0, loops=restrictionsCopy.size() / constraintsAmount, size=restrictionsCopy.size(); index<=loops; index++) { 
						StringBuffer nameBuffer = new StringBuffer(constraint.getName()).append("_").append(index);
						int startIndex = endIndex == 0 ? index * constraintsAmount : endIndex;
						int tempStartIndex = startIndex * MINIMUM_SUBLISTS;
						endIndex = tempStartIndex < size ? tempStartIndex : size;
						
						List<ConstraintRestriction> subList = restrictions.subList(startIndex, endIndex);
						ConstraintSource newConstraintSource = createConstraintSource(objects, condition, subList, inferences);
						restrictionsCopy.removeAll(subList);
						if(newConstraintSource.getRestrictions().isEmpty()) {
							continue;
						}
						
						String newConstraintName = nameBuffer.toString();
						VCObject foundEntry = search.findEntry(newConstraintName, VCML_PACKAGE.getConstraint(), vcobjects);
						if(foundEntry == null) {
							Description description = constraint.getDescription();
							if(description instanceof SimpleDescription) {
								String simpleDescription = ((SimpleDescription)description).getValue();
								Constraint newConstraint = VCMLObjectUtils.mkConstraint(nameBuffer.toString(), 
										VCMLObjectUtils.mkSimpleDescription(simpleDescription));
								vcobjects.add(newConstraint);											
							}
							// TODO behavior undefined for MultiLanguageDescriptions
						}
						 
						StringBuffer uriBuffer = new StringBuffer(newUriPart).append("_").append(index).append(".").append(fileExtension);
						URI uri = URI.createURI(uriBuffer.toString());
						Resource newConstraintResource = null;
						try {
							newConstraintResource = resourceSet.getResource(uri, true);
						} catch(Exception exception) {
							newConstraintResource = resourceSet.getResource(uri, true);
						}
						EList<EObject> contents = newConstraintResource.getContents();
						contents.add(newConstraintSource);				
					}
					restrictions.clear();
					restrictions.addAll(restrictionsCopy);
					List<Characteristic> referenced = Lists.newArrayList();
					for(int index=restrictions.size()-1; index>=0; index--) {
						if(constraintsAmount > index) {
							ConstraintRestriction cr = restrictions.get(index);
							List<Characteristic> cstics = cre.getUsedCharacteristics(cr);
							referenced.addAll(cstics);
						} else {
							restrictions.remove(index);
						}
					}
					BasicEList<CharacteristicReference_C> newInferences = new BasicEList<CharacteristicReference_C>();
					for(CharacteristicReference_C cstic_ref_c : inferences) {
						Characteristic currentlyUsed = null;
						if(cstic_ref_c instanceof ShortVarReference) {
							currentlyUsed = ((ShortVarReference)cstic_ref_c).getRef().getCharacteristic();
						} else if(cstic_ref_c instanceof ObjectCharacteristicReference) {
							currentlyUsed = ((ObjectCharacteristicReference)cstic_ref_c).getCharacteristic();
						}
						if(referenced.contains(currentlyUsed)) {
							newInferences.add(cstic_ref_c);
						}
					}
					inferences.clear();
					inferences.addAll(newInferences);
				} catch(NumberFormatException exception) {
					throw exception;
				}
			}
		}
	}
	
	/*
	 * Re-factoring: executes in-line operation for each restriction 
	 */
	public void refactoring_Inline_ConstraintSource(IRefactoringContext context) {
		EObject element = context.getSourceElement();
		ConstraintSource source = EcoreUtil2.getContainerOfType(element, ConstraintSource.class);
		inline_Condition(source);
		context.setSourceElement(source);
	}
	
	public void extract_Condition(ConstraintSource source) {
		ConditionalConstraintRestriction ccr = cre.canExtractCommonConditions(source);
		if(ccr != null) {
			EList<ConstraintRestriction> restrictions = source.getRestrictions();
			Condition condition = EcoreUtil.copy(ccr.getCondition());
			source.setCondition(condition);			
			BasicEList<ConstraintRestriction> newRestrictions = new BasicEList<ConstraintRestriction>();
			for(ConstraintRestriction restriction : restrictions) {
				ConditionalConstraintRestriction restriction3 = (ConditionalConstraintRestriction)restriction;
				ConstraintRestriction restriction2 = restriction3.getRestriction();
				ConstraintRestriction copy = EcoreUtil.copy(restriction2);
				newRestrictions.add(copy);
			}
			source.getRestrictions().clear();
			source.getRestrictions().addAll(newRestrictions);
		}
	}
	
	public void inline_Condition(ConstraintSource source) {
		Comparison previous = null;
		EList<ConstraintRestriction> restrictions = source.getRestrictions();
		for(ConstraintRestriction restriction : restrictions) {
			if(previous == null) {
				if(!(restriction instanceof Comparison)) {
					break;
				}
				previous = (Comparison)restriction;
				continue;
			} 
		}
		if(previous != null) {
			Condition condition = source.getCondition();
			source.setCondition(null);
			List<ConstraintRestriction> newRestrictions = Lists.newArrayList();
			for(ConstraintRestriction restriction : restrictions) {
				ConditionalConstraintRestriction ccr = VCML_FACTORY.createConditionalConstraintRestriction();
				ccr.setCondition(EcoreUtil.copy(condition));
				ccr.setRestriction(EcoreUtil.copy(restriction));
				newRestrictions.add(ccr);
			}
			restrictions.clear();
			restrictions.addAll(newRestrictions);
		}
	}

	protected ConstraintSource createConstraintSource(EList<ConstraintObject> objects, Condition condition, List<ConstraintRestriction> restrictions, EList<CharacteristicReference_C> inferences) {
		ConstraintSource constraintSource = VCMLObjectUtils.mkConstraintSource();
		
		Collection<ConstraintObject> objectsCopy = EcoreUtil.copyAll(objects);
		constraintSource.getObjects().addAll(objectsCopy);
		
		Condition conditionCopy = EcoreUtil.copy(condition);
		constraintSource.setCondition(conditionCopy);
		
		List<Characteristic> referenced = Lists.newArrayList();
		EList<ConstraintRestriction> newRestrictions = constraintSource.getRestrictions();
		for(ConstraintRestriction cr : restrictions) {
			ConstraintRestriction crc = EcoreUtil.copy(cr);
			referenced.addAll(cre.getUsedCharacteristics(crc));
			newRestrictions.add(crc);
		}
		
		EList<CharacteristicReference_C> newInferences = constraintSource.getInferences();
		for(CharacteristicReference_C cstic_ref_c : inferences) {
			Characteristic currentlyUsed = null;
			if(cstic_ref_c instanceof ShortVarReference) {
				currentlyUsed = ((ShortVarReference)cstic_ref_c).getRef().getCharacteristic();
			} else if(cstic_ref_c instanceof ObjectCharacteristicReference) {
				currentlyUsed = ((ObjectCharacteristicReference)cstic_ref_c).getCharacteristic();
			}
			if(!referenced.contains(currentlyUsed)) {
				continue;
			} else {
				CharacteristicReference_C copy = EcoreUtil.copy(cstic_ref_c);
				newInferences.add(copy);
			}
		}
		return constraintSource;
	}
}
