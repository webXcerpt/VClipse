package org.vclipse.configscan.views.actions;

import java.util.List;

import org.eclipse.jface.action.ContributionItem;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.vclipse.configscan.impl.model.TestCase;
import org.vclipse.configscan.impl.model.TestGroup;
import org.vclipse.configscan.impl.model.TestRun;
import org.vclipse.configscan.views.ConfigScanViewInput;

public class SearchContributionItem extends ContributionItem {
	
	private TreeViewer viewer;
	
	private TestCase foundTestCase;
	
	public SearchContributionItem(TreeViewer viewer) {
		this.viewer = viewer;
	}
	
	public void fill(Composite parent) {
		Composite mainComposite = new Composite(parent, SWT.NONE);
		GridLayout gridLayout = new GridLayout();
		gridLayout.marginHeight = 3;
		gridLayout.numColumns = 2;
		mainComposite.setLayout(gridLayout);
		
		new Label(mainComposite, SWT.NONE).setText("Search: ");
		
		FontData[]  fdata = parent.getDisplay().getSystemFont().getFontData();
		final Text searchText = new Text(mainComposite, SWT.BORDER);
		searchText.setFont(new Font(parent.getDisplay(), new FontData(fdata[0].getName(), 8, SWT.NORMAL)));
		
		GridData gridData = new GridData();
		gridData.verticalAlignment = GridData.BEGINNING;
		gridData.heightHint = 10;
		
		searchText.setLayoutData(gridData);
		searchText.setTextLimit(50);
		searchText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent event) {
				String searchForText = searchText.getText();
				if(!searchForText.isEmpty()) {
					Object input = viewer.getInput();
					if(input instanceof ConfigScanViewInput) {
						for(TestRun testRun : ((ConfigScanViewInput)input).getTestRuns()) {
							String title = testRun.getTitle();
							if(title.startsWith(searchForText) || title.contains(searchForText)) {
								foundTestCase = testRun;
								break;
							} 
							foundTestCase = searchFor(testRun.getTestCases(), searchForText);
							if(foundTestCase == null) {
								continue;
							} else {
								viewer.setSelection(new StructuredSelection(foundTestCase));
							}
						}
					}
				}
			}	
		});
		
		searchText.addFocusListener(new FocusAdapter() {
			public void focusGained(FocusEvent event) {
				searchText.setText("");
			}
		});
	}
	
	private TestCase searchFor(List<TestCase> testCases, String text) {
		for(TestCase testCase : testCases) {
			String title = testCase.getTitle();
			if(title.startsWith(text) || title.contains(text)) {
				return testCase;
			} else if(testCase instanceof TestGroup) {
				TestCase foundTestCase = searchFor(((TestGroup)testCase).getTestCases(), text);
				if(foundTestCase != null) {
					return foundTestCase;
				} else {
					continue;
				}
			}
		}
		return null;
	}
}