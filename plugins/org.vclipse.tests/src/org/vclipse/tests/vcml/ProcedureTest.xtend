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
package org.vclipse.tests.vcml

import org.eclipse.xtext.junit4.InjectWith
import org.eclipse.xtext.junit4.XtextRunner
import org.junit.Test
import org.junit.runner.RunWith
import org.vclipse.tests.procedure.ProcedureInjectorProvider

@InjectWith(typeof(ProcedureInjectorProvider))
@RunWith(typeof(XtextRunner))

class ProcedureTest extends DependencyTest {
	
@Test
def void sumPartsTest1() {
	'''
		$sum_parts ($self, characteristicId)
	'''.testParserRule("SumParts")
}

@Test
def void countPartsTest1() {
	'''
		$count_parts ($parent)
	'''.testParserRule("CountParts")
}

@Test
def void typeOfTest1() {
	'''
		type_of ($root, (material) (300) (nr='123', nr='124'))
	'''.testParserRule("TypeOf")
}
	
}