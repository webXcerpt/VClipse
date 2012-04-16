package org.vclipse.vcml.ui.outline;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.diagnostics.IDiagnosticConsumer;
import org.eclipse.xtext.nodemodel.INode;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;
import org.vclipse.vcml.linking.VCMLLinker;
import org.vclipse.vcml.ui.extension.IExtensionPointUtilities;
import org.vclipse.vcml.ui.outline.actions.IVCMLOutlineActionHandler;
import org.vclipse.vcml.ui.outline.actions.OutlineActionCanceledException;
import org.vclipse.vcml.vcml.VCObject;
import org.vclipse.vcml.vcml.VcmlPackage;

import com.google.common.collect.Sets;
import com.google.inject.Inject;

public class SapRequestObjectLinker extends VCMLLinker {

	@Inject
	private IExtensionPointUtilities extensionPointReader;
	
	private Set<String> seenObjects;
	
	public SapRequestObjectLinker() {
		seenObjects = Sets.newHashSet();
	}
	
	public void setSeenObjects(Set<String> seenObjects) {
		this.seenObjects = seenObjects;
	}
	
	@Override
	protected EObject createProxy(EObject object, INode node, EReference reference) {
		EObject proxyObject = super.createProxy(object, node, reference);
		String text = NodeModelUtils.getTokenText(node);
		if(text != null) {
			proxyObject.eSet(VcmlPackage.eINSTANCE.getVCObject_Name(), text);
		}
		return proxyObject;
	}
	
	@Override
	protected void afterModelLinked(EObject model, IDiagnosticConsumer diagnosticsConsumer) {
		resolveProxies(model);
	}
	
	private void resolveProxies(EObject object) {
		TreeIterator<EObject> treeIterator = object.eAllContents();
		while(treeIterator.hasNext()) {
			for(EObject crossReference : treeIterator.next().eCrossReferences()) {
				if(crossReference.eIsProxy()) {
					if(crossReference instanceof VCObject) {
						VCObject vcobject = (VCObject)crossReference;
						if(seenObjects.contains(vcobject.eClass().getName() + "/" + vcobject.getName().toUpperCase())) {
							continue;
						}
					}
					Collection<IVCMLOutlineActionHandler<?>> handlers = extensionPointReader.getHandler(crossReference.eClass().getInstanceClassName());
					for(IVCMLOutlineActionHandler<?> handler : handlers) {
						if(handler.getClass().getSimpleName().contains("Extract")) {
							try {
								Method method = handler.getClass().getMethod("run", new Class[]{getInstanceType(crossReference), Resource.class, IProgressMonitor.class, Set.class});
								method.invoke(handler, new Object[]{crossReference, object.eResource(), new NullProgressMonitor(), seenObjects});
							} catch (NoSuchMethodException e) {
								e.printStackTrace();
								// ignore 
							} catch (IllegalAccessException e) {
								e.printStackTrace();
								// ignore 
							} catch (InvocationTargetException e) {
								Throwable targetException = e.getTargetException();
								if (targetException instanceof OutlineActionCanceledException) {
									break;
								} else {
									e.printStackTrace();
									//targetException.printStackTrace(err); // display original cause in VClipse console
								}
							} catch (SecurityException e) {
								e.printStackTrace();
								// ignore 
							} catch (ClassNotFoundException e) {
								e.printStackTrace();
								// ignore 
							} catch (Exception e) {
								//e.printStackTrace(err); // this can be a JCoException or an AbapExeption
							}
						}
					}
				}
			}
		}
	}

	private Class<?> getInstanceType(EObject obj) throws ClassNotFoundException {
		return Class.forName(getInstanceTypeName(obj));
	}
	
	private String getInstanceTypeName(EObject obj) {
		return obj.eClass().getInstanceTypeName();
	}
}
