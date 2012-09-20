package org.vclipse.dependency.lexer;

import java.util.List;

import org.antlr.runtime.CharStream;
import org.antlr.runtime.CommonToken;
import org.antlr.runtime.RecognizerSharedState;
import org.antlr.runtime.Token;

import com.google.common.collect.Lists;

public abstract class Lexer extends org.eclipse.xtext.parser.antlr.Lexer {

	public Lexer() {
	}

	public Lexer(CharStream input, RecognizerSharedState state) {
		super(input, state);
	}

	public boolean atStartOfLine() {
		return input.getCharPositionInLine() == 0;
	}

	List<Token> exceedingTokens = Lists.newArrayList();
	int tokenStartCharIndex = 0;

	@Override
	public Token nextToken() {
		Token token = super.nextToken();
		if (token != null) {
			String text = token.getText();
			if (text == null)
				text = "";
			if (token.getCharPositionInLine() + text.length() > 72) {
				if (exceedingTokens.isEmpty()) {
					tokenStartCharIndex = this.state.tokenStartCharIndex;
				}
				exceedingTokens.add(token);
			} else {
				if (!exceedingTokens.isEmpty()) {
					exceedingTokens.remove(exceedingTokens.size() - 1);
					if (!exceedingTokens.isEmpty()) {
						Token firstToken = exceedingTokens.get(0);
						int startIndex = firstToken.getCharPositionInLine();
						int endIndex = startIndex;
						for (Token t : exceedingTokens) {
							String ttext = t.getText();
							if (ttext != null) {
								endIndex += ttext.length();
							}
						}
						Token t = new CommonToken(input,
								Token.INVALID_TOKEN_TYPE, Token.HIDDEN_CHANNEL,
								tokenStartCharIndex, tokenStartCharIndex
										+ endIndex - startIndex - 1);
						t.setLine(firstToken.getLine());
						t.setCharPositionInLine(startIndex);
						exceedingTokens = Lists.newArrayList();
						return t;
					}
				}
			}
		}
		return token;
	}

	@Override
	public String getErrorMessage(Token token) {
		String text = token.getText();
		if (text == null)
			text = "";
		if (token.getCharPositionInLine() + text.length() >= 72) {
			return "Line too long: tokens '" + text
					+ "' exceed line-length limit of 72 characters";
		}
		return "NO ERROR MESSAGE";
	}

}
