package org.vclipse.vcml.ui.outline.actions;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;
import org.eclipse.xtext.resource.IResourceFactory;
import org.vclipse.vcml.ui.outline.SapRequestObjectLinker;

import com.google.inject.Inject;
import com.google.inject.Provider;

public class VcmlOutlineActionProvider implements Provider<VCMLOutlineAction> {

	@Inject
	private IPreferenceStore preferenceStore;
	
	@Inject
	private IResourceFactory resourceFactory;
	
	@Inject
	private IContentOutlinePage outlinePage;
	
	@Inject
	private SapRequestObjectLinker linker;
	
	public VCMLOutlineAction get() {
		return new VCMLOutlineAction(preferenceStore, resourceFactory, outlinePage, linker);
	}
}
