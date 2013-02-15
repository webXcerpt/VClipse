package org.vclipse.tests.vcml;

import com.google.inject.Inject;
import java.util.Comparator;
import junit.framework.Assert;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.junit4.InjectWith;
import org.eclipse.xtext.junit4.XtextRunner;
import org.eclipse.xtext.serializer.ISerializer;
import org.eclipse.xtext.xbase.lib.Functions.Function0;
import org.eclipse.xtext.xbase.lib.Functions.Function2;
import org.eclipselabs.xtext.utils.unittesting.XtextTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.vclipse.base.naming.INameProvider;
import org.vclipse.tests.VClipseTestPlugin;
import org.vclipse.tests.VClipseTestUtilities;
import org.vclipse.vcml.VCMLUtilities;
import org.vclipse.vcml.vcml.VCObject;
import org.vclipse.vcml.vcml.VcmlModel;
import org.vclipse.vcml.vcml.VcmlPackage;

@RunWith(value = XtextRunner.class)
@InjectWith(value = VClipseTestPlugin.class)
@SuppressWarnings("all")
public class VCMLUtilityTests extends XtextTest {
  @Inject
  private VClipseTestUtilities testUtilities;
  
  @Inject
  private VCMLUtilities vcmlUtility;
  
  @Inject
  private ISerializer vcmlSerializer;
  
  @Inject
  private INameProvider nameProvider;
  
  private VcmlPackage vcmlPackage = VcmlPackage.eINSTANCE;
  
  public VCMLUtilityTests() {
    super(
      new Function0<String>() {
        public String apply() {
          String _simpleName = VCMLUtilityTests.class.getSimpleName();
          return _simpleName;
        }
      }.apply());
  }
  
  @Test
  public void test_SortVCObjectsList() {
    final Resource car_vcml = this.testUtilities.getResource("/compare/added_vc_objects/VCML/car.vcml");
    EList<EObject> _contents = car_vcml.getContents();
    EObject _get = _contents.get(0);
    final VcmlModel vcml_model = ((VcmlModel) _get);
    String _serialize = this.vcmlSerializer.serialize(vcml_model);
    final String contens_prior_sort = this.testUtilities.removeNoise(_serialize);
    EList<VCObject> _objects = vcml_model.getObjects();
    final Function2<VCObject,VCObject,Integer> _function = new Function2<VCObject,VCObject,Integer>() {
        public Integer apply(final VCObject first, final VCObject second) {
          String _name = first.getName();
          String _name_1 = second.getName();
          int _compareTo = _name.compareTo(_name_1);
          return _compareTo;
        }
      };
    this.vcmlUtility.<VCObject>sortEntries(_objects, new Comparator<VCObject>() {
        public int compare(VCObject o1,VCObject o2) {
          return _function.apply(o1,o2);
        }
    });
    String _serialize_1 = this.vcmlSerializer.serialize(vcml_model);
    final String contents_after_sort = this.testUtilities.removeNoise(_serialize_1);
    boolean _equals = contens_prior_sort.equals(contents_after_sort);
    Assert.assertFalse("Sort algorithm does not have an effect.", _equals);
  }
  
  @Test
  public void test_FindEntries() {
    final Resource car_vcml = this.testUtilities.getResource("/compare/added_vc_objects/VCML/car.vcml");
    EList<EObject> _contents = car_vcml.getContents();
    EObject _get = _contents.get(0);
    final VcmlModel vcml_model = ((VcmlModel) _get);
    EClass _class_ = this.vcmlPackage.getClass_();
    EList<VCObject> _objects = vcml_model.getObjects();
    VCObject found = this.vcmlUtility.<VCObject>findEntry("(300)CAR", _class_, _objects, this.nameProvider);
    Assert.assertNotNull("Entry is existing an was found.", found);
    EClass _characteristic = this.vcmlPackage.getCharacteristic();
    EList<VCObject> _objects_1 = vcml_model.getObjects();
    VCObject _findEntry = this.vcmlUtility.<VCObject>findEntry("NAME", _characteristic, _objects_1, this.nameProvider);
    found = _findEntry;
    Assert.assertNotNull("Entry is existing an was found.", found);
    EClass _characteristic_1 = this.vcmlPackage.getCharacteristic();
    EList<VCObject> _objects_2 = vcml_model.getObjects();
    VCObject _findEntry_1 = this.vcmlUtility.<VCObject>findEntry("NAME2", _characteristic_1, _objects_2, this.nameProvider);
    found = _findEntry_1;
    Assert.assertNull("Entry does not exist.", found);
    EClass _constraint = this.vcmlPackage.getConstraint();
    EList<VCObject> _objects_3 = vcml_model.getObjects();
    VCObject _findEntry_2 = this.vcmlUtility.<VCObject>findEntry("NAME", _constraint, _objects_3, this.nameProvider);
    found = _findEntry_2;
    Assert.assertNull("Entry does not exist.", found);
  }
}
