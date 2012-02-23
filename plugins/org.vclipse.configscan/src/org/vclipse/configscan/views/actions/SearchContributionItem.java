package org.vclipse.configscan.views.actions;

import java.util.List;

import org.eclipse.jface.action.ContributionItem;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
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

import com.google.common.collect.Lists;

public class SearchContributionItem extends ContributionItem {
	
	private TreeViewer viewer;
	
	private Text searchTextWidget;
	
	private TextFilter textFilter = new TextFilter();
	
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
		searchTextWidget = new Text(mainComposite, SWT.BORDER);
		searchTextWidget.setFont(new Font(parent.getDisplay(), new FontData(fdata[0].getName(), 8, SWT.NORMAL)));
		
		GridData gridData = new GridData();
		gridData.verticalAlignment = GridData.BEGINNING;
		gridData.heightHint = 10;
		
		searchTextWidget.setLayoutData(gridData);
		searchTextWidget.setTextLimit(50);
		searchTextWidget.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent event) {
				String text = searchTextWidget.getText();
				if(text.isEmpty()) {
					viewer.removeFilter(textFilter);
				} else {
					textFilter.setSearchText(text);
					List<ViewerFilter> list = Lists.asList(textFilter, viewer.getFilters());
					viewer.setFilters(list.toArray(new ViewerFilter[list.size()]));
				}
			}	
		});
	}
}

class TextFilter extends ViewerFilter {

	private String text;

	public void setSearchText(String text) {
		this.text = text;
	}
	
	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		if(element instanceof TestGroup) {
			return search(((TestGroup)element).getTestCases(), text);
		} else if(element instanceof TestCase) {
			String title = ((TestCase)element).getTitle();
			return title.startsWith(text) || title.contains(text) || title.equals(text);
		}
		return true;
	}
	
	private boolean search(List<TestCase> testCases, String text) {
		for(TestCase testCase : testCases) {
			String title = testCase.getTitle();
			if(title.startsWith(text) || title.contains(text)) {
				return true;
			} else if(testCase instanceof TestGroup) {
				return search(((TestGroup)testCase).getTestCases(), text);
			} else {
				continue;
			}
		}
		return false;
	}
}