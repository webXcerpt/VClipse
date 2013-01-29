/*******************************************************************************
 * Copyright (c) 2013 webXcerpt Software GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *		webXcerpt Software GmbH - initial creator
 *		www.webxcerpt.com
 ******************************************************************************/
package org.vclipse.base.compare;

import java.io.IOException;
import java.io.InputStream;

import com.google.common.io.InputSupplier;

public class DefaultInputSupplier implements InputSupplier<InputStream> {

	protected InputStream stream;
	
	public DefaultInputSupplier(InputStream stream) {
		this.stream = stream;
	}
	
	@Override
	public InputStream getInput() throws IOException {
		return stream;
	}
}
