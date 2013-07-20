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
package org.vclipse.vcml.utils;

import com.google.common.collect.Maps;
import com.google.inject.Inject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.conversion.IValueConverter;
import org.eclipse.xtext.xbase.lib.CollectionLiterals;
import org.vclipse.vcml.conversion.VCMLValueConverter;
import org.vclipse.vcml.utils.VCMLObjectUtils;

@SuppressWarnings("all")
public class CreateVcmlObjects extends VCMLObjectUtils {
  protected static /* VcmlFactory */Object VCML_FACTORY /* Skipped initializer because of errors */;
  
  protected static /* VcmlPackage */Object VCML_PACKAGE /* Skipped initializer because of errors */;
  
  @Inject
  private VCMLValueConverter valueConverter;
  
  private Map<String,String> name2Prefix;
  
  public CreateVcmlObjects() {
    HashMap<String,String> _newHashMap = Maps.<String, String>newHashMap();
    this.name2Prefix = _newHashMap;
  }
  
  public String addPrefixMapping(final String name, final String prefix) {
    String _put = this.name2Prefix.put(name, prefix);
    return _put;
  }
  
  public void clear() {
    this.name2Prefix.clear();
  }
  
  public /* org.vclipse.vcml.vcml.Class */Object newConfigurableClass(final String name, final String description) {
    String _plus = ("(300)" + name);
    Class _newClass = this.newClass(_plus, description);
    return _newClass;
  }
  
  public /* Material */Object newMaterial(final String name, final String description, final String type) {
    throw new Error("Unresolved compilation problems:"
      + "\nMaterial cannot be resolved to a type.");
  }
  
  private final /* HashMap<ArrayList<? extends Object>,Material> */Object _createCache_newMaterial = CollectionLiterals.newHashMap();
  
  private void _init_newMaterial(final Material it, final String name, final String description, final String type) {
    throw new Error("Unresolved compilation problems:"
      + "\nThe method mkSimpleDescription is undefined for the type CreateVcmlObjects"
      + "\nname cannot be resolved"
      + "\ndescription cannot be resolved"
      + "\ntype cannot be resolved");
  }
  
  public /* org.vclipse.vcml.vcml.Class */Object newClass(final String name, final String description) {
    throw new Error("Unresolved compilation problems:"
      + "\norg.vclipse.vcml.vcml.Class cannot be resolved to a type.");
  }
  
  private final /* HashMap<ArrayList<? extends Object>,Class> */Object _createCache_newClass = CollectionLiterals.newHashMap();
  
  private void _init_newClass(final Class it, final String name, final String description) {
    throw new Error("Unresolved compilation problems:"
      + "\nThe method mkSimpleDescription is undefined for the type CreateVcmlObjects"
      + "\nname cannot be resolved"
      + "\ndescription cannot be resolved");
  }
  
  public /* org.vclipse.vcml.vcml.Classification */Object newClassification(final /* org.vclipse.vcml.vcml.Class */Object class_) {
    throw new Error("Unresolved compilation problems:"
      + "\norg.vclipse.vcml.vcml.Classification cannot be resolved to a type."
      + "\norg.vclipse.vcml.vcml.Class cannot be resolved to a type.");
  }
  
  private final /* HashMap<ArrayList<? extends Object>,Classification> */Object _createCache_newClassification = CollectionLiterals.newHashMap();
  
  private void _init_newClassification(final Classification it, final /* org.vclipse.vcml.vcml.Class */Object class_) {
    throw new Error("Unresolved compilation problems:"
      + "\ncls cannot be resolved");
  }
  
  public /* BillOfMaterial */Object newBom(final /* Material */Object material, final String description) {
    throw new Error("Unresolved compilation problems:"
      + "\nBillOfMaterial cannot be resolved to a type."
      + "\nMaterial cannot be resolved to a type.");
  }
  
  private final /* HashMap<ArrayList<? extends Object>,BillOfMaterial> */Object _createCache_newBom = CollectionLiterals.newHashMap();
  
  private void _init_newBom(final BillOfMaterial it, final /* Material */Object material, final String description) {
    throw new Error("Unresolved compilation problems:"
      + "\nThe method mkSimpleDescription is undefined for the type CreateVcmlObjects"
      + "\nname cannot be resolved"
      + "\nname cannot be resolved"
      + "\nmaterial cannot be resolved"
      + "\ndescription cannot be resolved");
  }
  
  public /* BOMItem */Object newBOMItem(final int number, final /* Material */Object material) {
    throw new Error("Unresolved compilation problems:"
      + "\nBOMItem cannot be resolved to a type."
      + "\nMaterial cannot be resolved to a type.");
  }
  
  private final /* HashMap<ArrayList<? extends Object>,BOMItem> */Object _createCache_newBOMItem = CollectionLiterals.newHashMap();
  
  private void _init_newBOMItem(final BOMItem it, final int number, final /* Material */Object material) {
    throw new Error("Unresolved compilation problems:"
      + "\nmaterial cannot be resolved"
      + "\nitemnumber cannot be resolved");
  }
  
  public /* BOMItem */Object newBOMItem(final int number, final /* Material */Object material, final /* SelectionCondition */Object condition) {
    throw new Error("Unresolved compilation problems:"
      + "\nBOMItem cannot be resolved to a type."
      + "\nMaterial cannot be resolved to a type."
      + "\nSelectionCondition cannot be resolved to a type.");
  }
  
