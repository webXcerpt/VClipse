package org.vclipse.vcml.ui.extension;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.vclipse.vcml.ui.outline.actions.IVCMLOutlineActionHandler;
import org.vclipse.vcml.ui.outline.actions.VCMLOutlineAction;

// this interface is required to avoid a circular dependency
public interface IExtensionPointUtilities {

	public Collection<IVCMLOutlineActionHandler<?>> getHandler(String type);
	
	public List<VCMLOutlineAction> getActions();
	
	public Map<VCMLOutlineAction, String> getPathes();
}
