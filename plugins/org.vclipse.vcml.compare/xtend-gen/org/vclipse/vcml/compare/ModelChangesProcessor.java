/**
 * Copyright (c) 2010 - 2013 webXcerpt Software GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     	webXcerpt Software GmbH - initial creator
 * 		www.webxcerpt.com
 */
package org.vclipse.vcml.compare;

import com.google.common.base.Objects;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.inject.Singleton;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.xtext.diagnostics.Severity;
import org.eclipse.xtext.naming.QualifiedName;
import org.eclipse.xtext.nodemodel.ICompositeNode;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;
import org.eclipse.xtext.validation.CheckType;
import org.eclipse.xtext.validation.Issue.IssueImpl;
import org.eclipse.xtext.validation.ValidationMessageAcceptor;
import org.vclipse.vcml.compare.ResourceChangesProcessor;

/**
 * Implements the handling of reference changes and issue creation if a change does not allowed.
 */
@Singleton
@SuppressWarnings("all")
public class ModelChangesProcessor extends ResourceChangesProcessor {
  public static String VCML_EXTENSION = ".vcml";
  
  public static String DIFF_VCML_EXTENSION = "_diff.vcml";
  
  public static String COMPARE_ISSUE_CODE = "Compare_Issue_Code";
  
  private Multimap<String,IssueImpl> name2Issue;
  
  /**
   * Initialisation -> should be done before the handling on the changes is executed.
   */
  public void initialize(final /* VcmlModel */Object vcmlModel) {
    super.initialize(vcmlModel);
    HashMultimap<String,IssueImpl> _create = HashMultimap.<String, IssueImpl>create();
    this.name2Issue = _create;
  }
  
  /**
   * Returns the collected issues.
   */
  public Multimap<String,IssueImpl> getCollectedIssues() {
    return this.name2Issue;
  }
  
  /**
   * Reaction on an attribute change in a VCObject
   */
  public VCObject attributeChange(final /* Match */Object match, final EAttribute attribute, final Object value, final /* DifferenceKind */Object kind, final /* DifferenceSource */Object source) {
    throw new Error("Unresolved compilation problems:"
      + "\nVCObject cannot be resolved to a type."
      + "\nDifferenceSource cannot be resolved to a type."
      + "\nVCObject cannot be resolved to a type."
      + "\nleft cannot be resolved"
      + "\nLEFT cannot be resolved"
      + "\n== cannot be resolved");
  }
  
  /**
   * Rection on a reference change in the vcml model.
   */
  public void referenceChange(final /* Match */Object match, final EReference reference, final EObject value, final /* DifferenceKind */Object kind, final /* DifferenceSource */Object source) {
    throw new Error("Unresolved compilation problems:"
      + "\nVcmlModel cannot be resolved to a type."
      + "\nOptionType cannot be resolved to a type."
      + "\nThe method name is undefined for the type ModelChangesProcessor"
      + "\nThe method eClass is undefined for the type ModelChangesProcessor"
      + "\nThe method name is undefined for the type ModelChangesProcessor"
      + "\nDifferenceKind cannot be resolved to a type."
      + "\nDifferenceSource cannot be resolved to a type."
      + "\nVcmlModel cannot be resolved to a type."
      + "\nleft cannot be resolved"
      + "\nright cannot be resolved"
      + "\nvcmlModel_Options cannot be resolved"
      + "\n== cannot be resolved"
      + "\noptions cannot be resolved"
      + "\nUPS cannot be resolved"
      + "\n== cannot be resolved"
      + "\n&& cannot be resolved"
      + "\ninstanceClassName cannot be resolved"
      + "\n+ cannot be resolved"
      + "\nDELETE cannot be resolved"
      + "\n== cannot be resolved"
      + "\n&& cannot be resolved"
      + "\nLEFT cannot be resolved"
      + "\n== cannot be resolved"
      + "\n&& cannot be resolved"
      + "\ncharacteristic_Type cannot be resolved"
      + "\n== cannot be resolved"
      + "\nleft cannot be resolved"
      + "\nright cannot be resolved");
  }
  
  /**
   * Reaction on add operation between 2 models.
   */
  public void reference(final EObject left, final EObject right, final EObject value, final EReference reference, final /* DifferenceKind */Object kind, final /* DifferenceSource */Object source) {
    throw new Error("Unresolved compilation problems:"
      + "\nVCObject cannot be resolved to a type."
      + "\nVCObject cannot be resolved to a type."
      + "\nVCObject cannot be resolved to a type."
      + "\nDifferenceKind cannot be resolved to a type."
      + "\nDifferenceSource cannot be resolved to a type."
      + "\nVcmlModel cannot be resolved to a type."
      + "\nVCObject cannot be resolved to a type."
      + "\nVCObject cannot be resolved to a type."
      + "\nVCObject cannot be resolved to a type."
      + "\nADD cannot be resolved"
      + "\n== cannot be resolved"
      + "\n&& cannot be resolved"
      + "\nLEFT cannot be resolved"
      + "\n== cannot be resolved"
      + "\nvcmlModel_Objects cannot be resolved"
      + "\n== cannot be resolved");
  }
  
  /**
   * adds a vc object to the objects lists
   */
  protected VCObject addVCObject(final /* VCObject */Object vcobject) {
    VCObject _xblockexpression = null;
    {
      VCObject theObject = vcobject;
      final VCObject _converted_theObject = (VCObject)theObject;
      QualifiedName _fullyQualifiedName = this.nameProvider.getFullyQualifiedName(_converted_theObject);
      final String name = _fullyQualifiedName.toString();
      boolean _contains = this.seenObjects.contains(name);
      boolean _not = (!_contains);
      if (_not) {
        VCObject _copy = EcoreUtil.<VCObject>copy(theObject);
        theObject = _copy;
        this.newObjects.add(theObject);
        this.seenObjects.add(name);
      }
      _xblockexpression = (theObject);
    }
    return _xblockexpression;
  }
  
