package org.vclipse.configscan.views;

import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.TreeViewer;

class PropertyChangeListener implements IPropertyChangeListener {
	
	private TreeViewer treeViewer;
	
	public PropertyChangeListener(TreeViewer treeViewer) {
		this.treeViewer = treeViewer;
	}
	
	@Override
	public void propertyChange(PropertyChangeEvent event) {
		if(ConfigScanConfiguration.EXPAND_TREE_ON_INPUT.equals(event.getProperty())) {
			Object object = event.getNewValue();
			if(object instanceof Boolean) {
				treeViewer.setAutoExpandLevel((Boolean)object ? ConfigScanConfiguration.DEFAULT_EXPAND_LEVEL : 0);
			}
		}
	}
}
