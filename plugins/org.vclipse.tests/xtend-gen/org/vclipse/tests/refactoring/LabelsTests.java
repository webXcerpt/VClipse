package org.vclipse.tests.refactoring;

import com.google.inject.Inject;
import java.util.ArrayList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.xtext.junit4.InjectWith;
import org.eclipse.xtext.junit4.XtextRunner;
import org.eclipse.xtext.xbase.lib.Functions.Function0;
import org.eclipselabs.xtext.utils.unittesting.XtextTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.vclipse.refactoring.core.RefactoringContext;
import org.vclipse.refactoring.core.RefactoringType;
import org.vclipse.refactoring.utils.EntrySearch;
import org.vclipse.refactoring.utils.Labels;
import org.vclipse.tests.VClipseTestUtilities;
import org.vclipse.tests.refactoring.RefactoringInjectorProvider;

@RunWith(value = XtextRunner.class)
@InjectWith(value = RefactoringInjectorProvider.class)
@SuppressWarnings("all")
public class LabelsTests extends XtextTest {
  @Inject
  private EntrySearch search;
  
  @Inject
  private Labels labels;
  
  @Inject
  private VClipseTestUtilities resourcesLoader;
  
  public LabelsTests() {
    super(new Function0<String>() {
      public String apply() {
        String _simpleName = LabelsTests.class.getSimpleName();
        return _simpleName;
      }
    }.apply());
  }
  
  @Test
  public void test_UILabelProvider() {
    final ArrayList<EObject> entries = this.resourcesLoader.getResourceContents("/refactoring/Refactoring/car.vcml");
    EClass _class_ = VClipseTestUtilities.VCML_PACKAGE.getClass_();
    EObject findEntry = this.search.<EObject>findEntry("(300)CAR", _class_, entries);
    Assert.assertNotNull(findEntry);
    RefactoringContext context = RefactoringContext.create(findEntry, null, RefactoringType.Extract);
    String uiLabel = this.labels.getUILabel(context);
    Assert.assertEquals("Extract ", uiLabel);
    EReference _class_Characteristics = VClipseTestUtilities.VCML_PACKAGE.getClass_Characteristics();
    RefactoringContext _create = RefactoringContext.create(findEntry, _class_Characteristics, RefactoringType.Extract);
    context = _create;
    String _uILabel = this.labels.getUILabel(context);
    uiLabel = _uILabel;
    Assert.assertEquals("Extract characteristics ", uiLabel);
  }
}
