/** 
 * Copyright (c) 2010 - 2015 webXcerpt Software GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * webXcerpt Software GmbH - initial creator
 * www.webxcerpt.com
 */
package org.vclipse.idoc.formatting

import org.eclipse.xtext.formatting.impl.AbstractDeclarativeFormatter
import org.eclipse.xtext.formatting.impl.FormattingConfig
import org.vclipse.idoc.services.IDocGrammarAccess

class IDocFormatter extends AbstractDeclarativeFormatter {

	override protected void configureFormatting(FormattingConfig config) {
		val IDocGrammarAccess ga = grammarAccess as IDocGrammarAccess;
		config => [
			autoLinewrap = 120;
			setLinewrap.before(ga.IDocAccess.rightCurlyBracketKeyword_7)
			setLinewrap(2).after(ga.IDocAccess.rightCurlyBracketKeyword_7)
			setIndentation(ga.IDocAccess.leftCurlyBracketKeyword_4,
				ga.IDocAccess.rightCurlyBracketKeyword_7)
			setLinewrap.before(ga.segmentAccess.segmentKeyword_0)
			setLinewrap.before(ga.segmentAccess.rightCurlyBracketKeyword_5)
			setIndentation(ga.segmentAccess.leftCurlyBracketKeyword_2,
				ga.segmentAccess.rightCurlyBracketKeyword_5)
		]
	}

}
