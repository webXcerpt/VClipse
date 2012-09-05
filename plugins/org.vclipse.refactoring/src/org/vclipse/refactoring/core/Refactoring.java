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
package org.vclipse.refactoring.core;

import java.lang.reflect.Method;
import java.util.List;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.change.ChangeDescription;
import org.eclipse.emf.ecore.change.ChangeFactory;
import org.eclipse.emf.ecore.change.ChangeKind;
import org.eclipse.emf.ecore.change.FeatureChange;
import org.eclipse.emf.ecore.change.ListChange;
import org.eclipse.emf.ecore.change.util.ListDifferenceAnalyzer;
import org.eclipse.xtext.nodemodel.impl.NodeModelBuilder;
import org.eclipse.xtext.util.Pair;
import org.vclipse.refactoring.RefactoringPlugin;

import com.google.common.collect.Lists;
import com.google.inject.Inject;

public abstract class Refactoring extends MethodCollector {

	public static String BUTTON_STATE = "button_state";
	public static String TEXT_FIELD_ENTRY = "text_field_entry";
	
	public static final String REFACTORING_PREFIX = "refactoring_";
	
	protected ChangeFactory changeFactory;
	
	@Inject
	protected NodeModelBuilder nodeModelBuilder;
	
	@Inject
	protected ReferenceFinderExtension referencesFinder;
	
	public Refactoring() {
		collect(1, IRefactoringContext.class);
		collect(2);
		changeFactory = ChangeFactory.eINSTANCE;
	}
	
	@SuppressWarnings("unchecked")
	public List<EObject> refactor(IRefactoringContext context) {
		List<EObject> changes = Lists.newArrayList();
		EObject element = context.getSourceElement();
		String prefix = REFACTORING_PREFIX + context.getType();
		Pair<EObject, Method> pair = getMethod(element, context.getStructuralFeature(), prefix);
		if(pair != null) {
			try {
				Object invoke = pair.getSecond().invoke(this, new Object[]{context, pair.getFirst()});
				changes.addAll((List<EObject>)invoke);
			} catch (Exception exception) {
				RefactoringPlugin.log(exception.getMessage(), exception);
			}
		} 
		return changes;	
	}
	
	public void refactor(EObject object) {
		IRefactoringContext context = new RefactoringContext();
		context.setSourceElement(object);
		context.setType(RefactoringType.Replace);
		refactor(context);
	}
	
	public Pair<EObject, Method> getRefactoring(IRefactoringContext context) {
		String prefix = REFACTORING_PREFIX + context.getType();
		return getMethod(context.getSourceElement(), context.getStructuralFeature(), prefix);
	}

	protected ChangeDescription getChangeDescription(EObject object, EStructuralFeature feature, EObject value) {
		// TODO extract comment handling - commented out at the moment - exception during extraction
		
//		HiddenLeafNode comment = null;
//		ICompositeNode node = NodeModelUtils.getNode(object);
//		BidiTreeIterator<INode> iterator = node.getAsTreeIterable().iterator();
//		while(iterator.hasNext()) {
//			INode next = iterator.next();
//			if(next instanceof HiddenLeafNode) {
//				HiddenLeafNode hln = (HiddenLeafNode)next;
//				EObject grammarElement = hln.getGrammarElement();
//				if(grammarElement instanceof TerminalRule) {
//					TerminalRule tr = (TerminalRule)grammarElement;
//					if(tr.getName().contains("COMMENT")) {
//						comment = hln;
//						break;
//					}
//				}
//			}
//		}
		
		ChangeDescription description = changeFactory.createChangeDescription();
		FeatureChange featureChange = changeFactory.createFeatureChange(feature, object.eGet(feature), true);
		featureChange.setFeatureName(feature.getName());
		featureChange.setFeature(feature);
		featureChange.setReferenceValue(value);
		EList<FeatureChange> changes = new BasicEList<FeatureChange>();
		changes.add(featureChange);
		description.getObjectChanges().put(object, changes);
		description.applyAndReverse();
		
//		if(comment != null) {
//			ILeafNode newLeafNode = nodeModelBuilder.newLeafNode(comment.getOffset(), comment.getLength(), comment.getGrammarElement(), true, null, node);
// 
//			// TODO - the comment code is deleted without this line, there is an exception with with line after the first replacement		
//			nodeModelBuilder.replaceAndTransferLookAhead(comment, newLeafNode);
//		}
		
		return description;
	}
	
	public ListChange getListChange(ChangeKind changekind, EStructuralFeature structuralfeature, EObject object, EList<?> originalentries) {
		ListChange change = changeFactory.createListChange();
		change.setKind(changekind);
		change.setFeature(structuralfeature);
		change.getReferenceValues().add(object);
		if(changekind == ChangeKind.REMOVE_LITERAL) {
			change.setIndex(originalentries.indexOf(object));
		}
		change.applyAndReverse(getObjectList(originalentries));
		return change;
	}
	
	public void handleChangeOnList(List<EObject> changes, EObject object, EList<EObject> values) {
		ListDifferenceAnalyzer analyzer = new ListDifferenceAnalyzer();
		EList<EObject> copy = new BasicEList<EObject>(values);
		copy.remove(object);
		for(ListChange change : analyzer.analyzeLists(values, copy)) {
			change.applyAndReverse(getObjectList(values));
			changes.add(change);
		}
	}
 
	public void removeFromList(EList<EObject> changes, EObject object, EStructuralFeature feature, EList<Object> originalentries) {
		ListDifferenceAnalyzer analyzer = new ListDifferenceAnalyzer();
		EList<Object> copy = new BasicEList<Object>(originalentries);
		copy.remove(object);
		for(ListChange change : analyzer.analyzeLists(copy, originalentries)) {
			change.applyAndReverse(getObjectList(originalentries));
			changes.add(change);
		}
	}

	@SuppressWarnings("unchecked")
	public EList<Object> getObjectList(EList<?> elements) {
		return (EList<Object>)elements;
	}
	
	@SuppressWarnings("unchecked")
	public EList<EObject> getEObjectList(EList<?> elements) {
		return (EList<EObject>)elements;
	}
}
