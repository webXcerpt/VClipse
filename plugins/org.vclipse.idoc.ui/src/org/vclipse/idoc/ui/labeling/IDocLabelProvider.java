/*******************************************************************************
 * Copyright (c) 2010 - 2013 webXcerpt Software GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *  
 * Contributors:
 *     	webXcerpt Software GmbH - initial creator
 * 		www.webxcerpt.com
 ******************************************************************************/
package org.vclipse.idoc.ui.labeling;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.xtext.ui.label.DefaultEObjectLabelProvider;
import org.vclipse.idoc.iDoc.IDoc;
import org.vclipse.idoc.iDoc.NumberField;
import org.vclipse.idoc.iDoc.Segment;
import org.vclipse.idoc.iDoc.StringField;

import com.google.inject.Inject;

/**
 * Provides labels for a EObjects.
 * 
 * see http://www.eclipse.org/Xtext/documentation/latest/xtext.html#labelProvider
 */
public class IDocLabelProvider extends DefaultEObjectLabelProvider {

	@Inject
	public IDocLabelProvider(AdapterFactoryLabelProvider delegate) {
		super(delegate);
	}

	String image(EObject ele) {
		return ele.eClass().getName() + ".png";
	}

	StyledString text(IDoc ele) {
		return new StyledString(ele.getName() + " ", StyledString.DECORATIONS_STYLER).append(new StyledString(ele.getType()));
	}
	
	StyledString text(NumberField ele) {
		return new StyledString(ele.getName()).append(new StyledString(" " + ele.getValue(), StyledString.COUNTER_STYLER));
		
	}
		
	String text(Segment ele) {
		return ele.getType();
	}
	
	StyledString text(StringField ele) {
		return new StyledString(ele.getName()).append(new StyledString(" " + ele.getValue(), StyledString.COUNTER_STYLER));
		
	}
		
}
