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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.util.Strings;
import org.vclipse.bapi.actions.BAPIUtils;
import org.vclipse.bapi.actions.dependencynet.DependencyNetReader;
import org.vclipse.bapi.actions.interfacedesign.InterfaceDesignReader;
import org.vclipse.bapi.actions.procedure.ProcedureReader;
import org.vclipse.vcml.utils.VCMLObjectUtils;
import org.vclipse.vcml.utils.VCMLProxyFactory;
import org.vclipse.vcml.utils.VcmlUtils;
import org.vclipse.vcml.vcml.ConfigurationProfile;
import org.vclipse.vcml.vcml.ConfigurationProfileEntry;
import org.vclipse.vcml.vcml.DependencyNet;
import org.vclipse.vcml.vcml.InterfaceDesign;
import org.vclipse.vcml.vcml.Material;
import org.vclipse.vcml.vcml.Option;
import org.vclipse.vcml.vcml.Procedure;
import org.vclipse.vcml.vcml.VcmlModel;

import com.google.inject.Inject;
import com.sap.conn.jco.AbapException;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoParameterList;
import com.sap.conn.jco.JCoTable;

public class ConfigurationProfileReader extends BAPIUtils {

	@Inject
	private DependencyNetReader dependencyNetReader;
	
	@Inject
	private ProcedureReader procedureReader;
	
	@Inject
	private InterfaceDesignReader interfaceDesignReader;

	public void readAll(Material containerMaterial, Resource resource, IProgressMonitor monitor, Set<String> seenObjects, List<Option> options, boolean recurse) throws JCoException {
		read(containerMaterial, containerMaterial.getName(), resource, monitor, seenObjects, options, recurse);
	}
	
	public void read(Material containerMaterial, String profileName, Resource resource, IProgressMonitor monitor, Set<String> seenObjects, List<Option> options, boolean recurse) throws JCoException {
		VcmlModel model = (VcmlModel)resource.getContents().get(0);
		String materialName = containerMaterial.getName();
		if(materialName == null || !seenObjects.add("ConfigurationProfile/" + materialName.toUpperCase())) {
			return;
		}
//		String fixing = containerMaterial.getConfigurationprofiles().get(0).getFixing().getLiteral();
//		if (object.getFixing() != Fixing.NONE) {
//			fixing = object.getFixing().getLiteral();
//		}
		JCoFunction function = getJCoFunction("CARD_CON_PROFILE_READ", monitor);
		JCoParameterList ipl = function.getImportParameterList();
		ipl.setValue("OBJECT_TYPE", "MARA");
		
		handleOptions(options, ipl, "CHANGE_NO", "DATE");
		
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
				object.setStatus(VcmlUtils.createStatusFromInt(conProAttributes.getInt("STATUS")));
				object.setBomapplication(conProAttributes.getString("BOMAPPL"));
				object.setFixing(VcmlUtils.createFixingFromInt(conProAttributes.getInt("OB_FIX")));
				// TODO System.out.println("Class type\t" + conProAttributes.getString("CLASSTYPE"));
				// TODO System.out.println("BOM explosion\t" + conProAttributes.getValue("BOMEXPL"));
				// TODO more attributes for configuration profiles?
				String design = conProAttributes.getString("DESIGN");
				if (!Strings.isEmpty(design)) {
					InterfaceDesign interfaceDesign = null;
					if (recurse) {
						if(monitor.isCanceled()) {
							return;
						}
						interfaceDesign = interfaceDesignReader.read(design, model, monitor, seenObjects, options, recurse);
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
			JCoTable conProDependencyOrder = tpl.getTable("CON_PRO_DEPENDENCY_ORDER");
			Map<String, ConfigurationProfileEntry> entriesByName = new HashMap<String, ConfigurationProfileEntry>();
			for (int i = 0; i < conProDependencyData.getNumRows(); i++) {
				conProDependencyData.setRow(i);
				
				String cProfile = conProDependencyData.getString("C_PROFILE");
				ConfigurationProfile profile = configurationprofilesByName.get(cProfile);
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
						if(monitor.isCanceled()) {
							return;
						}
						procedure = procedureReader.read(depName, resource, monitor, seenObjects, options, recurse);
					}
					if (procedure==null) {
						procedure = VCMLProxyFactory.createProcedureProxy(resource, depName);
					}
					entry.setSequence(getSequenceNumber(conProDependencyOrder, cProfile, depName));
					entry.setDependency(procedure);
				} else if ("CNET".equals(depType)) {
					DependencyNet dependencyNet = null;
					if (recurse) {
						if(monitor.isCanceled()) {
							return;
						}
						dependencyNet = dependencyNetReader.read(depName, model, monitor, seenObjects, options, recurse);
					}
					if (dependencyNet==null) {
						dependencyNet = VCMLProxyFactory.createDependencyNetProxy(resource, depName);
					}
					profile.getDependencyNets().add(dependencyNet);
				} else {
					throw new IllegalArgumentException("unknown dependency type in configuration profile: " + depName + " of type " + depType);
				}
				VCMLObjectUtils.sortDependencyNets(profile.getDependencyNets());
				VCMLObjectUtils.sortEntries(profile.getEntries());
			}
		} catch (AbapException e) {
			handleAbapException(e);
		} 
	}

	private int getSequenceNumber(JCoTable tOrder, String profileName, String depName) {
		for (int i = 0; i < tOrder.getNumRows(); i++) {
			tOrder.setRow(i);
			if (Strings.equal(profileName,tOrder.getString("C_PROFILE")) && 
				Strings.equal(depName, tOrder.getString("DEP_INTERN"))) {
				return tOrder.getInt("DEP_LINENO");
			}
		}
		return 0;
	}

}
