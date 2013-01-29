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
package org.vclipse.vcml.validation;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.common.util.EList;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.naming.IQualifiedNameProvider;
import org.eclipse.xtext.validation.Check;
import org.eclipse.xtext.validation.CheckType;
import org.eclipse.xtext.validation.ComposedChecks;
import org.eclipse.xtext.validation.ValidationMessageAcceptor;
import org.vclipse.vcml.documentation.VCMLDescriptionProvider;
import org.vclipse.vcml.utils.DependencySourceUtils;
import org.vclipse.vcml.utils.VcmlUtils;
import org.vclipse.vcml.vcml.Characteristic;
import org.vclipse.vcml.vcml.CharacteristicType;
import org.vclipse.vcml.vcml.CharacteristicValue;
import org.vclipse.vcml.vcml.Class;
import org.vclipse.vcml.vcml.Constraint;
import org.vclipse.vcml.vcml.Dependency;
import org.vclipse.vcml.vcml.DependencyNet;
import org.vclipse.vcml.vcml.InterfaceDesign;
import org.vclipse.vcml.vcml.Literal;
import org.vclipse.vcml.vcml.Material;
import org.vclipse.vcml.vcml.NumberListEntry;
import org.vclipse.vcml.vcml.NumericCharacteristicValue;
import org.vclipse.vcml.vcml.NumericLiteral;
import org.vclipse.vcml.vcml.NumericType;
import org.vclipse.vcml.vcml.Option;
import org.vclipse.vcml.vcml.OptionType;
import org.vclipse.vcml.vcml.Precondition;
import org.vclipse.vcml.vcml.Procedure;
import org.vclipse.vcml.vcml.Row;
import org.vclipse.vcml.vcml.SelectionCondition;
import org.vclipse.vcml.vcml.SimpleDescription;
import org.vclipse.vcml.vcml.SymbolicLiteral;
import org.vclipse.vcml.vcml.SymbolicType;
import org.vclipse.vcml.vcml.VCObject;
import org.vclipse.vcml.vcml.VariantFunction;
import org.vclipse.vcml.vcml.VariantTable;
import org.vclipse.vcml.vcml.VariantTableArgument;
import org.vclipse.vcml.vcml.VariantTableContent;
import org.vclipse.vcml.vcml.VcmlPackage;

import com.google.common.collect.Sets;
import com.google.inject.Inject;

@ComposedChecks(validators= {ValueValidator.class})
public class VCMLJavaValidator extends AbstractVCMLJavaValidator {

	@Inject
	private VCMLDescriptionProvider descriptionProvider;
	
	@Inject
	private DependencySourceUtils dependencySourceUtils;
	
	@Inject
	private IQualifiedNameProvider nameProvider;
	
	private static final int MAXLENGTH_CLASS_CHARACTERISTICS = 999; // SAP limit because cstic index in class table has size 3
	private static final int MAXLENGTH_CLASS_NAME = 18;
	private static final int MAXLENGTH_INTERFACEDESIGN_NAME = 18;
	private static final int MAXLENGTH_NAME = 30;
	private static final int MAXLENGTH_DESCRIPTION = 30;
	private static final int MAXLENGTH_DEPENDENCYNET_CHARACTERISTICS = 50; // soft limit of size of dependency net (should not be larger because compilation has a O(n^2) algorithm)
	//private static final int MAXLENGTH_MATERIAL_NAME = 18;

	/*
	 * @Check(CheckType.EXPENSIVE) //executed upon validate action in context menu
     * @Check(CheckType.NORMAL) //upon save
     * @Check(CheckType.FAST) //while editig 
	 */
		
	/***
	 ***	Name checks 
	 ***/
	@Check
	public void checkNameLength(VCObject object) {
		String name = object instanceof VariantTableContent ? ((VariantTableContent)object).getTable().getName() : object.getName();
		if(object instanceof Class) {
			if(VcmlUtils.getClassName(name).length() > MAXLENGTH_CLASS_NAME) {
				error("Name of class is limited to " + MAXLENGTH_CLASS_NAME + " characters", VcmlPackage.Literals.VC_OBJECT__NAME);
			}
		} else if(object instanceof InterfaceDesign) {
				if(name.length() > MAXLENGTH_INTERFACEDESIGN_NAME) {
					error("Name of interface design is limited to " + MAXLENGTH_INTERFACEDESIGN_NAME + " characters", VcmlPackage.Literals.VC_OBJECT__NAME);
				}
		} else if(name.length() > MAXLENGTH_NAME) {
			error("Name of " + object.getClass().getSimpleName() + " is limited to " + MAXLENGTH_NAME + " characters", VcmlPackage.Literals.VC_OBJECT__NAME);
		}
	}
	
