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
package org.vclipse.vcml.syntaxcoloring;

import org.eclipse.xtext.ui.common.editor.syntaxcoloring.DefaultLexicalHighlightingConfiguration;
import org.eclipse.xtext.ui.common.editor.syntaxcoloring.antlr.DefaultAntlrTokenToAttributeIdMapper;

public class VCMLAntlrTokenToAttributeIdMapper extends DefaultAntlrTokenToAttributeIdMapper {

	@Override
	protected String calculateId(String tokenName, int tokenType) {
		if ("RULE_SYMBOL".equals(tokenName)) { 
			return DefaultLexicalHighlightingConfiguration.STRING_ID;
		} else if ("RULE_DEPENDENCY_COMMENT".equals(tokenName)) { 
			return DefaultLexicalHighlightingConfiguration.COMMENT_ID;
		}
		return super.calculateId(tokenName, tokenType);
	}
	
}
