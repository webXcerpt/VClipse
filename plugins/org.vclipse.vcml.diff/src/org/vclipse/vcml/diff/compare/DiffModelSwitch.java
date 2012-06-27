package org.vclipse.vcml.diff.compare;

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
import org.eclipse.xtext.validation.Issue.IssueImpl;
import org.vclipse.vcml.diff.IVcmlDiffFilter;
import org.vclipse.vcml.vcml.Constraint;
import org.vclipse.vcml.vcml.DependencyNet;
import org.vclipse.vcml.vcml.Model;
import org.vclipse.vcml.vcml.Option;
import org.vclipse.vcml.vcml.VCObject;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.google.inject.Inject;

public class DiffModelSwitch extends DiffSwitch<Boolean> {

	private Boolean HANDLED = Boolean.TRUE;
	private Boolean NOT_HANDLED = null;

	private Model resultModel;
	private Model newStateModel;
	private Set<VCObject> modelElements;
	private IProgressMonitor monitor;

	private IVcmlDiffFilter diffFilter;
	private DiffValidation diffValidation;
	
	private final Multimap<String, IssueImpl> name2Issue;
	
	@Inject
	public DiffModelSwitch(IVcmlDiffFilter vcmlDiffFilter, DiffValidation diffValidation) {
		modelElements = Sets.newHashSet();
		name2Issue = HashMultimap.create();
		this.diffFilter = vcmlDiffFilter;
		this.diffValidation = diffValidation;
	}
	
	public void extractDifferences(DiffModel diffModel, Model resultModel, Model newStateModel, IProgressMonitor monitor) {
		modelElements.clear();
		name2Issue.clear();
		this.resultModel = resultModel;
		this.newStateModel = newStateModel;
		this.monitor = monitor;
		doSwitch(diffModel);
	}
	
	public Multimap<String, IssueImpl> getIssues() {
		return name2Issue;
	}
	
	@Override
	public Boolean caseDiffModel(DiffModel diffModel) {
		for(DiffElement diffElement : diffModel.getOwnedElements()) {
			if(monitor.isCanceled()) {
				return HANDLED;
			}
			doSwitch(diffElement);
		}
		
		for(EObject root : diffModel.getLeftRoots()) {
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
		
		name2Issue.putAll(diffValidation.validate(diffModel));
		
		// finalize model
		List<VCObject> objects = resultModel.getObjects();
		for(VCObject vcobject : Lists.newArrayList(newStateModel.getObjects())) {
			if(modelElements.contains(vcobject)) {
				objects.add(EcoreUtil.copy(vcobject));
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
			//System.err.println("doSwitch " + element + " " + element.getClass().getSimpleName());
			doSwitch(element);
		}
		return NOT_HANDLED;
	}
	
	@Override
	public Boolean caseModelElementChangeLeftTarget(ModelElementChangeLeftTarget leftTarget) {
		EObject newStateObject = leftTarget.getLeftElement();
		if(newStateObject.eContainer() instanceof VCObject) {
			name2Issue.putAll(diffValidation.validate(leftTarget));		
		}
		return addObject2HandleList(newStateObject);
	}
	
	@Override
	public Boolean caseModelElementChangeRightTarget(ModelElementChangeRightTarget rightTarget) {
		if(rightTarget.getRightElement().eContainer() instanceof VCObject) {
			name2Issue.putAll(diffValidation.validate(rightTarget));		
		}
		return addObject2HandleList(rightTarget.getLeftParent());
	}
	
	@Override
	public Boolean caseUpdateAttribute(UpdateAttribute updateAttribute) {
		if(!diffFilter.canHandle(updateAttribute.getAttribute(), updateAttribute.getKind())) {
			name2Issue.putAll(diffValidation.validate(updateAttribute));
			return addObject2HandleList(updateAttribute.getLeftElement());
		}
		return NOT_HANDLED;
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
		return diffFilter.canHandle(object.getReference(), object.getKind()) ? HANDLED : addObject2HandleList(object.getLeftElement());
	}
	
	@Override
	public Boolean caseReferenceOrderChange(ReferenceOrderChange object) {
		if(!diffFilter.canHandle(object.getReference(), object.getKind())) {
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
	
	private boolean addObject2HandleList(EObject object) {
		if(object instanceof Option) {
			resultModel.getOptions().add((Option)object);
		} else {
			VCObject vcObject = EcoreUtil2.getContainerOfType(object, VCObject.class);
			if(vcObject != null) {
				modelElements.add(vcObject);			
			}
		}
		return HANDLED;
	}
}
