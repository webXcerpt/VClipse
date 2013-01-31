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

import org.eclipse.emf.ecore.EObject;
import org.vclipse.configscan.imports.AbstractConfigScanTransformationWizard;
import org.vclipse.configscan.imports.IConfigScanImportTransformation;
import org.vclipse.configscan.vcmlt.vcmlT.Model;
import org.vclipse.configscan.vcmlt.vcmlT.VcmlTFactory;

public abstract class Abstract2VcmlTImportWizard extends AbstractConfigScanTransformationWizard {

	public Abstract2VcmlTImportWizard(IConfigScanImportTransformation transformation) {
		super(transformation);
	}
	
	protected EObject createTargetModel() {
		VcmlTFactory vcmltFactory = VcmlTFactory.eINSTANCE;
		Model vcmltModel = vcmltFactory.createModel();
		vcmltModel.setTestcase(vcmltFactory.createTestCase());
		return vcmltModel;
	}

}
