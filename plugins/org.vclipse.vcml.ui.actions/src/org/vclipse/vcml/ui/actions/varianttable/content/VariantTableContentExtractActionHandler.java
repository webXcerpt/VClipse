package org.vclipse.vcml.ui.actions.varianttable.content;

import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.ecore.resource.Resource;
import org.vclipse.vcml.ui.outline.actions.IVcmlOutlineActionHandler;
import org.vclipse.vcml.vcml.Model;
import org.vclipse.vcml.vcml.Option;
import org.vclipse.vcml.vcml.VariantTableContent;

public class VariantTableContentExtractActionHandler extends VariantTableContentReader implements IVcmlOutlineActionHandler<VariantTableContent> {

	@Override
	public boolean isEnabled(VariantTableContent object) {
		return isConnected();
	}
	
	@Override
	public void run(VariantTableContent content, Resource resource, IProgressMonitor monitor, Set<String> seenObjects, List<Option> options) throws Exception {
		read(content.getTable().getName(), (Model)resource.getContents().get(0), monitor, seenObjects, options, true);
	}
}
