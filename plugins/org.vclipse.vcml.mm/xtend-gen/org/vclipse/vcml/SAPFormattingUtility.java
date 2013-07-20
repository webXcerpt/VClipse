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

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import org.eclipse.xtext.xbase.lib.Functions.Function0;

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
  protected String _toString(final /* NumericCharacteristicValue */Object value) {
    throw new Error("Unresolved compilation problems:"
      + "\nCharacteristicType cannot be resolved to a type."
      + "\nNumericType cannot be resolved to a type."
      + "\nBounds mismatch: The type argument <Void> is not a valid substitute for the bounded type parameter <T extends EObject> of the method getContainerOfType(EObject, Class<T>)"
      + "\nentry cannot be resolved");
  }
  
  /**
   * Returns formatted string representation of the value.
   */
  protected String _toString(final /* DateCharacteristicValue */Object value) {
    throw new Error("Unresolved compilation problems:"
      + "\nfrom cannot be resolved"
      + "\nto cannot be resolved"
      + "\n!= cannot be resolved"
      + "\nto cannot be resolved");
  }
  
  /**
   * Formats an interval with a given formatter.
   */
  protected String _format(final /* NumericInterval */Object interval, final NumberFormat formatter, final /* NumericType */Object type) {
    throw new Error("Unresolved compilation problems:"
      + "\nlowerBound cannot be resolved"
      + "\nupperBound cannot be resolved"
      + "\nunit cannot be resolved"
      + "\n!= cannot be resolved"
      + "\nunit cannot be resolved"
      + "\ntoLowerCase cannot be resolved");
  }
  
  /**
   * Formats bumeric literal with a given formatter.
   */
  protected String _format(final /* NumericLiteral */Object literal, final NumberFormat formatter, final /* NumericType */Object type) {
    throw new Error("Unresolved compilation problems:"
      + "\nvalue cannot be resolved");
  }
  
  /**
   * Creates a number or decimal formatter for the type.
   */
  public NumberFormat getFormatter(final /* NumericType */Object type) {
    throw new Error("Unresolved compilation problems:"
      + "\ndecimalPlaces cannot be resolved"
      + "\nnumberOfChars cannot be resolved"
      + "\n> cannot be resolved"
      + "\n- cannot be resolved"
      + "\n% cannot be resolved"
      + "\n== cannot be resolved"
      + "\n&& cannot be resolved"
      + "\n> cannot be resolved"
      + "\n== cannot be resolved"
      + "\n> cannot be resolved"
      + "\n> cannot be resolved"
      + "\n- cannot be resolved");
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
  
  public String toString(final NumericCharacteristicValue value) {
    if (value != null) {
      return _toString(value);
    } else {
      throw new IllegalArgumentException("Unhandled parameter types: " +
        Arrays.<Object>asList(value).toString());
    }
  }
  
  public String format(final NumericInterval interval, final NumberFormat formatter, final NumericType type) {
    if (interval != null
         && type != null) {
      return _format(interval, formatter, type);
    } else {
      throw new IllegalArgumentException("Unhandled parameter types: " +
        Arrays.<Object>asList(interval, formatter, type).toString());
    }
  }
}
