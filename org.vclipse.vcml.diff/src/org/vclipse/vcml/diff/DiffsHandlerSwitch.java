/**
 * 
 */
package org.vclipse.vcml.diff;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.compare.diff.metamodel.AttributeChangeLeftTarget;
import org.eclipse.emf.compare.diff.metamodel.AttributeChangeRightTarget;
import org.eclipse.emf.compare.diff.metamodel.DiffElement;
import org.eclipse.emf.compare.diff.metamodel.DiffModel;
import org.eclipse.emf.compare.diff.metamodel.ModelElementChangeLeftTarget;
import org.eclipse.emf.compare.diff.metamodel.ModelElementChangeRightTarget;
import org.eclipse.emf.compare.diff.metamodel.ReferenceChange;
import org.eclipse.emf.compare.diff.metamodel.ReferenceChangeLeftTarget;
import org.eclipse.emf.compare.diff.metamodel.ReferenceChangeRightTarget;
import org.eclipse.emf.compare.diff.metamodel.ReferenceOrderChange;
import org.eclipse.emf.compare.diff.metamodel.UpdateAttribute;
import org.eclipse.emf.compare.diff.metamodel.util.DiffSwitch;
import org.eclipse.emf.ecore.EObject;
import org.vclipse.vcml.vcml.Import;
import org.vclipse.vcml.vcml.Model;
import org.vclipse.vcml.vcml.Option;
import org.vclipse.vcml.vcml.VCObject;

/**
 *
 */
public class DiffsHandlerSwitch extends DiffSwitch<Boolean> {

	/**
	 * 
	 */
	private final Boolean HANDELED = Boolean.TRUE;
	
	/**
	 * 
	 */
	private final Boolean NOT_HANDELED = null;

	/**
	 * 
	 */
	private Model model2Build;
	
	/**
	 * 
	 */
	private EList<VCObject> modelElements; 
	
	/**
	 *	
	 */
	private IDiffFilter diffFilter;
	
	/**
	 * 
	 */
	private ReferenceConstructor referenceConstructor;
	
	/**
	 * 
	 */
	private Map<String, VCObject> objects2Add;
	
	/**
	 * 
	 */
	private IProgressMonitor monitor;
	
	/**
	 * @param model
	 * @param monitor
	 */
	public DiffsHandlerSwitch(final Model model, final IProgressMonitor monitor) {
		model2Build = model;
		modelElements = model.getObjects();
		diffFilter = new DefaultDiffFilter();
		referenceConstructor = new ReferenceConstructor();
		objects2Add = new HashMap<String, VCObject>();
		this.monitor = monitor;
	}

	/**
	 * @see org.eclipse.emf.compare.diff.metamodel.util.DiffSwitch#caseDiffModel(org.eclipse.emf.compare.diff.metamodel.DiffModel)
	 */
	@Override
	public Boolean caseDiffModel(final DiffModel object) {
		for(final DiffElement diffElement : object.getOwnedElements()) {
			if(monitor.isCanceled()) {
				return HANDELED;
			}
			doSwitch(diffElement);
		}
		for(String key : objects2Add.keySet()) {
			if(monitor.isCanceled()) {
				return HANDELED;
			}
			VCObject vcobject = objects2Add.get(key);
			if(!modelElements.contains(vcobject)) {
				modelElements.add(vcobject);
			}
		}
		return HANDELED;
	}
	
	/**
	 * @see org.eclipse.emf.compare.diff.metamodel.util.DiffSwitch#caseDiffElement(org.eclipse.emf.compare.diff.metamodel.DiffElement)
	 */
	@Override
	public Boolean caseDiffElement(final DiffElement object) {
		for(DiffElement element : object.getSubDiffElements()) {
			if(monitor.isCanceled()) {
				return HANDELED;
			}
			doSwitch(element);
		}
		return NOT_HANDELED;
	}

	/**
	 * @see org.eclipse.emf.compare.diff.metamodel.util.DiffSwitch#caseModelElementChangeLeftTarget(org.eclipse.emf.compare.diff.metamodel.ModelElementChangeLeftTarget)
	 */
	@Override
	public Boolean caseModelElementChangeLeftTarget(final ModelElementChangeLeftTarget object) {
		return addObject2HandleList(object.getLeftElement());
	}

	/**
	 * @see org.eclipse.emf.compare.diff.metamodel.util.DiffSwitch#caseModelElementChangeRightTarget(org.eclipse.emf.compare.diff.metamodel.ModelElementChangeRightTarget)
	 */
	@Override
	public Boolean caseModelElementChangeRightTarget(final ModelElementChangeRightTarget object) {
		EObject rightElement = object.getRightElement();
		// extract deleted VCObjects
		if(rightElement instanceof VCObject) {
			return addObject2HandleList(rightElement);
		} 
		// otherwise extract VCObject from the left model
		else {
			return addObject2HandleList(object.getLeftParent());
		}
	}
	
