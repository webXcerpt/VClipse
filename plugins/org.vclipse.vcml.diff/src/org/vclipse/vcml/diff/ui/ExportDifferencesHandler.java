package org.vclipse.vcml.diff.ui;

import java.util.Iterator;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.resources.IFile;
import org.vclipse.base.ui.FileListHandler;

import com.google.inject.Inject;

public class ExportDifferencesHandler extends FileListHandler {

	@Inject
	private ExportDiffsDialog dialog;

	@Override
	public void handleListVariable(Iterable<IFile> collection, ExecutionEvent event) {
		Iterator<IFile> iterator = collection.iterator();
		IFile firstFile = null, secondFile = null;
		for(int i=0; iterator.hasNext() && i<2; i++) {
			if(firstFile == null) {
				firstFile = (IFile)iterator.next();
			} else {
				secondFile = (IFile)iterator.next();
			}
		}
		
		if(firstFile != null) {
			if(secondFile != null) {
				if(firstFile.getLocalTimeStamp() > secondFile.getLocalTimeStamp()) {
					dialog.setNewFile(firstFile);
					dialog.setOldFile(secondFile);
					dialog.open();
				} else {
					dialog.setNewFile(secondFile);
					dialog.setOldFile(firstFile);
					dialog.open();
				}
				return;
			}
			dialog.setOldFile(firstFile);
			dialog.open();
		} 
	}
}
