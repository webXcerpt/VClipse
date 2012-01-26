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
import org.vclipse.configscan.utils.TestCaseFactory;
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
//		Object object = treeViewer.getInput();
//		if(object instanceof ConfigScanViewInput) {
//			ConfigScanViewInput input = (ConfigScanViewInput)object;
//			for(TestCase testCase : input.getTestCases()) {
//				Object adapter = testCase.getAdapter(TestRunAdapter.class);
//				if(adapter != null) {
//					TestRunAdapter testRunAdapter = (TestRunAdapter)adapter;
//					testRunAdapter.setFilter(new FailureTestObjectFilter());
//					EObject testModel = testRunAdapter.getTestModel();
//					ResourceSet resourceSet = testModel.eResource().getResourceSet();
//					handleChildren(testCase, resourceSet);
//					testRunAdapter.getTestCase().clearChildren();
//				}
//			}
//			treeViewer.setInput(input);
//		}
	}
	
	private void handleChildren(TestCase testCase, ResourceSet resourceSet) {
//		for(TestCase childTestCase : testCase.getChildren()) {
//			// handle only test groups
//			if(!childTestCase.getChildren().isEmpty() && childTestCase.getParent().getAdapter(TestRunAdapter.class) == null) {
//				appendAdapter(childTestCase, resourceSet);
//			} else {
//				handleChildren(childTestCase, resourceSet);
//			}
//		}
	}
	
	private void appendAdapter(TestCase testCase, ResourceSet resourceSet) {
//		URI sourceUri = testCase.getSourceUri();
//		if(sourceUri != null) { 
//			EObject eObject = resourceSet.getEObject(sourceUri, true);
//			if(Status.FAILURE == testCase.getStatus()) {
//				((TestCaseAdapter)TestCaseAdapterFactory.INSTANCE.adapt(eObject, TestCaseAdapter.class)).setTestCase(testCase);
//			}
//		}
	}
}
