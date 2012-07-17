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
package org.vclipse.bapi.actions.configurationprofile;

import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.ecore.resource.Resource;
import org.vclipse.bapi.actions.BAPIUtils;
import org.vclipse.bapi.actions.IBAPIActionRunner;
import org.vclipse.vcml.vcml.ConfigurationProfile;
import org.vclipse.vcml.vcml.Option;

import com.sap.conn.jco.JCoException;

public class ConfigurationProfileDeleteActionHandler extends BAPIUtils implements IBAPIActionRunner<ConfigurationProfile> {

	@Override
	public boolean isEnabled(ConfigurationProfile object) {
		return isConnected();
	}

	@Override
	public void run(ConfigurationProfile object, Resource resource,	IProgressMonitor monitor, Set<String> seenObjects, List<Option> options) throws JCoException {
		errorStream.println("// " + this.getClass().getSimpleName() + " not yet implemented");
	}

}
