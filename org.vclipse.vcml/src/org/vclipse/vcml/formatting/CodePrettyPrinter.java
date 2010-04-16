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
package org.vclipse.vcml.formatting;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.xtext.linking.ILinkingService;
import org.vclipse.vcml.vcml.Assignment;
import org.vclipse.vcml.vcml.BinaryCondition;
import org.vclipse.vcml.vcml.BinaryExpression;
import org.vclipse.vcml.vcml.Characteristic;
import org.vclipse.vcml.vcml.Comparison;
import org.vclipse.vcml.vcml.CompoundStatement;
import org.vclipse.vcml.vcml.Condition;
import org.vclipse.vcml.vcml.ConditionalStatement;
import org.vclipse.vcml.vcml.Function;
import org.vclipse.vcml.vcml.FunctionCall;
import org.vclipse.vcml.vcml.IsInvisible;
import org.vclipse.vcml.vcml.Literal;
import org.vclipse.vcml.vcml.NumberList;
import org.vclipse.vcml.vcml.NumberListEntry;
import org.vclipse.vcml.vcml.NumericInterval;
import org.vclipse.vcml.vcml.NumericLiteral;
import org.vclipse.vcml.vcml.SimpleStatement;
import org.vclipse.vcml.vcml.SymbolList;
import org.vclipse.vcml.vcml.SymbolicLiteral;
import org.vclipse.vcml.vcml.Table;
import org.vclipse.vcml.vcml.UnaryCondition;
import org.vclipse.vcml.vcml.UnaryExpression;
import org.vclipse.vcml.vcml.VcmlPackage;
import org.vclipse.vcml.vcml.util.VcmlSwitch;

import com.google.inject.Inject;

import de.uka.ilkd.pp.DataLayouter;
import de.uka.ilkd.pp.NoExceptions;
import de.uka.ilkd.pp.StringBackend;

/**
 * 
 */
public abstract class CodePrettyPrinter extends VcmlSwitch<DataLayouter<NoExceptions>> {
	
	@Inject
	protected ILinkingService linkingService;
	
	// idea of precedence levels is from Haskell's showsPrec
	protected static int PREC_OR = 7;
	protected static int PREC_AND = 6;
	protected static int PREC_COMPARISON = 4;
	protected static int PREC_ADD = 3;
	protected static int PREC_CONCAT = 3; // absolute value not really relevant
	protected static int PREC_MUL = 2;
	protected static int PREC_UNARY = 1; // - NOT

	protected static int PREC_MAX = 10;
	
	protected int precedenceLevel;
	
	protected static final int LINE_WIDTH = 71; // 72 is allowed in SAP, we reduce by 1 to be able to append punctuation
	protected static final int INDENTATION = 2;
	
	protected DataLayouter<NoExceptions> layouter;
	
	protected static VcmlPackage VCMLPACKAGE = VcmlPackage.eINSTANCE;
	
	/**
	 * @param o
	 * @return
	 */
	public String prettyPrint(EObject o) {
		StringBuilder sb = new StringBuilder();
		layouter = new DataLayouter<NoExceptions>(new StringBackend(sb, LINE_WIDTH), INDENTATION);
		doSwitch(o);
		layouter.close();
		return sb.toString();
	}
	
	/**
	 * @see org.vclipse.vcml.vcml.util.VcmlSwitch#caseCompoundStatement(org.vclipse.vcml.vcml.CompoundStatement)
	 */
	@Override
	public DataLayouter<NoExceptions> caseCompoundStatement(CompoundStatement object) {
		EList<SimpleStatement> statements = object.getStatements();
		// TODO improve wrapping
		layouter.print("(");
		for(int i=0, size=statements.size()-1; i<=size; i++) {
			doSwitch(statements.get(i));
			if(i<size) {
				layouter.print(", ").brk();
			}
		}
		layouter.print(")");
		return layouter;
	}

	/**
	 * @see org.vclipse.vcml.vcml.util.VcmlSwitch#caseConditionalStatement(org.vclipse.vcml.vcml.ConditionalStatement)
	 */
	@Override
	public DataLayouter<NoExceptions> caseConditionalStatement(ConditionalStatement object) {
		layouter.beginI();
		doSwitch(object.getStatement());
		layouter.brk().print("if").brk();
		doSwitch(object.getCondition());
		return layouter.end();
	}
	
