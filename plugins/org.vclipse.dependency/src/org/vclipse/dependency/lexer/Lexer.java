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
