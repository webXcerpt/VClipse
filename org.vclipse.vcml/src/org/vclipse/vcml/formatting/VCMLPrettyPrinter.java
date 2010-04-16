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

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.xtext.linking.ILinkingService;
import org.vclipse.vcml.VCMLPlugin;
import org.vclipse.vcml.utils.ISapConstants;
import org.vclipse.vcml.vcml.BOMItem;
import org.vclipse.vcml.vcml.BillOfMaterial;
import org.vclipse.vcml.vcml.Characteristic;
import org.vclipse.vcml.vcml.CharacteristicGroup;
import org.vclipse.vcml.vcml.CharacteristicOrValueDependencies;
import org.vclipse.vcml.vcml.CharacteristicValue;
import org.vclipse.vcml.vcml.Class;
import org.vclipse.vcml.vcml.ConfigurationProfile;
import org.vclipse.vcml.vcml.ConfigurationProfileEntry;
import org.vclipse.vcml.vcml.Constraint;
import org.vclipse.vcml.vcml.DependencyNet;
import org.vclipse.vcml.vcml.FormattedDocumentationBlock;
import org.vclipse.vcml.vcml.GlobalDependency;
import org.vclipse.vcml.vcml.Import;
import org.vclipse.vcml.vcml.InterfaceDesign;
import org.vclipse.vcml.vcml.LocalDependency;
import org.vclipse.vcml.vcml.LocalPrecondition;
import org.vclipse.vcml.vcml.LocalSelectionCondition;
import org.vclipse.vcml.vcml.Material;
import org.vclipse.vcml.vcml.Model;
import org.vclipse.vcml.vcml.MultiLanguageDescription;
import org.vclipse.vcml.vcml.MultiLanguageDescriptions;
import org.vclipse.vcml.vcml.MultipleLanguageDocumentation;
import org.vclipse.vcml.vcml.MultipleLanguageDocumentation_LanguageBlock;
import org.vclipse.vcml.vcml.NumericCharacteristicValue;
import org.vclipse.vcml.vcml.NumericType;
import org.vclipse.vcml.vcml.Option;
import org.vclipse.vcml.vcml.Precondition;
import org.vclipse.vcml.vcml.Procedure;
import org.vclipse.vcml.vcml.VCObject;
import org.vclipse.vcml.vcml.SelectionCondition;
import org.vclipse.vcml.vcml.SimpleDescription;
import org.vclipse.vcml.vcml.SimpleDocumentation;
import org.vclipse.vcml.vcml.SymbolicType;
import org.vclipse.vcml.vcml.VariantFunction;
import org.vclipse.vcml.vcml.VariantFunctionArgument;
import org.vclipse.vcml.vcml.VariantTable;
import org.vclipse.vcml.vcml.VariantTableArgument;
import org.vclipse.vcml.vcml.VcmlPackage;
import org.vclipse.vcml.vcml.util.VcmlSwitch;

import com.google.inject.Inject;

import de.uka.ilkd.pp.DataLayouter;
import de.uka.ilkd.pp.NoExceptions;
import de.uka.ilkd.pp.StringBackend;

/**
 * 
 */
public class VCMLPrettyPrinter extends VcmlSwitch<DataLayouter<NoExceptions>> {
	
	@Inject
	private ILinkingService linkingService;
	
	private static final int INDENTATION = 2;
	
	private DataLayouter<NoExceptions> layouter;
	
	private static VcmlPackage VCMLPACKAGE = VcmlPackage.eINSTANCE;
	
	/**
	 * @param o
	 * @return
	 */
	public String prettyPrint(EObject o) {
		StringBuilder sb = new StringBuilder();
		IEclipsePreferences preferences = new InstanceScope().getNode(VCMLPlugin.ID);
		layouter = 
			new DataLayouter<NoExceptions>(
					new StringBackend(sb, preferences.getInt(ISapConstants.PP_LINE_LENGTH, 70)), INDENTATION);
		doSwitch(o);
		layouter.close();
		return sb.toString();
	}
	
