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

import java.util.ArrayList;
import java.util.HashMap;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.xbase.lib.CollectionLiterals;
import org.vclipse.vcml.vcml.Characteristic;
import org.vclipse.vcml.vcml.Constraint;
import org.vclipse.vcml.vcml.DependencyNet;
import org.vclipse.vcml.vcml.InterfaceDesign;
import org.vclipse.vcml.vcml.Material;
import org.vclipse.vcml.vcml.Precondition;
import org.vclipse.vcml.vcml.Procedure;
import org.vclipse.vcml.vcml.SelectionCondition;
import org.vclipse.vcml.vcml.VCObject;
import org.vclipse.vcml.vcml.VariantFunction;
import org.vclipse.vcml.vcml.VariantTable;
import org.vclipse.vcml.vcml.VcmlFactory;

/**
 * Extensions of the VcmlFactory creating proxy objects with a given name in a given resource.
 */
@SuppressWarnings("all")
public class VCMLProxyFactory {
  public Characteristic characteristicProxy(final String name, final Resource resource) {
    final ArrayList<?>_cacheKey = CollectionLiterals.newArrayList(name, resource);
    final Characteristic _result;
    synchronized (_createCache_characteristicProxy) {
      if (_createCache_characteristicProxy.containsKey(_cacheKey)) {
        return _createCache_characteristicProxy.get(_cacheKey);
      }
      Characteristic _createCharacteristic = VcmlFactory.eINSTANCE.createCharacteristic();
      _result = _createCharacteristic;
      _createCache_characteristicProxy.put(_cacheKey, _result);
    }
    _init_characteristicProxy(_result, name, resource);
    return _result;
  }
  
  private final HashMap<ArrayList<? extends Object>,Characteristic> _createCache_characteristicProxy = CollectionLiterals.newHashMap();
  
  private void _init_characteristicProxy(final Characteristic it, final String name, final Resource resource) {
    this.<Characteristic>createProxy(it, name, resource);
  }
  
  public org.vclipse.vcml.vcml.Class classProxy(final String name, final Resource resource) {
    final ArrayList<?>_cacheKey = CollectionLiterals.newArrayList(name, resource);
    final org.vclipse.vcml.vcml.Class _result;
    synchronized (_createCache_classProxy) {
      if (_createCache_classProxy.containsKey(_cacheKey)) {
        return _createCache_classProxy.get(_cacheKey);
      }
      org.vclipse.vcml.vcml.Class _createClass = VcmlFactory.eINSTANCE.createClass();
      _result = _createClass;
      _createCache_classProxy.put(_cacheKey, _result);
    }
    _init_classProxy(_result, name, resource);
    return _result;
  }
  
  private final HashMap<ArrayList<? extends Object>,org.vclipse.vcml.vcml.Class> _createCache_classProxy = CollectionLiterals.newHashMap();
  
  private void _init_classProxy(final org.vclipse.vcml.vcml.Class it, final String name, final Resource resource) {
    this.<org.vclipse.vcml.vcml.Class>createProxy(it, name, resource);
  }
  
  public Constraint constraintProxy(final String name, final Resource resource) {
    final ArrayList<?>_cacheKey = CollectionLiterals.newArrayList(name, resource);
    final Constraint _result;
    synchronized (_createCache_constraintProxy) {
      if (_createCache_constraintProxy.containsKey(_cacheKey)) {
        return _createCache_constraintProxy.get(_cacheKey);
      }
      Constraint _createConstraint = VcmlFactory.eINSTANCE.createConstraint();
      _result = _createConstraint;
      _createCache_constraintProxy.put(_cacheKey, _result);
    }
    _init_constraintProxy(_result, name, resource);
    return _result;
  }
  
  private final HashMap<ArrayList<? extends Object>,Constraint> _createCache_constraintProxy = CollectionLiterals.newHashMap();
  
  private void _init_constraintProxy(final Constraint it, final String name, final Resource resource) {
    this.<Constraint>createProxy(it, name, resource);
  }
  
