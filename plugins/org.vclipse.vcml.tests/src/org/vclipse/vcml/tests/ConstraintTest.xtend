package org.vclipse.vcml.tests

import org.eclipse.xtext.junit4.InjectWith
import org.junit.Test
import org.junit.runner.RunWith
import org.vclipse.constraint.ConstraintInjectorProvider

import org.eclipse.xtext.junit4.XtextRunner


@InjectWith(typeof(ConstraintInjectorProvider))
@RunWith(typeof(XtextRunner))

class ConstraintTest extends DependencyTest {

@Test
def void shortVarTest1() {
	'''?xid'''.testParserRule("SHORTVAR")
}

@Test
def void shortVarTest2() {
	'''#xid'''.testParserRule("SHORTVAR")
}

@Test
def void shortVarTest3() {
	'''xid'''.testParserRule("SHORTVAR")
}

@Test
def void constraintSourceTest1() {
	'''
    objects: Obj is_a (300)MyClass . 
    restrictions:
      Obj.Param1 in ('A', 'B') if
        Obj.POWER = 'AC',
      Obj.CORE in ('C', 'D') if
        Obj.POWER = 'DC'. 
    inferences: MyClass.CORE. 
	'''.testParserRule("ConstraintSource")
}

@Test
def void constraintSourceTest2() {
	testParserRule('''
    objects: Obj is_a (300)MyClass . 
    condition: 
      (Obj.MODULE01 in ('A', 'B'))
    restrictions:
      Obj.Param1 in ('A', 'B') if
        Obj.POWER = 'AC',
      Obj.CORE in ('C', 'D') if
        Obj.POWER = 'DC'. 
    inferences: Obj.CORE. 
	'''.toString, "ConstraintSource")
}

@Test
def void contraintSourceTest3() {
	testParserRule('''
	objects: Obj is_a (300)MyClass . 
    condition:
      (Obj.MODULE01 in ('A', 'B', 'C', 'D') or
       Obj.MODULE02 in ('A', 'B', 'C', 'D') or
       Obj.MODULE03 in ('A', 'B', 'C', 'D') or
       Obj.MODULE04 in ('A', 'B', 'C', 'D', 'E')) and
       Obj.MODULE05 in ('A', 'B', 'C', 'D', 'E', 'F'). 
    
    restrictions: false. 
	'''.toString, "ConstraintSource")
}

@Test
def void contraintObjectTest1() {		// ConstraintClass
	testParserRule('''
	Obj is_a (300)MyClass 
	   where #shortVar = characteristicRef;
	         #shortVar2 = characteristicRef
	'''.toString, "ConstraintObject")
}

@Test
def void contraintObjectTest2() {		// ConstraintMaterial
	testParserRule('''
	Obj is_object (material) (300) (nr = 'foo', nr = 'bar')
	   where #shortVar = characteristicRef;
	         ?shortVar2 = characteristicRef
	'''.toString, "ConstraintObject")
}

@Test
def void conditionalContraintRestrictionTest1() {	// InCondition
	testParserRule('''
	characteristicRef in (0-9) if Obj.CHARACTERISTIC='NUM'
	'''.toString, "ConditionalConstraintRestriction")
}

@Test
def void conditionalContraintRestrictionTest2() {	// IsSpecified
	testParserRule('''
	characteristicRef specified
	'''.toString, "ConditionalConstraintRestriction")
}

@Test
def void conditionalContraintRestrictionTest3() {	// PartOfCondition
	testParserRule('''
	part_of (childRef, parentRef)
	'''.toString, "ConditionalConstraintRestriction")
}

@Test
def void conditionalContraintRestrictionTest4() {	// SubpartOfCondition
	testParserRule('''
	subpart_of (childRef, parentRef)
	'''.toString, "ConditionalConstraintRestriction")
}

@Test
def void conditionalContraintRestrictionTest5() {	// Comparison
	testParserRule('''
	('a' + 'b') * c <= 'd'
	'''.toString, "ConditionalConstraintRestriction")
}

@Test
def void conditionalContraintRestrictionTest6() {	// NegatedConstraintRestrictionLHS (Table)
	testParserRule('''
	not table testtable (
			characteristic1 = 'characteristic1',
			characteristic2 = 7
		)
	'''.toString, "ConditionalConstraintRestriction")
}

@Test
def void conditionalContraintRestrictionTest7() {	// NegatedConstraintRestrictionLHS (Function)
	testParserRule('''
	not function testfunction (
			characteristic1 = 'characteristic1'
		)
	'''.toString, "ConditionalConstraintRestriction")
}

@Test
def void conditionalContraintRestrictionTest8() {	// ConstraintRestrictionFalse
	testParserRule('''
	false
	'''.toString, "ConditionalConstraintRestriction")
}

@Test
def void characteristicReferenceTest1() {	// ObjectCharacteristicReference
	testParserRule('''
	Obj.CHARACTERISTIC
	'''.toString, "CharacteristicReference")
}

@Test
def void characteristicReferenceTest2() {	// ShortVarReference
	testParserRule('''
	Obj
	'''.toString, "CharacteristicReference")
}

@Test
def void literalTest1() {					// MDataCharacteristic
	testParserRule('''
	mdata characteristicRef
	'''.toString, "Literal")
}


}