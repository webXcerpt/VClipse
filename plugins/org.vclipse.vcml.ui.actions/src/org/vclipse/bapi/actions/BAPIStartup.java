package org.vclipse.bapi.actions;

import org.eclipse.ui.IStartup;

public class BAPIStartup implements IStartup {

	@Override
	public void earlyStartup() {
		BAPIActionPlugin.getInstance();
	}
}
