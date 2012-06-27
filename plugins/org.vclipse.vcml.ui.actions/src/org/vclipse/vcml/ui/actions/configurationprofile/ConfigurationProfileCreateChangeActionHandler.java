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
package org.vclipse.vcml.ui.actions.configurationprofile;

import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.ecore.resource.Resource;
import org.vclipse.vcml.ui.actions.BAPIUtils;
import org.vclipse.vcml.ui.outline.actions.IVcmlOutlineActionHandler;
import org.vclipse.vcml.vcml.ConfigurationProfile;
import org.vclipse.vcml.vcml.ConfigurationProfileEntry;
import org.vclipse.vcml.vcml.DependencyNet;
import org.vclipse.vcml.vcml.InterfaceDesign;
import org.vclipse.vcml.vcml.Material;
import org.vclipse.vcml.vcml.Option;
import org.vclipse.vcml.utils.VcmlUtils;

import com.sap.conn.jco.AbapException;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoParameterList;
import com.sap.conn.jco.JCoTable;

public class ConfigurationProfileCreateChangeActionHandler extends BAPIUtils implements IVcmlOutlineActionHandler<ConfigurationProfile> {

	@Override
	public boolean isEnabled(ConfigurationProfile object) {
		return isConnected();
	}

	@Override
	public void run(ConfigurationProfile object, Resource resource,	IProgressMonitor monitor, Set<String> seenObjects, List<Option> options) throws JCoException {
		String materialNumber = ((Material)object.eContainer()).getName();
		JCoFunction function = getJCoFunction("CAMA_CON_PROFILE_MAINTAIN", monitor);
		JCoParameterList ipl = function.getImportParameterList();
		ipl.setValue("OBJECT_TYPE", "MARA");
		
		handleOptions(options, ipl, null, null);
		
		JCoParameterList tpl = function.getTableParameterList();
		JCoTable conObjectKey = tpl.getTable("CON_OBJECT_KEY");
		conObjectKey.appendRow();
		conObjectKey.setValue("KEY_FELD", "MATNR");
		conObjectKey.setValue("KPARA_VALU", materialNumber); // TODO add additional material field to configuration profile // CWG: TSHIRT
		
		JCoTable conProAttributes = tpl.getTable("CON_PRO_ATTRIBUTES");
		conProAttributes.appendRow();
		String profileName = object.getName();
		conProAttributes.setValue("C_PROFILE", profileName);
		conProAttributes.setValue("CLASSTYPE", 300);
		conProAttributes.setValue("STATUS", VcmlUtils.createIntFromStatus(object.getStatus()));
		conProAttributes.setValue("BOMAPPL", object.getBomapplication());
		InterfaceDesign uidesign = object.getUidesign();
		if (uidesign!=null) {
			conProAttributes.setValue("DESIGN", uidesign.getName());
		}
		
		JCoTable conProDependencyData = tpl.getTable("CON_PRO_DEPENDENCY_DATA");
		JCoTable conProDependencyOrder = tpl.getTable("CON_PRO_DEPENDENCY_ORDER");
		for(DependencyNet dependencyNet : object.getDependencyNets()) {
			conProDependencyData.appendRow();
			conProDependencyOrder.appendRow();
			conProDependencyData.setValue("C_PROFILE", profileName);
			conProDependencyData.setValue("DEP_INTERN", dependencyNet.getName());
			conProDependencyOrder.setValue("C_PROFILE", profileName);
			conProDependencyOrder.setValue("DEP_LINENO", 0);
		}
		for(ConfigurationProfileEntry entry : object.getEntries()) {
			conProDependencyData.appendRow();
			conProDependencyOrder.appendRow();
			conProDependencyData.setValue("C_PROFILE", profileName);
			conProDependencyData.setValue("DEP_INTERN", entry.getDependency().getName());
			conProDependencyOrder.setValue("C_PROFILE", profileName);
			conProDependencyOrder.setValue("DEP_INTERN", entry.getDependency().getName());
			conProDependencyOrder.setValue("DEP_LINENO", entry.getSequence());
		}
		
		try {
			execute(function, monitor, profileName + " " + materialNumber);
		} catch (AbapException e) {
			handleAbapException(e);
		} 
	}

}
