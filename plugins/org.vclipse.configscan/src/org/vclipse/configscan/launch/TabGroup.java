/*******************************************************************************
 * Copyright (c) 2012 webXcerpt Software GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    webXcerpt Software GmbH - initial creator
 ******************************************************************************/
package org.vclipse.configscan.launch;

import org.eclipse.debug.ui.AbstractLaunchConfigurationTabGroup;
import org.eclipse.debug.ui.ILaunchConfigurationDialog;
import org.eclipse.debug.ui.ILaunchConfigurationTab;

import com.google.inject.Inject;

public class TabGroup extends AbstractLaunchConfigurationTabGroup {

	//@Inject
	//private ManyConnectionsTab manyConnectionsTab;
	
	@Inject
	private OneConnectionTab oneConnectionTab;
	
	@Override
	public void createTabs(ILaunchConfigurationDialog dialog, String mode) {
		setTabs(
				new ILaunchConfigurationTab[] {
						oneConnectionTab
						//manyConnectionsTab
						});
	}
}
