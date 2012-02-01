package org.vclipse.configscan.views.actions;

import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.vclipse.configscan.ConfigScanImageHelper;
import org.vclipse.configscan.ConfigScanPlugin;
import org.vclipse.configscan.IConfigScanImages;
import org.vclipse.configscan.impl.model.TestCase;
import org.vclipse.configscan.impl.model.TestGroup;
import org.vclipse.configscan.utils.FailureTreeTraverser;
import org.vclipse.configscan.utils.TypeTreeTraverser;
import org.vclipse.configscan.views.ConfigScanView;

public final class PreviousAction extends SimpleTreeViewerAction implements IMenuCreator, SelectionListener {

	public static final String ID = ConfigScanPlugin.ID + "." + PreviousAction.class.getSimpleName();
	
	private Menu menu;
	
	//private static final int PREVIOUS_TEST_RUN = 1001;
	private static final int PREVIOUS_TEST_GROUP = 1002;
	private static final int PREVIOUS_TEST_CASE = 1003;
	private static final int PREVIOUS_FAILURE = 1004;
	
	private int selected;
	
	public PreviousAction(ConfigScanView view, ConfigScanImageHelper imageHelper) {
		super(view, imageHelper);
		setMenuCreator(this);
		selected = PREVIOUS_FAILURE;
		setText("Show previous item");
		setImageDescriptor(imageHelper.getImageDescriptor(IConfigScanImages.SELECT_PREV));
		setToolTipText("Jump to previous item");
		setId(ID);
	}

	@Override
	public void run() {
		IStructuredSelection selection = (IStructuredSelection)treeViewer.getSelection();
		if(!selection.isEmpty()) {
			Object firstSelected = selection.getFirstElement();
			if(firstSelected instanceof TestCase) {
				TestCase testCase = (TestCase)firstSelected;
				TestCase nextNode =  null;
				switch (selected) {
				case PREVIOUS_FAILURE:
					nextNode = new FailureTreeTraverser().getPreviousItem(testCase);
					break;
//				case PREVIOUS_TEST_RUN:
//					nextNode = new TypeTreeTraverser(TestRun.class).getPreviousItem(testCase);
//					break;
				case PREVIOUS_TEST_GROUP:
					nextNode = new TypeTreeTraverser(TestGroup.class).getPreviousItem(testCase);
					break;
				case PREVIOUS_TEST_CASE:
					nextNode = new TypeTreeTraverser(TestCase.class).getPreviousItem(testCase);
					break;
				default:
					break;
				}
				if(nextNode != null) {
					treeViewer.setSelection(new StructuredSelection(nextNode));
				}
			}
		}
	}

	public void widgetSelected(SelectionEvent event) {
		selected = ((MenuItem)event.getSource()).getID();
	}

	public void widgetDefaultSelected(SelectionEvent event) {
		// not used
	}

	public void dispose() {
		if(menu != null) {
			menu.dispose();
			menu = null;
		}
	}

	public Menu getMenu(Control parent) {
		if(menu != null) {
			menu.dispose();
		}

		menu = new Menu(parent);

//		MenuItem item = new MenuItem(menu, SWT.RADIO);
//		item.setText("Previous test run");
//		item.setID(PREVIOUS_TEST_RUN);
//		item.addSelectionListener(this);
//		if(selected == PREVIOUS_TEST_RUN) {
//			item.setSelection(true);
//		}

		MenuItem item = new MenuItem(menu, SWT.RADIO);
		item.setText("Previous test group");
		item.setID(PREVIOUS_TEST_GROUP);
		item.addSelectionListener(this);
		if(selected == PREVIOUS_TEST_GROUP) {
			item.setSelection(true);
		}

		item = new MenuItem(menu, SWT.RADIO);
		item.setText("Previous test case");
		item.setID(PREVIOUS_TEST_CASE);
		item.addSelectionListener(this);
		if(selected == PREVIOUS_TEST_CASE) {
			item.setSelection(true);
		}

		item = new MenuItem(menu, SWT.RADIO);
		item.setText("Previous failure");
		item.setID(PREVIOUS_FAILURE);
		item.addSelectionListener(this);
		if(selected == PREVIOUS_FAILURE) {
			item.setSelection(true);
		}

		return menu;
	}

	public Menu getMenu(Menu parent) {
		return null;
	}
}
