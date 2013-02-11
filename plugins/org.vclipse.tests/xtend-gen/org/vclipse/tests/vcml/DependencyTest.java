package org.vclipse.tests.vcml;

import org.eclipse.xtend2.lib.StringConcatenation;
import org.eclipselabs.xtext.utils.unittesting.XtextTest;
import org.junit.Test;

@SuppressWarnings("all")
public class DependencyTest extends XtextTest {
  public void testParserRule(final CharSequence textToParse, final String ruleName) {
    String _string = textToParse.toString();
    this.testParserRule(_string, ruleName);
  }
  
  @Test
  public void fileTest() {
    this.testFile("VcmlTest/dependencytest.vcml");
  }
  
  @Test
  public void expressionTest1() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("+ (7 * 3) + (2 * 3) ");
    _builder.newLine();
    this.testParserRule(_builder, "Expression");
  }
  
  @Test
  public void expressionTest2() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("++- (7 * 3) + (2 * 3) ");
    _builder.newLine();
    this.testParserRule(_builder, "Expression");
  }
  
  @Test
  public void expressionTest3() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("++- (7 * 3) * (2 * 3) ");
    _builder.newLine();
    this.testParserRule(_builder, "Expression");
  }
  
  @Test
  public void expressionTest4() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("++- (7 + 3) * (2 * 3) ");
    _builder.newLine();
    this.testParserRule(_builder, "Expression");
  }
  
  @Test
  public void expressionTest5() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("(\'a\' * \'b\') * (\'b\' * 7) ");
    _builder.newLine();
    this.testParserRule(_builder, "Expression");
  }
  
  @Test
  public void expressionTest6() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("(sin (7) * \'b\') * (cos(\'b\') * 7) ");
    _builder.newLine();
    this.testParserRule(_builder, "Expression");
  }
  
  @Test
  public void conditionTest1() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("not \'a\' eq \'b\' and not \'b\' eq \'a\'");
    _builder.newLine();
    this.testParserRule(_builder, "Condition");
  }
  
  @Test
  public void conditionTest2() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("(not \'a\' eq \'b\') and (not (\'c\' eq \'a\' or \'c\' eq \'a\'))");
    _builder.newLine();
    this.testParserRule(_builder, "Condition");
  }
  
  @Test
  public void conditionTest3() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("(not \'a\' eq 2) and (not (\'c\' eq 2 or \'c\' eq (2)))");
    _builder.newLine();
    this.testParserRule(_builder, "Condition");
  }
  
  @Test
  public void listTest1() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("(1, 2, 3, 4, 5 - 6)");
    _builder.newLine();
    this.testParserRule(_builder, "NumberList");
  }
  
  @Test
  public void listTest2() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("(\'a\', \'b\', \'c\', \'d\')");
    _builder.newLine();
    this.testParserRule(_builder, "List");
  }
  
  @Test
  public void functionTest1() {
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
  
  @Test
  public void tableTest1() {
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
  
  @Test
  public void idTest() {
    this.testTerminal("abc", "ID");
    this.testTerminal("abc4", "ID");
    this.testTerminal("_abc", "ID");
    this.testNotTerminal("1abc", "ID");
    this.testNotTerminal("#abc", "ID");
  }
  
  @Test
  public void dependencyCommentTest() {
    this.testTerminal("\n*asdf", "SL_COMMENT");
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("foo");
    String _string = _builder.toString();
    this.testNotTerminal(_string, "SL_COMMENT");
    StringConcatenation _builder_1 = new StringConcatenation();
    _builder_1.append("\\\\tbar");
    String _string_1 = _builder_1.toString();
    this.testNotTerminal(_string_1, "SL_COMMENT");
  }
  
  @Test
  public void symbolTest() {
    this.testTerminal("\'a\'", "SYMBOL");
    this.testTerminal("\'\b\'", "SYMBOL");
    this.testNotTerminal("\'\t\'", "SYMBOL");
  }
  
  @Test
  public void intTest() {
    this.testTerminal("0123456789", "INT");
    this.testNotTerminal("0.123456789", "INT");
  }
  
  @Test
  public void stringTest1() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("\" \\\\\\\" \\\\b \\\\t \\\\n \\\\\\\\ \\\\\' \"");
    String _string = _builder.toString();
    this.testTerminal(_string, "STRING");
  }
  
  @Test
  public void stringTest2() {
    this.testTerminal("\'\\\" \\b \\t \\n \\\\ \\\'\'", "STRING");
  }
  
  @Test
  public void stringTest3() {
    this.testTerminal("\"foobar\"", "STRING");
  }
  
  @Test
  public void stringTest4() {
    this.testNotTerminal("foobar", "STRING");
  }
  
  @Test
  public void wsTest1() {
    this.testTerminal(" \t \r \n ", "WS");
  }
  
  @Test
  public void numberTest1() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("+12.34e-56");
    this.testParserRule(_builder, "NUMBER");
  }
  
  @Test
  public void numberTest2() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("12");
    this.testParserRule(_builder, "NUMBER");
  }
  
  @Test
  public void numberTest3() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("12e+3");
    this.testParserRule(_builder, "NUMBER");
  }
}
