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
package org.vclipse.dependency.formatting;

import java.io.IOException;
import java.util.List;

import org.eclipse.xtext.formatting.impl.NodeModelStreamer;
import org.eclipse.xtext.nodemodel.ICompositeNode;
import org.eclipse.xtext.nodemodel.ILeafNode;
import org.eclipse.xtext.nodemodel.INode;
import org.eclipse.xtext.parsetree.reconstr.ITokenStream;
import org.eclipse.xtext.parsetree.reconstr.ITokenStreamExtension;
import org.eclipse.xtext.util.ITextRegion;
import org.eclipse.xtext.util.TextRegion;
import org.vclipse.dependency.services.DependencyGrammarAccess;

import com.google.inject.Inject;

public class SourceCommentHandlingStreamer extends NodeModelStreamer {

	@Inject
	private DependencyGrammarAccess grammarAccess;
	
	@Override
	public ITextRegion feedTokenStream(ITokenStream out, ICompositeNode in, int offset, int length) throws IOException {
		List<INode> nodes = getLeafs(in, offset, offset + length);
		if (nodes.isEmpty())
			return new TextRegion(in.getOffset(), 0);
		if (out instanceof ITokenStreamExtension)
			((ITokenStreamExtension) out).init(findRootRuleForRegion(nodes.get(0)));
		boolean lastIsTokenOrComment = false;
		for (INode node : nodes) {
			if(tokenUtil.isCommentNode(node)) {
				out.writeHidden(grammarAccess.getSL_COMMENTRule(), "\n" + node.getText());
				continue;
			}
			boolean currentIsTokenOrComment = tokenUtil.isCommentNode(node) || tokenUtil.isToken(node);
			if (lastIsTokenOrComment && currentIsTokenOrComment)
				writeHiddenEmpty(out);
			lastIsTokenOrComment = currentIsTokenOrComment;
			if (node instanceof ILeafNode) {
				ILeafNode leaf = (ILeafNode) node;
				if (leaf.isHidden())
					writeHidden(out, leaf);
				else
					writeSemantic(out, leaf);
			} else if (node instanceof ICompositeNode)
				writeSemantic(out, (ICompositeNode) node);
		}
		out.flush();
		int rStart = nodes.get(0).getOffset();
		int rLength = (nodes.get(nodes.size() - 1).getOffset() + nodes.get(nodes.size() - 1).getLength()) - rStart;
		return new TextRegion(rStart, rLength);
	}
}
