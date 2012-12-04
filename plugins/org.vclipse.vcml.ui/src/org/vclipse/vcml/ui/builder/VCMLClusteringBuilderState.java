/*******************************************************************************
 * Copyright (c) 2012 webXcerpt Software GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    webXcerpt Software GmbH - initial creator
 ******************************************************************************/
package org.vclipse.vcml.ui.builder;

import java.util.List;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.xtext.builder.clustering.ClusteringBuilderState;

import com.google.common.collect.Lists;

@SuppressWarnings("restriction")
public class VCMLClusteringBuilderState extends ClusteringBuilderState {
	
	@Override
	protected void clearResourceSet(ResourceSet resourceSet) {
        boolean wasDeliver = resourceSet.eDeliver();
        try {
            resourceSet.eSetDeliver(false);
            List<Resource> resources = Lists.newArrayList(resourceSet.getResources());
            for (Resource r : resources) {
            	URI uri = r.getURI();
            	if (!uri.fileExtension().equals("vcml")) { 
            		resourceSet.getResources().remove(r);
            	}
            }
        } finally {
            resourceSet.eSetDeliver(wasDeliver);
        }
	}
}
