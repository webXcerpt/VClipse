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
package org.vclipse.base.naming;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;

import com.google.inject.ImplementedBy;

/**
 * Provides readable class names for EObjects and EClasses. To be used in
 * generic label and hover providers to avoid the output of Java class names to
 * the user.
 */
@ImplementedBy(DefaultClassNameProvider.class)
public interface IClassNameProvider {

	public String getClassName(EClass cls);

	public String getClassName(EObject cls);

}
