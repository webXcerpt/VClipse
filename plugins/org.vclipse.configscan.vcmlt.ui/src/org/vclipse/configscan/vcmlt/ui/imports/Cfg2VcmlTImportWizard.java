/*******************************************************************************
 * Copyright (c) 2010 - 2013 webXcerpt Software GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *  
 * Contributors:
 *     	webXcerpt Software GmbH - initial creator and others
 * 		www.webxcerpt.com
 ******************************************************************************/
package org.vclipse.configscan.vcmlt.ui.imports;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbench;
import org.vclipse.configscan.imports.IConfigScanImportTransformation;

import com.google.inject.Inject;
import com.google.inject.name.Named;

public class Cfg2VcmlTImportWizard extends Abstract2VcmlTImportWizard {

	@Inject
	public Cfg2VcmlTImportWizard(@Named("Cfg2VcmlTImportTransformation") IConfigScanImportTransformation transformation) {
		super(transformation);
	}
	
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		super.init(workbench, selection);
		setWindowTitle("Import wizard for VCMLT test cases from IPC export (*.cfg or *.xml) files");
	}

	protected String getTaskString() {
		return "Transforming cfg/xml files to VCMLT files...";
	}
	
	
}
