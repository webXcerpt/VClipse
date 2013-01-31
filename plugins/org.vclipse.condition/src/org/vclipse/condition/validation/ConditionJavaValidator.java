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
package org.vclipse.condition.validation;

import org.eclipse.xtext.validation.Check;
import org.vclipse.condition.validation.AbstractConditionJavaValidator;
import org.vclipse.vcml.vcml.ConditionSource;
 
public class ConditionJavaValidator extends AbstractConditionJavaValidator {

	@Check
	public void checkConditionSource(ConditionSource source) {
		checkSource(source);
	}

}
