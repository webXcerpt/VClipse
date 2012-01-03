/**
 * 
 */
package org.vclipse.vcml.diff;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.ecore.EObject;
import org.vclipse.vcml.vcml.Assignment;
import org.vclipse.vcml.vcml.BOMItem;
import org.vclipse.vcml.vcml.BillOfMaterial;
import org.vclipse.vcml.vcml.BinaryCondition;
import org.vclipse.vcml.vcml.BinaryExpression;
import org.vclipse.vcml.vcml.Characteristic;
import org.vclipse.vcml.vcml.CharacteristicGroup;
import org.vclipse.vcml.vcml.CharacteristicOrValueDependencies;
import org.vclipse.vcml.vcml.CharacteristicReference_C;
import org.vclipse.vcml.vcml.CharacteristicReference_P;
import org.vclipse.vcml.vcml.CharacteristicValue;
import org.vclipse.vcml.vcml.Class;
import org.vclipse.vcml.vcml.Classification;
import org.vclipse.vcml.vcml.Comparison;
import org.vclipse.vcml.vcml.CompoundStatement;
import org.vclipse.vcml.vcml.ConditionSource;
import org.vclipse.vcml.vcml.ConditionalConstraintRestriction;
import org.vclipse.vcml.vcml.ConditionalStatement;
import org.vclipse.vcml.vcml.ConfigurationProfile;
import org.vclipse.vcml.vcml.ConfigurationProfileEntry;
import org.vclipse.vcml.vcml.Constraint;
import org.vclipse.vcml.vcml.ConstraintObject;
import org.vclipse.vcml.vcml.ConstraintRestriction;
import org.vclipse.vcml.vcml.ConstraintSource;
import org.vclipse.vcml.vcml.DelDefault;
import org.vclipse.vcml.vcml.DependencyNet;
import org.vclipse.vcml.vcml.Function;
import org.vclipse.vcml.vcml.FunctionCall;
import org.vclipse.vcml.vcml.GlobalDependency;
import org.vclipse.vcml.vcml.InCondition_C;
import org.vclipse.vcml.vcml.InCondition_P;
import org.vclipse.vcml.vcml.InterfaceDesign;
import org.vclipse.vcml.vcml.IsInvisible;
import org.vclipse.vcml.vcml.IsSpecified_C;
import org.vclipse.vcml.vcml.IsSpecified_P;
import org.vclipse.vcml.vcml.Literal;
import org.vclipse.vcml.vcml.LocalDependency;
import org.vclipse.vcml.vcml.MDataCharacteristic_C;
import org.vclipse.vcml.vcml.MDataCharacteristic_P;
import org.vclipse.vcml.vcml.Material;
import org.vclipse.vcml.vcml.NumericCharacteristicValue;
import org.vclipse.vcml.vcml.NumericType;
import org.vclipse.vcml.vcml.ObjectCharacteristicReference;
import org.vclipse.vcml.vcml.PFunction;
import org.vclipse.vcml.vcml.PartOfCondition;
import org.vclipse.vcml.vcml.Precondition;
import org.vclipse.vcml.vcml.Procedure;
import org.vclipse.vcml.vcml.ProcedureSource;
import org.vclipse.vcml.vcml.SelectionCondition;
import org.vclipse.vcml.vcml.SetDefault;
import org.vclipse.vcml.vcml.SetOrDelDefault;
import org.vclipse.vcml.vcml.SetPricingFactor;
import org.vclipse.vcml.vcml.ShortVarDefinition;
import org.vclipse.vcml.vcml.ShortVarReference;
import org.vclipse.vcml.vcml.SimpleStatement;
import org.vclipse.vcml.vcml.Statement;
import org.vclipse.vcml.vcml.SubpartOfCondition;
import org.vclipse.vcml.vcml.SumParts;
import org.vclipse.vcml.vcml.SymbolicType;
import org.vclipse.vcml.vcml.Table;
import org.vclipse.vcml.vcml.TypeOf;
import org.vclipse.vcml.vcml.UnaryCondition;
import org.vclipse.vcml.vcml.UnaryExpression;
import org.vclipse.vcml.vcml.VCObject;
import org.vclipse.vcml.vcml.ValueAssignment;
import org.vclipse.vcml.vcml.VariantFunction;
import org.vclipse.vcml.vcml.VariantFunctionArgument;
import org.vclipse.vcml.vcml.VariantTable;
import org.vclipse.vcml.vcml.VariantTableArgument;
import org.vclipse.vcml.vcml.VcmlFactory;
import org.vclipse.vcml.vcml.util.VcmlSwitch;

