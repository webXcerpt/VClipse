/*******************************************************************************
 * Copyright (c) 2012 webXcerpt Software GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    webXcerpt Software GmbH - initial creator
 ******************************************************************************/
package org.vclipse.vcml.ui.refactoring;

import java.util.Set;

import org.eclipse.emf.ecore.EClass;
import org.vclipse.refactoring.core.ContainerPreviewComputer;
import org.vclipse.vcml.vcml.VcmlPackage;

import com.google.common.collect.Sets;

public class PreviewEntityComputer extends ContainerPreviewComputer {

	protected static final VcmlPackage VCML_PACKAGE = VcmlPackage.eINSTANCE;

	@Override
	public Set<EClass> getFavoredTypes() {
		return Sets.newHashSet(VCML_PACKAGE.getVCObject(), VCML_PACKAGE.getCondition(), VCML_PACKAGE.getConstraint(), VCML_PACKAGE.getProcedure());
	}

	@Override
	public Set<EClass> getIgnoreTypes() {
		return Sets.newHashSet(VCML_PACKAGE.getVcmlModel(), VCML_PACKAGE.getConstraintSource(), VCML_PACKAGE.getProcedureSource(), VCML_PACKAGE.getConditionSource());
	}
}
