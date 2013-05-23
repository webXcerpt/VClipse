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
package org.vclipse.vcml.utils;

import static org.vclipse.vcml.utils.VcmlUtils.mkBinaryCondition;
import static org.vclipse.vcml.utils.VcmlUtils.mkComparison;
import static org.vclipse.vcml.utils.VcmlUtils.mkCompoundStatement;
import static org.vclipse.vcml.utils.VcmlUtils.mkConditionalConstraintRestriction;
import static org.vclipse.vcml.utils.VcmlUtils.mkConditionalStatement;
import static org.vclipse.vcml.utils.VcmlUtils.mkNumericLiteral;
import static org.vclipse.vcml.utils.VcmlUtils.mkSymbolicLiteral;
import static org.vclipse.vcml.utils.VcmlUtils.mkUnaryExpression;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.vclipse.vcml.vcml.ComparisonOperator;
import org.vclipse.vcml.vcml.CompoundStatement;
import org.vclipse.vcml.vcml.Condition;
import org.vclipse.vcml.vcml.ConstraintRestriction;
import org.vclipse.vcml.vcml.Expression;
import org.vclipse.vcml.vcml.NumericLiteral;
import org.vclipse.vcml.vcml.SimpleStatement;
import org.vclipse.vcml.vcml.Statement;
import org.vclipse.vcml.vcml.SymbolicLiteral;
import org.vclipse.vcml.vcml.UnaryExpressionOperator;

import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;


/**
 *
 * This class provides convenience methods for constructing and simplifying VCML code objects.
 * It should not contain any NSN-specific assumptions.
 * TODO This code could be moved to org.vclipse.vcml plugin.
 *
 * @author Tim Geisler
 *
 */
public class SimplifyingVcmlUtils {

	public class ConstantCondition extends EObjectImpl implements Condition {
		private final boolean value;
		private ConstantCondition(final boolean value) {
			this.value = value;
		}
		public boolean getValue() {
			return value;
		}
	}

	public Condition mkConstantCondition(final boolean value) {
		return new ConstantCondition(value);
	}

	public Condition mkSimplifiedBinaryCondition(final String operator, final Condition left,	final Condition right) {
		if ("or".equalsIgnoreCase(operator)) {
			if (left instanceof ConstantCondition) {
				return (((ConstantCondition)left).getValue()) ? left : right;
			}
			if (right instanceof ConstantCondition) {
				return (((ConstantCondition)right).getValue()) ? right : left;
			}
		} else if ("and".equalsIgnoreCase(operator)) {
			if (left instanceof ConstantCondition) {
				return (((ConstantCondition)left).getValue()) ? right : left;
			}
			if (right instanceof ConstantCondition) {
				return (((ConstantCondition)right).getValue()) ? left : right;
			}
		}
		return mkBinaryCondition(operator, left, right);
	}

	public Condition mkSimplifiedComparison(final ComparisonOperator operator, final Expression left, final Expression right) {
		if (left instanceof NumericLiteral && right instanceof NumericLiteral) {
			// TODO evaluate comparison at compile time
		} else if (left instanceof SymbolicLiteral && right instanceof SymbolicLiteral) {
			// TODO evaluate comparison at compile time
		}
		return mkComparison(operator, left, right);
	}

	/**
	 *
	 * @param statement
	 * @param condition
	 * @return null in case the condition is always false
	 */
	public Statement mkSimplifiedConditionalStatement(final Statement statement, final Condition condition) {
		if (condition instanceof ConstantCondition) {
			return ((ConstantCondition)condition).getValue() ? statement : null;
		}
		return mkConditionalStatement(statement, condition);
	}

	public Statement mkSimplifiedCompoundStatement(final SimpleStatement... statements) {
		Iterable<SimpleStatement> statementsNonNull = Iterables.filter(Lists.newArrayList(statements), Predicates.notNull());
		switch (Iterables.size(statementsNonNull)) {
		case 0: return null;
		case 1: return Iterables.getFirst(statementsNonNull, null);
		default:
			final CompoundStatement compoundStatement = mkCompoundStatement();
			compoundStatement.getStatements().addAll(Lists.newArrayList(statementsNonNull));
			return compoundStatement;
		}
	}

	public Expression mkSimplifiedUnaryExpression(final UnaryExpressionOperator operator, final Expression expression) {
		switch (operator) {
		case PLUS:
			return expression;
		case MINUS:
			if (expression instanceof NumericLiteral) {
				final String value = ((NumericLiteral)expression).getValue();
				try {
					final int i = Integer.parseInt(value);
					return mkNumericLiteral(-i);
				} catch (final NumberFormatException e) {
					throw new IllegalArgumentException("illegal numeric literal " + expression);
				}
			}
			break;
		case LC:
			if (expression instanceof SymbolicLiteral) {
				return mkSymbolicLiteral(((SymbolicLiteral)expression).getValue().toLowerCase());
			}
			break;
		case UC:
			if (expression instanceof SymbolicLiteral) {
				return mkSymbolicLiteral(((SymbolicLiteral)expression).getValue().toUpperCase());
			}
			break;
		}
		return mkUnaryExpression(operator, expression);
	}

	public ConstraintRestriction mkSimplifiedConditionalConstraintRestriction(final Condition condition, final ConstraintRestriction restriction) {
		if (condition instanceof ConstantCondition) {
			return ((ConstantCondition)condition).getValue() ? restriction : null;
		}
		return mkConditionalConstraintRestriction(condition, restriction);
	}

}
