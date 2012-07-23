package org.vclipse.vcml.ui.outline;

import java.util.List;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.xtext.ui.editor.outline.impl.EObjectNode;
import org.eclipse.xtext.util.concurrent.IUnitOfWork;
import org.vclipse.base.VClipseStrings;
import org.vclipse.vcml.vcml.VcmlFactory;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

public class TypeFilterAction extends Action {

	private TreeViewer treeViewer;
	private ViewerFilter filter;
	
	public TypeFilterAction(TreeViewer treeViewer, final EClass type, ILabelProvider labelProvider) {
		super(VClipseStrings.appendTo("Type filter for ", VClipseStrings.splitCamelCase(type.getName()), true), Action.AS_CHECK_BOX);
		this.treeViewer = treeViewer;
		
		setImageDescriptor(
				ImageDescriptor.createFromImage(
						labelProvider.getImage(VcmlFactory.eINSTANCE.create(type))));
		
		filter = new ViewerFilter() {
			@Override
			public boolean select(Viewer viewer, Object parentElement, Object element) {
				if(element instanceof EObjectNode) {
					EObject result = ((EObjectNode)element).readOnly(new IUnitOfWork<EObject, EObject>() {
						public EObject exec(EObject eobject) throws Exception {
							return eobject;
						}
					});
					if(isChecked()) {
						if(result.eClass() == type) {
							return false;
						}
					}
				}
				return true;
			}
		};
	}

	@Override
	public void run() {
		List<ViewerFilter> filters = 
				Lists.newArrayList(treeViewer.getFilters());
		
		if(filters.contains(filter) && isChecked()) {
			filters.remove(filter);
		} else {
			filters.add(this.filter);
		}
		treeViewer.setFilters(Iterables.toArray(filters, ViewerFilter.class));
	}
}
