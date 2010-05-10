/**
 * 
 */
package org.vclipse.vcml.diff;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.compare.diff.metamodel.AttributeChangeLeftTarget;
import org.eclipse.emf.compare.diff.metamodel.DiffElement;
import org.eclipse.emf.compare.diff.metamodel.DiffModel;
import org.eclipse.emf.compare.diff.metamodel.DifferenceKind;
import org.eclipse.emf.compare.diff.metamodel.ModelElementChangeLeftTarget;
import org.eclipse.emf.compare.diff.metamodel.ModelElementChangeRightTarget;
import org.eclipse.emf.compare.diff.metamodel.ReferenceChange;
import org.eclipse.emf.compare.diff.metamodel.ReferenceChangeLeftTarget;
import org.eclipse.emf.compare.diff.metamodel.ReferenceOrderChange;
import org.eclipse.emf.compare.diff.metamodel.util.DiffSwitch;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EObject;
import org.vclipse.vcml.vcml.Import;
import org.vclipse.vcml.vcml.Model;
import org.vclipse.vcml.vcml.Option;
import org.vclipse.vcml.vcml.VCObject;
import org.vclipse.vcml.vcml.VcmlFactory;

/**
 *
 */
public class DiffsHandlerSwitch extends DiffSwitch<Boolean> {

	/**
	 * 
	 */
	private Model model2Build;
	
	/**
	 * 
	 */
	private EList<VCObject> modelElements; 

	
	/**
	 * @param model
	 */
	public DiffsHandlerSwitch(Model model) {
		model2Build = model;
		modelElements = model2Build.getObjects();
	}
	
	/**
	 * @see org.eclipse.emf.compare.diff.metamodel.util.DiffSwitch#caseDiffModel(org.eclipse.emf.compare.diff.metamodel.DiffModel)
	 */
	@Override
	public Boolean caseDiffModel(final DiffModel object) {
		for(final DiffElement diffElement : object.getOwnedElements()) {
			doSwitch(diffElement);
		}
		return Boolean.TRUE;
	}
	
	/**
	 * @see org.eclipse.emf.compare.diff.metamodel.util.DiffSwitch#caseDiffElement(org.eclipse.emf.compare.diff.metamodel.DiffElement)
	 */
	@Override
	public Boolean caseDiffElement(final DiffElement object) {
		for(DiffElement element : object.getSubDiffElements()) {
			doSwitch(element);
		}
		return Boolean.TRUE;
	}

	/**
	 * @see org.eclipse.emf.compare.diff.metamodel.util.DiffSwitch#caseReferenceChange(org.eclipse.emf.compare.diff.metamodel.ReferenceChange)
	 */
	@Override
	public Boolean caseReferenceChange(ReferenceChange object) {
		return addEObject(object.getLeftElement());
	}

	/**
	 * @see org.eclipse.emf.compare.diff.metamodel.util.DiffSwitch#caseReferenceChangeLeftTarget(org.eclipse.emf.compare.diff.metamodel.ReferenceChangeLeftTarget)
	 */
	@Override
	public Boolean caseReferenceChangeLeftTarget(final ReferenceChangeLeftTarget object) {
		return addEObject(object.getLeftElement()) ? Boolean.TRUE : null;
	}
	
	/**
	 * @see org.eclipse.emf.compare.diff.metamodel.util.DiffSwitch#caseReferenceOrderChange(org.eclipse.emf.compare.diff.metamodel.ReferenceOrderChange)
	 */
	@Override
	public Boolean caseReferenceOrderChange(final ReferenceOrderChange object) {
		EObject leftElement = object.getLeftElement();		
		if(leftElement instanceof VCObject) {
			if(!modelElements.contains(leftElement)) {
				modelElements.add((VCObject)leftElement);
				for(EObject o : object.getLeftTarget()) {
					EObject no = VcmlFactory.eINSTANCE.create(o.eClass());
					if(no instanceof VCObject) {
						((VCObject)no).setName(((VCObject)o).getName());
						modelElements.add((VCObject)no);
					}
				}
			}
		}
		return Boolean.TRUE;
	}

	/**
	 * @see org.eclipse.emf.compare.diff.metamodel.util.DiffSwitch#caseAttributeChangeLeftTarget(org.eclipse.emf.compare.diff.metamodel.AttributeChangeLeftTarget)
	 */
	@Override
	public Boolean caseAttributeChangeLeftTarget(final AttributeChangeLeftTarget object) {
		final EObject leftElement = object.getLeftElement();
		final EAttribute attribute = object.getAttribute();
		if(modelElements.contains(leftElement)) {
			leftElement.eSet(attribute, attribute.eGet(attribute, true));
			return Boolean.TRUE;
		} else {
			return addEObject(leftElement) ? Boolean.TRUE : null;
		}
	}
	
	/**
	 * @see org.eclipse.emf.compare.diff.metamodel.util.DiffSwitch#caseModelElementChangeRightTarget(org.eclipse.emf.compare.diff.metamodel.ModelElementChangeRightTarget)
	 */
	@Override
	public Boolean caseModelElementChangeRightTarget(ModelElementChangeRightTarget object) {
		if(DifferenceKind.DELETION == object.getKind()) {
			final EObject element = object.getRightElement();
			if(element instanceof Option) {
				return Boolean.FALSE;
			} else {
				return addEObject(element);
			}
		}
		return null;
	}

	/**
	 * @see org.eclipse.emf.compare.diff.metamodel.util.DiffSwitch#caseModelElementChangeLeftTarget(org.eclipse.emf.compare.diff.metamodel.ModelElementChangeLeftTarget)
	 */
	@Override
	public Boolean caseModelElementChangeLeftTarget(final ModelElementChangeLeftTarget object) {
		return addEObject(object.getLeftElement()) ? Boolean.TRUE : null;
	}
	
	/**
	 * @param object
	 * @return
	 */
	private boolean addEObject(EObject object) {
		if(!modelElements.contains(object)) {
			if(object instanceof Import) {
				return model2Build.getImports().add((Import)object);
			} else if(object instanceof Option) {
				return model2Build.getOptions().add((Option)object);
			} else if(object instanceof VCObject) {
				return modelElements.add((VCObject)object);
			}
		}
		return Boolean.FALSE;
	}
}
