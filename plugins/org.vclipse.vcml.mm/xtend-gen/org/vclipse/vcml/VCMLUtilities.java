package org.vclipse.vcml;

import com.google.common.base.Objects;
import com.google.common.base.Predicate;
import com.google.common.base.Strings;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.inject.Inject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.vclipse.base.naming.INameProvider;
import org.vclipse.vcml.SAPFormattingUtility;
import org.vclipse.vcml.VCMLFactoryExtension;
import org.vclipse.vcml.vcml.Characteristic;
import org.vclipse.vcml.vcml.CharacteristicOrValueDependencies;
import org.vclipse.vcml.vcml.CharacteristicType;
import org.vclipse.vcml.vcml.CharacteristicValue;
import org.vclipse.vcml.vcml.DateCharacteristicValue;
import org.vclipse.vcml.vcml.DateType;
import org.vclipse.vcml.vcml.Dependency;
import org.vclipse.vcml.vcml.NumericCharacteristicValue;
import org.vclipse.vcml.vcml.NumericType;
import org.vclipse.vcml.vcml.Option;
import org.vclipse.vcml.vcml.OptionType;
import org.vclipse.vcml.vcml.SymbolicType;

/**
 * Utilities for VCML Objects.
 */
@SuppressWarnings("all")
public class VCMLUtilities {
  @Inject
  private SAPFormattingUtility sapFormattingUtility;
  
  @Inject
  private VCMLFactoryExtension factoryExtension;
  
  /**
   * Returns an option with requested type, null if such an option does not exist as an entry.
   */
  public Option getOption(final List<Option> global, final List<Option> local, final OptionType type) {
    Iterable<Option> _plus = Iterables.<Option>concat(global, local);
    for (final Option option : _plus) {
      OptionType _name = option.getName();
      boolean _equals = Objects.equal(_name, type);
      if (_equals) {
        return option;
      }
    }
    return null;
  }
  
  /**
   * Sorts a list with a comparator.
   */
  public <T extends EObject> void sortEntries(final List<T> entries, final Comparator<T> comparator) {
    final ArrayList<T> entriesCopy = Lists.<T>newArrayList(entries);
    Collections.<T>sort(entriesCopy, comparator);
    entries.clear();
    entries.addAll(entriesCopy);
    return;
  }
  
  /**
   * Searches for an entry with a given type and name in entries.
   * Returns the first match, null if there is no match.
   */
  public <T extends EObject> T findEntry(final String name, final EClass type, final Iterable<T> entries, final INameProvider nameProvider) {
    boolean _equals = Objects.equal(nameProvider, null);
    if (_equals) {
      return null;
    }
    final Iterator<T> iterator = entries.iterator();
    boolean _hasNext = iterator.hasNext();
    if (_hasNext) {
      final Function1<T,Boolean> _function = new Function1<T,Boolean>() {
          public Boolean apply(final T entry) {
            final String entryName = nameProvider.getName(entry);
            boolean _isNullOrEmpty = Strings.isNullOrEmpty(entryName);
            if (_isNullOrEmpty) {
              return false;
            }
            boolean _and = false;
            boolean _equals = entryName.equals(name);
            if (!_equals) {
              _and = false;
            } else {
              EClass _eClass = entry.eClass();
              boolean _equals_1 = Objects.equal(_eClass, type);
              _and = (_equals && _equals_1);
            }
            return _and;
          }
        };
      Iterable<T> _filter = Iterables.<T>filter(entries, new Predicate<T>() {
          public boolean apply(T input) {
            return _function.apply(input);
          }
      });
      final Iterator<T> typedAndNamed = _filter.iterator();
      boolean _hasNext_1 = typedAndNamed.hasNext();
      if (_hasNext_1) {
        return typedAndNamed.next();
      }
    }
    return null;
  }
  
  /**
   * Dispatcher methods for returning dependencies of an object, they are created in the case they haven't been existed.
   */
  protected EList<Dependency> _getDependencies(final Characteristic cstic) {
    CharacteristicOrValueDependencies _dependencies = cstic.getDependencies();
    boolean _equals = Objects.equal(_dependencies, null);
    if (_equals) {
      CharacteristicOrValueDependencies _createCharacteristicOrValueDependencies = this.factoryExtension.VCML_FACTORY.createCharacteristicOrValueDependencies();
      cstic.setDependencies(_createCharacteristicOrValueDependencies);
    }
    CharacteristicOrValueDependencies _dependencies_1 = cstic.getDependencies();
    return _dependencies_1.getDependencies();
  }
  
  protected EList<Dependency> _getDependencies(final CharacteristicValue value) {
    CharacteristicOrValueDependencies _dependencies = value.getDependencies();
    boolean _equals = Objects.equal(_dependencies, null);
    if (_equals) {
      CharacteristicOrValueDependencies _createCharacteristicOrValueDependencies = this.factoryExtension.VCML_FACTORY.createCharacteristicOrValueDependencies();
      value.setDependencies(_createCharacteristicOrValueDependencies);
    }
    CharacteristicOrValueDependencies _dependencies_1 = value.getDependencies();
    return _dependencies_1.getDependencies();
  }
  