  private final /* HashMap<ArrayList<? extends Object>,BOMItem> */Object _createCache_newBOMItem_1 = CollectionLiterals.newHashMap();
  
  private void _init_newBOMItem(final BOMItem it, final int number, final /* Material */Object material, final /* SelectionCondition */Object condition) {
    throw new Error("Unresolved compilation problems:"
      + "\nselectionCondition cannot be resolved");
  }
  
  public /* BOMItem */Object newBOMItem(final int number, final /* org.vclipse.vcml.vcml.Class */Object cls) {
    throw new Error("Unresolved compilation problems:"
      + "\nBOMItem cannot be resolved to a type."
      + "\norg.vclipse.vcml.vcml.Class cannot be resolved to a type.");
  }
  
  private final /* HashMap<ArrayList<? extends Object>,BOMItem> */Object _createCache_newBOMItem_2 = CollectionLiterals.newHashMap();
  
  private void _init_newBOMItem_1(final BOMItem it, final int number, final /* org.vclipse.vcml.vcml.Class */Object cls) {
    throw new Error("Unresolved compilation problems:"
      + "\ncls cannot be resolved"
      + "\nitemnumber cannot be resolved");
  }
  
  public /* BOMItem */Object newBOMItem(final int number, final /* org.vclipse.vcml.vcml.Class */Object cls, final /* SelectionCondition */Object condition) {
    throw new Error("Unresolved compilation problems:"
      + "\nBOMItem cannot be resolved to a type."
      + "\norg.vclipse.vcml.vcml.Class cannot be resolved to a type."
      + "\nSelectionCondition cannot be resolved to a type.");
  }
  
  private final /* HashMap<ArrayList<? extends Object>,BOMItem> */Object _createCache_newBOMItem_3 = CollectionLiterals.newHashMap();
  
  private void _init_newBOMItem_1(final BOMItem it, final int number, final /* org.vclipse.vcml.vcml.Class */Object cls, final /* SelectionCondition */Object condition) {
    throw new Error("Unresolved compilation problems:"
      + "\nselectionCondition cannot be resolved");
  }
  
  public /* Characteristic */Object newNumericCharacteristic(final String name, final String description) {
    throw new Error("Unresolved compilation problems:"
      + "\nCharacteristic cannot be resolved to a type.");
  }
  
  private final /* HashMap<ArrayList<? extends Object>,Characteristic> */Object _createCache_newNumericCharacteristic = CollectionLiterals.newHashMap();
  
  private void _init_newNumericCharacteristic(final Characteristic it, final String name, final String description) {
    throw new Error("Unresolved compilation problems:"
      + "\nThe method mkSimpleDescription is undefined for the type CreateVcmlObjects"
      + "\nname cannot be resolved"
      + "\ndescription cannot be resolved"
      + "\ntype cannot be resolved");
  }
  
  public /* Characteristic */Object newSymbolicCharacteristic(final String name, final String description) {
    throw new Error("Unresolved compilation problems:"
      + "\nCharacteristic cannot be resolved to a type.");
  }
  
  private final /* HashMap<ArrayList<? extends Object>,Characteristic> */Object _createCache_newSymbolicCharacteristic = CollectionLiterals.newHashMap();
  
  private void _init_newSymbolicCharacteristic(final Characteristic it, final String name, final String description) {
    throw new Error("Unresolved compilation problems:"
      + "\nThe method mkSimpleDescription is undefined for the type CreateVcmlObjects"
      + "\nname cannot be resolved"
      + "\ndescription cannot be resolved"
      + "\ntype cannot be resolved");
  }
  
  public /* Constraint */Object newConstraint(final String name, final String description) {
    throw new Error("Unresolved compilation problems:"
      + "\nConstraint cannot be resolved to a type.");
  }
  
  private final /* HashMap<ArrayList<? extends Object>,Constraint> */Object _createCache_newConstraint = CollectionLiterals.newHashMap();
  
  private void _init_newConstraint(final Constraint it, final String name, final String description) {
    throw new Error("Unresolved compilation problems:"
      + "\nname cannot be resolved"
      + "\ndescription cannot be resolved"
      + "\nsource cannot be resolved"
      + "\ncreateConstraintSource cannot be resolved");
  }
  
  public Object newSimpleDescription(final String description) {
    throw new Error("Unresolved compilation problems:"
      + "\ncreateSimpleDescription cannot be resolved"
      + "\nsetValue cannot be resolved");
  }
  
  public /* CharacteristicType */Object newSymbolicTypeInstance() {
    throw new Error("Unresolved compilation problems:"
      + "\ncreateSymbolicType cannot be resolved"
      + "\nnumberOfChars cannot be resolved"
      + "\ncaseSensitive cannot be resolved");
  }
  
  public /* NumericType */Object newNumericTypeInstance() {
    throw new Error("Unresolved compilation problems:"
      + "\ncreateNumericType cannot be resolved"
      + "\nnumberOfChars cannot be resolved"
      + "\ndecimalPlaces cannot be resolved"
      + "\nnegativeValuesAllowed cannot be resolved");
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
  
  private String getNameWithPrefix(final String name) {
    String _xblockexpression = null;
    {
      boolean _containsKey = this.name2Prefix.containsKey(name);
      if (_containsKey) {
        String _get = this.name2Prefix.get(name);
        return (_get + name);
      }
      _xblockexpression = (name);
    }
    return _xblockexpression;
  }
}
