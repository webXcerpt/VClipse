/*******************************************************************************
 * Copyright (c) 2012 Obeo.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Obeo - initial API and implementation
 *******************************************************************************/
package org.vclipse.vcml.compare.experimental;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.jface.viewers.IElementComparer;

/**
 * We'll use this in order to compare our diff nodes through their target's {@link Object#equals(Object)}
 * instead of the nodes' own equals (which only resorts to instance equality).
 * <p>
 * Note that this will fall back to the default behavior for anything that is not an
 * {@link AbstractEDiffElement}.
 * </p>
 * <p>
 * This class most likely breaks the implicit contract of equals() since we are comparing AbstractEDiffElement
 * through two different means : if we have a target, use it... otherwise fall back to instance equality. Both
 * equals() and hashCode() follow this same rule.
 * </p>
 * 
 * @author <a href="mailto:laurent.goubet@obeo.fr">Laurent Goubet</a>
 */
public class DiffNodeComparer implements IElementComparer {
	/** Our delegate comparer. May be {@code null}. */
	private IElementComparer delegate;

	/**
	 * Constructs this comparer given the previous one that was installed on this viewer.
	 * 
	 * @param delegate
	 *            The comparer to which we should delegate our default behavior. May be {@code null}.
	 */
	public DiffNodeComparer(IElementComparer delegate) {
		this.delegate = delegate;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.jface.viewers.IElementComparer#equals(java.lang.Object, java.lang.Object)
	 */
	public boolean equals(Object a, Object b) {
		final boolean equal;
		if (a instanceof Adapter && b instanceof Adapter) {
			final Notifier targetA = ((Adapter)a).getTarget();
			if (targetA == null) {
				// Fall back to default behavior
				equal = a.equals(b);
			} else {
				equal = targetA.equals(((Adapter)b).getTarget());
			}
		} else if (delegate != null) {
			equal = delegate.equals(a, b);
		} else if (a != null) {
			equal = a.equals(b);
		} else {
			equal = b == null;
		}
		return equal;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.jface.viewers.IElementComparer#hashCode(java.lang.Object)
	 */
	public int hashCode(Object element) {
		final int hashCode;
		if (element instanceof Adapter) {
			final Notifier target = ((Adapter)element).getTarget();
			if (target == null) {
				// Fall back to default behavior
				hashCode = element.hashCode();
			} else {
				hashCode = target.hashCode();
			}
		} else if (delegate != null) {
			hashCode = delegate.hashCode(element);
		} else if (element != null) {
			hashCode = element.hashCode();
		} else {
			hashCode = 0;
		}
		return hashCode;
	}
}
