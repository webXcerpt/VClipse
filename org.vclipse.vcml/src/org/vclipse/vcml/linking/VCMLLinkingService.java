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
package org.vclipse.vcml.linking;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.xtext.linking.impl.DefaultLinkingService;
import org.eclipse.xtext.linking.impl.IllegalNodeException;
import org.eclipse.xtext.parsetree.AbstractNode;
import org.eclipse.xtext.scoping.IScope;
import org.eclipse.xtext.scoping.IScopedElement;
import org.vclipse.vcml.scoping.HashedScope;


public class VCMLLinkingService extends DefaultLinkingService {

	@Override
	public List<EObject> getLinkedObjects(EObject context, EReference ref, AbstractNode node) throws IllegalNodeException {
		final EClass requiredType = ref.getEReferenceType();
		if (requiredType == null)
			return Collections.<EObject> emptyList();

		final IScope scope = getScope(context, ref);
		final String s = getCrossRefNodeAsString(node);
		if (scope instanceof HashedScope) {
			final IScopedElement element = ((HashedScope)scope).getScopedElementByName(s);
			if (element != null)
				return Collections.singletonList(element.element());
		} else {
			final Iterator<IScopedElement> iterator = scope.getAllContents().iterator();
			if (s != null) {
				while (iterator.hasNext()) {
					final IScopedElement element = iterator.next();
					if (s.equals(element.name()))
						return Collections.singletonList(element.element());
				}
			}
		}
		return Collections.emptyList();
	}

}
