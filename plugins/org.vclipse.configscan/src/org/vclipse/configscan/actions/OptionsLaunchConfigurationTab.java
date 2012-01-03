package org.vclipse.configscan.actions;

import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import com.sap.conn.jco.JCoException;
import org.vclipse.configscan.IConfigScanRemoteConnections;
import org.vclipse.configscan.IConfigScanRemoteConnections.RemoteConnection;

public class OptionsLaunchConfigurationTab extends AbstractLaunchConfigurationTab {

	public static final String CURRENT_CONNECTION_INDEX = "currentConnectionIndex";
	
	private IConfigScanRemoteConnections remoteConnections;
	
	private List<? extends RemoteConnection> connections;
	
	private Combo combo;
	
	public OptionsLaunchConfigurationTab(IConfigScanRemoteConnections connections) {
		this.remoteConnections = connections;
	}
	
	@Override
	public void createControl(Composite parent) {
		setDirty(false);
		Composite mainArea = new Composite(parent, SWT.NONE);
		mainArea.setLayout(new GridLayout(2, false));
		Label label = new Label(mainArea, SWT.NONE);
		label.setText("Please select a connection: ");
		try {
			connections = remoteConnections.readConfigScanRemoteConnections();
		} catch (JCoException e) {
			throw new RuntimeException(e);
		}
		combo = new Combo(mainArea, SWT.DROP_DOWN | SWT.READ_ONLY);
		combo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		combo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				int i = ((Combo)event.getSource()).getSelectionIndex();
				if(i > -1) {
					combo.select(i);
					setDirty(true);
					updateLaunchConfigurationDialog();
				}
			}
		});
		for(RemoteConnection rc : connections) {
			combo.add(rc.getDescription());
		}
		if(!connections.isEmpty()) {
			combo.select(0);
		}
		setControl(mainArea);
	}

	@Override
	public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
		if(combo != null) {
			int selectionIndex = combo.getSelectionIndex();
			if(selectionIndex > -1) {
				configuration.setAttribute(CURRENT_CONNECTION_INDEX, selectionIndex);	
			}
		}		
	}

	@Override
	public void initializeFrom(ILaunchConfiguration configuration) {
		try {
			int selectionIndex = configuration.getAttribute(CURRENT_CONNECTION_INDEX, 0);
			if(selectionIndex > -1) {
				combo.select(selectionIndex);				
			}
		} catch(CoreException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void performApply(ILaunchConfigurationWorkingCopy configuration) {
		configuration.setAttribute(CURRENT_CONNECTION_INDEX, combo.getSelectionIndex());
	}

	@Override
	public String getName() {
		return "ConfigScan Options";
	}
}