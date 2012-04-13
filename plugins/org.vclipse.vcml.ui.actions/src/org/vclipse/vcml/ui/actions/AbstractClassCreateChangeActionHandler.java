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

import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.ecore.resource.Resource;
import org.vclipse.vcml.ui.outline.actions.IVCMLOutlineActionHandler;
import org.vclipse.vcml.vcml.Characteristic;
import org.vclipse.vcml.vcml.Class;
import org.vclipse.vcml.vcml.Language;
import org.vclipse.vcml.utils.DescriptionHandler;
import org.vclipse.vcml.utils.VcmlUtils;

import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoParameterList;
import com.sap.conn.jco.JCoStructure;
import com.sap.conn.jco.JCoTable;

public abstract class AbstractClassCreateChangeActionHandler extends BAPIUtils implements IVCMLOutlineActionHandler<Class> {

	protected abstract String getAction();
	protected abstract String getBAPI();
	protected abstract String getCLASSNUM();
	protected abstract String getCLASSTYPE();
	protected abstract String getCLASSBASICDATA();
	protected abstract String getCLASSDESCRIPTIONS();
	protected abstract String getCLASSCHARACTERISTICS();

	public boolean isEnabled(Class object) {
		return isConnected();
	}

	public void run(Class object, Resource resource, IProgressMonitor monitor, Set<String> seenObjects) throws JCoException {
		beginTransaction();
		JCoFunction function = getJCoFunction(getBAPI(), monitor);	
		JCoParameterList ipl = function.getImportParameterList();
		String classSpec = object.getName();
		String className = VcmlUtils.getClassName(classSpec);
		int classType = VcmlUtils.getClassType(classSpec);
		ipl.setValue(getCLASSNUM(), className);
		ipl.setValue(getCLASSTYPE(), classType);
		JCoStructure classBasicDataNew = ipl.getStructure(getCLASSBASICDATA());
		classBasicDataNew.setValue("STATUS", VcmlUtils.createIntFromStatus(object.getStatus()));
		classBasicDataNew.setValue("CLASSGROUP", nullIfEmpty(object.getGroup()));
		classBasicDataNew.setValue("VALID_FROM", getToday()); // TODO set VALID_FROM for classes?
		classBasicDataNew.setValue("VALID_TO", "9999-12-31"); // TODO set VALID_TO for classes?
		JCoParameterList tpl = function.getTableParameterList();
		final JCoTable classDescriptionsNew = tpl.getTable(getCLASSDESCRIPTIONS());
		new DescriptionHandler() {
			@Override
			public void handleSingleDescription(Language language, String value) {
				classDescriptionsNew.appendRow();
				classDescriptionsNew.setValue("CATCHWORD", value);
				classDescriptionsNew.setValue("LANGU", VcmlUtils.getLanguageCharacter(language));
				classDescriptionsNew.setValue("LANGU_ISO", language.toString());
			}
		}.handleDescription(object.getDescription());
		JCoTable classCharacteristicsNew = tpl.getTable(getCLASSCHARACTERISTICS());
		List<Characteristic> cstics = object.getCharacteristics();
		classCharacteristicsNew.appendRows(cstics.size());
		for(Characteristic cstic : cstics) {
			classCharacteristicsNew.setValue("NAME_CHAR", cstic.getName());
			classCharacteristicsNew.nextRow();
		}
		execute(function, monitor, object.getName());
		if (processReturnTable(function)) {
			commit(monitor);
		}
		endTransaction();
	}
	
}

