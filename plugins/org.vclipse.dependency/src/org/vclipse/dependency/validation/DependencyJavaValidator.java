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
package org.vclipse.dependency.validation;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Iterator;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.validation.Check;
import org.vclipse.vcml.utils.DependencySourceUtils;
import org.vclipse.vcml.vcml.BinaryExpression;
import org.vclipse.vcml.vcml.CharacteristicReference_C;
import org.vclipse.vcml.vcml.CharacteristicReference_P;
import org.vclipse.vcml.vcml.Comparison;
import org.vclipse.vcml.vcml.Expression;
import org.vclipse.vcml.vcml.MDataCharacteristic_C;
import org.vclipse.vcml.vcml.MDataCharacteristic_P;
import org.vclipse.vcml.vcml.NumericLiteral;
import org.vclipse.vcml.vcml.SymbolicLiteral;
import org.vclipse.vcml.vcml.UnaryExpression;
import org.vclipse.vcml.vcml.VCObject;
import org.vclipse.vcml.vcml.VcmlModel;
import org.vclipse.vcml.vcml.VcmlPackage;

import com.google.common.base.Charsets;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.inject.Inject;
 
public class DependencyJavaValidator extends AbstractDependencyJavaValidator {

	@Inject
	private DependencySourceUtils sourceUtils;
	
	protected void checkSource(EObject source) {
		
		// FIXME currently deactivated
		// checkSource is currently deactivated since the implementation below is very bad for performance (VCML resources are parsed very often)
		
		if (true) return;
		Resource sourceResource = source.eResource();
		URI vcmlUri = sourceUtils.getVcmlResourceURI(sourceResource.getURI());
		if (vcmlUri != null) {
			final String fileName = sourceResource.getURI().trimFileExtension()
					.lastSegment();
			String sourceName = source.eClass().getName();
			String objectName = sourceName.replace("Source", "");
			String sourceObjectName_decoded;
			try {
				sourceObjectName_decoded = URLDecoder.decode(fileName,
						Charsets.UTF_8.toString());
			} catch (UnsupportedEncodingException e) {
				sourceObjectName_decoded = fileName;
			}
			final String sourceObjectName = sourceObjectName_decoded;
			Resource vcmlResource = sourceResource.getResourceSet()
					.getResource(vcmlUri, true);
			EList<EObject> contents = vcmlResource.getContents();
			if (!contents.isEmpty()) {
				VcmlModel vcmlModel = (VcmlModel) contents.get(0);
				Iterator<VCObject> iterator = Iterables.filter(
						vcmlModel.getObjects(), new Predicate<VCObject>() {
							public boolean apply(VCObject object) {
								return object.getName().equalsIgnoreCase(
										sourceObjectName);
							}
						}).iterator();
				if (!iterator.hasNext()) {
					warning(objectName + " object does not exist for the "
							+ sourceName, source, null,
							"Not_Existent_Source_Object", new String[] {
									sourceName, fileName,
									vcmlResource.getURI().toString() });
				}
			}
		}
	}
	
	@Check
	public void checkComparison(Comparison comparison) {
		Expression left = comparison.getLeft();
		Expression right = comparison.getRight();
		if (isConstant(left) && isConstant(right)) {
			error("Simple condition without variables not allowed.", VcmlPackage.Literals.COMPARISON__OPERATOR);
		}
	}

	private boolean isConstant(Expression expression) {
		if (expression instanceof CharacteristicReference_C || expression instanceof CharacteristicReference_P || expression instanceof MDataCharacteristic_C || expression instanceof MDataCharacteristic_P) {
			return false;
		}
		if (expression instanceof NumericLiteral || expression instanceof SymbolicLiteral) {
			return true;
		}
		if (expression instanceof BinaryExpression) {
			return isConstant(((BinaryExpression)expression).getLeft()) && isConstant(((BinaryExpression)expression).getRight());
		}
		if (expression instanceof UnaryExpression) {
			return isConstant(((UnaryExpression)expression).getExpression());
		}
		// TODO add other uses
		return false;
	}

}
