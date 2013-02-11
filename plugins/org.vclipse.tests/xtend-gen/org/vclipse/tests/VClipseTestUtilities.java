package org.vclipse.tests;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.xbase.lib.Conversions;
import org.eclipselabs.xtext.utils.unittesting.XtextTest;
import org.vclipse.tests.VClipseTestPlugin;
import org.vclipse.vcml.vcml.VcmlFactory;
import org.vclipse.vcml.vcml.VcmlPackage;

/**
 * Utilities for VClipse tests.
 */
@SuppressWarnings("all")
public class VClipseTestUtilities extends XtextTest {
  public static VcmlPackage VCML_PACKAGE = VcmlPackage.eINSTANCE;
  
  public static VcmlFactory VCML_FACTORY = VcmlFactory.eINSTANCE;
  
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
  public Resource getResource(final String location) {
    EObject _resourceRoot = this.getResourceRoot(location);
    Resource _eResource = _resourceRoot.eResource();
    return _eResource;
  }
  
  /**
   * Returns the top level element for a resource on particular location.
   */
  public EObject getResourceRoot(final String location) {
    String _plus = (VClipseTestPlugin.ID + location);
    final URI uri = URI.createPlatformPluginURI(_plus, true);
    final Resource resource = this.resourceSet.getResource(uri, true);
    final EList<EObject> contents = resource.getContents();
    boolean _isEmpty = contents.isEmpty();
    if (_isEmpty) {
      return null;
    }
    return contents.get(0);
  }
  
  /**
   * Provides an input stream for a particular location.
   */
  public InputStream getInputStream(final String location) {
    Class<? extends Object> _class = this.getClass();
    ClassLoader _classLoader = _class.getClassLoader();
    InputStream _resourceAsStream = _classLoader.getResourceAsStream(location);
    return _resourceAsStream;
  }
  
  /**
   * Loads all contents of a resource on a particular location.
   */
  public ArrayList<EObject> getResourceContents(final String location) {
    final EObject root = this.getResourceRoot(location);
    boolean _equals = Objects.equal(root, null);
    if (_equals) {
      return Lists.<EObject>newArrayList();
    }
    TreeIterator<EObject> _eAllContents = root.eAllContents();
    final ArrayList<EObject> contents = Lists.<EObject>newArrayList(_eAllContents);
    contents.add(0, root);
    return contents;
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
