/*******************************************************************************
 * Copyright (c) 2010 webXcerpt Software GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    webXcerpt Software GmbH - initial creator
 ******************************************************************************/
package org.vclipse.vcml.formatting;

import java.io.IOException;
import java.io.Writer;

import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.formatting.IFormatter;
import org.eclipse.xtext.parsetree.reconstr.IParseTreeConstructor;
import org.eclipse.xtext.parsetree.reconstr.IParseTreeConstructor.TreeConstructionReport;
import org.eclipse.xtext.parsetree.reconstr.ITokenStream;
import org.eclipse.xtext.parsetree.reconstr.Serializer;
import org.eclipse.xtext.resource.SaveOptions;
import org.eclipse.xtext.validation.IConcreteSyntaxValidator;
import org.vclipse.vcml.VCMLPlugin;
import org.vclipse.vcml.utils.ISapConstants;
import org.vclipse.vcml.vcml.ConditionSource;
import org.vclipse.vcml.vcml.ConstraintSource;
import org.vclipse.vcml.vcml.ProcedureSource;

import com.google.inject.Inject;

public class VCMLSerializer extends Serializer {

	@Inject
	public VCMLSerializer(IParseTreeConstructor ptc, IFormatter fmt,
			IConcreteSyntaxValidator val) {
		super(ptc, fmt, val);
		// TODO Auto-generated constructor stub
	}

	@Override
	public String serialize(EObject obj, SaveOptions options) {
		if (usePrettyPrinter()) { 
			return serialize(obj);
		} else {
			return super.serialize(obj, options);
		}
	}

	@Override
	public void serialize(final EObject obj, final Writer writer, final SaveOptions options) throws IOException {
		writer.append(serialize(obj));
		writer.flush();
	}

	@Override
	public TreeConstructionReport serialize(final EObject obj, final ITokenStream tokenStream, final SaveOptions options) throws IOException {
		if (usePrettyPrinter()) { 
			// TODO how to implement this with VCMLPrettyPrinter
			throw new IllegalArgumentException("VCMLPrettyPrinter is not a serializer to ITokenStream");
		} else {
			return super.serialize(obj, tokenStream, options);
		}
	}

	@Override
	public String serialize(EObject obj) {
		if (usePrettyPrinter()) {
			if (obj instanceof ConditionSource || obj instanceof ProcedureSource) {
				return new ProcedurePrettyPrinter().prettyPrint(obj);
			} else if (obj instanceof ConstraintSource) {
				return new ConstraintPrettyPrinter().prettyPrint(obj);
			} else {
				return new VCMLPrettyPrinter().prettyPrint(obj);
			}
		} else {
			return super.serialize(obj);
		}
	}

	// FIXME
	/*
	@Override
	public TreeConstructionReport serialize(EObject obj, OutputStream out, CompositeNode node, boolean format) throws IOException {
		if (usePrettyPrinter()) {
			out.write(serialize(obj).getBytes("UTF-8"));
			return new TreeConstructionReport() {
				public TreeConstructionDiagnostic getDiagnostic() {
					return null;
				}
				public boolean isSuccess() {
					return true;
				}
			};
		} else {
			return super.serialize(obj, out, node, format);
		}
	}
*/

	private boolean usePrettyPrinter() {
		return Platform.getPreferencesService().getBoolean(VCMLPlugin.PREFERENCES_ID, ISapConstants.USE_PRETTY_PRINTER, false, null);
	}

}
