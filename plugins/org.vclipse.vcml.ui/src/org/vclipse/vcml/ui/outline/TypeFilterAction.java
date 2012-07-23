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
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.xtext.ui.editor.outline.impl.EObjectNode;
import org.eclipse.xtext.util.concurrent.IUnitOfWork;
import org.vclipse.base.VClipseStrings;
import org.vclipse.vcml.vcml.VcmlFactory;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

public class TypeFilterAction extends Action {

	private TreeViewer treeViewer;
	private ViewerFilter filter;
	
	private ImageDescriptor enabled;
	private ImageDescriptor disabled;
	
	boolean stateEnabled;
	
	public TypeFilterAction(TreeViewer treeViewer, final EClass type, ILabelProvider labelProvider) {
		super(VClipseStrings.appendTo("Type filter for ", VClipseStrings.splitCamelCase(type.getName()), true));
		this.treeViewer = treeViewer;
		
		Image actionImage = labelProvider.getImage(VcmlFactory.eINSTANCE.create(type));
		enabled = ImageDescriptor.createFromImage(actionImage);
		disabled = ImageDescriptor.createWithFlags(enabled, SWT.IMAGE_GRAY);
		
		setImageDescriptor(disabled);
		stateEnabled = false;
		
		filter = new ViewerFilter() {
			@Override
			public boolean select(Viewer viewer, Object parentElement, Object element) {
				if(element instanceof EObjectNode) {
					EObject result = ((EObjectNode)element).readOnly(new IUnitOfWork<EObject, EObject>() {
						public EObject exec(EObject eobject) throws Exception {
							return eobject;
						}
					});
					if(stateEnabled) {
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
		
		if(filters.contains(filter) && stateEnabled) {
			filters.remove(filter);
			setImageDescriptor(disabled);
			stateEnabled = false;
		} else {
			filters.add(this.filter);
			setImageDescriptor(enabled);
			stateEnabled = true;
		}
		treeViewer.setFilters(Iterables.toArray(filters, ViewerFilter.class));
	}
}
