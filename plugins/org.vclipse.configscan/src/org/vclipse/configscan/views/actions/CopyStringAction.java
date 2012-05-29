package org.vclipse.configscan.views.actions;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchCommandConstants;
import org.eclipse.ui.IWorkbenchWindow;
import org.vclipse.base.ui.util.ClasspathAwareImageHelper;
import org.vclipse.configscan.ConfigScanPlugin;
import org.vclipse.configscan.impl.model.TestCase;
import org.vclipse.configscan.views.ConfigScanView;

public class CopyStringAction extends SimpleTreeViewerAction {

	public static final String ID =  ConfigScanPlugin.ID + "." + CopyStringAction.class.getSimpleName();
	
	public CopyStringAction(ConfigScanView view, ClasspathAwareImageHelper imageHelper) {
		super(view, imageHelper);
		setToolTipText("Copy");
		setText("Copy");
        setActionDefinitionId(IWorkbenchCommandConstants.EDIT_COPY);
        setId(ID);
        IWorkbenchWindow workbenchWindow = view.getSite().getWorkbenchWindow();
		ISharedImages sharedImages = workbenchWindow.getWorkbench().getSharedImages();
        setImageDescriptor(sharedImages.getImageDescriptor(ISharedImages.IMG_TOOL_COPY));
        setDisabledImageDescriptor(sharedImages.getImageDescriptor(ISharedImages.IMG_TOOL_COPY_DISABLED));
	}

	@Override
	public void run() {
		ISelection selection = treeViewer.getSelection();
		if(!selection.isEmpty() && selection instanceof IStructuredSelection) {
			Object[] elements = ((IStructuredSelection)selection).toArray();
			StringBuffer copiedContent = new StringBuffer();
			for(int i=0, end=elements.length-1; i<elements.length; i++) {
				Object element = elements[i];
				if(element instanceof TestCase) {
					copiedContent.append(((TestCase)element).getTitle());
					if(i != end) {
						copiedContent.append(System.getProperty("line.separator"));						
					}
				}
			}
			view.getClipboard().setContents(new String[]{copiedContent.toString()}, new Transfer[]{TextTransfer.getInstance()});
		}
	}
}
