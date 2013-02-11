package org.vclipse.tests.compare;

import com.google.common.base.Objects;
import com.google.inject.Inject;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.junit4.InjectWith;
import org.eclipse.xtext.junit4.XtextRunner;
import org.eclipse.xtext.xbase.lib.Exceptions;
import org.eclipselabs.xtext.utils.unittesting.XtextTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.vclipse.refactoring.utils.EntrySearch;
import org.vclipse.tests.VClipseTestUtilities;
import org.vclipse.tests.compare.VCMLCompareInjectorProvider;
import org.vclipse.vcml.compare.VCMLCompareOperation;
import org.vclipse.vcml.vcml.Characteristic;
import org.vclipse.vcml.vcml.CharacteristicType;
import org.vclipse.vcml.vcml.NumericType;
import org.vclipse.vcml.vcml.SymbolicType;
import org.vclipse.vcml.vcml.VCObject;
import org.vclipse.vcml.vcml.VcmlFactory;
import org.vclipse.vcml.vcml.VcmlModel;
import org.vclipse.vcml.vcml.VcmlPackage;

@RunWith(value = XtextRunner.class)
@InjectWith(value = VCMLCompareInjectorProvider.class)
@SuppressWarnings("all")
public class VCMLCompareTests extends XtextTest {
  @Inject
  private VCMLCompareOperation compare;
  
  @Inject
  private VClipseTestUtilities resources;
  
  @Inject
  private EntrySearch entrySearch;
  
  private NullProgressMonitor monitor;
  
  private VcmlFactory vcmlFactory;
  
  private VcmlPackage vcmlPackage;
  
  /**
   * Following tests are to execute as JUnit Plug-in tests
   */
  public void before() {
    super.before();
    NullProgressMonitor _nullProgressMonitor = new NullProgressMonitor();
    this.monitor = _nullProgressMonitor;
    this.vcmlFactory = VcmlFactory.eINSTANCE;
    this.vcmlPackage = VcmlPackage.eINSTANCE;
  }
  
  /**
   * if new vc objects are added to a vcml model, they should be extracted
   * during the compare operation.
   */
  @Test
  public void testAddingVCObjects() {
    try {
      final EObject vcml = this.resources.getResourceRoot("/compare/added_vc_objects/VCML/engine.vcml");
      final EObject sap = this.resources.getResourceRoot("/compare/added_vc_objects/SAP/engine.vcml");
      final VcmlModel result = this.vcmlFactory.createVcmlModel();
      this.compare.compare(((VcmlModel) sap), ((VcmlModel) vcml), result, this.monitor);
      EList<VCObject> _objects = result.getObjects();
      boolean _isEmpty = _objects.isEmpty();
      Assert.assertFalse(_isEmpty);
      boolean _reportedProblems = this.compare.reportedProblems();
      Assert.assertFalse(_reportedProblems);
    } catch (Exception _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  /**
   * type changes for existing characteristics are not allowed, the changed object
   * is extracted and a marker is created for the characteristic type
   */
  @Test
  public void testChangedCsticType() {
    try {
      final EObject vcml = this.resources.getResourceRoot("/compare/changed_cstic_type/VCML/car.vcml");
      final EObject sap = this.resources.getResourceRoot("/compare/changed_cstic_type/SAP/car.vcml");
      VcmlModel result = this.vcmlFactory.createVcmlModel();
      this.compare.compare(((VcmlModel) sap), ((VcmlModel) vcml), result, this.monitor);
      EList<VCObject> _objects = result.getObjects();
      boolean _isEmpty = _objects.isEmpty();
      Assert.assertFalse(_isEmpty);
      boolean _reportedProblems = this.compare.reportedProblems();
      Assert.assertTrue(_reportedProblems);
      EClass _characteristic = this.vcmlPackage.getCharacteristic();
      EList<VCObject> _objects_1 = result.getObjects();
      VCObject entry = this.entrySearch.<VCObject>findEntry("NAME", _characteristic, _objects_1);
      boolean _equals = Objects.equal(entry, null);
      Assert.assertFalse(_equals);
      CharacteristicType _type = ((Characteristic) entry).getType();
      Assert.assertTrue((_type instanceof NumericType));
      EList<VCObject> _objects_2 = result.getObjects();
      int _size = _objects_2.size();
      boolean _equals_1 = (_size == 1);
      Assert.assertTrue(_equals_1);
      VcmlModel _createVcmlModel = this.vcmlFactory.createVcmlModel();
      result = _createVcmlModel;
      this.compare.compare(((VcmlModel) vcml), ((VcmlModel) sap), result, this.monitor);
      EList<VCObject> _objects_3 = result.getObjects();
      boolean _isEmpty_1 = _objects_3.isEmpty();
      Assert.assertFalse(_isEmpty_1);
      boolean _reportedProblems_1 = this.compare.reportedProblems();
      Assert.assertTrue(_reportedProblems_1);
      EClass _characteristic_1 = this.vcmlPackage.getCharacteristic();
      EList<VCObject> _objects_4 = result.getObjects();
      VCObject _findEntry = this.entrySearch.<VCObject>findEntry("NAME", _characteristic_1, _objects_4);
      entry = _findEntry;
      boolean _equals_2 = Objects.equal(entry, null);
      Assert.assertFalse(_equals_2);
      EClass _eClass = entry.eClass();
      String _name = _eClass.getName();
      String _plus = ("object: " + _name);
      CharacteristicType _type_1 = ((Characteristic) entry).getType();
      Assert.assertTrue(_plus, (_type_1 instanceof SymbolicType));
      EList<VCObject> _objects_5 = result.getObjects();
      int _size_1 = _objects_5.size();
      boolean _equals_3 = (_size_1 == 1);
      Assert.assertTrue(_equals_3);
    } catch (Exception _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
}
