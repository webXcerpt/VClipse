package org.vclipse.vcml.ui.extension;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.vclipse.vcml.ui.VCMLUiPlugin;
import org.vclipse.vcml.ui.outline.actions.VcmlOutlineActionProvider;
import org.vclipse.vcml.ui.outline.actions.IVCMLOutlineActionHandler;
import org.vclipse.vcml.ui.outline.actions.VCMLOutlineAction;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.inject.Inject;

public class ExtensionPointUtilities implements IExtensionPointUtilities {
	
	private static final String EXTENSION_POINT_ID = VCMLUiPlugin.ID + ".outlinePageActions";
	private static final String ELEMENT_ACTION = "action";
	private static final String ELEMENT_HANDLER = "handler";
	
	private static final String ATTRIBUTE_ID = "id";
	private static final String ATTRIBUTE_LABEL = "label";
	private static final String ATTRIBUTE_TOOTLTIP = "tooltip";
	private static final String ATTRIBUTE_ICON = "icon";
	private static final String ATTRIBUTE_DISABLED_ICON = "disabledIcon";
	private static final String ATTRIBUTE_STATE = "state";
	private static final String ATTRIBUTE_TOOLBAR_PATH = "toolbarPath";
	private static final String ATTRIBUTE_MENUBAR_PATH = "menubarPath";
	
	private static final String ATTRIBUTE_TYPE = "type";
	private static final String ATTRIBUTE_HANDLER = "handler";
	private static final String ATTRIBUTE_ACTION = "action";
	
	private final Multimap<String, IVCMLOutlineActionHandler<?>> type2Action;
	private final List<VCMLOutlineAction> vcmlOutlineActions;
	private final Map<String, VCMLOutlineAction> id2Action;
	private final Map<VCMLOutlineAction, String> action2Path;

	private VcmlOutlineActionProvider vcmlOutlineActionProvider;
	
	@Inject
	public ExtensionPointUtilities(VcmlOutlineActionProvider vcmlOutlineActionProvider) {
		type2Action = HashMultimap.create();
		id2Action = Maps.newHashMap();
		action2Path = new LinkedHashMap<VCMLOutlineAction, String>();
		vcmlOutlineActions = Lists.newArrayList();
		
		this.vcmlOutlineActionProvider = vcmlOutlineActionProvider;
	}

	private void read() {
		IExtensionPoint point = Platform.getExtensionRegistry().getExtensionPoint(EXTENSION_POINT_ID);
		for(IExtension extension : point.getExtensions()) {
			String contributingPluginID = extension.getNamespaceIdentifier();
			for(IConfigurationElement element : extension.getConfigurationElements()) {
				String elementName = element.getName();
				if(ELEMENT_ACTION.equals(elementName)) {
					handleActionElement(contributingPluginID, element);
				}
				if(ELEMENT_HANDLER.equals(elementName)) {
					handleHandlerElement(element);
				}
			}
		}
	}

	private void handleHandlerElement(IConfigurationElement element) {
		String actionId = element.getAttribute(ATTRIBUTE_ACTION);
		if(actionId != null) {
			VCMLOutlineAction action = id2Action.get(actionId);
			if(action != null) {
				try {
					Object handler = element.createExecutableExtension(ATTRIBUTE_HANDLER);
					if(handler instanceof IVCMLOutlineActionHandler<?>) {
						IVCMLOutlineActionHandler<?> actionHandler = (IVCMLOutlineActionHandler<?>)handler;
						String type = element.getAttribute(ATTRIBUTE_TYPE);
						if(type != null) {			
							action.addHandler(type, actionHandler);
							type2Action.put(type, actionHandler);
						}
					}
				} catch(CoreException e) {
					System.out.println(e.getMessage());
					e.printStackTrace();
				}
			}
		}
	}

	private void handleActionElement(String contributingPluginID, IConfigurationElement element) {
		VCMLOutlineAction action = vcmlOutlineActionProvider.get();
		vcmlOutlineActions.add(action);
		String id = element.getAttribute(ATTRIBUTE_ID);
		if(id != null) {
			action.setId(id);
			id2Action.put(id, action);
		}
		String label = element.getAttribute(ATTRIBUTE_LABEL);
		if(label != null) {
			action.setText(label);
		}
		String tooltip = element.getAttribute(ATTRIBUTE_TOOTLTIP);
		if(tooltip != null) {
			action.setToolTipText(tooltip);
		}
		String icon = element.getAttribute(ATTRIBUTE_ICON);
		if(icon != null) {
			ImageDescriptor image = AbstractUIPlugin.imageDescriptorFromPlugin(contributingPluginID, icon);
			if(image != null) {
				action.setImageDescriptor(image);
			}
		}
		String disabledIcon = element.getAttribute(ATTRIBUTE_DISABLED_ICON);
		if(disabledIcon != null) {
			ImageDescriptor image = AbstractUIPlugin.imageDescriptorFromPlugin(contributingPluginID, disabledIcon);
			if(image != null) {
				action.setDisabledImageDescriptor(image);
			}
		}
		String state = element.getAttribute(ATTRIBUTE_STATE);
		if(state != null) {
			action.setEnabled(Boolean.parseBoolean(state));
		}

		String menubarPath = element.getAttribute(ATTRIBUTE_MENUBAR_PATH);
		if(menubarPath != null) {
			action2Path.put(action, menubarPath);
		}

		String toolbarPath = element.getAttribute(ATTRIBUTE_TOOLBAR_PATH);
		if(toolbarPath != null) {

		}
	}
	
	public Collection<IVCMLOutlineActionHandler<?>> getHandler(String type) {
		if(type2Action.isEmpty()) {
			read();
		}
		return type2Action.get(type);
	}

	public List<VCMLOutlineAction> getActions() {
		if(vcmlOutlineActions.isEmpty()) {
			read();
		}
		return vcmlOutlineActions;
	}

	public Map<VCMLOutlineAction, String> getPathes() {
		if(action2Path.isEmpty()) {
			read();
		}
		return action2Path;
	}
}
