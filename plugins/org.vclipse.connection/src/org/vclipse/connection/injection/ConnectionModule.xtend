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
package org.vclipse.connection.injection

import org.eclipse.equinox.security.storage.ISecurePreferences
import org.eclipse.equinox.security.storage.SecurePreferencesFactory
import org.eclipse.jface.preference.IPreferenceStore
import org.eclipse.ui.plugin.AbstractUIPlugin
import org.eclipse.xtext.service.AbstractGenericModule
import org.eclipse.xtext.service.SingletonBinding
import org.vclipse.connection.IConnectionDataStorage
import org.vclipse.connection.IConnectionHandler
import org.vclipse.connection.IDestinationDataProvider
import org.vclipse.connection.VClipseConnectionPlugin
import org.vclipse.connection.internal.ConnectionHandler
import org.vclipse.connection.internal.DefaultConnectionDataStorage
import org.vclipse.connection.internal.DefaultDestinationDataProvider

/** 
 */
class ConnectionModule extends AbstractGenericModule {
	/** 
	 */
	static final String SECURE_PREFERENCES_NODE = VClipseConnectionPlugin::ID + ".securePreferencesNode"
	/** 
	 */
	AbstractUIPlugin plugin

	/** 
	 * @param plugin
	 */
	new(AbstractUIPlugin plugin) {
		this.plugin = plugin
	}

	/** 
	 * @return
	 */
	def IPreferenceStore bindPreferenceStore() {
		return plugin.getPreferenceStore()
	}

	/** 
	 * @return
	 */
	def ISecurePreferences bindSecurePreferences() {
		return SecurePreferencesFactory::getDefault().node(SECURE_PREFERENCES_NODE)
	}

	/** 
	 * @return
	 */
	@SingletonBinding def Class<? extends IConnectionHandler> bindConnectionHandler() {
		return typeof(ConnectionHandler)
	}

	/** 
	 * @return
	 */
	def Class<? extends IDestinationDataProvider> bindDestinationDataProvider() {
		return typeof(DefaultDestinationDataProvider)
	}

	/** 
	 * @return
	 */
	def Class<? extends IConnectionDataStorage> bindConnectionDataStorage() {
		return typeof(DefaultConnectionDataStorage)
	}

}
