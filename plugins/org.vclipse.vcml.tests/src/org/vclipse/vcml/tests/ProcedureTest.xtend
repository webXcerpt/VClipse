package org.vclipse.vcml.tests

import org.eclipse.xtext.junit4.InjectWith
import org.eclipselabs.xtext.utils.unittesting.XtextRunner2
import org.junit.Test
import org.junit.runner.RunWith
import org.vclipse.procedure.ProcedureInjectorProvider

@InjectWith(typeof(ProcedureInjectorProvider))
@RunWith(typeof(XtextRunner2))

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