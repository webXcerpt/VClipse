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
package org.vclipse.vcml.serializer;

import java.io.IOException;
import java.io.Writer;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.xtext.nodemodel.ICompositeNode;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;
import org.eclipse.xtext.resource.SaveOptions;
import org.eclipse.xtext.serializer.impl.Serializer;
import org.eclipse.xtext.util.ReplaceRegion;
import org.vclipse.vcml.formatting.ConstraintPrettyPrinter;
import org.vclipse.vcml.formatting.ProcedurePrettyPrinter;
import org.vclipse.vcml.formatting.VCMLPrettyPrinter;
import org.vclipse.vcml.vcml.ConditionSource;
import org.vclipse.vcml.vcml.ConstraintSource;
import org.vclipse.vcml.vcml.ProcedureSource;

import com.google.inject.Inject;

public class VCMLSerializer extends Serializer {

	@Inject
	private VCMLPrettyPrinter prettyPrinter;
	
	@Inject
	private ProcedurePrettyPrinter procedurePrinter;
	
	@Inject
	private ConstraintPrettyPrinter constraintPrinter;
	
	@Override
	public String serialize(EObject object, SaveOptions options) {
		String text = getDependencyObjectText(object);
		return text.isEmpty() ? prettyPrinter.prettyPrint(object) : text;
	}
	
	@Override
	public void serialize(EObject object, Writer writer, SaveOptions options) throws IOException {
		writer.append(serialize(object, options));
		writer.flush();
	}

	@Override
	public ReplaceRegion serializeReplacement(EObject object, SaveOptions options) {
		String text = getDependencyObjectText(object);
		if(text.isEmpty()) {
			return super.serializeReplacement(object, options);
		} else {
			ICompositeNode node = NodeModelUtils.findActualNodeFor(object);
			int offset = node.getOffset();
			int length = node.getLength();
			return new ReplaceRegion(offset, length, text);
		}
	}
	
	private String getDependencyObjectText(EObject object) {
		EObject rootContainer = EcoreUtil.getRootContainer(object);
		if(object instanceof ConstraintSource || rootContainer instanceof ConstraintSource) {
			return constraintPrinter.prettyPrint(object);
		} else if(object instanceof ConditionSource || rootContainer instanceof ConditionSource) {
			return procedurePrinter.prettyPrint(object);
		} else if(object instanceof ProcedureSource || rootContainer instanceof ProcedureSource) {
			return procedurePrinter.prettyPrint(object);
		} else {
			StringBuffer buffer = new StringBuffer();
			String content = constraintPrinter.prettyPrint(object);
			if(!content.isEmpty()) {
				buffer.append(content);
				buffer.append(",");
			} else {
				content = procedurePrinter.prettyPrint(object);	
				if(!content.isEmpty()) {
					buffer.append(content);					
				}
			}
			return buffer.toString();
		}
	}
}
