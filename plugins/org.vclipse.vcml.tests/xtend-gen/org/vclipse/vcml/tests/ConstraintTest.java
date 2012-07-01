package org.vclipse.vcml.tests;

import org.eclipse.xtend2.lib.StringConcatenation;
import org.eclipse.xtext.junit4.InjectWith;
import org.eclipselabs.xtext.utils.unittesting.XtextRunner2;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.vclipse.constraint.ConstraintInjectorProvider;
import org.vclipse.vcml.tests.DependencyTest;

@InjectWith(value = ConstraintInjectorProvider.class)
@RunWith(value = XtextRunner2.class)
@SuppressWarnings("all")
public class ConstraintTest extends DependencyTest {
  @Test
  public void shortVarTest1() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("?xid");
    this.testParserRule(_builder, "SHORTVAR");
  }
  
  @Test
  public void shortVarTest2() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("#xid");
    this.testParserRule(_builder, "SHORTVAR");
  }
  
  @Test
  public void shortVarTest3() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("xid");
    this.testParserRule(_builder, "SHORTVAR");
  }
  
  @Test
  public void constraintSourceTest1() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("objects: Obj is_a (300)MyClass . ");
    _builder.newLine();
    _builder.append("restrictions:");
    _builder.newLine();
    _builder.append("  ");
    _builder.append("Obj.Param1 in (\'A\', \'B\') if");
    _builder.newLine();
    _builder.append("    ");
    _builder.append("Obj.POWER = \'AC\',");
    _builder.newLine();
    _builder.append("  ");
    _builder.append("Obj.CORE in (\'C\', \'D\') if");
    _builder.newLine();
    _builder.append("    ");
    _builder.append("Obj.POWER = \'DC\'. ");
    _builder.newLine();
    _builder.append("inferences: MyClass.CORE. ");
    _builder.newLine();
    this.testParserRule(_builder, "ConstraintSource");
  }
  
  @Test
  public void constraintSourceTest2() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("objects: Obj is_a (300)MyClass . ");
    _builder.newLine();
    _builder.append("condition: ");
    _builder.newLine();
    _builder.append("  ");
    _builder.append("(Obj.MODULE01 in (\'A\', \'B\'))");
    _builder.newLine();
    _builder.append("restrictions:");
    _builder.newLine();
    _builder.append("  ");
    _builder.append("Obj.Param1 in (\'A\', \'B\') if");
    _builder.newLine();
    _builder.append("    ");
    _builder.append("Obj.POWER = \'AC\',");
    _builder.newLine();
    _builder.append("  ");
    _builder.append("Obj.CORE in (\'C\', \'D\') if");
    _builder.newLine();
    _builder.append("    ");
    _builder.append("Obj.POWER = \'DC\'. ");
    _builder.newLine();
    _builder.append("inferences: Obj.CORE. ");
    _builder.newLine();
    String _string = _builder.toString();
    this.testParserRule(_string, "ConstraintSource");
  }
  
  @Test
  public void contraintSourceTest3() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("objects: Obj is_a (300)MyClass . ");
    _builder.newLine();
    _builder.append("    ");
    _builder.append("condition:");
    _builder.newLine();
    _builder.append("      ");
    _builder.append("(Obj.MODULE01 in (\'A\', \'B\', \'C\', \'D\') or");
    _builder.newLine();
    _builder.append("       ");
    _builder.append("Obj.MODULE02 in (\'A\', \'B\', \'C\', \'D\') or");
    _builder.newLine();
    _builder.append("       ");
    _builder.append("Obj.MODULE03 in (\'A\', \'B\', \'C\', \'D\') or");
    _builder.newLine();
    _builder.append("       ");
    _builder.append("Obj.MODULE04 in (\'A\', \'B\', \'C\', \'D\', \'E\')) and");
    _builder.newLine();
    _builder.append("       ");
    _builder.append("Obj.MODULE05 in (\'A\', \'B\', \'C\', \'D\', \'E\', \'F\'). ");
    _builder.newLine();
    _builder.append("    ");
    _builder.newLine();
    _builder.append("    ");
    _builder.append("restrictions: false. ");
    _builder.newLine();
    String _string = _builder.toString();
    this.testParserRule(_string, "ConstraintSource");
  }
  
  @Test
  public void contraintObjectTest1() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("Obj is_a (300)MyClass ");
    _builder.newLine();
    _builder.append("   ");
    _builder.append("where #shortVar = characteristicRef;");
    _builder.newLine();
    _builder.append("         ");
    _builder.append("#shortVar2 = characteristicRef");
    _builder.newLine();
    String _string = _builder.toString();
    this.testParserRule(_string, "ConstraintObject");
  }
  
  @Test
  public void contraintObjectTest2() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("Obj is_object (material) (300) (nr = \'foo\', nr = \'bar\')");
    _builder.newLine();
    _builder.append("   ");
    _builder.append("where #shortVar = characteristicRef;");
    _builder.newLine();
    _builder.append("         ");
    _builder.append("?shortVar2 = characteristicRef");
    _builder.newLine();
    String _string = _builder.toString();
    this.testParserRule(_string, "ConstraintObject");
  }
  
  @Test
  public void conditionalContraintRestrictionTest1() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("characteristicRef in (0-9) if Obj.CHARACTERISTIC=\'NUM\'");
    _builder.newLine();
    String _string = _builder.toString();
    this.testParserRule(_string, "ConditionalConstraintRestriction");
  }
  
  @Test
  public void conditionalContraintRestrictionTest2() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("characteristicRef specified");
    _builder.newLine();
    String _string = _builder.toString();
    this.testParserRule(_string, "ConditionalConstraintRestriction");
  }
  
  @Test
  public void conditionalContraintRestrictionTest3() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("part_of (childRef, parentRef)");
    _builder.newLine();
    String _string = _builder.toString();
    this.testParserRule(_string, "ConditionalConstraintRestriction");
  }
  
  @Test
  public void conditionalContraintRestrictionTest4() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("subpart_of (childRef, parentRef)");
    _builder.newLine();
    String _string = _builder.toString();
    this.testParserRule(_string, "ConditionalConstraintRestriction");
  }
  
  @Test
  public void conditionalContraintRestrictionTest5() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("(\'a\' + \'b\') * c <= \'d\'");
    _builder.newLine();
    String _string = _builder.toString();
    this.testParserRule(_string, "ConditionalConstraintRestriction");
  }
  
  @Test
  public void conditionalContraintRestrictionTest6() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("not table testtable (");
    _builder.newLine();
    _builder.append("\t\t");
    _builder.append("characteristic1 = \'characteristic1\',");
    _builder.newLine();
    _builder.append("\t\t");
    _builder.append("characteristic2 = 7");
    _builder.newLine();
    _builder.append("\t");
    _builder.append(")");
    _builder.newLine();
    String _string = _builder.toString();
    this.testParserRule(_string, "ConditionalConstraintRestriction");
  }
  
  @Test
  public void conditionalContraintRestrictionTest7() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("not function testfunction (");
    _builder.newLine();
    _builder.append("\t\t");
    _builder.append("characteristic1 = \'characteristic1\'");
    _builder.newLine();
    _builder.append("\t");
    _builder.append(")");
    _builder.newLine();
    String _string = _builder.toString();
    this.testParserRule(_string, "ConditionalConstraintRestriction");
  }
  
  @Test
  public void conditionalContraintRestrictionTest8() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("false");
    _builder.newLine();
    String _string = _builder.toString();
    this.testParserRule(_string, "ConditionalConstraintRestriction");
  }
  
  @Test
  public void characteristicReferenceTest1() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("Obj.CHARACTERISTIC");
    _builder.newLine();
    String _string = _builder.toString();
    this.testParserRule(_string, "CharacteristicReference");
  }
  
  @Test
  public void characteristicReferenceTest2() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("Obj");
    _builder.newLine();
    String _string = _builder.toString();
    this.testParserRule(_string, "CharacteristicReference");
  }
  
  @Test
  public void literalTest1() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("mdata characteristicRef");
    _builder.newLine();
    String _string = _builder.toString();
    this.testParserRule(_string, "Literal");
  }
}