	/**
	 * @see org.vclipse.vcml.vcml.util.VcmlSwitch#caseAssignment(org.vclipse.vcml.vcml.Assignment)
	 */
	@Override
	public DataLayouter<NoExceptions> caseAssignment(Assignment object) {
		layouter.beginI(0);
		layouter.print("$self.");
		printCrossReference(object, VCMLPACKAGE.getAssignment_Characteristic(), VCMLPACKAGE.getVCObject_Name());
		layouter.brk().print("=").brk();
		if(object.getExpression() != null) {
			precedenceLevel = PREC_MAX;
			doSwitch(object.getExpression());
		}
		layouter.end();
		return layouter;
	}

	/**
	 * @see org.vclipse.vcml.vcml.util.VcmlSwitch#caseFunction(org.vclipse.vcml.vcml.Function)
	 */
	@Override
	public DataLayouter<NoExceptions> caseFunction(Function object) {
		layouter.print("function ");
		printCrossReference(object, VCMLPACKAGE.getFunction_Function(), VCMLPACKAGE.getVCObject_Name());
		layouter.print("(");
		EList<Characteristic> cstics = object.getCharacteristics();
		EList<Literal> literals = object.getValues();
		for(int i=0, size=cstics.size()-1; i<=size; i++) {
			printCrossReference(object, cstics.get(i), VCMLPACKAGE.getFunction_Characteristics(), VCMLPACKAGE.getVCObject_Name());
			layouter.print(" = ");
			doSwitch(literals.get(i));
			if(i<size) {
				layouter.print(",").brk();
			}
		}
		return layouter.print(")");
	}

	/**
	 * @see org.vclipse.vcml.vcml.util.VcmlSwitch#caseIsInvisible(org.vclipse.vcml.vcml.IsInvisible)
	 */
	@Override
	public DataLayouter<NoExceptions> caseIsInvisible(IsInvisible object) {
		layouter.print("$self.");
		printCrossReference(object, VCMLPACKAGE.getIsInvisible_Characteristic(), VCMLPACKAGE.getVCObject_Name());
		return layouter.print(" is").print(" invisible");
	}

	/**
	 * @see org.vclipse.vcml.vcml.util.VcmlSwitch#caseTable(org.vclipse.vcml.vcml.Table)
	 */
	@Override
	public DataLayouter<NoExceptions> caseTable(Table object) {
		layouter.brk().beginC();
		layouter.print("table ");
		printNullsafe(object.getTable().getName());
		layouter.print(" (").brk();
		EList<Characteristic> cstics = object.getCharacteristics();
		EList<Literal> values = object.getValues();
		for(int i=0, size=cstics.size()-1; i<=size; i++) {
			Characteristic cstic = cstics.get(i);
			printNullsafe(cstic.getName());
			layouter.print(" = ");
			doSwitch(values.get(i));
			if(i<size) {
				layouter.print(", ");
			}
		}
		return layouter.brk(1,-INDENTATION).print(")").end();
	}

	/*****************************************************************************
	 **	Expressions
	 *****************************************************************************/
	
	/**
	 * @see org.vclipse.vcml.vcml.util.VcmlSwitch#caseBinaryExpression(org.vclipse.vcml.vcml.BinaryExpression)
	 */
	@Override
	public DataLayouter<NoExceptions> caseBinaryExpression(BinaryExpression object) {
		int parentPrec = precedenceLevel, leftPrec = 0, rightPrec = 0;
		String operator = object.getOperator().toLowerCase();
		if("+".equals(operator)) {
			leftPrec = PREC_ADD;
			rightPrec = PREC_ADD;
		} else if("-".equals(operator)) {
			leftPrec = PREC_ADD;
			rightPrec = PREC_ADD - 1 ;// SAP is left associative
		} else if("*".equals(operator)) {
			leftPrec = PREC_MUL;
			rightPrec = PREC_MUL;
		} else if("/".equals(operator)) {
			leftPrec = PREC_MUL;
			rightPrec = PREC_MUL - 1;// SAP is left associative
		} else if("||".equals(operator)) {
			leftPrec = PREC_CONCAT;
			rightPrec = PREC_CONCAT - 1;// SAP is left associative
		};
		// TODO missing CONCAT
		boolean parentheses = haveParentheses(parentPrec, leftPrec, rightPrec);
		if(parentheses) {
			layouter.print("(");
		}
		layouter.beginI(0);
		precedenceLevel = leftPrec;
		doSwitch(object.getLeft());
		layouter.brk().print(operator).brk();
		precedenceLevel = rightPrec;
		doSwitch(object.getRight());
		layouter.end();
		if(parentheses) {
			layouter.print(")");
		}
		return layouter;
	}

