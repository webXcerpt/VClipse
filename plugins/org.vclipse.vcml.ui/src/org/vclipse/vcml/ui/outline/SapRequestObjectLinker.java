package org.vclipse.vcml.ui.outline;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.diagnostics.IDiagnosticConsumer;
import org.eclipse.xtext.nodemodel.INode;
import org.vclipse.vcml.linking.VCMLLinker;
import org.vclipse.vcml.ui.extension.IExtensionPointUtilities;
import org.vclipse.vcml.ui.outline.actions.IVCMLOutlineActionHandler;
import org.vclipse.vcml.ui.outline.actions.OutlineActionCanceledException;
import org.vclipse.vcml.vcml.VCObject;

import com.google.inject.Inject;

public class SapRequestObjectLinker extends VCMLLinker {

	@Inject
	private IExtensionPointUtilities extensionPointReader;
	
	@Override
	protected EObject createProxy(EObject object, INode node, EReference reference) {
		EObject proxyObject = super.createProxy(object, node, reference);
		if(proxyObject instanceof VCObject) {
			((VCObject)proxyObject).setName(node.getText());
		}
		return proxyObject;
	}
	
	@Override
	protected void afterModelLinked(EObject model, IDiagnosticConsumer diagnosticsConsumer) {
		TreeIterator<EObject> contents = model.eAllContents();
		while(contents.hasNext()) {
			resolveProxies(contents.next());
		}
	}
	
	private void resolveProxies(EObject object) {
		TreeIterator<EObject> treeIterator = object.eAllContents();
		while(treeIterator.hasNext()) {
			for(EObject crossReference : treeIterator.next().eCrossReferences()) {
				if(crossReference.eIsProxy()) {
					Collection<IVCMLOutlineActionHandler<?>> handlers = extensionPointReader.getHandler(crossReference.eClass().getInstanceClassName());
					for(IVCMLOutlineActionHandler<?> handler : handlers) {
						if(handler.getClass().getSimpleName().contains("Extract")) {
							try {
								Method method = handler.getClass().getMethod("run", new Class[]{getInstanceType(crossReference), Resource.class, IProgressMonitor.class});
								method.invoke(handler, new Object[]{crossReference, object.eResource(), new NullProgressMonitor()});
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
