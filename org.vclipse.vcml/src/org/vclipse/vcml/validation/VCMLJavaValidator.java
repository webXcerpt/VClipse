/*******************************************************************************
 * Copyright (c) 2010 webXcerpt Software GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    webXcerpt Software GmbH - initial creator
 ******************************************************************************/
package org.vclipse.vcml.validation;

import java.util.List;

import org.eclipse.xtext.validation.Check;
import org.eclipse.xtext.validation.CheckType;
import org.vclipse.vcml.utils.VCMLUtils;
import org.vclipse.vcml.vcml.BillOfMaterial;
import org.vclipse.vcml.vcml.Characteristic;
import org.vclipse.vcml.vcml.Class;
import org.vclipse.vcml.vcml.ConfigurationProfile;
import org.vclipse.vcml.vcml.Constraint;
import org.vclipse.vcml.vcml.DependencyNet;
import org.vclipse.vcml.vcml.Material;
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
	private static final int MAXLENGTH_MATERIAL_NAME = 18;

	/*
	 * @Check(CheckType.EXPENSIVE) //executed upon validate action in context menu
     * @Check(CheckType.NORMAL) //upon save
     * @Check(CheckType.FAST) //while editig 
	 */

	@Check(CheckType.FAST)
	public void checkCharacteristic(final Characteristic object) {
		if (object.getName().length() > MAXLENGTH_NAME) {
			error("Name of characteristic is limited to " + MAXLENGTH_NAME + " characters", VcmlPackage.Literals.VC_OBJECT__NAME);
		}
	}

	@Check(CheckType.FAST)
	public void checkProcedure(final Procedure object) {
		if (object.getName().length() > MAXLENGTH_NAME) {
			error("Name of procedure is limited to " + MAXLENGTH_NAME + " characters", VcmlPackage.Literals.VC_OBJECT__NAME);
		}
	}

	@Check(CheckType.FAST)
	public void checkDependencyNet(final DependencyNet object) {
		if (object.getName().length() > MAXLENGTH_NAME) {
			error("Name of dependency net is limited to " + MAXLENGTH_NAME + " characters", VcmlPackage.Literals.VC_OBJECT__NAME);
		}
		if (object.getConstraints().size() > MAXLENGTH_DEPENDENCYNET_CHARACTERISTICS) {
			warning("Dependency net " + object.getName() + " too large, should have for efficiency at most " + MAXLENGTH_DEPENDENCYNET_CHARACTERISTICS + " constraints", VcmlPackage.Literals.VC_OBJECT__NAME);
		}
	}

	@Check(CheckType.FAST)
	public void checkConstraint(final Constraint object) {
		if (object.getName().length() > MAXLENGTH_NAME) {
			error("Name of constraint is limited to " + MAXLENGTH_NAME + " characters", VcmlPackage.Literals.VC_OBJECT__NAME);
		}
	}

	@Check(CheckType.FAST)
	public void checkSelectionCondition(final SelectionCondition object) {
		if (object.getName().length() > MAXLENGTH_NAME) {
			error("Name of selection condition is limited to " + MAXLENGTH_NAME + " characters", VcmlPackage.Literals.VC_OBJECT__NAME);
		}
	}

	@Check(CheckType.FAST)
	public void checkPrecondition(final Precondition object) {
		if (object.getName().length() > MAXLENGTH_NAME) {
			error("Name of precondition is limited to " + MAXLENGTH_NAME + " characters", VcmlPackage.Literals.VC_OBJECT__NAME);
		}
	}

	@Check(CheckType.FAST)
	public void checkClass(final Class object) {
		if (VCMLUtils.getClassName(object.getName()).length() > MAXLENGTH_CLASS_NAME) {
			error("Name of class is limited to " + MAXLENGTH_CLASS_NAME + " characters", VcmlPackage.Literals.VC_OBJECT__NAME);
		}
		if (object.getCharacteristics().size() > MAXLENGTH_CLASS_CHARACTERISTICS) {
			error("Number of characteristics of a class is limited to " + MAXLENGTH_CLASS_CHARACTERISTICS, VcmlPackage.Literals.VC_OBJECT__NAME);
		}
	}

	@Check(CheckType.FAST)
	public void checkDescription(final SimpleDescription desc) {
		if (desc.getValue().length() > MAXLENGTH_DESCRIPTION) {
			warning("Descriptions are limited to " + MAXLENGTH_DESCRIPTION + " characters", VcmlPackage.Literals.SIMPLE_DESCRIPTION__VALUE);
		}
	}

}
