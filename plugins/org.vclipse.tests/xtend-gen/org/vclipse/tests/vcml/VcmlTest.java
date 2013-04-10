/**
 * Copyright (c) 2010 - 2013 webXcerpt Software GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     	webXcerpt Software GmbH - initial creator
 * 		www.webxcerpt.com
 */
package org.vclipse.tests.vcml;

import org.eclipse.xtend2.lib.StringConcatenation;
import org.eclipse.xtext.junit4.InjectWith;
import org.eclipse.xtext.junit4.XtextRunner;
import org.vclipse.tests.vcml.VCMLInjectorProvider;

/* @RunWith(XtextRunner.class) */@InjectWith(VCMLInjectorProvider.class)
@SuppressWarnings("all")
public class VcmlTest /* implements XtextTest  */{
  public Object testParserRule(final CharSequence textToParse, final String ruleName) {
    String _string = textToParse.toString();
    Object _testParserRule = this.testParserRule(_string, ruleName);
    return _testParserRule;
  }
  
  /* @Test
   */public void fileTest() {
    throw new Error("Unresolved compilation problems:"
      + "\nThe method setResourceRoot is undefined for the type VcmlTest"
      + "\nThe method testFile is undefined for the type VcmlTest");
  }
  
  /* @Test
   */public void fileTest2() {
    throw new Error("Unresolved compilation problems:"
      + "\nThe method setResourceRoot is undefined for the type VcmlTest"
      + "\nThe method testFile is undefined for the type VcmlTest");
  }
  
