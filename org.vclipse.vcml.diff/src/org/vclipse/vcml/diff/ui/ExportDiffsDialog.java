/**
 * 
 */
package org.vclipse.vcml.diff.ui;

import java.util.Iterator;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
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
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.vclipse.vcml.diff.ExportDiffsOperation;

/**
 *
 */
public class ExportDiffsDialog extends TitleAreaDialog {

	/**
	 * 
	 */
	private static final IWorkspaceRoot ROOT = ResourcesPlugin.getWorkspace().getRoot();
	
	/**
	 * 
	 */
	private static FileSelectionDialog FILE_SELECTION_DIALOG;
	
	/**
	 * 
	 */
	private IFile firstComparisonFile;
	
	/**
	 * 
	 */
	private IFile secondComparisonFile;
	
	/**
	 * 
	 */
	private IFile exportComparisonFile;
	
	/**
	 * 
	 */
	private Text firstPathText;
	
	/**
	 * 
	 */
	private Text secondPathText;
	
	/**
	 * 
	 */
	private Text thirdPathText;
	
	/**
	 *	
	 */
	private Button generateExportFileButton;
	
	/**
	 * 
	 */
	private Button switchPathsButton;
	
	/**
	 *	Resources, being preselected.
	 *
	 *	Use case 1 - user preselected a container.
	 *	Use case 2 - user preselected two source files.
	 *	Use case 3 - nothing is preselected, array is empty in this case.
	 */
	private IResource[] preselectedResources = new IResource[2];
	
	/**
	 * 
	 */
	public ExportDiffsDialog(final Shell parentShell, final IStructuredSelection selection) {
		super(parentShell);
		if(!selection.isEmpty()) {
			Iterator<?> iterator = selection.iterator();
			for(int i=0; i<2 && iterator.hasNext(); i++) {
				Object object = iterator.next();
				if(object instanceof IResource) {
					preselectedResources[i] = (IResource)object;
				}
			}
		}
		
		if(preselectedResources[0] instanceof IContainer) {
			FILE_SELECTION_DIALOG = new FileSelectionDialog(parentShell, (IContainer)preselectedResources[0]);			
		} else if(preselectedResources[0] instanceof IFile) {
			FILE_SELECTION_DIALOG = new FileSelectionDialog(parentShell, ((IFile)preselectedResources[0]).getParent());
		} else {
			FILE_SELECTION_DIALOG = new FileSelectionDialog(parentShell, ROOT);
		}
		FILE_SELECTION_DIALOG.setExtensions(new String[]{"*.vcml"});
	}

