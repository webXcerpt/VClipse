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
import org.vclipse.configscan.ConfigScanPlugin;
import org.vclipse.configscan.IConfigScanRemoteConnections;
import org.vclipse.configscan.IConfigScanRemoteConnections.RemoteConnection;
import org.vclipse.configscan.impl.model.TestRunAdapter;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.sap.conn.jco.JCoException;

public class ManyConnectionsTab extends AbstractLaunchConfigurationTab {

	public static final String SEPARATOR = "::";
	
	private final IConfigScanRemoteConnections remoteConnections;
	
	private Table table;
	
	private Button skipMaterialTestsButton;
	
	// map for selected items at initialization
	private Map<String, TableItem> name2Item = Maps.newHashMap();
	
	private Map<String, Object> nameValue = Maps.newHashMap();
	
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
		table = connectionsViewer.getTable();
		table.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		List<? extends RemoteConnection> connections = Lists.newArrayList();
		try {
			connections = remoteConnections.readConfigScanRemoteConnections();
		} catch (JCoException exception) {
			ConfigScanPlugin.log(exception.getMessage(), IStatus.ERROR);
		}
	
		connectionsViewer.setLabelProvider(new LabelProvider());
		connectionsViewer.setContentProvider(new ContentProvider());
		connectionsViewer.setInput(connections);
		table.addSelectionListener(new SelectionAdapter() {
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
		group.setLayout(new GridLayout(2, false));
		group.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		skipMaterialTestsButton = new Button(group, SWT.CHECK);
		skipMaterialTestsButton.setText("Skip material tests");
		skipMaterialTestsButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				setDirty(true);
				updateLaunchConfigurationDialog();
			}
		});
		
		setControl(mainArea);
	}
	
	@Override
	public void initializeFrom(ILaunchConfiguration configuration) {
		try {
			String prefix = configuration.getName() + SEPARATOR;
			
			// handle the connection values
			Map<String, Boolean> name2Checked = Maps.newHashMap();
			Map<?, ?> attributes = configuration.getAttributes();
			if(!attributes.isEmpty()) {
				for(Object key : attributes.keySet()) {
					if(key instanceof String && ((String)key).startsWith(prefix)) {
						Object value = attributes.get(key);
						if(value instanceof Boolean) {
							name2Checked.put((String)key, (Boolean)value);
						}
					}
				}
			}
			
			for(TableItem item : table.getItems()) {
				// init state
				item.setChecked(false);
				
				// enable item 
				String key = prefix + ((RemoteConnection)item.getData()).getDescription();
				if(name2Checked.containsKey(key)) {
					item.setChecked(true);
					name2Item.put(key, item);
				}
			}
		} catch (CoreException exception) {
			ConfigScanPlugin.log(exception.getMessage(), IStatus.ERROR);
		}
	}

	@Override
	public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
		// table == null if the launch configuration does not exist
		String prefixName = configuration.getName() + SEPARATOR;
		if(table != null) {
			for(String name : name2Item.keySet()) {
				if(name.startsWith(prefixName)) {
					name2Item.get(name).setChecked(true);
				}
			}
		}
	}

	@Override
	public void performApply(ILaunchConfigurationWorkingCopy configuration) {
		configuration.setAttribute(TestRunAdapter.SKIP_MATERIAL_TESTS, skipMaterialTestsButton.getSelection());
		String prefix = configuration.getName() + SEPARATOR;
		Map<String, Boolean> name2Checked = Maps.newHashMap();
		for(TableItem item : table.getItems()) {
			String key = prefix + ((RemoteConnection)item.getData()).getDescription();
			if(item.getChecked()) {
				name2Checked.put(key, true);
			} else {
				name2Item.remove(key);
			}
		}
		configuration.setAttributes(name2Checked);
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