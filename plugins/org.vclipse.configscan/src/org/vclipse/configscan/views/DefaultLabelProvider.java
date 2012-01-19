package org.vclipse.configscan.views;

import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider;

class DefaultLabelProvider extends DelegatingStyledCellLabelProvider {

	private IStyledLabelProvider labelProvider;
	
	public DefaultLabelProvider(IStyledLabelProvider labelProvider) {
		super(labelProvider);
		this.labelProvider = labelProvider;
	}

	@Override
	public String getToolTipText(Object element) {
		if(labelProvider instanceof CellLabelProvider) {
			return ((CellLabelProvider)labelProvider).getToolTipText(element);			
		}
		return "";
	}
}