	/**
	 * @see org.eclipse.jface.dialogs.TitleAreaDialog#createDialogArea(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected Control createDialogArea(final Composite parent) {
		getShell().setText("Compare 2 files with each other");
		setTitle("Comparison Dialog");
		setMessage("One can compare 2 files with vcml extension and extract differences to a third file");
		
		final Composite mainArea = new Composite(parent, SWT.NONE);
		mainArea.setLayout(new GridLayout());
		mainArea.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		Group group = new Group(mainArea, SWT.NONE);
		group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		group.setText("Comparison sources");
		group.setLayout(new GridLayout(3, false));
		
		Label label = new Label(group, SWT.NONE);
		label.setText("Compare file ");
		
		firstPathText = new Text(group, SWT.BORDER | SWT.READ_ONLY);
		firstPathText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		firstPathText.setSize(300, 0);
	
		if(preselectedResources[0] instanceof IFile) {
			firstPathText.setText(((IFile)preselectedResources[0]).getFullPath().toString());
		}
		
		Button button = new Button(group, SWT.PUSH);
		button.setText("Browse...");
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent event) {
				if(Dialog.OK == FILE_SELECTION_DIALOG.open()) {
					firstComparisonFile = FILE_SELECTION_DIALOG.getSelection();
					if(firstComparisonFile != null) {
						firstPathText.setText(firstComparisonFile.getFullPath().toString());
						validateEntries();
					}
				}
			}
		});
		
		label = new Label(group, SWT.NONE);
		label.setText("... with file:");
		GridData gridData = new GridData();
		gridData.horizontalAlignment = SWT.END;
		label.setLayoutData(gridData);
		
		secondPathText = new Text(group, SWT.BORDER | SWT.READ_ONLY);
		secondPathText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		if(preselectedResources[1] instanceof IFile) {
			secondPathText.setText(((IFile)preselectedResources[1]).getFullPath().toString());
		}
		
		button = new Button(group, SWT.PUSH);
		button.setText("Browse...");
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent event) {
				if(Dialog.OK == FILE_SELECTION_DIALOG.open()) {
					secondComparisonFile = FILE_SELECTION_DIALOG.getSelection();
					if(secondComparisonFile != null) {
						secondPathText.setText(secondComparisonFile.getFullPath().toString());
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
		
		thirdPathText = new Text(group, SWT.BORDER);
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 2;
		thirdPathText.setLayoutData(gridData);
		thirdPathText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(final ModifyEvent event) {
				String text = thirdPathText.getText();
				try {
					setErrorMessage(null);
					exportComparisonFile = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(text));
				} catch(final IllegalArgumentException exception) {
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
			public void widgetSelected(final SelectionEvent event) {
				boolean enabled = ((Button)event.widget).getSelection();
				browseExportPathButton.setEnabled(!enabled);
				thirdPathText.setEnabled(!enabled);
				validateEntries();
			}
		});
		
		// TODO implement the click-handling for the browseExportPathButton and remove the following 3 lines
		generateExportFileButton.setEnabled(false);
		generateExportFileButton.setSelection(true);
		browseExportPathButton.setEnabled(false);
		thirdPathText.setEditable(false);
		
		switchPathsButton = new Button(group, SWT.NONE);
		switchPathsButton.setText("Switch comparison paths");
		switchPathsButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent event) {
				final String tmpTextFirst = firstPathText.getText();
				firstPathText.setText(secondPathText.getText());
				secondPathText.setText(tmpTextFirst);
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

	/**
	 * @see org.eclipse.jface.dialogs.Dialog#buttonPressed(int)
	 */
	@Override
	protected void buttonPressed(final int buttonId) {
		if(Dialog.OK == buttonId) {
			if(firstComparisonFile != null && secondComparisonFile != null) {
				ExportDiffsOperation operation = new ExportDiffsOperation(firstComparisonFile, secondComparisonFile, exportComparisonFile);
				try {
					ResourcesPlugin.getWorkspace().run(operation, new NullProgressMonitor());
				} catch (CoreException e) {
				}
			}
			//System.out.println("Run compare operation");
		}
		super.buttonPressed(buttonId);
	}

	/**
	 *	Validation for the entries in the dialog, error messages are added and removed here. 
	 */
	private void validateEntries() {
		setErrorMessage(null);
		switchPathsButton.setEnabled(true);
		
		try {
			firstComparisonFile = ROOT.getFile(new Path(firstPathText.getText()));
		} catch(final IllegalArgumentException exception) {
			firstComparisonFile = null;
			switchPathsButton.setEnabled(false);
			setErrorMessage("Please specify the first file for comparison.");
			return;
		}
		
		try {
			secondComparisonFile = ROOT.getFile(new Path(secondPathText.getText()));
		} catch(final IllegalArgumentException exception) {
			secondComparisonFile = null;
			switchPathsButton.setEnabled(false);
			setErrorMessage("Please specify the second file for comparison.");
			return;
		}
		
		if(generateExportFileButton.getSelection() && firstComparisonFile != null) {
			String newName = firstComparisonFile.getName().replaceAll(".vcml", "_diff.vcml");
			exportComparisonFile = firstComparisonFile.getParent().getFile(new Path(newName));
			thirdPathText.setText(exportComparisonFile.getFullPath().toString());
		} else {
			try {
				exportComparisonFile = ROOT.getFile(new Path(thirdPathText.getText()));
			} catch(final IllegalArgumentException exception) {
				exportComparisonFile = null;
				setErrorMessage("Please specify a file for the differences export");
			}
		}
	}
}
