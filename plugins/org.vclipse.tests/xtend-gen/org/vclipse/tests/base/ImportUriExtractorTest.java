package org.vclipse.tests.base;

import com.google.inject.Inject;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.xtext.junit4.InjectWith;
import org.eclipse.xtext.junit4.XtextRunner;
import org.eclipse.xtext.xbase.lib.Functions.Function0;
import org.eclipselabs.xtext.utils.unittesting.XtextTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.vclipse.base.ImportUriExtractor;
import org.vclipse.tests.VClipseTestPlugin;
import org.vclipse.tests.VClipseTestUtilities;

@RunWith(value = XtextRunner.class)
@InjectWith(value = VClipseTestPlugin.class)
@SuppressWarnings("all")
public class ImportUriExtractorTest extends XtextTest {
  @Inject
  private VClipseTestUtilities resourcesLoader;
  
  @Inject
  private ImportUriExtractor uriExtractor;
  
  public ImportUriExtractorTest() {
    super(
      new Function0<String>() {
        public String apply() {
          String _simpleName = ImportUriExtractorTest.class.getSimpleName();
          return _simpleName;
        }
      }.apply());
  }
  
  @Test
  public void test_ImportUriComputation_EMPTY() {
    ResourceSetImpl _resourceSetImpl = new ResourceSetImpl();
    final ResourceSetImpl resourceSet = _resourceSetImpl;
    URI _createURI = URI.createURI("file:///c:/test.test");
    final Resource resource_one = resourceSet.createResource(_createURI);
    URI _createPlatformResourceURI = URI.createPlatformResourceURI("test2.test", true);
    final Resource resource_two = resourceSet.createResource(_createPlatformResourceURI);
    String extracted = this.uriExtractor.getImportUri(resource_one, resource_two);
    boolean _equals = "".equals(extracted);
    Assert.assertTrue(extracted, _equals);
    String _importUri = this.uriExtractor.getImportUri(resource_two, resource_one);
    extracted = _importUri;
    boolean _equals_1 = "".equals(extracted);
    Assert.assertTrue(extracted, _equals_1);
  }
  
  @Test
  public void test_ImportUriComputation_SAME_LENGTH() {
    final Resource resource_one = this.resourcesLoader.getResource("/compare/added_vc_objects/VCML/car.vcml");
    final Resource resource_two = this.resourcesLoader.getResource("/compare/added_vc_objects/VCML/engine.vcml");
    final Resource resource_three = this.resourcesLoader.getResource("/compare/added_vc_objects/SAP/car.vcml");
    String extracted = this.uriExtractor.getImportUri(resource_one, resource_two);
    boolean _equals = extracted.equals("car.vcml");
    Assert.assertTrue(extracted, _equals);
    String _importUri = this.uriExtractor.getImportUri(resource_one, resource_one);
    extracted = _importUri;
    boolean _equals_1 = extracted.equals("");
    Assert.assertTrue(extracted, _equals_1);
    String _importUri_1 = this.uriExtractor.getImportUri(resource_two, resource_one);
    extracted = _importUri_1;
    boolean _equals_2 = extracted.equals("engine.vcml");
    Assert.assertTrue(extracted, _equals_2);
    String _importUri_2 = this.uriExtractor.getImportUri(resource_one, resource_three);
    extracted = _importUri_2;
    boolean _equals_3 = extracted.equals("../VCML/car.vcml");
    Assert.assertTrue(extracted, _equals_3);
    String _importUri_3 = this.uriExtractor.getImportUri(resource_three, resource_one);
    extracted = _importUri_3;
    boolean _equals_4 = extracted.equals("../SAP/car.vcml");
    Assert.assertTrue(extracted, _equals_4);
    String _importUri_4 = this.uriExtractor.getImportUri(resource_two, resource_three);
    extracted = _importUri_4;
    boolean _equals_5 = extracted.equals("../VCML/engine.vcml");
    Assert.assertTrue(extracted, _equals_5);
    String _importUri_5 = this.uriExtractor.getImportUri(resource_three, resource_two);
    extracted = _importUri_5;
    boolean _equals_6 = extracted.equals("../SAP/car.vcml");
    Assert.assertTrue(extracted, _equals_6);
  }
  
  @Test
  public void test_ImportUriComputation_DIFFERENT_LENGTH() {
    final Resource resource_one = this.resourcesLoader.getResource("/compare/added_vc_objects/VCML/car.vcml");
    final Resource resource_two = this.resourcesLoader.getResource("/resources/VCML/car.vcml");
    String extracted = this.uriExtractor.getImportUri(resource_two, resource_one);
    boolean _equals = extracted.equals("../../../resources/VCML/car.vcml");
    Assert.assertTrue(extracted, _equals);
    String _importUri = this.uriExtractor.getImportUri(resource_one, resource_two);
    extracted = _importUri;
    boolean _equals_1 = extracted.equals("../../compare/added_vc_objects/VCML/car.vcml");
    Assert.assertTrue(extracted, _equals_1);
  }
}
