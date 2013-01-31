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

import org.eclipse.xtext.formatting.impl.AbstractDeclarativeFormatter;
import org.eclipse.xtext.formatting.impl.FormattingConfig;
import org.vclipse.dependency.services.DependencyGrammarAccess;
import org.vclipse.dependency.services.DependencyGrammarAccess.ConditionElements;
import org.vclipse.dependency.services.DependencyGrammarAccess.ExpressionElements;
import org.vclipse.dependency.services.DependencyGrammarAccess.FunctionCallElements;
import org.vclipse.dependency.services.DependencyGrammarAccess.FunctionElements;
import org.vclipse.dependency.services.DependencyGrammarAccess.MultiplicativeExpressionElements;
import org.vclipse.dependency.services.DependencyGrammarAccess.PFunctionElements;
import org.vclipse.dependency.services.DependencyGrammarAccess.TableElements;
import org.vclipse.dependency.services.DependencyGrammarAccess.UnaryPrimaryExpressionElements;

public class DependencyFormatter extends AbstractDeclarativeFormatter {
	
	@Override
	protected void configureFormatting(FormattingConfig c) {
		DependencyGrammarAccess dga = (DependencyGrammarAccess) getGrammarAccess();
		
		ExpressionElements expressions = dga.getExpressionAccess();
		MultiplicativeExpressionElements multis = dga.getMultiplicativeExpressionAccess();
		UnaryPrimaryExpressionElements unarys = dga.getUnaryPrimaryExpressionAccess();
		ConditionElements condition = dga.getConditionAccess();
		FunctionElements functions = dga.getFunctionAccess();
		FunctionCallElements callelements = dga.getFunctionCallAccess();
		PFunctionElements pfunction = dga.getPFunctionAccess();
		TableElements tables = dga.getTableAccess();
		
		{	// FunctionCallElements
//			c.setLinewrap().after(callelements.getFunctionAssignment_0());
//			c.setIndentationIncrement().after(callelements.getLeftParenthesisKeyword_1());
//			c.setIndentationDecrement().after(callelements.getRightParenthesisKeyword_3());
		}
		
		{	// PFunctionElements
//			c.setLinewrap().after(pfunction.getFunctionAssignment_1());
//			c.setIndentationIncrement().after(pfunction.getLeftParenthesisKeyword_2());
//			c.setIndentationDecrement().after(pfunction.getRightParenthesisKeyword_7());
		}
		
		c.setAutoLinewrap(72);
	}
}