	/**
	 * @see org.vclipse.vcml.vcml.util.VcmlSwitch#caseModel(org.vclipse.vcml.vcml.Model)
	 */
	@Override
	public DataLayouter<NoExceptions> caseModel(Model object) {
		layouter.beginC(0);
		EList<Import> imports = object.getImports();
		if(!imports.isEmpty()) {
			layouter.beginC(0);
			for(int i=0, size=imports.size()-1; i<=size; i++) {
				doSwitch(imports.get(i));
				if(i<size) {
					layouter.brk();
				}
			}
			layouter.end().nl().nl();
		}
		
		EList<Option> options = object.getOptions();
		if(!options.isEmpty()) {
			layouter.beginC().print("options {");
			for(Option option : options) {
				layouter.brk();
				doSwitch(option);
			}
			layouter.brk(1,-INDENTATION).print("}").end().nl().nl();
		}
		
		for(VCObject o : object.getObjects()) {
			doSwitch(o);
			layouter.nl().nl();
		}
		return layouter.end();
	}
	
	/**
	 * @see org.vclipse.vcml.vcml.util.VcmlSwitch#caseImport(org.vclipse.vcml.vcml.Import)
	 */
	@Override
	public DataLayouter<NoExceptions> caseImport(Import object) {
		return layouter.print("import ").print(doublequote(object.getImportURI()));
	}
	
	/**
	 * @see org.vclipse.vcml.vcml.util.VcmlSwitch#caseOption(org.vclipse.vcml.vcml.Option)
	 */
	@Override
	public DataLayouter<NoExceptions> caseOption(Option object) {
		layouter.beginI(0);
		printNullsafe(object.getName());
		return layouter.brk().print("=").brk().print(doublequote(object.getValue())).end();
	}

	/**
	 * @see org.vclipse.vcml.vcml.util.VcmlSwitch#caseCharacteristic(org.vclipse.vcml.vcml.Characteristic)
	 */
	@Override
	public DataLayouter<NoExceptions> caseCharacteristic(Characteristic object) {
		layouter.beginC().print("characteristic ");
		printName(object);
		if(hasBody(object)) {
			layouter.print(" {");
			{
				doSwitch(object.getDescription());
				doSwitch(object.getDocumentation());
				doSwitch(object.getType());
				layouter.brk().print("status ");
				printNullsafe(object.getStatus());
				if(object.getGroup() != null) {
					layouter.brk().print("group ").print(object.getGroup());
				}
				StringBuffer buffer = new StringBuffer();
				buffer.append("[ ");
				if(object.isAdditionalValues()) {
					buffer.append("additionalValues ");
				}
				if(object.isRequired()) {
					buffer.append("required ");
				}
				if(object.isRestrictable()) {
					buffer.append("restrictable ");
				}
				if(object.isNoDisplay()) {
					buffer.append("noDisplay ");
				}
				if(object.isNotReadyForInput()) {
					buffer.append("notReadyForInput ");
				}
				if(object.isMultiValue()) {
					buffer.append("multiValue ");
				}
				if(object.isDisplayAllowedValues()) {
					buffer.append("displayAllowedValues ");
				}
				buffer.append("]");
				if(buffer.length() > 2) {
					layouter.brk().print(buffer.toString().trim());
				}
				doSwitch(object.getDependencies());	
			}
			layouter.brk(1,-INDENTATION).print("}");
		}
		return layouter.end();
	}
	
	/**
	 * @see org.vclipse.vcml.vcml.util.VcmlSwitch#caseNumericType(org.vclipse.vcml.vcml.NumericType)
	 */
	@Override
	public DataLayouter<NoExceptions> caseNumericType(NumericType object) {
		layouter.brk().beginC().print("Numeric {");
		{
			layouter.brk().print("numberOfChars ").print(object.getNumberOfChars());
			layouter.brk().print("decimalPlaces ").print(object.getDecimalPlaces());
			if(object.getUnit() != null) {
				layouter.brk().print("unit ").print(object.getUnit());
			}
			StringBuffer buffer = new StringBuffer();
			buffer.append("[ ");
			if(object.isNegativeValuesAllowed()) {
				buffer.append("negativeValuesAllowed ");
			}
			if(object.isIntervalValuesAllowed()) {
				buffer.append("intervalValuesAllowed ");
			}
			buffer.append("]");
			if(buffer.length() > 2) {
				layouter.brk().print(buffer.toString().trim());
			}
			EList<NumericCharacteristicValue> values = object.getValues();
			if(!values.isEmpty()) {
				layouter.brk().beginC().print("values {").brk();
				for(NumericCharacteristicValue value : object.getValues()) {
					layouter.brk();
					printNullsafe(value.getName());
					CharacteristicOrValueDependencies dependencies = value.getDependencies();
					if (dependencies!=null) {
						layouter.brk();
						doSwitch(dependencies);
					}
				}
				layouter.brk(1, -INDENTATION).print("}").end();
			}
		}		
		return layouter.brk(1, -INDENTATION).print("}").end();
	}

