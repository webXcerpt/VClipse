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
import org.vclipse.configscan.impl.model.TestRunAdapter;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.sap.conn.jco.JCoException;

public class ManyConnectionsTab extends AbstractLaunchConfigurationTab {

	private final IConfigScanRemoteConnections remoteConnections;
	
	private Table connectionsTable;
	
	private Button skipMaterialTestsButton;
	
	private Text kbObjectText;
	
	private Text rtvText;
	
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
			ConfigScanPlugin.log(exception.getMessage(), IStatus.ERROR);
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
		
		GridData layoutData = new GridData();
		layoutData.horizontalSpan = 2;
		skipMaterialTestsButton.setLayoutData(layoutData);
		
		new Label(group, SWT.NONE).setText("KBOBJECT: ");
		kbObjectText = new Text(group, SWT.BORDER);
		kbObjectText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent event) {
				setDirty(true);
				updateLaunchConfigurationDialog();
			}
		});
		GridData gridData = new GridData();
		gridData.widthHint = 100;
		kbObjectText.setLayoutData(gridData);
		
		new Label(group, SWT.NONE).setText("RTV: ");
		rtvText = new Text(group, SWT.BORDER);
		rtvText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent event) {
				setDirty(true);
				updateLaunchConfigurationDialog();
			}
		});
		gridData = new GridData();
		gridData.widthHint = 100;
		rtvText.setLayoutData(gridData);
		
		setControl(mainArea);
	}
	
	@Override
	public void initializeFrom(ILaunchConfiguration configuration) {
		try {
			Map<?, ?> attributes = configuration.getAttributes();
			
			Object object = attributes.get(TestRunAdapter.SKIP_MATERIAL_TESTS);
			if(object != null) {
				skipMaterialTestsButton.setSelection((Boolean)object);
			}
			
			object = attributes.get(TestRunAdapter.KBOBJECT);
			if(object != null) {
				kbObjectText.setText((String)object);
			}
			
			object = attributes.get(TestRunAdapter.RTV);
			if(object != null) {
				rtvText.setText((String)object);
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
		// not used
	}

	@Override
	public void performApply(ILaunchConfigurationWorkingCopy configuration) {
		Map<String, Object> map = Maps.newHashMap();
		
		map.put(TestRunAdapter.SKIP_MATERIAL_TESTS, skipMaterialTestsButton.getSelection());
		map.put(TestRunAdapter.KBOBJECT, kbObjectText.getText());
		map.put(TestRunAdapter.RTV, rtvText.getText());
		
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