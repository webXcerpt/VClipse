package org.vclipse.vcml.diff.compare;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
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
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.diagnostics.Severity;
import org.eclipse.xtext.nodemodel.ICompositeNode;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;
import org.eclipse.xtext.validation.CheckType;
import org.eclipse.xtext.validation.Issue.IssueImpl;
import org.eclipse.xtext.validation.ValidationMessageAcceptor;
import org.vclipse.vcml.diff.IVcmlDiffFilter;
import org.vclipse.vcml.vcml.Constraint;
import org.vclipse.vcml.vcml.DependencyNet;
import org.vclipse.vcml.vcml.Model;
import org.vclipse.vcml.vcml.Option;
import org.vclipse.vcml.vcml.VCObject;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
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
	
	private final Map<String, IssueImpl> name2Issue;
	
	private ResourceSet resourceSet;
	
	public static final String COMPARE_ISSUE_CODE = "Compare_Issue";
	
	@Inject
	public DiffModelSwitch(IVcmlDiffFilter vcmlDiffFilter) {
		modelElements = Sets.newHashSet();
		this.diffFilter = vcmlDiffFilter;
		name2Issue = Maps.newHashMap();
	}
	
	public void extractDifferences(DiffModel diffModel, Model resultModel, Model newStateModel, IProgressMonitor monitor) {
		modelElements.clear();
		name2Issue.clear();
		resourceSet = resultModel.eResource().getResourceSet();
		this.resultModel = resultModel;
		this.newStateModel = newStateModel;
		this.monitor = monitor;
		doSwitch(diffModel);
	}
	
	public Map<String, IssueImpl> getIssues() {
		return name2Issue;
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
		for(final VCObject vcobject : leftObjects) {
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
			System.err.println("doSwitch " + element + " " + element.getClass().getSimpleName());
			doSwitch(element);
		}
		return NOT_HANDLED;
	}

	@Override
	public Boolean caseModelElementChangeLeftTarget(ModelElementChangeLeftTarget object) {
		EObject newStateObject = object.getLeftElement();
		EObject newStateContainer = newStateObject.eContainer();
		if(newStateContainer instanceof VCObject) {
			EReference feature = newStateObject.eContainmentFeature();
			EObject oldStateContainer = object.getRightParent();
			EObject oldStateObject = (EObject)oldStateContainer.eGet(feature);
			if(!diffFilter.changeAllowed(newStateObject.eContainer(), oldStateContainer, newStateObject, oldStateObject, object.getKind())) {
				String[] data = new String[]{EcoreUtil.getURI(oldStateContainer).toString(), EcoreUtil.getURI(newStateContainer).toString(), feature.getName()};
				String createMessage = createMessage(oldStateObject, data);
				IssueImpl issue = createIssue(createMessage, COMPARE_ISSUE_CODE, CheckType.FAST, Severity.ERROR);
				setProperties(newStateObject.eContainer(), issue, data);
				memorize(issue, (VCObject)newStateObject.eContainer());
			}
			
		}
		return addObject2HandleList(object.getLeftElement());
	}

	@Override
	public Boolean caseModelElementChangeRightTarget(ModelElementChangeRightTarget object) {
		EObject oldStateObject = object.getRightElement();
		EObject oldStateContainer = oldStateObject.eContainer();
		if(oldStateContainer instanceof VCObject) {
			EObject newStateContainer = object.getLeftParent();
			EReference feature = oldStateObject.eContainmentFeature();
			EObject newStateObject = (EObject)newStateContainer.eGet(feature);
			if(!diffFilter.changeAllowed(newStateObject.eContainer(), oldStateContainer, newStateObject, oldStateObject, object.getKind())) {
				String[] data = new String[]{EcoreUtil.getURI(oldStateContainer).toString(), EcoreUtil.getURI(newStateContainer).toString(), feature.getName()};
				String createMessage = createMessage(oldStateObject, data);
				IssueImpl issue = createIssue(createMessage, COMPARE_ISSUE_CODE, CheckType.FAST, Severity.ERROR);
				setProperties(newStateObject.eContainer(), issue, data);
				memorize(issue, (VCObject)newStateObject.eContainer());
			}
			
		}
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

	@Override
	public Boolean caseUpdateAttribute(UpdateAttribute object) {
		if(!diffFilter.canHandle(object.getAttribute(), object.getKind())) {
			EObject leftElement = object.getLeftElement();
			if(!diffFilter.changeAllowed(leftElement, null, object.getAttribute(), null, object.getKind())) {
//				EAttribute attribute = object.getAttribute();
//				
//				EStructuralFeature eStructuralFeature = leftElement.eClass().getEStructuralFeature(attribute.getName());
//				Object eGet = leftElement.eGet(eStructuralFeature);
//				
//				EStructuralFeature eContainingFeature = attribute.eContainingFeature();
//				
//				
//				error("", object.getLeftElement(), object.getAttribute());
			}
			return addObject2HandleList(object.getLeftElement());
		}
		return NOT_HANDLED;
	}

	private IssueImpl createIssue(String message, String code, CheckType type, Severity severity) {
		IssueImpl issue = new IssueImpl();
		issue.setType(type);
		issue.setSeverity(severity);
		issue.setMessage(message);
		issue.setCode(code);
		return issue;
	}
	
	private String createMessage(EObject object, String ... data) {
		if(data.length > 2) {
			String uriOldStateObject = data[0];
			String uriNewStateObject = data[1];
			String featureName = data[2];
			
			EObject oldStateObject = resourceSet.getEObject(URI.createURI(uriOldStateObject), true);
			EObject newStateObject = resourceSet.getEObject(URI.createURI(uriNewStateObject), true);
			EStructuralFeature feature = newStateObject.eClass().getEStructuralFeature(featureName);
			
			EObject containedOldState = (EObject)oldStateObject.eGet(feature);
			EObject containedNewState = (EObject)newStateObject.eGet(feature);
			
			Object containedObject = newStateObject.eGet(feature);
			if(containedObject instanceof EObject) {
				StringBuffer messageBuffer = new StringBuffer();
				messageBuffer.append("The change of " + featureName + " from ");
				messageBuffer.append("" + containedOldState.eClass().getName());
				messageBuffer.append(" to " + containedNewState.eClass().getName());
				messageBuffer.append(" for a " + oldStateObject.eClass().getName());
				messageBuffer.append(" is not allowed in SAP system.");	
				return messageBuffer.toString();
			}
		}
		return "";
	}
	
	protected EObject getEntry(EObject object, EStructuralFeature feature, int index) {
		EObject entry = object;
		if(feature != null) {
			Object childObject = object.eGet(feature);
			if(childObject instanceof EObject) {
				entry = (EObject)childObject;
			} else if(childObject instanceof EList<?>) {
				EList<?> listFeature = (EList<?>)childObject;
				if(index > ValidationMessageAcceptor.INSIGNIFICANT_INDEX && index < listFeature.size()) { 
					Object objectFromTheList = listFeature.get(index);
					if(objectFromTheList instanceof EObject) {
						entry = ((EObject)objectFromTheList);
					}
				}
			}
		}
		return entry;
	}
	
	protected void memorize(IssueImpl issue, VCObject object) {
		name2Issue.put(object.getName(), issue);						
	}
	
//	protected void error(String message, EObject object, EAttribute attribute) {
//		ICompositeNode node = NodeModelUtils.getNode(object);
//		if(node != null) {
//			Object attributeValue = object.eGet(attribute);
//			for(INode textNode : node.getChildren()) {
//				if(attributeValue instanceof Integer) {
//					String text = textNode.getText();
//					if(text.equals(((Integer)attributeValue).toString())) {
//						//messageAcceptor.acceptError("", object, textNode.getOffset(), 
//						//		textNode.getLength(), "Compare_Issue", new String[]{EcoreUtil.getURI(object).toString()});
//					}
//				}
//			}
//		}
//	}
	
	protected void setProperties(EObject entry, IssueImpl issue, String... issueData) {
		List<String> stringData = Lists.newArrayList(issueData == null ? new String[0] : issueData);
		ICompositeNode node = NodeModelUtils.getNode(entry);
		if(node != null) {
			issue.setLength(node.getTotalLength());
			issue.setLineNumber(node.getStartLine());
			issue.setOffset(node.getOffset());
			issue.setUriToProblem(EcoreUtil.getURI(entry));
		}
		issue.setData(stringData.toArray(new String[stringData.size()]));
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
