/*******************************************************************************
 * Copyright (c) 2012 webXcerpt Software GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *  
 * Contributors:
 *     webXcerpt Software GmbH - initial creator
 ******************************************************************************/
package org.vclipse.refactoring.core;

import org.eclipse.compare.ITypedElement;
import org.eclipse.compare.structuremergeviewer.Differencer;
import org.vclipse.base.compare.MultipleEntriesTypedElement;

public class DiffNode extends org.eclipse.compare.structuremergeviewer.DiffNode {
	
	public DiffNode() {
		super(null, Differencer.CHANGE);
	}
	
	@Override
	public String getName() {
		ITypedElement left = getLeft();
		if(left instanceof MultipleEntriesTypedElement) {
			return left.getName();
		}
		return super.getName();
	}
}
