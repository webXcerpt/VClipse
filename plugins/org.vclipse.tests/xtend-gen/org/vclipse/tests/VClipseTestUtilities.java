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

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.xbase.lib.Conversions;

/**
 * Utilities for VClipse tests.
 */
@SuppressWarnings("all")
public class VClipseTestUtilities /* implements XtextTest  */{
  public static /* VcmlPackage */Object VCML_PACKAGE /* Skipped initializer because of errors */;
  
  public static /* VcmlFactory */Object VCML_FACTORY /* Skipped initializer because of errors */;
  
  /**
   * Returns all contents of an entries container.
   */
  public ArrayList<EObject> getAllEntries(final EObject entry) {
    ArrayList<EObject> _xblockexpression = null;
    {
      final EObject rootContainer = EcoreUtil2.getRootContainer(entry);
      TreeIterator<EObject> _eAllContents = rootContainer.eAllContents();
      final ArrayList<EObject> entries = Lists.<EObject>newArrayList(_eAllContents);
      entries.add(0, rootContainer);
      _xblockexpression = (entries);
    }
    return _xblockexpression;
  }
  
  /**
   * Loads a resource for a particular location.
   */
  public Object getResource(final String location) {
    throw new Error("Unresolved compilation problems:"
      + "\nThe method eResource is undefined for the type VClipseTestUtilities");
  }
  
  /**
   * Returns the top level element for a resource on particular location.
   */
  public Object getResourceRoot(final String location) {
    throw new Error("Unresolved compilation problems:"
      + "\nThe method or field resourceSet is undefined for the type VClipseTestUtilities"
      + "\ngetResource cannot be resolved"
      + "\ncontents cannot be resolved"
      + "\nempty cannot be resolved"
      + "\nget cannot be resolved");
  }
  
  /**
   * Provides an input stream for a particular location.
   */
  public Object getInputStream(final String location) {
    throw new Error("Unresolved compilation problems:"
      + "\nThe method or field getClass is undefined for the type VClipseTestUtilities"
      + "\nclassLoader cannot be resolved"
      + "\ngetResourceAsStream cannot be resolved");
  }
  
  /**
   * Loads all contents of a resource on a particular location.
   */
  public ArrayList<EObject> getResourceContents(final String location) {
    throw new Error("Unresolved compilation problems:"
      + "\nThe method eAllContents is undefined for the type VClipseTestUtilities"
      + "\nType mismatch: cannot convert from Object to EObject");
  }
  
  /**
   * Removes new lines, tabulators, white spaces and values in the remove argument from the string.
   */
  public String removeNoise(final String string, final String... remove) {
    String _xifexpression = null;
    boolean _isEmpty = ((List<String>)Conversions.doWrapArray(remove)).isEmpty();
    if (_isEmpty) {
      _xifexpression = string;
    } else {
      _xifexpression = "";
    }
    String output = _xifexpression;
    for (final String part : remove) {
      String _replace = string.replace(part, "");
      output = _replace;
    }
    String _replace_1 = output.replace("\r", "");
    String _replace_2 = _replace_1.replace("\n", "");
    String _replace_3 = _replace_2.replace("\t", "");
    String _replace_4 = _replace_3.replace(" ", "");
    return _replace_4.trim();
  }
}
