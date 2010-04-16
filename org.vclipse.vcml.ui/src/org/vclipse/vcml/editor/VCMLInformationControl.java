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
package org.vclipse.vcml.editor;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.text.DefaultInformationControl;
import org.eclipse.jface.text.IInformationControlExtension2;
import org.eclipse.jface.text.IInformationControlExtension3;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.swt.widgets.Shell;
import org.vclipse.vcml.vcml.Characteristic;
import org.vclipse.vcml.vcml.Constraint;
import org.vclipse.vcml.vcml.ConstraintClass;
import org.vclipse.vcml.vcml.DependencyNet;
import org.vclipse.vcml.vcml.Description;
import org.vclipse.vcml.vcml.Documentation;
import org.vclipse.vcml.vcml.InterfaceDesign;
import org.vclipse.vcml.vcml.Material;
import org.vclipse.vcml.vcml.NumericType;
import org.vclipse.vcml.vcml.Precondition;
import org.vclipse.vcml.vcml.Procedure;
import org.vclipse.vcml.vcml.SelectionCondition;
import org.vclipse.vcml.vcml.ShortVarDefinition;
import org.vclipse.vcml.vcml.SymbolicType;
import org.vclipse.vcml.vcml.VariantFunction;
import org.vclipse.vcml.vcml.VariantTable;
import org.vclipse.vcml.vcml.util.VcmlSwitch;


public class VCMLInformationControl 
	extends DefaultInformationControl 
	implements IInformationControlExtension2, IInformationControlExtension3 {

	ILabelProvider labelProvider;
	
	public VCMLInformationControl(Shell parentShell, ILabelProvider labelProvider) {
		super(parentShell, (String)null);
		this.labelProvider = labelProvider;
	}

	public void setInput(Object input) {
		if (input instanceof EObject) {
			setInformation(createInformation((EObject)input));
		}
	}

	/* creates an pseudo-HTML string displaying hover information for object
	 * 
	 * for information on legal HTML markup, see org.eclipse.jface.internal.text.html.HTML2TextReader
	 */
	private String createInformation(final EObject object) {
		return new VcmlSwitch<String>() {
			@Override
			public String caseCharacteristic(Characteristic object) {
				return 
				printHeader("characteristic", object) +  
				doSwitch(object.getType()) +
				(object.isMultiValue() ? " multi-value " : "") +
				(object.isRestrictable() ? " restrictable " : "") +
				(object.isRequired() ? " required " : "") +
				(object.isNotReadyForInput() ? " no-input " : "") +
				(object.isAdditionalValues() ? " additional-values " : "") +
				(object.isDisplayAllowedValues() ? " disp-allowed-values " : "") +
				printDescription(object.getDescription()) + 
				printDocumentation(object.getDocumentation());
			}
			@Override
			public String caseClass(org.vclipse.vcml.vcml.Class object) {
				return 
				printHeader("class", object) +  
				printDescription(object.getDescription());
			}
			@Override
			public String caseProcedure(Procedure object) {
				return
				printHeader("procedure", object) +  
				printDescription(object.getDescription()) + 
				printDocumentation(object.getDocumentation());
			}
			@Override
			public String caseConstraint(Constraint object) {
				return
				printHeader("constraint", object) +  
				printDescription(object.getDescription()) + 
				printDocumentation(object.getDocumentation());
			}
			@Override
			public String caseDependencyNet(DependencyNet object) {
				return
				printHeader("dependency net", object) +  
				printDescription(object.getDescription()) + 
				printDocumentation(object.getDocumentation());
			}
			@Override
			public String caseInterfaceDesign(InterfaceDesign object) {
				return
				printHeader("interface design", object);
			}
			@Override
			public String caseMaterial(Material object) {
				return
				printHeader("material", object) +  
				printDescription(object.getDescription());
			}
			@Override
			public String casePrecondition(Precondition object) {
				return
				printHeader("precondition", object) +  
				printDescription(object.getDescription()) + 
				printDocumentation(object.getDocumentation());
			}
			@Override
			public String caseSelectionCondition(SelectionCondition object) {
				return
				printHeader("selection condition", object) +  
				printDescription(object.getDescription()) + 
				printDocumentation(object.getDocumentation());
			}
			@Override
			public String caseVariantFunction(VariantFunction object) {
				return
				printHeader("variant function", object) +  
				printDescription(object.getDescription());
			}
			@Override
			public String caseVariantTable(VariantTable object) {
				return
				printHeader("variant table", object) +  
				printDescription(object.getDescription());
			}
			
			@Override
			public String caseConstraintClass(ConstraintClass object) {
				return doSwitch(object.getClass_());
			}
			@Override
			public String caseShortVarDefinition(
					ShortVarDefinition object) {
				return doSwitch(object.getCharacteristic());
			}

			@Override
			public String caseNumericType(NumericType object) {
				return 
				"<p>" + 
				"NUM " + object.getNumberOfChars() + "." + object.getDecimalPlaces() + 
				(object.isIntervalValuesAllowed() ? " intervals " : "") +
				(object.isNegativeValuesAllowed() ? " negative " : "") +
				"</p>";
				// TODO print values
			}
			@Override
			public String caseSymbolicType(SymbolicType object) {
				return
				"<p>" + 
				"CHAR " + object.getNumberOfChars() +
				(object.isCaseSensitive() ? " case-sensitive " : "") +
				"</p>";
			}
			
			private String printHeader(String h, EObject o) {
				 return "<h1>" + h + " " + labelProvider.getText(o) + "</h1>";
				 
			}
			private String printDescription(Description d) {
				 return "<br/><br/>" + labelProvider.getText(d);
			}
			private String printDocumentation(Documentation d) {
				 return "<br/><br/>" + labelProvider.getText(d);
			}
		}.doSwitch(object);
	}
	
}
