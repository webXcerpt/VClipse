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

import org.eclipse.core.runtime.Status;
import org.vclipse.idoc2jcoidoc.IDoc2JCoIDocPlugin;

@SuppressWarnings("all")
public class IDocSenderStatus extends Status {
  private String upsNumber;
  
  private String sapSystem;
  
  public IDocSenderStatus(final int severity) {
    this(severity, "");
  }
  
  public IDocSenderStatus(final int severity, final String message) {
    super(severity, IDoc2JCoIDocPlugin.ID, message);
  }
  
  @Override
  public void setMessage(final String message) {
    super.setMessage(message);
  }
  
  public String getUpsNumber() {
    return this.upsNumber;
  }
  
  public void setUpsNumber(final String upsNumber) {
    this.upsNumber = upsNumber;
  }
  
  public String getSapSystem() {
    return this.sapSystem;
  }
  
  public void setSapSystem(final String sapSystem) {
    this.sapSystem = sapSystem;
  }
}
