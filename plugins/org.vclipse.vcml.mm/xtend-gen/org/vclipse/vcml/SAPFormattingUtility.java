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
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.xbase.lib.Exceptions;
import org.eclipse.xtext.xbase.lib.Functions.Function0;
import org.vclipse.vcml.vcml.CharacteristicType;
import org.vclipse.vcml.vcml.DateCharacteristicValue;
import org.vclipse.vcml.vcml.NumberListEntry;
import org.vclipse.vcml.vcml.NumericCharacteristicValue;
import org.vclipse.vcml.vcml.NumericInterval;
import org.vclipse.vcml.vcml.NumericLiteral;
import org.vclipse.vcml.vcml.NumericType;

/**
 * Utility providing formatted string representation for some values.
 * 
 * The format is equal to the one in sap.
 */
@SuppressWarnings("all")
public class SAPFormattingUtility {
  public static String EMPTY = "";
  
  public static String WHITESPACE = " ";
  
  public static String INTERVAL_BINDER = " - ";
  
  public static String DOT = ".";
  
  public static String COMMA = ",";
  
  public static String ZERO = "0";
  
  public static String NUMBER_SIGN = "#";
  
  public static SimpleDateFormat DATEFORMAT_SAP = new Function0<SimpleDateFormat>() {
    public SimpleDateFormat apply() {
      SimpleDateFormat _simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
      return _simpleDateFormat;
    }
  }.apply();
  
  public static SimpleDateFormat DATEFORMAT_VCML = new Function0<SimpleDateFormat>() {
    public SimpleDateFormat apply() {
      SimpleDateFormat _simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");
      return _simpleDateFormat;
    }
  }.apply();
  
  public static String DEFAULT_DATE = "00.00.0000";
  
  /**
   * Retruns formatted string representation of the value.
   */
  protected String _toString(final NumericCharacteristicValue value) {
    CharacteristicType _containerOfType = EcoreUtil2.<CharacteristicType>getContainerOfType(value, CharacteristicType.class);
    final NumericType csticType = ((NumericType) _containerOfType);
    NumberListEntry _entry = value.getEntry();
    NumberFormat _formatter = this.getFormatter(csticType);
    return this.format(_entry, _formatter, csticType);
  }
  
