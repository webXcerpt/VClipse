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
import org.eclipse.xtext.xbase.lib.CollectionLiterals;

/**
 * Extensions for {@link org.vclipse.vcml.vcml.VcmlFactory}.
 * 
 * Some of them create cached instances and other a new instance on each call.
 */
@SuppressWarnings("all")
public class VCMLFactoryExtension {
  public /* VcmlFactory */Object VCML_FACTORY;
  
  public /* VcmlPackage */Object VCML_PACKAGE;
  
  public VCMLFactoryExtension() {
    throw new Error("Unresolved compilation problems:"
      + "\nVcmlFactory cannot be resolved to a type."
      + "\nVcmlPackage cannot be resolved to a type."
      + "\neINSTANCE cannot be resolved"
      + "\neINSTANCE cannot be resolved");
  }
  
  public /* CharacteristicValue */Object newCharacteristicValue(final String name) {
    CharacteristicValue _characteristicValue = this.characteristicValue(name, null, null, false);
    return _characteristicValue;
  }
  
  public /* CharacteristicValue */Object characteristicValue(final String name, final String description, final String documentation, final boolean _default) {
    throw new Error("Unresolved compilation problems:"
      + "\nCharacteristicValue cannot be resolved to a type.");
  }
  
  private final /* HashMap<ArrayList<? extends Object>,CharacteristicValue> */Object _createCache_characteristicValue = CollectionLiterals.newHashMap();
  
  private void _init_characteristicValue(final CharacteristicValue it, final String name, final String description, final String documentation, final boolean _default) {
    throw new Error("Unresolved compilation problems:"
      + "\nThe method or field default is undefined for the type VCMLFactoryExtension"
      + "\nname cannot be resolved"
      + "\n^default cannot be resolved"
      + "\ndescription cannot be resolved"
      + "\ndocumentation cannot be resolved"
      + "\ndependencies cannot be resolved"
      + "\ncreateCharacteristicOrValueDependencies cannot be resolved");
  }
  
  public /* SimpleDescription */Object newSimpleDescription(final String description) {
    throw new Error("Unresolved compilation problems:"
      + "\ncreateSimpleDescription cannot be resolved"
      + "\nsetValue cannot be resolved");
  }
  
  public /* SimpleDocumentation */Object newSimpleDocumentation(final String documentation) {
    throw new Error("Unresolved compilation problems:"
      + "\ncreateSimpleDocumentation cannot be resolved"
      + "\nsetValue cannot be resolved");
  }
}
