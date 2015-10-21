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
package org.vclipse.console

import java.util.HashMap
import java.util.Map
import org.eclipse.swt.graphics.Color
import org.eclipse.swt.widgets.Display
import org.eclipse.ui.console.ConsolePlugin
import org.eclipse.ui.console.IConsole
import org.eclipse.ui.console.MessageConsole
import org.eclipse.ui.console.MessageConsoleStream
import org.eclipse.ui.plugin.AbstractUIPlugin
import org.osgi.framework.BundleContext

/** 
 * The activator class controls the plug-in life cycle
 */
class CMConsolePlugin extends AbstractUIPlugin {
	/** 
	 * The plug-in ID
	 */
	public static final String PLUGIN_ID = "org.vclipse.console"
	/** 
	 * The shared instance
	 */
	static CMConsolePlugin plugin
	/** 
	 */
	final Map<Kind, MessageConsoleStream> consoleStreams

	/** 
	 * The kind of MessageConsoleStream used
	 */
	public enum Kind {
		Task,
		Result,
		Error,
		Warning,
		Info
	}
	/** 
	 * The constructor
	 */

	new() {
		super()
		val MessageConsole console = new MessageConsole("VClipse", null)
		console.activate()
		ConsolePlugin.getDefault().getConsoleManager().addConsoles(#[console])
		consoleStreams = new HashMap<Kind, MessageConsoleStream>()
		val Display display = Display.getDefault()
		for (Kind k : Kind.values()) {
			val MessageConsoleStream newMessageStream = console.newMessageStream()
			newMessageStream.setColor(getColor(display, k))
			consoleStreams.put(k, newMessageStream)
		}

	}

	// TODO make this configurable or use other configurable colors
	/** 
	 * @param display
	 * @param kind
	 * @return
	 */
	def private Color getColor(Display display, Kind kind) {

		switch (kind) {
			case Task: {
				return new Color(display, 0, 0, 0)
			} // black
			case Result: {
				return new Color(display, 0, 0, 255)
			} // blue
			case Error: {
				return new Color(display, 255, 0, 0)
			} // red
			case Warning: {
				return new Color(display, 255, 0, 255)
			} // magenta
			case Info: {
				return new Color(display, 0, 255, 0)
			}
		} // green
		return null
	}

	/** 
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	override void start(BundleContext context) throws Exception {
		super.start(context)
		plugin = this
	}

	/** 
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	override void stop(BundleContext context) throws Exception {
		for (MessageConsoleStream stream : consoleStreams.values()) {
			stream.getColor().dispose()
		}
		plugin = null
		super.stop(context)
	}

	/** 
	 * Returns the shared instance
	 * @return the shared instance
	 */
	def static CMConsolePlugin getDefault() {
		return plugin
	}

	/** 
	 * @return
	 */
	def MessageConsoleStream getConsole(Kind kind) {
		return consoleStreams.get(kind)
	}

}
