package org.vclipse.exports.opencpq

import com.google.inject.Inject
import com.google.inject.Provider
import org.eclipse.core.commands.AbstractHandler
import org.eclipse.core.commands.ExecutionEvent
import org.eclipse.core.commands.ExecutionException
import org.eclipse.core.resources.IFile
import org.eclipse.core.resources.IResource
import org.eclipse.core.runtime.NullProgressMonitor
import org.eclipse.core.runtime.Path
import org.eclipse.emf.common.util.URI
import org.eclipse.emf.ecore.resource.ResourceSet
import org.eclipse.jface.viewers.ISelection
import org.eclipse.jface.viewers.IStructuredSelection
import org.eclipse.ui.handlers.HandlerUtil
import org.eclipse.xtext.util.StringInputStream
import org.vclipse.vcml.vcml.VcmlModel

class ExportHandler extends AbstractHandler {
	
	@Inject
	Exporter exporter
	
	@Inject
	Provider<ResourceSet> resourceSetProvider
	
	override execute(ExecutionEvent event) throws ExecutionException {
		val ISelection selection = HandlerUtil.getCurrentSelection(event);
		if (selection instanceof IStructuredSelection) {
			val iter = selection.iterator;
			while (iter.hasNext) {
				switch el: iter.next {
					IFile: {
						val URI vcmlUri = URI.createPlatformResourceURI(el.fullPath.toOSString, true);
						val resource = resourceSetProvider.get.getResource(vcmlUri, true);
						val IFile jsFile = el.parent.getFile(new Path(vcmlUri.trimFileExtension.lastSegment.toString).addFileExtension("js"));
						val contents = new StringInputStream(exporter.export(resource.contents.get(0) as VcmlModel).toString);
						if (jsFile.exists)
							jsFile.setContents(contents, IResource.NONE, new NullProgressMonitor)
						else
							jsFile.create(contents, IResource.NONE, new NullProgressMonitor)
					}	
				}
			}
		}
		null
	}	
	
}