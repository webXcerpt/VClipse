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
package org.vclipse.procedure.formatting;

import org.eclipse.xtext.GrammarUtil;
import org.eclipse.xtext.Keyword;
import org.eclipse.xtext.formatting.impl.AbstractDeclarativeFormatter;
import org.eclipse.xtext.formatting.impl.FormattingConfig;
import org.vclipse.dependency.services.DependencyGrammarAccess.ConditionElements;
import org.vclipse.dependency.services.DependencyGrammarAccess.ConjunctionElements;
import org.vclipse.dependency.services.DependencyGrammarAccess.FunctionElements;
import org.vclipse.dependency.services.DependencyGrammarAccess.PFunctionElements;
import org.vclipse.dependency.services.DependencyGrammarAccess.TableElements;
import org.vclipse.procedure.services.ProcedureGrammarAccess;
import org.vclipse.procedure.services.ProcedureGrammarAccess.ProcedureSourceElements;
import org.vclipse.procedure.services.ProcedureGrammarAccess.StatementElements;

/**
 * This class contains custom formatting description.
 * 
 * see : http://www.eclipse.org/Xtext/documentation/latest/xtext.html#formatting
 * on how and when to use it 
 * 
 * Also see {@link org.eclipse.xtext.xtext.XtextFormattingTokenSerializer} as an example
 */
public class ProcedureFormatter extends AbstractDeclarativeFormatter {
	
	

	@Override
	protected void configureFormatting(FormattingConfig c) {
// It's usually a good idea to activate the following three statements.
// They will add and preserve newlines around comments
//		c.setLinewrap(0, 1, 2).before(getGrammarAccess().getSL_COMMENTRule());
//		c.setLinewrap(0, 1, 2).before(getGrammarAccess().getML_COMMENTRule());
//		c.setLinewrap(0, 1, 1).after(getGrammarAccess().getML_COMMENTRule());
		
		
		// procedure source (linewrap after ,)
		
		
		ProcedureGrammarAccess pga = (ProcedureGrammarAccess) getGrammarAccess();
				
		ProcedureSourceElements proceduresources = pga.getProcedureSourceAccess();
		StatementElements statements = pga.getStatementAccess();
		PFunctionElements pfunction = pga.getPFunctionAccess();
		TableElements table = pga.getTableAccess();
		FunctionElements function = pga.getFunctionAccess();
		ConjunctionElements conjunctions = pga.getConjunctionAccess();
		ConditionElements conditions = pga.getConditionAccess();
		
		c.setAutoLinewrap(72);

		Iterable<Keyword> keywords = GrammarUtil.containedKeywords(pga.getGrammar());
		
		// dots
	    for (Keyword currentKeyword : keywords) {
	    	if (".".equals(currentKeyword.getValue())) {
	    		c.setNoSpace().around(currentKeyword);
	    	}
	    }
		
	    
//	    c.setNoSpace().before(pga.getSL_COMMENTRule());
	    c.setLinewrap(0, 1, 2).before(pga.getSL_COMMENTRule());
	    c.setLinewrap(0, 1, 1).after(pga.getSL_COMMENTRule());
	    
		c.setIndentationIncrement().after(pfunction.getLeftParenthesisKeyword_2());
		c.setIndentationDecrement().before(pfunction.getRightParenthesisKeyword_4());
		
    	c.setLinewrap().after(proceduresources.getCommaKeyword_1_0());
    	
    	c.setLinewrap(2).before(pfunction.getPfunctionKeyword_0());
    	c.setLinewrap(2).before(function.getFunctionKeyword_0());
    	c.setLinewrap(2).before(table.getTableKeyword_0());
//    	c.setLinewrap(2).before(statements.getIfKeyword_1_1());
    	
    	c.setLinewrap().before(conjunctions.getOperatorAndKeyword_1_1_0());	// 'and' keyword is in dependency
    	c.setLinewrap().before(conditions.getOperatorOrKeyword_1_1_0());	// 'or' keyword is in dependency
    	c.setLinewrap().after(conjunctions.getOperatorAndKeyword_1_1_0());	// 'and' keyword is in dependency
    	c.setLinewrap().after(conditions.getOperatorOrKeyword_1_1_0());	// 'or' keyword is in dependency
    	
//    	c.setLinewrap().after(pfunction.getFunctionAssignment_1());
    	c.setLinewrap().after(pfunction.getLeftParenthesisKeyword_2());
    	c.setLinewrap().after(pfunction.getCommaKeyword_3_3_0());
    	c.setLinewrap().before(pfunction.getRightParenthesisKeyword_4());
    	c.setIndentationIncrement().after(pfunction.getLeftParenthesisKeyword_2());
		c.setIndentationDecrement().before(pfunction.getRightParenthesisKeyword_4());
    	
    	c.setLinewrap().after(function.getLeftParenthesisKeyword_2());
    	c.setLinewrap().after(function.getCommaKeyword_6_0());
    	c.setLinewrap().before(function.getRightParenthesisKeyword_7());
    	c.setIndentationIncrement().after(function.getLeftParenthesisKeyword_2());
		c.setIndentationDecrement().before(function.getRightParenthesisKeyword_7());
    	
    	c.setLinewrap().after(table.getLeftParenthesisKeyword_2());
    	c.setLinewrap().after(table.getCommaKeyword_6_0());
    	c.setLinewrap().before(table.getRightParenthesisKeyword_7());
    	c.setIndentationIncrement().after(table.getLeftParenthesisKeyword_2());
		c.setIndentationDecrement().before(table.getRightParenthesisKeyword_7());
    	
    	
	}
}
