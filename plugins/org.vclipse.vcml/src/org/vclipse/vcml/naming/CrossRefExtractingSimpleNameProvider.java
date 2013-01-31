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
package org.vclipse.vcml.naming;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.CrossReference;
import org.eclipse.xtext.naming.QualifiedName;
import org.eclipse.xtext.nodemodel.ICompositeNode;
import org.eclipse.xtext.nodemodel.INode;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;

public class CrossRefExtractingSimpleNameProvider extends org.eclipse.xtext.naming.SimpleNameProvider {

	@Override
	public QualifiedName getFullyQualifiedName(EObject object) {
		QualifiedName fullyQualifiedName = super.getFullyQualifiedName(object);
		if(fullyQualifiedName == null) {
			fullyQualifiedName = getNameFromCrossReference(object);
		}
		return fullyQualifiedName;
	}
	
	protected QualifiedName getNameFromCrossReference(EObject object) {
		ICompositeNode cnode = NodeModelUtils.getNode(object);
		if(cnode == null) {
			return null;
		}
		for(INode node : cnode.getChildren()) {
			if(node.getGrammarElement() instanceof CrossReference) {
				String name = NodeModelUtils.getTokenText(node);
				return  QualifiedName.create(name);
			}
		}
		return null;
	}
}