	/**
	 * @see org.vclipse.vcml.vcml.util.VcmlSwitch#caseSymbolicType(org.vclipse.vcml.vcml.SymbolicType)
	 */
	@Override
	public DataLayouter<NoExceptions> caseSymbolicType(SymbolicType object) {
		layouter.brk().beginC().print("Symbolic {");
		layouter.brk().print("numberOfChars ").print(object.getNumberOfChars());
		if(object.isCaseSensitive()) {
			layouter.brk().print("[ caseSensitive ]");
		}
		EList<CharacteristicValue> values = object.getValues();
		if(!values.isEmpty()) {
			layouter.brk().beginC().print("values {");
			for(CharacteristicValue value : values) {
				layouter.brk().beginC().print("'");
				printNullsafe(value.getName());
				layouter.print("'");
				if(hasBody(value)) {
					layouter.print(" {");
					doSwitch(value.getDescription());
					doSwitch(value.getDocumentation());
					doSwitch(value.getDependencies());
					layouter.brk(1, -INDENTATION).print("}");
				}
				layouter.end();
			}
			layouter.brk(1, -INDENTATION).print("}").end();   
		}
		return layouter.brk(1, -INDENTATION).print("}").end();
	}
	
	/**
	 * @see org.vclipse.vcml.vcml.util.VcmlSwitch#caseLocalPrecondition(org.vclipse.vcml.vcml.LocalPrecondition)
	 */
	@Override
	public DataLayouter<NoExceptions> caseLocalPrecondition(LocalPrecondition object) {
		layouter.brk().beginC().print("precondition {");
		doSwitch(object.getDescription());
		doSwitch(object.getDocumentation());
		printStatus(object);
		printGroup(object);
		if (object.getSource()!=null) {
			layouter.brk().beginC().print("source {");
			String source = new ProcedurePrettyPrinter().prettyPrint(object.getSource());
			layouter.pre(source);
			layouter.brk(1, -INDENTATION).print("}").end();
		}
		return layouter.brk(1, -INDENTATION).print("}").end();
	}
	
	/**
	 * @see org.vclipse.vcml.vcml.util.VcmlSwitch#caseLocalSelectionCondition(org.vclipse.vcml.vcml.LocalSelectionCondition)
	 */
	@Override
	public DataLayouter<NoExceptions> caseLocalSelectionCondition(LocalSelectionCondition object) {
		layouter.brk().beginC().print("selectioncondition {");
		doSwitch(object.getDescription());
		doSwitch(object.getDocumentation());
		printStatus(object);
		printGroup(object);
		if(object.getSource() != null) {
			layouter.brk().beginC().print("source {").brk();
			String source = new ProcedurePrettyPrinter().prettyPrint(object.getSource());
			layouter.pre(source);
			layouter.brk(1, -INDENTATION).print("}").end();
		}
		return layouter.brk(1, -INDENTATION).print("}").end();
	}

	/**
	 * @see org.vclipse.vcml.vcml.util.VcmlSwitch#caseCharacteristicValue(org.vclipse.vcml.vcml.CharacteristicValue)
	 */
	@Override
	public DataLayouter<NoExceptions> caseCharacteristicValue(CharacteristicValue object) {
		layouter.brk().beginC();
		printNullsafe(object.getName());
		if(hasBody(object)) {
			layouter.print(" {");
			doSwitch(object.getDescription());
			doSwitch(object.getDocumentation());
			doSwitch(object.getDependencies());
			layouter.brk(1, -INDENTATION).print("}");
		}
		return layouter.end();
	}

