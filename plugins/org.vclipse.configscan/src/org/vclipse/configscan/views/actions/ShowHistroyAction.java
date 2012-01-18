package org.vclipse.configscan.views.actions;

import java.util.List;

import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.vclipse.configscan.ConfigScanImageHelper;
import org.vclipse.configscan.IConfigScanImages;
import org.vclipse.configscan.views.ConfigScanViewInput;
import org.vclipse.configscan.views.TestRunsHistory;

public class ShowHistroyAction extends SimpleTreeViewerAction implements IMenuCreator, SelectionListener {

	private Menu menu;
	
	private TestRunsHistory history;
	
	public ShowHistroyAction(TreeViewer treeViewer, ConfigScanImageHelper imageHelper, TestRunsHistory history) {
		super(treeViewer, imageHelper);
		setMenuCreator(this);
		setImageDescriptor(imageHelper.getImageDescriptor(IConfigScanImages.HISTORY));
		setText("Test run history");
		setToolTipText("Test run history");
		this.history = history;
	}

	@Override
	public void dispose() {
		if(menu != null) {
			menu.dispose();
			menu = null;
		}
	}

	@Override
	public Menu getMenu(Control parent) {
		if(menu != null) {
			menu.dispose();
		}
		menu = new Menu(parent);
		List<ConfigScanViewInput> historyEntries = history.getHistory();
		for(ConfigScanViewInput input : historyEntries) {
			MenuItem item = new MenuItem(menu, SWT.PUSH);
			item.setText(input.getConfigurationName() + " at " + input.getDate());
			item.setID(historyEntries.indexOf(input));
			item.addSelectionListener(this);
		}
		return menu;
	}

	@Override
	public Menu getMenu(Menu parent) {
		return null;
	}

	@Override
	public void widgetSelected(SelectionEvent event) {
		MenuItem item = (MenuItem)event.getSource();
		int id = item.getID();
		ConfigScanViewInput configScanViewInput = history.getHistory().get(id);
		treeViewer.setInput(configScanViewInput);
	}

	@Override
	public void widgetDefaultSelected(SelectionEvent e) {
		// not used
	}
}
