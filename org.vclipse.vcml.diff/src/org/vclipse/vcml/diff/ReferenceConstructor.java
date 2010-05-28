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
import org.vclipse.vcml.vcml.LocalPrecondition;
import org.vclipse.vcml.vcml.LocalSelectionCondition;
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
	
	/**
	 * 
	 */
	private static final VcmlFactory FACTORY = VcmlFactory.eINSTANCE;
	
	/**
	 * 
	 */
	private final Map<String, EObject> objects;
	
	/**
	 * 
	 */
	public ReferenceConstructor() {
		objects = new HashMap<String, EObject>();
	}
	
	/**
	 * 
	 */
	public void reset() {
		objects.clear();
	}
	
	/**
	 * @return
	 */
	public Map<String, EObject> getCreatedObjects() {
		return Collections.unmodifiableMap(objects);
	}

	/**
	 * @see org.vclipse.vcml.vcml.util.VcmlSwitch#caseClass(org.vclipse.vcml.vcml.Class)
	 */
	@Override
	public EObject caseClass(final Class parent) {
		for(final Characteristic cstic : parent.getCharacteristics()) {
			createVCObject(cstic);
		}
		return parent;
	}

	/**
	 * @see org.vclipse.vcml.vcml.util.VcmlSwitch#caseCharacteristic(org.vclipse.vcml.vcml.Characteristic)
	 */
	@Override
	public EObject caseCharacteristic(final Characteristic parent) {
		CharacteristicOrValueDependencies dependencies = parent.getDependencies();
		if(dependencies != null) {
			for(GlobalDependency dependency : dependencies.getDependencies()) {
				createVCObject(dependency);
			}
			doSwitch(dependencies.getLocalPrecondition());
			doSwitch(dependencies.getLocalSelectionCondition());
		}
		return parent;
	}

	/**
	 * @see org.vclipse.vcml.vcml.util.VcmlSwitch#caseDependencyNet(org.vclipse.vcml.vcml.DependencyNet)
	 */
	@Override
	public EObject caseDependencyNet(final DependencyNet parent) {
		for(Constraint constraint : parent.getConstraints()) {				
			createVCObject(constraint);
		}
		return parent;
	}

	/**
	 * @see org.vclipse.vcml.vcml.util.VcmlSwitch#caseMaterial(org.vclipse.vcml.vcml.Material)
	 */
	@Override
	public EObject caseMaterial(final Material parent) {
		for(Class clazz : parent.getClasses()) {			
			createVCObject(clazz);
		}
		for(BillOfMaterial bom : parent.getBillofmaterials()) {
			for(BOMItem item : bom.getItems()) {
				SelectionCondition sc = item.getSelectionCondition();
				createVCObject(sc);
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
				InterfaceDesign idesign = profile.getUidesign();
				createVCObject(idesign);
				for(DependencyNet net : profile.getDependencyNets()) {
					createVCObject(net);
				}
			}
		}
		return parent;
	}

	/**
	 * @see org.vclipse.vcml.vcml.util.VcmlSwitch#caseInterfaceDesign(org.vclipse.vcml.vcml.InterfaceDesign)
	 */
	@Override
	public EObject caseInterfaceDesign(final InterfaceDesign parent) {
		for(CharacteristicGroup group : parent.getCharacteristicGroups()) {
			for(Characteristic cstic : group.getCharacteristics()) {
				createVCObject(cstic);
			}
		}
		return parent;
	}
	
	/**
	 * @see org.vclipse.vcml.vcml.util.VcmlSwitch#caseProcedure(org.vclipse.vcml.vcml.Procedure)
	 */
	@Override
	public EObject caseProcedure(final Procedure parent) {
		ProcedureSource source = parent.getSource();
		if(source != null) {
			for(Statement statement : source.getStatements()) {
				doSwitch(statement);
			}
		}
		return parent;
	}
	
	/**
	 * @see org.vclipse.vcml.vcml.util.VcmlSwitch#caseCompoundStatement(org.vclipse.vcml.vcml.CompoundStatement)
	 */
	@Override
	public EObject caseCompoundStatement(final CompoundStatement parent) {
		for(SimpleStatement simple : parent.getStatements()) {
			doSwitch(simple);
		}
		return parent;
	}

	/**
	 * @see org.vclipse.vcml.vcml.util.VcmlSwitch#caseConditionalStatement(org.vclipse.vcml.vcml.ConditionalStatement)
	 */
	@Override
	public EObject caseConditionalStatement(final ConditionalStatement parent) {
		doSwitch(parent.getStatement());
		doSwitch(parent.getCondition());
		return parent;
	}
	
	/**
	 * @see org.vclipse.vcml.vcml.util.VcmlSwitch#caseDelDefault(org.vclipse.vcml.vcml.DelDefault)
	 */
	@Override
	public EObject caseDelDefault(final DelDefault parent) {
		createVCObject(parent.getCharacteristic());
		doSwitch(parent.getExpression());
		return parent;
	}

	/**
	 * @see org.vclipse.vcml.vcml.util.VcmlSwitch#caseAssignment(org.vclipse.vcml.vcml.Assignment)
	 */
	@Override
	public EObject caseAssignment(final Assignment parent) {
		createVCObject(parent.getCharacteristic());
		doSwitch(parent.getExpression());
		return parent;
	}

	/**
	 * @see org.vclipse.vcml.vcml.util.VcmlSwitch#caseFunction(org.vclipse.vcml.vcml.Function)
	 */
	@Override
	public EObject caseFunction(final Function parent) {
		for(Characteristic cstic : parent.getCharacteristics()) {
			createVCObject(cstic);
		}
		createVCObject(parent.getFunction());
		for(Literal literal : parent.getValues()) {
			doSwitch(literal);
		}
		return parent;
	}

	/**
	 * @see org.vclipse.vcml.vcml.util.VcmlSwitch#caseIsInvisible(org.vclipse.vcml.vcml.IsInvisible)
	 */
	@Override
	public EObject caseIsInvisible(final IsInvisible parent) {
		createVCObject(parent.getCharacteristic());
		return parent;
	}

	/**
	 * @see org.vclipse.vcml.vcml.util.VcmlSwitch#casePFunction(org.vclipse.vcml.vcml.PFunction)
	 */
	@Override
	public EObject casePFunction(final PFunction parent) {
		for(Characteristic cstic : parent.getCharacteristics()) {
			createVCObject(cstic);
		}
		createVCObject(parent.getFunction());
		return parent;
	}

	/**
	 * @see org.vclipse.vcml.vcml.util.VcmlSwitch#caseSetOrDelDefault(org.vclipse.vcml.vcml.SetOrDelDefault)
	 */
	@Override
	public EObject caseSetOrDelDefault(final SetOrDelDefault parent) {
		createVCObject(parent.getCharacteristic());
		doSwitch(parent.getExpression());
		return parent;
	}

	/**
	 * @see org.vclipse.vcml.vcml.util.VcmlSwitch#caseSetPricingFactor(org.vclipse.vcml.vcml.SetPricingFactor)
	 */
	@Override
	public EObject caseSetPricingFactor(final SetPricingFactor parent) {
		createVCObject(parent.getCharacteristic());
		doSwitch(parent.getArg1());
		doSwitch(parent.getArg2());
		return parent;
	}
	
	/**
	 * @see org.vclipse.vcml.vcml.util.VcmlSwitch#caseTable(org.vclipse.vcml.vcml.Table)
	 */
	@Override
	public EObject caseTable(final Table parent) {
		for(Characteristic cstic : parent.getCharacteristics()) {
			createVCObject(cstic);
		}
		createVCObject(parent.getTable());
		for(Literal literal : parent.getValues()) {
			doSwitch(literal);
		}
		return parent;
	}

	/**
	 * @see org.vclipse.vcml.vcml.util.VcmlSwitch#caseVariantFunction(org.vclipse.vcml.vcml.VariantFunction)
	 */
	@Override
	public EObject caseVariantFunction(final VariantFunction parent) {
		for(VariantFunctionArgument arg : parent.getArguments()) {
			doSwitch(arg);
		}
		return parent;
	}
	
	/**
	 * @see org.vclipse.vcml.vcml.util.VcmlSwitch#caseVariantFunctionArgument(org.vclipse.vcml.vcml.VariantFunctionArgument)
	 */
	@Override
	public EObject caseVariantFunctionArgument(final VariantFunctionArgument parent) {
		createVCObject(parent.getCharacteristic());
		return parent;
	}

	/**
	 * @see org.vclipse.vcml.vcml.util.VcmlSwitch#caseVariantTable(org.vclipse.vcml.vcml.VariantTable)
	 */
	@Override
	public EObject caseVariantTable(final VariantTable parent) {
		for(VariantTableArgument arg : parent.getArguments()) {
			doSwitch(arg);
		}
		return parent;
	}
	
	/**
	 * @see org.vclipse.vcml.vcml.util.VcmlSwitch#caseVariantTableArgument(org.vclipse.vcml.vcml.VariantTableArgument)
	 */
	@Override
	public EObject caseVariantTableArgument(final VariantTableArgument parent) {
		createVCObject(parent.getCharacteristic());
		return parent;
	}
	
	/**
	 * @see org.vclipse.vcml.vcml.util.VcmlSwitch#caseBinaryCondition(org.vclipse.vcml.vcml.BinaryCondition)
	 */
	@Override
	public EObject caseBinaryCondition(final BinaryCondition parent) {
		doSwitch(parent.getLeft());
		doSwitch(parent.getRight());
		return parent;
	}

	/**
	 * @see org.vclipse.vcml.vcml.util.VcmlSwitch#caseComparison(org.vclipse.vcml.vcml.Comparison)
	 */
	@Override
	public EObject caseComparison(final Comparison parent) {
		doSwitch(parent.getLeft());
		doSwitch(parent.getRight());
		return parent;
	}

	/**
	 * @see org.vclipse.vcml.vcml.util.VcmlSwitch#caseInCondition_C(org.vclipse.vcml.vcml.InCondition_C)
	 */
	@Override
	public EObject caseInCondition_C(final InCondition_C parent) {
		createVCObject(parent.getCharacteristic());
		return parent;
	}

	/**
	 * @see org.vclipse.vcml.vcml.util.VcmlSwitch#caseInCondition_P(org.vclipse.vcml.vcml.InCondition_P)
	 */
	@Override
	public EObject caseInCondition_P(final InCondition_P parent) {
		createVCObject(parent.getCharacteristic());
		return parent;
	}

	/**
	 * @see org.vclipse.vcml.vcml.util.VcmlSwitch#caseIsSpecified_C(org.vclipse.vcml.vcml.IsSpecified_C)
	 */
	@Override
	public EObject caseIsSpecified_C(final IsSpecified_C parent) {
		createVCObject(parent.getCharacteristic());
		return parent;
	}

	/**
	 * @see org.vclipse.vcml.vcml.util.VcmlSwitch#caseIsSpecified_P(org.vclipse.vcml.vcml.IsSpecified_P)
	 */
	@Override
	public EObject caseIsSpecified_P(final IsSpecified_P parent) {
		createVCObject(parent.getCharacteristic());
		return parent;
	}

	/**
	 * @see org.vclipse.vcml.vcml.util.VcmlSwitch#casePartOfCondition(org.vclipse.vcml.vcml.PartOfCondition)
	 */
	@Override
	public EObject casePartOfCondition(final PartOfCondition parent) {
		doSwitch(parent.getChild());
		doSwitch(parent.getParent());
		return parent;
	}

	/**
	 * @see org.vclipse.vcml.vcml.util.VcmlSwitch#caseSubpartOfCondition(org.vclipse.vcml.vcml.SubpartOfCondition)
	 */
	@Override
	public EObject caseSubpartOfCondition(final SubpartOfCondition parent) {
		doSwitch(parent.getChild());
		doSwitch(parent.getParent());
		return parent;
	}

	/**
	 * @see org.vclipse.vcml.vcml.util.VcmlSwitch#caseTypeOf(org.vclipse.vcml.vcml.TypeOf)
	 */
	@Override
	public EObject caseTypeOf(final TypeOf parent) {
		doSwitch(parent.getVariantclass());
		return parent;
	}

	/**
	 * @see org.vclipse.vcml.vcml.util.VcmlSwitch#caseUnaryCondition(org.vclipse.vcml.vcml.UnaryCondition)
	 */
	@Override
	public EObject caseUnaryCondition(final UnaryCondition parent) {
		doSwitch(parent.getCondition());
		return parent;
	}

	/**
	 * @see org.vclipse.vcml.vcml.util.VcmlSwitch#caseBinaryExpression(org.vclipse.vcml.vcml.BinaryExpression)
	 */
	@Override
	public EObject caseBinaryExpression(final BinaryExpression parent) {
		doSwitch(parent.getLeft());
		doSwitch(parent.getRight());
		return parent;
	}

	/**
	 * @see org.vclipse.vcml.vcml.util.VcmlSwitch#caseFunctionCall(org.vclipse.vcml.vcml.FunctionCall)
	 */
	@Override
	public EObject caseFunctionCall(final FunctionCall parent) {
		doSwitch(parent.getArgument());
		return parent;
	}
	
	/**
	 * @see org.vclipse.vcml.vcml.util.VcmlSwitch#caseObjectCharacteristicReference(org.vclipse.vcml.vcml.ObjectCharacteristicReference)
	 */
	@Override
	public EObject caseObjectCharacteristicReference(final ObjectCharacteristicReference parent) {
		createVCObject(parent.getCharacteristic());
		doSwitch(parent.getLocation());
		return parent;
	}

	/**
	 * @see org.vclipse.vcml.vcml.util.VcmlSwitch#caseShortVarReference(org.vclipse.vcml.vcml.ShortVarReference)
	 */
	@Override
	public EObject caseShortVarReference(final ShortVarReference parent) {
		doSwitch(parent.getRef());
		return parent;
	}
	
	/**
	 * @see org.vclipse.vcml.vcml.util.VcmlSwitch#caseShortVarDefinition(org.vclipse.vcml.vcml.ShortVarDefinition)
	 */
	@Override
	public EObject caseShortVarDefinition(final ShortVarDefinition parent) {
		createVCObject(parent.getCharacteristic());
		return parent;
	}

	/**
	 * @see org.vclipse.vcml.vcml.util.VcmlSwitch#caseCharacteristicReference_P(org.vclipse.vcml.vcml.CharacteristicReference_P)
	 */
	@Override
	public EObject caseCharacteristicReference_P(final CharacteristicReference_P parent) {
		createVCObject(parent.getCharacteristic());
		return parent;
	}

	/**
	 * @see org.vclipse.vcml.vcml.util.VcmlSwitch#caseMDataCharacteristic_C(org.vclipse.vcml.vcml.MDataCharacteristic_C)
	 */
	@Override
	public EObject caseMDataCharacteristic_C(final MDataCharacteristic_C parent) {
		createVCObject(parent.getCharacteristic());
		return parent;
	}

	/**
	 * @see org.vclipse.vcml.vcml.util.VcmlSwitch#caseMDataCharacteristic_P(org.vclipse.vcml.vcml.MDataCharacteristic_P)
	 */
	@Override
	public EObject caseMDataCharacteristic_P(final MDataCharacteristic_P parent) {
		createVCObject(parent.getCharacteristic());
		return parent;
	}

	/**
	 * @see org.vclipse.vcml.vcml.util.VcmlSwitch#caseSumParts(org.vclipse.vcml.vcml.SumParts)
	 */
	@Override
	public EObject caseSumParts(final SumParts parent) {
		createVCObject(parent.getCharacteristic());
		return parent;
	}

	/**
	 * @see org.vclipse.vcml.vcml.util.VcmlSwitch#caseUnaryExpression(org.vclipse.vcml.vcml.UnaryExpression)
	 */
	@Override
	public EObject caseUnaryExpression(final UnaryExpression parent) {
		doSwitch(parent.getExpression());
		return parent;
	}
	
	/**
	 * @see org.vclipse.vcml.vcml.util.VcmlSwitch#caseBillOfMaterial(org.vclipse.vcml.vcml.BillOfMaterial)
	 */
	@Override
	public EObject caseBillOfMaterial(final BillOfMaterial parent) {
		for(BOMItem item : parent.getItems()) {
			return doSwitch(item);
		}
		return parent;
	}

	/**
	 * @see org.vclipse.vcml.vcml.util.VcmlSwitch#caseBOMItem(org.vclipse.vcml.vcml.BOMItem)
	 */
	@Override
	public EObject caseBOMItem(final BOMItem parent) {
		createVCObject(parent.getMaterial());
		createVCObject(parent.getSelectionCondition());
		for(ConfigurationProfileEntry entry : parent.getEntries()) {
			doSwitch(entry);
		}
		return parent;
	}

	/**
	 * @see org.vclipse.vcml.vcml.util.VcmlSwitch#caseCharacteristicGroup(org.vclipse.vcml.vcml.CharacteristicGroup)
	 */
	@Override
	public EObject caseCharacteristicGroup(final CharacteristicGroup parent) {
		for(Characteristic cstic : parent.getCharacteristics()) {
			createVCObject(cstic);
		}
		return parent;
	}

	/**
	 * @see org.vclipse.vcml.vcml.util.VcmlSwitch#caseCharacteristicOrValueDependencies(org.vclipse.vcml.vcml.CharacteristicOrValueDependencies)
	 */
	@Override
	public EObject caseCharacteristicOrValueDependencies(final CharacteristicOrValueDependencies parent) {
		for(GlobalDependency dependency : parent.getDependencies()) {
			doSwitch(dependency);
		}
		doSwitch(parent.getLocalPrecondition());
		doSwitch(parent.getLocalSelectionCondition());
		return parent;
	}

	/**
	 * @see org.vclipse.vcml.vcml.util.VcmlSwitch#caseCharacteristicValue(org.vclipse.vcml.vcml.CharacteristicValue)
	 */
	@Override
	public EObject caseCharacteristicValue(final CharacteristicValue parent) {
		doSwitch(parent.getDependencies());
		return parent;
	}

	/**
	 * @see org.vclipse.vcml.vcml.util.VcmlSwitch#caseConditionalConstraintRestriction(org.vclipse.vcml.vcml.ConditionalConstraintRestriction)
	 */
	@Override
	public EObject caseConditionalConstraintRestriction(final ConditionalConstraintRestriction parent) {
		doSwitch(parent.getCondition());
		doSwitch(parent.getRestriction());
		return parent;
	}

	/**
	 * @see org.vclipse.vcml.vcml.util.VcmlSwitch#caseConditionSource(org.vclipse.vcml.vcml.ConditionSource)
	 */
	@Override
	public EObject caseConditionSource(final ConditionSource parent) {
		doSwitch(parent.getCondition());
		return parent;
	}

	/**
	 * @see org.vclipse.vcml.vcml.util.VcmlSwitch#caseConfigurationProfile(org.vclipse.vcml.vcml.ConfigurationProfile)
	 */
	@Override
	public EObject caseConfigurationProfile(final ConfigurationProfile parent) {
		createVCObject(parent.getUidesign());
		for(DependencyNet net : parent.getDependencyNets()) {
			createVCObject(net);
		}
		for(ConfigurationProfileEntry entry : parent.getEntries()) {
			doSwitch(entry);
		}
		return parent;
	}

	/**
	 * @see org.vclipse.vcml.vcml.util.VcmlSwitch#caseConfigurationProfileEntry(org.vclipse.vcml.vcml.ConfigurationProfileEntry)
	 */
	@Override
	public EObject caseConfigurationProfileEntry(final ConfigurationProfileEntry parent) {
		createVCObject(parent.getDependency());
		return parent;
	}

	/**
	 * @see org.vclipse.vcml.vcml.util.VcmlSwitch#caseConstraint(org.vclipse.vcml.vcml.Constraint)
	 */
	@Override
	public EObject caseConstraint(final Constraint parent) {
		createVCObject(parent);
		return parent;
	}

	/**
	 * @see org.vclipse.vcml.vcml.util.VcmlSwitch#caseConstraintObject(org.vclipse.vcml.vcml.ConstraintObject)
	 */
	@Override
	public EObject caseConstraintObject(final ConstraintObject parent) {
		for(ShortVarDefinition def : parent.getShortVars()) {
			doSwitch(def);
		}
		return parent;
	}

	/**
	 * @see org.vclipse.vcml.vcml.util.VcmlSwitch#caseConstraintSource(org.vclipse.vcml.vcml.ConstraintSource)
	 */
	@Override
	public EObject caseConstraintSource(final ConstraintSource parent) {
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

	/**
	 * @see org.vclipse.vcml.vcml.util.VcmlSwitch#caseLocalDependency(org.vclipse.vcml.vcml.LocalDependency)
	 */
	@Override
	public EObject caseLocalDependency(final LocalDependency parent) {
		doSwitch(parent.getSource());
		return parent;
	}

	/**
	 * @see org.vclipse.vcml.vcml.util.VcmlSwitch#caseLocalPrecondition(org.vclipse.vcml.vcml.LocalPrecondition)
	 */
	@Override
	public EObject caseLocalPrecondition(final LocalPrecondition parent) {
		doSwitch(parent.getSource());
		return parent;
	}

	/**
	 * @see org.vclipse.vcml.vcml.util.VcmlSwitch#caseLocalSelectionCondition(org.vclipse.vcml.vcml.LocalSelectionCondition)
	 */
	@Override
	public EObject caseLocalSelectionCondition(final LocalSelectionCondition object) {
		return doSwitch(object.getSource());
	}

	/**
	 * @see org.vclipse.vcml.vcml.util.VcmlSwitch#caseNumericCharacteristicValue(org.vclipse.vcml.vcml.NumericCharacteristicValue)
	 */
	@Override
	public EObject caseNumericCharacteristicValue(final NumericCharacteristicValue parent) {
		doSwitch(parent.getDependencies());
		return parent;
	}

	/**
	 * @see org.vclipse.vcml.vcml.util.VcmlSwitch#caseNumericType(org.vclipse.vcml.vcml.NumericType)
	 */
	@Override
	public EObject caseNumericType(final NumericType parent) {
		for(NumericCharacteristicValue numCharValue : parent.getValues()) {
			doSwitch(numCharValue);
		}
		return parent;
	}

	/**
	 * @see org.vclipse.vcml.vcml.util.VcmlSwitch#casePrecondition(org.vclipse.vcml.vcml.Precondition)
	 */
	@Override
	public EObject casePrecondition(final Precondition parent) {
		doSwitch(parent.getSource());
		return parent;
	}

	/**
	 * @see org.vclipse.vcml.vcml.util.VcmlSwitch#caseProcedureSource(org.vclipse.vcml.vcml.ProcedureSource)
	 */
	@Override
	public EObject caseProcedureSource(final ProcedureSource parent) {
		for(Statement statement : parent.getStatements()) {
			doSwitch(statement);
		}
		return parent;
	}

	/**
	 * @see org.vclipse.vcml.vcml.util.VcmlSwitch#caseSelectionCondition(org.vclipse.vcml.vcml.SelectionCondition)
	 */
	@Override
	public EObject caseSelectionCondition(final SelectionCondition parent) {
		doSwitch(parent.getSource());
		return parent;
	}

	/**
	 * @see org.vclipse.vcml.vcml.util.VcmlSwitch#caseSetDefault(org.vclipse.vcml.vcml.SetDefault)
	 */
	@Override
	public EObject caseSetDefault(final SetDefault parent) {
		createVCObject(parent.getCharacteristic());
		doSwitch(parent.getExpression());
		return parent;		
	}


	/**
	 * @see org.vclipse.vcml.vcml.util.VcmlSwitch#caseSymbolicType(org.vclipse.vcml.vcml.SymbolicType)
	 */
	@Override
	public EObject caseSymbolicType(final SymbolicType parent) {
		for(CharacteristicValue dependency : parent.getValues()) {
			doSwitch(dependency);
		}
		return parent;	
	}
	
	/**
	 * @param source
	 * @param clazz
	 * @return
	 */
	private void createVCObject(final EObject source) {
		if(source instanceof VCObject) {
			VCObject newObject = (VCObject)FACTORY.create(source.eClass());
			String name = ((VCObject)source).getName();
			newObject.setName(name);
			objects.put(name, newObject);
		}
	}
}