	/**
	 * @see org.vclipse.vcml.vcml.util.VcmlSwitch#caseCharacteristicOrValueDependencies(org.vclipse.vcml.vcml.CharacteristicOrValueDependencies)
	 */
	@Override
	public DataLayouter<NoExceptions> caseCharacteristicOrValueDependencies(CharacteristicOrValueDependencies object) {
		layouter.brk().beginC().print("dependencies {");
		if(object.getLocalPrecondition() != null) {
			layouter.brk();
			doSwitch(object.getLocalPrecondition());
		}
		if(object.getLocalSelectionCondition() != null) {
			layouter.brk();
			doSwitch(object.getLocalSelectionCondition());
		}
		for(GlobalDependency dependency : object.getDependencies()) {
			layouter.brk();
			printCrossReference(object, dependency, VCMLPACKAGE.getCharacteristicOrValueDependencies_Dependencies(), VCMLPACKAGE.getVCObject_Name());
		}
		return layouter.brk(1, -INDENTATION).print("}").end();
	}
	
	/**
	 * @see org.vclipse.vcml.vcml.util.VcmlSwitch#caseMultipleLanguageDocumentation(org.vclipse.vcml.vcml.MultipleLanguageDocumentation)
	 */
	@Override
	public DataLayouter<NoExceptions> caseMultipleLanguageDocumentation(MultipleLanguageDocumentation object) {
		layouter.brk().beginC().print("documentation {");
		for (MultipleLanguageDocumentation_LanguageBlock block : object.getLanguageblocks()) {
			layouter.brk();
			layouter.beginC(3);
			printNullsafe(block.getLanguage().name());
			for (FormattedDocumentationBlock formattedBlock : block.getFormattedDocumentationBlocks()) {
				layouter.brk();
				layouter.beginI(0);
				layouter.print(doublequote(formattedBlock.getValue()));
				if (formattedBlock.getFormat() != null) {
					layouter.brk().print("format").brk().print(doublequote(formattedBlock.getFormat()));
				}
				layouter.end();
			}
			layouter.end();
		}
		return layouter.brk(1,-INDENTATION).print("}").end();   
	}

	/**
	 * @see org.vclipse.vcml.vcml.util.VcmlSwitch#caseSimpleDocumentation(org.vclipse.vcml.vcml.SimpleDocumentation)
	 */
	@Override
	public DataLayouter<NoExceptions> caseSimpleDocumentation(SimpleDocumentation object) {
		return layouter.brk().print("documentation ").print(doublequote(object.getValue()));
	}

	/**
	 * @see org.vclipse.vcml.vcml.util.VcmlSwitch#caseClass(org.vclipse.vcml.vcml.Class)
	 */
	@Override
	public DataLayouter<NoExceptions> caseClass(Class object) {
		layouter.beginC().print("class ");
		printName(object);
		if(hasBody(object)) {
			layouter.print(" {");
			{
				doSwitch(object.getDescription());
				layouter.brk().print("status ").print(object.getStatus());
				if(object.getGroup() != null) {
					layouter.brk().print("group ").print(object.getGroup());
				}
				layouter.brk().beginC().print("characteristics {");
				{
					for(Characteristic cstic : object.getCharacteristics()) {
						layouter.brk();
						printCrossReference(object, cstic, VCMLPACKAGE.getClass_Characteristics(), VCMLPACKAGE.getVCObject_Name());
					}
				}
				layouter.brk(1,-INDENTATION).print("}").end();  
			}
			layouter.brk(1,-INDENTATION).print("}");
		}
		return layouter.end();
	}
	
	/**
	 * @see org.vclipse.vcml.vcml.util.VcmlSwitch#caseConstraint(org.vclipse.vcml.vcml.Constraint)
	 */
	@Override
	public DataLayouter<NoExceptions> caseConstraint(Constraint object) {
		layouter.beginC().print("constraint ");
		printName(object);
		if(hasBody(object)) {
			layouter.print(" {");
			{
				doSwitch(object.getDescription());
				doSwitch(object.getDocumentation());
				layouter.brk().print("status ").print(object.getStatus().getName());
				if(object.getSource() != null) {
					layouter.brk().beginC().print("source {").brk();
					String source = new ConstraintPrettyPrinter().prettyPrint(object.getSource());
					layouter.pre(source);
					layouter.brk(1, -INDENTATION).print("}").end();
				}
			}
			layouter.brk(1,-INDENTATION).print("}");
		}
		return layouter.end();
	}
	
