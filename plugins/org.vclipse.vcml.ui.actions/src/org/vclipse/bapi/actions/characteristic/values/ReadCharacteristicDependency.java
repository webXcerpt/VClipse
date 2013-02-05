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
package org.vclipse.bapi.actions.characteristic.values;

import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.vclipse.bapi.actions.JCoFunctionPerformer;
import org.vclipse.bapi.actions.handler.BAPIActionHandler;
import org.vclipse.vcml.vcml.Option;
import org.vclipse.vcml.vcml.VCObject;
import org.vclipse.vcml.vcml.VcmlModel;

import com.google.inject.Inject;
import com.sap.conn.jco.JCoException;

import org.vclipse.vcml.vcml.Characteristic;

/**
 * Reads dependency objects used by characteristics.
 */
public class ReadCharacteristicDependency extends BAPIActionHandler {

	@Inject
	private JCoFunctionPerformer functionPerformer;
	
	/**
	 * 
	 */
	public void read(Characteristic cstic, VcmlModel vcmlModel, final IProgressMonitor monitor, Map<String, VCObject> seenObjects, List<Option> globalOptions, boolean recurse) throws JCoException {
		if(monitor.isCanceled()) {
			return;
		}
		StringBuffer messageBuffer = new StringBuffer("Extracting procedures for values of the characteristic ").append(cstic.getName());
		SubMonitor submonitor = SubMonitor.convert(monitor, messageBuffer.toString(), IProgressMonitor.UNKNOWN);
		
		// TODO implement
		
		submonitor.done();
	}
}
