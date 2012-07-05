/**
 * 
 */
package org.vclipse.idoc2jcoidoc;

import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.vclipse.idoc.iDoc.Model;

import com.google.inject.ImplementedBy;
import com.sap.conn.idoc.IDocDocument;
import com.sap.conn.jco.JCoException;

@ImplementedBy(DefaultIDoc2JCoIDocProcessor.class)
public interface IIDoc2JCoIDocProcessor {

	public List<IDocDocument> transform(Model idocModel, IProgressMonitor monitor) throws JCoException, CoreException;
}
