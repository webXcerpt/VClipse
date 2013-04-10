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
package org.vclipse.tests.compare;

import com.google.inject.Inject;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.xtext.junit4.InjectWith;
import org.eclipse.xtext.junit4.XtextRunner;
import org.vclipse.refactoring.utils.EntrySearch;
import org.vclipse.tests.VClipseTestUtilities;
import org.vclipse.tests.compare.VCMLCompareInjectorProvider;
import org.vclipse.vcml.compare.VCMLCompareOperation;
import org.vclipse.vcml.vcml.VcmlFactory;
import org.vclipse.vcml.vcml.VcmlPackage;

/* @RunWith(XtextRunner.class) */@InjectWith(VCMLCompareInjectorProvider.class)
@SuppressWarnings("all")
public class VCMLCompareTests /* implements XtextTest  */{
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
  public VcmlPackage before() {
    throw new Error("Unresolved compilation problems:"
      + "\nThe method or field super is undefined for the type VCMLCompareTests"
      + "\nbefore cannot be resolved");
  }
  
  /**
   * if new vc objects are added to a vcml model, they should be extracted
   * during the compare operation.
   */
  /* @Test
   */public Object testAddingVCObjects() {
    throw new Error("Unresolved compilation problems:"
      + "\nThe method assertFalse is undefined for the type VCMLCompareTests"
      + "\nThe method assertFalse is undefined for the type VCMLCompareTests");
  }
  
  /**
   * type changes for existing characteristics are not allowed, the changed object
   * is extracted and a marker is created for the characteristic type
   */
  /* @Test
   */public Object testChangedCsticType() {
    throw new Error("Unresolved compilation problems:"
      + "\nThe method assertFalse is undefined for the type VCMLCompareTests"
      + "\nThe method assertTrue is undefined for the type VCMLCompareTests"
      + "\nThe method assertFalse is undefined for the type VCMLCompareTests"
      + "\nThe method assertTrue is undefined for the type VCMLCompareTests"
      + "\nThe method assertTrue is undefined for the type VCMLCompareTests"
      + "\nThe method assertFalse is undefined for the type VCMLCompareTests"
      + "\nThe method assertTrue is undefined for the type VCMLCompareTests"
      + "\nThe method assertFalse is undefined for the type VCMLCompareTests"
      + "\nThe method assertTrue is undefined for the type VCMLCompareTests"
      + "\nThe method assertTrue is undefined for the type VCMLCompareTests");
  }
}
