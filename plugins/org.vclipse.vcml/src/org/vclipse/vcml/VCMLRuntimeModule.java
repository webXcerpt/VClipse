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
package org.vclipse.vcml;

import org.eclipse.xtext.conversion.IValueConverterService;
import org.eclipse.xtext.linking.ILinker;
import org.eclipse.xtext.linking.ILinkingDiagnosticMessageProvider;
import org.eclipse.xtext.naming.IQualifiedNameConverter;
import org.eclipse.xtext.naming.IQualifiedNameProvider;
import org.eclipse.xtext.parsetree.reconstr.ITokenSerializer;
import org.eclipse.xtext.resource.IDefaultResourceDescriptionStrategy;
import org.eclipse.xtext.serializer.ISerializer;
import org.eclipse.xtext.validation.CompositeEValidator;
import org.eclipse.xtext.validation.INamesAreUniqueValidationHelper;
import org.vclipse.base.naming.IClassNameProvider;
import org.vclipse.base.naming.INameProvider;
import org.vclipse.base.naming.NullQualifiedNameConverter;
import org.vclipse.vcml.conversion.VCMLValueConverter;
import org.vclipse.vcml.documentation.VCMLClassNameProvider;
import org.vclipse.vcml.linking.VCMLLinker;
import org.vclipse.vcml.naming.CrossRefExtractingSimpleNameProvider;
import org.vclipse.vcml.naming.UniqueVCMLNamesValidationHelper;
import org.vclipse.vcml.naming.VCMLNameProvider;
import org.vclipse.vcml.resource.VCMLResourceDescriptionStrategy;
import org.vclipse.vcml.serializer.VCMLCrossReferenceSerializer;
import org.vclipse.vcml.serializer.VCMLSerializer;
import org.vclipse.vcml.validation.VCMLLinkingDiagnosticMessageProvider;

import com.google.inject.Binder;
import com.google.inject.name.Names;

/**
 * Use this class to register components to be used at runtime / without the Equinox extension registry.
 */
public class VCMLRuntimeModule extends org.vclipse.vcml.AbstractVCMLRuntimeModule {

	@Override
	public void configure(Binder binder) {
		super.configure(binder);
		binder.bindConstant().annotatedWith(Names.named(CompositeEValidator.USE_EOBJECT_VALIDATOR)).to(false);
	}

	@Override
	public Class<? extends IValueConverterService> bindIValueConverterService() {
		return VCMLValueConverter.class;
	}

	@Override
	public Class<? extends ILinker> bindILinker() {
		return VCMLLinker.class;
	}

	public Class<? extends ITokenSerializer.ICrossReferenceSerializer> bindICrossReferenceSerializer() {
		return VCMLCrossReferenceSerializer.class;
	}
	
	@Override
	public Class<? extends org.eclipse.xtext.resource.IContainer.Manager> bindIContainer$Manager() {
		return org.eclipse.xtext.resource.containers.StateBasedContainerManager.class;
	}
	
	public Class<? extends IClassNameProvider> bindIClassNameProvider() {
		return VCMLClassNameProvider.class;
	}

	public Class<? extends IQualifiedNameConverter> bindIQualifiedNameConverter() {
		return NullQualifiedNameConverter.class;
	}

	public Class<? extends ILinkingDiagnosticMessageProvider.Extended> bindILinkingDiagnosticMessageProvider() {
		return VCMLLinkingDiagnosticMessageProvider.class;
	}
	
	public Class<? extends INamesAreUniqueValidationHelper> bindNamesAreUniqueValidationHelper() {
		return UniqueVCMLNamesValidationHelper.class;
	} 
	
	public Class<? extends IDefaultResourceDescriptionStrategy> bindIDefaultResourceDescriptionStrategy() {
		return VCMLResourceDescriptionStrategy.class;
	}

	@Override
	public Class<? extends ISerializer> bindISerializer() {
		return VCMLSerializer.class;
	}
	
	public Class<? extends IQualifiedNameProvider> bindIQualifiedNameProvider() {
		return CrossRefExtractingSimpleNameProvider.class;
	}
	
	public Class<? extends INameProvider> bindNameProvider() {
		return VCMLNameProvider.class;
	}
}