  public VariantFunction variantFunctionProxy(final String name, final Resource resource) {
    final ArrayList<?>_cacheKey = CollectionLiterals.newArrayList(name, resource);
    final VariantFunction _result;
    synchronized (_createCache_variantFunctionProxy) {
      if (_createCache_variantFunctionProxy.containsKey(_cacheKey)) {
        return _createCache_variantFunctionProxy.get(_cacheKey);
      }
      VariantFunction _createVariantFunction = VcmlFactory.eINSTANCE.createVariantFunction();
      _result = _createVariantFunction;
      _createCache_variantFunctionProxy.put(_cacheKey, _result);
    }
    _init_variantFunctionProxy(_result, name, resource);
    return _result;
  }
  
  private final HashMap<ArrayList<? extends Object>,VariantFunction> _createCache_variantFunctionProxy = CollectionLiterals.newHashMap();
  
  private void _init_variantFunctionProxy(final VariantFunction it, final String name, final Resource resource) {
    this.<VariantFunction>createProxy(it, name, resource);
  }
  
  public VariantTable variantTableProxy(final String name, final Resource resource) {
    final ArrayList<?>_cacheKey = CollectionLiterals.newArrayList(name, resource);
    final VariantTable _result;
    synchronized (_createCache_variantTableProxy) {
      if (_createCache_variantTableProxy.containsKey(_cacheKey)) {
        return _createCache_variantTableProxy.get(_cacheKey);
      }
      VariantTable _createVariantTable = VcmlFactory.eINSTANCE.createVariantTable();
      _result = _createVariantTable;
      _createCache_variantTableProxy.put(_cacheKey, _result);
    }
    _init_variantTableProxy(_result, name, resource);
    return _result;
  }
  
  private final HashMap<ArrayList<? extends Object>,VariantTable> _createCache_variantTableProxy = CollectionLiterals.newHashMap();
  
  private void _init_variantTableProxy(final VariantTable it, final String name, final Resource resource) {
    this.<VariantTable>createProxy(it, name, resource);
  }
  
  public SelectionCondition selectionConditionProxy(final String name, final Resource resource) {
    final ArrayList<?>_cacheKey = CollectionLiterals.newArrayList(name, resource);
    final SelectionCondition _result;
    synchronized (_createCache_selectionConditionProxy) {
      if (_createCache_selectionConditionProxy.containsKey(_cacheKey)) {
        return _createCache_selectionConditionProxy.get(_cacheKey);
      }
      SelectionCondition _createSelectionCondition = VcmlFactory.eINSTANCE.createSelectionCondition();
      _result = _createSelectionCondition;
      _createCache_selectionConditionProxy.put(_cacheKey, _result);
    }
    _init_selectionConditionProxy(_result, name, resource);
    return _result;
  }
  
  private final HashMap<ArrayList<? extends Object>,SelectionCondition> _createCache_selectionConditionProxy = CollectionLiterals.newHashMap();
  
  private void _init_selectionConditionProxy(final SelectionCondition it, final String name, final Resource resource) {
    this.<SelectionCondition>createProxy(it, name, resource);
  }
  
  public Procedure procedureProxy(final String name, final Resource resource) {
    final ArrayList<?>_cacheKey = CollectionLiterals.newArrayList(name, resource);
    final Procedure _result;
    synchronized (_createCache_procedureProxy) {
      if (_createCache_procedureProxy.containsKey(_cacheKey)) {
        return _createCache_procedureProxy.get(_cacheKey);
      }
      Procedure _createProcedure = VcmlFactory.eINSTANCE.createProcedure();
      _result = _createProcedure;
      _createCache_procedureProxy.put(_cacheKey, _result);
    }
    _init_procedureProxy(_result, name, resource);
    return _result;
  }
  
  private final HashMap<ArrayList<? extends Object>,Procedure> _createCache_procedureProxy = CollectionLiterals.newHashMap();
  
  private void _init_procedureProxy(final Procedure it, final String name, final Resource resource) {
    this.<Procedure>createProxy(it, name, resource);
  }
  
