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
package org.vclipse.vcml.ui.quickfix;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.nodemodel.ICompositeNode;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;
import org.eclipse.xtext.ui.editor.model.IXtextDocument;
import org.eclipse.xtext.ui.editor.model.edit.IModificationContext;
import org.eclipse.xtext.ui.editor.model.edit.ISemanticModification;
import org.vclipse.vcml.vcml.VCObject;

public class DefaultSemanticModification implements ISemanticModification {

	private String textToInsert;
	
	public DefaultSemanticModification(String textToInsert) {
		this.textToInsert = textToInsert;
	}
	
	public void apply(EObject element, IModificationContext context) throws Exception {
		EObject container = EcoreUtil2.getContainerOfType(element, VCObject.class);
		ICompositeNode node = NodeModelUtils.getNode(container);
		IXtextDocument xtextDocument = context.getXtextDocument();
		textToInsert = "\n" + computePrefix(xtextDocument, node.getOffset()) + textToInsert;
		xtextDocument.replace(node.getTotalOffset() + node.getTotalLength(), 0, textToInsert);
	}
	
	protected String computePrefix(IXtextDocument doc, int offset) {
		String text = doc.get();
		String prefix = "";
		for(int i=offset; offset>-1; i--) {
			if(text.charAt(i) == '\n') {
				return prefix;
			} else if(text.charAt(i) == '\t') {
				prefix += '\t';
			} else if(text.charAt(i) == ' '){
				prefix += " ";
			}
		}
		return prefix;
	}

}
