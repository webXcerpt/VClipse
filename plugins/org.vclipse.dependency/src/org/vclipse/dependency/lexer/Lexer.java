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
package org.vclipse.dependency.lexer;

import org.antlr.runtime.CharStream;
import org.antlr.runtime.RecognizerSharedState;

public abstract class Lexer extends org.eclipse.xtext.parser.antlr.Lexer {

	public Lexer() {
	}

	public Lexer(CharStream input, RecognizerSharedState state) {
		super(input, state);
	}

	public boolean atStartOfLine() {
		return input.getCharPositionInLine() == 0;
	}

}
