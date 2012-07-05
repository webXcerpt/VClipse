package org.vclipse.vcml2idoc.actions;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;
import org.vclipse.base.ui.FileListHandler;
import org.vclipse.vcml2idoc.VCML2IDocPlugin;
import org.vclipse.vcml2idoc.transformation.IVCML2IDocTransformation;

import com.google.inject.Inject;

public class TransformantionHandler extends FileListHandler {

	@Inject
	private IVCML2IDocTransformation transformation;
	
	@Override
	public void handleListVariable(final Iterable<IFile> collection, ExecutionEvent event) {
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindow(event);
		if(window != null) {
			Job job = new Job("Conversion of vcml files to idoc files.") {
				@Override
				protected IStatus run(IProgressMonitor monitor) {
					try {
						transformation.transform(collection, monitor);
						return Status.OK_STATUS;
					} catch (InvocationTargetException exception) {
						VCML2IDocPlugin.log(exception.getMessage(), exception);
						return Status.CANCEL_STATUS;
					}
				}
			};
			job.schedule();
		}
	}
}
