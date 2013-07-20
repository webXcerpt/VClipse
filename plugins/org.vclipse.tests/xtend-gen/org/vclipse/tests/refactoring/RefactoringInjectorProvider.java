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

import com.google.inject.Injector;
import org.eclipse.xtext.junit4.IInjectorProvider;

/**
 * Injector provider used by test classes being written for re-factoring plug-in
 */
@SuppressWarnings("all")
public class RefactoringInjectorProvider implements IInjectorProvider {
  public Injector getInjector() {
    throw new Error("Unresolved compilation problems:"
      + "\nType mismatch: cannot convert from VCMLRuntimeModule to Module");
  }
}