/**
 *	This class creates the absent references for the objects adopted by the diff model.
 *
 *	Example:
 *		if we transfer a class object to the diff-model, it requires probably(because it references them) 
 *		any characteristics which are available in the model.
 */
public class ReferenceConstructor extends VcmlSwitch<EObject> {

	private static VcmlFactory VCML = VcmlFactory.eINSTANCE;
	
	private Map<String, VCObject> objects;
	
	public ReferenceConstructor() {
		objects = new HashMap<String, VCObject>();
	}
	
	@Override
	public EObject doSwitch(EObject eObject) {
		return eObject == null ? null : super.doSwitch(eObject);
	}
	
	public void reset() {
		objects.clear();
	}
	
	public Map<String, VCObject> getCreatedObjects() {
		return Collections.unmodifiableMap(objects);
	}

	@Override
	public EObject caseClass(Class parent) {
		for(Characteristic cstic : parent.getCharacteristics()) {
			createVCObject(cstic);
		}
		return parent;
	}

	@Override
	public EObject caseCharacteristic(Characteristic parent) {
		CharacteristicOrValueDependencies dependencies = parent.getDependencies();
		if(dependencies != null) {
			for(GlobalDependency dependency : dependencies.getDependencies()) {
				doSwitch(dependency);
			}
			doSwitch(dependencies.getLocalPrecondition());				
			doSwitch(dependencies.getLocalSelectionCondition());
		}
		return parent;
	}

	@Override
	public EObject caseDependencyNet(DependencyNet parent) {
		for(Constraint constraint : parent.getConstraints()) {				
			createVCObject(constraint);
		}
		return parent;
	}

	@Override
	public EObject caseMaterial(Material parent) {
		for(Classification classification : parent.getClassifications()) {			
			doSwitch(classification);
		}
		for(BillOfMaterial bom : parent.getBillofmaterials()) {
			for(BOMItem item : bom.getItems()) {
				createVCObject(item.getSelectionCondition());
				for(ConfigurationProfileEntry entry : item.getEntries()) {
					doSwitch(entry.getDependency());
				}
			}
		}
		for(ConfigurationProfile profile : parent.getConfigurationprofiles()) {
			if(profile != null) {
				for(ConfigurationProfileEntry entry : profile.getEntries()) {
					doSwitch(entry.getDependency());
				}
				createVCObject(profile.getUidesign());
				for(DependencyNet net : profile.getDependencyNets()) {
					createVCObject(net);
				}
			}
		}
		return parent;
	}
	
	@Override
	public EObject caseClassification(Classification object) {
		// TODO do we need to handle the super classes ? -> object.getCls().getSuperClasses()
		for(Characteristic cstic : object.getCls().getCharacteristics()) {
			createVCObject(cstic);
		}
		for(ValueAssignment va : object.getValueAssignments()) {
			createVCObject(va.getCharacteristic());
		}
		return object;
	}

	@Override
	public EObject caseInterfaceDesign(InterfaceDesign parent) {
		for(CharacteristicGroup group : parent.getCharacteristicGroups()) {
			for(Characteristic cstic : group.getCharacteristics()) {
				createVCObject(cstic);
			}
		}
		return parent;
	}

	@Override
	public EObject caseProcedure(Procedure parent) {
		ProcedureSource source = parent.getSource();
		if(source != null) {
			for(Statement statement : source.getStatements()) {
				doSwitch(statement);
			}
		}
		return parent;
	}

	@Override
	public EObject caseCompoundStatement(CompoundStatement parent) {
		for(SimpleStatement simple : parent.getStatements()) {
			doSwitch(simple);
		}
		return parent;
	}

	@Override
	public EObject caseConditionalStatement(ConditionalStatement parent) {
		doSwitch(parent.getStatement());
		doSwitch(parent.getCondition());
		return parent;
	}
	
	@Override
	public EObject caseDelDefault(DelDefault parent) {
		createVCObject(parent.getCharacteristic());
		doSwitch(parent.getExpression());
		return parent;
	}

	@Override
	public EObject caseAssignment(Assignment parent) {
		createVCObject(parent.getCharacteristic());
		doSwitch(parent.getExpression());
		return parent;
	}

	@Override
	public EObject caseFunction(Function parent) {
		for(Characteristic cstic : parent.getCharacteristics()) {
			createVCObject(cstic);
		}
		createVCObject(parent.getFunction());
		for(Literal literal : parent.getValues()) {
			doSwitch(literal);
		}
		return parent;
	}

	@Override
	public EObject caseIsInvisible(IsInvisible parent) {
		createVCObject(parent.getCharacteristic());
		return parent;
	}

