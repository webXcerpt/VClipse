package org.vclipse.vcml.diff;

import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
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
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.xtext.EcoreUtil2;
import org.vclipse.vcml.vcml.Constraint;
import org.vclipse.vcml.vcml.DependencyNet;
import org.vclipse.vcml.vcml.Model;
import org.vclipse.vcml.vcml.Option;
import org.vclipse.vcml.vcml.VCObject;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class DiffsHandlerSwitch extends DiffSwitch<Boolean> {

	private Boolean HANDLED = Boolean.TRUE;
	private Boolean NOT_HANDLED = null;

	private Model resultModel;
	private Model newStateModel;
	private Set<VCObject> modelElements; 
	private IDiffFilter diffFilter;
	private IProgressMonitor monitor;
	
	public DiffsHandlerSwitch(Model resultModel, Model newStateModel, IProgressMonitor monitor) {
		this.resultModel = resultModel;
		this.newStateModel = newStateModel;
		modelElements = Sets.newHashSet();
		diffFilter = new DefaultDiffFilter();
		this.monitor = monitor;
	}

	@Override
	public Boolean caseDiffModel(DiffModel object) {
		for(DiffElement diffElement : object.getOwnedElements()) {
			if(monitor.isCanceled()) {
				return HANDLED;
			}
			doSwitch(diffElement);
		}
		for(EObject root : object.getLeftRoots()) {
			if(root instanceof Model) {
				List<DependencyNet> dependencyNets = Lists.newArrayList(Iterables.filter(((Model)root).getObjects(), DependencyNet.class)); // to avoid concurrent modification
				for(DependencyNet dnet : dependencyNets) {
					depnet:
					for(Constraint constraint : dnet.getConstraints()) {
						if(modelElements.contains(constraint)) {
							modelElements.add(dnet);
							break depnet; // add depnet only once
						}
					}
				}
			}
		}
		// finalize model
		List<VCObject> objects = resultModel.getObjects();
		List<VCObject> leftObjects = Lists.newArrayList(newStateModel.getObjects());
		for(VCObject o : leftObjects) {
			if(modelElements.contains(o)) {
				objects.add(EcoreUtil.copy(o));
			}
		}
		return HANDLED;
	}

	@Override
	public Boolean caseDiffElement(DiffElement object) {
		for(DiffElement element : object.getSubDiffElements()) {
			if(monitor.isCanceled()) {
				return HANDLED;
			}
			System.err.println("doSwitch " + element + " " + element.getClass().getSimpleName());
			doSwitch(element);
		}
		return NOT_HANDLED;
	}

	@Override
	public Boolean caseModelElementChangeLeftTarget(ModelElementChangeLeftTarget object) {
		return addObject2HandleList(object.getLeftElement());
	}

	@Override
	public Boolean caseModelElementChangeRightTarget(ModelElementChangeRightTarget object) {
		return addObject2HandleList(object.getLeftParent());
	}
	
	@Override
	public Boolean caseReferenceChangeLeftTarget(ReferenceChangeLeftTarget object) {
		return addObject2HandleList(object.getLeftElement());
	}

	@Override
	public Boolean caseReferenceChangeRightTarget(ReferenceChangeRightTarget object) {
		return addObject2HandleList(object.getLeftElement());
	}

	@Override
	public Boolean caseReferenceChange(ReferenceChange object) {
		if(!diffFilter.filter(object.getReference(), object.getKind())) {
			return addObject2HandleList(object.getLeftElement());
		}
		return HANDLED;
	}

	@Override
	public Boolean caseReferenceOrderChange(ReferenceOrderChange object) {
		List<EObject> leftTarget = object.getLeftTarget();
		List<EObject> rightTarget = object.getRightTarget();
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
	public Boolean caseAttributeChangeLeftTarget(AttributeChangeLeftTarget object) {
		return addObject2HandleList(object.getLeftElement());
	}

	@Override
	public Boolean caseAttributeChangeRightTarget(AttributeChangeRightTarget object) {
		return addObject2HandleList(object.getLeftElement());
	}

	@Override
	public Boolean caseUpdateAttribute(UpdateAttribute object) {
		if(!diffFilter.filter(object.getAttribute(), object.getKind())) {
			return addObject2HandleList(object.getLeftElement());
		}
		return NOT_HANDLED;
	}

	private boolean addObject2HandleList(EObject object) {
		if(object instanceof Option) {
			resultModel.getOptions().add((Option)object);
		} else {
			VCObject vcObject = EcoreUtil2.getContainerOfType(object, VCObject.class);
			if(vcObject != null) {
				modelElements.add(vcObject);			}
		}
		return HANDLED;
	}
}
