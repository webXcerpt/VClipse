package org.vclipse.vcml.diff.compare;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.compare.diff.metamodel.DiffElement;
import org.eclipse.emf.compare.diff.metamodel.DiffModel;
import org.eclipse.emf.compare.diff.metamodel.ModelElementChangeLeftTarget;
import org.eclipse.emf.compare.diff.metamodel.ModelElementChangeRightTarget;
import org.eclipse.emf.compare.diff.metamodel.UpdateAttribute;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.xtext.diagnostics.Severity;
import org.eclipse.xtext.nodemodel.ICompositeNode;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;
import org.eclipse.xtext.util.PolymorphicDispatcher;
import org.eclipse.xtext.validation.CheckType;
import org.eclipse.xtext.validation.Issue.IssueImpl;
import org.eclipse.xtext.validation.ValidationMessageAcceptor;
import org.vclipse.vcml.vcml.Characteristic;
import org.vclipse.vcml.vcml.CharacteristicType;
import org.vclipse.vcml.vcml.Constraint;
import org.vclipse.vcml.vcml.DependencyNet;
import org.vclipse.vcml.vcml.VCObject;
import org.vclipse.vcml.vcml.VcmlModel;
import org.vclipse.vcml.vcml.VcmlPackage;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;

public class DiffValidation {

	public static final String COMPARE_ISSUE_CODE = "Compare_Issue";
	
	private final PolymorphicDispatcher<Multimap<String, IssueImpl>> validationDispatcher = 
			new PolymorphicDispatcher<Multimap<String, IssueImpl>>("validate", 1, 1, 
					Collections.singletonList(this), PolymorphicDispatcher.NullErrorHandler.<Multimap<String, IssueImpl>> get()) {
		@Override
		protected Multimap<String, IssueImpl> handleNoSuchMethod(final Object... params) {
			return null;
		}
	};
	
	public Multimap<String, IssueImpl> validate(DiffElement diffElement) {
		return validationDispatcher.invoke(diffElement);
	}
	
	protected Multimap<String, IssueImpl> validate(DiffModel diffModel) {
		Multimap<String, IssueImpl> issues = HashMultimap.create();
		Map<String, String> constraintNameDependencyNetName = Maps.newHashMap();
		Map<String, DependencyNet> constraintDependencyNet = Maps.newHashMap();
		
		for(EObject root : diffModel.getRightRoots()) {
			if(root instanceof VcmlModel) {
				for(DependencyNet dnet : Lists.newArrayList(Iterables.filter(((VcmlModel)root).getObjects(), DependencyNet.class))) {
					for(Constraint constraint : dnet.getConstraints()) {
						constraintNameDependencyNetName.put(constraint.getName(), dnet.getName());
						constraintDependencyNet.put(constraint.getName(), dnet);
					}
				}
			} 
		}
		for(EObject root : diffModel.getLeftRoots()) {
			if(root instanceof VcmlModel) {
				for(DependencyNet dependencyNet : Lists.newArrayList(Iterables.filter(((VcmlModel)root).getObjects(), DependencyNet.class))) {
					for(Constraint constraint : dependencyNet.getConstraints()) {
						String name = constraint.getName();
						DependencyNet oldDepedencyNet = constraintDependencyNet.get(constraint.getName());
						String dependencyNetName = constraintNameDependencyNetName.get(name);
						if(!dependencyNet.getName().equals(dependencyNetName)) {
							int objectIndex = dependencyNet.getConstraints().indexOf(constraint);
							int featureid = VcmlPackage.eINSTANCE.getDependencyNet_Constraints().getFeatureID();
							String message = createMessage(dependencyNet, oldDepedencyNet, featureid, objectIndex);
							IssueImpl issue = createIssue(message, COMPARE_ISSUE_CODE, CheckType.FAST, Severity.ERROR);
							if(oldDepedencyNet == null) {
								return issues;
							}
							issue.setData(new String[]{
									oldDepedencyNet == null ? EcoreUtil.getURI(dependencyNet).toString() : 
										EcoreUtil.getURI(oldDepedencyNet).toString(), 
											EcoreUtil.getURI(dependencyNet).toString(),
												VcmlPackage.eINSTANCE.getDependencyNet_Constraints().getName(), 
													String.valueOf(dependencyNet.getConstraints().indexOf(constraint))});
							setProperties(dependencyNet, oldDepedencyNet, issue, issue.getData());
							issues.put(dependencyNet.getName(), issue);
						}
					}
				}
			}
		}
		return issues;
	}
	
	protected Multimap<String, IssueImpl> validate(ModelElementChangeRightTarget rightTarget) {
		EObject oldStateObject = rightTarget.getRightElement();
		EObject oldStateContainer = oldStateObject.eContainer();
		EObject newStateContainer = rightTarget.getLeftParent();
		EReference feature = oldStateObject.eContainmentFeature();
		Object featureValue = newStateContainer.eGet(feature);
		if(featureValue instanceof EObject) {
			EObject newStateObject = (EObject)featureValue;
			if(newStateContainer instanceof Characteristic && oldStateContainer instanceof Characteristic) {
				if(newStateObject instanceof CharacteristicType && oldStateObject instanceof CharacteristicType) {
					Multimap<String, IssueImpl> map = HashMultimap.create();
					map.put(((Characteristic)newStateContainer).getName(), 
							createCharacteristicTypeIssue(newStateContainer, oldStateContainer, (EObject)newStateContainer.eGet(feature), oldStateObject, feature));
					return map;
				}
			}
		}
		return HashMultimap.create();
	}
	
