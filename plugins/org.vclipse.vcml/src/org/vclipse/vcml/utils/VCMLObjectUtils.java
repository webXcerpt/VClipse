package org.vclipse.vcml.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.vclipse.vcml.vcml.BOMItem;
import org.vclipse.vcml.vcml.BillOfMaterial;
import org.vclipse.vcml.vcml.Characteristic;
import org.vclipse.vcml.vcml.CharacteristicGroup;
import org.vclipse.vcml.vcml.Class;
import org.vclipse.vcml.vcml.Classification;
import org.vclipse.vcml.vcml.ConditionSource;
import org.vclipse.vcml.vcml.ConfigurationProfile;
import org.vclipse.vcml.vcml.ConfigurationProfileEntry;
import org.vclipse.vcml.vcml.Constraint;
import org.vclipse.vcml.vcml.ConstraintClass;
import org.vclipse.vcml.vcml.ConstraintMaterial;
import org.vclipse.vcml.vcml.ConstraintSource;
import org.vclipse.vcml.vcml.DependencyNet;
import org.vclipse.vcml.vcml.Description;
import org.vclipse.vcml.vcml.InterfaceDesign;
import org.vclipse.vcml.vcml.Literal;
import org.vclipse.vcml.vcml.Material;
import org.vclipse.vcml.vcml.ObjectType;
import org.vclipse.vcml.vcml.Option;
import org.vclipse.vcml.vcml.OptionType;
import org.vclipse.vcml.vcml.PartialKey;
import org.vclipse.vcml.vcml.Procedure;
import org.vclipse.vcml.vcml.ProcedureSource;
import org.vclipse.vcml.vcml.Row;
import org.vclipse.vcml.vcml.SelectionCondition;
import org.vclipse.vcml.vcml.SimpleDescription;
import org.vclipse.vcml.vcml.SimpleDocumentation;
import org.vclipse.vcml.vcml.SymbolicType;
import org.vclipse.vcml.vcml.VCObject;
import org.vclipse.vcml.vcml.ValueAssignment;
import org.vclipse.vcml.vcml.VariantFunction;
import org.vclipse.vcml.vcml.VariantTable;
import org.vclipse.vcml.vcml.VariantTableArgument;
import org.vclipse.vcml.vcml.VariantTableContent;
import org.vclipse.vcml.vcml.VcmlFactory;
import org.vclipse.vcml.vcml.VcmlModel;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

public class VCMLObjectUtils {

	private static final VcmlFactory VCML = VcmlFactory.eINSTANCE;

	static public BillOfMaterial mkBillOfMaterial() {
		return VCML.createBillOfMaterial();
	}

	static public BOMItem mkBOMItem() {
		return VCML.createBOMItem();
	}

	static public Characteristic mkCharacteristic(final String name) {
		final Characteristic object = VCML.createCharacteristic();
		object.setName(name);
		return object;
	}

	static public CharacteristicGroup mkCharacteristicGroup(final String name) {
		final CharacteristicGroup cg = VCML.createCharacteristicGroup();
		cg.setName(name);
		return cg;
	}

	static public Class mkClass(final String name, final Description description) {
		final Class clazz = VCML.createClass();
		clazz.setName(name);
		clazz.setDescription(description);
		return clazz;
	}

	static public Classification mkClassification(final Class cls) {
		final Classification classification = VCML.createClassification();
		classification.setCls(cls);
		return classification;
	}

	static public ValueAssignment mkValueAssignment(final Characteristic cstic, Iterable<? extends Literal> literals) {
		final ValueAssignment valueAssignment = VCML.createValueAssignment();
		valueAssignment.setCharacteristic(cstic);
		for (Literal literal : literals) {
			valueAssignment.getValues().add(literal);
		}
		return valueAssignment;
	}

	static public ConfigurationProfile mkConfigurationProfile(final String name, final String bomApplication, final InterfaceDesign uidesign) {
		final ConfigurationProfile object = VCML.createConfigurationProfile();
		object.setName(name);
		object.setBomapplication(bomApplication);
		object.setUidesign(uidesign);
		return object;
	}

	static public ConfigurationProfileEntry mkConfigurationProfileEntry(final int sequence, final Procedure procedure) {
		final ConfigurationProfileEntry object = VCML.createConfigurationProfileEntry();
		object.setSequence(sequence);
		object.setDependency(procedure);
		return object;
	}

	// TODO delete - implemented in the vcml object creator
	static public Constraint mkConstraint(final String name, final Description description) {
		final Constraint object = VCML.createConstraint();
		object.setName(name);
		object.setDescription(description);
		object.setSource(mkConstraintSource());
		return object;
	}

	static public ConstraintClass mkConstraintClass(final String name, final Class cls) {
		final ConstraintClass object = VCML.createConstraintClass();
		object.setName(name);
		object.setClass(cls);
		return object;
	}

	static public ConstraintMaterial mkConstraintMaterial(final String name, final Material material) {
		final ConstraintMaterial object = VCML.createConstraintMaterial();
		object.setName(name);
		final ObjectType objectType = VCML.createObjectType();
		objectType.setClassType(300);
		objectType.setType("material");
		object.setObjectType(objectType);
		final EList<PartialKey> attrs = objectType.getAttrs();
		final PartialKey partialKey = VCML.createPartialKey();
		attrs.add(partialKey);
		partialKey.setKey("nr");
		partialKey.setMaterial(material);
		return object;
	}

