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

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.eclipse.emf.common.util.Monitor;
import org.vclipse.vcml.compare.FeatureFilter;
import org.vclipse.vcml.compare.ResourceChangesProcessor;

@Singleton
@SuppressWarnings("all")
public class ResourceDifferencesEngine /* implements DefaultDiffEngine  */{
  private FeatureFilter featureFilter;
  
  @Inject
  public ResourceDifferencesEngine(final ResourceChangesProcessor processor, final FeatureFilter featureFilter) {
    throw new Error("Unresolved compilation problems:"
      + "\nThe method super is undefined for the type ResourceDifferencesEngine");
  }
  
  protected FeatureFilter createFeatureFilter() {
    return this.featureFilter;
  }
  
  protected Object checkForDifferences(final /* Match */Object match, final Monitor monitor) {
    throw new Error("Unresolved compilation problems:"
      + "\nDependency cannot be resolved to a type."
      + "\nDependency cannot be resolved to a type."
      + "\nThe method or field diffProcessor is undefined for the type ResourceDifferencesEngine"
      + "\nDifferenceKind cannot be resolved to a type."
      + "\nDifferenceSource cannot be resolved to a type."
      + "\nThe method or field super is undefined for the type ResourceDifferencesEngine"
      + "\nDependency cannot be resolved to a type."
      + "\nDependency cannot be resolved to a type."
      + "\nleft cannot be resolved"
      + "\nright cannot be resolved"
      + "\nADD cannot be resolved"
      + "\nLEFT cannot be resolved"
      + "\ncheckForDifferences cannot be resolved");
  }
}