  /**
   * Validation of the change, the errors are reported as issues in the xtext editor
   */
  protected VCObject validate(final EObject left, final EObject right, final EObject value, final EReference reference, final /* DifferenceKind */Object kind, final /* DifferenceSource */Object source) {
    throw new Error("Unresolved compilation problems:"
      + "\nCharacteristic cannot be resolved to a type."
      + "\nCharacteristic cannot be resolved to a type."
      + "\nCharacteristic cannot be resolved to a type."
      + "\nCharacteristic cannot be resolved to a type."
      + "\nCharacteristic cannot be resolved to a type."
      + "\ncharacteristic_Type cannot be resolved"
      + "\n== cannot be resolved"
      + "\ntype cannot be resolved"
      + "\ntype cannot be resolved"
      + "\n!= cannot be resolved"
      + "\n&& cannot be resolved"
      + "\n!= cannot be resolved"
      + "\n&& cannot be resolved"
      + "\neClass cannot be resolved"
      + "\n!= cannot be resolved"
      + "\neClass cannot be resolved"
      + "\nname cannot be resolved");
  }
  
  /**
   * Creates a new cstic type issue - one with properties set a.s.o.
   */
  protected IssueImpl createCharacteristicTypeIssue(final EObject newContainer, final EObject oldContainer, final EObject newObject) {
    IssueImpl _xblockexpression = null;
    {
      final EReference containment = newObject.eContainmentFeature();
      int _featureID = containment.getFeatureID();
      final String message = this.createTypeChangeErrorMessage(oldContainer, newContainer, _featureID, ValidationMessageAcceptor.INSIGNIFICANT_INDEX);
      final IssueImpl issue = this.newIssue(message, ModelChangesProcessor.COMPARE_ISSUE_CODE, CheckType.FAST, Severity.ERROR);
      URI _uRI = EcoreUtil.getURI(newContainer);
      final String newContainerUri = _uRI.toString();
      URI _uRI_1 = EcoreUtil.getURI(oldContainer);
      final String oldContainerUri = _uRI_1.toString();
      String _name = containment.getName();
      this.setProperties(newObject, issue, newContainerUri, oldContainerUri, _name);
      _xblockexpression = (issue);
    }
    return _xblockexpression;
  }
  
  /**
   * Creates a new issue instance.
   */
  protected IssueImpl newIssue(final String message, final String code, final CheckType type, final Severity severity) {
    IssueImpl _xblockexpression = null;
    {
      IssueImpl _issueImpl = new IssueImpl();
      final IssueImpl issue = _issueImpl;
      issue.setType(type);
      issue.setSeverity(severity);
      issue.setMessage(message);
      issue.setCode(code);
      _xblockexpression = (issue);
    }
    return _xblockexpression;
  }
  
  /**
   * Creates a type chage error message.
   */
  protected String createTypeChangeErrorMessage(final EObject newState, final EObject oldState, final int featureID, final int index) {
    String _xblockexpression = null;
    {
      EClass _eClass = newState.eClass();
      final EStructuralFeature feature = _eClass.getEStructuralFeature(featureID);
      Object _eGet = newState.eGet(feature);
      EObject entryNewState = ((EObject) _eGet);
      StringBuffer _stringBuffer = new StringBuffer();
      final StringBuffer messageBuffer = _stringBuffer;
      messageBuffer.append("The change between ");
      EClass _eClass_1 = entryNewState.eClass();
      String _name = _eClass_1.getName();
      messageBuffer.append(_name);
      boolean _notEquals = (!Objects.equal(oldState, null));
      if (_notEquals) {
        messageBuffer.append(" and ");
        Object _eGet_1 = oldState.eGet(feature);
        EObject entryOldState = ((EObject) _eGet_1);
        EClass _eClass_2 = entryOldState.eClass();
        String _name_1 = _eClass_2.getName();
        messageBuffer.append(_name_1);
      }
      messageBuffer.append(" for a ");
      EClass _eClass_3 = newState.eClass();
      String _name_2 = _eClass_3.getName();
      messageBuffer.append(_name_2);
      messageBuffer.append(" is not allowed in SAP system.");
      String _string = messageBuffer.toString();
      _xblockexpression = (_string);
    }
    return _xblockexpression;
  }
  
  /**
   * Sets the properties of an issue that are valueable for the xtext editor
   */
  protected void setProperties(final EObject newState, final IssueImpl issue, final String... issueData) {
    final ICompositeNode node = NodeModelUtils.getNode(newState);
    boolean _notEquals = (!Objects.equal(node, null));
    if (_notEquals) {
      int _length = node.getLength();
      issue.setLength(Integer.valueOf(_length));
      int _startLine = node.getStartLine();
      issue.setLineNumber(Integer.valueOf(_startLine));
      int _offset = node.getOffset();
      issue.setOffset(Integer.valueOf(_offset));
      URI _uRI = EcoreUtil.getURI(newState);
      String uriAsString = _uRI.toString();
      String _replace = uriAsString.replace(ModelChangesProcessor.VCML_EXTENSION, ModelChangesProcessor.DIFF_VCML_EXTENSION);
      uriAsString = _replace;
      URI _createURI = URI.createURI(uriAsString);
      issue.setUriToProblem(_createURI);
    }
    issue.setData(issueData);
  }
}
