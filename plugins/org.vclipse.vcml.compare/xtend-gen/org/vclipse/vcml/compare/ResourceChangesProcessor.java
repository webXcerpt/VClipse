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

import com.google.common.collect.Multimap;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.util.Set;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.xtext.naming.IQualifiedNameProvider;
import org.vclipse.base.ImportUriExtractor;
import org.vclipse.vcml.compare.FeatureFilter;
import org.vclipse.vcml.utils.DependencySourceUtils;

/**
 * Proccessor that is suited for changes handling on the resource level.
 * 
 * It means that it handles the import and option statements.
 */
@Singleton
@SuppressWarnings("all")
public class ResourceChangesProcessor /* implements DiffBuilder  */{
  protected /* VcmlModel */Object newVcmlModel;
  
  protected /* EList<Import> */Object newImports;
  
  protected /* EList<VCObject> */Object newObjects;
  
  protected /* EList<Option> */Object newOptions;
  
  protected boolean importsHandled;
  
  protected Set<String> seenObjects;
  
  protected /* VcmlPackage */Object vcmlPackage;
  
  protected /* VcmlFactory */Object vcmlFactory;
  
  private Multimap<Integer,Exception> compareErrors;
  
  @Inject
  protected ImportUriExtractor uriUtility;
  
  @Inject
  protected DependencySourceUtils sourceUtils;
  
  @Inject
  protected IQualifiedNameProvider nameProvider;
  
  @Inject
  protected IWorkspaceRoot root;
  
  @Inject
  protected FeatureFilter featureFilter;
  
  public void initialize(final /* VcmlModel */Object vcmlModel) {
    throw new Error("Unresolved compilation problems:"
      + "\nVcmlPackage cannot be resolved to a type."
      + "\nVcmlFactory cannot be resolved to a type."
      + "\neINSTANCE cannot be resolved"
      + "\neINSTANCE cannot be resolved"
      + "\nimports cannot be resolved"
      + "\nobjects cannot be resolved"
      + "\noptions cannot be resolved");
  }
  
  /**
   * Returns the errors.
   */
  public Multimap<Integer,Exception> getErrors() {
    return this.compareErrors;
  }
  
  public void referenceChange(final /* Match */Object match, final EReference reference, final EObject value, final /* DifferenceKind */Object kind, final /* DifferenceSource */Object source) {
    throw new Error("Unresolved compilation problems:"
      + "\nVcmlModel cannot be resolved to a type."
      + "\nThe method importURI is undefined for the type ResourceChangesProcessor"
      + "\nVcmlFactory.eINSTANCE cannot be resolved to a type."
      + "\nThe method importURI is undefined for the type ResourceChangesProcessor"
      + "\nVcmlFactory.eINSTANCE cannot be resolved to a type."
      + "\nVcmlModel cannot be resolved to a type."
      + "\nleft cannot be resolved"
      + "\nright cannot be resolved"
      + "\nimports cannot be resolved"
      + "\n^import cannot be resolved"
      + "\ninstanceClassName cannot be resolved"
      + "\n+ cannot be resolved"
      + "\neINSTANCE cannot be resolved"
      + "\ncreateImport cannot be resolved"
      + "\nsetImportURI cannot be resolved"
      + "\neResource cannot be resolved"
      + "\neResource cannot be resolved"
      + "\n^import cannot be resolved"
      + "\ninstanceClassName cannot be resolved"
      + "\n+ cannot be resolved"
      + "\neINSTANCE cannot be resolved"
      + "\ncreateImport cannot be resolved"
      + "\nsetImportURI cannot be resolved");
  }
  
  protected void reference(final EObject left, final EObject right, final EObject value, final EReference reference, final /* DifferenceKind */Object kind, final /* DifferenceSource */Object source) {
    throw new Error("Unresolved compilation problems:"
      + "\nDependency cannot be resolved to a type."
      + "\nDependency cannot be resolved to a type."
      + "\nDependency cannot be resolved to a type.");
  }
  
  /**
   * Handle the dependency changes directly.
   */
  public void dependencyChange(final /* Dependency */Object left, final /* Dependency */Object right, final /* DifferenceKind */Object kind, final /* DifferenceSource */Object source) {
    throw new Error("Unresolved compilation problems:"
      + "\nVCObject cannot be resolved to a type."
      + "\nDifferenceKind cannot be resolved to a type."
      + "\nDifferenceSource cannot be resolved to a type."
      + "\nThe method getInputStream is undefined for the type ResourceChangesProcessor"
      + "\nThe method getInputStream is undefined for the type ResourceChangesProcessor"
      + "\nThe method getSourceURI is undefined for the type ResourceChangesProcessor"
      + "\nThe method getInputStream is undefined for the type ResourceChangesProcessor"
      + "\n== cannot be resolved"
      + "\nADD cannot be resolved"
      + "\n== cannot be resolved"
      + "\n&& cannot be resolved"
      + "\nLEFT cannot be resolved"
      + "\n== cannot be resolved"
      + "\n== cannot be resolved"
      + "\nsegmentsList cannot be resolved"
      + "\nsubList cannot be resolved"
      + "\nget cannot be resolved"
      + "\nget cannot be resolved"
      + "\nget cannot be resolved"
      + "\nlastSegment cannot be resolved");
  }
}
