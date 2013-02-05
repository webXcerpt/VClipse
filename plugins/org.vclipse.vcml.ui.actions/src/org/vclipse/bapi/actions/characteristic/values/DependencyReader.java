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
import org.eclipse.emf.ecore.resource.Resource;
import org.vclipse.bapi.actions.JCoFunctionPerformer;
import org.vclipse.bapi.actions.constraint.ConstraintReader;
import org.vclipse.bapi.actions.handler.BAPIActionHandler;
import org.vclipse.bapi.actions.precondition.PreconditionReader;
import org.vclipse.bapi.actions.procedure.ProcedureReader;
import org.vclipse.bapi.actions.selectioncondition.SelectionConditionReader;
import org.vclipse.vcml.VCMLFactoryExtension;
import org.vclipse.vcml.VCMLUtilities;
import org.vclipse.vcml.utils.DependencySourceUtils;
import org.vclipse.vcml.vcml.Dependency;
import org.vclipse.vcml.vcml.Option;
import org.vclipse.vcml.vcml.VCObject;

import com.google.inject.Inject;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoParameterList;
import com.sap.conn.jco.JCoStructure;

/**
 * Common code for components reading dependencies.
 */
public abstract class DependencyReader extends BAPIActionHandler {

	@Inject
	protected ProcedureReader procedureReader;
	
	@Inject
	protected ConstraintReader constraintReader;
	
	@Inject
	protected PreconditionReader preconditionReader;
	
	@Inject
	protected SelectionConditionReader selectionConditionReader;
	
	@Inject
	protected JCoFunctionPerformer functionPerformer;
	
	@Inject
	protected VCMLFactoryExtension factoryExtension;
	
	@Inject
	protected VCMLUtilities vcmlUtilities;
	
	protected Dependency readDependency(String dependencyName, IProgressMonitor monitor, Resource resource, Map<String, VCObject> seenObjects, List<Option> localOptions, List<Option> globalOptions, boolean recurse) throws JCoException {
		JCoFunction dependencyTypeFunction = functionPerformer.CARD_DEPENDENCY_READ(dependencyName, monitor, localOptions, globalOptions);
		JCoParameterList epl = dependencyTypeFunction.getExportParameterList();
		JCoStructure dependencyData = epl.getStructure(JCoFunctionPerformer.DEPENDENCY_DATA);
		String dependencyType = dependencyData.getString(JCoFunctionPerformer.DEP_TYPE).toLowerCase();
		if(DependencySourceUtils.EXTENSION_PROCEDURE.equals(dependencyType)) {
			return procedureReader.read(dependencyName, resource, monitor, seenObjects, globalOptions, recurse);
		} 
		if(DependencySourceUtils.EXTENSION_CONSTRAINT.equals(dependencyType)) {
			return constraintReader.read(dependencyName, resource, monitor, seenObjects, globalOptions, recurse);
		} 
		if(DependencySourceUtils.EXTENSION_PRECONDITION.equals(dependencyType)) {
			return preconditionReader.read(dependencyName, resource, monitor, seenObjects, globalOptions, recurse);
		} 
		if(DependencySourceUtils.EXTENSION_SELECTIONCONDITION.equals(dependencyType)) {
			return selectionConditionReader.read(dependencyName, resource, monitor, seenObjects, globalOptions, recurse);
		}
		return null;
	}
}
