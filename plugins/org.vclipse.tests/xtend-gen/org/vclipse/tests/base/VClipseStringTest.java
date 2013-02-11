package org.vclipse.tests.base;

import java.util.List;
import junit.framework.Assert;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.xtext.junit4.XtextRunner;
import org.eclipse.xtext.xbase.lib.Functions.Function0;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.eclipselabs.xtext.utils.unittesting.XtextTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.vclipse.base.VClipseStrings;
import org.vclipse.tests.VClipseTestUtilities;
import org.vclipse.vcml.vcml.VcmlPackage;

@RunWith(value = XtextRunner.class)
@SuppressWarnings("all")
public class VClipseStringTest extends XtextTest {
  private VcmlPackage vcmlPackage;
  
  public VClipseStringTest() {
    super(
      new Function0<String>() {
        public String apply() {
          String _simpleName = VClipseStringTest.class.getSimpleName();
          return _simpleName;
        }
      }.apply());
  }
  
  public void before() {
    this.vcmlPackage = VClipseTestUtilities.VCML_PACKAGE;
  }
  
  @Test
  public void test_BOMItem() {
    EClass type = this.vcmlPackage.getBOMItem();
    String _name = type.getName();
    List<String> result = VClipseStrings.splitCamelCase(_name);
    String _join = IterableExtensions.join(result, " ");
    int _size = result.size();
    Assert.assertEquals(_join, 2, _size);
    String _get = result.get(0);
    Assert.assertEquals(_get, "BOM");
    String _get_1 = result.get(1);
    Assert.assertEquals(_get_1, "Item");
  }
  
  @Test
  public void test_MDataCharacteristic_C() {
    EClass type = this.vcmlPackage.getMDataCharacteristic_C();
    String _name = type.getName();
    List<String> result = VClipseStrings.splitCamelCase(_name);
    String _join = IterableExtensions.join(result, " ");
    int _size = result.size();
    Assert.assertEquals(_join, 4, _size);
    String _get = result.get(0);
    Assert.assertEquals(_get, "M");
    String _get_1 = result.get(1);
    Assert.assertEquals(_get_1, "Data");
    String _get_2 = result.get(2);
    Assert.assertEquals(_get_2, "Characteristic_");
    String _get_3 = result.get(3);
    Assert.assertEquals(_get_3, "C");
  }
  
  @Test
  public void test_PFunction() {
    EClass type = this.vcmlPackage.getPFunction();
    String _name = type.getName();
    List<String> result = VClipseStrings.splitCamelCase(_name);
    String _join = IterableExtensions.join(result, " ");
    int _size = result.size();
    Assert.assertEquals(_join, 2, _size);
    String _get = result.get(0);
    Assert.assertEquals(_get, "P");
    String _get_1 = result.get(1);
    Assert.assertEquals(_get_1, "Function");
  }
  
  @Test
  public void test_BinaryCondition() {
    EClass type = this.vcmlPackage.getBinaryCondition();
    String _name = type.getName();
    List<String> result = VClipseStrings.splitCamelCase(_name);
    String _join = IterableExtensions.join(result, " ");
    int _size = result.size();
    Assert.assertEquals(_join, 2, _size);
    String _get = result.get(0);
    Assert.assertEquals(_get, "Binary");
    String _get_1 = result.get(1);
    Assert.assertEquals(_get_1, "Condition");
  }
}
