/** 
 * Copyright (c) 2010 - 2013 webXcerpt Software GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * webXcerpt Software GmbH - initial creator
 * www.webxcerpt.com
 */
package org.vclipse.idoc.formatting

import org.eclipse.emf.common.util.EList
import org.eclipse.emf.ecore.EObject
import org.vclipse.base.VClipseStrings
import org.vclipse.idoc.iDoc.Field
import org.vclipse.idoc.iDoc.IDoc
import org.vclipse.idoc.iDoc.Import
import org.vclipse.idoc.iDoc.Model
import org.vclipse.idoc.iDoc.NumberField
import org.vclipse.idoc.iDoc.Segment
import org.vclipse.idoc.iDoc.StringField
import org.vclipse.idoc.iDoc.util.IDocSwitch
import de.uka.ilkd.pp.DataLayouter
import de.uka.ilkd.pp.NoExceptions
import de.uka.ilkd.pp.StringBackend

class IDocPrettyPrinter extends IDocSwitch<DataLayouter<NoExceptions>> {
	DataLayouter<NoExceptions> layouter
	static final int INDENTATION = 2

	def String prettyPrint(EObject object) {
		val StringBuilder sb = new StringBuilder()
		// FIXME -> PreferenceSetting ?
		var int lineLength = 70
		layouter = new DataLayouter<NoExceptions>(new StringBackend(sb, lineLength), INDENTATION)
		doSwitch(object)
		layouter.close()
		return sb.toString()
	}

	override DataLayouter<NoExceptions> caseModel(Model object) {
		layouter.beginC(0)
		for (Import curImport : object.getImports()) {
			layouter.print("import ").print(curImport.getImportURI()).brk()
		}
		layouter.nl()
		for (IDoc idoc : object.getIdocs()) {
			doSwitch(idoc).brk()
		}
		return layouter.end()
	}

	override DataLayouter<NoExceptions> caseIDoc(IDoc object) {
		layouter.beginC().print("idoc ").print(quote(object.getName()))
		layouter.print(''' «object.getType()»''')
		layouter.print(''' «object.getMessageType()»''')
		layouter.print(" {")
		{
			printFields(object.getFields())
			val EList<Segment> segments = object.getSegments()
			if (!segments.isEmpty()) {
				layouter.brk()
			}

			for (var int i = 0, var int size = segments.size(); i < size; i++) {
				doSwitch(segments.get(i))
				if (i < size - 1) {
					layouter.brk()
				}

			}

		}
		return layouter.brk(1, -INDENTATION).print("}").end()
	}

	override DataLayouter<NoExceptions> caseSegment(Segment segment) {
		layouter.beginC().print("segment ").print(segment.getType())
		layouter.print(" {").brk()
		printFields(segment.getFields())
		var EList<Segment> segments = segment.getSegments()
		if (!segments.isEmpty()) {
			layouter.brk()
		}

		for (var int i = 0, var int size = segments.size(); i < size; i++) {
			doSwitch(segments.get(i))
			if (i < size - 1) {
				layouter.brk()
			}

		}
		return layouter.brk(1, -INDENTATION).print("}").end()
	}

	override DataLayouter<NoExceptions> caseStringField(StringField object) {
		return layouter.print('''«object.getName()»=«quote(object.getValue())»''')
	}

	override DataLayouter<NoExceptions> caseNumberField(NumberField object) {
		return layouter.print('''«object.getName()»=«object.getValue()»''')
	}

	def private void printFields(EList<Field> fields) {

		for (var int i = 0, var int size = fields.size(), var int finish = size - 1; i < size; i++) {
			doSwitch(fields.get(i))
			if (i < finish) {
				layouter.print(" ")
			}

		}

	}

	def private String quote(String string) {
		return Character.valueOf('"').charValue + VClipseStrings.convertToJavaString(string) +
			Character.valueOf('"').charValue
	}

}
