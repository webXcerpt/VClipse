package org.vclipse.refactoring.ui;

import java.util.Iterator;

import org.eclipse.compare.CompareConfiguration;
import org.eclipse.compare.internal.NullViewer;
import org.eclipse.compare.structuremergeviewer.DiffNode;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.ui.refactoring.ChangePreviewViewerInput;
import org.eclipse.ltk.ui.refactoring.IChangePreviewViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.xtext.serializer.ISerializer;
import org.eclipse.xtext.ui.compare.InjectableViewerCreator;
import org.vclipse.base.ui.util.EObjectTypedElement;
import org.vclipse.refactoring.ExtensionsReader;
import org.vclipse.refactoring.core.ModelChange;

import com.google.inject.Inject;
import com.google.inject.Injector;

@SuppressWarnings("restriction")
public class ChangePreviewViewer implements IChangePreviewViewer {

	@Inject
	private ExtensionsReader extensionReader;
	
	private Viewer viewer;
	
	private Composite composite;

	@Override
	public void createControl(Composite parent) {
		viewer = new NullViewer(parent);
		this.composite = parent;
	}

	@Override
	public Control getControl() {
		if(viewer != null) {
			return viewer.getControl();
		}
		return null;
	}

	@Override
	public void setInput(ChangePreviewViewerInput input) {
		if(viewer instanceof NullViewer) {
			Change change = input.getChange();
			if(change instanceof ModelChange) {
				ModelChange modelChange = (ModelChange)change;
				EObject current = modelChange.getCurrent();
				EObject rootContainer = EcoreUtil.getRootContainer(current);
				Iterator<Injector> iterator = extensionReader.getInjector().get(rootContainer.eClass()).iterator();
				if(iterator.hasNext()) {
					Injector injector = iterator.next();
					InjectableViewerCreator instance = injector.getInstance(InjectableViewerCreator.class);
					viewer = instance.createViewer(composite, new CompareConfiguration());
					ISerializer serializer = injector.getInstance(ISerializer.class);
					DiffNode diffNode = new DiffNode(new EObjectTypedElement(current, serializer), new EObjectTypedElement(modelChange.getChanged(), serializer));
					viewer.setInput(diffNode);	
				}
			}
		}
	}
}
