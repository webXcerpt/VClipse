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
package org.vclipse.tests.refactoring.swtbot;

import com.google.inject.Injector;
import org.eclipse.core.resources.IProject;
import org.vclipse.refactoring.utils.EntrySearch;
import org.vclipse.refactoring.utils.Extensions;
import org.vclipse.tests.SWTBotWorkspaceWorker;
import org.vclipse.tests.VClipseTestUtilities;
import org.vclipse.tests.refactoring.RefactoringInjectorProvider;
import org.vclipse.vcml.refactoring.VCMLRefactoring;

/* @RunWith(SWTBotJunit4ClassRunner.class)
 */@SuppressWarnings("all")
public class RegressionRefactoringTest extends SWTBotWorkspaceWorker {
  private VClipseTestUtilities resourcesLoader;
  
  private Extensions extensions;
  
  private VCMLRefactoring vcmlRefactoring;
  
  private EntrySearch search;
  
  public Object before() {
    IProject _xblockexpression = null;
    {
      super.before();
      RefactoringInjectorProvider _refactoringInjectorProvider = new RefactoringInjectorProvider();
      final Injector injector = _refactoringInjectorProvider.getInjector();
      VClipseTestUtilities _instance = injector.<VClipseTestUtilities>getInstance(VClipseTestUtilities.class);
      this.resourcesLoader = _instance;
      Extensions _instance_1 = injector.<Extensions>getInstance(Extensions.class);
      this.extensions = _instance_1;
      VCMLRefactoring _instance_2 = injector.<VCMLRefactoring>getInstance(VCMLRefactoring.class);
      this.vcmlRefactoring = _instance_2;
      EntrySearch _instance_3 = injector.<EntrySearch>getInstance(EntrySearch.class);
      this.search = _instance_3;
      this.cleanWorkspace();
      IProject _createProject = this.createProject();
      _xblockexpression = (_createProject);
    }
    return _xblockexpression;
  }
  
  protected IProject createProject() {
    throw new Error("Unresolved compilation problems:"
      + "\nThe method sleep is undefined for the type RegressionRefactoringTest"
      + "\nType mismatch: cannot convert from Object to InputStream"
      + "\nType mismatch: cannot convert from Object to InputStream"
      + "\nType mismatch: cannot convert from Object to InputStream"
      + "\nType mismatch: cannot convert from Object to InputStream");
  }
  
  /* @Test
   */public void test() {
    throw new Error("Unresolved compilation problems:"
      + "\nThe method sleep is undefined for the type RegressionRefactoringTest");
  }
}
