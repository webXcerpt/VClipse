package org.vclipse.configscan.views.actions;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.vclipse.configscan.ConfigScanImageHelper;
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

public class RelaunchedFailedAction extends SimpleTreeViewerAction {

	public RelaunchedFailedAction(ConfigScanView view, ConfigScanImageHelper imageHelper) {
		super(view, imageHelper);
		setText("Relaunched failed tests");
		setToolTipText("Relaunch failed tests");
		setImageDescriptor(imageHelper.getImageDescriptor(IConfigScanImages.RELAUNCHF));
	}

	@Override
	public void run() {
		Object object = treeViewer.getInput();
		if(object instanceof ConfigScanViewInput) {
			ConfigScanViewInput input = (ConfigScanViewInput)object;
			for(TestRun testRun : input.getTestRuns()) {
				testRun.setFilter(new FailureTestObjectFilter());
				EObject testModel = testRun.getTestModel();
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
