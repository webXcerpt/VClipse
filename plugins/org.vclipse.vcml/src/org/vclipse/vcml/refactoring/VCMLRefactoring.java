package org.vclipse.vcml.refactoring;

import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.vclipse.refactoring.IRefactoringContext;
import org.vclipse.vcml.vcml.BinaryCondition;
import org.vclipse.vcml.vcml.CharacteristicReference_C;
import org.vclipse.vcml.vcml.CharacteristicReference_P;
import org.vclipse.vcml.vcml.Comparison;
import org.vclipse.vcml.vcml.ComparisonOperator;
import org.vclipse.vcml.vcml.InCondition_C;
import org.vclipse.vcml.vcml.Literal;
import org.vclipse.vcml.vcml.NumberList;
import org.vclipse.vcml.vcml.NumberListEntry;
import org.vclipse.vcml.vcml.NumericLiteral;
import org.vclipse.vcml.vcml.PFunction;
import org.vclipse.vcml.vcml.SymbolList;
import org.vclipse.vcml.vcml.SymbolicLiteral;

import com.google.common.collect.Lists;

public class VCMLRefactoring extends VCMLSimplifier {

	private static final String RIGHT_REFERENCE_ENTRY = "@right";
	
	public void refactoring_Extract_InCondition_C_list(IRefactoringContext context, InCondition_C condtion) {
		CharacteristicReference_C cstic = condtion.getCharacteristic();
		org.vclipse.vcml.vcml.List list = condtion.getList();
		List<Comparison> comparisons = Lists.newArrayList();
		if(list instanceof NumberList) {
			for(NumberListEntry entry : ((NumberList)list).getEntries()) {
				if(entry instanceof NumericLiteral) {
					Comparison comparison = VCML_FACTORY.createComparison();
					comparison.setLeft(EcoreUtil.copy(cstic));
					comparison.setOperator(ComparisonOperator.EQ);
					comparison.setRight(EcoreUtil.copy((NumericLiteral)entry));
					comparisons.add(comparison);
				}
			}
		} else if(list instanceof SymbolList) {
			EList<SymbolicLiteral> entries = ((SymbolList)list).getEntries();
			for(SymbolicLiteral literal : entries) {
				Comparison comparison = VCML_FACTORY.createComparison();
				comparison.setLeft(EcoreUtil.copy(cstic));
				comparison.setOperator(ComparisonOperator.EQ);
				comparison.setRight(EcoreUtil.copy(literal));
				comparisons.add(comparison);
			}
		}
		BinaryCondition be = VCML_FACTORY.createBinaryCondition();
		BinaryCondition current = be;
		for(Comparison comparison : comparisons) {
			if(be.getLeft() == null) {
				be.setLeft(comparison);
				be.setOperator("or");
				continue;
			} 
			if(comparisons.indexOf(comparison) != comparisons.size() - 1) {
				current = VCML_FACTORY.createBinaryCondition();
				current.setLeft(comparison);
				current.setOperator("or");
				be.setRight(current);
			} else {
				current.setRight(comparison);
			}
		}
		
		EObject container = condtion.eContainer();
		EReference feature = condtion.eContainmentFeature();
		container.eSet(feature, be);
		context.setSourceElement(container);
	}

//	public List<? extends EObject> refactoring_Replace_PFunction_values(IRefactoringContext context, PFunction pfunction) {
//		List<EObject> changes = Lists.newArrayList();
//		EList<Literal> values = pfunction.getValues();
//		Literal literal = values.get(values.indexOf(context.getSourceElement()));
//		if(literal instanceof CharacteristicReference_P) {
//			CharacteristicReference_P crp = (CharacteristicReference_P)literal;
//			Map<?, ?> attributes = context.getAttributes();
//			replaceCsticReferences(changes, crp, attributes);
//		}
//		return changes;
//	}
//	
//	private void replaceCsticReferences(List<EObject> changes, CharacteristicReference_P cstic_p, Map<?, ?> args) {
//		Resource resource = cstic_p.eResource();
//		Object button_state = args.get(BUTTON_STATE);
//		if(button_state instanceof Boolean && (Boolean)button_state) {
//			for(IReferenceDescription description :  referencesFinder.getReferences(cstic_p.getCharacteristic(), true)) {
//				URI uri = description.getSourceEObjectUri();
//				if(uri.toString().contains(RIGHT_REFERENCE_ENTRY)) {
//					EObject eObject = resource.getEObject(uri.fragment());
//					EObject first = cstic_p.eContainer();
//					EObject second = eObject.eContainer();
//					if(first != null && second != null) {
//						if(first.eClass() == second.eClass()) {
//							if(eObject instanceof CharacteristicReference_P) {
//								replaceCsticReference(changes, (CharacteristicReference_P)eObject, args);
//							}
//						}
//					}				
//				}
//			}
//		} 
//		replaceCsticReference(changes, cstic_p, args);
//	}
//	
//	private void replaceCsticReference(List<EObject> changes, CharacteristicReference_P cstic_p, Map<?, ?> args) {
//		PFunction pfunction = EcoreUtil2.getContainerOfType(cstic_p, PFunction.class);
//		if(pfunction != null) {
//			Object text_field_entry = args.get(TEXT_FIELD_ENTRY);
//			if(text_field_entry instanceof String) {
//				String text = (String)text_field_entry;
//				Literal newLiteral = null;
//				try {
//					Integer.parseInt(text);
//					NumericLiteral numLit = VCML_FACTORY.createNumericLiteral();
//					numLit.setValue(text);
//					newLiteral = numLit;
//				} catch(Exception exception) {
//					SymbolicLiteral symlit = VCML_FACTORY.createSymbolicLiteral();
//					symlit.setValue(text);
//					newLiteral = symlit;
//				}
//				EReference reference = VCML_PACKAGE.getPFunction_Values();
//				ListChange listChange = getListChange(ChangeKind.ADD_LITERAL, reference, newLiteral, pfunction.getValues());
//				changes.add(listChange);
//				EObject removeObject = cstic_p.eContainer() instanceof MDataCharacteristic_P ? cstic_p.eContainer() : cstic_p;
//				listChange = getListChange(ChangeKind.REMOVE_LITERAL, reference, removeObject, pfunction.getValues());
//				changes.add(listChange);
//			}			
//		}
//	}
}
