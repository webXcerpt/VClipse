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
package org.vclipse.constraint.formatting;

import java.util.List;

import org.eclipse.xtext.GrammarUtil;
import org.eclipse.xtext.IGrammarAccess;
import org.eclipse.xtext.Keyword;
import org.eclipse.xtext.formatting.impl.AbstractDeclarativeFormatter;
import org.eclipse.xtext.formatting.impl.FormattingConfig;
import org.vclipse.constraint.services.ConstraintGrammarAccess;
import org.vclipse.constraint.services.ConstraintGrammarAccess.ConstraintSourceElements;

import com.google.common.collect.Lists;

/**
 * This class contains custom formatting description.
 * 
 * see : http://www.eclipse.org/Xtext/documentation/latest/xtext.html#formatting
 * on how and when to use it 
 * 
 * Also see {@link org.eclipse.xtext.xtext.XtextFormattingTokenSerializer} as an example
 */
public class ConstraintFormatter extends AbstractDeclarativeFormatter {
	
//	@Inject
//	DependencyFormatter base;
	
	@Override
	protected void configureFormatting(FormattingConfig c) {
// It's usually a good idea to activate the following three statements.
// They will add and preserve newlines around comments
//		c.setLinewrap(0, 1, 2).before(getGrammarAccess().getSL_COMMENTRule());
//		c.setLinewrap(0, 1, 2).before(getGrammarAccess().getML_COMMENTRule());
//		c.setLinewrap(0, 1, 1).after(getGrammarAccess().getML_COMMENTRule());
		
		// super.configureFormatting(c);
		
		
		ConstraintGrammarAccess cga = (ConstraintGrammarAccess) getGrammarAccess();
		
		c.setAutoLinewrap(72);
		
//		Iterable<Keyword> keywords = GrammarUtil.containedKeywords(cga.getGrammar());
		
		ConstraintSourceElements elements = cga.getConstraintSourceAccess();
		{	// comment
//			c.setNoSpace().around(cga.getSL_COMMENTRule());
			//c.setLinewrap(0, 1, 2).before(cga.getSL_COMMENTRule());
			//c.setLinewrap(0, 1, 1).after(cga.getSL_COMMENTRule());
		}
		
		List<IGrammarAccess> grammarList = Lists.newArrayList();
		
		grammarList.add(cga);
//		grammarList.add((IGrammarAccess)cga.getConstraintClassAccess());
//		grammarList.add((IGrammarAccess)cga.getConstraintMaterialAccess());
//		grammarList.add((IGrammarAccess)cga.getShortVarDefinitionAccess());
//		grammarList.add((IGrammarAccess)cga.getConstraintClassAccess());
//		grammarList.add((IGrammarAccess)cga.getPartOfConditionAccess());
//		grammarList.add((IGrammarAccess)cga.getSubpartOfConditionAccess());
//		grammarList.add((IGrammarAccess)cga.getConditionalConstraintRestrictionAccess());
//		grammarList.add((IGrammarAccess)cga.getConstraintRestrictionFalseAccess());
//		grammarList.add((IGrammarAccess)cga.getConstraintRestrictionLHSAccess());
//		grammarList.add((IGrammarAccess)cga.getNegatedConstraintRestrictionLHSAccess());
//		grammarList.add((IGrammarAccess)cga.getCharacteristicReferenceAccess());
//		grammarList.add((IGrammarAccess)cga.getObjectCharacteristicReferenceAccess());
//		grammarList.add((IGrammarAccess)cga.getShortVarReferenceAccess());
//		grammarList.add((IGrammarAccess)cga.getLiteralAccess());
//		grammarList.add((IGrammarAccess)cga.getMDataCharacteristicAccess());
//		grammarList.add((IGrammarAccess)cga.getUnaryConditionAccess());
//		grammarList.add((IGrammarAccess)cga.getPrimaryConditionAccess());
//		grammarList.add((IGrammarAccess)cga.getIsSpecifiedAccess());
//		grammarList.add((IGrammarAccess)cga.getInConditionAccess());
		
		
//		final List<Keyword> cgaKeywords = ConstraintFormatter.findKeywords(grammarList, ".");
		final List<Keyword> cgaKeywords = ConstraintFormatter.findAllKeywords(grammarList);
		
		// dots
	    for (Keyword currentKeyword : cgaKeywords) {
//	    	if (currentKeyword == elements.getFullStopKeyword_10()) {
				String value = currentKeyword.getValue();
				if (".".equals(value)) {
					c.setNoSpace().around(currentKeyword);
				}
				else if(",".equals(value)) {
					c.setNoSpace().before(currentKeyword);
				}
				else if("(".equals(value)) {
//					c.setNoSpace().after(currentKeyword);
				}
				else if(")".equals(value)) {
//					c.setNoSpace().before(currentKeyword);
				}
//			}
	    }
		
		{	// SAPObject definitions on toplevel
//			c.setNoSpace().before(elements.getObjectsKeyword_0());
//			c.setNoSpace().before(elements.getConditionKeyword_5_0());
//			c.setNoSpace().before(elements.getRestrictionKeyword_6_0());
//			c.setNoSpace().before(elements.getRestrictionsKeyword_6_1());
//			c.setNoSpace().before(elements.getInferencesKeyword_11_0());
			
//			c.setIndentationDecrement().before(elements.getInferencesKeyword_11_0()););
			c.setNoSpace().before(elements.getColonKeyword_1());
//			c.setLinewrap(2).before(elements.getObjectsKeyword_0());
			
			c.setNoSpace().before(elements.getColonKeyword_5_1());
			c.setLinewrap(2).before(elements.getConditionKeyword_5_0());
			
			c.setNoSpace().before(elements.getColonKeyword_7());
			c.setLinewrap(2).before(elements.getRestrictionKeyword_6_0());
			
//			c.setNoSpace().before(elements.getColonKeyword_7());
			c.setLinewrap(2).before(elements.getRestrictionsKeyword_6_1());
			
			c.setNoSpace().before(elements.getColonKeyword_11_1());
			c.setLinewrap(2).before(elements.getInferencesKeyword_11_0());
		}
		
		{	// constraint source (linewrap after . : ,)
	    	c.setLinewrap().after(elements.getColonKeyword_1());
	    	c.setLinewrap().after(elements.getColonKeyword_5_1());
	    	c.setLinewrap().after(elements.getColonKeyword_7());
	    	c.setLinewrap().after(elements.getColonKeyword_11_1());
	    	c.setLinewrap().after(elements.getCommaKeyword_3_0());
	    	c.setLinewrap().after(elements.getCommaKeyword_9_0());
	    	c.setLinewrap().after(elements.getCommaKeyword_11_3_0());
	    	c.setLinewrap().after(elements.getFullStopKeyword_4());
	    	c.setLinewrap().after(elements.getFullStopKeyword_5_3());
	    	c.setLinewrap().after(elements.getFullStopKeyword_10());
	    	c.setLinewrap().after(elements.getFullStopKeyword_11_4());
	    	
		}
		
		{	// indendation after toplevel objects and additional line wraps
			
			
			c.setLinewrap().after(elements.getCommaKeyword_3_0());
			
			c.setLinewrap().after(elements.getFullStopKeyword_5_3());
			c.setLinewrap().after(elements.getFullStopKeyword_4());

//			c.setIndentationDecrement().before(elements.getConditionKeyword_5_0());
//			c.setIndentationDecrement().before(elements.getRestrictionKeyword_6_0());
//			c.setIndentationDecrement().before(elements.getRestrictionsKeyword_6_1());
//			c.setIndentationDecrement().before(elements.getInferencesKeyword_11_0());	// 2x ?
//			c.setIndentationDecrement().before(elements.getInferencesKeyword_11_0());
			
			c.setIndentation(elements.getObjectsKeyword_0(), elements.getConditionKeyword_5_0());
			c.setIndentation(elements.getConditionKeyword_5_0(), elements.getRestrictionsKeyword_6_1());
			c.setIndentation(elements.getRestrictionsKeyword_6_1(), elements.getInferencesKeyword_11_0());
			c.setIndentationIncrement().after(elements.getInferencesKeyword_11_0());
			
//			c.setIndentationIncrement().after(elements.getColonKeyword_1());
//			c.setIndentationIncrement().after(elements.getColonKeyword_5_1());
//			c.setIndentationIncrement().after(elements.getColonKeyword_7());
//			c.setIndentationIncrement().after(elements.getColonKeyword_11_1());
			
			c.setLinewrap().after(cga.getConstraintClassAccess().getWhereKeyword_2_0());
			c.setIndentation(cga.getConstraintClassAccess().getWhereKeyword_2_0(), cga.getConstraintClassAccess().getShortVarsAssignment_2_2_1());
			
			c.setLinewrap().after(cga.getConditionalConstraintRestrictionAccess().getIfKeyword_1_1());
			c.setIndentation(cga.getConditionalConstraintRestrictionAccess().getIfKeyword_1_1(), elements.getRestrictionsConditionalConstraintRestrictionParserRuleCall_9_1_0());
// !!!			c.setIndentation(cga.getConditionalConstraintRestrictionAccess().getIfKeyword_1_1(), cga.getConditionalConstraintRestrictionAccess().getConditionConditionParserRuleCall_1_2_0());
//			c.setIndentationDecrement().after(elements.getRestrictionsConditionalConstraintRestrictionParserRuleCall_9_1_0());

//			c.setIndentationDecrement().after(elements.getRestrictionsAssignment_8());
			// c.setIndentationIncrement().between(cga.getConditionalConstraintRestrictionAccess().getIfKeyword_1_1(), cga.getConditionalConstraintRestrictionAccess().getConditionConditionParserRuleCall_1_2_0());		
//			c.setIndentationIncrement().after(cga.getConditionalConstraintRestrictionAccess().getIfKeyword_1_1());
//			c.setIndentationIncrement().before(cga.getConditionalConstraintRestrictionAccess().getConditionAssignment_1_2());
//			c.setIndentationIncrement().before(cga.getConditionalConstraintRestrictionAccess().getIfKeyword_1_1());
//			c.setIndentation(cga.getConditionalConstraintRestrictionAccess().getConstraintRestrictionLHSParserRuleCall_0(), cga.getConditionalConstraintRestrictionAccess().getConditionAssignment_1_2());
			
// !!			c.setIndentationIncrement().between(cga.getConditionalConstraintRestrictionAccess().getIfKeyword_1_1(), cga.getConditionalConstraintRestrictionAccess().getConditionAssignment_1_2()); // , elements.getCommaKeyword_9_0());
//			c.setIndentationDecrement().before(cga.getConditionalConstraintRestrictionAccess().getConditionConditionParserRuleCall_1_2_0());
			
//			c.setIndentationIncrement().after(cga.getConstraintClassAccess().getWhereKeyword_2_0());				// commented
//			c.setIndentationDecrement().after(elements.getCommaKeyword_9_0());										// commented
			
			//c.setIndentationIncrement().after(cga.getConditionalConstraintRestrictionAccess().getIfKeyword_1_1());	// commented
			//c.setIndentationDecrement().after(cga.getConditionalConstraintRestrictionAccess().getConditionAssignment_1_2());										// commented
			
//			c.setIndentationIncrement().around(cga.getConditionalConstraintRestrictionAccess().getConditionConditionParserRuleCall_1_2_0());	// commented
			
			c.setLinewrap().after(elements.getCommaKeyword_3_0());
			c.setLinewrap().after(elements.getFullStopKeyword_4());
			c.setLinewrap().after(elements.getCommaKeyword_9_0());
			c.setLinewrap().after(elements.getFullStopKeyword_10());
			
			c.setLinewrap().after(elements.getCommaKeyword_11_3_0());
			c.setLinewrap().after(elements.getFullStopKeyword_11_4());
		}
		
		{	// function
			c.setLinewrap(2).before(cga.getFunctionAccess().getFunctionKeyword_0());
			c.setLinewrap().after(cga.getFunctionAccess().getLeftParenthesisKeyword_2());
			c.setIndentationIncrement().after(cga.getFunctionAccess().getLeftParenthesisKeyword_2());
			c.setIndentationDecrement().before(cga.getFunctionAccess().getRightParenthesisKeyword_7());
			c.setLinewrap().before(cga.getFunctionAccess().getRightParenthesisKeyword_7());
			c.setLinewrap().after(cga.getFunctionAccess().getCommaKeyword_6_0());
		}
		{	// pfunction
			c.setLinewrap(2).before(cga.getPFunctionAccess().getPfunctionKeyword_0());
			c.setLinewrap().after(cga.getPFunctionAccess().getLeftParenthesisKeyword_2());
			c.setIndentationIncrement().after(cga.getPFunctionAccess().getLeftParenthesisKeyword_2());
			c.setIndentationDecrement().before(cga.getPFunctionAccess().getRightParenthesisKeyword_4());
			c.setLinewrap().before(cga.getPFunctionAccess().getRightParenthesisKeyword_4());
			c.setLinewrap().after(cga.getPFunctionAccess().getCommaKeyword_3_3_0());
		}
		{	// table
			c.setLinewrap(2).before(cga.getTableAccess().getTableKeyword_0());
			c.setLinewrap().after(cga.getTableAccess().getLeftParenthesisKeyword_2());
//			c.setIndentation(cga.getTableAccess().getLeftParenthesisKeyword_2(), cga.getTableAccess().getRightParenthesisKeyword_7());
			c.setIndentationIncrement().after(cga.getTableAccess().getLeftParenthesisKeyword_2());
			c.setIndentationDecrement().before(cga.getTableAccess().getRightParenthesisKeyword_7());
			c.setLinewrap().before(cga.getTableAccess().getRightParenthesisKeyword_7());
			c.setLinewrap().after(cga.getTableAccess().getCommaKeyword_6_0());
		}
	}
	
	public static List<Keyword> findKeywords(List<IGrammarAccess> gas, String... keywords) {
		List<Keyword> results = Lists.newArrayList();
		for (IGrammarAccess ga : gas) {
			results.addAll(ga.findKeywords(keywords));
		}
		return results;
	}
	
	public static List<Keyword> findAllKeywords(List<IGrammarAccess> gas) {
		List<String> results = Lists.newArrayList();
		for (IGrammarAccess ga : gas) {
			results.addAll(Lists.newArrayList(GrammarUtil.getAllKeywords(ga.getGrammar())));		// returns a String
		}
		
		return findKeywords(gas, results.toArray(new String[0]));									// convert String to Keyword and return
	}
}
