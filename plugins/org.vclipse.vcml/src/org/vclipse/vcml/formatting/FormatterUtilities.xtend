/*******************************************************************************
 * Copyright (c) 2012 webXcerpt Software GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    webXcerpt Software GmbH - initial creator
 ******************************************************************************/
package org.vclipse.vcml.formatting

import org.eclipse.xtext.IGrammarAccess
import org.eclipse.xtext.formatting.impl.FormattingConfig
import org.eclipse.xtext.Keyword
import org.eclipse.xtext.util.Pair

class FormatterUtilities {
	
	// formatting for the vcml content enclosed in curly braces
	def handleCurlyBraces(IGrammarAccess grammarAccess, FormattingConfig config, String left, String right) {
		for(Pair<Keyword, Keyword> pair : grammarAccess.findKeywordPairs(left, right)) {
			val open = pair.getFirst();
			config.setIndentationIncrement().after(open);
			config.setLinewrap().after(open);
			val close = pair.getSecond();
			config.setIndentationDecrement().before(close);
			config.setLinewrap().around(close);
		}
	}
	
	// formatting for the vcml content enclosed in braces like [], ()
	def handleNoSpaceNoLineWrapBraces(IGrammarAccess grammarAccess, FormattingConfig config, String left, String right) {
		for(Pair<Keyword, Keyword> pair : grammarAccess.findKeywordPairs(left, right)) {
			config.setNoSpace().after(pair.getFirst());
			config.setNoSpace().before(pair.getSecond());
			config.setNoLinewrap().between(pair.getFirst(), pair.getSecond());
		}
	}
}