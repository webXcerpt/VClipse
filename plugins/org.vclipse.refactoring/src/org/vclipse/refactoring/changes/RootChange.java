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
package org.vclipse.refactoring.changes;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.CompositeChange;
import org.vclipse.refactoring.IRefactoringUIContext;

public class RootChange extends CompositeChange {

	private boolean performed = false;
	
	public RootChange(IRefactoringUIContext context) {
		super("Changes during the re-factoring process.", new Change[0]);
		ModelChange modelChange = new ModelChange(context);
		add(modelChange);
	}
	
	@Override
 	public Change perform(IProgressMonitor pm) throws CoreException {
		if(!performed) {
			for(Change change : getChildren()) {
				change.perform(pm);
			}
			performed = true;
		}
		return null;
	}
	
	@Override
	public Object getModifiedElement() {
		if(getChildren().length != 0) {
			return getChildren()[0].getModifiedElement();
		}
		return null;
	}
	
	@Override
	public void dispose() {
		performed = false;
		super.dispose();
	}
}
