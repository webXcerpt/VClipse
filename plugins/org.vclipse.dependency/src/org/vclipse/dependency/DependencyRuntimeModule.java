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
package org.vclipse.dependency;

import org.eclipse.xtext.conversion.IValueConverterService;
import org.eclipse.xtext.naming.IQualifiedNameProvider;
import org.eclipse.xtext.parsetree.reconstr.ITokenSerializer;
import org.eclipse.xtext.resource.IDefaultResourceDescriptionStrategy;
import org.eclipse.xtext.serializer.ISerializer;
import org.vclipse.dependency.resource.DependencyResourceDescriptionStrategy;
import org.vclipse.vcml.conversion.VCMLValueConverter;
import org.vclipse.vcml.naming.CrossRefExtractingSimpleNameProvider;
import org.vclipse.vcml.serializer.VCMLCrossReferenceSerializer;
import org.vclipse.vcml.serializer.VCMLSerializer;

/**
 * Use this class to register components to be used at runtime / without the Equinox extension registry.
 */
public class DependencyRuntimeModule extends org.vclipse.dependency.AbstractDependencyRuntimeModule {

	@Override
	public Class<? extends IValueConverterService> bindIValueConverterService() {
		return VCMLValueConverter.class;
	}
	
	public Class<? extends ITokenSerializer.ICrossReferenceSerializer> bindICrossReferenceSerializer() {
		return VCMLCrossReferenceSerializer.class;
	}
	
	public Class<? extends IDefaultResourceDescriptionStrategy> bindIDefaultResourceDescriptionStrategy() {
		return DependencyResourceDescriptionStrategy.class;
	}

	@Override
	public Class<? extends ISerializer> bindISerializer() {
		return VCMLSerializer.class;
	}
	
	public Class<? extends IQualifiedNameProvider> bindIQualifiedNameProvider() {
		return CrossRefExtractingSimpleNameProvider.class;
	}
}
