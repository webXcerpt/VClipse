/**
 * Copyright (c) 2010 - 2013 webXcerpt Software GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     	webXcerpt Software GmbH - initial creator
 * 		www.webxcerpt.com
 */
package org.vclipse.vcml;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.vclipse.vcml.vcml.BinaryExpression;
import org.vclipse.vcml.vcml.Characteristic;
import org.vclipse.vcml.vcml.CharacteristicReference_C;
import org.vclipse.vcml.vcml.Comparison;
import org.vclipse.vcml.vcml.Condition;
import org.vclipse.vcml.vcml.ConditionalConstraintRestriction;
import org.vclipse.vcml.vcml.ConstraintRestriction;
import org.vclipse.vcml.vcml.ConstraintRestrictionFalse;
import org.vclipse.vcml.vcml.ConstraintSource;
import org.vclipse.vcml.vcml.Expression;
import org.vclipse.vcml.vcml.Function;
import org.vclipse.vcml.vcml.FunctionCall;
import org.vclipse.vcml.vcml.InCondition_C;
import org.vclipse.vcml.vcml.IsSpecified_C;
import org.vclipse.vcml.vcml.Literal;
import org.vclipse.vcml.vcml.NegatedConstraintRestrictionLHS;
import org.vclipse.vcml.vcml.NumericLiteral;
import org.vclipse.vcml.vcml.ObjectCharacteristicReference;
import org.vclipse.vcml.vcml.ShortVarDefinition;
import org.vclipse.vcml.vcml.ShortVarReference;
import org.vclipse.vcml.vcml.SymbolicLiteral;
import org.vclipse.vcml.vcml.Table;

@SuppressWarnings("all")
public class ConstraintRestrictionsUtilities {
  public ConditionalConstraintRestriction canExtractCommonConditions(final ConstraintSource source) {
    ConditionalConstraintRestriction forReturn = ((ConditionalConstraintRestriction) null);
    EList<ConstraintRestriction> _restrictions = source.getRestrictions();
    for (final ConstraintRestriction restriction : _restrictions) {
      {
        boolean _equals = Objects.equal(forReturn, null);
        if (_equals) {
          if ((restriction instanceof ConditionalConstraintRestriction)) {
            forReturn = ((ConditionalConstraintRestriction) restriction);
          } else {
            return forReturn;
          }
        } else {
          if ((restriction instanceof ConditionalConstraintRestriction)) {
            final ConditionalConstraintRestriction current = ((ConditionalConstraintRestriction) restriction);
            final Condition condition = forReturn.getCondition();
            final Condition condition2 = current.getCondition();
            boolean _equals_1 = EcoreUtil.equals(condition, condition2);
            if (_equals_1) {
              forReturn = current;
            } else {
              return null;
            }
          }
        }
        return forReturn;
      }
    }
    return null;
  }
  
  protected List<Characteristic> _usedCstis(final BinaryExpression exp) {
    List<Characteristic> _xblockexpression = null;
    {
      Expression _left = exp.getLeft();
      final List<Characteristic> cstics = this.usedCstis(_left);
      Expression _right = exp.getRight();
      List<Characteristic> _usedCstis = this.usedCstis(_right);
      cstics.addAll(_usedCstis);
      _xblockexpression = (cstics);
    }
    return _xblockexpression;
  }
  
  protected List<Characteristic> _usedCstis(final Comparison comparison) {
    List<Characteristic> _xblockexpression = null;
    {
      Expression _left = comparison.getLeft();
      final List<Characteristic> cstics = this.usedCstis(_left);
      Expression _right = comparison.getRight();
      List<Characteristic> _usedCstis = this.usedCstis(_right);
      cstics.addAll(_usedCstis);
      _xblockexpression = (cstics);
    }
    return _xblockexpression;
  }
  
  protected List<Characteristic> _usedCstis(final ConditionalConstraintRestriction restriction) {
    ConstraintRestriction _restriction = restriction.getRestriction();
    List<Characteristic> _usedCstis = this.usedCstis(_restriction);
    return _usedCstis;
  }
  
  protected List<Characteristic> _usedCstis(final ObjectCharacteristicReference reference) {
    Characteristic _characteristic = reference.getCharacteristic();
    return Lists.<Characteristic>newArrayList(_characteristic);
  }
  
  protected List<Characteristic> _usedCstis(final ShortVarReference reference) {
    ShortVarDefinition _ref = reference.getRef();
    Characteristic _characteristic = _ref.getCharacteristic();
    return Lists.<Characteristic>newArrayList(_characteristic);
  }
  
