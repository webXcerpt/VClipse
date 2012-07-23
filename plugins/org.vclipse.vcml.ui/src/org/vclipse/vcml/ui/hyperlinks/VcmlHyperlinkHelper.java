package org.vclipse.vcml.ui.hyperlinks;

import java.util.Arrays;
import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.xtext.nodemodel.INode;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;
import org.eclipse.xtext.resource.EObjectAtOffsetHelper;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.ui.editor.hyperlinking.HyperlinkHelper;
import org.eclipse.xtext.ui.editor.hyperlinking.IHyperlinkAcceptor;
import org.eclipse.xtext.ui.editor.hyperlinking.IHyperlinkHelper;
import org.vclipse.vcml.utils.DependencySourceUtils;
import org.vclipse.vcml.vcml.Dependency;
import org.vclipse.vcml.vcml.VcmlPackage;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.inject.Inject;

public class VcmlHyperlinkHelper extends HyperlinkHelper {

	@Inject 
	private EObjectAtOffsetHelper eObjectAtOffsetHelper;
	
	@Inject
	private DependencySourceUtils sourceUtils;
	
	@Inject
	private HyperlinkDelegateExtension extensions;
	
	@Override
	public IHyperlink[] createHyperlinksByOffset(XtextResource resource, int offset, boolean createMultipleHyperlinks) {
		List<IHyperlink> links = Lists.newArrayList();
		IHyperlink[] createHyperlinksByOffset = super.createHyperlinksByOffset(resource, offset, createMultipleHyperlinks);
		if(createHyperlinksByOffset != null) {
			links.addAll(Arrays.asList(createHyperlinksByOffset));			
		}
		for(IHyperlinkHelper helper : extensions.getExtensions()) {
			createHyperlinksByOffset = helper.createHyperlinksByOffset(resource, offset, true);
			if(createHyperlinksByOffset != null) {
				links.addAll(Arrays.asList(createHyperlinksByOffset));				
			}
		}
		return links.isEmpty() ? null : Iterables.toArray(links, IHyperlink.class);
	}

	@Override
	public void createHyperlinksByOffset(XtextResource resource, int offset, IHyperlinkAcceptor acceptor) {
		super.createHyperlinksByOffset(resource, offset, acceptor);
		EObject elementAt = eObjectAtOffsetHelper.resolveContainedElementAt(resource, offset);
		if(elementAt instanceof Dependency) {
			EObject source = sourceUtils.getSource((Dependency)elementAt);
			if(source != null) {
				List<INode> findNodesForFeature = NodeModelUtils.findNodesForFeature(elementAt, VcmlPackage.eINSTANCE.getVCObject_Name());
				if(!findNodesForFeature.isEmpty()) {
					INode nameNode = findNodesForFeature.get(0);
					createHyperlinksTo(resource, new Region(nameNode.getTotalOffset() + 1, nameNode.getTotalLength()), source, acceptor);					
				}
			}
		}
	}
}
