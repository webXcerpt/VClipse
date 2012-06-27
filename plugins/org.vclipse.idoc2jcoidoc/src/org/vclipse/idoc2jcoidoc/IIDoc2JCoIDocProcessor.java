/**
 * 
 */
package org.vclipse.idoc2jcoidoc;

import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.vclipse.idoc.iDoc.Model;

import com.sap.conn.idoc.IDocDocument;
import com.sap.conn.jco.JCoException;

/**
 *
 */
public interface IIDoc2JCoIDocProcessor {

	/**
	 * @param idocModel
	 * @param monitor
	 * @return
	 * @throws JCoException
	 * @throws CoreException
	 */
	public List<IDocDocument> transform(final Model idocModel, final IProgressMonitor monitor) throws JCoException, CoreException;
}
