/*******************************************************************************
 * Copyright (c) 2010 webXcerpt Software GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     webXcerpt Software GmbH - initial creator
 ******************************************************************************/
package org.vclipse.vcml.ui.actions;

public class ClassChangeActionHandler extends AbstractClassCreateChangeActionHandler {
	
	@Override public String getAction() { return "CHANGE"; };
	@Override public String getBAPI() { return "BAPI_CLASS_CHANGE"; };
	@Override public String getCLASSNUM() { return "CLASSNUM"; };
	@Override public String getCLASSTYPE() { return "CLASSTYPE"; };
	@Override public String getCLASSBASICDATA() { return "CLASSBASICDATANEW"; };
	@Override public String getCLASSDESCRIPTIONS() { return "CLASSDESCRIPTIONSNEW"; };
	@Override public String getCLASSCHARACTERISTICS() { return "CLASSCHARACTERISTICSNEW"; };

}
