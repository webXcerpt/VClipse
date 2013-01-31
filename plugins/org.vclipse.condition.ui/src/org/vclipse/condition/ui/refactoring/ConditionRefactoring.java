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
package org.vclipse.condition.ui.refactoring;

import java.util.Set;

import org.eclipse.emf.ecore.EClass;
import org.vclipse.vcml.refactoring.VCMLRefactoring;

import com.google.common.collect.Sets;

public class ConditionRefactoring extends VCMLRefactoring {

	@Override
	public Set<EClass> getTopLevelTypes() {
		return Sets.newHashSet(VCML_PACKAGE.getConditionSource());
	}
}
