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
package org.vclipse.idoc2jcoidoc

import org.eclipse.core.runtime.Status

class IDocSenderStatus extends Status {
	String upsNumber
	String sapSystem

	new(int severity) {
		this(severity, "")
	}

	new(int severity, String message) {
		super(severity, IDoc2JCoIDocPlugin.ID, message)
	}

	override void setMessage(String message) {
		super.setMessage(message)
	}

	def String getUpsNumber() {
		return upsNumber
	}

	def void setUpsNumber(String upsNumber) {
		this.upsNumber = upsNumber
	}

	def String getSapSystem() {
		return sapSystem
	}

	def void setSapSystem(String sapSystem) {
		this.sapSystem = sapSystem
	}

}
