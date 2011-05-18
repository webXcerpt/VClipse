/*******************************************************************************
 * Copyright (c) 2010 webXcerpt Software GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *  
 * Contributors:
 *     webXcerpt Software GmbH - initial creator
 ******************************************************************************/
/***  ****//**
 * 
 */
package org.vclipse.connection;

import java.util.List;

/**
 * @author as
 * 
 */
public interface IConnectionDataStorage {

	/**
	 * @return
	 */
	public List<IConnection> loadConnectionData(boolean loadPassword);
	
	/**
	 * @param connections
	 */
	public void storeConnectionData(List<IConnection> connections, int currentConnectionIndex);
	
	/**
	 * @return
	 */
	public int getCurrentConnectionIndex();
}