  /* @Test
   */public void parseImportTest() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("import \"platform:/resource/org.vclipse.vcml.mm/src/org/vclipse/vcml/mm/VCML.ecore\"");
    _builder.newLine();
    this.testParserRule(_builder, "Import");
  }
  
  /* @Test
   */public void parseOptionTest() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("ECM = \"engineering change master\" ");
    _builder.newLine();
    this.testParserRule(_builder, "Option");
  }
  
  /* @Test
   */public void parseBillOfMaterialTest() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("billofmaterial {");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("items {");
    _builder.newLine();
    _builder.append("\t\t");
    _builder.append("1");
    _builder.newLine();
    _builder.append("\t\t");
    _builder.append("item1");
    _builder.newLine();
    _builder.append("\t\t");
    _builder.append("dependencies {");
    _builder.newLine();
    _builder.append("\t\t\t");
    _builder.append("item0");
    _builder.newLine();
    _builder.append("\t\t\t");
    _builder.append("2 item2");
    _builder.newLine();
    _builder.append("\t\t\t");
    _builder.append("3 item3");
    _builder.newLine();
    _builder.append("\t\t");
    _builder.append("}");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("}");
    _builder.newLine();
    _builder.append("}");
    _builder.newLine();
    this.testParserRule(_builder, "BillOfMaterial");
  }
  
  /* @Test
   */public void parseCharacteristicSymbolicTest() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("characteristic CSTIC1 {");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("description \"test\"");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("documentation \"test-documentation\"");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("symbolic {numberOfChars 30");
    _builder.newLine();
    _builder.append("\t\t");
    _builder.append("[caseSensitive]");
    _builder.newLine();
    _builder.append("\t\t");
    _builder.append("values {\'A\' \'B\' \'C\'}");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("}");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("status locked");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("group \"Group1\"");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("[");
    _builder.newLine();
    _builder.append("\t\t");
    _builder.append("additionalValues");
    _builder.newLine();
    _builder.append("\t\t");
    _builder.append("noDisplay");
    _builder.newLine();
    _builder.append("\t\t");
    _builder.append("multiValue");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("]");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("dependencies {");
    _builder.newLine();
    _builder.append("\t\t");
    _builder.append("dependency1");
    _builder.newLine();
    _builder.append("\t\t");
    _builder.append("dependency2");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("}");
    _builder.newLine();
    _builder.append("}");
    _builder.newLine();
    this.testParserRule(_builder, "Characteristic");
  }
  
  /* @Test
   */public void parseCharacteristicNumericTest() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("characteristic CSTIC1 {");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("description \"test\"");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("documentation \"test-documentation\"");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("numeric {");
    _builder.newLine();
    _builder.append("\t\t");
    _builder.append("numberOfChars 30");
    _builder.newLine();
    _builder.append("\t\t");
    _builder.append("decimalPlaces 2");
    _builder.newLine();
    _builder.append("\t\t");
    _builder.append("unit \"unit1\"");
    _builder.newLine();
    _builder.append("\t\t");
    _builder.append("[");
    _builder.newLine();
    _builder.append("\t\t\t");
    _builder.append("negativeValuesAllowed");
    _builder.newLine();
    _builder.append("\t\t\t");
    _builder.append("intervalValuesAllowed");
    _builder.newLine();
    _builder.append("\t\t");
    _builder.append("]");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("}");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("status locked");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("group \"Group1\"");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("[");
    _builder.newLine();
    _builder.append("\t\t");
    _builder.append("additionalValues");
    _builder.newLine();
    _builder.append("\t\t");
    _builder.append("noDisplay");
    _builder.newLine();
    _builder.append("\t\t");
    _builder.append("multiValue");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("]");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("dependencies {");
    _builder.newLine();
    _builder.append("\t\t");
    _builder.append("dependency1");
    _builder.newLine();
    _builder.append("\t\t");
    _builder.append("dependency2");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("}");
    _builder.newLine();
    _builder.append("}");
    _builder.newLine();
    this.testParserRule(_builder, "Characteristic");
  }
  
  /* @Test
   */public void parseCharacteristicDateTest() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("characteristic CSTIC1 {");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("description \"test\"");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("documentation \"test-documentation\"");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("date {");
    _builder.newLine();
    _builder.append("\t\t");
    _builder.append("[intervalValuesAllowed]");
    _builder.newLine();
    _builder.append("\t\t");
    _builder.append("values {");
    _builder.newLine();
    _builder.append("\t\t\t");
    _builder.append("01.01.2012-02.01.2012 {");
    _builder.newLine();
    _builder.append("\t\t\t\t");
    _builder.append("documentation \"Date Documentation\"");
    _builder.newLine();
    _builder.append("\t\t\t\t");
    _builder.append("dependencies {");
    _builder.newLine();
    _builder.append("\t\t\t\t\t");
    _builder.append("dependency3");
    _builder.newLine();
    _builder.append("\t\t\t\t");
    _builder.append("}");
    _builder.newLine();
    _builder.append("\t\t\t");
    _builder.append("}");
    _builder.newLine();
    _builder.append("\t\t");
    _builder.append("}");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("}");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("status locked");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("group \"Group1\"");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("[");
    _builder.newLine();
    _builder.append("\t\t");
    _builder.append("additionalValues");
    _builder.newLine();
    _builder.append("\t\t");
    _builder.append("noDisplay");
    _builder.newLine();
    _builder.append("\t\t");
    _builder.append("multiValue");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("]");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("dependencies {");
    _builder.newLine();
    _builder.append("\t\t");
    _builder.append("dependency1");
    _builder.newLine();
    _builder.append("\t\t");
    _builder.append("dependency2");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("}");
    _builder.newLine();
    _builder.append("}");
    _builder.newLine();
    this.testParserRule(_builder, "Characteristic");
  }
  
  /* @Test
   */public void parseClassTest() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("class (300) DE {");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("description\"A Test Class\"");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("status released");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("group \"TestGroup\"");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("characteristics {");
    _builder.newLine();
    _builder.append("\t\t");
    _builder.append("DE");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("}");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("superclasses {");
    _builder.newLine();
    _builder.append("\t\t");
    _builder.append("(300) myId");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("}");
    _builder.newLine();
    _builder.append("}");
    _builder.newLine();
    this.testParserRule(_builder, "Class");
  }
  
  /* @Test
   */public void parseConfigurationProfileTest() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("configurationprofile testprofile {");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("status locked");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("bomapplication bomid");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("uidesign designid");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("netid");
    _builder.newLine();
    _builder.append("\t");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("dep01\t\t// DependencyNet");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("dep02");
    _builder.newLine();
    _builder.append("\t");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("1 dep03\t\t// ConfigurationProfileEntry");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("2 dep04");
    _builder.newLine();
    _builder.append("}");
    _builder.newLine();
    this.testParserRule(_builder, "ConfigurationProfile");
  }
  
  /* @Test
   */public void parseProcedureTest() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("procedure testprocedure {");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("description \"procedure test description\"");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("documentation \"procedure test documentation\"");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("status released");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("group \"TestGroup\"");
    _builder.newLine();
    _builder.append("}");
    _builder.newLine();
    this.testParserRule(_builder, "Procedure");
  }
  
  /* @Test
   */public void parseSelectionConditionTest() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("selectioncondition testselectioncondition {");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("description \"selection condition test description\"");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("documentation \"selection condition test documentation\"");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("status inPreparation");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("group \"TestGroup\"");
    _builder.newLine();
    _builder.append("}");
    _builder.newLine();
    this.testParserRule(_builder, "SelectionCondition");
  }
  
  /* @Test
   */public void parsePreconditionTest() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("precondition testprecondition {");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("description \"precondition test description\"");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("documentation \"precondition test documentation\"");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("status released");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("group \"TestGroup\"");
    _builder.newLine();
    _builder.append("}");
    _builder.newLine();
    this.testParserRule(_builder, "Precondition");
  }
  
  /* @Test
   */public void parseDependencyNetTest() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("dependencynet netid {");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("description \"A test profile\"");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("documentation \"A test profile documentation\"");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("status released");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("group \"Dependency Group\"");
    _builder.newLine();
    _builder.append("\t");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("testconstraint");
    _builder.newLine();
    _builder.append("}");
    _builder.newLine();
    this.testParserRule(_builder, "DependencyNet");
  }
  
  /* @Test
   */public void parseMaterialTest() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("material testmaterial {");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("description \"material test description\"");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("type typeId");
    _builder.newLine();
    _builder.append("\t");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("billofmaterial {");
    _builder.newLine();
    _builder.append("\t\t");
    _builder.append("items {");
    _builder.newLine();
    _builder.append("\t\t\t");
    _builder.append("1 item1");
    _builder.newLine();
    _builder.append("\t\t\t");
    _builder.append("dependencies {");
    _builder.newLine();
    _builder.append("\t\t\t\t");
    _builder.append("2 item2");
    _builder.newLine();
    _builder.append("\t\t\t\t");
    _builder.append("3 item3");
    _builder.newLine();
    _builder.append("\t\t\t");
    _builder.append("}");
    _builder.newLine();
    _builder.append("\t\t");
    _builder.append("}");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("}");
    _builder.newLine();
    _builder.append("\t");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("classes {");
    _builder.newLine();
    _builder.append("\t\t");
    _builder.append("(300) class300 {");
    _builder.newLine();
    _builder.append("\t\t\t");
    _builder.append("characteristicId = 7");
    _builder.newLine();
    _builder.append("\t\t");
    _builder.append("}");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("}");
    _builder.newLine();
    _builder.append("\t");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("configurationprofile testprofile {");
    _builder.newLine();
    _builder.append("\t\t");
    _builder.append("status released");
    _builder.newLine();
    _builder.append("\t\t");
    _builder.append("bomapplication bomId");
    _builder.newLine();
    _builder.append("\t\t");
    _builder.append("uidesign uiId");
    _builder.newLine();
    _builder.append("\t\t");
    _builder.append("netId");
    _builder.newLine();
    _builder.append("\t\t");
    _builder.append("1 configurationprofileentry");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("}");
    _builder.newLine();
    _builder.append("}");
    _builder.newLine();
    this.testParserRule(_builder, "Material");
  }
  
  /* @Test
   */public void parseInterfaceDesignTest() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("interfacedesign testdesign {");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("characteristicgroup testcharacteristic {");
    _builder.newLine();
    _builder.append("\t\t");
    _builder.append("description \"test characteristicgroup\"");
    _builder.newLine();
    _builder.append("\t\t");
    _builder.append("DE");
    _builder.newLine();
    _builder.append("\t\t");
    _builder.append("EN");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("}");
    _builder.newLine();
    _builder.append("}");
    _builder.newLine();
    this.testParserRule(_builder, "InterfaceDesign");
  }
  
  /* @Test
   */public void parseConstraintTest() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("constraint testconstraint {");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("description \" A test constraint\"");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("documentation \"A test constraint documentation\"");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("status locked");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("group \"Constraint Group\"");
    _builder.newLine();
    _builder.append("}");
    _builder.newLine();
    this.testParserRule(_builder, "Constraint");
  }
  
  /* @Test
   */public void parseVariantFunctionTest() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("variantfunction testfunction {");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("description \"variant function test description\"");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("status released");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("group \"TestGroup\"");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("arguments {");
    _builder.newLine();
    _builder.append("\t\t");
    _builder.append("in DE");
    _builder.newLine();
    _builder.append("\t\t");
    _builder.append("AF");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("}");
    _builder.newLine();
    _builder.append("}");
    _builder.newLine();
    this.testParserRule(_builder, "VariantFunction");
  }
  
  /* @Test
   */public void parseVariantTableTest() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("varianttable testtable {");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("description \"variant table test description\"");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("status locked");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("group \"TestGroup\"");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("arguments {");
    _builder.newLine();
    _builder.append("\t\t");
    _builder.append("key DE");
    _builder.newLine();
    _builder.append("\t\t");
    _builder.append("key EN");
    _builder.newLine();
    _builder.append("\t\t");
    _builder.append("AF");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("}");
    _builder.newLine();
    _builder.append("}");
    _builder.newLine();
    this.testParserRule(_builder, "VariantTable");
  }
  
  /* @Test
   */public void parseVariantTableContentTest() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("varianttablecontent DE {");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("row 1 2 \'three\' \'four\'");
    _builder.newLine();
    _builder.append("}");
    _builder.newLine();
    this.testParserRule(_builder, "VariantTableContent");
  }
  
  /* @Test
   */public void idTest() {
    throw new Error("Unresolved compilation problems:"
      + "\nThe method testTerminal is undefined for the type VcmlTest"
      + "\nThe method testTerminal is undefined for the type VcmlTest"
      + "\nThe method testTerminal is undefined for the type VcmlTest"
      + "\nThe method testNotTerminal is undefined for the type VcmlTest"
      + "\nThe method testNotTerminal is undefined for the type VcmlTest");
  }
  
  /* @Test
   */public void stringTest() {
    throw new Error("Unresolved compilation problems:"
      + "\nThe method testTerminal is undefined for the type VcmlTest"
      + "\nThe method testTerminal is undefined for the type VcmlTest"
      + "\nThe method testTerminal is undefined for the type VcmlTest");
  }
  
  /* @Test
   */public void symbolTest() {
    throw new Error("Unresolved compilation problems:"
      + "\nThe method testTerminal is undefined for the type VcmlTest"
      + "\nThe method testTerminal is undefined for the type VcmlTest"
      + "\nThe method testNotTerminal is undefined for the type VcmlTest");
  }
}
