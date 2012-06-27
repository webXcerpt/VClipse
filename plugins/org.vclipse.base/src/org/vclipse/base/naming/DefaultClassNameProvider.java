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
package org.vclipse.base.naming;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;

public class DefaultClassNameProvider implements IClassNameProvider {

	public String getClassName(EClass cls) {
		return cls.getName();
	}

	public String getClassName(EObject o) {
		return getClassName(o.eClass());
	}

}
