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
package org.vclipse.tests.vcml;

import com.google.inject.Inject;
import org.eclipse.xtext.junit4.InjectWith;
import org.eclipse.xtext.junit4.XtextRunner;
import org.eclipse.xtext.serializer.ISerializer;
import org.vclipse.base.naming.INameProvider;
import org.vclipse.tests.VClipseTestPlugin;
import org.vclipse.tests.VClipseTestUtilities;
import org.vclipse.vcml.VCMLUtilities;

/* @RunWith(XtextRunner.class) */@InjectWith(VClipseTestPlugin.class)
@SuppressWarnings("all")
public class VCMLUtilityTests /* implements XtextTest  */{
  @Inject
  private VClipseTestUtilities testUtilities;
  
  @Inject
  private VCMLUtilities vcmlUtility;
  
  @Inject
  private ISerializer vcmlSerializer;
  
  @Inject
  private INameProvider nameProvider;
  
  private /* VcmlPackage */Object vcmlPackage /* Skipped initializer because of errors */;
  
  public VCMLUtilityTests() {
    throw new Error("Unresolved compilation problems:"
      + "\nThe method super is undefined for the type VCMLUtilityTests");
  }
  
  /* @Test
   */public void test_SortVCObjectsList() {
    throw new Error("Unresolved compilation problems:"
      + "\nVcmlModel cannot be resolved to a type."
      + "\nVCObject cannot be resolved to a type."
      + "\nVCObject cannot be resolved to a type."
      + "\nAssert cannot be resolved to a type."
      + "\ncontents cannot be resolved"
      + "\nget cannot be resolved"
      + "\nobjects cannot be resolved"
      + "\nname cannot be resolved"
      + "\ncompareTo cannot be resolved"
      + "\nname cannot be resolved"
      + "\nassertFalse cannot be resolved");
  }
  
  /* @Test
   */public void test_FindEntries() {
    throw new Error("Unresolved compilation problems:"
      + "\nVcmlModel cannot be resolved to a type."
      + "\nAssert cannot be resolved to a type."
      + "\nAssert cannot be resolved to a type."
      + "\nAssert cannot be resolved to a type."
      + "\nAssert cannot be resolved to a type."
      + "\ncontents cannot be resolved"
      + "\nget cannot be resolved"
      + "\nclass_ cannot be resolved"
      + "\nobjects cannot be resolved"
      + "\nassertNotNull cannot be resolved"
      + "\ncharacteristic cannot be resolved"
      + "\nobjects cannot be resolved"
      + "\nassertNotNull cannot be resolved"
      + "\ncharacteristic cannot be resolved"
      + "\nobjects cannot be resolved"
      + "\nassertNull cannot be resolved"
      + "\nconstraint cannot be resolved"
      + "\nobjects cannot be resolved"
      + "\nassertNull cannot be resolved");
  }
}
