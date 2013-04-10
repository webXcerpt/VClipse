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

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import org.eclipse.xtext.junit4.IInjectorProvider;
import org.eclipse.xtext.ui.shared.internal.SharedModule;
import org.eclipse.xtext.util.Modules2;
import org.vclipse.vcml.compare.VCMLComparePlugin;
import org.vclipse.vcml.compare.injection.VCMLCompareModule;
import org.vclipse.vcml.ui.VCMLUiModule;
import org.vclipse.vcml.ui.internal.VCMLActivator;

/**
 * Injector provider for VCML Compare tests.
 */
@SuppressWarnings("all")
public class VCMLCompareInjectorProvider implements IInjectorProvider {
  public Injector getInjector() {
    VCMLComparePlugin _instance = VCMLComparePlugin.getInstance();
    VCMLCompareModule _vCMLCompareModule = new VCMLCompareModule(_instance);
    VCMLActivator _instance_1 = VCMLActivator.getInstance();
    VCMLUiModule _vCMLUiModule = new VCMLUiModule(_instance_1);
    SharedModule _sharedModule = new SharedModule();
    Module _mixin = Modules2.mixin(_vCMLCompareModule, _vCMLUiModule, _sharedModule);
    Injector _createInjector = Guice.createInjector(_mixin);
    return _createInjector;
  }
}
