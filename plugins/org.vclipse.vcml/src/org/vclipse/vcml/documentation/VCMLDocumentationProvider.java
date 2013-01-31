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
package org.vclipse.vcml.documentation;

import org.vclipse.base.DeclarativeEObjectDocumentationProvider;
import org.vclipse.vcml.utils.DocumentationHandler;
import org.vclipse.vcml.utils.VcmlUtils;
import org.vclipse.vcml.vcml.Characteristic;
import org.vclipse.vcml.vcml.CharacteristicValue;
import org.vclipse.vcml.vcml.Constraint;
import org.vclipse.vcml.vcml.DependencyNet;
import org.vclipse.vcml.vcml.Documentation;
import org.vclipse.vcml.vcml.Language;
import org.vclipse.vcml.vcml.Precondition;
import org.vclipse.vcml.vcml.Procedure;
import org.vclipse.vcml.vcml.SelectionCondition;
import org.vclipse.vcml.vcml.ShortVarDefinition;

public class VCMLDocumentationProvider extends
		DeclarativeEObjectDocumentationProvider {

	String documentation(Characteristic object) {
		return toString(object.getDocumentation());
	}

	String documentation(CharacteristicValue object) {
		return toString(object.getDocumentation());
	}

	String documentation(Constraint object) {
		return toString(object.getDocumentation());
	}

	String documentation(DependencyNet object) {
		return toString(object.getDocumentation());
	}

	String documentation(Precondition object) {
		return toString(object.getDocumentation());
	}

	String documentation(Procedure object) {
		return toString(object.getDocumentation());
	}

	String documentation(SelectionCondition object) {
		return toString(object.getDocumentation());
	}

	String documentation(ShortVarDefinition object) {
		return getDocumentation(object.getCharacteristic());
	}

	protected String toString(Documentation documentation) {
		if (documentation==null) {
			return null;
		}
		final StringBuffer result = new StringBuffer();
		new DocumentationHandler() {
			private Language defaultLanguage = VcmlUtils.getDefaultLanguage(); 
			@Override
			public void addDocumentationEntry(final Language language, final String text, final String format) {
				if (defaultLanguage.equals(language)) {
					result.append(text);
				}
			}
		}.handleDocumentation(documentation);
		return result.toString();
	}

}