	static public ConstraintSource mkConstraintSource() {
		return VCML.createConstraintSource();
	}

	static public DependencyNet mkDependencyNet(final String name, final Description description) {
		final DependencyNet object = VCML.createDependencyNet();
		object.setName(name);
		object.setDescription(description);
		return object;
	}

	static public InterfaceDesign mkInterfaceDesign(final String name) {
		final InterfaceDesign id = VCML.createInterfaceDesign();
		id.setName(name);
		return id;
	}

	static public Material mkMaterial(final String name, final Description description, final String type) {
		final Material material = VCML.createMaterial();
		material.setName(name);
		material.setDescription(description);
		material.setType(type);
		return material;
	}

	static public VcmlModel mkModel() {
		return VCML.createVcmlModel();
	}

	static public Option mkOption(final OptionType type, final String value) {
		final Option option = VCML.createOption();
		option.setName(type);
		option.setValue(value);
		return option;
	}

	static public Procedure mkProcedure(final String name, final Description description) {
		final Procedure object = VCML.createProcedure();
		object.setName(name);
		object.setDescription(description);
		object.setSource(mkProcedureSource());
		return object;
	}

	static public ProcedureSource mkProcedureSource() {
		return VCML.createProcedureSource();
	}

	static public SelectionCondition mkSelectionCondition(final String name, final Description description) {
		final SelectionCondition object = VCML.createSelectionCondition();
		object.setName(name);
		object.setDescription(description);
		object.setSource(mkConditionSource());
		return object;
	}

	static public ConditionSource mkConditionSource() {
		return VCML.createConditionSource();
	}

	static public SimpleDescription mkSimpleDescription(final String description) {
		final SimpleDescription simpleDescription = VCML.createSimpleDescription();
		simpleDescription.setValue(description == null ? "" : description);
		return simpleDescription;
	}

	static public SimpleDocumentation mkSimpleDocumentation(final String documentation) {
		final SimpleDocumentation simpleDocumentation = VCML.createSimpleDocumentation();
		simpleDocumentation.setValue(documentation == null ? "" : documentation);
		return simpleDocumentation;
	}

	static public SymbolicType mkSymbolicType() {
		return VCML.createSymbolicType();
	}

	static public VariantFunction mkVariantFunction(final String name) {
		final VariantFunction object = VCML.createVariantFunction();
		object.setName(name);
		return object;
	}

	static public VariantTable mkVariantTable(String name, Description description) {
		VariantTable variantTable = VCML.createVariantTable();
		variantTable.setName(name);
		variantTable.setDescription(description == null ? mkSimpleDescription("") : description);
		return variantTable;
	}
	
	static public VariantTableContent mkVariantTableContent(VariantTable vt) {
		VariantTableContent vtc = VCML.createVariantTableContent();
		vtc.setTable(vt);
		return vtc;
	}
	
	static public Row mkRow() {
		return VCML.createRow();
	}
	
	static public VariantTableArgument mkVariantTableArgument(Characteristic cstic, boolean key) {
		VariantTableArgument vta = VCML.createVariantTableArgument();
		vta.setCharacteristic(cstic);
		vta.setKey(key);
		return vta;
	}
	
	static public void sortEntries(List<ConfigurationProfileEntry> entries) {
		// direct sort of "entries" does not work: The 'no duplicates' constraint is violated
		ArrayList<ConfigurationProfileEntry> arrayList = Lists.newArrayList(entries);
		Collections.sort(arrayList, new Comparator<ConfigurationProfileEntry>() {
			public int compare(ConfigurationProfileEntry arg0, ConfigurationProfileEntry arg1) {
				return new Integer(arg0.getSequence()).compareTo(new Integer(arg1.getSequence()));
			}});
		entries.clear();
		entries.addAll(arrayList);
	}
	
	// TODO replace these two methods by a method sortVCObjects(List<? extends VCObject> entries)
	static public void sortDependencyNets(List<DependencyNet> entries) {
		ArrayList<DependencyNet> arrayList = Lists.newArrayList(entries);
		Collections.sort(arrayList, new Comparator<DependencyNet>() {
			public int compare(DependencyNet object_one, DependencyNet object_two) {
				return object_one.getName().compareTo(object_two.getName());
			}});
		entries.clear();
		entries.addAll(arrayList);
	}
	
	static public void sortConstraints(List<Constraint> entries) {
		ArrayList<Constraint> arrayList = Lists.newArrayList(entries);
		Collections.sort(arrayList, new Comparator<Constraint>() {
			public int compare(Constraint object_one, Constraint object_two) {
				return object_one.getName().compareTo(object_two.getName());
			}});
		entries.clear(); 
		entries.addAll(arrayList);
	}
	
	static public <T extends VCObject> Iterable<T> getObjectsByNameAndType(final String name, VcmlModel vcmlModel, final java.lang.Class<T> type) { 
		if(vcmlModel == null || type == null) {
			return Lists.newArrayList();
		} else {
			Iterable<T> typeFilter = Iterables.filter(vcmlModel.getObjects(), type);
			if(name == null || name.isEmpty()) {
				return typeFilter;
			} else {
				return Iterables.filter(typeFilter, new Predicate<VCObject>() {
					public boolean apply(VCObject object) {
						return name.equals(object.getName()) && type.isAssignableFrom(object.getClass());
					}
				});
			}
		}
	}
}