	@Override
	public EObject casePFunction(PFunction parent) {
		for(Characteristic cstic : parent.getCharacteristics()) {
			createVCObject(cstic);
		}
		createVCObject(parent.getFunction());
		return parent;
	}

	@Override
	public EObject caseSetOrDelDefault(SetOrDelDefault parent) {
		createVCObject(parent.getCharacteristic());
		doSwitch(parent.getExpression());
		return parent;
	}

	@Override
	public EObject caseSetPricingFactor(SetPricingFactor parent) {
		createVCObject(parent.getCharacteristic());
		doSwitch(parent.getArg1());
		doSwitch(parent.getArg2());
		return parent;
	}
	
	@Override
	public EObject caseTable(Table parent) {
		for(Characteristic cstic : parent.getCharacteristics()) {
			createVCObject(cstic);
		}
		createVCObject(parent.getTable());
		for(Literal literal : parent.getValues()) {
			doSwitch(literal);
		}
		return parent;
	}

	@Override
	public EObject caseVariantFunction(VariantFunction parent) {
		for(VariantFunctionArgument arg : parent.getArguments()) {
			doSwitch(arg);
		}
		return parent;
	}
	
	@Override
	public EObject caseVariantFunctionArgument(VariantFunctionArgument parent) {
		createVCObject(parent.getCharacteristic());
		return parent;
	}

	@Override
	public EObject caseVariantTable(VariantTable parent) {
		for(VariantTableArgument arg : parent.getArguments()) {
			doSwitch(arg);
		}
		return parent;
	}
	
	@Override
	public EObject caseVariantTableArgument(VariantTableArgument parent) {
		createVCObject(parent.getCharacteristic());
		return parent;
	}
	
	@Override
	public EObject caseBinaryCondition(BinaryCondition parent) {
		doSwitch(parent.getLeft());
		doSwitch(parent.getRight());
		return parent;
	}

	@Override
	public EObject caseComparison(Comparison parent) {
		doSwitch(parent.getLeft());
		doSwitch(parent.getRight());
		return parent;
	}

	@Override
	public EObject caseInCondition_C(InCondition_C parent) {
		return doSwitch(parent.getCharacteristic());
	}

	@Override
	public EObject caseInCondition_P(InCondition_P parent) {
		return doSwitch(parent.getCharacteristic());
	}

	@Override
	public EObject caseIsSpecified_C(IsSpecified_C parent) {
		return doSwitch(parent.getCharacteristic());
	}

	@Override
	public EObject caseIsSpecified_P(IsSpecified_P parent) {
		return doSwitch(parent.getCharacteristic());
	}
	
	@Override
	public EObject casePartOfCondition(PartOfCondition parent) {
		doSwitch(parent.getChild());
		doSwitch(parent.getParent());
		return parent;
	}

	@Override
	public EObject caseSubpartOfCondition(SubpartOfCondition parent) {
		doSwitch(parent.getChild());
		doSwitch(parent.getParent());
		return parent;
	}

	@Override
	public EObject caseTypeOf(TypeOf parent) {
		doSwitch(parent.getVariantclass());
		return parent;
	}

	@Override
	public EObject caseUnaryCondition(UnaryCondition parent) {
		doSwitch(parent.getCondition());
		return parent;
	}

	@Override
	public EObject caseBinaryExpression(BinaryExpression parent) {
		doSwitch(parent.getLeft());
		doSwitch(parent.getRight());
		return parent;
	}

	@Override
	public EObject caseFunctionCall(FunctionCall parent) {
		doSwitch(parent.getArgument());
		return parent;
	}
	
	@Override
	public EObject caseObjectCharacteristicReference(ObjectCharacteristicReference parent) {
		createVCObject(parent.getCharacteristic());
		doSwitch(parent.getLocation());
		return parent;
	}

	@Override
	public EObject caseShortVarReference(ShortVarReference parent) {
		doSwitch(parent.getRef());
		return parent;
	}
	
	@Override
	public EObject caseShortVarDefinition(ShortVarDefinition parent) {
		createVCObject(parent.getCharacteristic());
		return parent;
	}

	@Override
	public EObject caseCharacteristicReference_P(CharacteristicReference_P parent) {
		createVCObject(parent.getCharacteristic());
		return parent;
	}
	
	@Override
	public EObject caseMDataCharacteristic_C(MDataCharacteristic_C parent) {
		return doSwitch(parent.getCharacteristic());
	}

	@Override
	public EObject caseMDataCharacteristic_P(MDataCharacteristic_P parent) {
		return doSwitch(parent.getCharacteristic());
	}

