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
package org.vclipse.vcml.conversion;

import org.eclipse.xtext.common.services.DefaultTerminalConverters;
import org.eclipse.xtext.conversion.IValueConverter;
import org.eclipse.xtext.conversion.ValueConverter;
import org.eclipse.xtext.conversion.impl.AbstractNullSafeConverter;
import org.eclipse.xtext.conversion.impl.AbstractToStringConverter;
import org.eclipse.xtext.parsetree.AbstractNode;

public class VCMLValueConverter extends DefaultTerminalConverters {

	private static IValueConverter<String> TOUPPER_VALUECONVERTER = new AbstractToStringConverter<String>() {
		@Override
		protected String internalToValue(String string, AbstractNode node) {
			return string.toUpperCase();
		}
	};
	
	private static IValueConverter<String> NOWHITESPACE_TOUPPER_VALUECONVERTER = new AbstractToStringConverter<String>() {
		@Override
		protected String internalToValue(String string, AbstractNode node) {
			String result = string.replaceAll("\\s", "").toUpperCase();
			return result;
		}
	};
	
	// SAP IDs, EXTENDED_IDs and SHORTVARs are interpreted as uppercase
	@Override
	@ValueConverter(rule = "ID")
	public IValueConverter<String> ID() {
		return TOUPPER_VALUECONVERTER;
	}

	@ValueConverter(rule = "EXTENDED_ID")
	public IValueConverter<String> EXTENDED_ID() {
		return TOUPPER_VALUECONVERTER;
	}

	@ValueConverter(rule = "SHORTVAR")
	public IValueConverter<String> SHORTVAR() {
		return TOUPPER_VALUECONVERTER;
	}

	@ValueConverter(rule = "CLASSNAME")
	public IValueConverter<String> CLASSNAME() {
		return NOWHITESPACE_TOUPPER_VALUECONVERTER;
	}

	// uses own Strings implementation to avoid \\u conversion of unicode characters
	// see https://bugs.eclipse.org/bugs/show_bug.cgi?id=289613 for this issue
	@Override
	@ValueConverter(rule = "STRING")
	public IValueConverter<String> STRING() {
		return new AbstractNullSafeConverter<String>() {
			@Override
			protected String internalToValue(String string, AbstractNode node) {
				return Strings.convertFromJavaString(string.substring(1, string.length() - 1));
			}

			@Override
			protected String internalToString(String value) {
				return '"' + Strings.convertToJavaString(value) + '"';
			}
		};
	}

	@ValueConverter(rule = "SYMBOL")
	public IValueConverter<String> SYMBOL() {
		return new AbstractNullSafeConverter<String>() {
			@Override
			protected String internalToValue(String string, AbstractNode node) {
				return string.substring(1, string.length() - 1);
			}

			@Override
			protected String internalToString(String value) {
				return "'" + value + "'";
			}
		};
	}


}
