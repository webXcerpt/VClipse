package org.vclipse.vcml.ui.outline;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.ui.editor.outline.impl.EObjectNode;
import org.eclipse.xtext.util.concurrent.IUnitOfWork;
import org.vclipse.vcml.vcml.BillOfMaterial;
import org.vclipse.vcml.vcml.VCObject;

@SuppressWarnings("rawtypes")
public class VCObjectAdapterFactory implements IAdapterFactory {

	public Object getAdapter(Object adaptableObject,  Class adapterType) {
		if(adaptableObject instanceof EObjectNode) {
			if(adapterType == VCObject.class || adapterType == BillOfMaterial.class) {
				EObjectNode node = (EObjectNode)adaptableObject;
				EObject result = node.readOnly(new IUnitOfWork<EObject, EObject>() {
					public EObject exec(EObject object) throws Exception {
						return object;
					}
				});
				return result instanceof VCObject || result instanceof BillOfMaterial ? result : null;
			}
		}
		return null;
	}

	public Class[] getAdapterList() {
		return new Class[]{VCObject.class, BillOfMaterial.class};
	}
}
