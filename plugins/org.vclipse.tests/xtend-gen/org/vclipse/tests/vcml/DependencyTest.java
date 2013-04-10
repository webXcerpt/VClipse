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

@SuppressWarnings("all")
public class DependencyTest /* implements XtextTest  */{
  public Object testParserRule(final CharSequence textToParse, final String ruleName) {
    String _string = textToParse.toString();
    Object _testParserRule = this.testParserRule(_string, ruleName);
    return _testParserRule;
  }
  
  /* @Test
   */public void fileTest() {
    throw new Error("Unresolved compilation problems:"
      + "\nThe method testFile is undefined for the type DependencyTest");
  }
  
  /* @Test
   */public void expressionTest1() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("+ (7 * 3) + (2 * 3) ");
    _builder.newLine();
    this.testParserRule(_builder, "Expression");
  }
  
  /* @Test
   */public void expressionTest2() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("++- (7 * 3) + (2 * 3) ");
    _builder.newLine();
    this.testParserRule(_builder, "Expression");
  }
  
  /* @Test
   */public void expressionTest3() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("++- (7 * 3) * (2 * 3) ");
    _builder.newLine();
    this.testParserRule(_builder, "Expression");
  }
  
  /* @Test
   */public void expressionTest4() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("++- (7 + 3) * (2 * 3) ");
    _builder.newLine();
    this.testParserRule(_builder, "Expression");
  }
  
  /* @Test
   */public void expressionTest5() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("(\'a\' * \'b\') * (\'b\' * 7) ");
    _builder.newLine();
    this.testParserRule(_builder, "Expression");
  }
  
  /* @Test
   */public void expressionTest6() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("(sin (7) * \'b\') * (cos(\'b\') * 7) ");
    _builder.newLine();
    this.testParserRule(_builder, "Expression");
  }
  
  /* @Test
   */public void conditionTest1() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("not \'a\' eq \'b\' and not \'b\' eq \'a\'");
    _builder.newLine();
    this.testParserRule(_builder, "Condition");
  }
  
  /* @Test
   */public void conditionTest2() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("(not \'a\' eq \'b\') and (not (\'c\' eq \'a\' or \'c\' eq \'a\'))");
    _builder.newLine();
    this.testParserRule(_builder, "Condition");
  }
  
  /* @Test
   */public void conditionTest3() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("(not \'a\' eq 2) and (not (\'c\' eq 2 or \'c\' eq (2)))");
    _builder.newLine();
    this.testParserRule(_builder, "Condition");
  }
  
  /* @Test
   */public void listTest1() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("(1, 2, 3, 4, 5 - 6)");
    _builder.newLine();
    this.testParserRule(_builder, "NumberList");
  }
  
  /* @Test
   */public void listTest2() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("(\'a\', \'b\', \'c\', \'d\')");
    _builder.newLine();
    this.testParserRule(_builder, "List");
  }
  
  /* @Test
   */public void functionTest1() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("function testfunction (");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("characteristic1 = \'characteristic1\'");
    _builder.newLine();
    _builder.append(")");
    _builder.newLine();
    this.testParserRule(_builder, "Function");
  }
  
  /* @Test
   */public void tableTest1() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("table testtable (");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("characteristic1 = \'characteristic1\',");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("characteristic2 = 7");
    _builder.newLine();
    _builder.append(")");
    _builder.newLine();
    this.testParserRule(_builder, "Table");
  }
  
  /* @Test
   */public void idTest() {
    throw new Error("Unresolved compilation problems:"
      + "\nThe method testTerminal is undefined for the type DependencyTest"
      + "\nThe method testTerminal is undefined for the type DependencyTest"
      + "\nThe method testTerminal is undefined for the type DependencyTest"
      + "\nThe method testNotTerminal is undefined for the type DependencyTest"
      + "\nThe method testNotTerminal is undefined for the type DependencyTest");
  }
  
  /* @Test
   */public void dependencyCommentTest() {
    throw new Error("Unresolved compilation problems:"
      + "\nThe method testTerminal is undefined for the type DependencyTest"
      + "\nThe method testNotTerminal is undefined for the type DependencyTest"
      + "\nThe method testNotTerminal is undefined for the type DependencyTest");
  }
  
  /* @Test
   */public void symbolTest() {
    throw new Error("Unresolved compilation problems:"
      + "\nThe method testTerminal is undefined for the type DependencyTest"
      + "\nThe method testTerminal is undefined for the type DependencyTest"
      + "\nThe method testNotTerminal is undefined for the type DependencyTest");
  }
  
  /* @Test
   */public void intTest() {
    throw new Error("Unresolved compilation problems:"
      + "\nThe method testTerminal is undefined for the type DependencyTest"
      + "\nThe method testNotTerminal is undefined for the type DependencyTest");
  }
  
  /* @Test
   */public void stringTest1() {
    throw new Error("Unresolved compilation problems:"
      + "\nThe method testTerminal is undefined for the type DependencyTest");
  }
  
  /* @Test
   */public void stringTest2() {
    throw new Error("Unresolved compilation problems:"
      + "\nThe method testTerminal is undefined for the type DependencyTest");
  }
  
  /* @Test
   */public void stringTest3() {
    throw new Error("Unresolved compilation problems:"
      + "\nThe method testTerminal is undefined for the type DependencyTest");
  }
  
  /* @Test
   */public void stringTest4() {
    throw new Error("Unresolved compilation problems:"
      + "\nThe method testNotTerminal is undefined for the type DependencyTest");
  }
  
  /* @Test
   */public void wsTest1() {
    throw new Error("Unresolved compilation problems:"
      + "\nThe method testTerminal is undefined for the type DependencyTest");
  }
  
  /* @Test
   */public void numberTest1() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("+12.34e-56");
    this.testParserRule(_builder, "NUMBER");
  }
  
  /* @Test
   */public void numberTest2() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("12");
    this.testParserRule(_builder, "NUMBER");
  }
  
  /* @Test
   */public void numberTest3() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("12e+3");
    this.testParserRule(_builder, "NUMBER");
  }
}
