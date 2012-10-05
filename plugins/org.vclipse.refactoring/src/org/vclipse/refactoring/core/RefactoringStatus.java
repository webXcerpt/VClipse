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

import org.eclipse.emf.common.util.Diagnostic;

public class RefactoringStatus extends org.eclipse.ltk.core.refactoring.RefactoringStatus {

	private Iterable<Diagnostic> diagnostics;
	
	public Iterable<Diagnostic> getDiagnostics() {
		return diagnostics;
	}
	
	public void setDiagnostics(Iterable<Diagnostic> diagnostics) {
		this.diagnostics = diagnostics;
	}
}
