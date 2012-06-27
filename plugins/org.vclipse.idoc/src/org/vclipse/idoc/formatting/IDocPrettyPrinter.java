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
package org.vclipse.idoc.formatting;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.vclipse.base.VClipseStrings;
import org.vclipse.idoc.iDoc.Field;
import org.vclipse.idoc.iDoc.IDoc;
import org.vclipse.idoc.iDoc.Import;
import org.vclipse.idoc.iDoc.Model;
import org.vclipse.idoc.iDoc.NumberField;
import org.vclipse.idoc.iDoc.Segment;
import org.vclipse.idoc.iDoc.StringField;
import org.vclipse.idoc.iDoc.util.IDocSwitch;

import de.uka.ilkd.pp.DataLayouter;
import de.uka.ilkd.pp.NoExceptions;
import de.uka.ilkd.pp.StringBackend;

public class IDocPrettyPrinter extends IDocSwitch<DataLayouter<NoExceptions>> {

	private DataLayouter<NoExceptions> layouter;

	private static final int INDENTATION = 2;

	public String prettyPrint(final EObject object) {
		final StringBuilder sb = new StringBuilder();
		// FIXME -> PreferenceSetting ?
		int lineLength = 70;
		
		layouter = new DataLayouter<NoExceptions>(
				new StringBackend(sb, lineLength), INDENTATION);
		doSwitch(object);
		layouter.close();
		return sb.toString();
	}

	@Override
	public DataLayouter<NoExceptions> caseModel(final Model object) {
		layouter.beginC(0);
		for(Import curImport : object.getImports()) {
			layouter.print("import ").print(curImport.getImportURI()).brk();
		}
		layouter.nl();
		for(IDoc idoc : object.getIdocs()) {
			doSwitch(idoc).brk();
		}
		return layouter.end();
	}

	@Override
	public DataLayouter<NoExceptions> caseIDoc(final IDoc object) {
		layouter.beginC().print("idoc ").print(quote(object.getName()));
		layouter.print(" " + object.getType());
		layouter.print(" " + object.getMessageType());
		layouter.print(" {");
		{
			printFields(object.getFields());
			final EList<Segment> segments = object.getSegments();
			if(!segments.isEmpty()) {
				layouter.brk();
			}
			for(int i=0, size=segments.size(); i<size; i++) {
				doSwitch(segments.get(i));
				if(i < size-1) {
					layouter.brk();
				}
			}
		}
		return layouter.brk(1, -INDENTATION).print("}").end();
	}

	@Override
	public DataLayouter<NoExceptions> caseSegment(final Segment segment) {
		layouter.beginC().print("segment ").print(segment.getType());
		layouter.print(" {").brk();
		printFields(segment.getFields());
		EList<Segment> segments = segment.getSegments();
		if(!segments.isEmpty()) {
			layouter.brk();
		}
		for(int i=0, size=segments.size(); i<size; i++) {
			doSwitch(segments.get(i));
			if(i < size-1) {
				layouter.brk();
			}
		}
		return layouter.brk(1, -INDENTATION).print("}").end();
	}
	
	@Override
	public DataLayouter<NoExceptions> caseStringField(final StringField object) {
		return layouter.print(object.getName() + "=" + quote(object.getValue()));
	}

	@Override
	public DataLayouter<NoExceptions> caseNumberField(final NumberField object) {
		return layouter.print(object.getName() + "=" + object.getValue());
	}
	
	private void printFields(final EList<Field> fields) {
		for(int i=0, size=fields.size(), finish = size-1; i<size; i++) {
			doSwitch(fields.get(i));
			if(i < finish) {
				layouter.print(" ");
			}
		}
	}

	private String quote(final String string) {
		return '"' + VClipseStrings.convertToJavaString(string) + '"';
	}
}
