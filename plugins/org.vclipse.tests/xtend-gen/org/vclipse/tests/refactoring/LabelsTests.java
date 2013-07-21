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
import org.vclipse.refactoring.utils.EntrySearch;
import org.vclipse.refactoring.utils.Labels;
import org.vclipse.tests.VClipseTestUtilities;
import org.vclipse.tests.refactoring.RefactoringInjectorProvider;

/* @RunWith(XtextRunner.class) */@InjectWith(RefactoringInjectorProvider.class)
@SuppressWarnings("all")
public class LabelsTests /* implements XtextTest  */{
  @Inject
  private EntrySearch search;
  
  @Inject
  private Labels labels;
  
  @Inject
  private VClipseTestUtilities resourcesLoader;
  
  public LabelsTests() {
    throw new Error("Unresolved compilation problems:"
      + "\nThe method super is undefined for the type LabelsTests");
  }
  
  /* @Test
   */public Object test_UILabelProvider() {
    throw new Error("Unresolved compilation problems:"
      + "\nAssert cannot be resolved to a type."
      + "\nAssert cannot be resolved to a type."
      + "\nAssert cannot be resolved to a type."
      + "\nclass_ cannot be resolved"
      + "\nassertNotNull cannot be resolved"
      + "\nassertEquals cannot be resolved"
      + "\nclass_Characteristics cannot be resolved"
      + "\nassertEquals cannot be resolved");
  }
}
