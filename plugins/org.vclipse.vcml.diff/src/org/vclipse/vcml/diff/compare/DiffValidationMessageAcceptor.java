package org.vclipse.vcml.diff.compare;

import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.xtext.diagnostics.Severity;
import org.eclipse.xtext.naming.IQualifiedNameProvider;
import org.eclipse.xtext.naming.QualifiedName;
import org.eclipse.xtext.nodemodel.ICompositeNode;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;
import org.eclipse.xtext.validation.CheckType;
import org.eclipse.xtext.validation.Issue.IssueImpl;
import org.eclipse.xtext.validation.ValidationMessageAcceptor;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.inject.Inject;

public class DiffValidationMessageAcceptor implements ValidationMessageAcceptor {

	private final Map<String, IssueImpl> issues;
	
	private final IQualifiedNameProvider nameProvider;
	
	@Inject
	public DiffValidationMessageAcceptor(IQualifiedNameProvider nameProvider) {
		issues = Maps.newHashMap();
		this.nameProvider = nameProvider;
	}

	public IssueImpl getIssue(EObject object) {
		QualifiedName qualifiedName = nameProvider.apply(object);
		if(qualifiedName != null) {
			return issues.get(qualifiedName.getLastSegment());				
		}
		return null;
	}
	
	public boolean hasIssues() {
		return !issues.isEmpty();
	}
	
	@Override
	public void acceptError(String message, EObject object, EStructuralFeature feature, int index, String code, String... issueData) {
		IssueImpl issue = getIssue(message, code, CheckType.NORMAL, Severity.ERROR);
		setProperties(feature, issue, getEntry(object, feature, index), issueData);
		memorize(issue, object);
	}

	@Override
	public void acceptError(String message, EObject object, int offset, int length, String code, String... issueData) {
		
	}

	@Override
	public void acceptWarning(String message, EObject object, EStructuralFeature feature, int index, String code, String... issueData) {
//		IssueImpl issue = getIssue(message, code, CheckType.NORMAL, Severity.WARNING);
//		EObject entry = getEntry(object, feature, index);
	}

	@Override
	public void acceptWarning(String message, EObject object, int offset, int length, String code, String... issueData) {
//		IssueImpl issue = getIssue(message, code, CheckType.NORMAL, Severity.WARNING);
//		EObject entry = getEntry(object, null, ValidationMessageAcceptor.INSIGNIFICANT_INDEX);
	}

	@Override
	public void acceptInfo(String message, EObject object, EStructuralFeature feature, int index, String code, String... issueData) {
//		IssueImpl issue = getIssue(message, code, CheckType.NORMAL, Severity.INFO);
//		EObject entry = getEntry(object, null, index);
	}

	@Override
	public void acceptInfo(String message, EObject object, int offset, int length, String code, String... issueData) {
//		IssueImpl issue = getIssue(message, code, CheckType.NORMAL, Severity.INFO);
//		EObject entry = getEntry(object, null, ValidationMessageAcceptor.INSIGNIFICANT_INDEX);
	}
	
	protected IssueImpl getIssue(String message, String code, CheckType type, Severity severity) {
		IssueImpl issue = new IssueImpl();
		issue.setType(type);
		issue.setSeverity(severity);
		issue.setMessage(message);
		issue.setCode(code);
		return issue;
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

	protected void setProperties(EStructuralFeature feature, IssueImpl issue, EObject entry, String... issueData) {
		List<String> stringData = Lists.newArrayList(issueData == null ? new String[0] : issueData);
		ICompositeNode node = NodeModelUtils.getNode(entry);
		if(node != null) {
			issue.setLength(node.getTotalLength());
			issue.setLineNumber(node.getStartLine());
			issue.setOffset(node.getOffset());
			issue.setUriToProblem(EcoreUtil.getURI(entry));
			if(feature != null) {
				stringData.add(feature.getName());				
			}
			issue.setData(stringData.toArray(new String[stringData.size()]));
		}
		issue.setData(stringData.toArray(new String[stringData.size()]));
	}

	protected void memorize(IssueImpl issue, EObject object) {
		QualifiedName qualifiedName = nameProvider.apply(object);
		if(qualifiedName != null) {
			issues.put(qualifiedName.getLastSegment(), issue);			
		}
	}
}
