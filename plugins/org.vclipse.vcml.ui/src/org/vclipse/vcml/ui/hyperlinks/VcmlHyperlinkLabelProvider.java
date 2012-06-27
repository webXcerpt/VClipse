package org.vclipse.vcml.ui.hyperlinks;

import org.apache.log4j.Logger;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryLabelProvider;
import org.vclipse.vcml.ui.labeling.VCMLLabelProvider;
import org.vclipse.vcml.utils.DependencySourceUtils;
import org.vclipse.vcml.vcml.ConditionSource;
import org.vclipse.vcml.vcml.ConstraintSource;
import org.vclipse.vcml.vcml.ProcedureSource;
import org.vclipse.vcml.vcml.VCObject;

import com.google.inject.Inject;

public class VcmlHyperlinkLabelProvider extends VCMLLabelProvider {

	private Logger logger = Logger.getLogger(VcmlHyperlinkLabelProvider.class);
	
	@Inject
	private DependencySourceUtils sourceUtils;
	
	@Inject
	public VcmlHyperlinkLabelProvider(AdapterFactoryLabelProvider delegate) {
		super(delegate);
	}

	@Override
	public String getText(Object element) {
		if(element instanceof ConditionSource || element instanceof ConstraintSource || element instanceof ProcedureSource) {
			VCObject dependency = sourceUtils.getDependency((EObject)element);
			if(dependency != null) {
				return "Go to dependency source";
			} else {
				logger.warn("Found dependency object is null.");
			}
		}
		return super.getText(element);
	}
}
