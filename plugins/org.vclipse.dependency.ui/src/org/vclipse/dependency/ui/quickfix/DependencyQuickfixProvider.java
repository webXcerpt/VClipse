
package org.vclipse.dependency.ui.quickfix;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.xtext.resource.SaveOptions;
import org.eclipse.xtext.ui.editor.model.edit.IModificationContext;
import org.eclipse.xtext.ui.editor.model.edit.ISemanticModification;
import org.eclipse.xtext.ui.editor.quickfix.DefaultQuickfixProvider;
import org.eclipse.xtext.ui.editor.quickfix.Fix;
import org.eclipse.xtext.ui.editor.quickfix.IssueResolutionAcceptor;
import org.eclipse.xtext.validation.Issue;
import org.vclipse.vcml.utils.DependencySourceUtils;
import org.vclipse.vcml.vcml.VCObject;
import org.vclipse.vcml.vcml.VcmlFactory;
import org.vclipse.vcml.vcml.VcmlModel;

import com.google.inject.Inject;

public class DependencyQuickfixProvider extends DefaultQuickfixProvider {

	@Inject
	private DependencySourceUtils sourceUtils;

	@Fix("Not_Existent_Source_Object")
	public void fixNotExistentSourceObject(Issue issue, IssueResolutionAcceptor acceptor) {
		String[] data = issue.getData();
		final String type = data[0];
		final String name = data[1];
		final String fileUri = data[2];
		
		String label = "Create vcobject with name " + name;
		String description = "Creates a new vcobject with name " + name;
		acceptor.accept(issue, label, description, null, new ISemanticModification() {
			public void apply(EObject element, IModificationContext context) throws Exception {
				ResourceSet resourceSet = element.eResource().getResourceSet();
				URI uri = URI.createURI(fileUri);
				Resource vcmlResource = resourceSet.getResource(uri, true);
				createVCObject(name, type, vcmlResource);
			}
		});
	}
	
	@Fix("Unresolved_Class")
	public void fixUnresolved_Class(Issue issue, IssueResolutionAcceptor acceptor) {
		fixUnresolvedEObject(issue, acceptor);
	}
	
	@Fix("Unresolved_Characteristic")
	public void fixUnresolved_Characteristic(Issue issue, IssueResolutionAcceptor acceptor) {
		fixUnresolvedEObject(issue, acceptor);
	}
	
	@Fix("Unresolved_VariantTable")
	public void fixUnresolved_VariantTable(Issue issue, IssueResolutionAcceptor acceptor) {
		fixUnresolvedEObject(issue, acceptor);
	}
	
	protected void fixUnresolvedEObject(Issue issue, IssueResolutionAcceptor acceptor) {
		final String linkText = issue.getData()[0];
		final String referenceType = issue.getData()[1];
		
		String label = "Create vcobject with name " + linkText;
		String description = "Creates a new vcobject with name " + linkText;
		acceptor.accept(issue, label, description, null, new ISemanticModification() {
			@Override
			public void apply(EObject element, IModificationContext context) throws Exception {
				Resource eResource = element.eResource();
				URI vcmlUri = sourceUtils.getVcmlResourceURI(eResource.getURI());
				Resource vcmlResource = eResource.getResourceSet().getResource(vcmlUri, true);
				if(vcmlUri != null) {
					createVCObject(linkText, referenceType, vcmlResource);
				}
			}
		});
	}
	
	protected void createVCObject(final String linkText, final String referenceType, Resource resource) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, IOException  {
		EList<EObject> contents = resource.getContents();
		if(!contents.isEmpty()) {
			EObject topObject = contents.get(0);
			if(topObject instanceof VcmlModel) {
				VcmlModel vcmlModel = (VcmlModel)topObject;
				VcmlFactory vcml = VcmlFactory.eINSTANCE;
				Object result = vcml.getClass().getMethod("create" + referenceType).invoke(vcml);
				if(result instanceof VCObject) {
					VCObject vcobject = (VCObject)result;
					vcobject.setName(linkText);
					vcmlModel.getObjects().add(vcobject);
					resource.save(SaveOptions.defaultOptions().toOptionsMap());
				}
			}
		}
	}
}