	@Check(CheckType.FAST)
	public void checkCharacteristic(Characteristic object) {
//		if (object.isMultiValue() && object.isRestrictable()) {
//			error("Multivalued characteristic " + object.getName() + " must not be restrictable", VcmlPackage.Literals.CHARACTERISTIC__RESTRICTABLE);
//		}
	}
	
	@Check
	public void checkDependencyNet(DependencyNet dependencyNet) {
		if(dependencyNet.getConstraints().size() > MAXLENGTH_DEPENDENCYNET_CHARACTERISTICS) {
			warning("Dependency net " + dependencyNet.getName() + " too large ("+ dependencyNet.getConstraints().size() +" constraints), should have for efficiency at most " + MAXLENGTH_DEPENDENCYNET_CHARACTERISTICS + " constraints", VcmlPackage.Literals.VC_OBJECT__NAME);
		}
	}
	
	@Check(CheckType.FAST)
	public void checkClass(Class object) {
		if(object.getCharacteristics().size() > MAXLENGTH_CLASS_CHARACTERISTICS) {
			error("Number of characteristics of a class is limited to " + MAXLENGTH_CLASS_CHARACTERISTICS + "(" + object.getCharacteristics().size() + ")", VcmlPackage.Literals.VC_OBJECT__NAME);
		}
	}

	@Check(CheckType.FAST)
	public void checkDescription(SimpleDescription desc) {
		VCObject vcObject = EcoreUtil2.getContainerOfType(desc, VCObject.class);
		if (vcObject instanceof Material) {
			if (desc.getValue().length() > 40) {
				warning("Material descriptions are limited to 40 characters", VcmlPackage.Literals.SIMPLE_DESCRIPTION__VALUE);
			}
		} else if (vcObject instanceof Class) {
			if (desc.getValue().length() > 40) {
				warning("Class descriptions are limited to 40 characters", VcmlPackage.Literals.SIMPLE_DESCRIPTION__VALUE);
			}
		} else if (vcObject instanceof VariantFunction) {
			if (desc.getValue().length() > 40) {
				warning("Variant function descriptions are limited to 40 characters", VcmlPackage.Literals.SIMPLE_DESCRIPTION__VALUE);
			}
		} else if (vcObject instanceof VariantTable) {
			if (desc.getValue().length() > 40) {
				warning("Variant table descriptions are limited to 40 characters", VcmlPackage.Literals.SIMPLE_DESCRIPTION__VALUE);
			}
		} else {
			if (desc.getValue().length() > MAXLENGTH_DESCRIPTION) {
				warning("Descriptions are limited to " + MAXLENGTH_DESCRIPTION + " characters", VcmlPackage.Literals.SIMPLE_DESCRIPTION__VALUE);
			}
		}
	}
	
	@Check(CheckType.FAST)
	public void checkValueDescription(SymbolicType type) {
		// code adapted from http://blogs.itemis.de/stundzig/archives/487
		Map<String, CharacteristicValue> descriptions = new HashMap<String, CharacteristicValue>();
		Set<String> duplicateDescriptions = Sets.newHashSet();
		
		Map<String, CharacteristicValue> names = new HashMap<String, CharacteristicValue>();
		Set<String> duplicateNames = Sets.newHashSet();
		for(CharacteristicValue value : ((SymbolicType)type).getValues()) {
			String name = value.getName();			
			if(names.get(name) != null) {
				duplicateNames.add(name);
				error("Duplicate value " + name, value, VcmlPackage.Literals.CHARACTERISTIC_VALUE__NAME, ValidationMessageAcceptor.INSIGNIFICANT_INDEX);
			} else {
				names.put(name, value);
			}
			
			String description = descriptionProvider.getDocumentation(value);
			if (description!=null) {
				if(descriptions.get(description) != null) {
					duplicateDescriptions.add(description);
					error("Duplicate description '" + description + "' for value " + name, value, VcmlPackage.Literals.CHARACTERISTIC_VALUE__DESCRIPTION, ValidationMessageAcceptor.INSIGNIFICANT_INDEX);
				} else {
					descriptions.put(description, value);
				}
			}
		}
		for(String description : duplicateDescriptions) {
			error("Duplicate description '" + description + "' for value " + descriptions.get(description).getName(), descriptions.get(description), VcmlPackage.Literals.CHARACTERISTIC_VALUE__DESCRIPTION, ValidationMessageAcceptor.INSIGNIFICANT_INDEX);
		}
		for(String name : duplicateNames) {
			error("Duplicate value " + name, names.get(name), VcmlPackage.Literals.CHARACTERISTIC_VALUE__NAME, ValidationMessageAcceptor.INSIGNIFICANT_INDEX);
		}
	}
	
