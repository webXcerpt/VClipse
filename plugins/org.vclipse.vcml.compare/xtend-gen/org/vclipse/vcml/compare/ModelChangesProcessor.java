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
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.compare.DifferenceKind;
import org.eclipse.emf.compare.DifferenceSource;
import org.eclipse.emf.compare.Match;
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
import org.vclipse.vcml.vcml.Characteristic;
import org.vclipse.vcml.vcml.CharacteristicType;
import org.vclipse.vcml.vcml.Option;
import org.vclipse.vcml.vcml.OptionType;
import org.vclipse.vcml.vcml.VCObject;
import org.vclipse.vcml.vcml.VcmlModel;

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
  public void initialize(final VcmlModel vcmlModel) {
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
  public void attributeChange(final Match match, final EAttribute attribute, final Object value, final DifferenceKind kind, final DifferenceSource source) {
    final EObject left = match.getLeft();
    boolean _equals = Objects.equal(DifferenceSource.LEFT, source);
    if (_equals) {
      if ((left instanceof VCObject)) {
        this.addVCObject(((VCObject) left));
      }
    }
  }
  
  /**
   * Rection on a reference change in the vcml model.
   */
  public void referenceChange(final Match match, final EReference reference, final EObject value, final DifferenceKind kind, final DifferenceSource source) {
    final EObject left = match.getLeft();
    final EObject right = match.getRight();
    boolean _and = false;
    if (!(left instanceof VcmlModel)) {
      _and = false;
    } else {
      EReference _vcmlModel_Options = this.vcmlPackage.getVcmlModel_Options();
      boolean _equals = Objects.equal(_vcmlModel_Options, reference);
      _and = ((left instanceof VcmlModel) && _equals);
    }
    if (_and) {
      EList<Option> _options = ((VcmlModel) left).getOptions();
      for (final Option option : _options) {
        boolean _and_1 = false;
        OptionType _name = option.getName();
        boolean _equals_1 = Objects.equal(OptionType.UPS, _name);
        if (!_equals_1) {
          _and_1 = false;
        } else {
          EClass _eClass = option.eClass();
          String _instanceClassName = _eClass.getInstanceClassName();
          OptionType _name_1 = option.getName();
          String _plus = (_instanceClassName + _name_1);
          boolean _contains = this.seenObjects.contains(_plus);
          boolean _not = (!_contains);
          _and_1 = (_equals_1 && _not);
        }
        if (_and_1) {
          Option _copy = EcoreUtil.<Option>copy(option);
          this.newOptions.add(_copy);
          EClass _eClass_1 = option.eClass();
          String _instanceClassName_1 = _eClass_1.getInstanceClassName();
          this.seenObjects.add(_instanceClassName_1);
        }
      }
      return;
    }
    boolean _and_2 = false;
    boolean _and_3 = false;
    boolean _equals_2 = Objects.equal(DifferenceKind.DELETE, kind);
    if (!_equals_2) {
      _and_3 = false;
    } else {
      boolean _equals_3 = Objects.equal(DifferenceSource.LEFT, source);
      _and_3 = (_equals_2 && _equals_3);
    }
    if (!_and_3) {
      _and_2 = false;
    } else {
      EReference _characteristic_Type = this.vcmlPackage.getCharacteristic_Type();
      boolean _equals_4 = Objects.equal(_characteristic_Type, reference);
      _and_2 = (_and_3 && _equals_4);
    }
    if (_and_2) {
      EObject _left = match.getLeft();
      EObject _right = match.getRight();
      this.validate(_left, _right, value, reference, kind, source);
      return;
    }
    this.reference(left, right, value, reference, kind, source);
  }
  
  /**
   * Reaction on add operation between 2 models.
   */
  public void reference(final EObject left, final EObject right, final EObject value, final EReference reference, final DifferenceKind kind, final DifferenceSource source) {
    boolean _and = false;
    boolean _equals = Objects.equal(DifferenceKind.ADD, kind);
    if (!_equals) {
      _and = false;
    } else {
      boolean _equals_1 = Objects.equal(DifferenceSource.LEFT, source);
      _and = (_equals && _equals_1);
    }
    if (_and) {
      this.validate(left, right, value, reference, kind, source);
      EReference _vcmlModel_Objects = this.vcmlPackage.getVcmlModel_Objects();
      boolean _equals_2 = Objects.equal(_vcmlModel_Objects, reference);
      if (_equals_2) {
        boolean _and_1 = false;
        if (!(left instanceof VcmlModel)) {
          _and_1 = false;
        } else {
          _and_1 = ((left instanceof VcmlModel) && (value instanceof VCObject));
        }
        if (_and_1) {
          this.addVCObject(((VCObject) value));
          return;
        }
      }
      if ((left instanceof VCObject)) {
        if ((right instanceof VCObject)) {
          boolean _equals_3 = EcoreUtil.equals(left, right);
          boolean _not = (!_equals_3);
          if (_not) {
            this.addVCObject(((VCObject) left));
          }
        } else {
          this.addVCObject(((VCObject) left));
        }
        return;
      }
    }
  }
  
  /**
   * adds a vc object to the objects lists
   */
  protected VCObject addVCObject(final VCObject vcobject) {
    VCObject _xblockexpression = null;
    {
      VCObject theObject = vcobject;
      QualifiedName _fullyQualifiedName = this.nameProvider.getFullyQualifiedName(theObject);
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
  protected VCObject validate(final EObject left, final EObject right, final EObject value, final EReference reference, final DifferenceKind kind, final DifferenceSource source) {
    VCObject _xifexpression = null;
    boolean _and = false;
    if (!(left instanceof Characteristic)) {
      _and = false;
    } else {
      _and = ((left instanceof Characteristic) && (right instanceof Characteristic));
    }
    if (_and) {
      VCObject _xifexpression_1 = null;
      EReference _characteristic_Type = this.vcmlPackage.getCharacteristic_Type();
      boolean _equals = Objects.equal(_characteristic_Type, reference);
      if (_equals) {
        VCObject _xblockexpression = null;
        {
          final CharacteristicType leftType = ((Characteristic) left).getType();
          final CharacteristicType rightType = ((Characteristic) right).getType();
          VCObject _xifexpression_2 = null;
          boolean _and_1 = false;
          boolean _and_2 = false;
          boolean _notEquals = (!Objects.equal(leftType, null));
          if (!_notEquals) {
            _and_2 = false;
          } else {
            boolean _notEquals_1 = (!Objects.equal(rightType, null));
            _and_2 = (_notEquals && _notEquals_1);
          }
          if (!_and_2) {
            _and_1 = false;
          } else {
            EClass _eClass = leftType.eClass();
            EClass _eClass_1 = rightType.eClass();
            boolean _notEquals_2 = (!Objects.equal(_eClass, _eClass_1));
            _and_1 = (_and_2 && _notEquals_2);
          }
          if (_and_1) {
            VCObject _xblockexpression_1 = null;
            {
              final Characteristic cstic = ((Characteristic) left);
              String _name = cstic.getName();
              IssueImpl _createCharacteristicTypeIssue = this.createCharacteristicTypeIssue(left, right, leftType);
              this.name2Issue.put(_name, _createCharacteristicTypeIssue);
              VCObject _addVCObject = this.addVCObject(cstic);
              _xblockexpression_1 = (_addVCObject);
            }
            _xifexpression_2 = _xblockexpression_1;
          }
          _xblockexpression = (_xifexpression_2);
        }
        _xifexpression_1 = _xblockexpression;
      }
      _xifexpression = _xifexpression_1;
    }
    return _xifexpression;
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
