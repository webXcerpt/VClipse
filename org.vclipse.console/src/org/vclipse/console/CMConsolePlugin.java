package org.vclipse.console;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class CMConsolePlugin extends AbstractUIPlugin {

	/**
	 *  The plug-in ID
	 */
	public static final String PLUGIN_ID = "org.vclipse.console";

	/**
	 *  The shared instance
	 */
	private static CMConsolePlugin plugin;
	
	/**
	 * 
	 */
	private final Map<Kind, MessageConsoleStream> consoleStreams;
	
	/**
	 * 
	 * The kind of MessageConsoleStream used
	 *
	 */
	public enum Kind {Task, Result, Error, Warning, Info};
	
	/**
	 * The constructor
	 */
	public CMConsolePlugin() {
		super();
		final MessageConsole console = new MessageConsole("VClipse", null);
		console.activate();
		ConsolePlugin.getDefault().getConsoleManager().addConsoles(new IConsole[]{console});
		consoleStreams = new HashMap<Kind, MessageConsoleStream>();
		final Display display = Display.getDefault();
		for (Kind k : Kind.values()) {
			final MessageConsoleStream newMessageStream = console.newMessageStream();
			newMessageStream.setColor(getColor(display, k));
			consoleStreams.put(k, newMessageStream);
		}
	}

	// TODO make this configurable or use other configurable colors
	/**
	 * @param display
	 * @param kind
	 * @return
	 */
	private Color getColor(final Display display, final Kind kind) {
		switch (kind) {
			case Task:    return new Color(display,   0,   0,   0); // black
			case Result:  return new Color(display,   0,   0, 255); // blue
			case Error:   return new Color(display, 255,   0,   0); // red
			case Warning: return new Color(display, 255,   0, 255); // magenta
			case Info:    return new Color(display,   0, 255,   0); // green
		}
		return null;
	}

	/**
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	@Override
	public void start(final BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	/**
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	@Override
	public void stop(final BundleContext context) throws Exception {
		for(MessageConsoleStream stream : consoleStreams.values()) {
			stream.getColor().dispose();
		}
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static CMConsolePlugin getDefault() {
		return plugin;
	}

	/**
	 * @return
	 */
	public MessageConsoleStream getConsole(final Kind kind) {
		return consoleStreams.get(kind);
	}
}
