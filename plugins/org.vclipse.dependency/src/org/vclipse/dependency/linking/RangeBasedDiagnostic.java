package org.vclipse.dependency.linking;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.diagnostics.Diagnostic;
import org.eclipse.xtext.validation.CheckType;

public class RangeBasedDiagnostic extends org.eclipse.xtext.validation.RangeBasedDiagnostic implements Diagnostic {

	public RangeBasedDiagnostic(int severity, String message, EObject source,
			int offset, int length, CheckType checkType, String issueCode,
			String[] issueData) {
		super(severity, message, source, offset, length, checkType, issueCode, issueData);
	}

	@Override
	public String getLocation() {
		return getSourceEObject().eResource().getURI().toPlatformString(false);
	}

	@Override
	public int getLine() {
		return 0;
	}

	@Override
	public int getColumn() {
		return 0;
	}

}
