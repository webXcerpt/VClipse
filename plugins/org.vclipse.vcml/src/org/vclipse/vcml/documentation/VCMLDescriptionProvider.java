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

import org.eclipse.emf.common.util.EList;
import org.vclipse.base.DeclarativeEObjectDocumentationProvider;
import org.vclipse.vcml.utils.DescriptionHandler;
import org.vclipse.vcml.utils.VcmlUtils;
import org.vclipse.vcml.vcml.Characteristic;
import org.vclipse.vcml.vcml.CharacteristicValue;
import org.vclipse.vcml.vcml.Constraint;
import org.vclipse.vcml.vcml.ConstraintClass;
import org.vclipse.vcml.vcml.DependencyNet;
import org.vclipse.vcml.vcml.Description;
import org.vclipse.vcml.vcml.Language;
import org.vclipse.vcml.vcml.Material;
import org.vclipse.vcml.vcml.MultiLanguageDescription;
import org.vclipse.vcml.vcml.MultiLanguageDescriptions;
import org.vclipse.vcml.vcml.Precondition;
import org.vclipse.vcml.vcml.Procedure;
import org.vclipse.vcml.vcml.SelectionCondition;
import org.vclipse.vcml.vcml.ShortVarDefinition;
import org.vclipse.vcml.vcml.SimpleDescription;
import org.vclipse.vcml.vcml.VariantFunction;
import org.vclipse.vcml.vcml.VariantTable;

public class VCMLDescriptionProvider extends
		DeclarativeEObjectDocumentationProvider {

	String documentation(Characteristic object) {
		return toString(object.getDescription());
	}

	String documentation(CharacteristicValue object) {
		return toString(object.getDescription());
	}

	String documentation(Constraint object) {
		return toString(object.getDescription());
	}

	String documentation(ConstraintClass object) {
		return getDocumentation(object.getClass_());
	}

	String documentation(DependencyNet object) {
		return toString(object.getDescription());
	}

	String documentation(Material object) {
		return toString(object.getDescription());
	}

	String documentation(org.vclipse.vcml.vcml.Class object) {
		return toString(object.getDescription());
	}

	String documentation(Precondition object) {
		return toString(object.getDescription());
	}

	String documentation(Procedure object) {
		return toString(object.getDescription());
	}

	String documentation(SelectionCondition object) {
		return toString(object.getDescription());
	}

	String documentation(ShortVarDefinition object) {
		return getDocumentation(object.getCharacteristic());
	}

	String documentation(VariantFunction object) {
		return toString(object.getDescription());
	}

	String documentation(VariantTable object) {
		return toString(object.getDescription());
	}

	protected String toString(Description description) {
		if (description==null) {
			return null;
		}
		final StringBuffer result = new StringBuffer();
		if(description instanceof SimpleDescription) {
			new DescriptionHandler() {
				private Language defaultLanguage = VcmlUtils.getDefaultLanguage();

				@Override
				public void handleSingleDescription(Language language, String value) {
					if (defaultLanguage.equals(language)) {
						result.append(value);
					}
				}
			}.handleDescription(description);
		} else {
			EList<MultiLanguageDescription> descriptions = ((MultiLanguageDescriptions)description).getDescriptions();
			for(MultiLanguageDescription mlDescription : descriptions) {
				result.append(mlDescription.getLanguage().getLiteral()).append(" ");
				result.append("\"").append(mlDescription.getValue()).append("\"");
				if(descriptions.indexOf(mlDescription) != descriptions.size() - 1) {
					result.append(" ");
				}
			}
			
		}
		return result.toString();
	}
}
