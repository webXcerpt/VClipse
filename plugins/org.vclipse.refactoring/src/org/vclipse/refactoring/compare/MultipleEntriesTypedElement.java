package org.vclipse.refactoring.compare;

import java.io.InputStream;

import org.eclipse.compare.CompareUI;
import org.eclipse.compare.ITypedElement;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.swt.graphics.Image;
import org.eclipse.xtext.naming.IQualifiedNameProvider;
import org.eclipse.xtext.serializer.ISerializer;
import org.vclipse.refactoring.RefactoringPlugin;

public class MultipleEntriesTypedElement extends DefaultElement {

	private final EObject[] elements;
	private final ISerializer serializer;
	private final IQualifiedNameProvider nameProvider;

	public static MultipleEntriesTypedElement getDefault() {
		return new MultipleEntriesTypedElement();
	}
	
	public MultipleEntriesTypedElement(ISerializer serializer, EObject ... elements) {
		this(serializer, null, elements);
	}
	
	public MultipleEntriesTypedElement(ISerializer serializer, IQualifiedNameProvider nameProvider, EObject ... elements) {
		super(serializer, nameProvider);
		this.elements = elements;
		this.serializer = serializer;
		this.nameProvider = nameProvider;
	}
	
	private MultipleEntriesTypedElement() {
		elements = null;
		serializer = null;
		nameProvider = null;
	}

	@Override
	public InputStream getContents() throws CoreException {
		if(serializer == null) {
			return super.getContents();
		} else {
			buffer = new MultipleEntriesStorage(serializer, nameProvider, elements);
		}
		return buffer.getContents();
	}

	@Override
	public String getName() {
		if(buffer == null) {
			try {
				getContents();
			} catch(CoreException exception) {
				RefactoringPlugin.log(exception.getMessage(), exception);
			}
		}
		return buffer.getName();
	}

	@Override
	public Image getImage() {
		String type = getType();
		return CompareUI.getImage(type);
	}

	@Override
	public String getType() {
		String name = getName();
		if (name != null) {
			int index = name.lastIndexOf('.');
			if (index == -1)
				return ""; //$NON-NLS-1$
			if (index == (name.length() - 1))
				return ""; //$NON-NLS-1$
			return name.substring(index + 1);
		}
		return ITypedElement.FOLDER_TYPE;
	}
}
