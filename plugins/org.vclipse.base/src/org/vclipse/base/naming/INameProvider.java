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

import org.eclipse.emf.ecore.EObject;

import com.google.common.base.Function;

public interface INameProvider extends Function<EObject, String> {

	/**
	 * @return the name for the given object, <code>null</code> if this {@link INameProvider} is not
	 *         responsible or if the given object doesn't have qualified name.
	 */
	String getName(EObject obj);

	abstract class AbstractImpl implements INameProvider {
		public String apply(EObject from) {
			return getName(from);
		}
	}
}
