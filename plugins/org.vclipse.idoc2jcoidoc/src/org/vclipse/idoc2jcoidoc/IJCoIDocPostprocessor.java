/*******************************************************************************
 * Copyright (c) 2009 webXcerpt Software GmbH (http://www.webxcerpt.com).
 * All rights reserved.
 *******************************************************************************/
package org.vclipse.idoc2jcoidoc;

import java.util.List;

import com.sap.conn.idoc.IDocDocument;
import com.sap.conn.idoc.IDocException;

public interface IJCoIDocPostprocessor {

	public void postprocess(List<IDocDocument> idocs) throws IDocException;
	
}