	/**
	 * @see org.vclipse.vcml.vcml.util.VcmlSwitch#caseDependencyNet(org.vclipse.vcml.vcml.DependencyNet)
	 */
	@Override
	public DataLayouter<NoExceptions> caseDependencyNet(DependencyNet object) {
		layouter.beginC().print("dependencynet ");
		printName(object);
		if(hasBody(object)) {
			layouter.print(" {");
			{
				doSwitch(object.getDescription());
				doSwitch(object.getDocumentation());
				layouter.brk().print("status ").print(object.getStatus().getName());
				if(object.getGroup() != null) {
					layouter.brk().print("group ").print(object.getGroup());
				}
				for (Constraint constraint : object.getConstraints()) {
					layouter.brk();
					printCrossReference(object, constraint, VCMLPACKAGE.getDependencyNet_Constraints(), VCMLPACKAGE.getVCObject_Name());
				}
			}
			layouter.brk(1, -INDENTATION).print("}");
		}
		return layouter.end();
	}
	
	/**
	 * @see org.vclipse.vcml.vcml.util.VcmlSwitch#caseInterfaceDesign(org.vclipse.vcml.vcml.InterfaceDesign)
	 */
	@Override
	public DataLayouter<NoExceptions> caseInterfaceDesign(InterfaceDesign object) {
		layouter.beginC().print("interfacedesign ");
		printName(object);
		if(hasBody(object)) {
			layouter.print(" {");
			{
				for (CharacteristicGroup group : object.getCharacteristicGroups()) {
					doSwitch(group);
				}
			}
			layouter.brk(1, -INDENTATION).print("}");
		}
		return layouter.end();
	}
	
	/**
	 * @see org.vclipse.vcml.vcml.util.VcmlSwitch#caseCharacteristicGroup(org.vclipse.vcml.vcml.CharacteristicGroup)
	 */
	@Override
	public DataLayouter<NoExceptions> caseCharacteristicGroup(CharacteristicGroup object) {
		layouter.brk().beginC().print("characteristicgroup ");
		printNullsafe(object.getName());
		layouter.print(" {");
		doSwitch(object.getDescription());
		for(Characteristic cstic : object.getCharacteristics()) {
			layouter.brk();
			printCrossReference(object, cstic, VCMLPACKAGE.getCharacteristicGroup_Characteristics(), VCMLPACKAGE.getVCObject_Name());
		}
		return layouter.brk(1, -INDENTATION).print("}").end();
	}
	
	/**
	 * @see org.vclipse.vcml.vcml.util.VcmlSwitch#caseBillOfMaterial(org.vclipse.vcml.vcml.BillOfMaterial)
	 */
	@Override
	public DataLayouter<NoExceptions> caseBillOfMaterial(BillOfMaterial object) {
		layouter.brk().beginC().print("billofmaterial {");
		EList<BOMItem> items = object.getItems();
		if(!items.isEmpty()) {
			layouter.brk().beginC().print("items {");
			for(BOMItem item : items) {
				layouter.brk();
				doSwitch(item);
			}
			layouter.brk(1, -INDENTATION).print("}").end();
		}
		return layouter.brk(1, -INDENTATION).print("}").end();
	}
	
	/**
	 * @see org.vclipse.vcml.vcml.util.VcmlSwitch#caseBOMItem(org.vclipse.vcml.vcml.BOMItem)
	 */
	@Override
	public DataLayouter<NoExceptions> caseBOMItem(BOMItem object) {
		printNullsafe(object.getItemnumber());
		layouter.print(" ");
		printCrossReference(object, VCMLPACKAGE.getBOMItem_Material(), VCMLPACKAGE.getVCObject_Name());
		EList<ConfigurationProfileEntry> entries = object.getEntries();
		SelectionCondition selCondition = object.getSelectionCondition();
		if(selCondition != null && !entries.isEmpty()) {
			layouter.brk().beginC().print("dependencies {");
			{
				printCrossReference(object, VCMLPACKAGE.getBOMItem_SelectionCondition(), VCMLPACKAGE.getVCObject_Name());
				for(ConfigurationProfileEntry entry : entries) {
					layouter.brk();
					printNullsafe(entry.getSequence());
					layouter.print(" ");
					printCrossReference(entry, VCMLPACKAGE.getConfigurationProfileEntry_Dependency(), VCMLPACKAGE.getVCObject_Name());
				}
			}
			layouter.brk(1, -INDENTATION).print("}").end();
		}
		return layouter;
	}

