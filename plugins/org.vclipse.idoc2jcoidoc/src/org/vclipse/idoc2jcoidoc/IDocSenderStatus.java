/*******************************************************************************
 * Copyright (c) 2010 - 2013 webXcerpt Software GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *  
 * Contributors:
 *     	webXcerpt Software GmbH - initial creator
 * 		www.webxcerpt.com
 ******************************************************************************/
package org.vclipse.idoc2jcoidoc;

import org.eclipse.core.runtime.Status;

public class IDocSenderStatus extends Status {

	private String upsNumber;
	
	private String sapSystem;
	
	public IDocSenderStatus(int severity) {
		this(severity, "");
	}
	
	public IDocSenderStatus(int severity, String message) {
		super(severity, IDoc2JCoIDocPlugin.ID, message);
	}
	
	public void setMessage(String message) {
		super.setMessage(message);
	}
	
	public String getUpsNumber() {
		return upsNumber;
	}

	public void setUpsNumber(String upsNumber) {
		this.upsNumber = upsNumber;
	}

	public String getSapSystem() {
		return sapSystem;
	}

	public void setSapSystem(String sapSystem) {
		this.sapSystem = sapSystem;
	}
}