  protected List<Characteristic> _usedCstis(final InCondition_C inCondition) {
    CharacteristicReference_C _characteristic = inCondition.getCharacteristic();
    List<Characteristic> _usedCstis = this.usedCstis(_characteristic);
    return _usedCstis;
  }
  
  protected List<Characteristic> _usedCstis(final IsSpecified_C isSpecified) {
    CharacteristicReference_C _characteristic = isSpecified.getCharacteristic();
    List<Characteristic> _usedCstis = this.usedCstis(_characteristic);
    return _usedCstis;
  }
  
  protected List<Characteristic> _usedCstis(final NegatedConstraintRestrictionLHS restriction) {
    EObject _restriction = restriction.getRestriction();
    List<Characteristic> _usedCstis = this.usedCstis(_restriction);
    return _usedCstis;
  }
  
  protected List<Characteristic> _usedCstis(final Table table) {
    ArrayList<Characteristic> _xblockexpression = null;
    {
      final ArrayList<Characteristic> cstics = Lists.<Characteristic>newArrayList();
      EList<Literal> _values = table.getValues();
      for (final Literal literal : _values) {
        {
          final List<Characteristic> usedCstics = this.usedCstis(literal);
          cstics.addAll(usedCstics);
        }
      }
      _xblockexpression = (cstics);
    }
    return _xblockexpression;
  }
  
  protected List<Characteristic> _usedCstis(final Function function) {
    ArrayList<Characteristic> _xblockexpression = null;
    {
      final ArrayList<Characteristic> cstics = Lists.<Characteristic>newArrayList();
      EList<Literal> _values = function.getValues();
      for (final Literal literal : _values) {
        {
          final List<Characteristic> usedCstics = this.usedCstis(literal);
          cstics.addAll(usedCstics);
        }
      }
      _xblockexpression = (cstics);
    }
    return _xblockexpression;
  }
  
  protected List<Characteristic> _usedCstis(final SymbolicLiteral obj) {
    ArrayList<Characteristic> _newArrayList = Lists.<Characteristic>newArrayList();
    return _newArrayList;
  }
  
  protected List<Characteristic> _usedCstis(final NumericLiteral obj) {
    ArrayList<Characteristic> _newArrayList = Lists.<Characteristic>newArrayList();
    return _newArrayList;
  }
  
  protected List<Characteristic> _usedCstis(final ConstraintRestrictionFalse obj) {
    ArrayList<Characteristic> _newArrayList = Lists.<Characteristic>newArrayList();
    return _newArrayList;
  }
  
  protected List<Characteristic> _usedCstis(final FunctionCall obj) {
    ArrayList<Characteristic> _newArrayList = Lists.<Characteristic>newArrayList();
    return _newArrayList;
  }
  
  public List<Characteristic> usedCstis(final EObject reference) {
    if (reference instanceof ObjectCharacteristicReference) {
      return _usedCstis((ObjectCharacteristicReference)reference);
    } else if (reference instanceof ShortVarReference) {
      return _usedCstis((ShortVarReference)reference);
    } else if (reference instanceof Function) {
      return _usedCstis((Function)reference);
    } else if (reference instanceof NumericLiteral) {
      return _usedCstis((NumericLiteral)reference);
    } else if (reference instanceof SymbolicLiteral) {
      return _usedCstis((SymbolicLiteral)reference);
    } else if (reference instanceof Table) {
      return _usedCstis((Table)reference);
    } else if (reference instanceof BinaryExpression) {
      return _usedCstis((BinaryExpression)reference);
    } else if (reference instanceof Comparison) {
      return _usedCstis((Comparison)reference);
    } else if (reference instanceof ConditionalConstraintRestriction) {
      return _usedCstis((ConditionalConstraintRestriction)reference);
    } else if (reference instanceof ConstraintRestrictionFalse) {
      return _usedCstis((ConstraintRestrictionFalse)reference);
    } else if (reference instanceof FunctionCall) {
      return _usedCstis((FunctionCall)reference);
    } else if (reference instanceof InCondition_C) {
      return _usedCstis((InCondition_C)reference);
    } else if (reference instanceof IsSpecified_C) {
      return _usedCstis((IsSpecified_C)reference);
    } else if (reference instanceof NegatedConstraintRestrictionLHS) {
      return _usedCstis((NegatedConstraintRestrictionLHS)reference);
    } else {
      throw new IllegalArgumentException("Unhandled parameter types: " +
        Arrays.<Object>asList(reference).toString());
    }
  }
}
