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
import org.vclipse.vcml.vcml.Model;
import org.vclipse.vcml.vcml.Option;
import org.vclipse.vcml.vcml.VCObject;

/**
 *
 */
public class DiffsHandlerSwitch extends DiffSwitch<Boolean> {

	
	private final Boolean HANDLED = Boolean.TRUE;
	private final Boolean NOT_HANDLED = null;


	private Model model2Build;
	

	private EList<VCObject> modelElements; 
	private Map<String, VCObject> objects2Add;
	

	private IDiffFilter diffFilter;
	private ReferenceConstructor referenceConstructor;
	private IProgressMonitor monitor;
	

	public DiffsHandlerSwitch(final Model model, final IProgressMonitor monitor) {
		model2Build = model;
		modelElements = model.getObjects();
		diffFilter = new DefaultDiffFilter();
		referenceConstructor = new ReferenceConstructor();
		objects2Add = new HashMap<String, VCObject>();
		this.monitor = monitor;
	}

	@Override
	public Boolean caseDiffModel(final DiffModel object) {
		for(final DiffElement diffElement : object.getOwnedElements()) {
			if(monitor.isCanceled()) {
				return HANDLED;
			}
			System.err.println("caseDiffModel with " + diffElement);
			doSwitch(diffElement);
		}
		for(String key : objects2Add.keySet()) {
			if(monitor.isCanceled()) {
				return HANDLED;
			}
			VCObject vcobject = objects2Add.get(key);
			if(!modelElements.contains(vcobject)) {
				modelElements.add(vcobject);
			}
		}
		return HANDLED;
	}

	@Override
	public Boolean caseDiffElement(final DiffElement object) {
		System.err.println(object.getClass().getSimpleName() + " " + object);
		for(DiffElement element : object.getSubDiffElements()) {
			if(monitor.isCanceled()) {
				return HANDLED;
			}
			System.err.println(element.getClass().getSimpleName() + " " + element);
			doSwitch(element);
		}
		return NOT_HANDLED;
	}

	@Override
	public Boolean caseModelElementChangeLeftTarget(final ModelElementChangeLeftTarget object) {
		return addObject2HandleList(object.getLeftElement());
	}

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
	
	@Override
	public Boolean caseReferenceChangeLeftTarget(final ReferenceChangeLeftTarget object) {
		return addObject2HandleList(object.getLeftElement());
	}

	@Override
	public Boolean caseReferenceChangeRightTarget(final ReferenceChangeRightTarget object) {
		return addObject2HandleList(object.getLeftElement());
	}

	@Override
	public Boolean caseReferenceChange(final ReferenceChange object) {
		if(!diffFilter.filter(object.getReference(), object.getKind())) {
			return addObject2HandleList(object.getLeftElement());
		}
		return HANDLED;
	}

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
		return HANDLED;
	}
	
	@Override
	public Boolean caseAttributeChangeLeftTarget(final AttributeChangeLeftTarget object) {
		return addObject2HandleList(object.getRightElement());
	}

	@Override
	public Boolean caseAttributeChangeRightTarget(final AttributeChangeRightTarget object) {
		return addObject2HandleList(object.getRightElement());
	}

	@Override
	public Boolean caseUpdateAttribute(final UpdateAttribute object) {
		if(!diffFilter.filter(object.getAttribute(), object.getKind())) {
			return addObject2HandleList(object.getLeftElement());
		}
		return NOT_HANDLED;
	}

	private boolean addObject2HandleList(final EObject object) {
//		System.err.println("addObject2HandleList " + object);
		if(object instanceof Option) {
			model2Build.getOptions().add((Option)object);
		} else if(object instanceof VCObject) {
			handleObject(object);
		} else {
			EObject parent = object.eContainer();
			while(parent != null && !(parent instanceof VCObject)) {
				parent = parent.eContainer();
			}
			handleObject(parent);
		}
		return HANDLED;
	}
	
	private void handleObject(EObject object) {
		System.err.println("handleObject " + object);
		if(object instanceof VCObject) {
			VCObject vcobject = (VCObject)object;
			objects2Add.put(vcobject.getName(), vcobject);
			referenceConstructor.reset();
			referenceConstructor.doSwitch(vcobject);
			
			for(VCObject createdVCObject : referenceConstructor.getCreatedObjects().values()) {
				if(monitor.isCanceled()) {
					return;
				}
				objects2Add.put(createdVCObject.getName(), vcobject);
			}
		}
	}
}
