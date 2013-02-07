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
package org.vclipse.bapi.actions;

import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.vclipse.vcml.vcml.VCObject;
import org.vclipse.vcml.vcml.VcmlModel;

/**
 *
 */
public interface IBAPIActionRunnerExtension <T> {
	
	public void run(T object, VcmlModel vcmlModel, IProgressMonitor monitor, Map<String, VCObject> seenObjects) throws Exception;

	public boolean enabled(T object);
}
