package org.vclipse.tests.refactoring.swtbot;

import com.google.inject.Injector;
import java.io.InputStream;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.vclipse.refactoring.utils.EntrySearch;
import org.vclipse.refactoring.utils.Extensions;
import org.vclipse.tests.SWTBotWorkspaceWorker;
import org.vclipse.tests.VClipseTestUtilities;
import org.vclipse.tests.refactoring.RefactoringInjectorProvider;
import org.vclipse.vcml.refactoring.VCMLRefactoring;

@RunWith(value = SWTBotJunit4ClassRunner.class)
@SuppressWarnings("all")
public class RegressionRefactoringTest extends SWTBotWorkspaceWorker {
  private VClipseTestUtilities resourcesLoader;
  
  private Extensions extensions;
  
  private VCMLRefactoring vcmlRefactoring;
  
  private EntrySearch search;
  
  public void before() {
    super.before();
    RefactoringInjectorProvider _refactoringInjectorProvider = new RefactoringInjectorProvider();
    final Injector injector = _refactoringInjectorProvider.getInjector();
    VClipseTestUtilities _instance = injector.<VClipseTestUtilities>getInstance(VClipseTestUtilities.class);
    this.resourcesLoader = _instance;
    Extensions _instance_1 = injector.<Extensions>getInstance(Extensions.class);
    this.extensions = _instance_1;
    VCMLRefactoring _instance_2 = injector.<VCMLRefactoring>getInstance(VCMLRefactoring.class);
    this.vcmlRefactoring = _instance_2;
    EntrySearch _instance_3 = injector.<EntrySearch>getInstance(EntrySearch.class);
    this.search = _instance_3;
    this.cleanWorkspace();
    this.createProject();
  }
  
  protected IProject createProject() {
    IProject _xblockexpression = null;
    {
      final IProject project = super.createProject();
      IFolder folder = this.createFolder(project, "car-dep");
      InputStream _inputStream = this.resourcesLoader.getInputStream("/refactoring/Refactoring/car-dep/CAR_SELECTION.cons");
      IFile file = this.createFile(folder, "CAR_SELECTION.cons", _inputStream);
      IFolder _createFolder = this.createFolder(project, "engine-dep");
      folder = _createFolder;
      InputStream _inputStream_1 = this.resourcesLoader.getInputStream("/refactoring/Refactoring/engine-dep/TYPE_SELECTION.cons");
      IFile _createFile = this.createFile(folder, "TYPE_SELECTION.cons", _inputStream_1);
      file = _createFile;
      InputStream _inputStream_2 = this.resourcesLoader.getInputStream("/refactoring/Refactoring/car.vcml");
      IFile _createFile_1 = this.createFile(project, "car.vcml", _inputStream_2);
      file = _createFile_1;
      InputStream _inputStream_3 = this.resourcesLoader.getInputStream("/refactoring/Refactoring/engine.vcml");
      IFile _createFile_2 = this.createFile(project, "engine.vcml", _inputStream_3);
      file = _createFile_2;
      this.bot.sleep(10000);
      _xblockexpression = (project);
    }
    return _xblockexpression;
  }
  
  @Test
  public void test() {
    this.bot.sleep(10000);
  }
}