  protected EList<Dependency> _getDependencies(final NumericCharacteristicValue value) {
    CharacteristicOrValueDependencies _dependencies = value.getDependencies();
    boolean _equals = Objects.equal(_dependencies, null);
    if (_equals) {
      CharacteristicOrValueDependencies _createCharacteristicOrValueDependencies = this.factoryExtension.VCML_FACTORY.createCharacteristicOrValueDependencies();
      value.setDependencies(_createCharacteristicOrValueDependencies);
    }
    CharacteristicOrValueDependencies _dependencies_1 = value.getDependencies();
    return _dependencies_1.getDependencies();
  }
  
  protected EList<Dependency> _getDependencies(final DateCharacteristicValue value) {
    CharacteristicOrValueDependencies _dependencies = value.getDependencies();
    boolean _equals = Objects.equal(_dependencies, null);
    if (_equals) {
      CharacteristicOrValueDependencies _createCharacteristicOrValueDependencies = this.factoryExtension.VCML_FACTORY.createCharacteristicOrValueDependencies();
      value.setDependencies(_createCharacteristicOrValueDependencies);
    }
    CharacteristicOrValueDependencies _dependencies_1 = value.getDependencies();
    return _dependencies_1.getDependencies();
  }
  
  /**
   * Dispatcher methods for returning name to value mapping.
   */
  protected Map<String,EObject> _getNameToValue(final SymbolicType type) {
    final HashMap<String,EObject> name2Value = Maps.<String, EObject>newHashMap();
    EList<CharacteristicValue> _values = ((SymbolicType) type).getValues();
    for (final CharacteristicValue value : _values) {
      String _name = value.getName();
      name2Value.put(_name, value);
    }
    return name2Value;
  }
  
  protected Map<String,EObject> _getNameToValue(final NumericType type) {
    final HashMap<String,EObject> name2Value = Maps.<String, EObject>newHashMap();
    EList<NumericCharacteristicValue> _values = ((NumericType) type).getValues();
    for (final NumericCharacteristicValue value : _values) {
      {
        final String string = this.sapFormattingUtility.toString(value);
        boolean _equals = Objects.equal(string, null);
        if (_equals) {
          IllegalArgumentException _illegalArgumentException = new IllegalArgumentException("Result of the computation should not be null.");
          throw _illegalArgumentException;
        }
        name2Value.put(string, value);
      }
    }
    return name2Value;
  }
  
  protected Map<String,EObject> _getNameToValue(final DateType type) {
    final HashMap<String,EObject> name2Value = Maps.<String, EObject>newHashMap();
    EList<DateCharacteristicValue> _values = type.getValues();
    for (final DateCharacteristicValue value : _values) {
      {
        final String string = this.sapFormattingUtility.toString(value);
        boolean _equals = Objects.equal(string, null);
        if (_equals) {
          IllegalArgumentException _illegalArgumentException = new IllegalArgumentException("Result of the computation should not be null.");
          throw _illegalArgumentException;
        }
        name2Value.put(string, value);
      }
    }
    return name2Value;
  }
  
  /**
   * Method for checking a feature of an eobject on equality with exival.
   */
  public boolean check(final EObject eobject, final EStructuralFeature feature, final Object exival) {
    EClass _eClass = eobject.eClass();
    EList<EStructuralFeature> _eAllStructuralFeatures = _eClass.getEAllStructuralFeatures();
    boolean _contains = _eAllStructuralFeatures.contains(feature);
    if (_contains) {
      final Object value = eobject.eGet(feature);
      if ((value instanceof EObject)) {
        return EcoreUtil2.equals(((EObject) value), ((EObject) exival));
      }
      if ((value instanceof EList<?>)) {
        return EcoreUtil2.equals(((EList) value), ((EList) exival));
      }
      boolean _and = false;
      boolean _equals = Objects.equal(value, null);
      if (!_equals) {
        _and = false;
      } else {
        boolean _equals_1 = Objects.equal(exival, null);
        _and = (_equals && _equals_1);
      }
      if (_and) {
        return true;
      }
    }
    return false;
  }
  
  public EList<Dependency> getDependencies(final EObject cstic) {
    if (cstic instanceof Characteristic) {
      return _getDependencies((Characteristic)cstic);
    } else if (cstic instanceof CharacteristicValue) {
      return _getDependencies((CharacteristicValue)cstic);
    } else if (cstic instanceof DateCharacteristicValue) {
      return _getDependencies((DateCharacteristicValue)cstic);
    } else if (cstic instanceof NumericCharacteristicValue) {
      return _getDependencies((NumericCharacteristicValue)cstic);
    } else {
      throw new IllegalArgumentException("Unhandled parameter types: " +
        Arrays.<Object>asList(cstic).toString());
    }
  }
  
  public Map<String,EObject> getNameToValue(final CharacteristicType type) {
    if (type instanceof DateType) {
      return _getNameToValue((DateType)type);
    } else if (type instanceof NumericType) {
      return _getNameToValue((NumericType)type);
    } else if (type instanceof SymbolicType) {
      return _getNameToValue((SymbolicType)type);
    } else {
      throw new IllegalArgumentException("Unhandled parameter types: " +
        Arrays.<Object>asList(type).toString());
    }
  }
}
