package org.vclipse.vcml.compare;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.compare.scope.FilterComparisonScope;
import org.vclipse.vcml.vcml.Dependency;
import org.vclipse.vcml.vcml.Import;
import org.vclipse.vcml.vcml.VcmlModel;

/**
 * This scope contains Import and Dependency objects, VcmlModel objects at the resource level.
 */
@SuppressWarnings("all")
public class VCMLResourceScope extends FilterComparisonScope {
  /**
   * Constructor
   */
  public VCMLResourceScope(final Notifier left, final Notifier right) {
    super(left, right, null);
    Predicate<Object> _instanceOf = Predicates.instanceOf(Import.class);
    Predicate<Object> _instanceOf_1 = Predicates.instanceOf(Dependency.class);
    Predicate<Object> _or = Predicates.<Object>or(_instanceOf, _instanceOf_1);
    this.setEObjectContentFilter(_or);
    Predicate<Object> _instanceOf_2 = Predicates.instanceOf(VcmlModel.class);
    this.setResourceContentFilter(_instanceOf_2);
  }
}
