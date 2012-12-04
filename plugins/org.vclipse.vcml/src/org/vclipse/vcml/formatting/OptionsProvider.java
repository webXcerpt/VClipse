/*******************************************************************************
 * Copyright (c) 2012 webXcerpt Software GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    webXcerpt Software GmbH - initial creator
 ******************************************************************************/
package org.vclipse.vcml.formatting;

import java.util.Map;

import com.google.common.collect.Maps;
import com.google.inject.Provider;

public class OptionsProvider implements Provider<Map<String, String>>{

	public static final String VCML_FILE_NAME = "vcml_file_name";
	
	public static final String VCML_FILE_URI = "vcml_file_uri";
	
	private Map<String, String> options;
	
	public OptionsProvider() {
		options = Maps.newHashMap();
	}
	
	public Map<String, String> get() {
		return options;
	}
}
