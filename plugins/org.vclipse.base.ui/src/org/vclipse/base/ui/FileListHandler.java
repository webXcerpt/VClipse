package org.vclipse.base.ui;

import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.e4.ui.workbench.modeling.ExpressionContext;

import com.google.common.collect.Iterables;

public abstract class FileListHandler extends AbstractHandler {

	public Object execute(ExecutionEvent event) throws ExecutionException {
		Object appContext = event.getApplicationContext();
		if(appContext instanceof ExpressionContext) {
			Object defVariable = ((ExpressionContext)appContext).getDefaultVariable();
			if(defVariable instanceof List<?>) {
				Iterable<IFile> filteredContent = Iterables.filter((List<?>)defVariable, IFile.class);
				handleListVariable(filteredContent, event);				
			}
		}
		return null;
	}
	
	public abstract void handleListVariable(Iterable<IFile> collection, ExecutionEvent event);
}
