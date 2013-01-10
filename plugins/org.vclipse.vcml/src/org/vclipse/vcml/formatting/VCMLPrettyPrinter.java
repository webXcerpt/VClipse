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

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.xtext.resource.SaveOptions;
import org.vclipse.base.VClipseStrings;
import org.vclipse.vcml.utils.DependencySourceUtils;
import org.vclipse.vcml.utils.VCMLObjectUtils;
import org.vclipse.vcml.vcml.BOMItem;
import org.vclipse.vcml.vcml.BillOfMaterial;
import org.vclipse.vcml.vcml.Characteristic;
import org.vclipse.vcml.vcml.CharacteristicGroup;
import org.vclipse.vcml.vcml.CharacteristicOrValueDependencies;
import org.vclipse.vcml.vcml.CharacteristicReference_C;
import org.vclipse.vcml.vcml.CharacteristicReference_P;
import org.vclipse.vcml.vcml.CharacteristicValue;
import org.vclipse.vcml.vcml.Class;
import org.vclipse.vcml.vcml.Classification;
import org.vclipse.vcml.vcml.ConfigurationProfile;
import org.vclipse.vcml.vcml.ConfigurationProfileEntry;
import org.vclipse.vcml.vcml.Constraint;
import org.vclipse.vcml.vcml.DateCharacteristicValue;
import org.vclipse.vcml.vcml.DateType;
import org.vclipse.vcml.vcml.Dependency;
import org.vclipse.vcml.vcml.DependencyNet;
import org.vclipse.vcml.vcml.Description;
import org.vclipse.vcml.vcml.FormattedDocumentationBlock;
import org.vclipse.vcml.vcml.Import;
import org.vclipse.vcml.vcml.InterfaceDesign;
import org.vclipse.vcml.vcml.Literal;
import org.vclipse.vcml.vcml.MDataCharacteristic_P;
import org.vclipse.vcml.vcml.Material;
import org.vclipse.vcml.vcml.MultiLanguageDescription;
import org.vclipse.vcml.vcml.MultiLanguageDescriptions;
import org.vclipse.vcml.vcml.MultipleLanguageDocumentation;
import org.vclipse.vcml.vcml.MultipleLanguageDocumentation_LanguageBlock;
import org.vclipse.vcml.vcml.NumberListEntry;
import org.vclipse.vcml.vcml.NumericCharacteristicValue;
import org.vclipse.vcml.vcml.NumericInterval;
import org.vclipse.vcml.vcml.NumericLiteral;
import org.vclipse.vcml.vcml.NumericType;
import org.vclipse.vcml.vcml.Option;
import org.vclipse.vcml.vcml.Precondition;
import org.vclipse.vcml.vcml.Procedure;
import org.vclipse.vcml.vcml.Row;
import org.vclipse.vcml.vcml.SelectionCondition;
import org.vclipse.vcml.vcml.SimpleDescription;
import org.vclipse.vcml.vcml.SimpleDocumentation;
import org.vclipse.vcml.vcml.Status;
import org.vclipse.vcml.vcml.SymbolicLiteral;
import org.vclipse.vcml.vcml.SymbolicType;
import org.vclipse.vcml.vcml.VCObject;
import org.vclipse.vcml.vcml.ValueAssignment;
import org.vclipse.vcml.vcml.VariantFunction;
import org.vclipse.vcml.vcml.VariantFunctionArgument;
import org.vclipse.vcml.vcml.VariantTable;
import org.vclipse.vcml.vcml.VariantTableArgument;
import org.vclipse.vcml.vcml.VariantTableContent;
import org.vclipse.vcml.vcml.VcmlModel;

import com.google.inject.Inject;

import de.uka.ilkd.pp.DataLayouter;
import de.uka.ilkd.pp.NoExceptions;

public class VCMLPrettyPrinter extends DefaultPrettyPrinter {

	@Inject(optional=true)
	private DependencySourceUtils sourceUtils;
	
