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
