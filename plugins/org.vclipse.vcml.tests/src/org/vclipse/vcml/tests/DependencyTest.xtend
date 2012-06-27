package org.vclipse.vcml.tests

import org.eclipselabs.xtext.utils.unittesting.XtextTest
import org.junit.Test

class DependencyTest extends XtextTest {

def testParserRule(CharSequence textToParse, String ruleName) {	//TODO: move to new class which extends XtextTest
        testParserRule(textToParse.toString, ruleName)
}

@Test
def void fileTest() {
	testFile("VcmlTest/dependencytest.vcml");
}

@Test
def void expressionTest1() {
	'''
		+ (7 * 3) + (2 * 3) 
	'''.testParserRule("Expression")
}

@Test
def void expressionTest2() {
	'''
		++- (7 * 3) + (2 * 3) 
	'''.testParserRule("Expression")
}

@Test
def void expressionTest3() {
	'''
		++- (7 * 3) * (2 * 3) 
	'''.testParserRule("Expression")
}

@Test
def void expressionTest4() {
	'''
		++- (7 + 3) * (2 * 3) 
	'''.testParserRule("Expression")
}

@Test
def void expressionTest5() {
	'''
		('a' * 'b') * ('b' * 7) 
	'''.testParserRule("Expression")
}

@Test
def void expressionTest6() {
	'''
		(sin (7) * 'b') * (cos('b') * 7) 
	'''.testParserRule("Expression")
}

@Test
def void conditionTest1() {
	'''
		not 'a' eq 'b' and not 'b' eq 'a'
	'''.testParserRule("Condition")
}

@Test
def void conditionTest2() {
	'''
		(not 'a' eq 'b') and (not ('c' eq 'a' or 'c' eq 'a'))
	'''.testParserRule("Condition")
}

@Test
def void conditionTest3() {
	'''
		(not 'a' eq 2) and (not ('c' eq 2 or 'c' eq (2)))
	'''.testParserRule("Condition")
}

@Test
def void listTest1() {
	'''
		(1, 2, 3, 4, 5 - 6)
	'''.testParserRule("NumberList")
}

@Test
def void listTest2() {
	'''
		('a', 'b', 'c', 'd')
	'''.testParserRule("List")
}

@Test
def void functionTest1() {
	'''
		function testfunction (
			characteristic1 = 'characteristic1'
		)
	'''.testParserRule("Function")
}

@Test
def void tableTest1() {
	'''
		table testtable (
			characteristic1 = 'characteristic1',
			characteristic2 = 7
		)
	'''.testParserRule("Table")
}

@Test
def void idTest() {
	testTerminal("abc", "ID")
	testTerminal("abc4", "ID")
	testTerminal("_abc", "ID")
	
	testNotTerminal("1abc", "ID")
	testNotTerminal("#abc", "ID")
}
	
@Test
def void dependencyCommentTest() {
	testTerminal("\n*asdf", "SL_COMMENT")
	
	testNotTerminal('''foo'''.toString, "SL_COMMENT")
	
	testNotTerminal('''\\tbar'''.toString, "SL_COMMENT")
}

@Test
def void symbolTest() {
	testTerminal("'a'", "SYMBOL")
	
	testTerminal("'\b'", "SYMBOL")
	
	testNotTerminal("'\t'", "SYMBOL")
}

@Test
def void intTest() {
	testTerminal("0123456789", "INT")
	
	testNotTerminal("0.123456789", "INT")
}

@Test
def void stringTest1() {
	testTerminal('''" \\\" \\b \\t \\n \\\\ \\' "'''.toString, "STRING")
}

@Test
def void stringTest2() {
	testTerminal("'\\\" \\b \\t \\n \\\\ \\''", "STRING")
}

@Test
def void stringTest3() {				// not tested: single quotes in rule STRING are intersecting with single quotes in rule SYMBOL if its only one word   
	testTerminal("\"foobar\"", "STRING")
}


@Test
def void stringTest4() {
	testNotTerminal("foobar", "STRING")
}

@Test
def void wsTest1() {
	testTerminal(" \t \r \n ", "WS")
}

@Test
def void numberTest1() {
	'''+12.34e-56'''.testParserRule("NUMBER")
}

@Test
def void numberTest2() {
	'''12'''.testParserRule("NUMBER")
}

@Test
def void numberTest3() {
	'''12e+3'''.testParserRule("NUMBER")
}

}