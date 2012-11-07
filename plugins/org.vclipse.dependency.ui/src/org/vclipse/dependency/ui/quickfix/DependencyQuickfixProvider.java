
package org.vclipse.dependency.ui.quickfix;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EGenericType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.xtext.resource.SaveOptions;
import org.eclipse.xtext.ui.editor.model.edit.IModificationContext;
import org.eclipse.xtext.ui.editor.model.edit.ISemanticModification;
import org.eclipse.xtext.ui.editor.quickfix.DefaultQuickfixProvider;
import org.eclipse.xtext.ui.editor.quickfix.Fix;
import org.eclipse.xtext.ui.editor.quickfix.IssueResolutionAcceptor;
import org.eclipse.xtext.util.SimpleAttributeResolver;
import org.eclipse.xtext.validation.Issue;
import org.vclipse.vcml.utils.DependencySourceUtils;
import org.vclipse.vcml.vcml.ConstraintClass;
import org.vclipse.vcml.vcml.ConstraintMaterial;
import org.vclipse.vcml.vcml.ConstraintObject;
import org.vclipse.vcml.vcml.ObjectCharacteristicReference;
import org.vclipse.vcml.vcml.VCObject;
import org.vclipse.vcml.vcml.VcmlFactory;
import org.vclipse.vcml.vcml.VcmlModel;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
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
				EObject locationContext = getContext(element);
				ResourceSet resourceSet = element.eResource().getResourceSet();
				URI uri = URI.createURI(fileUri);
				Resource vcmlResource = resourceSet.getResource(uri, true);
				createVCObject(name, type, vcmlResource, locationContext);
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
				EObject locationContext = getContext(element);
				Resource eResource = element.eResource();
				URI vcmlUri = sourceUtils.getVcmlResourceURI(eResource.getURI());
				Resource vcmlResource = eResource.getResourceSet().getResource(vcmlUri, true);
				if(vcmlUri != null) {
					createVCObject(linkText, referenceType, vcmlResource, locationContext);
				}
			}
		});
	}
	
	protected EObject getContext(EObject element) {
		if(element instanceof ObjectCharacteristicReference) {
			ConstraintObject location = ((ObjectCharacteristicReference)element).getLocation();
			return location instanceof ConstraintClass ? ((ConstraintClass)location).getClass_() : ((ConstraintMaterial)location).getObjectType();
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	protected void createVCObject(final String linkText, final String referenceType, Resource resource, EObject locationContext) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, IOException  {
		EList<EObject> contents = resource.getContents();
		if(!contents.isEmpty()) {
			EObject topObject = contents.get(0);
			if(topObject instanceof VcmlModel) {
				VcmlModel vcmlModel = (VcmlModel)topObject;
				VcmlFactory vcml = VcmlFactory.eINSTANCE;
				Object result = vcml.getClass().getMethod("create" + referenceType).invoke(vcml);
				if(result instanceof VCObject) {
					final VCObject vcobject = (VCObject)result;
					vcobject.setName(linkText);
					EList<VCObject> objects = vcmlModel.getObjects();
					Iterator<VCObject> vciterator = Iterables.filter(objects, new Predicate<VCObject>() {
						public boolean apply(VCObject object) {
							return vcobject.eClass() == object.eClass() && object.getName().equals(vcobject.getName());
						}
					}).iterator();
					if(!vciterator.hasNext()) {
						objects.add(vcobject);						
					} 
	
					if(locationContext != null) {
						EClass type = locationContext.eClass();
						EList<EReference> references = type.getEAllReferences();
						for(EReference reference : references) {
							EGenericType genericType = reference.getEGenericType();
							if(genericType.getEClassifier() == vcobject.eClass()) {
								if(reference.isMany()) {
									EList<EObject> entries = (EList<EObject>)locationContext.eGet(reference);
									final String name = vcobject.getName();
									Iterator<EObject> iterator = Iterables.filter(entries, new Predicate<EObject>() {
										public boolean apply(EObject object) {
											String resolvedName = SimpleAttributeResolver.NAME_RESOLVER.getValue(object);
											return name.equals(resolvedName);
										}
									}).iterator();
									if(!iterator.hasNext()) {
										entries.add(EcoreUtil.copy(vcobject));										
									}
								}
							}
						}
					}				
					resource.save(SaveOptions.defaultOptions().toOptionsMap());
				}
			}
		}
	}
}
