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
package org.vclipse.tests.refactoring;

import com.google.inject.Inject;
import org.eclipse.xtext.junit4.InjectWith;
import org.eclipse.xtext.junit4.XtextRunner;
import org.vclipse.refactoring.utils.Extensions;
import org.vclipse.tests.VClipseTestUtilities;
import org.vclipse.tests.refactoring.RefactoringInjectorProvider;

/* @RunWith(XtextRunner.class) */@InjectWith(RefactoringInjectorProvider.class)
@SuppressWarnings("all")
public class ConfigurationTests /* implements XtextTest  */{
  @Inject
  private Extensions extensions;
  
  @Inject
  private VClipseTestUtilities resourcesLoader;
  
  public ConfigurationTests() {
    throw new Error("Unresolved compilation problems:"
      + "\nThe method super is undefined for the type ConfigurationTests");
  }
  
  /* @Test
   */public Object testRefactoringConfiguration() {
    throw new Error("Unresolved compilation problems:"
      + "\nCharacteristic cannot be resolved to a type."
      + "\nAssert cannot be resolved to a type."
      + "\nAssert cannot be resolved to a type."
      + "\nAssert cannot be resolved to a type."
      + "\nType mismatch: cannot convert from Void to EObject"
      + "\nType mismatch: cannot convert from Void to EObject"
      + "\nvcmlModel_Objects cannot be resolved"
      + "\nassertEquals cannot be resolved"
      + "\nassertTrue cannot be resolved"
      + "\nassertTrue cannot be resolved"
      + "\nvcmlModel_Objects cannot be resolved");
  }
}