	protected Multimap<String, IssueImpl> validate(ModelElementChangeLeftTarget leftTarget) {
		EObject newStateObject = leftTarget.getLeftElement();
		EObject newStateContainer = newStateObject.eContainer();
		EReference feature = newStateObject.eContainmentFeature();
		EObject oldStateContainer = leftTarget.getRightParent();
		Object featureValue = oldStateContainer.eGet(feature);
		if(featureValue instanceof EObject) {
			EObject oldStateObject = (EObject)featureValue;
			if(newStateContainer instanceof Characteristic && oldStateContainer instanceof Characteristic) {
				if(newStateObject instanceof CharacteristicType && oldStateObject instanceof CharacteristicType) {
					Multimap<String, IssueImpl> map = HashMultimap.create();
					map.put(((Characteristic)newStateContainer).getName(), 
							createCharacteristicTypeIssue(newStateContainer, oldStateContainer, (EObject)newStateContainer.eGet(feature), oldStateObject, feature));
					return map;
				}
			}
		}
		return HashMultimap.create();
	}
	
	protected Multimap<String, IssueImpl> validate(UpdateAttribute updateAttribute) {
		EObject newStateObject = updateAttribute.getLeftElement();
		EObject newStateContainer = newStateObject.eContainer();
		EObject oldStateObject = updateAttribute.getRightElement();
		EAttribute attribute = updateAttribute.getAttribute();
		if(newStateObject instanceof CharacteristicType && oldStateObject instanceof CharacteristicType && 
				VcmlPackage.CHARACTERISTIC_TYPE__NUMBER_OF_CHARS == attribute.getFeatureID()) {
			String message = createMessage(oldStateObject, newStateObject, attribute.getFeatureID(), ValidationMessageAcceptor.INSIGNIFICANT_INDEX);
			IssueImpl issue = createIssue(message, COMPARE_ISSUE_CODE, CheckType.FAST, Severity.ERROR);
			issue.setData(new String[]{EcoreUtil.getURI(newStateContainer).toString(), EcoreUtil.getURI(oldStateObject.eContainer()).toString(), 
					attribute.getName(), EcoreUtil.getURI(newStateObject).toString()});
			setProperties(newStateContainer, oldStateObject, issue, issue.getData());
			Multimap<String, IssueImpl> map = HashMultimap.create();
			map.put(((Characteristic)newStateContainer).getName(), issue);
			return map;
		}
		return HashMultimap.create();
	}
	
	protected IssueImpl createCharacteristicTypeIssue(EObject newStateContainer, EObject oldStateContainer, EObject newStateObject, EObject oldStateObject, EReference reference) {
		String message = createMessage(oldStateContainer, newStateContainer, reference.getFeatureID(), ValidationMessageAcceptor.INSIGNIFICANT_INDEX);
		IssueImpl issue = createIssue(message, COMPARE_ISSUE_CODE, CheckType.FAST, Severity.ERROR);
		issue.setData(new String[]{EcoreUtil.getURI(newStateContainer).toString(), EcoreUtil.getURI(oldStateContainer).toString(), reference.getName()});
		setProperties(newStateObject.eContainer(), oldStateObject, issue, issue.getData());
		return issue;
	}
	
	protected IssueImpl createIssue(String message, String code, CheckType type, Severity severity) {
		IssueImpl issue = new IssueImpl();
		issue.setType(type);
		issue.setSeverity(severity);
		issue.setMessage(message);
		issue.setCode(code);
		return issue;
	}
	
	private String createMessage(EObject newStateObject, EObject oldStateObject, int featureID, int index) {
		EStructuralFeature feature = newStateObject.eClass().getEStructuralFeature(featureID);
		Object object = newStateObject.eGet(feature);
		Object entry = object instanceof EList<?> ? ((EList<?>)object).get(index) : object;
		StringBuffer messageBuffer = new StringBuffer();
		String name = entry instanceof VCObject ? ((VCObject)entry).getName() : "";
		messageBuffer.append("The change between " + newStateObject.eClass().getName());
		messageBuffer.append(oldStateObject == null ? "" : " and " + oldStateObject.eClass().getName());
		messageBuffer.append(" for the " + entry.getClass().getSimpleName() + " entry " + (name.isEmpty() ? "" : " with " + name));
		messageBuffer.append(" is not allowed in SAP system.");	
		return messageBuffer.toString();
	}
	
	protected void setProperties(EObject newStateObject, EObject oldStateObject, IssueImpl issue, String... issueData) {
		List<String> stringData = Lists.newArrayList(issueData == null ? new String[0] : issueData);
		ICompositeNode node = NodeModelUtils.getNode(newStateObject);
		if(node != null) {
			issue.setLength(node.getTotalLength());
			issue.setLineNumber(node.getStartLine());
			issue.setOffset(node.getOffset());
			issue.setUriToProblem(EcoreUtil.getURI(newStateObject));
		}
		issue.setData(stringData.toArray(new String[stringData.size()]));
	}
}
