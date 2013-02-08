/*******************************************************************************
 * Copyright (c) 2010 - 2013 webXcerpt Software GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *  
 * Contributors:
 *     	webXcerpt Software GmbH - initial creator and others
 * 		www.webxcerpt.com
 ******************************************************************************/
package org.vclipse.tests.vcmlt;

import java.util.HashMap;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.junit4.InjectWith;
import org.eclipse.xtext.junit4.XtextRunner;
import org.eclipselabs.xtext.utils.unittesting.XtextTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.vclipse.configscan.ITestObjectFilter;
import org.vclipse.configscan.utils.DocumentUtility;
import org.vclipse.configscan.vcmlt.builder.VcmlTConfigScanXMLProvider;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.google.common.collect.Maps;
import com.google.inject.Inject;

@RunWith(XtextRunner.class)
@InjectWith(VcmlTInjectorProviderWithVCML.class)
public class VcmlTConfigScanXMLProviderTest extends XtextTest {


	@Inject
	VcmlTConfigScanXMLProvider csXmlProvider;
	
	@Inject
	DocumentUtility documentUtility;
	
	@Inject
	ITestObjectFilter filter;
	
	@Test
	public void transformToXML() {
		ignoreSerializationDifferences();
		testFile("simpleTestFile.vcmlt", "ABC.vcml");
		EObject root = getModelRoot();
		HashMap<Element, URI> map = Maps.newHashMap();
		Document document = csXmlProvider.transform(root, filter, map, null);
		System.err.println(documentUtility.serialize(document));
	}
}
