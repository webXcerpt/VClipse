/*******************************************************************************
 * Copyright (c) 2010 webXcerpt Software GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     webXcerpt Software GmbH - initial creator
 ******************************************************************************/
package org.vclipse.idoc.formatting;

import java.io.IOException;
import java.io.Writer;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.formatting.IFormatter;
import org.eclipse.xtext.parsetree.reconstr.IParseTreeConstructor;
import org.eclipse.xtext.parsetree.reconstr.Serializer;
import org.eclipse.xtext.resource.SaveOptions;
import org.eclipse.xtext.validation.IConcreteSyntaxValidator;

import com.google.inject.Inject;

public class IDocSerializer extends Serializer {
	
	@Inject
	public IDocSerializer(final IParseTreeConstructor treeConstructor, 
			final IFormatter formatter, final IConcreteSyntaxValidator validator) {
		super(treeConstructor, formatter, validator);
	}

	@Override
	public String serialize(final EObject object, final SaveOptions options) {
		return serialize(object);
	}
	
	public void serialize(final EObject obj, final Writer writer, final SaveOptions options) throws IOException {
		writer.append(serialize(obj));
		writer.flush();
	}
	
	@Override
	public String serialize(final EObject object) {
		return new IDocPrettyPrinter().prettyPrint(object);
	}
}
