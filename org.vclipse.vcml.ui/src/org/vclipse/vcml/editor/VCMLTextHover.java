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
/**
 * 
 */
package org.vclipse.vcml.editor;

import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextHoverExtension;
import org.eclipse.jface.text.ITextHoverExtension2;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.xtext.Assignment;
import org.eclipse.xtext.CrossReference;
import org.eclipse.xtext.GrammarUtil;
import org.eclipse.xtext.concurrent.IUnitOfWork;
import org.eclipse.xtext.linking.ILinkingService;
import org.eclipse.xtext.linking.impl.IllegalNodeException;
import org.eclipse.xtext.parser.IParseResult;
import org.eclipse.xtext.parsetree.AbstractNode;
import org.eclipse.xtext.parsetree.NodeUtil;
import org.eclipse.xtext.parsetree.ParseTreeUtil;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.ui.common.editor.hover.XtextTextHover;
import org.eclipse.xtext.ui.core.editor.model.IXtextDocument;
import org.eclipse.xtext.ui.core.editor.model.XtextDocumentUtil;
import org.eclipse.xtext.util.Wrapper;

import com.google.inject.Inject;

/**
 * 
 */
public class VCMLTextHover extends XtextTextHover implements ITextHoverExtension, ITextHoverExtension2 {
	
	@Inject
	ILinkingService linkingService;
	
	@Inject
	IInformationControlCreator informationControlCreator;
	
	@Override
	public IRegion getHoverRegion(ITextViewer textViewer, int offset) {
		return new Region(offset, 0);
	}

	private AbstractNode getAbstractNode(ITextViewer textViewer,
			final IRegion hoverRegion) {
		IXtextDocument doc = XtextDocumentUtil.get(textViewer);
		AbstractNode node = doc.readOnly(new IUnitOfWork<AbstractNode, XtextResource>() {
			public AbstractNode exec(XtextResource resource) throws Exception {
				IParseResult parseResult = resource.getParseResult();
				Assert.isNotNull(parseResult);
				int offset = hoverRegion.getOffset();
				return ParseTreeUtil.getCurrentOrFollowingNodeByOffset(parseResult.getRootNode(), offset);
			}
		});
		return node;
	}

	protected List<EObject> findCrossLinkedEObject(AbstractNode node, Wrapper<Region> location) {
		AbstractNode nodeToCheck = node;
		while(nodeToCheck != null && !(nodeToCheck.getGrammarElement() instanceof Assignment)) {
			if (nodeToCheck.getGrammarElement() instanceof CrossReference) {
				EObject semanticModel = NodeUtil.getNearestSemanticObject(nodeToCheck);
				EReference eReference = GrammarUtil.getReference((CrossReference) nodeToCheck.getGrammarElement(),
						semanticModel.eClass());
				try {
					if (location != null)
						location.set(new Region(nodeToCheck.getOffset(), nodeToCheck.getLength()));
					return linkingService.getLinkedObjects(semanticModel, eReference, nodeToCheck);
				} catch (IllegalNodeException ex) {
					return Collections.emptyList();
				}
			}
			nodeToCheck = nodeToCheck.getParent();
		}
		return Collections.emptyList();
	}
	
	public IInformationControlCreator getHoverControlCreator() {
		return informationControlCreator;
	}

	public Object getHoverInfo2(ITextViewer textViewer, IRegion hoverRegion) {
		AbstractNode node = getAbstractNode(textViewer, hoverRegion);
		List<EObject> crossLinkedEObject = findCrossLinkedEObject(node, null);
		if (crossLinkedEObject.isEmpty())
			return null;
		return crossLinkedEObject.get(0);
	}

}
