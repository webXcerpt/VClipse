package org.vclipse.tests.vcml;

import org.eclipse.xtend2.lib.StringConcatenation;
import org.eclipse.xtext.junit4.InjectWith;
import org.eclipse.xtext.junit4.XtextRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.vclipse.tests.procedure.ProcedureInjectorProvider;
import org.vclipse.tests.vcml.DependencyTest;

@InjectWith(value = ProcedureInjectorProvider.class)
@RunWith(value = XtextRunner.class)
@SuppressWarnings("all")
public class ProcedureTest extends DependencyTest {
  @Test
  public void sumPartsTest1() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("$sum_parts ($self, characteristicId)");
    _builder.newLine();
    this.testParserRule(_builder, "SumParts");
  }
  
  @Test
  public void countPartsTest1() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("$count_parts ($parent)");
    _builder.newLine();
    this.testParserRule(_builder, "CountParts");
  }
  
  @Test
  public void typeOfTest1() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("type_of ($root, (material) (300) (nr=\'123\', nr=\'124\'))");
    _builder.newLine();
    this.testParserRule(_builder, "TypeOf");
  }
}
