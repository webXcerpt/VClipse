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
package org.vclipse.idoc.conversion;

public class Strings extends org.eclipse.xtext.util.Strings {

	// copied from org.eclipse.xtext.util.Strings, but removed \\u conversion
	
	/**
	 * Mostly copied from {@link java.util.Properties#loadConvert}
	 */
	public static String convertFromJavaString(final String javaString) {
		char[] in = javaString.toCharArray();
		int off = 0;
		int len = javaString.length();
		char[] convtBuf = new char[len];
		char aChar;
		char[] out = convtBuf;
		int outLen = 0;
		int end = off + len;

		while (off < end) {
			aChar = in[off++];
			if (aChar == '\\') {
				aChar = in[off++];
				if (aChar == 't')
					aChar = '\t';
				else if (aChar == 'r')
					aChar = '\r';
				else if (aChar == 'n')
					aChar = '\n';
				else if (aChar == 'f')
					aChar = '\f';
				else if (aChar == 'b')
					aChar = '\b';
				else if (aChar == '"')
					aChar = '\"';
				out[outLen++] = aChar;
			}
			else {
				out[outLen++] = aChar;
			}
		}
		return new String(out, 0, outLen);
	}

	/**
	 * Mostly copied from {@link java.util.Properties#saveConvert}
	 */
	public static String convertToJavaString(final String theString) {
		int len = theString.length();
		int bufLen = len * 2;
		if (bufLen < 0) {
			bufLen = Integer.MAX_VALUE;
		}
		StringBuffer outBuffer = new StringBuffer(bufLen);

		for (int x = 0; x < len; x++) {
			char aChar = theString.charAt(x);
			// Handle common case first, selecting largest block that
			// avoids the specials below
			if ((aChar > 61) && (aChar < 127)) {
				if (aChar == '\\') {
					outBuffer.append('\\');
					outBuffer.append('\\');
					continue;
				}
				outBuffer.append(aChar);
				continue;
			}
			switch (aChar) {
				case ' ':
					outBuffer.append(' ');
					break;
				case '\t':
					outBuffer.append('\\');
					outBuffer.append('t');
					break;
				case '\n':
					outBuffer.append('\\');
					outBuffer.append('n');
					break;
				case '\r':
					outBuffer.append('\\');
					outBuffer.append('r');
					break;
				case '\f':
					outBuffer.append('\\');
					outBuffer.append('f');
					break;
				case '\b':
					outBuffer.append('\\');
					outBuffer.append('b');
					break;
				case '"':
					outBuffer.append('\\');
					outBuffer.append('"');
					break;
				default:
					outBuffer.append(aChar);
			}
		}
		return outBuffer.toString();
	}

	
}
