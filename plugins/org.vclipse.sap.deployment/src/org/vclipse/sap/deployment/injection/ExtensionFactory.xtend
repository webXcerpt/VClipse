/** 
 * Copyright (c) 2010 - 2013 webXcerpt Software GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * webXcerpt Software GmbH - initial creator
 * www.webxcerpt.com
 */
package org.vclipse.sap.deployment.injection

import org.eclipse.xtext.ui.guice.AbstractGuiceAwareExecutableExtensionFactory
import org.osgi.framework.Bundle
import org.vclipse.sap.deployment.DeploymentPlugin
import com.google.inject.Injector

/** 
 */
class ExtensionFactory extends AbstractGuiceAwareExecutableExtensionFactory {
	override protected Bundle getBundle() {
		return DeploymentPlugin.getDefault().getBundle()
	}

	override protected Injector getInjector() {
		return DeploymentPlugin.getDefault().getInjector()
	}

}
