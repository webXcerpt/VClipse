/** 
 * Copyright (c) 2010 - 2015 webXcerpt Software GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * webXcerpt Software GmbH - initial creator
 * www.webxcerpt.com
 */
package org.vclipse.base.ui

import com.google.common.collect.Iterables
import java.util.List
import org.eclipse.core.commands.AbstractHandler
import org.eclipse.core.commands.ExecutionEvent
import org.eclipse.core.commands.ExecutionException
import org.eclipse.core.resources.IFile
import org.eclipse.e4.core.commands.ExpressionContext

abstract class FileListHandler extends AbstractHandler {
	override Object execute(ExecutionEvent event) throws ExecutionException {
		val Object appContext = event.getApplicationContext()
		if (appContext instanceof ExpressionContext) {
			val defVariable = appContext.getDefaultVariable
			if (defVariable instanceof List<?>)
				handleListVariable(Iterables.filter(defVariable as List<?>, IFile), event)
		}
		null
	}

	def abstract void handleListVariable(Iterable<IFile> collection, ExecutionEvent event)

}
