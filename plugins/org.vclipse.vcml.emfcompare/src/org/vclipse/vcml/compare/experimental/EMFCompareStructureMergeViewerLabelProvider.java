/*******************************************************************************
 * Copyright (c) 2012, 2013 Obeo.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Obeo - initial API and implementation
 *******************************************************************************/
package org.vclipse.vcml.compare.experimental;

import com.google.common.base.Preconditions;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.compare.ide.ui.internal.util.StyledStringConverter;
import org.eclipse.emf.compare.provider.IItemStyledLabelProvider;
import org.eclipse.emf.compare.rcp.ui.EMFCompareRCPUIPlugin;
import org.eclipse.emf.compare.rcp.ui.structuremergeviewer.groups.DifferenceGroup;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryLabelProvider;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider.IStyledLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;

class EMFCompareStructureMergeViewerLabelProvider extends AdapterFactoryLabelProvider.FontAndColorProvider implements IStyledLabelProvider {

	/**
	 * @param adapterFactory
	 */
	public EMFCompareStructureMergeViewerLabelProvider(AdapterFactory adapterFactory, Viewer viewer) {
		super(adapterFactory, viewer);
	}

	@Override
	public String getText(Object element) {
		return getStyledText(element).getString();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.emf.edit.ui.provider.AdapterFactoryLabelProvider#getFont(java.lang.Object)
	 */
	@Override
	public Font getFont(Object object) {
		if (object instanceof Adapter) {
			return super.getFont(((Adapter)object).getTarget());
		}
		return super.getFont(object);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.emf.edit.ui.provider.AdapterFactoryLabelProvider#getForeground(java.lang.Object)
	 */
	@Override
	public Color getForeground(Object object) {
		if (object instanceof Adapter) {
			return super.getForeground(((Adapter)object).getTarget());
		}
		return super.getForeground(object);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.emf.edit.ui.provider.AdapterFactoryLabelProvider#getBackground(java.lang.Object)
	 */
	@Override
	public Color getBackground(Object object) {
		if (object instanceof Adapter) {
			return super.getBackground(((Adapter)object).getTarget());
		}
		return super.getBackground(object);
	}

	@Override
	public Image getImage(Object element) {
		final Image ret;
		if (element instanceof Adapter) {
			ret = super.getImage(((Adapter)element).getTarget());
		} else if (element instanceof DifferenceGroup) {
			final Image groupImage = ((DifferenceGroup)element).getImage();
			if (groupImage != null) {
				ret = groupImage;
			} else {
				ret = EMFCompareRCPUIPlugin.getImage("icons/full/toolb16/group.gif"); //$NON-NLS-1$ 
			}
		} else {
			ret = super.getImage(element);
		}

		return ret;
	}

	public StyledString getStyledText(Object element) {
		final StyledString ret;
		if (element instanceof Adapter) {
			Notifier target = ((Adapter)element).getTarget();
			StyledString styledText = getStyledText(getAdapterFactory(), target);
			if (styledText == null) {
				ret = new StyledString(super.getText(target));
			} else {
				ret = styledText;
			}
		} else if (element instanceof DifferenceGroup) {
			ret = new StyledString(((DifferenceGroup)element).getName());
		} else {
			ret = new StyledString(super.getText(element));
		}
		return ret;

	}

	/**
	 * Returns the styled text string of the given <code>object</code> by adapting it to
	 * {@link IItemStyledLabelProvider} and asking for its
	 * {@link IItemStyledLabelProvider#getStyledText(Object) text}. Returns null if <code>object</code> is
	 * null.
	 * 
	 * @param adapterFactory
	 *            the adapter factory to adapt from
	 * @param object
	 *            the object from which we want a text
	 * @return the text, or null if object is null.
	 * @throws NullPointerException
	 *             if <code>adapterFactory</code> is null.
	 */
	private static StyledString getStyledText(final AdapterFactory adapterFactory, final Object object) {
		Preconditions.checkNotNull(adapterFactory);
		if (object == null) {
			return null;
		}

		Object itemStyledLabelProvider = adapterFactory.adapt(object, IItemStyledLabelProvider.class);
		if (itemStyledLabelProvider instanceof IItemStyledLabelProvider) {
			StyledStringConverter stringConverter = new StyledStringConverter();
			return stringConverter.toJFaceStyledString(((IItemStyledLabelProvider)itemStyledLabelProvider)
					.getStyledText(object));
		}
		return null;
	}
}