	@Override
	public EObject caseSumParts(SumParts parent) {
		createVCObject(parent.getCharacteristic());
		return parent;
	}

	@Override
	public EObject caseUnaryExpression(UnaryExpression parent) {
		doSwitch(parent.getExpression());
		return parent;
	}
	
	@Override
	public EObject caseBillOfMaterial(BillOfMaterial parent) {
		for(BOMItem item : parent.getItems()) {
			return doSwitch(item);
		}
		return parent;
	}

	@Override
	public EObject caseBOMItem(BOMItem parent) {
		createVCObject(parent.getMaterial());
		createVCObject(parent.getSelectionCondition());
		for(ConfigurationProfileEntry entry : parent.getEntries()) {
			doSwitch(entry);
		}
		return parent;
	}

	@Override
	public EObject caseCharacteristicGroup(CharacteristicGroup parent) {
		for(Characteristic cstic : parent.getCharacteristics()) {
			createVCObject(cstic);
		}
		return parent;
	}

	@Override
	public EObject caseCharacteristicOrValueDependencies(CharacteristicOrValueDependencies parent) {
		for(GlobalDependency dependency : parent.getDependencies()) {
			doSwitch(dependency);
		}
		doSwitch(parent.getLocalPrecondition());
		doSwitch(parent.getLocalSelectionCondition());
		return parent;
	}

	@Override
	public EObject caseCharacteristicValue(CharacteristicValue parent) {
		doSwitch(parent.getDependencies());
		return parent;
	}

	@Override
	public EObject caseConditionalConstraintRestriction(ConditionalConstraintRestriction parent) {
		doSwitch(parent.getCondition());
		doSwitch(parent.getRestriction());
		return parent;
	}

	@Override
	public EObject caseConditionSource(ConditionSource parent) {
		doSwitch(parent.getCondition());
		return parent;
	}

	@Override
	public EObject caseConfigurationProfile(ConfigurationProfile parent) {
		createVCObject(parent.getUidesign());
		for(DependencyNet net : parent.getDependencyNets()) {
			createVCObject(net);
		}
		for(ConfigurationProfileEntry entry : parent.getEntries()) {
			doSwitch(entry);
		}
		return parent;
	}

	@Override
	public EObject caseConfigurationProfileEntry(ConfigurationProfileEntry parent) {
		createVCObject(parent.getDependency());
		return parent;
	}

	@Override
	public EObject caseConstraint(Constraint parent) {
		createVCObject(parent);
		return parent;
	}

	@Override
	public EObject caseConstraintObject(ConstraintObject parent) {
		for(ShortVarDefinition def : parent.getShortVars()) {
			doSwitch(def);
		}
		return parent;
	}

	@Override
	public EObject caseConstraintSource(ConstraintSource parent) {
		doSwitch(parent.getCondition());
		for(CharacteristicReference_C c : parent.getInferences()) {
			doSwitch(c);
		}
		for(ConstraintObject o : parent.getObjects()) {
			doSwitch(o);
		}
		for(ConstraintRestriction r : parent.getRestrictions()) {
			doSwitch(r);
		}
		return parent;
	}

	@Override
	public EObject caseLocalDependency(LocalDependency parent) {
		return doSwitch(parent.getSource());
	}

	@Override
	public EObject caseNumericCharacteristicValue(NumericCharacteristicValue parent) {
		return doSwitch(parent.getDependencies());
	}

	@Override
	public EObject caseNumericType(NumericType parent) {
		for(NumericCharacteristicValue numCharValue : parent.getValues()) {
			doSwitch(numCharValue);
		}
		return parent;
	}

	@Override
	public EObject casePrecondition(Precondition parent) {
		return doSwitch(parent.getSource());
	}

	@Override
	public EObject caseProcedureSource(ProcedureSource parent) {
		for(Statement statement : parent.getStatements()) {
			doSwitch(statement);
		}
		return parent;
	}

	@Override
	public EObject caseSelectionCondition(SelectionCondition parent) {
		return doSwitch(parent.getSource());
	}

	@Override
	public EObject caseSetDefault(SetDefault parent) {
		createVCObject(parent.getCharacteristic());
		return doSwitch(parent.getExpression());
	}

	@Override
	public EObject caseSymbolicType(SymbolicType parent) {
		for(CharacteristicValue dependency : parent.getValues()) {
			doSwitch(dependency);
		}
		return parent;	
	}
	
	private void createVCObject(VCObject source) {
		VCObject newObject = (VCObject)VCML.create(source.eClass());
		String name = ((VCObject)source).getName();
		newObject.setName(name);
		objects.put(name, newObject);
	}
}