	/**
	 * @see org.vclipse.vcml.vcml.util.VcmlSwitch#caseFunctionCall(org.vclipse.vcml.vcml.FunctionCall)
	 */
	@Override
	public DataLayouter<NoExceptions> caseFunctionCall(FunctionCall object) {
		printNullsafe(object.getFunction().getName());
		layouter.print("(");
		precedenceLevel = PREC_MAX;
		doSwitch(object.getArgument());
		return layouter.print(")");
	}

	/**
	 * @see org.vclipse.vcml.vcml.util.VcmlSwitch#caseUnaryExpression(org.vclipse.vcml.vcml.UnaryExpression)
	 */
	@Override
	public DataLayouter<NoExceptions> caseUnaryExpression(UnaryExpression object) {
		int parentPrec = precedenceLevel;
		String operator = object.getOperator().getLiteral();
		layouter.print(operator);
		boolean parentheses = haveParentheses(parentPrec, PREC_UNARY);
		if(parentheses) {
			layouter.print("(");
		}
		precedenceLevel = PREC_UNARY;
		doSwitch(object.getExpression());
		if(parentheses) {
			layouter.print(")");
		}
		precedenceLevel = parentPrec;
		return layouter;
	}
	
	
	/*********************************************************************************
	 ** Cases for Condition-class
	 *********************************************************************************/
	
	/**
	 * @see org.vclipse.vcml.vcml.util.VcmlSwitch#caseBinaryCondition(org.vclipse.vcml.vcml.BinaryCondition)
	 */
	@Override
	public DataLayouter<NoExceptions> caseBinaryCondition(BinaryCondition object) {
		int parentPrec = precedenceLevel, leftPrec = 0, rightPrec = 0;
		String operator = object.getOperator().toLowerCase();
		if("and".equals(operator)) {
			leftPrec = PREC_AND;
			rightPrec = PREC_AND;
		} else if("or".equals(operator)) {
			leftPrec = PREC_OR;
			rightPrec = PREC_OR;
		}
		boolean parentheses = haveParentheses(parentPrec, leftPrec, rightPrec);
		if(parentheses) {
			layouter.print("(");
		}
		layouter.beginI(0);
		precedenceLevel = leftPrec;
		doSwitch(object.getLeft());
		layouter.brk().print(operator).brk();
		precedenceLevel = rightPrec;
		doSwitch(object.getRight());
		layouter.end();
		if(parentheses) {
			layouter.print(")");
		}
		precedenceLevel = parentPrec;
		return layouter;
	}

	/**
	 * @param parentPrec
	 * @param leftPrec
	 * @param rightPrec
	 * @return
	 */
	protected boolean haveParentheses(int parentPrec, int leftPrec, int rightPrec) {
		return parentPrec < leftPrec || parentPrec < rightPrec;
	}
	
	protected boolean haveParentheses(int parentPrec, int argPrec) {
		return parentPrec < argPrec;
	}
	
	/**
	 * @see org.vclipse.vcml.vcml.util.VcmlSwitch#caseComparison(org.vclipse.vcml.vcml.Comparison)
	 */
	@Override
	public DataLayouter<NoExceptions> caseComparison(Comparison object) {
		int parentPrec = precedenceLevel;
		precedenceLevel = PREC_COMPARISON;
		doSwitch(object.getLeft());
		layouter.print(" ").print(object.getOperator()).print(" ");
		doSwitch(object.getRight());
		precedenceLevel = parentPrec;
		return layouter;
	}
	
	/**
	 * @see org.vclipse.vcml.vcml.util.VcmlSwitch#caseNumberList(org.vclipse.vcml.vcml.NumberList)
	 */
	@Override
	public DataLayouter<NoExceptions> caseNumberList(NumberList object) {
		layouter.print("(");
		boolean start = true;
		for(NumberListEntry entry : object.getEntries()) {
			if(start) {
				start = false;
			} else {
				layouter.print(", ");
			}
			doSwitch(entry);
		}
		return layouter.print(")");
	}
	
