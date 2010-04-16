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
package org.vclipse.vcml.validation;

import org.eclipse.xtext.validation.Check;
import org.eclipse.xtext.validation.CheckType;
import org.vclipse.vcml.utils.VCMLUtils;
import org.vclipse.vcml.vcml.Characteristic;
import org.vclipse.vcml.vcml.Class;
import org.vclipse.vcml.vcml.Constraint;
import org.vclipse.vcml.vcml.DependencyNet;
import org.vclipse.vcml.vcml.Precondition;
import org.vclipse.vcml.vcml.Procedure;
import org.vclipse.vcml.vcml.SelectionCondition;
import org.vclipse.vcml.vcml.SimpleDescription;
import org.vclipse.vcml.vcml.VcmlPackage;


public class VCMLJavaValidator extends AbstractVCMLJavaValidator {

	private static final int MAXLENGTH_CLASS_CHARACTERISTICS = 999; // SAP limit because cstic index in class table has size 3
	private static final int MAXLENGTH_CLASS_NAME = 18;
	private static final int MAXLENGTH_NAME = 30;
	private static final int MAXLENGTH_DESCRIPTION = 30;
	private static final int MAXLENGTH_DEPENDENCYNET_CHARACTERISTICS = 50; // soft limit of size of dependency net (should not be larger because compilation has a O(n^2) algorithm)

	/*
	 * @Check(CheckType.EXPENSIVE) //executed upon validate action in context menu
     * @Check(CheckType.NORMAL) //upon save
     * @Check(CheckType.FAST) //while editig 
	 */
	
	@Check(CheckType.NORMAL)
	public void checkCharacteristic(Characteristic object) {
		if (object.getName().length() > MAXLENGTH_NAME) {
			error("Name of characteristic is limited to " + MAXLENGTH_NAME + " characters", VcmlPackage.CHARACTERISTIC__NAME);
		}
	}
	
	@Check(CheckType.NORMAL)
	public void checkProcedure(Procedure object) {
		if (object.getName().length() > MAXLENGTH_NAME) {
			error("Name of procedure is limited to " + MAXLENGTH_NAME + " characters", VcmlPackage.PROCEDURE__NAME);
		}
	}
	
	@Check(CheckType.NORMAL)
	public void checkDependencyNet(DependencyNet object) {
		if (object.getName().length() > MAXLENGTH_NAME) {
			error("Name of dependency net is limited to " + MAXLENGTH_NAME + " characters", VcmlPackage.DEPENDENCY_NET__NAME);
		}
		if (object.getConstraints().size() > MAXLENGTH_DEPENDENCYNET_CHARACTERISTICS) {
			warning("Dependency net " + object.getName() + " too large, should have for efficiency at most " + MAXLENGTH_DEPENDENCYNET_CHARACTERISTICS + " constraints", VcmlPackage.DEPENDENCY_NET__CONSTRAINTS);
		}
	}
	
	@Check(CheckType.NORMAL)
	public void checkConstraint(Constraint object) {
		if (object.getName().length() > MAXLENGTH_NAME) {
			error("Name of constraint is limited to " + MAXLENGTH_NAME + " characters", VcmlPackage.CONSTRAINT__NAME);
		}
	}
	
	@Check(CheckType.NORMAL)
	public void checkSelectionCondition(SelectionCondition object) {
		if (object.getName().length() > MAXLENGTH_NAME) {
			error("Name of selection condition is limited to " + MAXLENGTH_NAME + " characters", VcmlPackage.SELECTION_CONDITION__NAME);
		}
	}
	
	@Check(CheckType.NORMAL)
	public void checkPrecondition(Precondition object) {
		if (object.getName().length() > MAXLENGTH_NAME) {
			error("Name of precondition is limited to " + MAXLENGTH_NAME + " characters", VcmlPackage.PRECONDITION__NAME);
		}
	}
	
	@Check(CheckType.NORMAL)
	public void checkClass(Class object) {
		if (VCMLUtils.getClassName(object.getName()).length() > MAXLENGTH_CLASS_NAME) {
			error("Name of class is limited to " + MAXLENGTH_CLASS_NAME + " characters", VcmlPackage.CLASS__NAME);
		}
		if (object.getCharacteristics().size() > MAXLENGTH_CLASS_CHARACTERISTICS) {
			error("Number of characteristics of a class is limited to " + MAXLENGTH_CLASS_CHARACTERISTICS, VcmlPackage.CLASS__CHARACTERISTICS);
		}
	}

	@Check(CheckType.NORMAL)
	public void checkDescription(SimpleDescription desc) {
		if (desc.getValue().length() > MAXLENGTH_DESCRIPTION) {
			error("Descriptions are limited to " + MAXLENGTH_DESCRIPTION + " characters", VcmlPackage.SIMPLE_DESCRIPTION__VALUE);
		}
	}

}
