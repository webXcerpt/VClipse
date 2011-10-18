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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.util.Strings;
import org.vclipse.vcml.vcml.ConfigurationProfile;
import org.vclipse.vcml.vcml.ConfigurationProfileEntry;
import org.vclipse.vcml.vcml.DependencyNet;
import org.vclipse.vcml.vcml.InterfaceDesign;
import org.vclipse.vcml.vcml.Material;
import org.vclipse.vcml.vcml.Procedure;
import org.vclipse.vcml.utils.VCMLProxyFactory;
import org.vclipse.vcml.utils.VCMLUtils;

import com.sap.conn.jco.AbapException;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoParameterList;
import com.sap.conn.jco.JCoTable;

public class ConfigurationProfileReader extends BAPIUtils {

	private static final DependencyNetReader DEPENDENCYNET_READER = new DependencyNetReader();
	private static final ProcedureReader PROCEDURE_READER = new ProcedureReader();
	private static final InterfaceDesignReader INTERFACEDESIGN_READER = new InterfaceDesignReader();

	public void readAll(Material containerMaterial, Resource resource, IProgressMonitor monitor, Set<String> seenObjects, boolean recurse) throws JCoException {
		read(containerMaterial, null, resource, monitor, seenObjects, recurse);
	}
	
	public void read(Material containerMaterial, String profileName, Resource resource, IProgressMonitor monitor, Set<String> seenObjects, boolean recurse) throws JCoException {
		String materialName = containerMaterial.getName();
		if (!seenObjects.add("ConfigurationProfile#" + materialName)) {
			return;
		}
		JCoFunction function = getJCoFunction("CARD_CON_PROFILE_READ", monitor);
		function.getImportParameterList().setValue("OBJECT_TYPE", "MARA");
		JCoParameterList tpl = function.getTableParameterList();
		JCoTable conObjectKey = tpl.getTable("CON_OBJECT_KEY");
		conObjectKey.appendRow();
		conObjectKey.setValue("KEY_FELD", "MATNR");
		conObjectKey.setValue("KPARA_VALU", materialName); // TODO add additional material field to configuration profile // CWG: TSHIRT
		try {
			execute(function, monitor, materialName);

			JCoTable conProAttributes = tpl.getTable("CON_PRO_ATTRIBUTES");
			EList<ConfigurationProfile> configurationprofiles = containerMaterial.getConfigurationprofiles();
			Map<String, ConfigurationProfile> configurationprofilesByName = new HashMap<String, ConfigurationProfile>();
			for (int i = 0; i < conProAttributes.getNumRows(); i++) {
				conProAttributes.setRow(i);
				String name = conProAttributes.getString("C_PROFILE");
				if (profileName!=null && !profileName.equals(name)) { // wrong profile
					continue;
				}
				ConfigurationProfile object = VCML.createConfigurationProfile();
				object.setName(name);
				object.setStatus(VCMLUtils.createStatusFromInt(conProAttributes.getInt("STATUS")));
				object.setBomapplication(conProAttributes.getString("BOMAPPL"));
				// TODO System.out.println("Class type\t" + conProAttributes.getString("CLASSTYPE"));
				// TODO System.out.println("BOM explosion\t" + conProAttributes.getValue("BOMEXPL"));
				// TODO more attributes for configuration profiles?
				String design = conProAttributes.getString("DESIGN");
				if (!Strings.isEmpty(design)) {
					InterfaceDesign interfaceDesign = null;
					if (recurse) {
						interfaceDesign = INTERFACEDESIGN_READER.read(design, resource, monitor, seenObjects, recurse);
					}
					if (interfaceDesign==null) {
						interfaceDesign = VCMLProxyFactory.createInterfaceDesignProxy(resource, design);
					}
					object.setUidesign(interfaceDesign);
				}
				configurationprofiles.add(object);
				configurationprofilesByName.put(name, object);
			}
			
			JCoTable conProDependencyData = tpl.getTable("CON_PRO_DEPENDENCY_DATA");
			Map<String, ConfigurationProfileEntry> entriesByName = new HashMap<String, ConfigurationProfileEntry>();
			for (int i = 0; i < conProDependencyData.getNumRows(); i++) {
				conProDependencyData.setRow(i);
				
				ConfigurationProfile profile = configurationprofilesByName.get(conProDependencyData.getString("C_PROFILE"));
				if (profile==null) { // unknown profile
					continue;
				}
				String depType = conProDependencyData.getString("DEP_TYPE");
				String depName = conProDependencyData.getString("DEP_INTERN");
				if ("PROC".equals(depType)) {
					ConfigurationProfileEntry entry = VCML.createConfigurationProfileEntry();
					profile.getEntries().add(entry);
					entriesByName.put(depName, entry);
					Procedure procedure = null;
					if (recurse) {
						procedure = PROCEDURE_READER.read(depName, resource, monitor, seenObjects, recurse);
					}
					if (procedure==null) {
						procedure = VCMLProxyFactory.createProcedureProxy(resource, depName);
					}
					entry.setDependency(procedure);
				} else if ("CNET".equals(depType)) {
					DependencyNet dependencyNet = null;
					if (recurse) {
						dependencyNet = DEPENDENCYNET_READER.read(depName, resource, monitor, seenObjects, recurse);
					}
					if (dependencyNet==null) {
						dependencyNet = VCMLProxyFactory.createDependencyNetProxy(resource, depName);
					}
					profile.getDependencyNets().add(dependencyNet);
				} else {
					throw new IllegalArgumentException("unknown dependency type in configuration profile: " + depName + " of type " + depType);
				}
			}
			
			JCoTable conProDependencyOrder = tpl.getTable("CON_PRO_DEPENDENCY_ORDER");
			for (int i = 0; i < conProDependencyOrder.getNumRows(); i++) {
				conProDependencyOrder.setRow(i);
				ConfigurationProfileEntry entry = entriesByName.get(conProDependencyData.getString("DEP_INTERN"));
				if (entry!=null) {
					entry.setSequence(conProDependencyOrder.getInt("DEP_LINENO"));
				}
			}

		} catch (AbapException e) {
			handleAbapException(e);
		} 
	}

}
