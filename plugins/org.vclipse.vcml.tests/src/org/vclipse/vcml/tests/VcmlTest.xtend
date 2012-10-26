package org.vclipse.vcml.tests

import org.eclipse.xtext.junit4.InjectWith
import org.eclipse.xtext.junit4.XtextRunner
import org.eclipselabs.xtext.utils.unittesting.XtextTest
import org.junit.Test
import org.junit.runner.RunWith
import org.vclipse.vcml.VCMLInjectorProvider

@RunWith(typeof(XtextRunner))
@InjectWith(typeof(VCMLInjectorProvider))

class VcmlTest extends XtextTest {

// @Inject ParseHelper<Model> parser

def testParserRule(CharSequence textToParse, String ruleName) {
        testParserRule(textToParse.toString, ruleName)
}

@Test
def void fileTest() {
	//setResourceRoot("classpath:/org.vclipse.vcml.tests/resources")
	//System.out.println(getClass.classLoader.getResource("/resources").toString)
	//setResourceRoot(getClass.classLoader.getResource("/resources").toString)
	setResourceRoot("file:C:/eclipse/workspace-splitting/org.vclipse.vcml.tests/resources")
	testFile("VcmlTest/characteristictest.vcml")
}

@Test
def void fileTest2() {
	setResourceRoot("classpath:/resources")
	//setResourceRoot("file:C:/eclipse/workspace-splitting/org.vclipse.vcml.tests/resources")
	testFile("characteristictest.vcml")
}

@Test
def void parseImportTest() {
	'''import "platform:/resource/org.vclipse.vcml.mm/src/org/vclipse/vcml/mm/VCML.ecore"
	'''.testParserRule("Import")
}

@Test
def void parseOptionTest() {
	'''ECM = "engineering change master" 
	'''.testParserRule("Option")
}

@Test
def void parseBillOfMaterialTest() {
	'''
		billofmaterial {
			items {
				1
				item1
				dependencies {
					item0
					2 item2
					3 item3
				}
			}
		}
	'''.testParserRule("BillOfMaterial")
}

@Test
def void parseCharacteristicSymbolicTest() {
	'''
		characteristic CSTIC1 {
			description "test"
			documentation "test-documentation"
			symbolic {numberOfChars 30
				[caseSensitive]
				values {'A' 'B' 'C'}
			}
			status locked
			group "Group1"
			[
				additionalValues
				noDisplay
				multiValue
			]
			dependencies {
				dependency1
				dependency2
			}
		}
	'''.testParserRule("Characteristic")
}

@Test
def void parseCharacteristicNumericTest() {
	'''
		characteristic CSTIC1 {
			description "test"
			documentation "test-documentation"
			numeric {
				numberOfChars 30
				decimalPlaces 2
				unit "unit1"
				[
					negativeValuesAllowed
					intervalValuesAllowed
				]
			}
			status locked
			group "Group1"
			[
				additionalValues
				noDisplay
				multiValue
			]
			dependencies {
				dependency1
				dependency2
			}
		}
	'''.testParserRule("Characteristic")
}

@Test
def void parseCharacteristicDateTest() {
	'''
		characteristic CSTIC1 {
			description "test"
			documentation "test-documentation"
			date {
				[intervalValuesAllowed]
				values {
					01.01.2012-02.01.2012 {
						documentation "Date Documentation"
						dependencies {
							dependency3
						}
					}
				}
			}
			status locked
			group "Group1"
			[
				additionalValues
				noDisplay
				multiValue
			]
			dependencies {
				dependency1
				dependency2
			}
		}
		'''.testParserRule("Characteristic")
}

@Test
def void parseClassTest() {
	'''
		class (300) DE {
			description"A Test Class"
			status released
			group "TestGroup"
			characteristics {
				DE
			}
			superclasses {
				(300) myId
			}
		}
	'''.testParserRule("Class")
}

@Test
def void parseConfigurationProfileTest() {
	'''
		configurationprofile testprofile {
			status locked
			bomapplication bomid
			uidesign designid
			netid
			
			dep01		// DependencyNet
			dep02
			
			1 dep03		// ConfigurationProfileEntry
			2 dep04
		}
	'''.testParserRule("ConfigurationProfile")
}

@Test
def void parseProcedureTest() {
	'''
		procedure testprocedure {
			description "procedure test description"
			documentation "procedure test documentation"
			status released
			group "TestGroup"
		}
	'''.testParserRule("Procedure")
}

@Test
def void parseSelectionConditionTest() {
	'''
		selectioncondition testselectioncondition {
			description "selection condition test description"
			documentation "selection condition test documentation"
			status inPreparation
			group "TestGroup"
		}
	'''.testParserRule("SelectionCondition")
}

@Test
def void parsePreconditionTest() {
	'''
		precondition testprecondition {
			description "precondition test description"
			documentation "precondition test documentation"
			status released
			group "TestGroup"
		}
	'''.testParserRule("Precondition")
}

@Test
def void parseDependencyNetTest() {
	'''
			dependencynet netid {
				description "A test profile"
				documentation "A test profile documentation"
				status released
				group "Dependency Group"
				
				testconstraint
			}
	'''.testParserRule("DependencyNet")
}

@Test
def void parseMaterialTest() {
	'''
		material testmaterial {
			description "material test description"
			type typeId
			
			billofmaterial {
				items {
					1 item1
					dependencies {
						2 item2
						3 item3
					}
				}
			}
			
			classes {
				(300) class300 {
					characteristicId = 7
				}
			}
			
			configurationprofile testprofile {
				status released
				bomapplication bomId
				uidesign uiId
				netId
				1 configurationprofileentry
			}
		}
	'''.testParserRule("Material")
}

@Test
def void parseInterfaceDesignTest() {
	'''
		interfacedesign testdesign {
			characteristicgroup testcharacteristic {
				description "test characteristicgroup"
				DE
				EN
			}
		}
	'''.testParserRule("InterfaceDesign")
}

@Test
def void parseConstraintTest() {
	'''
	constraint testconstraint {
		description " A test constraint"
		documentation "A test constraint documentation"
		status locked
		group "Constraint Group"
	}
	'''.testParserRule("Constraint")
}

@Test
def void parseVariantFunctionTest() {
	'''
		variantfunction testfunction {
			description "variant function test description"
			status released
			group "TestGroup"
			arguments {
				in DE
				AF
			}
		}
	'''.testParserRule("VariantFunction")
}

@Test
def void parseVariantTableTest() {
	'''
		varianttable testtable {
			description "variant table test description"
			status locked
			group "TestGroup"
			arguments {
				key DE
				key EN
				AF
			}
		}
	'''.testParserRule("VariantTable")
}

@Test
def void parseVariantTableContentTest() {
	'''
		varianttablecontent DE {
			row 1 2 'three' 'four'
		}
	'''.testParserRule("VariantTableContent")
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
def void stringTest() {
	testTerminal('''"\\t"'''.toString, "STRING")
	
	testTerminal('''"foo"'''.toString, "STRING")
	
	testTerminal('''"\\tbar"'''.toString, "STRING")
}

@Test
def void symbolTest() {
	testTerminal("'a'", "SYMBOL")
	
	testTerminal("'\b'", "SYMBOL")
	
	testNotTerminal("'\t'", "SYMBOL")
}

}