	@Check(CheckType.FAST)
	public void checkVariantTableContents(final VariantTableContent content) {
		EList<VariantTableArgument> parameters = content.getTable().getArguments();
		EList<Row> rows = content.getRows();
		for(Row row : rows) {
			EList<Literal> values = row.getValues();
			if(values.size() != parameters.size()) {
				error("Illegal number of values in a row. Expected " + parameters.size() + " values.", VcmlPackage.Literals.VARIANT_TABLE_CONTENT__ROWS, rows.indexOf(row));
			}
			for(Literal value : values) {
				int index = values.indexOf(value);
				if(index < parameters.size()) {
					Characteristic param = parameters.get(index).getCharacteristic();
					if(!param.eIsProxy() && !param.isAdditionalValues() && !contains(param.getType(), value)) {
						error("Parameter " + param.getName() + " does not contain value " + VcmlUtils.getLiteralName(value), 
								row, VcmlPackage.Literals.ROW__VALUES, index);
					}
				}
			}
		}
	}
	
	private boolean contains(CharacteristicType type, Literal value) {
		if(value instanceof SymbolicLiteral && type instanceof SymbolicType) {
			if (((SymbolicType)type).getValues().isEmpty()) {
				return true;
			}
			String strValue = ((SymbolicLiteral)value).getValue();
			for(CharacteristicValue cv : ((SymbolicType)type).getValues()) {
				if(cv.getName().equals(strValue)) {
					return true;
				}
			}
		} else if(value instanceof NumericLiteral && type instanceof NumericType) {
			if (((NumericType)type).getValues().isEmpty()) {
				return true;
			}
			String strValue = ((NumericLiteral)value).getValue();
			for(NumericCharacteristicValue ncv : ((NumericType)type).getValues()) {
				NumberListEntry entry = ncv.getEntry();
				if (entry instanceof NumericLiteral) {
					if(((NumericLiteral)entry).getValue().equals(strValue)) {
						return true;
					}
				}
				// TODO check intervals
			}
		} 
		return false;
	}
	