	@Inject(optional=true)
	private OptionsProvider optionsProvider;
	
	public OptionsProvider getOptionsProvider() {
		return optionsProvider;
	}
	
	// workaround for object pretty printing without context 
	// 	-> for example call of the pretty printer on a simple description directly
	public String prettyPrint(EObject object) {
		initialize();
		if (!(object instanceof VCObject)) {
			layouter.beginC();
		}
		doSwitch(object);
		if (!(object instanceof VCObject)) {
			layouter.end();
		}
		layouter.close();
		return stringBuilder.toString();
	}
	
	@Override
	public DataLayouter<NoExceptions> caseVcmlModel(VcmlModel object) {
		layouter.beginC(0);

		EList<Import> imports = object.getImports();
		if(!imports.isEmpty()) {
			layouter.beginI();
			for(Import importStatement : imports) {
				layouter.print("import \"").print(importStatement.getImportURI()).pre("\"").brk();
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
	
	@Override
	public DataLayouter<NoExceptions> caseOption(Option object) {
		layouter.beginI(0);
		printNullsafe(object.getName());
		return layouter.brk().print("=").brk().print(doublequote(object.getValue())).end();
	}

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
				printStatus(object.getStatus());
				printGroup(object.getGroup());
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
				if(object.getTable()!=null || object.getField()!=null) {
					buffer.append("table ").append(asSymbol(object.getTable())).append(" ");
					buffer.append("field ").append(asSymbol(object.getField())).append(" ");
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
	
	@Override
	public DataLayouter<NoExceptions> caseNumericType(NumericType object) {
		layouter.brk().beginC().print("numeric {");
		{
			layouter.brk().print("numberOfChars ").print(object.getNumberOfChars());
			layouter.brk().print("decimalPlaces ").print(object.getDecimalPlaces());
			if(object.getUnit() != null) {
				layouter.brk().print("unit ").print(doublequote(object.getUnit()));
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
				for(NumericCharacteristicValue value : values) {
					layouter.brk();
					NumberListEntry entry = value.getEntry();
					if(value.isDefault()) {
						layouter.print('*');
					}
					if (entry instanceof NumericInterval) { // parentheses only if intervals are used in value definitions
						layouter.print("(");
						doSwitch(entry);
						layouter.print(")");
					} else {
						doSwitch(entry);
					}
					if(hasBody(value)) {
						layouter.print(" {");
						doSwitch(value.getDocumentation());
						doSwitch(value.getDependencies());
						layouter.brk(1, -INDENTATION).print("}");
					}
				}
				layouter.brk(1, -INDENTATION).print("}").end();
			}
		}		
		return layouter.brk(1, -INDENTATION).print("}").end();
	}

	@Override
	public DataLayouter<NoExceptions> caseNumericLiteral(NumericLiteral object) {
		layouter.print(object.getValue());
		return layouter;
	}
	
	@Override
	public DataLayouter<NoExceptions> caseNumericInterval(NumericInterval object) {
		layouter.print(object.getLowerBound());
		layouter.brk();
		layouter.print("-");
		layouter.brk();
		layouter.print(object.getUpperBound());
		return layouter;
	}
	
	@Override
	public DataLayouter<NoExceptions> caseSymbolicType(SymbolicType object) {
		layouter.brk().beginC().print("symbolic {");
		layouter.brk().print("numberOfChars ").print(object.getNumberOfChars());
		if(object.isCaseSensitive()) {
			layouter.brk().print("[ caseSensitive ]");
		}
		EList<CharacteristicValue> values = object.getValues();
		if(!values.isEmpty()) {
			layouter.brk().beginC().print("values {");
			for(CharacteristicValue value : values) {
				doSwitch(value);
			}
			layouter.brk(1, -INDENTATION).print("}").end();   
		}
		return layouter.brk(1, -INDENTATION).print("}").end();
	}
	
	@Override
	public DataLayouter<NoExceptions> caseDateType(DateType object) {
		layouter.brk().beginC().print("date {");
		{
			StringBuffer buffer = new StringBuffer();
			buffer.append("[ ");
			if(object.isIntervalValuesAllowed()) {
				buffer.append("intervalValuesAllowed ");
			}
			buffer.append("]");
			if(buffer.length() > 2) {
				layouter.brk().print(buffer.toString().trim());
			}
			EList<DateCharacteristicValue> values = object.getValues();
			if(!values.isEmpty()) {
				layouter.brk().beginC().print("values {").brk();
				for(DateCharacteristicValue value : object.getValues()) {
					layouter.brk();
					layouter.beginC();
					if(value.isDefault()) {
						layouter.print("*");
					}
					layouter.print(value.getFrom());
					if (value.getTo()!=null) {
						layouter.brk().print("-").brk().print(value.getTo());
					}
					layouter.end();
					if(hasBody(value)) {
						layouter.print(" {");
						doSwitch(value.getDocumentation());
						doSwitch(value.getDependencies());
						layouter.brk(1, -INDENTATION).print("}");
					}
				}
				layouter.brk(1, -INDENTATION).print("}").end();
			}
		}		
		return layouter.brk(1, -INDENTATION).print("}").end();
	}

	@Override
	public DataLayouter<NoExceptions> caseCharacteristicValue(CharacteristicValue value) {
		layouter.brk().beginC();
		if(value.isDefault()) {
			layouter.print("*");
		}
		layouter.print("'" + value.getName() + "'");
		if(hasBody(value)) {
			layouter.print(" {");
			doSwitch(value.getDescription());
			doSwitch(value.getDocumentation());
			doSwitch(value.getDependencies());
			layouter.brk(1, -INDENTATION).print("}");
		}
		return layouter.end();
	}

	@Override
	public DataLayouter<NoExceptions> caseCharacteristicOrValueDependencies(CharacteristicOrValueDependencies object) {
		layouter.brk().beginC().print("dependencies {");
		for(Dependency dependency : object.getDependencies()) {
			layouter.brk();
			printCrossReference(object, dependency, VCMLPACKAGE.getCharacteristicOrValueDependencies_Dependencies(), VCMLPACKAGE.getVCObject_Name());
		}
		return layouter.brk(1, -INDENTATION).print("}").end();
	}
	
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

	@Override
	public DataLayouter<NoExceptions> caseSimpleDocumentation(SimpleDocumentation object) {
		return layouter.brk().print("documentation ").print(doublequote(object.getValue()));
	}

	@Override
	public DataLayouter<NoExceptions> caseClass(Class object) {
		layouter.beginC().print("class ");
		printNullsafe(object.getName());
		if(hasBody(object)) {
			layouter.print(" {");
			{
				if(object.getDescription() == null) {
					object.setDescription(VCMLObjectUtils.mkSimpleDescription(""));
				}
				doSwitch(object.getDescription());
				printStatus(object.getStatus());
				printGroup(object.getGroup());
				layouter.brk().beginC().print("characteristics {");
				{
					for(Characteristic cstic : object.getCharacteristics()) {
						layouter.brk();
						printCrossReference(object, cstic, VCMLPACKAGE.getClass_Characteristics(), VCMLPACKAGE.getVCObject_Name());
					}
				}
				layouter.brk(1,-INDENTATION).print("}").end();  
				EList<Class> superClasses = object.getSuperClasses();
				if (!superClasses.isEmpty()) {
					layouter.brk().beginC().print("superclasses {");
					{
						for(Class cls : superClasses) {
							layouter.brk();
							printCrossReference(object, cls, VCMLPACKAGE.getClass_SuperClasses(), VCMLPACKAGE.getVCObject_Name());
						}
					}
					layouter.brk(1,-INDENTATION).print("}").end();
				}
			}
			layouter.brk(1,-INDENTATION).print("}");
		}
		return layouter.end();
	}
	
	@Override
	public DataLayouter<NoExceptions> caseConstraint(Constraint object) {
		layouter.beginC().print("constraint ");
		printName(object);
		if(hasBody(object)) {
			layouter.print(" {");
			{
				doSwitch(object.getDescription());
				doSwitch(object.getDocumentation());
				printStatus(object.getStatus());
				printGroup(object.getGroup());
			}
			layouter.brk(1,-INDENTATION).print("}");
			writeSourceCode(object, object.getSource());
		}
		return layouter.end();
	}

	@Override
	public DataLayouter<NoExceptions> caseDependencyNet(DependencyNet object) {
		layouter.beginC().print("dependencynet ");
		printName(object);
		if(hasBody(object)) {
			layouter.print(" {");
			{
				doSwitch(object.getDescription());
				doSwitch(object.getDocumentation());
				printStatus(object.getStatus());
				printGroup(object.getGroup());
				for (Constraint constraint : object.getConstraints()) {
					layouter.brk();
					printCrossReference(object, constraint, VCMLPACKAGE.getDependencyNet_Constraints(), VCMLPACKAGE.getVCObject_Name());
				}
			}
			layouter.brk(1, -INDENTATION).print("}");
		}
		return layouter.end();
	}
	
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
	
	@Override
	public DataLayouter<NoExceptions> caseCharacteristicGroup(CharacteristicGroup object) {
		layouter.brk().beginC().print("characteristicgroup ");
		printNullsafe(asSymbol(object.getName()));
		layouter.print(" {");
		doSwitch(object.getDescription());
		for(Characteristic cstic : object.getCharacteristics()) {
			layouter.brk();
			printCrossReference(object, cstic, VCMLPACKAGE.getCharacteristicGroup_Characteristics(), VCMLPACKAGE.getVCObject_Name());
		}
		return layouter.brk(1, -INDENTATION).print("}").end();
	}
	
	@Override
	public DataLayouter<NoExceptions> caseBillOfMaterial(BillOfMaterial object) {
		layouter.beginC().print("billofmaterial ");
		printName(object); 
		layouter.print(" {");
		Material material = object.getMaterial();
		if (material != null ) {
			layouter.brk().print("material ");
			printName(material);
		}
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
	
	@Override
	public DataLayouter<NoExceptions> caseBOMItem(BOMItem object) {
		printNullsafe(object.getItemnumber());
		layouter.print(" ");
		printCrossReference(object, VCMLPACKAGE.getBOMItem_Material(), VCMLPACKAGE.getVCObject_Name());
		SelectionCondition selCondition = object.getSelectionCondition();
		EList<ConfigurationProfileEntry> entries = object.getEntries();
		if(selCondition != null || !entries.isEmpty()) {
			layouter.brk().beginC().print("dependencies {");
			{
				if (selCondition != null) {
					layouter.brk();
					printCrossReference(object, VCMLPACKAGE.getBOMItem_SelectionCondition(), VCMLPACKAGE.getVCObject_Name());
				}
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

	@Override
	public DataLayouter<NoExceptions> caseMaterial(Material object) {
		layouter.beginC().print("material ");
		printName(object);
		if(hasBody(object)) {
			layouter.print(" {");
			{
				Description description = object.getDescription();
				if(description == null) {
					description = VCML.createSimpleDescription();
					((SimpleDescription)description).setValue("");
				}
				doSwitch(description);
				
				EList<ConfigurationProfile> configurationprofiles = object.getConfigurationprofiles();
				String type = object.getType();
				if(type == null) {
					type = configurationprofiles.isEmpty() ? "ZHAW" : "ZKMT";
				}
				layouter.brk().print("type ").print(type);
				
				EList<BillOfMaterial> billofmaterials = object.getBillofmaterials();
				if(!billofmaterials.isEmpty()) {
					layouter.brk().beginC().print("billofmaterials {");
					for(BillOfMaterial bom : billofmaterials) {
						layouter.brk();
						printCrossReference(object, bom, VCMLPACKAGE.getMaterial_Billofmaterials(), VCMLPACKAGE.getVCObject_Name());
					}
					layouter.brk(1, -INDENTATION).print("}").end();
				}
				
				EList<Classification> classifications = object.getClassifications();
				if(!classifications.isEmpty()) {
					layouter.brk().beginC().print("classes {");
					{
						for(Classification classification : classifications) {
							Class cls = classification.getCls();
							layouter.brk();
							printCrossReference(classification, cls, VCMLPACKAGE.getClassification_Cls(), VCMLPACKAGE.getVCObject_Name());
							EList<ValueAssignment> valueAssignments = classification.getValueAssignments();
							if (!valueAssignments.isEmpty()) {
								layouter.print(" {");
								layouter.brk();
								for(ValueAssignment va : valueAssignments) {
									printCrossReference(va, va.getCharacteristic(), VCMLPACKAGE.getValueAssignment_Characteristic(), VCMLPACKAGE.getVCObject_Name());
									layouter.beginC();
									layouter.print(" = ");
									for(Literal x : va.getValues()) {
										doSwitch(x);
										layouter.brk();
									}
									layouter.end();
									layouter.brk();
								}
								layouter.print("}");
							}
						}
					}
					layouter.brk(1, -INDENTATION).print("}").end();
				}
				
				if(!configurationprofiles.isEmpty()) {
					layouter.brk().beginC().print("configurationprofiles {");
					for(ConfigurationProfile profile : configurationprofiles) {
						layouter.brk();
						printCrossReference(object, profile, VCMLPACKAGE.getMaterial_Billofmaterials(), VCMLPACKAGE.getVCObject_Name());
					}
					layouter.brk(1, -INDENTATION).print("}").end();
				}
			}
			layouter.brk(1,-INDENTATION).print("}");
		}
		return layouter.end();
	}

	@Override
	public DataLayouter<NoExceptions> caseConfigurationProfile(ConfigurationProfile profile) {
		layouter.beginC().print("configurationprofile ");
		printName(profile);
		layouter.print(" {");
		{
			Material material = profile.getMaterial();
			if (material != null ) {
				layouter.brk().print("material ");
				printName(material);
			}
			String bomapplication = profile.getBomapplication();
			if (bomapplication != null && !bomapplication.isEmpty()) {
				layouter.brk().print("bomapplication ").print(profile.getBomapplication());
			}
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
		return layouter.brk(1, -INDENTATION).print("}").end();
	}

	@Override
	public DataLayouter<NoExceptions> casePrecondition(Precondition object) {
		layouter.beginC().print("precondition ");
		printName(object);
		if(hasBody(object)) {
			layouter.print(" {");
			{
				doSwitch(object.getDescription());
				doSwitch(object.getDocumentation());
				printStatus(object.getStatus());
				printGroup(object.getGroup());
			}
			layouter.brk(1,-INDENTATION).print("}");
		}
		return layouter.end();
	}

	@Override
	public DataLayouter<NoExceptions> caseSimpleDescription(SimpleDescription object) {
		return layouter.brk().beginC().print("description ").print(doublequote(object.getValue())).end();
	}

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
	
	@Override
	public DataLayouter<NoExceptions> caseProcedure(Procedure object) {
		layouter.beginC().print("procedure ");
		printName(object);
		if(hasBody(object)) {
			layouter.print(" {");
			{
				doSwitch(object.getDescription());
				doSwitch(object.getDocumentation());
				printStatus(object.getStatus());
				printGroup(object.getGroup());
			}
			layouter.brk(1, -INDENTATION).print("}"); 
			
			writeSourceCode(object, object.getSource());
		}
		return layouter.end();
	}
	
	@Override
	public DataLayouter<NoExceptions> caseSelectionCondition(SelectionCondition object) {
		layouter.beginC().print("selectioncondition ");
		printName(object);
		if(hasBody(object)) {
			layouter.print(" {");
			{
				doSwitch(object.getDescription());
				doSwitch(object.getDocumentation());
				printStatus(object.getStatus());
				printGroup(object.getGroup());
			}
			layouter.brk(1, -INDENTATION).print("}");
		}
		return layouter.end();
	}
	
	@Override
	public DataLayouter<NoExceptions> caseVariantFunction(VariantFunction object) {
		layouter.beginC().print("variantfunction ");
		printName(object);
		if(hasBody(object)) {
			layouter.print(" {");
			{
				doSwitch(object.getDescription());
				printStatus(object.getStatus());
				printGroup(object.getGroup());
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

	@Override
	public DataLayouter<NoExceptions> caseVariantTable(VariantTable object) {
		layouter.beginC().print("varianttable ");
		printName(object);
		if(hasBody(object)) {
			layouter.print(" {");
			{
				doSwitch(object.getDescription());
				printStatus(object.getStatus());
				printGroup(object.getGroup());
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
	
	@Override
	public DataLayouter<NoExceptions> caseVariantTableContent(VariantTableContent object) {
		layouter.beginC().print("varianttablecontent ");
		printName(object.getTable());
		layouter.print(" {").brk();
		for(Row row : object.getRows()) {
			layouter.print("row ");
			for(Literal value : row.getValues()) {
				doSwitch(value).print(" ");
			}
			layouter.brk();
		}
		layouter.brk(1, -INDENTATION).print("}");
		return layouter.end();
	}
	
	@Override
	public DataLayouter<NoExceptions> caseLiteral(Literal object) {
		if(object instanceof SymbolicLiteral) {
			layouter.print(symbolName(((SymbolicLiteral)object).getValue()));
		} else if(object instanceof NumericLiteral) {
			layouter.print(((NumericLiteral)object).getValue());
		} else if(object instanceof CharacteristicReference_C) {
			layouter.print(symbolName(((CharacteristicReference_P)object).getCharacteristic().getName()));
		} else if(object instanceof MDataCharacteristic_P) {
			layouter.print(symbolName(((MDataCharacteristic_P)object).getCharacteristic().getCharacteristic().getName()));
		}
		return layouter;
	}

	@Override
	public DataLayouter<NoExceptions> doSwitch(EObject theEObject) {
		if (theEObject==null) {
			return layouter;
		}
		return super.doSwitch(theEObject);
	}

	public DataLayouter<NoExceptions> brk_doSwitch(EObject theEObject) {
		if (theEObject==null) {
			return layouter;
		}
		layouter.brk();
		return super.doSwitch(theEObject);
	}
	
	private String doublequote(final String string) {
		return "\"" + VClipseStrings.convertToJavaString(string) + "\"";
	}
	
	private void printGroup(String group) {
		if(group != null) {
			layouter.brk().print("group ").print(doublequote(group));
		}
	}
	
	private void printStatus(Status status) {
		layouter.brk().print("status ").print(status.getLiteral());
	}
	
	private void printName(VCObject object) {
		printNullsafe(asSymbol(object.getName()));
	}

	private void writeSourceCode(Dependency object, EObject sourceCode) {
		if(optionsProvider != null && sourceCode != null) {
			String vcmluri = optionsProvider.get().get(OptionsProvider.VCML_FILE_URI);
			if(vcmluri != null && sourceUtils != null) {
				try {
					URI sourceUri = sourceUtils.sourceUri(object, vcmluri);
					Resource resource = new ResourceSetImpl().createResource(sourceUri);
					EList<EObject> contents = resource.getContents();
					contents.add(sourceCode);
					resource.save(SaveOptions.newBuilder().noValidation().getOptions().toOptionsMap());
				} catch(IOException exception) {
					exception.printStackTrace();
				}
			}
		}
	}
}