package org.vclipse.vcml.utils;

import com.google.common.collect.Iterables;
import com.google.inject.Inject;
import java.util.ArrayList;
import java.util.HashMap;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.conversion.IValueConverter;
import org.eclipse.xtext.xbase.lib.CollectionLiterals;
import org.vclipse.vcml.conversion.VCMLValueConverter;
import org.vclipse.vcml.utils.VCMLObjectUtils;
import org.vclipse.vcml.vcml.BOMItem;
import org.vclipse.vcml.vcml.BillOfMaterial;
import org.vclipse.vcml.vcml.Characteristic;
import org.vclipse.vcml.vcml.CharacteristicType;
import org.vclipse.vcml.vcml.ConfigurationProfileEntry;
import org.vclipse.vcml.vcml.Material;
import org.vclipse.vcml.vcml.NumericType;
import org.vclipse.vcml.vcml.SelectionCondition;
import org.vclipse.vcml.vcml.SimpleDescription;
import org.vclipse.vcml.vcml.SymbolicType;
import org.vclipse.vcml.vcml.VcmlFactory;
import org.vclipse.vcml.vcml.VcmlPackage;

@SuppressWarnings("all")
public class CreateVcmlObjects extends VCMLObjectUtils {
  protected static VcmlFactory VCML_FACTORY = VcmlFactory.eINSTANCE;
  
  protected static VcmlPackage VCML_PACKAGE = VcmlPackage.eINSTANCE;
  
  @Inject
  private VCMLValueConverter valueConverter;
  
  public org.vclipse.vcml.vcml.Class newConfigurableClass(final String name, final String description) {
    String _plus = ("(300)" + name);
    org.vclipse.vcml.vcml.Class _newClass = this.newClass(_plus, description);
    return _newClass;
  }
  
  public Material newMaterial(final String name, final String description, final String type) {
    final ArrayList<?>_cacheKey = CollectionLiterals.newArrayList(name, description, type);
    final Material _result;
    synchronized (_createCache_newMaterial) {
      if (_createCache_newMaterial.containsKey(_cacheKey)) {
        return _createCache_newMaterial.get(_cacheKey);
      }
      Material _createMaterial = VcmlFactory.eINSTANCE.createMaterial();
      _result = _createMaterial;
      _createCache_newMaterial.put(_cacheKey, _result);
    }
    _init_newMaterial(_result, name, description, type);
    return _result;
  }
  
  private final HashMap<ArrayList<? extends Object>,Material> _createCache_newMaterial = CollectionLiterals.newHashMap();
  
  private void _init_newMaterial(final Material it, final String name, final String description, final String type) {
    it.setName(name);
    SimpleDescription _mkSimpleDescription = VCMLObjectUtils.mkSimpleDescription(description);
    it.setDescription(_mkSimpleDescription);
    String _extendedIDString = this.getExtendedIDString(type);
    it.setType(_extendedIDString);
  }
  
  public org.vclipse.vcml.vcml.Class newClass(final String name, final String description) {
    final ArrayList<?>_cacheKey = CollectionLiterals.newArrayList(name, description);
    final org.vclipse.vcml.vcml.Class _result;
    synchronized (_createCache_newClass) {
      if (_createCache_newClass.containsKey(_cacheKey)) {
        return _createCache_newClass.get(_cacheKey);
      }
      org.vclipse.vcml.vcml.Class _createClass = VcmlFactory.eINSTANCE.createClass();
      _result = _createClass;
      _createCache_newClass.put(_cacheKey, _result);
    }
    _init_newClass(_result, name, description);
    return _result;
  }
  
  private final HashMap<ArrayList<? extends Object>,org.vclipse.vcml.vcml.Class> _createCache_newClass = CollectionLiterals.newHashMap();
  
  private void _init_newClass(final org.vclipse.vcml.vcml.Class it, final String name, final String description) {
    it.setName(name);
    SimpleDescription _mkSimpleDescription = VCMLObjectUtils.mkSimpleDescription(description);
    it.setDescription(_mkSimpleDescription);
  }
  
  public BillOfMaterial newBom(final Material material, final String description) {
    final ArrayList<?>_cacheKey = CollectionLiterals.newArrayList(material, description);
    final BillOfMaterial _result;
    synchronized (_createCache_newBom) {
      if (_createCache_newBom.containsKey(_cacheKey)) {
        return _createCache_newBom.get(_cacheKey);
      }
      BillOfMaterial _createBillOfMaterial = VcmlFactory.eINSTANCE.createBillOfMaterial();
      _result = _createBillOfMaterial;
      _createCache_newBom.put(_cacheKey, _result);
    }
    _init_newBom(_result, material, description);
    return _result;
  }
  
  private final HashMap<ArrayList<? extends Object>,BillOfMaterial> _createCache_newBom = CollectionLiterals.newHashMap();
  
  private void _init_newBom(final BillOfMaterial it, final Material material, final String description) {
    String _name = material.getName();
    it.setName(_name);
    it.setMaterial(material);
    SimpleDescription _mkSimpleDescription = VCMLObjectUtils.mkSimpleDescription(description);
    it.setDescription(_mkSimpleDescription);
  }
  
  public BOMItem newBOMItem(final int number, final Material material) {
    final ArrayList<?>_cacheKey = CollectionLiterals.newArrayList(number, material);
    final BOMItem _result;
    synchronized (_createCache_newBOMItem) {
      if (_createCache_newBOMItem.containsKey(_cacheKey)) {
        return _createCache_newBOMItem.get(_cacheKey);
      }
      BOMItem _createBOMItem = VcmlFactory.eINSTANCE.createBOMItem();
      _result = _createBOMItem;
      _createCache_newBOMItem.put(_cacheKey, _result);
    }
    _init_newBOMItem(_result, number, material);
    return _result;
  }
  