	/**
	 * @see org.vclipse.vcml.vcml.util.VcmlSwitch#caseMaterial(org.vclipse.vcml.vcml.Material)
	 */
	@Override
	public DataLayouter<NoExceptions> caseMaterial(Material object) {
		layouter.beginC().print("material ");
		printName(object);
		if(hasBody(object)) {
			layouter.print(" {");
			{
				doSwitch(object.getDescription());
				if(object.getType() != null) {
					layouter.brk().print("type ").print(object.getType());
				}
				for(BillOfMaterial bom : object.getBillofmaterials()) {
					doSwitch(bom);
				}
				EList<Class> classes = object.getClasses();
				if(!classes.isEmpty()) {
					layouter.brk().beginC().print("classes {");
					{
						for(Class clazz : classes) {
							layouter.brk();
							printCrossReference(object, clazz, VCMLPACKAGE.getMaterial_Classes(), VCMLPACKAGE.getVCObject_Name());
						}
					}
					layouter.brk(1, -INDENTATION).print("}").end();
				}
				for(ConfigurationProfile profile : object.getConfigurationprofiles()) {
					layouter.brk().beginC().print("configurationprofile '");
					printNullsafe(profile.getName());
					layouter.print("' {");
					{
						if(profile.getUidesign() != null) {
							layouter.brk().print("uidesign ");
							printCrossReference(profile, VCMLPACKAGE.getConfigurationProfile_Uidesign(), VCMLPACKAGE.getVCObject_Name());
						}
						for(DependencyNet net : profile.getDependencyNets()) {
							layouter.brk();
							printCrossReference(profile, net, VCMLPACKAGE.getConfigurationProfile_DependencyNets(), VCMLPACKAGE.getVCObject_Name());
						}
						for(ConfigurationProfileEntry entry : profile.getEntries()) {
							layouter.brk();
							printNullsafe(entry.getSequence());
							layouter.print(" ");
							printCrossReference(entry, VCMLPACKAGE.getConfigurationProfileEntry_Dependency(), VCMLPACKAGE.getVCObject_Name());
						}
					}
					layouter.brk(1, -INDENTATION).print("}").end();
				}
			}
			layouter.brk(1,-INDENTATION).print("}");
		}
		return layouter.end();
	}

	/**
	 * @see org.vclipse.vcml.vcml.util.VcmlSwitch#casePrecondition(org.vclipse.vcml.vcml.Precondition)
	 */
	@Override
	public DataLayouter<NoExceptions> casePrecondition(Precondition object) {
		layouter.beginC().print("precondition ");
		printName(object);
		if(hasBody(object)) {
			layouter.print(" {");
			{
				doSwitch(object.getDescription());
				doSwitch(object.getDocumentation());
				layouter.brk().print("status ").print(object.getStatus());
				if(object.getGroup() != null) {
					layouter.brk().print("group ").print(object.getGroup());
				}
				if(object.getSource() != null) {
					layouter.brk().beginC().print("source {").brk();
					String source = new ProcedurePrettyPrinter().prettyPrint(object.getSource());
					layouter.pre(source);
					layouter.brk(1, -INDENTATION).print("}").end();
				}
			}
			layouter.brk(1,-INDENTATION).print("}");
		}
		return layouter.end();
	}

	/**
	 * @see org.vclipse.vcml.vcml.util.VcmlSwitch#caseSimpleDescription(org.vclipse.vcml.vcml.SimpleDescription)
	 */
	@Override
	public DataLayouter<NoExceptions> caseSimpleDescription(SimpleDescription object) {
		return layouter.brk().beginC().print("description ").print(doublequote(object.getValue())).end();
	}

