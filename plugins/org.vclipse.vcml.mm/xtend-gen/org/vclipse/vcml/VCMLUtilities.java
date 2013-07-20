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
package org.vclipse.vcml;

import com.google.common.base.Objects;
import com.google.common.base.Predicate;
import com.google.common.base.Strings;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.inject.Inject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.xtext.EcoreUtil2;
import org.vclipse.base.naming.INameProvider;
import org.vclipse.vcml.SAPFormattingUtility;
import org.vclipse.vcml.VCMLFactoryExtension;

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
  public /* Option */Object getOption(final /* List<Option> */Object global, final /* List<Option> */Object local, final /* OptionType */Object type) {
    throw new Error("Unresolved compilation problems:"
      + "\nname cannot be resolved"
      + "\n== cannot be resolved");
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
      final Predicate<T> _function = new Predicate<T>() {
          public boolean apply(final T entry) {
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
      Iterable<T> _filter = Iterables.<T>filter(entries, _function);
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
  protected Object _getDependencies(final /* Characteristic */Object cstic) {
    throw new Error("Unresolved compilation problems:"
      + "\ndependencies cannot be resolved"
      + "\n== cannot be resolved"
      + "\ndependencies cannot be resolved"
      + "\ncreateCharacteristicOrValueDependencies cannot be resolved"
      + "\ndependencies cannot be resolved"
      + "\ndependencies cannot be resolved");
  }
  
  protected Object _getDependencies(final /* CharacteristicValue */Object value) {
    throw new Error("Unresolved compilation problems:"
      + "\ndependencies cannot be resolved"
      + "\n== cannot be resolved"
      + "\ndependencies cannot be resolved"
      + "\ncreateCharacteristicOrValueDependencies cannot be resolved"
      + "\ndependencies cannot be resolved"
      + "\ndependencies cannot be resolved");
  }
  
  protected Object _getDependencies(final /* NumericCharacteristicValue */Object value) {
    throw new Error("Unresolved compilation problems:"
      + "\ndependencies cannot be resolved"
      + "\n== cannot be resolved"
      + "\ndependencies cannot be resolved"
      + "\ncreateCharacteristicOrValueDependencies cannot be resolved"
      + "\ndependencies cannot be resolved"
      + "\ndependencies cannot be resolved");
  }
  
  protected Object _getDependencies(final /* DateCharacteristicValue */Object value) {
    throw new Error("Unresolved compilation problems:"
      + "\ndependencies cannot be resolved"
      + "\n== cannot be resolved"
      + "\ndependencies cannot be resolved"
      + "\ncreateCharacteristicOrValueDependencies cannot be resolved"
      + "\ndependencies cannot be resolved"
      + "\ndependencies cannot be resolved");
  }
  
  /**
   * Dispatcher methods for returning name to value mapping.
   */
  protected Map<String,EObject> _getNameToValue(final /* SymbolicType */Object type) {
    throw new Error("Unresolved compilation problems:"
      + "\nSymbolicType cannot be resolved to a type."
      + "\nThe method name is undefined for the type VCMLUtilities"
      + "\nvalues cannot be resolved");
  }
  
  protected Map<String,EObject> _getNameToValue(final /* NumericType */Object type) {
    throw new Error("Unresolved compilation problems:"
      + "\nNumericType cannot be resolved to a type."
      + "\nvalues cannot be resolved");
  }
  
  protected Map<String,EObject> _getNameToValue(final /* DateType */Object type) {
    throw new Error("Unresolved compilation problems:"
      + "\nvalues cannot be resolved");
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
  
  public Object getDependencies(final Characteristic cstic) {
    if (cstic != null) {
      return _getDependencies(cstic);
    } else {
      throw new IllegalArgumentException("Unhandled parameter types: " +
        Arrays.<Object>asList(cstic).toString());
    }
  }
  
  public Map<String,EObject> getNameToValue(final SymbolicType type) {
    if (type != null) {
      return _getNameToValue(type);
    } else {
      throw new IllegalArgumentException("Unhandled parameter types: " +
        Arrays.<Object>asList(type).toString());
    }
  }
}
