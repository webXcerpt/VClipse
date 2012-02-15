package org.vclipse.configscan.imports;

import java.io.File;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ContainerSelectionDialog;
import org.eclipse.ui.dialogs.FilteredResourcesSelectionDialog;

import com.google.common.collect.Lists;

class WizardPage extends org.eclipse.jface.wizard.WizardPage implements IWizardPage {

	private Text folderText;
	private Text fileText;
	private Text modelFileText;
	
	private boolean overwrite = false;
	private boolean opentargetfile = false;
	
	private IWorkspaceRoot wroot;
	
	private IContainer targetContainer;
	
	private IFile targetFile;
	private IFile modelFile;
	
	private IStructuredSelection selection;
	
	// Table widgets
	private Table table;
	private TableViewer tableViewer;
	
	private ContentProvider contentProvider;
	private LabelProvider labelProvider;
	
	private IConfigScanImportTransformation transformation;

	protected WizardPage(String pageName, IStructuredSelection selection, IConfigScanImportTransformation transformation) {
		super(pageName);
		wroot = ResourcesPlugin.getWorkspace().getRoot();
		this.selection = selection;
		this.transformation = transformation;
	}

	public void createControl(Composite parent) {
		Composite mainArea = new Composite(parent, SWT.NONE);
		mainArea.setLayout(new GridLayout(3, false));
		
		Label label = new Label(mainArea, SWT.NONE);
		label.setText("Source/model file(*." + transformation.getReferencedModelExtension() + "):");
		
		modelFileText = new Text(mainArea, SWT.BORDER | SWT.READ_ONLY);
		modelFileText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				validatePage();
			}
		});
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		modelFileText.setLayoutData(gridData);
		
		Button button = new Button(mainArea, SWT.PUSH);
		button.setText("Browse...");
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				modelFile = null;
				FilteredResourcesSelectionDialog frsd = 
						new FilteredResourcesSelectionDialog(getShell(), false, 
								targetContainer == null ? wroot : targetContainer, IResource.FILE);
				if(Window.OK == frsd.open()) {
					Object result = frsd.getFirstResult();
					if(result instanceof IFile) {
						modelFile = (IFile)result;
						modelFileText.setText(modelFile.getFullPath().toString());
						
						// default values
						IContainer container = modelFile.getParent();
						if(container instanceof IProject) {
							targetContainer = ((IProject)container).getFolder("Testcases");
						} else {
							targetContainer = ((IFolder)container).getFolder("Testcases");
						}
						if(!targetContainer.exists()) {
							try {
								((IFolder)targetContainer).create(true, true, new NullProgressMonitor());
							} catch (CoreException e1) {
								e1.printStackTrace();
							}
						} 
						folderText.setText(targetContainer == null ? "" : targetContainer.getFullPath().toString());
						fileText.setText(modelFile.getName().replace(transformation.getReferencedModelExtension(), 
								transformation.getTargetModelExtension()));
					}
				}
			}
		});
		
		label = new Label(mainArea, SWT.NONE);
		label.setText("Target folder:");
		
		folderText = new Text(mainArea, SWT.READ_ONLY | SWT.BORDER);
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		folderText.setLayoutData(gridData);
		folderText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				validatePage();
			}
		});
		
		button = new Button(mainArea, SWT.PUSH);
		button.setText("Browse...");
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				ContainerSelectionDialog csd = 
						new ContainerSelectionDialog(getShell(), wroot, 
								false, "Please specify a container(project or folder) for " + transformation.getTargetModelExtension() + " test cases.");
				csd.showClosedProjects(false);
				if(Window.OK == csd.open()) {
					Object[] result = csd.getResult();
					if(result[0] instanceof IPath) {
						IResource resource = wroot.findMember((IPath)result[0]);
						targetContainer = resource instanceof IContainer && resource.isAccessible() ? (IContainer)resource : null;
						folderText.setText(targetContainer == null ? "" : targetContainer.getFullPath().toString());
					}
				}
			}
		});
		
		label = new Label(mainArea, SWT.NONE);
		label.setText("Target file name(*." + transformation.getTargetModelExtension() + "):");
		
		fileText = new Text(mainArea, SWT.BORDER);
		fileText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				validatePage();				
			}
		});
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 2;
		fileText.setLayoutData(gridData);
		
		createTableArea(mainArea);
		
		button = new Button(mainArea, SWT.CHECK);
		button.setText("Overwrite existing target file");
		button.setSelection(true);
		overwrite = true;
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				overwrite = ((Button)e.widget).getSelection();
				validatePage();
			}
		});
		gridData = new GridData();
		gridData.horizontalSpan = 3;
		button.setLayoutData(gridData);
		
		button = new Button(mainArea, SWT.CHECK);
		button.setText("Open target file (" + transformation.getTargetModelExtension() + ") on finish");
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				opentargetfile = ((Button)e.widget).getSelection();
			}
		});
		gridData = new GridData();
		gridData.horizontalSpan = 3;
		button.setLayoutData(gridData);
		
		setControl(mainArea);
		
		// validate the selection: assuming 2 cases 
			// 	- selection contains a target container
			// 	- selection contains files for transformation 
		Object firstElement = selection.getFirstElement();
		if(firstElement instanceof IContainer) {
			targetContainer = (IContainer)firstElement;
			folderText.setText(targetContainer.getFullPath().toString());
		} else if(firstElement instanceof IFile) {
			tableViewer.setInput(selection);
			checkAllItems(true);
		}
		validatePage();
	}

	private void createTableArea(Composite mainArea) {
		Group selectedFilesGroup = new Group(mainArea, SWT.NONE);
		selectedFilesGroup.setText("Files for transformation");
		selectedFilesGroup.setLayout(new GridLayout(2, false));
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 3;
		selectedFilesGroup.setLayoutData(gridData);
		
		tableViewer = new TableViewer(selectedFilesGroup, SWT.BORDER | SWT.CHECK 
				| SWT.FULL_SELECTION | SWT.V_SCROLL | SWT.H_SCROLL);
		table = tableViewer.getTable(); 
		table.setHeaderVisible(false);
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.verticalSpan = 5;
		gridData.heightHint = 150;
		gridData.widthHint = 150;
		table.setLayoutData(gridData);
		
		contentProvider = new ContentProvider();
		tableViewer.setContentProvider(contentProvider);
		labelProvider = new LabelProvider();
		tableViewer.setLabelProvider(labelProvider);
		table.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				validatePage();
			}
		});
		
		Button button = new Button(selectedFilesGroup, SWT.PUSH);
		button.setText("Add");
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog fd = new FileDialog(getShell(), SWT.OPEN);
				fd.setFilterExtensions(new String[]{"*.xml", "*.cfg"});
				if(targetContainer == null) {
					fd.setFilterPath(wroot.getLocation().toString());	
				} else {
					fd.setFilterPath(targetContainer.getLocation().toString());
				}
				String result = fd.open();	
				if(result != null) {
					tableViewer.add(new File(result));
				}
				validatePage();
			}
		});
		gridData = new GridData();
		gridData.widthHint = 80;
		button.setLayoutData(gridData);
		
		button = new Button(selectedFilesGroup, SWT.PUSH);
		button.setText("Remove");
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection strSelection = (IStructuredSelection)tableViewer.getSelection();
				if(!strSelection.isEmpty()) {
					tableViewer.remove(strSelection.getFirstElement());
				}
				validatePage();
			}
		});
		gridData = new GridData();
		gridData.widthHint = 80;
		button.setLayoutData(gridData);
		
		button = new Button(selectedFilesGroup, SWT.PUSH);
		button.setText("Check all");
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				checkAllItems(true);
			}
		});
		gridData = new GridData();
		gridData.widthHint = 80;
		button.setLayoutData(gridData);
		
		button = new Button(selectedFilesGroup, SWT.PUSH);
		button.setText("Uncheck all");
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				checkAllItems(false);
			}
		});
		gridData = new GridData();
		gridData.widthHint = 80;
		button.setLayoutData(gridData);
	}
	
	private void checkAllItems(boolean check) {
		for(TableItem ti : table.getItems()) {
			ti.setChecked(check);
		}
		validatePage();
	}
	
	public IContainer getTargetContainer() {
		return targetContainer;
	}
	
	public IFile getExportLocation() {
		return targetFile;
	}
	
	public IFile getSourceLocation() {
		return modelFile;
	}
	
	public List<File> getFilesToTransform() {
		List<File> files2Transform = Lists.newArrayList();
		for(TableItem tableItem : tableViewer.getTable().getItems()) {
			if(tableItem.getChecked()) {
				files2Transform.add((File)tableItem.getData());
			}
		}
		return files2Transform;
	}
	
	public boolean overwriteExistigTargetFile() {
		return overwrite;
	}
	
	public boolean openTargetFile() {
		return opentargetfile;
	}
	
	private boolean isSelectionEmpty() {
		Table table = tableViewer.getTable();
		for(TableItem tableItem : table.getItems()) {
			if(tableItem.getChecked()) {
				return false;
			}
		}
		return true;
	}
	
	private void validatePage() {
		setPageComplete(false);
		targetContainer = null;
		targetFile = null;
		
		String folderString = folderText.getText();
		String fileString = fileText.getText();
		IResource resource = wroot.findMember(folderString);
		
		if(modelFile == null) {
			setErrorMessage("Please specify a model file referenced by the tests.");
		} else if(folderString.isEmpty()) {
			setErrorMessage("Please select a folder where to place the files containing the " + transformation.getReferencedModelExtension() + " test cases");
		} else if(fileString.isEmpty()) {
			setErrorMessage("Please specify a file name for " + transformation.getTargetModelExtension() + " test cases");
		} else if(resource instanceof IContainer) {
			targetContainer = (IContainer)resource;
			targetFile = targetContainer.getFile(new Path(fileString));
			if(!isValidFileName(targetFile)) {
				setErrorMessage("Target file should have \"" + transformation.getTargetModelExtension() +"\" file extension");
			} else if(targetFile.exists() && !overwrite) {
				setErrorMessage("Target file already exists. Please enable the overwrite option.");
			} else if(isSelectionEmpty()) {
				setErrorMessage("Please select a file/files for the transformation.");
			} else {
				setErrorMessage(null);
				setDescription("Please push the finish button to run the transformation.");
				setPageComplete(true);
			}
		}
	}
	
	private boolean isValidFileName(IFile file) {
		String fileExtension = file.getFileExtension();
		return fileExtension == null ? false : fileExtension.equals(transformation.getTargetModelExtension());
	}
}