	/**
	 * @see org.vclipse.vcml.vcml.util.VcmlSwitch#caseMultiLanguageDescriptions(org.vclipse.vcml.vcml.MultiLanguageDescriptions)
	 */
	@Override
	public DataLayouter<NoExceptions> caseMultiLanguageDescriptions(MultiLanguageDescriptions object) {
		layouter.brk().beginC().print("description {");
		{
			for(MultiLanguageDescription desc : object.getDescriptions()) {
				layouter.brk();
				printNullsafe(desc.getLanguage());
				layouter.print(" ").print(doublequote(desc.getValue()));
			}
		}
		return layouter.brk(1,-INDENTATION).print("}").end();   
	}
	
	/**
	 * @see org.vclipse.vcml.vcml.util.VcmlSwitch#caseProcedure(org.vclipse.vcml.vcml.Procedure)
	 */
	@Override
	public DataLayouter<NoExceptions> caseProcedure(Procedure object) {
		layouter.beginC().print("procedure ");
		printName(object);
		if(hasBody(object)) {
			layouter.print(" {");
			{
				doSwitch(object.getDescription());
				doSwitch(object.getDocumentation());
				layouter.brk().print("status ").print(object.getStatus().getName());
				if(object.getSource() != null) {
					layouter.brk().beginC().print("source {").brk();
					String source = new ProcedurePrettyPrinter().prettyPrint(object.getSource());
					layouter.pre(source);
					layouter.brk(1, -INDENTATION).print("}").end();
				}
			}
			layouter.brk(1, -INDENTATION).print("}"); 
		}
		return layouter.end();
	}
	
	/**
	 * @see org.vclipse.vcml.vcml.util.VcmlSwitch#caseSelectionCondition(org.vclipse.vcml.vcml.SelectionCondition)
	 */
	@Override
	public DataLayouter<NoExceptions> caseSelectionCondition(SelectionCondition object) {
		layouter.beginC().print("selectioncondition ");
		printName(object);
		if(hasBody(object)) {
			layouter.print(" {");
			{
				doSwitch(object.getDescription());
				doSwitch(object.getDocumentation());
				layouter.brk().print("status ").print(object.getStatus());
				if(object.getGroup() != null) {
					layouter.brk().print("group ").print(object.getGroup());
				}
				if(object.getSource() != null) {
					layouter.brk().beginC().print("source {");
					String source = new ProcedurePrettyPrinter().prettyPrint(object.getSource());
					layouter.pre(source);
					layouter.brk(1, -INDENTATION).print("}").end();
				}
			}
			layouter.brk(1, -INDENTATION).print("}");
		}
		return layouter.end();
	}
	
	/**
	 * @see org.vclipse.vcml.vcml.util.VcmlSwitch#caseVariantFunction(org.vclipse.vcml.vcml.VariantFunction)
	 */
	@Override
	public DataLayouter<NoExceptions> caseVariantFunction(VariantFunction object) {
		layouter.beginC().print("variantfunction ");
		printName(object);
		if(hasBody(object)) {
			layouter.print(" {");
			{
				doSwitch(object.getDescription());
				layouter.brk().print("status ").print(object.getStatus());
				if(object.getGroup() != null) {
					layouter.brk().print("group ").print(object.getGroup());
				}
				layouter.brk().beginC().print("arguments {");
				{
					for(VariantFunctionArgument arg : object.getArguments()) {
						layouter.brk();
						if (arg.isIn()) {
							layouter.print("in ");
						}
						printCrossReference(arg, VCMLPACKAGE.getVariantFunctionArgument_Characteristic(), VCMLPACKAGE.getVCObject_Name());
					}
				}
				layouter.brk(1, -INDENTATION).print("}").end();
			}
			layouter.brk(1, -INDENTATION).print("}");
		}
		return layouter.end();
	}

	/**
	 * @see org.vclipse.vcml.vcml.util.VcmlSwitch#caseVariantTable(org.vclipse.vcml.vcml.VariantTable)
	 */
	@Override
	public DataLayouter<NoExceptions> caseVariantTable(VariantTable object) {
		layouter.beginC().print("varianttable ");
		printName(object);
		if(hasBody(object)) {
			layouter.print(" {");
			{
				doSwitch(object.getDescription());
				layouter.brk().print("status ").print(object.getStatus());
				if(object.getGroup() != null) {
					layouter.brk().print("group ").print(object.getGroup());
				}
				layouter.brk().beginC().print("arguments {");
				{
					for(VariantTableArgument arg : object.getArguments()) {
						layouter.brk();
						if (arg.isKey()) {
							layouter.print("key ");
						}
						printCrossReference(arg, arg.getCharacteristic(), VCMLPACKAGE.getVariantTableArgument_Characteristic(), VCMLPACKAGE.getVCObject_Name());
					}
				}
				layouter.brk(1, -INDENTATION).print("}").end();
			}
			layouter.brk(1, -INDENTATION).print("}");
		}
		return layouter.end();
	}

