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

/**
 * 
 */
class SAPFormattingUtility {

	/**
	 * Returns string representation.
	 */
	def String toString(NumericCharacteristicValue value) {
		// values have to be formatted in the following way(the same format as in sap)
		// one can not extract dependencies otherwise
		var formatBuffer = new StringBuffer
		val csticType = EcoreUtil2::getContainerOfType(value, typeof(CharacteristicType))
		if(csticType instanceof NumericType) {
			var format = NumberFormat::getNumberInstance()
			val numericType = csticType as NumericType
			var decimal = numericType.decimalPlaces
			var numOfChars = numericType.numberOfChars
			while(numOfChars > 0) {
				formatBuffer.append("#")
				numOfChars = numOfChars - 1
				if(
					// do not group numbers if decimal == 0 => should be tested
					(decimal != 0) && (numOfChars % decimal == 0) && (numOfChars > 1)
				) {
					formatBuffer.append(",")
				}
			}
			if(decimal == 0) {
				formatBuffer.append(".")
				formatBuffer.append("#")
			}
			if(decimal > 0) {
				formatBuffer.append(".")
				while(decimal > 0) {
					formatBuffer.append("0")
					decimal = decimal - 1
				}
				format = new DecimalFormat(formatBuffer.toString)
			}
			
			val resultBuffer = new StringBuffer()
			val entry = value.entry
			if(entry instanceof NumericLiteral) {
				val numericLiteral = (entry as NumericLiteral).value
				resultBuffer.append(format.format(new Double(numericLiteral)))
				return resultBuffer.toString
			}
			
			if(entry instanceof NumericInterval) {
				val interval = entry as NumericInterval
				resultBuffer.append(format.format(new Double(interval.lowerBound)))
				resultBuffer.append(" - ")
				resultBuffer.append(format.format(new Double(interval.upperBound)))
				resultBuffer.append(" ").append(numericType.unit.toLowerCase)
			
				var start = 0
				while(start < resultBuffer.length) {
					val _char = "" + resultBuffer.charAt(start)
					if(",".equals(_char)) {
						resultBuffer.replace(start, start + 1, ".")
					} 
					if(".".equals(_char)) {
						resultBuffer.replace(start, start + 1, ",")
					}
					start = start + 1
				}
				return resultBuffer.toString
			}
		}
		return null
	}
}