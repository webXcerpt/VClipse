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
package org.vclipse.vcml.resource;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.linking.lazy.LazyURIEncoder;
import org.eclipse.xtext.parsetree.AbstractNode;
import org.eclipse.xtext.parsetree.NodeUtil;
import org.eclipse.xtext.util.Triple;
import org.eclipse.xtext.util.Tuples;

public class VCMLURIEncoder extends LazyURIEncoder {

	private static final String VCML_LINK = "vcmlLink_";
	private static final String SEP = "::";

	/**
	 * encodes the given three parameters into a string, so that they can be
	 * retrieved from a resource using {@link #decode(Resource, String)}
	 * 
	 * @param obj
	 * @param ref
	 * @param node
	 * @return
	 */
	@Override
	public String encode(EObject obj, EReference ref, AbstractNode node) {
		StringBuilder fragment = new StringBuilder(80).append(VCML_LINK).append(SEP);
		fragment.append(obj.eResource().getURIFragment(obj)).append(SEP);
		appendReferenceURI(fragment, ref);
		fragment.append(SEP);
		getRelativePath(fragment, NodeUtil.getNodeAdapter(obj).getParserNode(), node);
		return fragment.toString();
	}

	protected void appendReferenceURI(StringBuilder builder, EReference ref) {
		EPackage pack = ref.getEContainingClass().getEPackage();
		builder.append(pack.getNsURI());
		builder.append('#');
		builder.append(ref.getEContainingClass().getName());
		builder.append('/');
		builder.append(ref.getFeatureID());
	}

	/**
	 * decodes the uriFragment
	 * 
	 * @param res
	 * @param uriFragment
	 * @return
	 */
	@Override
	public Triple<EObject, EReference, AbstractNode> decode(Resource res, String uriFragment) {
		String[] split = uriFragment.split(SEP);
		EObject source = res.getEObject(split[1]);
		String reference = split[2];
		int referenceHash = reference.lastIndexOf('#');
		int referenceSlash = reference.lastIndexOf('/');
		EPackage pack = res.getResourceSet().getPackageRegistry().getEPackage(reference.substring(0, referenceHash));
		EClass classifier = (EClass) pack.getEClassifier(reference.substring(referenceHash+1, referenceSlash));
		EReference ref = (EReference) classifier.getEStructuralFeature(Integer.parseInt(reference.substring(referenceSlash + 1)));
		AbstractNode text = getNode(NodeUtil.getNodeAdapter(source).getParserNode(), split[3]);
		return Tuples.create(source, ref, text);
	}

	@Override
	public boolean isCrossLinkFragment(Resource res, String s) {
		return s.startsWith(VCML_LINK);
	}

	
}