	/**
	 * @see org.eclipse.emf.compare.diff.metamodel.util.DiffSwitch#caseReferenceChangeLeftTarget(org.eclipse.emf.compare.diff.metamodel.ReferenceChangeLeftTarget)
	 */
	@Override
	public Boolean caseReferenceChangeLeftTarget(final ReferenceChangeLeftTarget object) {
		return addObject2HandleList(object.getLeftElement());
	}

	/**
	 * @see org.eclipse.emf.compare.diff.metamodel.util.DiffSwitch#caseReferenceChangeRightTarget(org.eclipse.emf.compare.diff.metamodel.ReferenceChangeRightTarget)
	 */
	@Override
	public Boolean caseReferenceChangeRightTarget(final ReferenceChangeRightTarget object) {
		return addObject2HandleList(object.getLeftElement());
	}

	/**
	 * @see org.eclipse.emf.compare.diff.metamodel.util.DiffSwitch#caseReferenceChange(org.eclipse.emf.compare.diff.metamodel.ReferenceChange)
	 */
	@Override
	public Boolean caseReferenceChange(final ReferenceChange object) {
		if(!diffFilter.filter(object.getReference(), object.getKind())) {
			return addObject2HandleList(object.getLeftElement());
		}
		return HANDELED;
	}

	/**
	 * @see org.eclipse.emf.compare.diff.metamodel.util.DiffSwitch#caseReferenceOrderChange(org.eclipse.emf.compare.diff.metamodel.ReferenceOrderChange)
	 */
	@Override
	public Boolean caseReferenceOrderChange(final ReferenceOrderChange object) {
		EList<EObject> leftTarget = object.getLeftTarget();
		EList<EObject> rightTarget = object.getRightTarget();
		if(leftTarget != null && rightTarget != null) {
			if(leftTarget.size() == rightTarget.size()) {
				if(!diffFilter.filter(object.getReference(), object.getKind())) {
					return addObject2HandleList(object.getLeftElement());
				}
			}
		} else {
			return addObject2HandleList(object.getLeftElement());
		}
		return HANDELED;
	}
	
	/**
	 * @see org.eclipse.emf.compare.diff.metamodel.util.DiffSwitch#caseAttributeChangeLeftTarget(org.eclipse.emf.compare.diff.metamodel.AttributeChangeLeftTarget)
	 */
	@Override
	public Boolean caseAttributeChangeLeftTarget(final AttributeChangeLeftTarget object) {
		return addObject2HandleList(object.getRightElement());
	}

	/**
	 * @see org.eclipse.emf.compare.diff.metamodel.util.DiffSwitch#caseAttributeChangeRightTarget(org.eclipse.emf.compare.diff.metamodel.AttributeChangeRightTarget)
	 */
	@Override
	public Boolean caseAttributeChangeRightTarget(final AttributeChangeRightTarget object) {
		return addObject2HandleList(object.getRightElement());
	}

	/**
	 * @see org.eclipse.emf.compare.diff.metamodel.util.DiffSwitch#caseUpdateAttribute(org.eclipse.emf.compare.diff.metamodel.UpdateAttribute)
	 */
	@Override
	public Boolean caseUpdateAttribute(final UpdateAttribute object) {
		if(!diffFilter.filter(object.getAttribute(), object.getKind())) {
			return addObject2HandleList(object.getLeftElement());
		}
		return NOT_HANDELED;
	}

	/**
	 * @param object
	 * @return
	 */
	private boolean addObject2HandleList(final EObject object) {
		if(object instanceof Option) {
			model2Build.getOptions().add((Option)object);
		} else if(object instanceof Import) {
			model2Build.getImports().add((Import)object);
		} else if(object instanceof VCObject) {
			handleObject(object);
		} else {
			EObject parent = object.eContainer();
			while(parent != null && !(parent instanceof VCObject)) {
				parent = parent.eContainer();
			}
			handleObject(parent);
		}
		return HANDELED;
	}
	
	/**
	 * @param vcobject
	 */
	private void handleObject(EObject object) {
		if(object instanceof VCObject) {
			VCObject vcobject = (VCObject)object;
			objects2Add.put(vcobject.getName(), vcobject);
			referenceConstructor.reset();
			referenceConstructor.doSwitch(vcobject);
			Map<String, EObject> createdObjects = referenceConstructor.getCreatedObjects();
			if(createdObjects != null) {
				for(EObject created : createdObjects.values()) {
					if(monitor.isCanceled()) {
						return;
					}
					if(created instanceof VCObject) {					
						vcobject = (VCObject)created;
						objects2Add.put(vcobject.getName(), vcobject);
					}
				}
			}
		}
	}
}
