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
package org.vclipse.vcml.ui.labeling;

import org.eclipse.emf.edit.ui.provider.AdapterFactoryLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.xtext.ui.label.DefaultEObjectLabelProvider;
import org.vclipse.vcml.utils.DescriptionHandler;
import org.vclipse.vcml.utils.VcmlUtils;
import org.vclipse.vcml.vcml.Characteristic;
import org.vclipse.vcml.vcml.Class;
import org.vclipse.vcml.vcml.Description;
import org.vclipse.vcml.vcml.Function;
import org.vclipse.vcml.vcml.Language;
import org.vclipse.vcml.vcml.Table;
import org.vclipse.vcml.vcml.VariantFunction;
import org.vclipse.vcml.vcml.VariantTable;

import com.google.inject.Inject;

/**
 * Provides labels for a EObjects.
 * 
 * see http://www.eclipse.org/Xtext/documentation/latest/xtext.html#labelProvider
 */
public abstract class AbstractVClipseLabelProvider extends DefaultEObjectLabelProvider {

	@Inject
	public AbstractVClipseLabelProvider(AdapterFactoryLabelProvider delegate) {
		super(delegate);
	}

	protected StyledString createStyledString(String name, Description description) {
		if (description==null) {
			return new StyledString(name, StyledString.QUALIFIER_STYLER); // assumption: object has no body
		}
		final StyledString result = new StyledString(name); 
		new DescriptionHandler() {
			private Language defaultLanguage = VcmlUtils.getDefaultLanguage(); 
			@Override
			public void handleSingleDescription(Language language, String value) {
				if (defaultLanguage.equals(language)) {
					result.append(new StyledString(" " + value, StyledString.DECORATIONS_STYLER)); 
				}
			}
		}.handleDescription(description);
		return result;
	}
	
	public String image(Characteristic element) {
		return "s_chaa.gif";
	}

	public String image(Class element) {
		return "b_clas.gif";
	}

	public StyledString text(Characteristic element) {
		return createStyledString(element.getName(), element.getDescription());
	}
	
	public StyledString text(Class element) {
		return createStyledString(element.getName(), element.getDescription());
	}
	
	public StyledString text(Table table) {
		VariantTable variantTable = table.getTable();
		return createStyledString(table.getTable().getName() + " (...)", variantTable.getDescription());
	}
	
	public StyledString text(Function element) {
		VariantFunction function = element.getFunction();
		return createStyledString(function.getName() + " (...)", function.getDescription());
	}
}
