package org.vclipse.vcml.validation;

import org.eclipse.xtext.diagnostics.DiagnosticMessage;
import org.eclipse.xtext.diagnostics.Severity;
import org.eclipse.xtext.linking.impl.LinkingDiagnosticMessageProvider;

public class VCMLLinkingDiagnosticMessageProvider extends LinkingDiagnosticMessageProvider {
	
	@Override
	public DiagnosticMessage getUnresolvedProxyMessage(ILinkingDiagnosticContext context) {
		String referenceType = context.getReference().getEReferenceType().getName();
		String issueCode = "Unresolved_" + referenceType;
		return issueCode.isEmpty() ? super.getUnresolvedProxyMessage(context) : new DiagnosticMessage(context.getLinkText() + " cannot be resolved", 
				Severity.ERROR, issueCode, context.getLinkText(), referenceType, context.getContext().eClass().getName());
	}
}
