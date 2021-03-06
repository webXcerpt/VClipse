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
package org.vclipse.refactoring.ui;

import org.eclipse.compare.CompareConfiguration;
import org.eclipse.compare.internal.NullViewer;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.ui.refactoring.ChangePreviewViewerInput;
import org.eclipse.ltk.ui.refactoring.IChangePreviewViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.xtext.ui.compare.InjectableViewerCreator;
import org.vclipse.refactoring.changes.ModelChange;
import org.vclipse.refactoring.changes.ModelChangeEntry;
import org.vclipse.refactoring.core.DiffNode;
import org.vclipse.refactoring.utils.Extensions;

import com.google.inject.Inject;

public class SourceCodeChangePreviewViewer implements IChangePreviewViewer {
	
	@Inject
	private Extensions extensions;
	
	private Viewer viewer;
	
	private Composite composite;
	
	@Override
	public void createControl(Composite parent) {
		viewer = new NullViewer(parent);
		this.composite = parent;
	}

	@Override
	public void setInput(ChangePreviewViewerInput input) {
		Change change = input.getChange();
		Object modifiedElement = change.getModifiedElement();
		if(viewer instanceof NullViewer && modifiedElement instanceof EObject) {
			EObject modifiedEObject = (EObject)modifiedElement;
			InjectableViewerCreator instance = extensions.getInstance(InjectableViewerCreator.class, modifiedEObject);
			if(instance != null) {
				viewer = instance.createViewer(composite, new CompareConfiguration());					
			}
		}
		if(change instanceof ModelChangeEntry) {
			ModelChangeEntry sourceCodeChange = (ModelChangeEntry)change;
			DiffNode preview = sourceCodeChange.getDiffNode();
			viewer.setInput(preview);
		} else if(change instanceof ModelChange) {
			ModelChange sourceCodeChanges = (ModelChange)change;
			DiffNode preview = sourceCodeChanges.getDiffNode();
			viewer.setInput(preview);
		}
	}

	@Override
	public Control getControl() {
		if(viewer != null) {
			return viewer.getControl();
		}
		return null;
	}
}
