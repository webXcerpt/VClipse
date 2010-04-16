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
package org.vclipse.vcml.formatting;

import java.io.IOException;
import java.io.OutputStream;

import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.formatting.IFormatter;
import org.eclipse.xtext.parsetree.CompositeNode;
import org.eclipse.xtext.parsetree.reconstr.IHiddenTokenMerger;
import org.eclipse.xtext.parsetree.reconstr.IParseTreeConstructor;
import org.eclipse.xtext.parsetree.reconstr.ITokenStream;
import org.eclipse.xtext.parsetree.reconstr.SerializerUtil;
import org.eclipse.xtext.parsetree.reconstr.IParseTreeConstructor.TreeConstructionDiagnostic;
import org.eclipse.xtext.parsetree.reconstr.IParseTreeConstructor.TreeConstructionReport;
import org.vclipse.vcml.VCMLPlugin;
import org.vclipse.vcml.utils.ISapConstants;
import org.vclipse.vcml.vcml.ConditionSource;
import org.vclipse.vcml.vcml.ConstraintSource;
import org.vclipse.vcml.vcml.ProcedureSource;

import com.google.inject.Inject;

public class VCMLSerializerUtil extends SerializerUtil {

	@Inject
	public VCMLSerializerUtil(IParseTreeConstructor ptc, IFormatter fmt, IHiddenTokenMerger mgr) {
		super(ptc, fmt, mgr);
	}

	@Override
	public String serialize(EObject obj, boolean format) {
		if (usePrettyPrinter()) { 
			return serialize(obj);
		} else {
			return super.serialize(obj, format);
		}
	}

	@Override
	public TreeConstructionReport serialize(EObject obj, ITokenStream out,
			CompositeNode node, boolean format) throws IOException {
		if (usePrettyPrinter()) { 
			// TODO how to implement this with VCMLPrettyPrinter
			throw new IllegalArgumentException("VCMLPrettyPrinter is not a serializer to ITokenStream");
		} else {
			return super.serialize(obj, out, node, format);
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

	private boolean usePrettyPrinter() {
		return Platform.getPreferencesService().getBoolean(VCMLPlugin.ID, ISapConstants.USE_PRETTY_PRINTER, false, null);
	}

}
