/*******************************************************************************
 * Copyright (c) 2010 webXcerpt Software GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    webXcerpt Software GmbH - initial creator
 *******************************************************************************/
package org.vclipse.vcml.outline.actions;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.ecore.resource.Resource;

/**
 * @param <T>
 */
public interface IVCMLOutlineActionHandler<T> {
	
	/**
	 * @param object
	 * @param monitor
	 */
	public void run(T object, Resource resource, IProgressMonitor monitor) throws Exception;
	
	/**
	 * @param object
	 * @return
	 */
	public boolean isEnabled(T object);
	
}
