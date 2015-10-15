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
package org.vclipse.idoc2jcoidoc.internal;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.xtext.xbase.lib.Exceptions;
import org.vclipse.idoc2jcoidoc.IDoc2JCoIDocPlugin;
import org.vclipse.idoc2jcoidoc.IUiConstants;

@SuppressWarnings("all")
public class PreferenceInitializer extends AbstractPreferenceInitializer {
  /**
   * Defaults
   */
  private final static String DEFAULT_PARTNER_TYPE = "LS";
  
  private final static String DEFAULT_PARTNER_NUMBER = "CML";
  
  @Inject
  @Named(IDoc2JCoIDocPlugin.ID)
  private IPreferenceStore preferenceStore;
  
  private Properties properties;
  
  public PreferenceInitializer() throws IOException {
    Properties _properties = new Properties();
    this.properties = _properties;
    Class<? extends PreferenceInitializer> _class = this.getClass();
    ClassLoader classLoader = _class.getClassLoader();
    try {
      InputStream _resourceAsStream = classLoader.getResourceAsStream("org/vclipse/idoc2jcoidoc/internal/rfc_overridden_settings.properties");
      this.properties.load(_resourceAsStream);
    } catch (final Throwable _t) {
      if (_t instanceof Exception) {
        final Exception exception = (Exception)_t;
        InputStream _resourceAsStream_1 = classLoader.getResourceAsStream("org/vclipse/idoc2jcoidoc/internal/rfc_default_settings.properties");
        this.properties.load(_resourceAsStream_1);
      } else {
        throw Exceptions.sneakyThrow(_t);
      }
    }
  }
  
  @Override
  public void initializeDefaultPreferences() {
    this.preferenceStore.putValue(IUiConstants.NUMBERS_PROVIDER, IUiConstants.TARGET_SYSTEM);
    String _property = this.properties.getProperty(IUiConstants.RFC_FOR_UPS_NUMBERS);
    this.preferenceStore.putValue(IUiConstants.RFC_FOR_UPS_NUMBERS, _property);
    String _property_1 = this.properties.getProperty(IUiConstants.RFC_FOR_IDOC_NUMBERS);
    this.preferenceStore.putValue(IUiConstants.RFC_FOR_IDOC_NUMBERS, _property_1);
    this.preferenceStore.putValue(IUiConstants.PARTNER_NUMBER, PreferenceInitializer.DEFAULT_PARTNER_NUMBER);
    this.preferenceStore.putValue(IUiConstants.PARTNER_TYPE, PreferenceInitializer.DEFAULT_PARTNER_TYPE);
  }
}
