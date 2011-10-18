/*******************************************************************************
 * Copyright (c) 2010 webXcerpt Software GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    webXcerpt Software GmbH - initial creator
 ******************************************************************************/
package org.vclipse.vcml.conversion;

import java.util.regex.Pattern;

import org.eclipse.xtext.common.services.DefaultTerminalConverters;
import org.eclipse.xtext.conversion.IValueConverter;
import org.eclipse.xtext.conversion.ValueConverter;
import org.eclipse.xtext.conversion.ValueConverterException;
import org.eclipse.xtext.conversion.impl.AbstractNullSafeConverter;
import org.eclipse.xtext.conversion.impl.AbstractToStringConverter;
import org.eclipse.xtext.nodemodel.INode;
import org.eclipse.xtext.util.Strings;

public class VCMLValueConverter extends DefaultTerminalConverters {

	private static IValueConverter<String> TOUPPER_VALUECONVERTER = new AbstractToStringConverter<String>() {
		@Override
		protected String internalToValue(String string, INode node) throws ValueConverterException {
			return string.toUpperCase();
		}
	};
	
	private static IValueConverter<String> NOWHITESPACE_TOUPPER_VALUECONVERTER = new AbstractToStringConverter<String>() {
		@Override
		protected String internalToValue(String string, INode node) throws ValueConverterException {
			return string.replaceAll("\\s", "").toUpperCase();
		}
	};
	
	// ('a'..'z'|'A'..'Z'|'_') ('a'..'z'|'A'..'Z'|'_'|'0'..'9')*
	protected static final Pattern ID_PATTERN = Pattern.compile("[a-zA-Z_][a-zA-Z_\\d]*");

	protected static IValueConverter<String> symbolOrIdValueConverter = new SymbolOrIdValueConverter();

	public static class SymbolOrIdValueConverter extends AbstractNullSafeConverter<String> {
		@Override
		protected String internalToValue(String string, INode node) throws ValueConverterException {
			final int lastCharIndex = string.length() - 1;
			if (lastCharIndex >= 0 && string.charAt(0) == '\'' && string.charAt(lastCharIndex) == '\'') {
				return string.substring(1, lastCharIndex); // no conversion to upper case
			} else {
				return string.toUpperCase();
			}
		}

		@Override
		protected String internalToString(final String value) {
			if (ID_PATTERN.matcher(value).matches()) {
				return value;
			} else {
				return "'" + value + "'";
			}
		}
	}

	// SAP IDs, EXTENDED_IDs and SHORTVARs are interpreted as uppercase
	@Override
	@ValueConverter(rule = "ID")
	public IValueConverter<String> ID() {
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
			protected String internalToValue(String string, INode node) throws ValueConverterException {
				return Strings.convertFromJavaString(string.substring(1, string.length() - 1), false);
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
			protected String internalToValue(String string, INode node) throws ValueConverterException {
				return string.substring(1, string.length() - 1);
			}

			@Override
			protected String internalToString(String value) {
				return "'" + value + "'";
			}
		};
	}

	@ValueConverter(rule = "EXTENDED_ID")
	public IValueConverter<String> EXTENDED_ID() {
		return symbolOrIdValueConverter;
	}


}