	/**
	 * @see org.vclipse.vcml.vcml.util.VcmlSwitch#caseNumericInterval(org.vclipse.vcml.vcml.NumericInterval)
	 */
	@Override
	public DataLayouter<NoExceptions> caseNumericInterval(NumericInterval object) {
		printNullsafe(object.getLowerBound());
		layouter.print(" - ");
		printNullsafe(object.getUpperBound());
		return layouter;
	}

	/**
	 * @see org.vclipse.vcml.vcml.util.VcmlSwitch#caseNumericLiteral(org.vclipse.vcml.vcml.NumericLiteral)
	 */
	@Override
	public DataLayouter<NoExceptions> caseNumericLiteral(NumericLiteral object) {
		printNullsafe(object.getValue());
		return layouter;
	}

	/**
	 * @see org.vclipse.vcml.vcml.util.VcmlSwitch#caseSymbolList(org.vclipse.vcml.vcml.SymbolList)
	 */
	@Override
	public DataLayouter<NoExceptions> caseSymbolList(SymbolList object) {
		layouter.print("(").beginI(0);
		boolean start = true;
		for(SymbolicLiteral entry : object.getEntries()) {
			if(start) {
				start = false;
			} else {
				layouter.print(",").brk();
			}
			doSwitch(entry);
		}
		return layouter.end().print(")");
	}
	
	/**
	 * @see org.vclipse.vcml.vcml.util.VcmlSwitch#caseSymbolicLiteral(org.vclipse.vcml.vcml.SymbolicLiteral)
	 */
	@Override
	public DataLayouter<NoExceptions> caseSymbolicLiteral(SymbolicLiteral object) {
		layouter.print("'");
		printNullsafe(object.getValue());
		return layouter.print("'");
	}

	/**
	 * @see org.vclipse.vcml.vcml.util.VcmlSwitch#caseUnaryCondition(org.vclipse.vcml.vcml.UnaryCondition)
	 */
	@Override
	public DataLayouter<NoExceptions> caseUnaryCondition(UnaryCondition object) {
		int parentPrec = precedenceLevel;
		layouter.print("not ");
		Condition condition = object.getCondition();
		boolean parentheses = haveParentheses(parentPrec, PREC_UNARY) || condition instanceof UnaryCondition;
		if(parentheses) {
			layouter.print("(");
		}
		precedenceLevel = PREC_UNARY;
		doSwitch(condition);
		if(parentheses) {
			layouter.print(")");
		}
		precedenceLevel = parentPrec;
		return layouter;
	}

	/****************************************************************************************
	 ** Cases for Condition class end.
	 ****************************************************************************************

	/**
	 * @see org.vclipse.vcml.vcml.util.VcmlSwitch#doSwitch(org.eclipse.emf.ecore.EObject)
	 * allow calls with null argument (should not do anything)
	 */
	@Override
	public DataLayouter<NoExceptions> doSwitch(EObject theEObject) {
		if (theEObject==null) {
			return layouter;
		}
		return super.doSwitch(theEObject);
	}

	/**
	 * @param theEObject
	 * @return
	 */
	public DataLayouter<NoExceptions> brk_doSwitch(EObject theEObject) {
		if (theEObject==null) {
			return layouter;
		}
		layouter.brk();
		return super.doSwitch(theEObject);
	}
	
	protected void printNullsafe(Object object) {
		layouter.print(object==null ? "null" : object);
	}

	protected void printCrossReference(EObject context, EReference ref, EAttribute att) {
		printCrossReference(context, (EObject)context.eGet(ref), ref, att);
	}

	protected void printCrossReference(EObject context, EObject object, EReference ref, EAttribute att) {
		String linkText;
		Object o = object.eGet(att);
		if (o!=null) {
			linkText = o.toString();
		} else {
			try {
				linkText = linkingService.getLinkText(object, ref, context);
			} catch (Exception ex) {
				linkText = "###EXCEPTION###";
				ex.printStackTrace();
			}
		}
		printNullsafe(linkText);
	}
	
}
