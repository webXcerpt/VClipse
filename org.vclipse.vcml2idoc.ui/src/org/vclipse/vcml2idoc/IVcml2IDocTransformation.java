/**
 * 
 */
package org.vclipse.vcml2idoc;

import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * @author as
 *
 */
public interface IVcml2IDocTransformation {

	/**
	 * @param iterator
	 * @param monitor
	 * @throws InvocationTargetException
	 */
	public void tranform(final Iterator<IFile> iterator, final IProgressMonitor monitor) throws InvocationTargetException;
}
