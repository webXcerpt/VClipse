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
package org.vclipse.base;

import java.util.Collections;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.documentation.IEObjectDocumentationProvider;
import org.eclipse.xtext.util.Exceptions;
import org.eclipse.xtext.util.PolymorphicDispatcher;
import org.eclipse.xtext.util.PolymorphicDispatcher.ErrorHandler;

public class DeclarativeEObjectDocumentationProvider implements IEObjectDocumentationProvider {

	private final PolymorphicDispatcher<String> documentationDispatcher = new PolymorphicDispatcher<String>("documentation", 1, 1,
			Collections.singletonList(this), new ErrorHandler<String>() {
				public String handle(Object[] params, Throwable e) {
					return Exceptions.throwUncheckedException(e);
				}
			});

	public Object documentation(Object element) {
		return null;
	}
	
	public Object documentation(Void element) {
		return null;
	}
	
	@Override
	public String getDocumentation(EObject o) {
		String documentation = documentationDispatcher.invoke(o);
		if (documentation != null) {
			return documentation;
		}
		return null;
	}

}
