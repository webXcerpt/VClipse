package org.vclipse.vcml;

import com.google.common.base.Objects;
import java.util.ArrayList;
import java.util.HashMap;
import org.eclipse.xtext.xbase.lib.CollectionLiterals;
import org.vclipse.vcml.vcml.CharacteristicOrValueDependencies;
import org.vclipse.vcml.vcml.CharacteristicValue;
import org.vclipse.vcml.vcml.SimpleDescription;
import org.vclipse.vcml.vcml.SimpleDocumentation;
import org.vclipse.vcml.vcml.VcmlFactory;
import org.vclipse.vcml.vcml.VcmlPackage;

/**
 * Extensions for {@link org.vclipse.vcml.vcml.VcmlFactory}.
 * 
 * Some of them create cached instances and other a new instance on each call.
 */
@SuppressWarnings("all")
public class VCMLFactoryExtension {
  public VcmlFactory VCML_FACTORY;
  
  public VcmlPackage VCML_PACKAGE;
  
  public VCMLFactoryExtension() {
    this.VCML_FACTORY = VcmlFactory.eINSTANCE;
    this.VCML_PACKAGE = VcmlPackage.eINSTANCE;
  }
  
  public CharacteristicValue newCharacteristicValue(final String name) {
    CharacteristicValue _characteristicValue = this.characteristicValue(name, null, null, false);
    return _characteristicValue;
  }
  
  public CharacteristicValue characteristicValue(final String name, final String description, final String documentation, final boolean _default) {
    final ArrayList<?>_cacheKey = CollectionLiterals.newArrayList(name, description, documentation, _default);
    final CharacteristicValue _result;
    synchronized (_createCache_characteristicValue) {
      if (_createCache_characteristicValue.containsKey(_cacheKey)) {
        return _createCache_characteristicValue.get(_cacheKey);
      }
      CharacteristicValue _createCharacteristicValue = VcmlFactory.eINSTANCE.createCharacteristicValue();
      _result = _createCharacteristicValue;
      _createCache_characteristicValue.put(_cacheKey, _result);
    }
    _init_characteristicValue(_result, name, description, documentation, _default);
    return _result;
  }
  
  private final HashMap<ArrayList<? extends Object>,CharacteristicValue> _createCache_characteristicValue = CollectionLiterals.newHashMap();
  
  private void _init_characteristicValue(final CharacteristicValue it, final String name, final String description, final String documentation, final boolean _default) {
    it.setName(name);
    boolean _isDefault = it.isDefault();
    it.setDefault(_isDefault);
    boolean _notEquals = (!Objects.equal(description, null));
    if (_notEquals) {
      SimpleDescription _newSimpleDescription = this.newSimpleDescription(description);
      it.setDescription(_newSimpleDescription);
    }
    boolean _notEquals_1 = (!Objects.equal(documentation, null));
    if (_notEquals_1) {
      SimpleDocumentation _newSimpleDocumentation = this.newSimpleDocumentation(documentation);
      it.setDocumentation(_newSimpleDocumentation);
    }
    CharacteristicOrValueDependencies _createCharacteristicOrValueDependencies = this.VCML_FACTORY.createCharacteristicOrValueDependencies();
    it.setDependencies(_createCharacteristicOrValueDependencies);
  }
  
  public SimpleDescription newSimpleDescription(final String description) {
    final SimpleDescription simpleDescription = this.VCML_FACTORY.createSimpleDescription();
    simpleDescription.setValue(description);
    return simpleDescription;
  }
  
  public SimpleDocumentation newSimpleDocumentation(final String documentation) {
    final SimpleDocumentation simpleDocumentation = this.VCML_FACTORY.createSimpleDocumentation();
    simpleDocumentation.setValue(documentation);
    return simpleDocumentation;
  }
}
