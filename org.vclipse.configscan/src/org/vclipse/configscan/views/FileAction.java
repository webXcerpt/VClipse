package org.vclipse.configscan.views;


import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.xtext.util.Files;


/** This class is for the File-Menu drop-down.
 * 
 * @author kulig
 *
 */
public class FileAction extends Action implements IMenuCreator {
	private Menu fMenu;
	private Composite parent;
	private TreeViewer viewer;
	private Labels labels;
	
	public FileAction(Composite parent, TreeViewer viewer, Labels labels) {
		this.parent = parent;
		this.viewer = viewer;
		this.labels = labels;
		setMenuCreator(this);
		setText("File");
		setToolTipText("File menu");
		
		
	}
	
	@Override
	public void dispose() {
		if(fMenu != null) {
			fMenu.dispose();
			fMenu = null;
		}
	}

	@Override
	public Menu getMenu(Control parent) {
		if(fMenu != null) {
			fMenu.dispose();
		}
		fMenu = new Menu(parent);
		
		
		MenuItem saveAs = new MenuItem(fMenu, SWT.PUSH | SWT.Deactivate);
		saveAs.setText("Save ConfigScan log (XML) as...");
		saveAs.addSelectionListener(new SaveAsSelectionListener());
		
		MenuItem importXml = new MenuItem(fMenu, SWT.PUSH);
		importXml.setText("Import ConfigScan log (XML)");
		importXml.addSelectionListener(new ImportXmlSelectionListener());
		
		
				
		return fMenu;
		
	}

	@Override
	public Menu getMenu(Menu parent) {
		return null;
	}

	
	public class ImportXmlSelectionListener implements SelectionListener {

		@Override
		public void widgetSelected(SelectionEvent e) {
			FileDialog fd = new FileDialog(parent.getShell(), SWT.OPEN);
			fd.setText("XML File Dialog");
			System.err.println("Trying to show FileDialog");
			String path = fd.open();
			if(path == null) {
				// Cancelled
			}
			else {
				String content = Files.readFileIntoString(path);
				viewer.setContentProvider(new ViewContentProvider(content));
				viewer.setLabelProvider(new ViewLabelProvider());			// we have no map
				
				viewer.refresh();
				viewer.expandToLevel(Config.EXPAND_LEVEL);
				int runs = ((ViewContentProvider) viewer.getContentProvider()).getNumberOfRuns();
				int successes = ((ViewContentProvider) viewer.getContentProvider()).getNumberOfSuccess();
				int failures = ((ViewContentProvider) viewer.getContentProvider()).getNumberOfFailure();
				int time = 0;
				labels.updateLabels(runs, failures, successes, time);
			}

		}

		@Override
		public void widgetDefaultSelected(SelectionEvent e) {

		}

	};
	
	public class SaveSelectionListener implements SelectionListener {

		@Override
		public void widgetSelected(SelectionEvent e) {
			
		}

		@Override
		public void widgetDefaultSelected(SelectionEvent e) {

		}

	};
	
	public class SaveAsSelectionListener implements SelectionListener {

		@Override
		public void widgetSelected(SelectionEvent e) {
			FileDialog fd = new FileDialog(parent.getShell(), SWT.SAVE);
			fd.setText("XML File Dialog");
			System.err.println("Trying to show FileDialog");
			String path = fd.open();
			if(path == null) {
				// Cancelled
			}
			else {
				String content = new XmlLoader().parseXmlToString(((ViewContentProvider) viewer.getContentProvider()).getLogDocument());
				Files.writeStringIntoFile(path, content);
			}
		}

		@Override
		public void widgetDefaultSelected(SelectionEvent e) {

		}

	};
	
	
}
