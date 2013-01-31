/*******************************************************************************
 * Copyright (c) 2010 - 2013 webXcerpt Software GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *  
 * Contributors:
 *     	webXcerpt Software GmbH - initial creator
 * 		www.webxcerpt.com
 ******************************************************************************/
package org.vclipse.configscan;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.vclipse.configscan.impl.model.TestRun;
import org.vclipse.configscan.views.ConfigScanViewInput;

import com.google.common.collect.Lists;

public class JUnitTestUtils {

	public List<ConfigScanViewInput> createConfigScanViewInputs(String ... names) {
		List<ConfigScanViewInput> inputs = Lists.newArrayList();
		for(String name : names) {
			ConfigScanViewInput input = new ConfigScanViewInput();
			input.setConfigurationName(name);
			input.setDate(null, IConfigScanConfiguration.DATE_FORMAT_UI_ENTRIES);
			input.setTestRuns(new ArrayList<TestRun>());
			inputs.add(input);
		}
		return inputs;
	}
	
	public void clean(String path) throws IOException {
		BufferedWriter out = new BufferedWriter(new FileWriter(path));
		out.write("");
		out.close();
	}
}
