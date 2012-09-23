package org.vclipse.dependency.linking;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.diagnostics.IDiagnosticConsumer;
import org.eclipse.xtext.diagnostics.Severity;
import org.eclipse.xtext.linking.lazy.LazyLinker;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;
import org.eclipse.xtext.validation.CheckType;

public class DependencyLinker extends LazyLinker {

	@Override
	protected void afterModelLinked(EObject model,
			IDiagnosticConsumer diagnosticsConsumer) {
		super.afterModelLinked(model, diagnosticsConsumer);
		checkSourceLineLength(model, diagnosticsConsumer);
	}

	// checks whether a line is not longer than 72 characters
	// implemented in linker due to discussion in http://www.eclipse.org/forums/index.php/mv/msg/379675/918740/#msg_918740
	protected void checkSourceLineLength(EObject object,
			IDiagnosticConsumer diagnosticsConsumer) {
		Resource resource = object.eResource();
		String text = NodeModelUtils.getNode(object).getText();
		int textLength = text.length();
		int currentIx = 0;
		int currentLineLength = 0;
		int markerStart = -1;
		int markerLength = 0;
		while (currentIx < textLength) {
			char currentChar = text.charAt(currentIx);
			if (currentChar == '\r' || currentChar == '\n') {
				currentLineLength = 0;
				if (markerStart != -1) {
					RangeBasedDiagnostic diagnostic = new RangeBasedDiagnostic(
							Severity.ERROR.ordinal(),
							"Line too long (only 72 characters allowed)",
							object, markerStart, markerLength, CheckType.FAST,
							null, new String[] {});
					diagnosticsConsumer.consume(diagnostic, Severity.ERROR);
					resource.getErrors().add(diagnostic);
					markerStart = -1;
					markerLength = 0;
				}
			} else {
				currentLineLength++;
				if (currentLineLength > 72) {
					if (markerStart == -1) {
						markerStart = currentIx;
					}
					markerLength++;
				}
			}
			currentIx++;
		}
	}

}