  private final HashMap<ArrayList<? extends Object>,BOMItem> _createCache_newBOMItem = CollectionLiterals.newHashMap();
  
  private void _init_newBOMItem(final BOMItem it, final int number, final Material material) {
    it.setMaterial(material);
    it.setItemnumber(number);
  }
  
  public BOMItem newBOMItem(final int number, final Material material, final SelectionCondition condition, final Iterable<ConfigurationProfileEntry> entries) {
    final ArrayList<?>_cacheKey = CollectionLiterals.newArrayList(number, material, condition, entries);
    final BOMItem _result;
    synchronized (_createCache_newBOMItem_1) {
      if (_createCache_newBOMItem_1.containsKey(_cacheKey)) {
        return _createCache_newBOMItem_1.get(_cacheKey);
      }
      BOMItem _createBOMItem = VcmlFactory.eINSTANCE.createBOMItem();
      _result = _createBOMItem;
      _createCache_newBOMItem_1.put(_cacheKey, _result);
    }
    _init_newBOMItem(_result, number, material, condition, entries);
    return _result;
  }
  
  private final HashMap<ArrayList<? extends Object>,BOMItem> _createCache_newBOMItem_1 = CollectionLiterals.newHashMap();
  
  private void _init_newBOMItem(final BOMItem it, final int number, final Material material, final SelectionCondition condition, final Iterable<ConfigurationProfileEntry> entries) {
    final BOMItem newBomItem = this.newBOMItem(number, material);
    newBomItem.setSelectionCondition(condition);
    EList<ConfigurationProfileEntry> _entries = newBomItem.getEntries();
    Iterables.<ConfigurationProfileEntry>addAll(_entries, entries);
  }
  
  public Characteristic newNumericCharacteristic(final String name, final String description) {
    final ArrayList<?>_cacheKey = CollectionLiterals.newArrayList(name, description);
    final Characteristic _result;
    synchronized (_createCache_newNumericCharacteristic) {
      if (_createCache_newNumericCharacteristic.containsKey(_cacheKey)) {
        return _createCache_newNumericCharacteristic.get(_cacheKey);
      }
      Characteristic _createCharacteristic = VcmlFactory.eINSTANCE.createCharacteristic();
      _result = _createCharacteristic;
      _createCache_newNumericCharacteristic.put(_cacheKey, _result);
    }
    _init_newNumericCharacteristic(_result, name, description);
    return _result;
  }
  
  private final HashMap<ArrayList<? extends Object>,Characteristic> _createCache_newNumericCharacteristic = CollectionLiterals.newHashMap();
  
  private void _init_newNumericCharacteristic(final Characteristic it, final String name, final String description) {
    it.setName(name);
    SimpleDescription _mkSimpleDescription = VCMLObjectUtils.mkSimpleDescription(description);
    it.setDescription(_mkSimpleDescription);
    NumericType _newNumericTypeInstance = this.newNumericTypeInstance();
    it.setType(_newNumericTypeInstance);
  }
  
  public Characteristic newSymbolicCharacteristic(final String name, final String description) {
    final ArrayList<?>_cacheKey = CollectionLiterals.newArrayList(name, description);
    final Characteristic _result;
    synchronized (_createCache_newSymbolicCharacteristic) {
      if (_createCache_newSymbolicCharacteristic.containsKey(_cacheKey)) {
        return _createCache_newSymbolicCharacteristic.get(_cacheKey);
      }
      Characteristic _createCharacteristic = VcmlFactory.eINSTANCE.createCharacteristic();
      _result = _createCharacteristic;
      _createCache_newSymbolicCharacteristic.put(_cacheKey, _result);
    }
    _init_newSymbolicCharacteristic(_result, name, description);
    return _result;
  }
  
  private final HashMap<ArrayList<? extends Object>,Characteristic> _createCache_newSymbolicCharacteristic = CollectionLiterals.newHashMap();
  
  private void _init_newSymbolicCharacteristic(final Characteristic it, final String name, final String description) {
    it.setName(name);
    SimpleDescription _mkSimpleDescription = VCMLObjectUtils.mkSimpleDescription(description);
    it.setDescription(_mkSimpleDescription);
    CharacteristicType _newSymbolicTypeInstance = this.newSymbolicTypeInstance();
    it.setType(_newSymbolicTypeInstance);
  }
  
  public CharacteristicType newSymbolicTypeInstance() {
    SymbolicType _xblockexpression = null;
    {
      final SymbolicType symbolicType = CreateVcmlObjects.VCML_FACTORY.createSymbolicType();
      symbolicType.setNumberOfChars(30);
      symbolicType.setCaseSensitive(true);
      _xblockexpression = (symbolicType);
    }
    return _xblockexpression;
  }
  
  public NumericType newNumericTypeInstance() {
    NumericType _xblockexpression = null;
    {
      final NumericType numericType = CreateVcmlObjects.VCML_FACTORY.createNumericType();
      numericType.setNumberOfChars(15);
      numericType.setDecimalPlaces(3);
      numericType.setNegativeValuesAllowed(true);
      _xblockexpression = (numericType);
    }
    return _xblockexpression;
  }
  
  public String genVCNamePrefix(final Resource resource) {
    URI _uRI = resource.getURI();
    URI _trimFileExtension = _uRI.trimFileExtension();
    String _lastSegment = _trimFileExtension.lastSegment();
    String _plus = (_lastSegment + "_");
    return _plus;
  }
  
  public String getExtendedIDString(final String type) {
    IValueConverter<String> _EXTENDED_ID = this.valueConverter.EXTENDED_ID();
    String _string = _EXTENDED_ID.toString(type);
    return _string;
  }
}
