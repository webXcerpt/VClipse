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
package org.vclipse.vcml.scoping;

import org.eclipse.emf.ecore.EReference;
import org.eclipse.xtext.scoping.IScope;
import org.eclipse.xtext.scoping.Scopes;
import org.eclipse.xtext.scoping.impl.AbstractDeclarativeScopeProvider;
import org.vclipse.vcml.vcml.Class;
import org.vclipse.vcml.vcml.Classification;

/**
 * This class contains custom scoping description.
 * 
 * see : http://www.eclipse.org/Xtext/documentation/latest/xtext.html#scoping
 * on how and when to use it 
 *
 */
public class VCMLScopeProvider extends AbstractDeclarativeScopeProvider {
	
	IScope scope_ValueAssignment_characteristic(Classification context, EReference ref) {
		Class cls = context.getCls();
		if (cls.getDescription()!=null) {
			// class has a body
			return Scopes.scopeFor(cls.getCharacteristics());
		} else {
			// class has no body - we do not handle this case, but allow all cstics from delegate/global scope
			return null;
		}
	}
	
}
