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
package org.vclipse.configscan.views.actions;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.vclipse.base.ui.util.ClasspathAwareImageHelper;
import org.vclipse.configscan.ConfigScanPlugin;
import org.vclipse.configscan.IConfigScanImages;
import org.vclipse.configscan.impl.FailureTestObjectFilter;
import org.vclipse.configscan.impl.TestCaseAdapterFactory;
import org.vclipse.configscan.impl.TestCaseAdapterFactory.TestCaseAdapter;
import org.vclipse.configscan.impl.model.TestCase;
import org.vclipse.configscan.impl.model.TestCase.Status;
import org.vclipse.configscan.impl.model.TestGroup;
import org.vclipse.configscan.impl.model.TestRun;
import org.vclipse.configscan.views.ConfigScanView;
import org.vclipse.configscan.views.ConfigScanViewInput;

public final class RelaunchedFailedAction extends SimpleTreeViewerAction {

	public static final String ID = ConfigScanPlugin.ID + "." + RelaunchedFailedAction.class.getSimpleName();
	
	public RelaunchedFailedAction(ConfigScanView view, ClasspathAwareImageHelper imageHelper) {
		super(view, imageHelper);
		setText("Relaunched failed tests");
		setToolTipText("Relaunch failed tests");
		setImageDescriptor(imageHelper.getImageDescriptor(IConfigScanImages.RELAUNCHF));
		setId(ID);
	}

	@Override
	public void run() {
		Object object = treeViewer.getInput();
		if(object instanceof ConfigScanViewInput) {
			ConfigScanViewInput input = (ConfigScanViewInput)object;
			for(TestRun testRun : input.getTestRuns()) {
				testRun.setFilter(new FailureTestObjectFilter());
				EObject testModel = testRun.getTestModel();
				if(testModel == null) {
					continue;
				}
				handleChildren(testRun, testModel.eResource().getResourceSet());
				testRun.removeTestCases();
			}
			treeViewer.setInput(input);
		}
	}
	
	private void handleChildren(TestGroup testGroup, ResourceSet resourceSet) {
		for(TestCase testCase : testGroup.getTestCases()) {
			if(testCase instanceof TestGroup) {
				handleChildren((TestGroup)testCase, resourceSet);
			} else {
				TestGroup parent = (TestGroup)testCase.getParent();
				URI sourceUri = parent.getSourceURI();
				if(sourceUri != null) { 
					EObject eObject = resourceSet.getEObject(sourceUri, true);
					if(Status.FAILURE == testCase.getStatus()) {
						((TestCaseAdapter)TestCaseAdapterFactory.INSTANCE.adapt(eObject, TestCaseAdapter.class)).setTestCase(testCase);
					}
				}
				break;
			}
		}
	}
}
