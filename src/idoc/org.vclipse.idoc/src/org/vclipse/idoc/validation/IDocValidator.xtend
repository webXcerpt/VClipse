/*******************************************************************************
 * Copyright (c) 2010 - 2015 webXcerpt Software GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *  
 * Contributors:
 *     	webXcerpt Software GmbH - initial creator
 * 		www.webxcerpt.com
 ******************************************************************************/
package org.vclipse.idoc.validation

import org.eclipse.xtext.validation.Check
import org.vclipse.idoc.iDoc.IDocPackage
import org.vclipse.idoc.iDoc.Segment
import org.vclipse.idoc.iDoc.StringField
import org.eclipse.xtext.validation.ValidationMessageAcceptor

class IDocValidator extends AbstractIDocValidator {

	private static val LENGTH_LIMIT = 72;

	@Check
	def checkSegment(Segment segment) {
		if ("E1CUKNM".equals(segment.type)) {
			for (field : segment.fields) {
				if ("LINE".equals(field.name) && field instanceof StringField) {
					val stringfield = field as StringField;
					val length = stringfield.value.length;
					if (length > LENGTH_LIMIT) {
						error('''Length of E1CUKNM LINE should be «LENGTH_LIMIT» characters maximum, but is «length» characters''',
							stringfield, IDocPackage$Literals::STRING_FIELD__VALUE,
							ValidationMessageAcceptor::INSIGNIFICANT_INDEX);
						}
					}
				}
			}
		}
	}
