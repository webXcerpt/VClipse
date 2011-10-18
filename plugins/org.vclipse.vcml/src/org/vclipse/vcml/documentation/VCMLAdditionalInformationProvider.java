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
package org.vclipse.vcml.documentation;

import org.vclipse.base.DeclarativeEObjectDocumentationProvider;
import org.vclipse.vcml.vcml.Characteristic;
import org.vclipse.vcml.vcml.CharacteristicType;
import org.vclipse.vcml.vcml.ConstraintClass;
import org.vclipse.vcml.vcml.NumericType;
import org.vclipse.vcml.vcml.ShortVarDefinition;
import org.vclipse.vcml.vcml.SymbolicType;

public class VCMLAdditionalInformationProvider extends
		DeclarativeEObjectDocumentationProvider {

	String documentation(Characteristic object) {
		CharacteristicType type = object.getType();
		if (type==null) {
			return null; // there is no body for the cstic
		}
		return getDocumentation(type)
				+ (object.isMultiValue() ? " multi-value " : "")
				+ (object.isRestrictable() ? " restrictable " : "")
				+ (object.isRequired() ? " required " : "")
				+ (object.isNotReadyForInput() ? " no-input " : "")
				+ (object.isAdditionalValues() ? " additional-values " : "")
				+ (object.isDisplayAllowedValues() ? " disp-allowed-values "
						: "");
	}

	String documentation(ConstraintClass object) {
		return getDocumentation(object.getClass_());
	}

	String documentation(NumericType object) {
		return "NUM " + object.getNumberOfChars() + "."
				+ object.getDecimalPlaces()
				+ (object.isIntervalValuesAllowed() ? " intervals " : "")
				+ (object.isNegativeValuesAllowed() ? " negative " : "");
		// TODO print values
	}

	String documentation(ShortVarDefinition object) {
		return getDocumentation(object.getCharacteristic());
	}

	String documentation(SymbolicType object) {
		return "CHAR " + object.getNumberOfChars()
				+ (object.isCaseSensitive() ? " case-sensitive " : "");
	}

}
