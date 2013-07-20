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
package org.vclipse.tests.base;

import com.google.inject.Inject;
import org.eclipse.xtext.junit4.InjectWith;
import org.eclipse.xtext.junit4.XtextRunner;
import org.vclipse.base.ImportUriExtractor;
import org.vclipse.tests.VClipseTestPlugin;
import org.vclipse.tests.VClipseTestUtilities;

/* @RunWith(XtextRunner.class) */@InjectWith(VClipseTestPlugin.class)
@SuppressWarnings("all")
public class ImportUriExtractorTest /* implements XtextTest  */{
  @Inject
  private VClipseTestUtilities resourcesLoader;
  
  @Inject
  private ImportUriExtractor uriExtractor;
  
  public ImportUriExtractorTest() {
    throw new Error("Unresolved compilation problems:"
      + "\nThe method super is undefined for the type ImportUriExtractorTest");
  }
  
  /* @Test
   */public void test_ImportUriComputation_EMPTY() {
    throw new Error("Unresolved compilation problems:"
      + "\nAssert cannot be resolved to a type."
      + "\nAssert cannot be resolved to a type."
      + "\nassertTrue cannot be resolved"
      + "\nassertTrue cannot be resolved");
  }
  
  /* @Test
   */public void test_ImportUriComputation_SAME_LENGTH() {
    throw new Error("Unresolved compilation problems:"
      + "\nAssert cannot be resolved to a type."
      + "\nAssert cannot be resolved to a type."
      + "\nAssert cannot be resolved to a type."
      + "\nAssert cannot be resolved to a type."
      + "\nAssert cannot be resolved to a type."
      + "\nAssert cannot be resolved to a type."
      + "\nAssert cannot be resolved to a type."
      + "\nType mismatch: cannot convert from Object to Resource"
      + "\nType mismatch: cannot convert from Object to Resource"
      + "\nType mismatch: cannot convert from Object to Resource"
      + "\nType mismatch: cannot convert from Object to Resource"
      + "\nType mismatch: cannot convert from Object to Resource"
      + "\nType mismatch: cannot convert from Object to Resource"
      + "\nType mismatch: cannot convert from Object to Resource"
      + "\nType mismatch: cannot convert from Object to Resource"
      + "\nType mismatch: cannot convert from Object to Resource"
      + "\nType mismatch: cannot convert from Object to Resource"
      + "\nType mismatch: cannot convert from Object to Resource"
      + "\nType mismatch: cannot convert from Object to Resource"
      + "\nType mismatch: cannot convert from Object to Resource"
      + "\nType mismatch: cannot convert from Object to Resource"
      + "\nassertTrue cannot be resolved"
      + "\nassertTrue cannot be resolved"
      + "\nassertTrue cannot be resolved"
      + "\nassertTrue cannot be resolved"
      + "\nassertTrue cannot be resolved"
      + "\nassertTrue cannot be resolved"
      + "\nassertTrue cannot be resolved");
  }
  
  /* @Test
   */public void test_ImportUriComputation_DIFFERENT_LENGTH() {
    throw new Error("Unresolved compilation problems:"
      + "\nAssert cannot be resolved to a type."
      + "\nAssert cannot be resolved to a type."
      + "\nType mismatch: cannot convert from Object to Resource"
      + "\nType mismatch: cannot convert from Object to Resource"
      + "\nType mismatch: cannot convert from Object to Resource"
      + "\nType mismatch: cannot convert from Object to Resource"
      + "\nassertTrue cannot be resolved"
      + "\nassertTrue cannot be resolved");
  }
}
