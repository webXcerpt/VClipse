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

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.compare.scope.FilterComparisonScope;
import org.vclipse.vcml.vcml.Option;
import org.vclipse.vcml.vcml.VCObject;

/**
 * This scope contains VCObject and Option objects.
 */
@SuppressWarnings("all")
public class VCMLModelScope extends FilterComparisonScope {
  /**
   * Constructor
   */
  public VCMLModelScope(final Notifier left, final Notifier right) {
    super(left, right, null);
    Predicate<Object> _instanceOf = Predicates.instanceOf(Option.class);
    Predicate<Object> _instanceOf_1 = Predicates.instanceOf(VCObject.class);
    Predicate<Object> _or = Predicates.<Object>or(_instanceOf, _instanceOf_1);
    this.setEObjectContentFilter(_or);
  }
}
