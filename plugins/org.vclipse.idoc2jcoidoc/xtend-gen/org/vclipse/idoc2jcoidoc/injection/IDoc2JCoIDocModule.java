/**
 * Copyright (c) 2010 - 2013 webXcerpt Software GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * webXcerpt Software GmbH - initial creator
 * www.webxcerpt.com
 */
package org.vclipse.idoc2jcoidoc.injection;

import com.google.inject.Binder;
import com.google.inject.Injector;
import com.google.inject.binder.AnnotatedBindingBuilder;
import com.google.inject.binder.LinkedBindingBuilder;
import com.google.inject.name.Named;
import com.google.inject.name.Names;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.xtext.service.AbstractGenericModule;
import org.vclipse.connection.IConnectionHandler;
import org.vclipse.connection.VClipseConnectionPlugin;
import org.vclipse.idoc2jcoidoc.DefaultIDoc2JCoIDocProcessor;
import org.vclipse.idoc2jcoidoc.IDoc2JCoIDocPlugin;
import org.vclipse.idoc2jcoidoc.IIDoc2JCoIDocProcessor;
import org.vclipse.vcml.ui.VCMLUiPlugin;
import org.vclipse.vcml2idoc.VCML2IDocPlugin;
import org.vclipse.vcml2idoc.transformation.VCML2IDocSwitch;

@SuppressWarnings("all")
public class IDoc2JCoIDocModule extends AbstractGenericModule {
  private AbstractUIPlugin plugin;
  
  public IDoc2JCoIDocModule(final AbstractUIPlugin activator) {
    this.plugin = activator;
  }
  
  public IPreferenceStore bindPreferenceStore() {
    return this.plugin.getPreferenceStore();
  }
  
  @Override
  public void configure(final Binder binder) {
    super.configure(binder);
    Injector vcmlInjector = VCMLUiPlugin.getInjector();
    VCML2IDocPlugin _default = VCML2IDocPlugin.getDefault();
    Injector vcml2IDocInjector = _default.getInjector();
    VClipseConnectionPlugin _default_1 = VClipseConnectionPlugin.getDefault();
    Injector connectionInjector = _default_1.getInjector();
    AnnotatedBindingBuilder<IPreferenceStore> _bind = binder.<IPreferenceStore>bind(IPreferenceStore.class);
    Named _named = Names.named(VCMLUiPlugin.ID);
    LinkedBindingBuilder<IPreferenceStore> _annotatedWith = _bind.annotatedWith(_named);
    IPreferenceStore _instance = vcmlInjector.<IPreferenceStore>getInstance(IPreferenceStore.class);
    _annotatedWith.toInstance(_instance);
    AnnotatedBindingBuilder<IPreferenceStore> _bind_1 = binder.<IPreferenceStore>bind(IPreferenceStore.class);
    Named _named_1 = Names.named(VCML2IDocPlugin.ID);
    LinkedBindingBuilder<IPreferenceStore> _annotatedWith_1 = _bind_1.annotatedWith(_named_1);
    IPreferenceStore _instance_1 = vcml2IDocInjector.<IPreferenceStore>getInstance(IPreferenceStore.class);
    _annotatedWith_1.toInstance(_instance_1);
    AnnotatedBindingBuilder<IPreferenceStore> _bind_2 = binder.<IPreferenceStore>bind(IPreferenceStore.class);
    Named _named_2 = Names.named(IDoc2JCoIDocPlugin.ID);
    LinkedBindingBuilder<IPreferenceStore> _annotatedWith_2 = _bind_2.annotatedWith(_named_2);
    IPreferenceStore _preferenceStore = this.plugin.getPreferenceStore();
    _annotatedWith_2.toInstance(_preferenceStore);
    AnnotatedBindingBuilder<VCML2IDocSwitch> _bind_3 = binder.<VCML2IDocSwitch>bind(VCML2IDocSwitch.class);
    VCML2IDocSwitch _instance_2 = vcml2IDocInjector.<VCML2IDocSwitch>getInstance(VCML2IDocSwitch.class);
    _bind_3.toInstance(_instance_2);
    AnnotatedBindingBuilder<IConnectionHandler> _bind_4 = binder.<IConnectionHandler>bind(IConnectionHandler.class);
    IConnectionHandler _instance_3 = connectionInjector.<IConnectionHandler>getInstance(IConnectionHandler.class);
    _bind_4.toInstance(_instance_3);
  }
  
  public Class<? extends IIDoc2JCoIDocProcessor> bindIDoc2JCoIDocProcessor() {
    return DefaultIDoc2JCoIDocProcessor.class;
  }
}
