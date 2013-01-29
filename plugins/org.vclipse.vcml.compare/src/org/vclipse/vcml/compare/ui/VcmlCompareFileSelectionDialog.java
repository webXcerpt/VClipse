/*******************************************************************************
 * Copyright (c) 2013 webXcerpt Software GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    webXcerpt Software GmbH - initial creator - www.webxcerpt.com
 ******************************************************************************/
package org.vclipse.vcml.compare.ui;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ContainerSelectionDialog;
import org.eclipse.ui.ide.IDE;
import org.vclipse.vcml.compare.VcmlComparePlugin;

/**
 * Allows select files for comparison on vcml level. 
 */
public class VcmlCompareFileSelectionDialog extends TitleAreaDialog {

	private IContainer preselectedContainer;

	private Text parentContainerText;
	
	private Combo filterCombo;

	private String[] fileExtensions = new String[0];
	
	private TableViewer tableViewer;

	private IFile selectedFile;
	
	public VcmlCompareFileSelectionDialog(Shell parent, IContainer container) {
		super(parent);
		preselectedContainer = container;
	}
	
	public void setExtensions(String[] extensions) {
		if(extensions.length > 0) {
			fileExtensions = extensions;
		}
	}
	
	public IFile getSelection() {
		return selectedFile;
	}
	
	private void setFilter() {
		if(tableViewer != null) {
			tableViewer.setFilters(new ViewerFilter[]{new ViewerFilter() {
				public boolean select(Viewer viewer, Object parentElement, Object element) {
					if(element instanceof IFile && filterCombo.getSelectionIndex() > -1) {
						String fileExtension = ((IFile)element).getFileExtension();
							return fileExtension.equals(
									filterCombo.getItem(
										filterCombo.getSelectionIndex()).substring(2));
					}
					return false;
				}
			}});
		}
	}
	
	protected Control createDialogArea(Composite parent) {
		getShell().setText("File selection dialog");
		setTitle("Source file selection dialog");
		setMessage("Please select a VCML source file...");
		
		GridData gridData = new GridData(GridData.FILL_BOTH);
		Composite dialogArea = new Composite(parent, SWT.NONE);
		dialogArea.setLayout(new GridLayout(3, false));
		dialogArea.setLayoutData(gridData);
		
		Label label = new Label(dialogArea, SWT.NONE);
		label.setText("Parent container:");
		
		parentContainerText = new Text(dialogArea, SWT.BORDER | SWT.READ_ONLY);
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		parentContainerText.setLayoutData(gridData);
		
		Button button = new Button(dialogArea, SWT.PUSH);
		button.setText("Browse...");
		button.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
				Shell shell = Display.getDefault().getActiveShell();
				ContainerSelectionDialog dialog = new ContainerSelectionDialog(shell, root, false, "Please select a container.");
				if(Window.OK == dialog.open()) {
					Object[] result = dialog.getResult();
					IPath selectedPath = (IPath)result[0];
					IContainer container = (IContainer)root.findMember(selectedPath);
					if(container != null) { 
						parentContainerText.setText(container.getFullPath().toString() + "/");
						tableViewer.setInput(container);
					}
				}
			}
		});
		
		// initialise table viewer
		initTableViewer(dialogArea);
		
		Composite filterComposite = new Composite(dialogArea, SWT.NONE);
		filterComposite.setLayout(new GridLayout(2, false));
		gridData = new GridData();
		gridData.horizontalSpan = 3;
		filterComposite.setLayoutData(gridData);
		
		label = new Label(filterComposite, SWT.NONE);
		label.setText("Filter:");
		
		filterCombo = new Combo(filterComposite, SWT.BORDER | SWT.READ_ONLY);
		if(fileExtensions.length == 0) {
			filterCombo.setItems(new String[]{"*.*"});
		} else {
			filterCombo.setItems(fileExtensions);
			setFilter();
		}
		filterCombo.select(0);
		filterCombo.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				setFilter();
			}
		});
		
		if(preselectedContainer != null) {
			parentContainerText.setText(preselectedContainer.getFullPath().toString() + "/");
			tableViewer.setInput(preselectedContainer);
		} else {
			tableViewer.setInput(ResourcesPlugin.getWorkspace().getRoot());
		}
		return parent;
	}
	
	protected void initTableViewer(Composite parent) {
		tableViewer = new TableViewer(parent, SWT.CHECK | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		
		// init the content provider
		tableViewer.setContentProvider(new IStructuredContentProvider() {
			private IResource[] resources = new IResource[0];
			
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
				if(newInput instanceof IContainer && oldInput != newInput) {
					IContainer container = (IContainer)newInput;
					try {
						resources = container.members();
					} catch(CoreException exception) {
						resources = new IResource[0];
						Status status = new Status(IStatus.ERROR, VcmlComparePlugin.ID, exception.getMessage());
						VcmlComparePlugin.getInstance().getLog().log(status);
					}
				} else {
					resources = new IResource[0];
				}
			}
			
			public Object[] getElements(Object inputElement) {
				return resources;
			}
			
			public void dispose() {
				// not used
			}
		});
		
		// init the label provider
		tableViewer.setLabelProvider(new LabelProvider() {
			@Override
			public Image getImage(Object element) {
				ISharedImages sharedImages = PlatformUI.getWorkbench().getSharedImages();
				if(element instanceof IProject) {
					return sharedImages.getImage(IDE.SharedImages.IMG_OBJ_PROJECT);
				} 
				if(element instanceof IFolder) {
					return sharedImages.getImage(ISharedImages.IMG_OBJ_FOLDER);
				} 
				return sharedImages.getImage(ISharedImages.IMG_OBJ_FILE);
			}
			@Override
			public String getText(Object element) {
				return element instanceof IResource ? ((IResource)element).getName() : null;
			}
		});
		
		GridData gridData = new GridData(GridData.FILL_BOTH);
		gridData.horizontalSpan = 3;
		gridData.heightHint = 200;
		tableViewer.getTable().setLayoutData(gridData);
		tableViewer.getTable().addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				Table table = tableViewer.getTable();
				TableItem item = (TableItem)event.item;
				for(TableItem curItem : table.getItems()) {
					curItem.setChecked(false);
				}
				item.setChecked(true);
				selectedFile = (IFile)item.getData();
			}
		});
	}
}
