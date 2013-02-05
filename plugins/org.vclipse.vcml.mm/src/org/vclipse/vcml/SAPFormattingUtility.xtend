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
package org.vclipse.vcml

import java.text.DecimalFormat
import java.text.NumberFormat
import org.eclipse.xtext.EcoreUtil2
import org.vclipse.vcml.vcml.CharacteristicType
import org.vclipse.vcml.vcml.NumericCharacteristicValue
import org.vclipse.vcml.vcml.NumericInterval
import org.vclipse.vcml.vcml.NumericLiteral
import org.vclipse.vcml.vcml.NumericType
import org.vclipse.vcml.vcml.DateType
import org.vclipse.vcml.vcml.DateCharacteristicValue
import java.text.SimpleDateFormat

/**
 * 
 */
class SAPFormattingUtility {

	public static String EMPTY = ""
	public static String WHITESPACE = " "
	public static String FROM_TO_BINDER = " - "
	public static String DOT = "."
	public static String COMMA = ","
	public static String ZERO = "0"
	public static String NUMBER_SIGN = "#"
	
	public static SimpleDateFormat DATEFORMAT_SAP = new SimpleDateFormat("yyyyMMdd");
	public static SimpleDateFormat DATEFORMAT_VCML = new SimpleDateFormat("dd.MM.yyyy");
	public static String DEFAULT_DATE = "00.00.0000"

	/**
	 * Returns string representation.
	 */
	def dispatch toString(NumericCharacteristicValue value) {
		val csticType = EcoreUtil2::getContainerOfType(value, typeof(CharacteristicType))
		return format(value.entry, getFormatter(csticType), csticType as NumericType)
	}
	
	/**
	 * 
	 */
	def dispatch toString(DateCharacteristicValue value) {
		
	}
	
	/**
	 * 
	 */
	def dispatch format(NumericInterval interval, NumberFormat formatter, NumericType type) {
		val resultBuffer = new StringBuffer
		var doubleValue = new Double(interval.lowerBound)
		var formatted = formatter.format(doubleValue)
		resultBuffer.append(formatted)
		resultBuffer.append(FROM_TO_BINDER)
		doubleValue = new Double(interval.upperBound)
		formatted = formatter.format(doubleValue)
		resultBuffer.append(formatted)
		resultBuffer.append(WHITESPACE).append(type.unit.toLowerCase)
			
		var start = 0
		while(start < resultBuffer.length) {
			val _char = EMPTY + resultBuffer.charAt(start)
			if(COMMA.equals(_char)) {
				resultBuffer.replace(start, start + 1, DOT)
			} 
			if(DOT.equals(_char)) {
				resultBuffer.replace(start, start + 1, COMMA)
			}
			start = start + 1
		}
		return resultBuffer.toString
	}
	
	/**
	 * 
	 */
	def dispatch format(NumericLiteral literal, NumberFormat formatter, NumericType type) {
		val resultBuffer = new StringBuffer
		val doubleValue = new Double(literal.value)
		val formatted = formatter.format(doubleValue)
		resultBuffer.append(formatted)
		return resultBuffer.toString
	}
	
	/**
	 * 
	 */
	def dispatch getFormatter(NumericType type) {
		val formatBuffer = new StringBuffer
		var format = NumberFormat::getNumberInstance
		var decimal = type.decimalPlaces
		var numOfChars = type.numberOfChars
		while(numOfChars > 0) {
			formatBuffer.append(NUMBER_SIGN)
			numOfChars = numOfChars - 1
			if(
				// do not group numbers if decimal == 0 => should be tested
				(decimal != 0) && (numOfChars % decimal == 0) && (numOfChars > 1)
			) {
				formatBuffer.append(COMMA)
			}
		}
		if(decimal == 0) {
			formatBuffer.append(DOT)
			formatBuffer.append(NUMBER_SIGN)
		}
		if(decimal > 0) {
			formatBuffer.append(DOT)
			while(decimal > 0) {
				formatBuffer.append(ZERO)
				decimal = decimal - 1
			}
			format = new DecimalFormat(formatBuffer.toString)
		}
		format
	}
	
	/**
	 * 
	 */
	def dispatch getFormatter(DateType type) {
		
	}
}