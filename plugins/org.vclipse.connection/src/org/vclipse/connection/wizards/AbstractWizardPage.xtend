/** 
 * Copyright (c) 2010 - 2013 webXcerpt Software GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * webXcerpt Software GmbH - initial creator
 * www.webxcerpt.com
 */
package org.vclipse.connection.wizards

import java.io.File
import java.util.ArrayList
import java.util.List
import org.eclipse.jface.dialogs.Dialog
import org.eclipse.jface.resource.ImageDescriptor
import org.eclipse.jface.viewers.DoubleClickEvent
import org.eclipse.jface.viewers.IDoubleClickListener
import org.eclipse.jface.viewers.IStructuredSelection
import org.eclipse.jface.viewers.TableViewer
import org.eclipse.jface.window.Window
import org.eclipse.jface.wizard.IWizardPage
import org.eclipse.jface.wizard.WizardPage
import org.eclipse.swt.SWT
import org.eclipse.swt.events.ModifyEvent
import org.eclipse.swt.events.ModifyListener
import org.eclipse.swt.events.SelectionAdapter
import org.eclipse.swt.events.SelectionEvent
import org.eclipse.swt.layout.GridData
import org.eclipse.swt.layout.GridLayout
import org.eclipse.swt.widgets.Button
import org.eclipse.swt.widgets.Composite
import org.eclipse.swt.widgets.FileDialog
import org.eclipse.swt.widgets.Label
import org.eclipse.swt.widgets.Table
import org.eclipse.swt.widgets.TableColumn
import org.eclipse.swt.widgets.TableItem
import org.eclipse.swt.widgets.Text
import org.vclipse.connection.VClipseConnectionPlugin
import org.vclipse.connection.internal.AbstractConnection
import org.vclipse.connection.internal.CreateEditConnectionDialog

/** 
 */
abstract class AbstractWizardPage extends WizardPage implements IWizardPage {
	/** 
	 */
	protected Label targetFileLabel
	/** 
	 */
	protected Composite fileArea
	/** 
	 */
	protected Text targetFileText
	/** 
	 */
	protected File targetFile
	/** 
	 */
	protected Composite connectionsArea
	/** 
	 */
	protected final List<AbstractConnection> connections
	/** 
	 */
	protected TableViewer tableViewer

	/** 
	 * @param pageName
	 */
	protected new(String pageName) {
		this(pageName, null, null)
	}

	/** 
	 * @param pageName
	 * @param title
	 * @param titleImage
	 */
	protected new(String pageName, String title, ImageDescriptor titleImage) {
		super(pageName, title, titleImage)
		connections = new ArrayList<AbstractConnection>()
	}

	/** 
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	override void createControl(Composite parent) {
		val Composite mainArea = new Composite(parent, SWT::NONE)
		mainArea.setLayout(new GridLayout())
		fileArea = new Composite(mainArea, SWT::NONE)
		fileArea.setLayout(new GridLayout(3, false))
		fileArea.setLayoutData(new GridData(GridData::FILL_HORIZONTAL))
		targetFileLabel = new Label(fileArea, SWT::NONE)
		targetFileLabel.setText("")
		targetFileText = new Text(fileArea, SWT::BORDER.bitwiseOr(SWT::READ_ONLY))
		targetFileText.setLayoutData(new GridData(GridData::FILL_HORIZONTAL))
		targetFileText.addModifyListener(([ModifyEvent event|handleTargetFileTextModified(event)] as ModifyListener))
		var Button button = new Button(fileArea, SWT::PUSH)
		button.setText("Browse...")
		button.addSelectionListener(new SelectionAdapter() {
			override void widgetSelected(SelectionEvent event) {
				handleBrowseForTargetFileButtonPushed()
			}
		})
		connectionsArea = new Composite(mainArea, SWT::NONE)
		connectionsArea.setLayout(new GridLayout(2, false))
		connectionsArea.setLayoutData(new GridData(GridData::FILL_HORIZONTAL))
		val Label label = new Label(connectionsArea, SWT::NONE)
		label.setText("Available connections:")
		var GridData gridData = new GridData()
		gridData.horizontalSpan = 2
		label.setLayoutData(gridData)
		tableViewer = new TableViewer(connectionsArea,
			SWT::V_SCROLL.bitwiseOr(SWT::H_SCROLL).bitwiseOr(SWT::CHECK).bitwiseOr(SWT::SINGLE).bitwiseOr(SWT::BORDER).
				bitwiseOr(SWT::FULL_SELECTION))
		val Table table = tableViewer.getTable()
		table.setLinesVisible(true)
		table.setHeaderVisible(true)
		table.addSelectionListener(new SelectionAdapter() {
			override void widgetSelected(SelectionEvent event) {
				handleTableSelection(event)
			}
		})
		tableViewer.addDoubleClickListener((
			[DoubleClickEvent event|handleTableDoubleClicked(event)] as IDoubleClickListener))
		var TableColumn item = new TableColumn(table, SWT::NONE)
		item.setText("")
		item.setWidth(20)
		item = new TableColumn(table, SWT::LEFT)
		item.setText("System name")
		item.setWidth(150)
		item = new TableColumn(table, SWT::LEFT)
		item.setText("Server name")
		item.setWidth(150)
		item = new TableColumn(table, SWT::LEFT)
		item.setText("System number")
		item.setWidth(150)
		tableViewer.setContentProvider(new ContentProvider())
		tableViewer.setLabelProvider(new LabelProvider())
		gridData = new GridData()
		gridData.verticalSpan = 6
		gridData.heightHint = 200
		table.setLayoutData(gridData)
		gridData = new GridData(GridData::FILL_HORIZONTAL)
		gridData.widthHint = 100
		button = new Button(connectionsArea, SWT::PUSH)
		button.setText("Create")
		button.addSelectionListener(new SelectionAdapter() {
			override void widgetSelected(SelectionEvent event) {
				handleCreateButtonPushed()
			}
		})
		button.setLayoutData(gridData)
		disableButton(button)
		button = new Button(connectionsArea, SWT::PUSH)
		button.setText("Check all")
		button.addSelectionListener(new SelectionAdapter() {
			override void widgetSelected(SelectionEvent event) {
				handleCheckAllButtonPushed()
			}
		})
		button.setLayoutData(gridData)
		disableButton(button)
		button = new Button(connectionsArea, SWT::PUSH)
		button.setText("Uncheck all")
		button.addSelectionListener(new SelectionAdapter() {
			override void widgetSelected(SelectionEvent event) {
				handleUncheckAllButtonPushed()
			}
		})
		button.setLayoutData(gridData)
		disableButton(button)
		button = new Button(connectionsArea, SWT::PUSH)
		button.setText("Remove")
		button.addSelectionListener(new SelectionAdapter() {
			override void widgetSelected(SelectionEvent event) {
				handleRemoveButtonPushed()
			}
		})
		button.setLayoutData(gridData)
		disableButton(button)
		setControl(mainArea)
		validatePage()
	}

	/** 
	 * @param event
	 */
	def protected void handleTargetFileTextModified(ModifyEvent event) {
		targetFile = new File((event.widget as Text).getText())
	}

