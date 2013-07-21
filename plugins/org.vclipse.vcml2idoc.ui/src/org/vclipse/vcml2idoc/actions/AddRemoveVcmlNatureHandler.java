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
package org.vclipse.vcml2idoc.actions;

import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.e4.core.commands.ExpressionContext;
import org.vclipse.vcml2idoc.VCML2IDocPlugin;
import org.vclipse.vcml2idoc.builder.VCML2IDocNature;

import com.google.common.collect.Lists;

public class AddRemoveVcmlNatureHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		Object appContext = event.getApplicationContext();
		if(appContext instanceof ExpressionContext) {
			Object defVariable = ((ExpressionContext)appContext).getDefaultVariable();
			if(defVariable instanceof List<?>) {
				for(Object entry : (List<?>)defVariable) {
					if(entry instanceof IProject) {
						IProject project = (IProject)entry;
						try {
							IProjectDescription description = project.getDescription();
							List<String> natureIds = Lists.newArrayList(description.getNatureIds());
							if(natureIds.contains(VCML2IDocNature.ID)) {
								natureIds.remove(VCML2IDocNature.ID);
							} else {
								natureIds.add(VCML2IDocNature.ID);
							}
							description.setNatureIds(natureIds.toArray(new String[natureIds.size()]));
							project.setDescription(description, new NullProgressMonitor());
						} catch (CoreException exception) {
							VCML2IDocPlugin.log(exception.getMessage(), exception);
						}
					}
				}
			}
		}
		return null;
	}
}
