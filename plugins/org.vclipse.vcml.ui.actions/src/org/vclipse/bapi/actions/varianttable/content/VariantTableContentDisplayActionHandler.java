package org.vclipse.bapi.actions.varianttable.content;

import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.ecore.resource.Resource;
import org.vclipse.bapi.actions.IBAPIActionRunner;
import org.vclipse.vcml.vcml.Option;
import org.vclipse.vcml.vcml.VCObject;
import org.vclipse.vcml.vcml.VariantTableContent;
import org.vclipse.vcml.vcml.VcmlModel;

public class VariantTableContentDisplayActionHandler extends VariantTableContentReader implements IBAPIActionRunner<VariantTableContent> {

	@Override
	public boolean isEnabled(VariantTableContent object) {
		return isConnected();
	}
	
	@Override
	public void run(VariantTableContent variantTableContent, Resource resource,IProgressMonitor monitor, Map<String, VCObject> seenObjects, List<Option> options) throws Exception {
		read(variantTableContent.getName(), (VcmlModel)resource.getContents().get(0), monitor, seenObjects, options, false);
	}
}
