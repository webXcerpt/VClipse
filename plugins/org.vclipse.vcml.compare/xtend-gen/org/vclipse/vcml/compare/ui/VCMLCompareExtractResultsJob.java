/**
 * Copyright (c) 2010 - 2013 webXcerpt Software GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     	webXcerpt Software GmbH - initial creator
 * 		www.webxcerpt.com
 */
package org.vclipse.vcml.compare.ui;

import com.google.common.base.Objects;
import com.google.inject.Inject;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.xtext.util.StringInputStream;
import org.eclipse.xtext.xbase.lib.Exceptions;
import org.vclipse.vcml.compare.VCMLCompareOperation;
import org.vclipse.vcml.compare.VCMLComparePlugin;

/**
 * Job extracting the compare results.
 */
@SuppressWarnings("all")
public class VCMLCompareExtractResultsJob extends WorkspaceJob {
  private VCMLCompareOperation compareOperation;
  
  private IFile left;
  
  private IFile right;
  
  private IFile result;
  
  @Inject
  public VCMLCompareExtractResultsJob(final VCMLCompareOperation compareOperation) {
    super("Job extracting differences between 2 vcml files.");
    this.compareOperation = compareOperation;
  }
  
  /**
   * Setter for files the compare operation is running on.
   */
  protected IFile setFileToCompare(final IFile file, final /* DifferenceSource */Object source) {
    throw new Error("Unresolved compilation problems:"
      + "\nThe method or field LEFT is undefined for the type VCMLCompareExtractResultsJob"
      + "\n== cannot be resolved");
  }
  
  /**
   * Setter for the file containing results after extraction.
   */
  protected IFile setResultFile(final IFile file) {
    IFile _result = this.result = file;
    return _result;
  }
  
  /**
   * Creates the result file and executes the compare operation.
   */
  public IStatus runInWorkspace(final IProgressMonitor monitor) throws CoreException {
    StringBuffer _stringBuffer = new StringBuffer("Exporting differences between ");
    final StringBuffer messageBuffer = _stringBuffer;
    String _name = this.left.getName();
    messageBuffer.append(_name);
    messageBuffer.append(" and ");
    String _name_1 = this.right.getName();
    messageBuffer.append(_name_1);
    String _string = messageBuffer.toString();
    monitor.beginTask(_string, IProgressMonitor.UNKNOWN);
    try {
      boolean _isAccessible = this.result.isAccessible();
      boolean _not = (!_isAccessible);
      if (_not) {
        StringInputStream _stringInputStream = new StringInputStream("");
        this.result.create(_stringInputStream, true, monitor);
      }
      this.compareOperation.compare(this.right, this.left, this.result, monitor);
      monitor.done();
      return Status.OK_STATUS;
    } catch (final Throwable _t) {
      if (_t instanceof Exception) {
        final Exception exception = (Exception)_t;
        monitor.done();
        final String message = exception.getMessage();
        String _xifexpression = null;
        boolean _or = false;
        boolean _equals = Objects.equal(message, null);
        if (_equals) {
          _or = true;
        } else {
          boolean _isEmpty = message.isEmpty();
          _or = (_equals || _isEmpty);
        }
        if (_or) {
          Class<? extends Exception> _class = exception.getClass();
          String _simpleName = _class.getSimpleName();
          _xifexpression = _simpleName;
        } else {
          _xifexpression = message;
        }
        VCMLComparePlugin.log(IStatus.ERROR, _xifexpression);
        return Status.CANCEL_STATUS;
      } else {
        throw Exceptions.sneakyThrow(_t);
      }
    }
  }
}
