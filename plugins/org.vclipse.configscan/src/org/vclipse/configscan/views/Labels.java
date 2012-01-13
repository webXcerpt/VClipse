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
package org.vclipse.configscan.views;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class Labels {
	private Label runsLabel;

	private Label errorsLabel;

	private Label successesLabel;

	private Label timeLabel;

	private Composite parent;
	
	public Labels(Composite parent, int runs, int errors, int successes, int time) {
		this.parent = parent;
		
		runsLabel = new Label(parent, SWT.NONE);
		runsLabel.setText("Runs: " + runs);
		
		errorsLabel = new Label(parent, SWT.NONE);
		errorsLabel.setText("Errors: " + errors);
		
		successesLabel = new Label(parent, SWT.NONE);
		successesLabel.setText("Successes: " + successes);
		
		timeLabel = new Label(parent, SWT.NONE);
		timeLabel.setText("Time: " + time);
	}
	
	public void updateLabels(int runs, int errors, int successes, int time) {
		runsLabel.setText("Runs: " + runs);
		errorsLabel.setText("Errors: " + errors);
		successesLabel.setText("Successes: " + successes);
		timeLabel.setText("Time: " + time);
		
		runsLabel.redraw();
		runsLabel.update();
		
		errorsLabel.redraw();
		errorsLabel.update();
		
		successesLabel.redraw();
		successesLabel.update();
		
		timeLabel.redraw();
		timeLabel.update();
		
		parent.layout();
	}
}
