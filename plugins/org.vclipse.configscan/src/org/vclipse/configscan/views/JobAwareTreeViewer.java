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

import java.util.List;

import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Composite;

import com.google.common.collect.Lists;

public class JobAwareTreeViewer extends TreeViewer implements IJobChangeListener {

	public class TreeViewerLockEvent {
		
		private TreeViewer treeViewer;
		
		public TreeViewerLockEvent(TreeViewer treeViewer) {
			this.treeViewer = treeViewer;
		}
		
		public TreeViewer getViewer() {
			return treeViewer;
		}
	}
	
	public interface ITreeViewerLockListener {
		
		public void available(TreeViewerLockEvent event);
		
		public void locked(TreeViewerLockEvent event);
	}
	
	private List<ITreeViewerLockListener> listener;
	
	public JobAwareTreeViewer(Composite parent, int style) {
		super(parent, style);
		listener = Lists.newArrayList();
	}

	public void addTreeViewerLockListener(ITreeViewerLockListener lockListener) {
		listener.add(lockListener);
	}
	
	public void removeTreeViewerLockListener(ITreeViewerLockListener lockListener) {
		listener.remove(lockListener);
	}
	
	public void aboutToRun(IJobChangeEvent event) {
		for(ITreeViewerLockListener lockListener : listener) {
			lockListener.locked(new TreeViewerLockEvent(this));
		}
	}

	@Override
	public void awake(IJobChangeEvent event) {
		
	}

	@Override
	public void done(IJobChangeEvent event) {
		for(ITreeViewerLockListener lockListener : listener) {
			lockListener.available(new TreeViewerLockEvent(this));
		}
	}

	@Override
	public void running(IJobChangeEvent event) {
		for(ITreeViewerLockListener lockListener : listener) {
			lockListener.locked(new TreeViewerLockEvent(this));
		}
	}

	@Override
	public void scheduled(IJobChangeEvent event) {
		for(ITreeViewerLockListener lockListener : listener) {
			lockListener.locked(new TreeViewerLockEvent(this));
		}
	}

	@Override
	public void sleeping(IJobChangeEvent event) {
		for(ITreeViewerLockListener lockListener : listener) {
			lockListener.available(new TreeViewerLockEvent(this));
		}
	}	
}
