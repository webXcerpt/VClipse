package org.vclipse.configscan.launch;

import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.jface.viewers.BaseLabelProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.vclipse.configscan.ConfigScanPlugin;
import org.vclipse.configscan.IConfigScanRemoteConnections;
import org.vclipse.configscan.IConfigScanRemoteConnections.RemoteConnection;
import org.vclipse.configscan.impl.model.TestRun;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.sap.conn.jco.JCoException;

public class ManyConnectionsTab extends AbstractLaunchConfigurationTab {

	private final IConfigScanRemoteConnections remoteConnections;
	
	private Table connectionsTable;
	
	private Button skipMaterialTestsButton;
	
	private Text kbObjectText;
	
	private Button stopOnError;
	
	private Button performanceRun;
	
	private Button breakPointEnabled;
	
	private Text testDate;
	
	private Text rootQuantity;
	
	private Text logFilesLocation;
	
	private Button enableFilesLogging;
	
	@Inject
	public ManyConnectionsTab(IConfigScanRemoteConnections remoteConnections) {
		this.remoteConnections = remoteConnections;
	}
	
	@Override
	public void createControl(Composite parent) {
		setDirty(false);
		
		Composite mainArea = new Composite(parent, SWT.NONE);
		mainArea.setLayout(new GridLayout());
	
		new Label(mainArea, SWT.NONE).setText("Please select systems for running tests:");
		
		TableViewer connectionsViewer = new TableViewer(mainArea, SWT.BORDER | SWT.CHECK 
				| SWT.FULL_SELECTION | SWT.V_SCROLL | SWT.H_SCROLL);
		connectionsTable = connectionsViewer.getTable();
		connectionsTable.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		List<? extends RemoteConnection> connections = Lists.newArrayList();
		try {
			connections = remoteConnections.readConfigScanRemoteConnections();
		} catch (JCoException exception) {
			ConfigScanPlugin.log(exception.toString(), IStatus.ERROR);
		}
	
		connectionsViewer.setLabelProvider(new LabelProvider());
		connectionsViewer.setContentProvider(new ContentProvider());
		connectionsViewer.setInput(connections);
		connectionsTable.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				if(event.detail == SWT.CHECK) {
					setDirty(true);
					updateLaunchConfigurationDialog();					
				}
			}
		});
		
		Group group = new Group(mainArea, SWT.NONE);
		group.setText("Options");
		group.setLayout(new GridLayout(2, true));
		group.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Composite leftComposite = new Composite(group, SWT.NONE);
		leftComposite.setLayout(new GridLayout(2, false));
		leftComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		Composite rightComposite = new Composite(group, SWT.NONE);
		rightComposite.setLayout(new GridLayout());
		
		skipMaterialTestsButton = new Button(rightComposite, SWT.CHECK);
		skipMaterialTestsButton.setText("Skip material tests");
		skipMaterialTestsButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				setDirty(true);
				updateLaunchConfigurationDialog();
			}
		});
		
		stopOnError = new Button(rightComposite, SWT.CHECK);
		stopOnError.setText("Stop on error");
		stopOnError.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				setDirty(true);
				updateLaunchConfigurationDialog();
			}
		});
		
		performanceRun = new Button(rightComposite, SWT.CHECK);
		performanceRun.setText("Performance run");
		performanceRun.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				setDirty(true);
				updateLaunchConfigurationDialog();
			}
		});
		
		breakPointEnabled = new Button(rightComposite, SWT.CHECK);
		breakPointEnabled.setText("Breakpoint enabled");
		breakPointEnabled.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				setDirty(true);
				updateLaunchConfigurationDialog();
			}
		});
		
		new Label(leftComposite, SWT.NONE).setText("KBOBJECT: ");
		kbObjectText = new Text(leftComposite, SWT.BORDER);
		kbObjectText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent event) {
				setDirty(true);
				updateLaunchConfigurationDialog();
			}
		});
		kbObjectText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		new Label(leftComposite, SWT.NONE).setText("Test date: ");
		testDate = new Text(leftComposite, SWT.BORDER);
		testDate.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent event) {
				setDirty(true);
				updateLaunchConfigurationDialog();
			}
		});
		testDate.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		new Label(leftComposite, SWT.NONE).setText("Root quantity: ");
		rootQuantity = new Text(leftComposite, SWT.BORDER);
		rootQuantity.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent event) {
				setDirty(true);
				updateLaunchConfigurationDialog();
			}
		});
		rootQuantity.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		new Label(group, SWT.NONE).setText("Location for test/input documents: ");
		logFilesLocation = new Text(group, SWT.BORDER);
		logFilesLocation.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent event) {
				setDirty(true);
				updateLaunchConfigurationDialog();
			}
		});
		logFilesLocation.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		logFilesLocation.setEnabled(false);
		
		enableFilesLogging = new Button(group, SWT.CHECK);
		enableFilesLogging.setText("Enable test/input document logging");
		enableFilesLogging.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				boolean selection = enableFilesLogging.getSelection();
				if(!selection) {
					logFilesLocation.setText(TestRun.LOG_FILES_LOCATION);
				}
				logFilesLocation.setEnabled(selection);
				setDirty(true);
				updateLaunchConfigurationDialog();
			}
		});
		setControl(mainArea);
	}
	
	@Override
	public void initializeFrom(ILaunchConfiguration configuration) {
		try {
			Map<?, ?> attributes = configuration.getAttributes();
			
			Object object = attributes.get(TestRun.SKIP_MATERIAL_TESTS);
			if(object != null) {
				skipMaterialTestsButton.setSelection((Boolean)object);
			}
			
			object = attributes.get(TestRun.KBOBJECT);
			if(object != null) {
				kbObjectText.setText((String)object);
			}
			
			object = attributes.get(TestRun.STOP_ON_ERROR);
			if(object != null) {
				stopOnError.setSelection((Boolean)object);
			}
			
			object = attributes.get(TestRun.PERFORMANCE_RUN);
			if(object != null) {
				performanceRun.setSelection((Boolean)object);
			}
			
			object = attributes.get(TestRun.BREAKPOINT_ENABLED);
			if(object != null) {
				breakPointEnabled.setSelection((Boolean)object);
			}
			
			object = attributes.get(TestRun.ROOT_QUANTITY);
			if(object != null) {
				rootQuantity.setText((String)object);
			}
			
			object = attributes.get(TestRun.LOGGING_ENABLED);
			if(object != null) {
				boolean loggingEnabled = (Boolean)object;
				enableFilesLogging.setSelection(loggingEnabled);
				if(loggingEnabled) {
					object = attributes.get(TestRun.LOG_FILES_LOCATION);
					logFilesLocation.setEnabled(true);
					if(object != null) {
						logFilesLocation.setText((String)object);
					}
				} else {
					logFilesLocation.setEnabled(false);
					logFilesLocation.setText(TestRun.LOG_FILES_LOCATION);
				}
			}
			
			for(TableItem item : connectionsTable.getItems()) {
				object = attributes.get(item.getText());
				if(object instanceof Boolean) {
					item.setChecked((Boolean)object);
				}
			}
		} catch (CoreException exception) {
			ConfigScanPlugin.log(exception.getMessage(), IStatus.ERROR);
		}
	}

	@Override
	public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
		Map<String, Object> map = Maps.newHashMap();
		map.put(TestRun.SKIP_MATERIAL_TESTS, false);
		map.put(TestRun.KBOBJECT, "");
		map.put(TestRun.STOP_ON_ERROR, false);
		map.put(TestRun.PERFORMANCE_RUN, false);
		map.put(TestRun.BREAKPOINT_ENABLED, true);
		map.put(TestRun.TEST_DATE, "");
		map.put(TestRun.ROOT_QUANTITY, "1");
		map.put(TestRun.LOG_FILES_LOCATION, "");
		map.put(TestRun.LOGGING_ENABLED, false);
		configuration.setAttributes(map);
	}

	@Override
	public void performApply(ILaunchConfigurationWorkingCopy configuration) {
		Map<String, Object> map = Maps.newHashMap();
		
		map.put(TestRun.SKIP_MATERIAL_TESTS, skipMaterialTestsButton.getSelection());
		map.put(TestRun.KBOBJECT, kbObjectText.getText());
		map.put(TestRun.STOP_ON_ERROR, stopOnError.getSelection());
		map.put(TestRun.PERFORMANCE_RUN, performanceRun.getSelection());
		map.put(TestRun.BREAKPOINT_ENABLED, breakPointEnabled.getSelection());
		map.put(TestRun.TEST_DATE, testDate.getText());
		map.put(TestRun.ROOT_QUANTITY, rootQuantity.getText());
		
		boolean selection = enableFilesLogging.getSelection();
		map.put(TestRun.LOGGING_ENABLED, selection);
		if(selection) {
			map.put(TestRun.LOG_FILES_LOCATION, logFilesLocation.getText());			
		}
		
		for(TableItem item : connectionsTable.getItems()) {
			map.put(item.getText(), item.getChecked());
		}
		configuration.setAttributes(map);
	}

	@Override
	public String getName() {
		return "ConfigScan Connections";
	}	
}

class ContentProvider implements IStructuredContentProvider {

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		
	}
	
	public void dispose() {

	}

	public Object[] getElements(Object inputElement) {
		if(inputElement instanceof List<?>) {
			return ((List<?>)inputElement).toArray();
		}
		return new Object[0];
	}
}

class LabelProvider extends BaseLabelProvider implements ILabelProvider {

	@Override
	public Image getImage(Object element) {
		return null;
	}

	@Override
	public String getText(Object element) {
		if(element instanceof RemoteConnection) {
			return ((RemoteConnection)element).getDescription();
		}
		return "";
	}
}