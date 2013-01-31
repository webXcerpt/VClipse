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
package org.vclipse.vcml.ui;

import org.eclipse.xtext.builder.builderState.IBuilderState;
import org.vclipse.vcml.ui.builder.VCMLClusteringBuilderState;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.Scopes;

@SuppressWarnings("restriction")
public class VCMLSharedModule implements Module {

    public void configure(Binder binder) {
		binder.bind(IBuilderState.class).to(VCMLClusteringBuilderState.class).in(Scopes.SINGLETON);
    }

}
