package org.vclipse.vcml.compare.ui;

import com.google.common.collect.Iterables;
import com.google.inject.Inject;
import java.util.Iterator;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IStatus;
import org.vclipse.base.ui.FileListHandler;
import org.vclipse.vcml.compare.VCMLComparePlugin;
import org.vclipse.vcml.compare.ui.VCMLCompareExtractDifferencesDialog;

/**
 * Invokes the validation of the selection made in the project explorer on vcml files and the call of the compare dialog.
 */
@SuppressWarnings("all")
public class VCMLCompareInvokeExportHandler extends FileListHandler {
  @Inject
  private VCMLCompareExtractDifferencesDialog dialog;
  
  /**
   * The selected files are combined to an iterable in the FileListHandler
   */
  public void handleListVariable(final Iterable<IFile> iterable, final ExecutionEvent event) {
    int _size = Iterables.size(iterable);
    boolean _greaterThan = (_size > 2);
    if (_greaterThan) {
      VCMLComparePlugin.log(IStatus.ERROR, "Only 2 files are allowed for this action.");
      return;
    }
    final Iterator<IFile> iterator = iterable.iterator();
    int _size_1 = Iterables.size(iterable);
    boolean _equals = (_size_1 == 1);
    if (_equals) {
      IFile _next = iterator.next();
      this.dialog.setLeft(_next);
      this.dialog.open();
      return;
    }
    final IFile first = iterator.next();
    final IFile second = iterator.next();
    long _localTimeStamp = first.getLocalTimeStamp();
    long _localTimeStamp_1 = second.getLocalTimeStamp();
    boolean _greaterThan_1 = (_localTimeStamp > _localTimeStamp_1);
    if (_greaterThan_1) {
      this.dialog.setRight(first);
      this.dialog.setLeft(second);
      this.dialog.open();
    } else {
      this.dialog.setRight(second);
      this.dialog.setLeft(first);
      this.dialog.open();
    }
  }
}
