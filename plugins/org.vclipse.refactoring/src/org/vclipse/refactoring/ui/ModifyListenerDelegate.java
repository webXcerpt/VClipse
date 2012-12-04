/*******************************************************************************
 * Copyright (c) 2012 webXcerpt Software GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *  
 * Contributors:
 *     webXcerpt Software GmbH - initial creator
 ******************************************************************************/
package org.vclipse.refactoring.ui;

import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.internal.SWTEventListener;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TypedListener;

public class ModifyListenerDelegate implements ModifyListener {

	private Listener[] listener;
	
	public void handleAndDelegateTo(Listener[] registeredListener) {
		this.listener = registeredListener;
	}

	@Override
	public void modifyText(ModifyEvent event) {
		for(Listener curListener : listener) {
			if(curListener instanceof TypedListener) {
				TypedListener typedListener = (TypedListener)curListener;
				SWTEventListener eventListener = typedListener.getEventListener();
				if(eventListener instanceof ModifyListener) {
					ModifyListener modifyListener = (ModifyListener)eventListener;
					modifyListener.modifyText(event);
				}
			}
		}
	}
}
