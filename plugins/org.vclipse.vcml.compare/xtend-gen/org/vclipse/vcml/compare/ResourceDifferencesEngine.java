package org.vclipse.vcml.compare;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.eclipse.emf.common.util.Monitor;
import org.eclipse.emf.compare.DifferenceKind;
import org.eclipse.emf.compare.DifferenceSource;
import org.eclipse.emf.compare.Match;
import org.eclipse.emf.compare.diff.DefaultDiffEngine;
import org.eclipse.emf.compare.diff.IDiffProcessor;
import org.eclipse.emf.ecore.EObject;
import org.vclipse.vcml.compare.FeatureFilter;
import org.vclipse.vcml.compare.ResourceChangesProcessor;
import org.vclipse.vcml.vcml.Dependency;

@Singleton
@SuppressWarnings("all")
public class ResourceDifferencesEngine extends DefaultDiffEngine {
  private FeatureFilter featureFilter;
  
  @Inject
  public ResourceDifferencesEngine(final ResourceChangesProcessor processor, final FeatureFilter featureFilter) {
    super(processor);
    this.featureFilter = featureFilter;
  }
  
  protected org.eclipse.emf.compare.diff.FeatureFilter createFeatureFilter() {
    return this.featureFilter;
  }
  
  protected void checkForDifferences(final Match match, final Monitor monitor) {
    final EObject left = match.getLeft();
    final EObject right = match.getRight();
    boolean _and = false;
    if (!(left instanceof Dependency)) {
      _and = false;
    } else {
      _and = ((left instanceof Dependency) && (right instanceof Dependency));
    }
    if (_and) {
      IDiffProcessor _diffProcessor = this.getDiffProcessor();
      final ResourceChangesProcessor resourceProcessor = ((ResourceChangesProcessor) _diffProcessor);
      resourceProcessor.dependencyChange(((Dependency) left), ((Dependency) right), DifferenceKind.ADD, DifferenceSource.LEFT);
      return;
    }
    super.checkForDifferences(match, monitor);
  }
}
