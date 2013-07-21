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

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import org.eclipse.xtext.junit4.IInjectorProvider;
import org.eclipse.xtext.util.Modules2;
import org.vclipse.refactoring.RefactoringPlugin;
import org.vclipse.refactoring.guice.RefactoringModule;
import org.vclipse.vcml.VCMLRuntimeModule;

/**
 * Injector provider used by test classes being written for re-factoring plug-in
 */
@SuppressWarnings("all")
public class RefactoringInjectorProvider implements IInjectorProvider {
  public Injector getInjector() {
    RefactoringPlugin _instance = RefactoringPlugin.getInstance();
    RefactoringModule _refactoringModule = new RefactoringModule(_instance);
    VCMLRuntimeModule _vCMLRuntimeModule = new VCMLRuntimeModule();
    Module _mixin = Modules2.mixin(_refactoringModule, _vCMLRuntimeModule);
    final Injector injector = Guice.createInjector(_mixin);
    return injector;
  }
}
