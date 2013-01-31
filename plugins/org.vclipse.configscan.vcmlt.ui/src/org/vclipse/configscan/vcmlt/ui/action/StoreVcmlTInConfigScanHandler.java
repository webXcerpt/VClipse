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
package org.vclipse.configscan.vcmlt.ui.action;

import java.util.HashMap;

import org.eclipse.core.resources.IFile;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.resource.XtextResourceSet;
import org.vclipse.base.naming.INameProvider;
import org.vclipse.configscan.IConfigScanXMLProvider;
import org.vclipse.configscan.ITestObjectFilter;
import org.vclipse.configscan.actions.AbstractStoreTestCaseHandler;
import org.vclipse.configscan.utils.DocumentUtility;
import org.vclipse.configscan.vcmlt.vcmlT.Model;
import org.vclipse.configscan.vcmlt.vcmlT.TestCase;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.sap.conn.jco.JCoException;

public class StoreVcmlTInConfigScanHandler extends AbstractStoreTestCaseHandler {

	@Inject 
	private IConfigScanXMLProvider configScanXMLProvider;

	@Inject
	private INameProvider sapNameProvider;
	
	@Inject
	private DocumentUtility documentUtility;
	
	@Inject
	private ITestObjectFilter filter;
	
	@Override
	protected void storeTestcaseFileInConfigScan(IFile file) throws JCoException {
		Resource resource = new XtextResourceSet().getResource(URI.createURI(file.getLocationURI().toString()), true);
		if (resource == null) {
			throw new IllegalArgumentException("Could not read resource for " + file.getName());
		}
		EList<EObject> contents = resource.getContents();
		if (contents.size()==0) {
			throw new IllegalArgumentException("Resource has no contents");
		}
		if (!(contents.get(0) instanceof Model)) {
			throw new IllegalArgumentException("Contents of resource are not a VCMLT test case");
		}
		Model model = (Model) contents.get(0);
		TestCase testcase = model.getTestcase();
		if (testcase==null) {
			throw new IllegalArgumentException("Testcase element required in VCMLT test-case file");
		}

		Document doc = configScanXMLProvider.transform(model, filter, new HashMap<Element, URI>(), Maps.newHashMap());
		String xmlString = documentUtility.serialize(doc);
		String matNr = sapNameProvider.getName(testcase.getItem());
		String docNumber = testcase.getDocument();
		if (docNumber == null) {
			docNumber = testcase.getItem().getName();
		}
		String part = testcase.getPart();
		storeTestcaseInConfigScan(xmlString, matNr, docNumber, testcase.getDescription(), testcase.getVersion(), part==null ? "VCM" : part);
	}
}