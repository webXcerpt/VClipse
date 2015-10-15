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
package org.vclipse.idoc2jcoidoc;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.vclipse.idoc2jcoidoc.IUiConstants;
import org.vclipse.idoc2jcoidoc.injection.IDoc2JCoIDocModule;

@SuppressWarnings("all")
public class IDoc2JCoIDocPlugin extends AbstractUIPlugin {
  public final static String ID = "org.vclipse.idoc2jcoidoc";
  
  private static IDoc2JCoIDocPlugin plugin;
  
  private Injector injector;
  
  @Override
  public void start(final BundleContext context) throws Exception {
    super.start(context);
    IDoc2JCoIDocPlugin.plugin = this;
  }
  
  @Override
  public void stop(final BundleContext context) throws Exception {
    IDoc2JCoIDocPlugin.plugin = null;
    super.stop(context);
  }
  
  public Injector getInjector() {
    if ((this.injector == null)) {
      IDoc2JCoIDocModule _iDoc2JCoIDocModule = new IDoc2JCoIDocModule(this);
      Injector _createInjector = Guice.createInjector(_iDoc2JCoIDocModule);
      this.injector = _createInjector;
    }
    return this.injector;
  }
  
  public static IDoc2JCoIDocPlugin getInstance() {
    return IDoc2JCoIDocPlugin.plugin;
  }
  
  public static void log(final String message, final int severity) {
    IDoc2JCoIDocPlugin _instance = IDoc2JCoIDocPlugin.getInstance();
    ILog _log = _instance.getLog();
    Status _status = new Status(severity, IDoc2JCoIDocPlugin.ID, IStatus.OK, message, null);
    _log.log(_status);
  }
  
  public static void log(final String message, final Throwable throwable) {
    IDoc2JCoIDocPlugin _instance = IDoc2JCoIDocPlugin.getInstance();
    ILog _log = _instance.getLog();
    Status _status = new Status(IStatus.ERROR, IDoc2JCoIDocPlugin.ID, IStatus.OK, message, throwable);
    _log.log(_status);
  }
  
  public static ImageDescriptor getImageDescriptor(final String key) {
    IDoc2JCoIDocPlugin _instance = IDoc2JCoIDocPlugin.getInstance();
    ImageRegistry _imageRegistry = _instance.getImageRegistry();
    return _imageRegistry.getDescriptor(key);
  }
  
  public static Image getImage(final String key) {
    IDoc2JCoIDocPlugin _instance = IDoc2JCoIDocPlugin.getInstance();
    ImageRegistry _imageRegistry = _instance.getImageRegistry();
    return _imageRegistry.get(key);
  }
  
  @Override
  protected void initializeImageRegistry(final ImageRegistry registry) {
    this.addImage(IUiConstants.SEND_IDOCS_IMAGE, "icons/sendidocs.png");
    this.addImage(IUiConstants.SEND_IDOCS_IMAGE_DISABLED, "icons/sendidocs_disabled.png");
    this.addImage(IUiConstants.IDOC_DOCUMENT_IMAGE, "icons/idoc.gif");
    this.addImage(IUiConstants.IDOC_SEGMENT_IMAGE, "icons/page_white_text.png");
    super.initializeImageRegistry(registry);
  }
  
  private void addImage(final String name, final String path) {
    IDoc2JCoIDocPlugin _instance = IDoc2JCoIDocPlugin.getInstance();
    ImageRegistry _imageRegistry = _instance.getImageRegistry();
    ImageDescriptor _imageDescriptorFromPlugin = AbstractUIPlugin.imageDescriptorFromPlugin(IDoc2JCoIDocPlugin.ID, path);
    Image _createImage = _imageDescriptorFromPlugin.createImage();
    _imageRegistry.put(name, _createImage);
  }
}
