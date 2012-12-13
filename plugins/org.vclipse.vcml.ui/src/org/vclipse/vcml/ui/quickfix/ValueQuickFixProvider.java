/*******************************************************************************
 * Copyright (c) 2012 webXcerpt Software GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    webXcerpt Software GmbH - initial creator
 ******************************************************************************/
package org.vclipse.vcml.ui.quickfix;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.ui.editor.model.edit.IModificationContext;
import org.eclipse.xtext.ui.editor.model.edit.ISemanticModification;
import org.eclipse.xtext.ui.editor.quickfix.DefaultQuickfixProvider;
import org.eclipse.xtext.ui.editor.quickfix.Fix;
import org.eclipse.xtext.ui.editor.quickfix.IssueResolutionAcceptor;
import org.eclipse.xtext.validation.Issue;
import org.vclipse.vcml.validation.ValueValidator;

public class ValueQuickFixProvider extends DefaultQuickfixProvider {

	@Fix("Forbidden_default_attribute")
	public void fix_Duplicate_default_value(Issue issue, IssueResolutionAcceptor acceptor) {
		String label = issue.getData()[0];
		acceptor.accept(issue, label, label, null, new ISemanticModification() {
			public void apply(EObject element, IModificationContext context) throws Exception {
				for(EAttribute attribute : element.eClass().getEAllAttributes()) {
					if(ValueValidator.DEFAULT.equals(attribute.getName())) {
						element.eSet(attribute, false);
						break;
					}
				}
			}
		});
	}
}
