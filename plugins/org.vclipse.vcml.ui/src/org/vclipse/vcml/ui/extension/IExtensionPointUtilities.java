package org.vclipse.vcml.ui.extension;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.vclipse.vcml.ui.outline.actions.IVcmlOutlineActionHandler;
import org.vclipse.vcml.ui.outline.actions.VcmlOutlineAction;

// this interface is required to avoid a circular dependency
public interface IExtensionPointUtilities {

	public Collection<IVcmlOutlineActionHandler<?>> getHandler(String type);
	
	public List<VcmlOutlineAction> getActions();
	
	public Map<VcmlOutlineAction, String> getPathes();
}
