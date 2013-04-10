/**
 * Copyright (c) 2010 - 2013 webXcerpt Software GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     	webXcerpt Software GmbH - initial creator
 * 		www.webxcerpt.com
 */
package org.vclipse.tests.vcml;

import org.eclipse.xtend2.lib.StringConcatenation;
import org.eclipse.xtext.junit4.InjectWith;
import org.eclipse.xtext.junit4.XtextRunner;
import org.vclipse.tests.procedure.ProcedureInjectorProvider;
import org.vclipse.tests.vcml.DependencyTest;

@InjectWith(ProcedureInjectorProvider.class)/* 
@RunWith(XtextRunner.class) */
@SuppressWarnings("all")
public class ProcedureTest extends DependencyTest {
  /* @Test
   */public void sumPartsTest1() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("$sum_parts ($self, characteristicId)");
    _builder.newLine();
    this.testParserRule(_builder, "SumParts");
  }
  
  /* @Test
   */public void countPartsTest1() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("$count_parts ($parent)");
    _builder.newLine();
    this.testParserRule(_builder, "CountParts");
  }
  
  /* @Test
   */public void typeOfTest1() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("type_of ($root, (material) (300) (nr=\'123\', nr=\'124\'))");
    _builder.newLine();
    this.testParserRule(_builder, "TypeOf");
  }
}
