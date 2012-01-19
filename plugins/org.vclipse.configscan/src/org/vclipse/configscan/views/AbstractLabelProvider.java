package org.vclipse.configscan.views;

import java.util.List;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider.IStyledLabelProvider;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.xtext.ui.editor.utils.TextStyle;
import org.eclipse.xtext.ui.label.StylerFactory;
import org.vclipse.configscan.ConfigScanImageHelper;
import org.vclipse.configscan.impl.model.TestCase;
import org.vclipse.configscan.impl.model.TestRunAdapter;
import org.vclipse.configscan.impl.model.TestCase.Status;
import org.vclipse.configscan.utils.TestCaseUtility;

import com.google.inject.Inject;

public abstract class AbstractLabelProvider extends ColumnLabelProvider implements IStyledLabelProvider {

	protected static final String EMPTY = "";
	
	protected TestCaseUtility testCaseUtility;
	
	protected ConfigScanImageHelper imageHelper;
	
	protected TextStyle failureStyle;
	
	protected TextStyle successStyle;
	
	public AbstractLabelProvider() {
		failureStyle = new TextStyle();
		failureStyle.setColor(new RGB(0xcc, 0, 0));
				
		successStyle = new TextStyle();
		successStyle.setColor(new RGB(0x32, 0x92, 0));
	}
	
	@Inject
	public void setImageHelper(ConfigScanImageHelper imageHelper) {
		this.imageHelper = imageHelper;
	}
	
	@Inject
	public void setTestCaseUtility(TestCaseUtility testCaseUtility) {
		this.testCaseUtility = testCaseUtility;
	}

	protected EObject getReferencedEObject(Object element) {
		if(element instanceof TestCase) {
			TestCase testCase = (TestCase)element;
			TestCase root = testCaseUtility.getRoot(testCase);
			if(root != null) {
				Object adapter = root.getAdapter(TestRunAdapter.class);
				if(adapter != null) {
					EObject testModel = ((TestRunAdapter)adapter).getTestModel();
					if(testModel != null) {
						URI sourceUri = testCase.getSourceUri();
						if(sourceUri != null) {
							return testModel.eResource().getResourceSet().getEObject(sourceUri, true);							
						}
					}
				}
			}			
		}
		return null;
	}
	
	protected StyledString getStatistics(TestCase testCase) {
		List<TestCase> children = testCase.getChildren();
		if(!children.isEmpty()) {
			int failures = 0;
			for(TestCase childTestCase : testCase.getChildren()) {
				if(Status.FAILURE == childTestCase.getStatus()) {
					failures++;
				}
			}
			int numberOfTestCases = children.size();
			StyledString numberOfTests = new StyledString("     Number of tests = { " + numberOfTestCases + " } ");
			StylerFactory stylerFactory = new StylerFactory();
			numberOfTests.append(stylerFactory.createFromXtextStyle("Success = { " + (numberOfTestCases - failures) + " } ", successStyle));
			numberOfTests.append(new StylerFactory().createFromXtextStyle("Failures = { " + failures + " } ", failureStyle));
			return numberOfTests;
		}
		return new StyledString(EMPTY);
 	}

}