	/*****
	 *****	Multiple usage checks 
	 *****/
//	@Check
//	public void checkMultipleUsage_DependencyNet(DependencyNet dependencyNet) {
//		String dependencyNetName = dependencyNet.getName();
//		EList<Constraint> constraints = dependencyNet.getConstraints();
//		Set<String> constraintNames = Sets.newHashSet();
//		for(int index=0; index<dependencyNet.getConstraints().size(); index++) {
//			String constraintName = constraints.get(index).getName();
//			if(constraintNames.contains(constraintName)) {
//				error("Constraint with name " + constraintName + " already used in the depedency net " + 
//						dependencyNetName, dependencyNet, VcmlPackage.Literals.DEPENDENCY_NET__CONSTRAINTS, 
//							index, "MultipleUsage_DependencyNet_Constraint", new String[]{dependencyNetName, constraintName, "" + index});
//			} else {
//				constraintNames.add(constraintName);
//			}
//		}
//	}
//	
//	@Check
//	public void checkMultipleUsage_InterfaceDesign(InterfaceDesign interfaceDesign) {
//		String interfaceDesignName = interfaceDesign.getName();
//		EList<CharacteristicGroup> csticGroups = interfaceDesign.getCharacteristicGroups();
//		Set<String> csticGroupNames = Sets.newHashSet();
//		for(int csticGroupIndex=0; csticGroupIndex<csticGroups.size(); csticGroupIndex++) {
//			CharacteristicGroup csticGroup = csticGroups.get(csticGroupIndex);
//			String csticGroupName = csticGroup.getName();
//			if(csticGroupNames.contains(csticGroupName)) {
//				error("Characteristic group with name " + csticGroupName + "already used in the interface design " +
//						interfaceDesignName, interfaceDesign, VcmlPackage.Literals.INTERFACE_DESIGN__CHARACTERISTIC_GROUPS,
//							csticGroupIndex, "MultipleUsage_InterfaceDesign_CharacteristicGroup",
//								new String[]{interfaceDesignName, csticGroupName, "" + csticGroupIndex});
//			} else {
//				csticGroupNames.add(csticGroupName);
//			}
//			
//			Set<String> csticNames = Sets.newHashSet();
//			EList<Characteristic> characteristics = csticGroup.getCharacteristics();
//			for(int csticIndex=0; csticIndex<characteristics.size(); csticIndex++) {
//				String csticName = characteristics.get(csticIndex).getName();
//				if(csticNames.contains(csticName)) {
//					error("Characteristic with name " + csticName + " already used in the characteristic group " + 
//						csticGroupName, csticGroup, VcmlPackage.Literals.CHARACTERISTIC_GROUP__CHARACTERISTICS, 
//							csticIndex, "MultipleUsage_CharacteristicGroup_Characteristic", 
//								new String[]{csticGroupName, csticName, "" + csticIndex});
//				} else {
//					csticNames.add(csticName);
//				}
//			}
//		}
//	}
//	
//	@Check
//	public void checkMultipleUsage_Class(Class object) {
//		String className = object.getName();
//		Set<String> csticNames = Sets.newHashSet();
//		EList<Characteristic> characteristics = object.getCharacteristics();
//		for(int csticIndex=0; csticIndex<characteristics.size(); csticIndex++) {
//			String csticName = characteristics.get(csticIndex).getName();
//			if(csticNames.contains(csticName)) {
//				error("Characteristic with name " + csticName + " already used in the class " + 
//					className, object, VcmlPackage.Literals.CLASS__CHARACTERISTICS, 
//						csticIndex, "MultipleUsage_Class_Characteristic", 
//							new String[]{className, csticName, "" + csticIndex});
//			} else {
//				csticNames.add(csticName);
//			}
//		}
//	}
	
	@Check
	public void checkConstraintSource(Constraint dependency) {
		if (dependency.getDescription()!=null) {
			checkDependencySource(dependency);
		}
	}

	@Check
	public void checkProcedureSource(Procedure dependency) {
		if (dependency.getDescription()!=null) {
			checkDependencySource(dependency);
		}
	}

	@Check
	public void checkSelectionConditionSource(SelectionCondition dependency) {
		if (dependency.getDescription()!=null) {
			checkDependencySource(dependency);
		}
	}

	@Check
	public void checkPreconditionSource(Precondition dependency) {
		if (dependency.getDescription()!=null) {
			checkDependencySource(dependency);
		}
	}

	private void checkDependencySource(Dependency dependency) {
		try {
			// try to check whether file exists, but do not parse
			// TODO could be improved
			dependencySourceUtils.getInputStream(dependency);
		} catch (IOException ex) {
			String name = nameProvider.getFullyQualifiedName(dependency).getLastSegment();
			String fileName = dependencySourceUtils.getFilename(dependency);
			error("Source element for " + dependency.eClass().getName().toLowerCase() + " " + 
					name + " does not exist", dependency, 
						VcmlPackage.eINSTANCE.getVCObject_Name(), "Not_Existent_Source", 
							new String[]{name, dependency.eClass().getName(), fileName});
		}
	}
	
	@Check
	public void checkDateType(Option option) {
		if(option.getName() == OptionType.KEY_DATE) {
			if(!isValidDate(option.getValue())) {
				error(option.getValue() + " is not a valid date value (use format DD.MM.YYYY)", VcmlPackage.Literals.OPTION__VALUE);
			}
		}
	}
	
	private boolean isValidDate(String date) {
		if(date == null) {
			return false;
		}
		
		SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
		
		try {
			if(format.parse(date) != null) {
				return true;
			}
		}
		catch (ParseException e) {
			
		}
		return false;
	}

}