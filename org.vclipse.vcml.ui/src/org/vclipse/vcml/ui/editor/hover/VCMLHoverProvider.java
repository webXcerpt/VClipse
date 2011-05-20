/*******************************************************************************
 * Copyright (c) 2010 webXcerpt Software GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    webXcerpt Software GmbH - initial creator
 ******************************************************************************/
package org.vclipse.vcml.ui.editor.hover;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.naming.IQualifiedNameProvider;
import org.eclipse.xtext.ui.editor.hover.html.DefaultEObjectHoverProvider;
import org.vclipse.base.IClassNameProvider;
import org.vclipse.vcml.documentation.VCMLAdditionalInformationProvider;
import org.vclipse.vcml.documentation.VCMLDescriptionProvider;
import org.vclipse.vcml.documentation.VCMLDocumentationProvider;
import org.vclipse.vcml.utils.DescriptionHandler;
import org.vclipse.vcml.utils.VCMLUtils;
import org.vclipse.vcml.vcml.Description;
import org.vclipse.vcml.vcml.Language;

import com.google.inject.Inject;

public class VCMLHoverProvider extends DefaultEObjectHoverProvider {

	@Inject
	private IClassNameProvider classNameProvider;

	@Inject
	private IQualifiedNameProvider nameProvider;

	@Inject
	private VCMLAdditionalInformationProvider additionalInformationProvider;

	@Inject
	private VCMLDescriptionProvider descriptionProvider;

	@Inject
	private VCMLDocumentationProvider documentationProvider;

	@Override
	protected String getFirstLine(EObject o) {
		return getClassName(o) + " <b>" + nameProvider.getFullyQualifiedName(o) + "</b>";
	}

	protected String getClassName(EObject o) {
		return getClassNameProvider().getClassName(o);
	}

	protected IClassNameProvider getClassNameProvider() {
		return classNameProvider;
	}

	protected String getHoverInfoAsHtml(EObject o) {
		if (!hasHover(o))
			return null;
		StringBuffer buffer = new StringBuffer();
		buffer.append(getFirstLine(o));
		String description = descriptionProvider.getDocumentation(o);
		if (description!=null && description.length()>0) {
			buffer.append("<p>");
			buffer.append(description);
			buffer.append("</p>");
		}
		String documentation = documentationProvider.getDocumentation(o);
		if (documentation!=null && documentation.length()>0) {
			buffer.append("<p>");
			buffer.append(documentation);
			buffer.append("</p>");
		}
		String additionalInformation = additionalInformationProvider.getDocumentation(o);
		if (additionalInformation!=null && additionalInformation.length()>0) {
			buffer.append("<p>");
			buffer.append(additionalInformation);
			buffer.append("</p>");
		}
		String multilineCommentDocumentation = getDocumentation(o);
		if (multilineCommentDocumentation!=null && multilineCommentDocumentation.length()>0) {
			buffer.append("<p>");
			buffer.append(multilineCommentDocumentation);
			buffer.append("</p>");
		}
		return buffer.toString();
	}
	
}
