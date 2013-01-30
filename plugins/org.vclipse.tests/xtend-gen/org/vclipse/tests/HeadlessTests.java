/*******************************************************************************
 * Copyright (c) 2013 webXcerpt Software GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *  
 * Contributors:
 *     	webXcerpt Software GmbH - initial creator
 * 		www.webxcerpt.com
 ******************************************************************************/
package org.vclipse.tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import org.vclipse.tests.compare.VCMLCompareTests;
import org.vclipse.tests.refactoring.ConfigurationTests;
import org.vclipse.tests.refactoring.LabelsTests;
import org.vclipse.tests.refactoring.SearchTests;

/**
 * Runner for headless tests
 */
@RunWith(Suite.class)
@SuiteClasses({
	
	VCMLCompareTests.class,  
	ConfigurationTests.class,
	LabelsTests.class,
	SearchTests.class
	
	})
public class HeadlessTests {

}
