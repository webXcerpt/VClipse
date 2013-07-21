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
import org.eclipse.emf.compare.diff.DefaultDiffEngine;
import org.vclipse.vcml.compare.FeatureFilter;
import org.vclipse.vcml.compare.ModelChangesProcessor;

@Singleton
@SuppressWarnings("all")
public class ModelDifferencesEngine extends DefaultDiffEngine {
  private FeatureFilter featureFilter;
  
  @Inject
  public ModelDifferencesEngine(final ModelChangesProcessor processor, final FeatureFilter featureFilter) {
    super(processor);
    this.featureFilter = featureFilter;
  }
  
  protected org.eclipse.emf.compare.diff.FeatureFilter createFeatureFilter() {
    return this.featureFilter;
  }
}
