/*******************************************************************************
 * Copyright (c) 2010 webXcerpt Software GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    webXcerpt Software GmbH - initial creator
 *******************************************************************************/
package org.vclipse.vcml.resource;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.resource.IFragmentProvider;
import org.vclipse.vcml.vcml.Constraint;
import org.vclipse.vcml.vcml.ConstraintObject;
import org.vclipse.vcml.vcml.Model;
import org.vclipse.vcml.vcml.VCObject;
import org.vclipse.vcml.vcml.VcmlFactory;


public class VCMLFragmentProvider implements IFragmentProvider {

	private static final String CONSTRAINT_OBJECT_CLASSIFIER = VcmlFactory.eINSTANCE.createConstraintObject().eClass().getClassifierID()+"";

	public EObject getEObject(Resource resource, String fragment) {
		for(EObject o  : resource.getContents()) {
			if (o instanceof Model) {
				Model model = (Model)o;
				int i = fragment.indexOf('#');
				String fragmentClassifier = fragment.substring(0,i);
				String fragmentName = fragment.substring(i+1,fragment.length());
				if (fragmentClassifier.equals(CONSTRAINT_OBJECT_CLASSIFIER)) {
					int j = fragmentName.indexOf('#');
					String constraintName = fragmentName.substring(0,j);
					String constraintObjectName = fragmentName.substring(j+1,fragmentName.length());
					for(VCObject so : model.getObjects()) {
						if (so instanceof Constraint) {
							if (so.getName().equals(constraintName)) {
								for(ConstraintObject co : ((Constraint)so).getSource().getObjects()) {
									if (co.getName().equals(constraintObjectName)) {
										return co;
									}
								}
							}
						}
					}	
				} else {
					for(VCObject so : model.getObjects()) {
						String classifier = ""+so.eClass().getClassifierID();
						if (fragmentClassifier.equals(classifier) &&
								so.getName().equals(fragmentName)) {
							return so;
						}
					}	
				}
			}	
		}
		return null;
	}

	public String getFragment(EObject obj) {
		// System.err.println("getFragment " + obj);
		if (obj instanceof VCObject) {
			VCObject so = (VCObject)obj;
			String result = so.eClass().getClassifierID() + "#" + so.getName();
			// System.err.println("  = " + result);
			return result;
		} else if (obj instanceof ConstraintObject) {
			ConstraintObject co = (ConstraintObject)obj;
			String result = co.eClass().getClassifierID() + "#" + ((Constraint)co.eContainer().eContainer()).getName() + "#" + co.getName();
			// System.err.println("  = " + result);
			return result;
			/*
		} else if (obj instanceof Function) {
			Function f = (Function)obj;
			String result = f.eClass().getClassifierID() + "#" + f.getFunction();
			System.err.println("  = " + result);
			return result;
			*/
		}
		// EStructuralFeature eContainingFeature = obj.eContainingFeature();
		// System.err.println("  containing feature: " + (eContainingFeature==null?null:eContainingFeature.getName()));
		return "NA";
	}

}
