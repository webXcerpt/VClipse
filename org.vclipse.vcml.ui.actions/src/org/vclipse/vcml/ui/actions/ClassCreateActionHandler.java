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

public class ClassCreateActionHandler extends AbstractClassCreateChangeActionHandler {
	
	@Override public String getAction() { return "CREATE"; };
	@Override public String getBAPI() { return "BAPI_CLASS_CREATE"; };
	@Override public String getCLASSNUM() { return "CLASSNUMNEW"; };
	@Override public String getCLASSTYPE() { return "CLASSTYPENEW"; };
	@Override public String getCLASSBASICDATA() { return "CLASSBASICDATA"; };
	@Override public String getCLASSDESCRIPTIONS() { return "CLASSDESCRIPTIONS"; };
	@Override public String getCLASSCHARACTERISTICS() { return "CLASSCHARACTERISTICS"; };

}
