/*******************************************************************************
 * Copyright (c) 2010 webXcerpt Software GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    webXcerpt Software GmbH - initial creator
 *******************************************************************************/
package org.vclipse.vcml.editor;

import org.eclipse.jface.text.IInformationControl;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.swt.widgets.Shell;

import com.google.inject.Inject;

public class VCMLInformationControlCreator implements IInformationControlCreator {

	@Inject
	ILabelProvider labelProvider;
	
	public IInformationControl createInformationControl(Shell parent) {
		return new VCMLInformationControl(parent, labelProvider);
	}

}
