package org.vclipse.base;

import org.eclipse.xtext.naming.IQualifiedNameConverter;

public class NullQualifiedNameConverter extends IQualifiedNameConverter.DefaultImpl {

	public String getDelimiter() {
		return null; // no delimiter - no qualified names
	}
}
