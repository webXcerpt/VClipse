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

import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.isEmpty;
import static com.google.common.collect.Iterables.toArray;
import static com.google.common.collect.Iterables.transform;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import org.eclipse.compare.structuremergeviewer.ICompareInput;
import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.compare.Comparison;
import org.eclipse.emf.compare.Diff;
import org.eclipse.emf.compare.ide.ui.internal.structuremergeviewer.provider.ComparisonNode;
import org.eclipse.emf.compare.rcp.ui.structuremergeviewer.groups.DifferenceGroup;
import org.eclipse.emf.compare.rcp.ui.structuremergeviewer.groups.StructureMergeViewerGrouper;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryContentProvider;

class EMFCompareStructureMergeViewerContentProvider extends AdapterFactoryContentProvider {

	private final StructureMergeViewerGrouper fViewerGrouper;

	EMFCompareStructureMergeViewerContentProvider(AdapterFactory adapterFactory,
			StructureMergeViewerGrouper structureMergeViewerGrouper) {
		super(adapterFactory);
		this.fViewerGrouper = structureMergeViewerGrouper;
	}

	@Override
	public Object getParent(Object element) {
		final Object ret;
		if (element instanceof Adapter) {
			ret = getAdapterFactory().adapt(super.getParent(((Adapter)element).getTarget()),
					ICompareInput.class);
		} else if (element instanceof DifferenceGroup) {
			ret = ((DifferenceGroup)element).getComparison();
		} else {
			ret = null;
		}
		return ret;
	}

	@Override
	public final boolean hasChildren(Object element) {
		final boolean ret;
		if (element instanceof ComparisonNode) {
			Comparison target = ((ComparisonNode)element).getTarget();
			final Iterable<? extends DifferenceGroup> groups = fViewerGrouper.getGroups(target);
			if (isEmpty(groups)) {
				ret = super.hasChildren(((Adapter)element).getTarget());
			} else {
				ret = true;
			}
		} else if (element instanceof DifferenceGroup) {
			ret = !isEmpty(((DifferenceGroup)element).getDifferences());
		} else if (element instanceof Adapter) {
			ret = super.hasChildren(((Adapter)element).getTarget());
		} else {
			ret = false;
		}
		return ret;
	}

	@Override
	public final Object[] getChildren(Object element) {
		final Object[] ret;
		if (element instanceof ComparisonNode) {
			Comparison target = ((ComparisonNode)element).getTarget();
			final Iterable<? extends DifferenceGroup> groups = fViewerGrouper.getGroups(target);
			if (!isEmpty(groups)) {
				ret = Iterables.toArray(groups, DifferenceGroup.class);
			} else {
				Iterable<ICompareInput> compareInputs = adapt(super.getChildren(((Adapter)element)
						.getTarget()), getAdapterFactory(), ICompareInput.class);
				ret = toArray(compareInputs, ICompareInput.class);
			}
		} else if (element instanceof DifferenceGroup) {
			Iterable<? extends Diff> differences = ((DifferenceGroup)element).getDifferences();
			Iterable<ICompareInput> compareInputs = adapt(differences, getAdapterFactory(),
					ICompareInput.class);
			ret = toArray(compareInputs, ICompareInput.class);
		} else if (element instanceof Adapter) {
			Iterable<ICompareInput> compareInputs = adapt(super.getChildren(((Adapter)element).getTarget()),
					getAdapterFactory(), ICompareInput.class);
			ret = toArray(compareInputs, ICompareInput.class);
		} else {
			ret = new Object[0];
		}
		return ret;
	}

	@Override
	public Object[] getElements(Object element) {
		return getChildren(element);
	}

	/**
	 * Adapts each elements of the the given <code>iterable</code> to the given <code>type</code> by using the
	 * given <code>adapterFactory</code>.
	 * 
	 * @param <T>
	 *            the type of returned elements.
	 * @param iterable
	 *            the iterable to transform.
	 * @param adapterFactory
	 *            the {@link AdapterFactory} used to adapt elements
	 * @param type
	 *            the target type of adapted elements
	 * @return an iterable with element of type <code>type</code>.
	 */
	static <T> Iterable<T> adapt(Iterable<?> iterable, final AdapterFactory adapterFactory,
			final Class<T> type) {
		Function<Object, Object> adaptFunction = new Function<Object, Object>() {
			public Object apply(Object input) {
				return adapterFactory.adapt(input, type);
			}
		};
		return filter(transform(iterable, adaptFunction), type);
	}

	static <T> Iterable<T> adapt(Object[] iterable, final AdapterFactory adapterFactory, final Class<T> type) {
		return adapt(Lists.newArrayList(iterable), adapterFactory, type);
	}
}