  /**
   * Returns formatted string representation of the value.
   */
  protected String _toString(final DateCharacteristicValue value) {
    try {
      StringBuffer _stringBuffer = new StringBuffer();
      final StringBuffer resultBuffer = _stringBuffer;
      String _from = value.getFrom();
      final Date fromDate = SAPFormattingUtility.DATEFORMAT_VCML.parse(_from);
      String _format = SAPFormattingUtility.DATEFORMAT_VCML.format(fromDate);
      resultBuffer.append(_format);
      String _to = value.getTo();
      boolean _notEquals = (!Objects.equal(_to, null));
      if (_notEquals) {
        String _to_1 = value.getTo();
        final Date toDate = SAPFormattingUtility.DATEFORMAT_VCML.parse(_to_1);
        resultBuffer.append(SAPFormattingUtility.INTERVAL_BINDER);
        String _format_1 = SAPFormattingUtility.DATEFORMAT_VCML.format(toDate);
        resultBuffer.append(_format_1);
      }
      return resultBuffer.toString();
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  /**
   * Formats an interval with a given formatter.
   */
  protected String _format(final NumericInterval interval, final NumberFormat formatter, final NumericType type) {
    StringBuffer _stringBuffer = new StringBuffer();
    final StringBuffer resultBuffer = _stringBuffer;
    String _lowerBound = interval.getLowerBound();
    Double _double = new Double(_lowerBound);
    Double doubleValue = _double;
    String formatted = formatter.format(doubleValue);
    resultBuffer.append(formatted);
    resultBuffer.append(SAPFormattingUtility.INTERVAL_BINDER);
    String _upperBound = interval.getUpperBound();
    Double _double_1 = new Double(_upperBound);
    doubleValue = _double_1;
    String _format = formatter.format(doubleValue);
    formatted = _format;
    resultBuffer.append(formatted);
    resultBuffer.append(SAPFormattingUtility.WHITESPACE);
    String _unit = type.getUnit();
    boolean _notEquals = (!Objects.equal(_unit, null));
    if (_notEquals) {
      String _unit_1 = type.getUnit();
      String _lowerCase = _unit_1.toLowerCase();
      resultBuffer.append(_lowerCase);
    }
    this.replace_COMMA_DOT(resultBuffer);
    return resultBuffer.toString();
  }
  
  /**
   * Formats bumeric literal with a given formatter.
   */
  protected String _format(final NumericLiteral literal, final NumberFormat formatter, final NumericType type) {
    StringBuffer _stringBuffer = new StringBuffer();
    final StringBuffer resultBuffer = _stringBuffer;
    String _value = literal.getValue();
    Double _double = new Double(_value);
    final Double doubleValue = _double;
    final String formatted = formatter.format(doubleValue);
    resultBuffer.append(formatted);
    this.replace_COMMA_DOT(resultBuffer);
    return resultBuffer.toString();
  }
  
  /**
   * Creates a number or decimal formatter for the type.
   */
  public NumberFormat getFormatter(final NumericType type) {
    NumberFormat _xblockexpression = null;
    {
      StringBuffer _stringBuffer = new StringBuffer();
      final StringBuffer formatBuffer = _stringBuffer;
      NumberFormat format = NumberFormat.getNumberInstance();
      int decimal = type.getDecimalPlaces();
      int numOfChars = type.getNumberOfChars();
      boolean _greaterThan = (numOfChars > 0);
      boolean _while = _greaterThan;
      while (_while) {
        {
          formatBuffer.append(SAPFormattingUtility.NUMBER_SIGN);
          int _minus = (numOfChars - 1);
          numOfChars = _minus;
          boolean _and = false;
          int _modulo = (numOfChars % 3);
          boolean _equals = (_modulo == 0);
          if (!_equals) {
            _and = false;
          } else {
            boolean _greaterThan_1 = (numOfChars > 1);
            _and = (_equals && _greaterThan_1);
          }
          if (_and) {
            formatBuffer.append(SAPFormattingUtility.COMMA);
          }
        }
        boolean _greaterThan_1 = (numOfChars > 0);
        _while = _greaterThan_1;
      }
      boolean _equals = (decimal == 0);
      if (_equals) {
        formatBuffer.append(SAPFormattingUtility.DOT);
        formatBuffer.append(SAPFormattingUtility.NUMBER_SIGN);
      }
      boolean _greaterThan_1 = (decimal > 0);
      if (_greaterThan_1) {
        formatBuffer.append(SAPFormattingUtility.DOT);
        boolean _greaterThan_2 = (decimal > 0);
        boolean _while_1 = _greaterThan_2;
        while (_while_1) {
          {
            formatBuffer.append(SAPFormattingUtility.ZERO);
            int _minus = (decimal - 1);
            decimal = _minus;
          }
          boolean _greaterThan_3 = (decimal > 0);
          _while_1 = _greaterThan_3;
        }
        String _string = formatBuffer.toString();
        DecimalFormat _decimalFormat = new DecimalFormat(_string);
        format = _decimalFormat;
      }
      _xblockexpression = (format);
    }
    return _xblockexpression;
  }
  
  /**
   * Returns date format specified for vcml.
   */
  public SimpleDateFormat getVcmlDateFormat() {
    return SAPFormattingUtility.DATEFORMAT_VCML;
  }
  
  /**
   * Replaces all commas with dots and all dots with commas in the buffer.
   */
  public void replace_COMMA_DOT(final StringBuffer buffer) {
    int start = 0;
    int _length = buffer.length();
    boolean _lessThan = (start < _length);
    boolean _while = _lessThan;
    while (_while) {
      {
        char _charAt = buffer.charAt(start);
        final String _char = (SAPFormattingUtility.EMPTY + Character.valueOf(_charAt));
        boolean _equals = SAPFormattingUtility.COMMA.equals(_char);
        if (_equals) {
          int _plus = (start + 1);
          buffer.replace(start, _plus, SAPFormattingUtility.DOT);
        }
        boolean _equals_1 = SAPFormattingUtility.DOT.equals(_char);
        if (_equals_1) {
          int _plus_1 = (start + 1);
          buffer.replace(start, _plus_1, SAPFormattingUtility.COMMA);
        }
        int _plus_2 = (start + 1);
        start = _plus_2;
      }
      int _length_1 = buffer.length();
      boolean _lessThan_1 = (start < _length_1);
      _while = _lessThan_1;
    }
  }
  
  public String toString(final EObject value) {
    if (value instanceof DateCharacteristicValue) {
      return _toString((DateCharacteristicValue)value);
    } else if (value instanceof NumericCharacteristicValue) {
      return _toString((NumericCharacteristicValue)value);
    } else {
      throw new IllegalArgumentException("Unhandled parameter types: " +
        Arrays.<Object>asList(value).toString());
    }
  }
  
  public String format(final NumberListEntry literal, final NumberFormat formatter, final NumericType type) {
    if (literal instanceof NumericLiteral) {
      return _format((NumericLiteral)literal, formatter, type);
    } else if (literal instanceof NumericInterval) {
      return _format((NumericInterval)literal, formatter, type);
    } else {
      throw new IllegalArgumentException("Unhandled parameter types: " +
        Arrays.<Object>asList(literal, formatter, type).toString());
    }
  }
}