	/** 
	 */
	def protected void handleBrowseForTargetFileButtonPushed() {
		val FileDialog dialog = new FileDialog(fileArea.getShell(), SWT::OPEN)
		dialog.setFilterExtensions((#["*.ini"] as String[]))
		val String absolutePath = dialog.open()
		if (absolutePath === null || absolutePath.isEmpty()) {
			targetFile = null
		} else {
			targetFileText.setText(absolutePath)
		}
		validatePage()
	}

	/** 
	 */
	def protected void handleCreateButtonPushed() {
		val CreateEditConnectionDialog dialog = new CreateEditConnectionDialog(getShell(), null)
		if (Window::OK === dialog.open()) {
			tableViewer.add(dialog.getNewSAPConnection())
		}

	}

	/** 
	 */
	def protected void handleCheckAllButtonPushed() {
		for (TableItem item : tableViewer.getTable().getItems()) {
			item.setChecked(true)
			connections.add(item.getData() as AbstractConnection)
		}
		validatePage()
	}

	/** 
	 */
	def protected void handleUncheckAllButtonPushed() {
		for (TableItem item : tableViewer.getTable().getItems()) {
			item.setChecked(false)
			connections.remove(item.getData() as AbstractConnection)
		}
		validatePage()
	}

	/** 
	 */
	def protected void handleRemoveButtonPushed() {
		val Table table = tableViewer.getTable()
		for (TableItem item : table.getItems()) {
			if (item.getChecked()) {
				val Object object = item.getData()
				tableViewer.remove(object)
				connections.remove(object)
			}

		}
		validatePage()
	}

	/** 
	 * @param event
	 */
	def protected void handleTableSelection(SelectionEvent event) {
		if (event.detail === SWT::CHECK) {
			val TableItem item = event.item as TableItem
			val AbstractConnection connection = item.getData() as AbstractConnection
			if (item.getChecked()) {
				connections.add(connection)
			} else {
				connections.remove(connection)
			}
			validatePage()
		}

	}

	/** 
	 * @param event
	 */
	def protected void handleTableDoubleClicked(DoubleClickEvent event) {
		val AbstractConnection selected = (event.getSelection() as IStructuredSelection).
			getFirstElement() as AbstractConnection
		if (Dialog::OK === new CreateEditConnectionDialog(getShell(), selected).open()) {
			tableViewer.refresh(selected)
		}

	}

	/** 
	 */
	def protected void validatePage() {
		setPageComplete(false)
		setErrorMessage(null)
		if (targetFileText.getText().isEmpty()) {
			setErrorMessage("Please select a target file!")
		} else if (connections.isEmpty()) {
			setMessage("Please select at least one connection to finish the wizard!", WARNING)
		} else {
			setMessage("Wizard can be finished now. Please push the 'Finish'-button.", INFORMATION)
			setPageComplete(true)
		}
	}

	/** 
	 * @param button
	 */
	def private void disableButton(Button button) {
		if (!VClipseConnectionPlugin::getDefault().isJCoAvailable()) {
			button.setEnabled(false)
			button.setToolTipText("Disabled because of absence of jco library")
		}

	}

}
