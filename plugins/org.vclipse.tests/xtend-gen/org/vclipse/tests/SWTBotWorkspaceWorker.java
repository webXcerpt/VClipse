/**
 * Copyright (c) 2010 - 2013 webXcerpt Software GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     	webXcerpt Software GmbH - initial creator
 * 		www.webxcerpt.com
 */
package org.vclipse.tests;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import java.io.InputStream;
import java.util.ArrayList;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.xtext.util.StringInputStream;
import org.eclipse.xtext.xbase.lib.Conversions;
import org.eclipse.xtext.xbase.lib.Exceptions;

@SuppressWarnings("all")
public class SWTBotWorkspaceWorker /* implements XtextTest  */{
  protected /* SWTWorkbenchBot */Object bot;
  
  protected IProgressMonitor monitor;
  
  /**
   * Initialization
   */
  public Object before() {
    throw new Error("Unresolved compilation problems:"
      + "\nThe method or field super is undefined for the type SWTBotWorkspaceWorker"
      + "\nSWTWorkbenchBot cannot be resolved."
      + "\nbefore cannot be resolved"
      + "\nactivate cannot be resolved"
      + "\nperspectiveByLabel cannot be resolved"
      + "\nviewByTitle cannot be resolved"
      + "\n!= cannot be resolved"
      + "\nclose cannot be resolved"
      + "\nsleep cannot be resolved");
  }
  
  public Object after() {
    throw new Error("Unresolved compilation problems:"
      + "\nsleep cannot be resolved");
  }
  
  /**
   * Removes all projects from the workspace
   */
  protected Object cleanWorkspace() {
    throw new Error("Unresolved compilation problems:"
      + "\nsleep cannot be resolved");
  }
  
  /**
   * Extracts resources being used in the test plug-in itself to the workspace.
   * They are placed in the project org.vclipse.tests, so one can write own SWTBot tests.
   */
  protected IProject createProject() {
    try {
      IProject _xblockexpression = null;
      {
        IWorkspace _workspace = ResourcesPlugin.getWorkspace();
        final IWorkspaceRoot root = _workspace.getRoot();
        IProject project = root.getProject("org.vclipse.tests");
        NullProgressMonitor _nullProgressMonitor = new NullProgressMonitor();
        IProgressMonitor monitor = ((IProgressMonitor) _nullProgressMonitor);
        boolean _isAccessible = project.isAccessible();
        boolean _not = (!_isAccessible);
        if (_not) {
          IWorkbench _workbench = PlatformUI.getWorkbench();
          IWorkbenchWindow _activeWorkbenchWindow = _workbench.getActiveWorkbenchWindow();
          IWorkbenchPage _activePage = _activeWorkbenchWindow.getActivePage();
          final IEditorPart activeEditor = _activePage.getActiveEditor();
          boolean _notEquals = (!Objects.equal(activeEditor, null));
          if (_notEquals) {
            final IWorkbenchPartSite site = activeEditor.getSite();
            if ((site instanceof IEditorSite)) {
              final IActionBars actionBars = ((IEditorSite) site).getActionBars();
              IStatusLineManager _statusLineManager = actionBars.getStatusLineManager();
              IProgressMonitor _progressMonitor = _statusLineManager.getProgressMonitor();
              monitor = _progressMonitor;
            }
          }
          project.create(monitor);
          project.open(monitor);
        }
        final ArrayList<String> natures = Lists.<String>newArrayList(JavaCore.NATURE_ID, "org.eclipse.pde.PluginNature");
        final IProjectDescription description = project.getDescription();
        description.setNatureIds(((String[])Conversions.unwrapArray(natures, String.class)));
        project.setDescription(description, monitor);
        _xblockexpression = (project);
      }
      return _xblockexpression;
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  /**
   * Creates a folder with a given name in the parent container.
   */
  protected IFolder createFolder(final IContainer parent, final String name) {
    try {
      IFolder _xblockexpression = null;
      {
        Path _path = new Path(name);
        final IFolder folder = parent.getFolder(_path);
        boolean _exists = folder.exists();
        boolean _not = (!_exists);
        if (_not) {
          NullProgressMonitor _nullProgressMonitor = new NullProgressMonitor();
          folder.create(true, true, _nullProgressMonitor);
        }
        _xblockexpression = (folder);
      }
      return _xblockexpression;
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  /**
   * Creates a file with a given name in the parent container
   */
  protected IFile createFile(final IContainer parent, final String name, final InputStream stream) {
    try {
      IFile _xblockexpression = null;
      {
        Path _path = new Path(name);
        final IFile file = parent.getFile(_path);
        boolean _exists = file.exists();
        boolean _not = (!_exists);
        if (_not) {
          InputStream _xifexpression = null;
          boolean _equals = Objects.equal(stream, null);
          if (_equals) {
            StringInputStream _stringInputStream = new StringInputStream("");
            _xifexpression = _stringInputStream;
          } else {
            _xifexpression = stream;
          }
          NullProgressMonitor _nullProgressMonitor = new NullProgressMonitor();
          file.create(_xifexpression, IResource.FORCE, _nullProgressMonitor);
        }
        _xblockexpression = (file);
      }
      return _xblockexpression;
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
}
