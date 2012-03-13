package org.vclipse.vcml.diff.compare;

import java.io.IOException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.xtext.util.StringInputStream;
import org.vclipse.vcml.diff.VcmlDiffPlugin;

import com.google.inject.Inject;

public final class ExtractDifferencesJob extends Job {

	private IFile resultFile;
	private IFile newFile;
	private IFile oldFile;

	private final VcmlCompare compare;
	
	@Inject
	public ExtractDifferencesJob(VcmlCompare compare) {
		super("Export job for differences betweeen 2 vcml files.");
		this.compare = compare;
	}

	public void setResultFile(IFile resultFile) {
		this.resultFile = resultFile;
	}
	
	public void setNewFile(IFile newFile) {
		this.newFile = newFile;
	}
	
	public void setOldFile(IFile oldFile) {
		this.oldFile = oldFile;
	}
	
	@Override
	protected IStatus run(IProgressMonitor monitor) {
		monitor.beginTask("Exporting differences...", IProgressMonitor.UNKNOWN);
		try  {
			monitor.subTask("Creating export file...");
			if(!resultFile.exists()) {
				resultFile.create(new StringInputStream(""), true, monitor);
			} else {
				resultFile.setContents(new StringInputStream(""), true, true, monitor);
			}
			monitor.worked(5);
			compare.compare(oldFile, newFile, resultFile, monitor);
			return Status.OK_STATUS;
		} catch(CoreException exception) {
			VcmlDiffPlugin.showErrorDialog(exception, "Error during differences export", exception.getMessage());
			return Status.CANCEL_STATUS;
		} catch(IOException exception) {
			VcmlDiffPlugin.showErrorDialog(exception, "Error during differences export", exception.getMessage());
			return Status.CANCEL_STATUS;
		} catch(InterruptedException exception) {
			VcmlDiffPlugin.showErrorDialog(exception, "Error during differences export", exception.getMessage());
			return Status.CANCEL_STATUS;
		} finally {
			monitor.done();
		}
	}

}