  public Precondition preconditionProxy(final String name, final Resource resource) {
    final ArrayList<?>_cacheKey = CollectionLiterals.newArrayList(name, resource);
    final Precondition _result;
    synchronized (_createCache_preconditionProxy) {
      if (_createCache_preconditionProxy.containsKey(_cacheKey)) {
        return _createCache_preconditionProxy.get(_cacheKey);
      }
      Precondition _createPrecondition = VcmlFactory.eINSTANCE.createPrecondition();
      _result = _createPrecondition;
      _createCache_preconditionProxy.put(_cacheKey, _result);
    }
    _init_preconditionProxy(_result, name, resource);
    return _result;
  }
  
  private final HashMap<ArrayList<? extends Object>,Precondition> _createCache_preconditionProxy = CollectionLiterals.newHashMap();
  
  private void _init_preconditionProxy(final Precondition it, final String name, final Resource resource) {
    this.<Precondition>createProxy(it, name, resource);
  }
  
  public Material materialProxy(final String name, final Resource resource) {
    final ArrayList<?>_cacheKey = CollectionLiterals.newArrayList(name, resource);
    final Material _result;
    synchronized (_createCache_materialProxy) {
      if (_createCache_materialProxy.containsKey(_cacheKey)) {
        return _createCache_materialProxy.get(_cacheKey);
      }
      Material _createMaterial = VcmlFactory.eINSTANCE.createMaterial();
      _result = _createMaterial;
      _createCache_materialProxy.put(_cacheKey, _result);
    }
    _init_materialProxy(_result, name, resource);
    return _result;
  }
  
  private final HashMap<ArrayList<? extends Object>,Material> _createCache_materialProxy = CollectionLiterals.newHashMap();
  
  private void _init_materialProxy(final Material it, final String name, final Resource resource) {
    this.<Material>createProxy(it, name, resource);
  }
  
  public InterfaceDesign interfaceDesignProxy(final String name, final Resource resource) {
    final ArrayList<?>_cacheKey = CollectionLiterals.newArrayList(name, resource);
    final InterfaceDesign _result;
    synchronized (_createCache_interfaceDesignProxy) {
      if (_createCache_interfaceDesignProxy.containsKey(_cacheKey)) {
        return _createCache_interfaceDesignProxy.get(_cacheKey);
      }
      InterfaceDesign _createInterfaceDesign = VcmlFactory.eINSTANCE.createInterfaceDesign();
      _result = _createInterfaceDesign;
      _createCache_interfaceDesignProxy.put(_cacheKey, _result);
    }
    _init_interfaceDesignProxy(_result, name, resource);
    return _result;
  }
  
  private final HashMap<ArrayList<? extends Object>,InterfaceDesign> _createCache_interfaceDesignProxy = CollectionLiterals.newHashMap();
  
  private void _init_interfaceDesignProxy(final InterfaceDesign it, final String name, final Resource resource) {
    this.<InterfaceDesign>createProxy(it, name, resource);
  }
  
  public DependencyNet dependencyNetProxy(final String name, final Resource resource) {
    final ArrayList<?>_cacheKey = CollectionLiterals.newArrayList(name, resource);
    final DependencyNet _result;
    synchronized (_createCache_dependencyNetProxy) {
      if (_createCache_dependencyNetProxy.containsKey(_cacheKey)) {
        return _createCache_dependencyNetProxy.get(_cacheKey);
      }
      DependencyNet _createDependencyNet = VcmlFactory.eINSTANCE.createDependencyNet();
      _result = _createDependencyNet;
      _createCache_dependencyNetProxy.put(_cacheKey, _result);
    }
    _init_dependencyNetProxy(_result, name, resource);
    return _result;
  }
  
  private final HashMap<ArrayList<? extends Object>,DependencyNet> _createCache_dependencyNetProxy = CollectionLiterals.newHashMap();
  
  private void _init_dependencyNetProxy(final DependencyNet it, final String name, final Resource resource) {
    this.<DependencyNet>createProxy(it, name, resource);
  }
  
  protected <T extends VCObject> T createProxy(final T object, final String name, final Resource resource) {
    T _xblockexpression = null;
    {
      object.setName(name);
      URI _uRI = resource.getURI();
      final URI uri = _uRI.appendFragment(name);
      ((InternalEObject) object).eSetProxyURI(uri);
      _xblockexpression = (object);
    }
    return _xblockexpression;
  }
}
