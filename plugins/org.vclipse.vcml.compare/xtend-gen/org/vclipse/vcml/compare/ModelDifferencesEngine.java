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
