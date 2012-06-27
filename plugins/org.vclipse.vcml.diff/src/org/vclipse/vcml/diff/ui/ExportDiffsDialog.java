/**
 * 
 */
package org.vclipse.vcml.diff.ui;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.vclipse.vcml.diff.compare.ExtractDifferencesJob;

import com.google.inject.Inject;

public class ExportDiffsDialog extends TitleAreaDialog {

	private IFile oldFile;
	private IFile newFile;
	
	private IFile resultFile;
	
	private Text oldFileText;
	private Text newFileText;
	private Text resultsFileText;
	
	private Button generateExportFileButton;
	private Button switchPathsButton;
	
	private IWorkspaceRoot root;
	
	@Inject
	private ExtractDifferencesJob job;
	
	public ExportDiffsDialog() {
		super(Display.getCurrent().getActiveShell());
		this.root = ResourcesPlugin.getWorkspace().getRoot();
	}
	
	public void setOldFile(IFile file) {
		oldFile = file;
	}
	
	public void setNewFile(IFile file) {
		newFile = file;
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		if(oldFile == null) {
			throw new IllegalArgumentException("oldFile might not be null");
		}
		
		final FileSelectionDialog fileSelectionDialog = 
				new FileSelectionDialog(parent.getShell(), oldFile.getParent());
		fileSelectionDialog.setExtensions(new String[]{"*.vcml"});
		
		getShell().setText("Compare 2 files with each other");
		setTitle("VcmlCompare Dialog");
		setMessage("One can compare 2 files with vcml extension and extract differences to a third file");
		
		Composite mainArea = new Composite(parent, SWT.NONE);
		mainArea.setLayout(new GridLayout());
		mainArea.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		Group group = new Group(mainArea, SWT.NONE);
		group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		group.setText("VcmlCompare sources");
		group.setLayout(new GridLayout(3, false));
		
		Label label = new Label(group, SWT.NONE);
		label.setText("Compare file (old)");
		
		oldFileText = new Text(group, SWT.BORDER | SWT.READ_ONLY);
		oldFileText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		oldFileText.setSize(300, 0);
		oldFileText.setText(oldFile.getFullPath().toString());
		
		Button button = new Button(group, SWT.PUSH);
		button.setText("Browse...");
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				if(Dialog.OK == fileSelectionDialog.open()) {
					oldFile = fileSelectionDialog.getSelection();
					if(oldFile != null) {
						oldFileText.setText(oldFile.getFullPath().toString());
						validateEntries();
					}
				}
			}
		});
		
		label = new Label(group, SWT.NONE);
		label.setText("... with file (new):");
		GridData gridData = new GridData();
		gridData.horizontalAlignment = SWT.END;
		label.setLayoutData(gridData);
		
		newFileText = new Text(group, SWT.BORDER | SWT.READ_ONLY);
		newFileText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		if(newFile != null) {
			newFileText.setText(newFile.getFullPath().toString());
		}
		
		button = new Button(group, SWT.PUSH);
		button.setText("Browse...");
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				if(Dialog.OK == fileSelectionDialog.open()) {
					newFile = fileSelectionDialog.getSelection();
					if(newFile != null) {
						newFileText.setText(newFile.getFullPath().toString());
						validateEntries();
					}
				}
			}
		});
		
		group = new Group(mainArea, SWT.NONE);
		group.setText("Options");
		group.setLayout(new GridLayout(4, false));
		group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		label = new Label(group, SWT.NONE);
		label.setText("Export diffs to file:");
		
		resultsFileText = new Text(group, SWT.BORDER);
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 2;
		resultsFileText.setLayoutData(gridData);
		resultsFileText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent event) {
				String text = resultsFileText.getText();
				try {
					setErrorMessage(null);
					resultFile = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(text));
				} catch(IllegalArgumentException exception) {
					setErrorMessage(exception.getMessage());
				}
			}
		});
		
		final Button browseExportPathButton = new Button(group, SWT.PUSH);
		browseExportPathButton.setText("Browse...");
		
		generateExportFileButton = new Button(group, SWT.CHECK);
		generateExportFileButton.setText("Generate file for export");
		gridData = new GridData();
		gridData.horizontalSpan = 2;
		generateExportFileButton.setLayoutData(gridData);
		generateExportFileButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				boolean enabled = ((Button)event.widget).getSelection();
				browseExportPathButton.setEnabled(!enabled);
				resultsFileText.setEnabled(!enabled);
				validateEntries();
			}
		});
		
		// TODO implement the click-handling for the browseExportPathButton and remove the following 3 lines
		generateExportFileButton.setEnabled(false);
		generateExportFileButton.setSelection(true);
		browseExportPathButton.setEnabled(false);
		resultsFileText.setEditable(false);
		
		switchPathsButton = new Button(group, SWT.NONE);
		switchPathsButton.setText("Switch comparison paths");
		switchPathsButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				String tmpTextFirst = oldFileText.getText();
				oldFileText.setText(newFileText.getText());
				newFileText.setText(tmpTextFirst);
				validateEntries();
			}
		});
		gridData = new GridData();
		gridData.horizontalSpan = 2;
		gridData.horizontalAlignment = SWT.END;
		switchPathsButton.setLayoutData(gridData);
		
		validateEntries();
		return super.createDialogArea(parent);
	}

	@Override
	protected void buttonPressed(int buttonId) {
		if(Dialog.OK == buttonId) {
			if(oldFile != null && newFile != null) {
				job.setNewFile(newFile);
				job.setOldFile(oldFile);
				job.setResultFile(resultFile);
				job.schedule();
			}
		}
		super.buttonPressed(buttonId);
	}

	private void validateEntries() {
		setErrorMessage(null);
		switchPathsButton.setEnabled(true);
		
		try {
			oldFile = root.getFile(new Path(oldFileText.getText()));
		} catch(IllegalArgumentException exception) {
			oldFile = null;
			switchPathsButton.setEnabled(false);
			setErrorMessage("Please specify the first file for comparison.");
			return;
		}
		
		try {
			newFile = root.getFile(new Path(newFileText.getText()));
		} catch(IllegalArgumentException exception) {
			newFile = null;
			switchPathsButton.setEnabled(false);
			setErrorMessage("Please specify the second file for comparison.");
			return;
		}
		
		// create a name for the result file and provide this value to the text widget
		if(generateExportFileButton.getSelection() && oldFile != null) {
			String newName = newFile.getName().replaceAll(".vcml", "_diff.vcml");
			resultFile = newFile.getParent().getFile(new Path(newName));
			resultsFileText.setText(resultFile.getFullPath().toString());
		} else {
			try {
				resultFile = root.getFile(new Path(resultsFileText.getText()));
			} catch(IllegalArgumentException exception) {
				resultFile = null;
				setErrorMessage("Please specify a file for the differences export");
			}
		}
	}
}