	/**
	 * @see org.vclipse.vcml.vcml.util.VcmlSwitch#doSwitch(org.eclipse.emf.ecore.EObject)
	 * allow calls with null argument (should not do anything)
	 */
	@Override
	public DataLayouter<NoExceptions> doSwitch(EObject theEObject) {
		if (theEObject==null) {
			return layouter;
		}
		return super.doSwitch(theEObject);
	}

	/**
	 * @param theEObject
	 * @return
	 */
	public DataLayouter<NoExceptions> brk_doSwitch(EObject theEObject) {
		if (theEObject==null) {
			return layouter;
		}
		layouter.brk();
		return super.doSwitch(theEObject);
	}
	
	// TODO the following methods could be refined
	private boolean hasBody(Characteristic object) {
		return object.getDescription()!=null
		|| object.getDocumentation()!=null;
	}
	
	private boolean hasBody(CharacteristicValue object) {
		return object.getDescription() != null 
		|| object.getDocumentation() != null
		|| object.getDependencies() != null;
	}

	private boolean hasBody(Class object) {
		return object.getDescription()!=null;
	}
	
	private boolean hasBody(Constraint object) {
		return object.getDescription()!=null
		|| object.getDocumentation()!=null;
	}
	
	private boolean hasBody(DependencyNet object) {
		return object.getDescription()!=null
		|| object.getDocumentation()!=null;
	}
	
	private boolean hasBody(Material object) {
		return object.getDescription()!=null
		|| !object.getBillofmaterials().isEmpty()
		|| !object.getClasses().isEmpty()
		|| !object.getConfigurationprofiles().isEmpty();
	}
	
	private boolean hasBody(Precondition object) {
		return object.getDescription()!=null
		|| object.getDocumentation()!=null;
	}
	
	private boolean hasBody(Procedure object) {
		return object.getDescription()!=null
		|| object.getDocumentation()!=null;
	}
	
	private boolean hasBody(SelectionCondition object) {
		return object.getDescription()!=null
		|| object.getDocumentation()!=null;
	}
	
	private boolean hasBody(VariantFunction object) {
		return object.getDescription()!=null;
	}
	
	private boolean hasBody(VariantTable object) {
		return object.getDescription()!=null;
	}
	
	private boolean hasBody(InterfaceDesign object) {
		return !object.getCharacteristicGroups().isEmpty();
	}
	
	// TODO use injected value converter?
	private String doublequote(String string) {
		return "\"" + string + "\"";
	}
	
	/**
	 * @param dependency
	 */
	private void printGroup(LocalDependency dependency) {
		if(dependency.getGroup() != null) {
			layouter.brk().print("group ").print(dependency.getGroup());
		}
	}
	
	/**
	 * @param dependency
	 */
	private void printStatus(LocalDependency dependency) {
		layouter.brk().print("status ").print(dependency.getStatus());
	}
	
	private void printNullsafe(Object object) {
		layouter.print(object==null ? "null" : object);
	}

	private void printName(VCObject object) {
		printNullsafe(object.getName());
	}

	private void printCrossReference(EObject context, EReference ref, EAttribute att) {
		printCrossReference(context, (EObject)context.eGet(ref), ref, att);
	}

	private void printCrossReference(EObject context, EObject object, EReference ref, EAttribute att) {
		String linkText;
		if (object==null) {
			layouter.print("###null object for crossref###");
			return;
		}
		Object o = object.eGet(att);
		if (o!=null) {
			linkText = o.toString();
		} else {
			try {
				linkText = linkingService.getLinkText(object, ref, context);
			} catch (Exception ex) {
				linkText = "###EXCEPTION###";
				ex.printStackTrace();
			}
		}
		printNullsafe(linkText);
	}
	
}
