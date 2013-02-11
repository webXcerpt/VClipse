package org.vclipse.vcml.compare;

import com.google.common.base.Objects;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.io.ByteStreams;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.compare.DifferenceKind;
import org.eclipse.emf.compare.DifferenceSource;
import org.eclipse.emf.compare.Match;
import org.eclipse.emf.compare.diff.DiffBuilder;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.xtext.naming.IQualifiedNameProvider;
import org.eclipse.xtext.naming.QualifiedName;
import org.eclipse.xtext.util.StringInputStream;
import org.eclipse.xtext.xbase.lib.CollectionLiterals;
import org.eclipse.xtext.xbase.lib.Exceptions;
import org.vclipse.base.ImportUriExtractor;
import org.vclipse.base.compare.DefaultInputSupplier;
import org.vclipse.vcml.compare.FeatureFilter;
import org.vclipse.vcml.utils.DependencySourceUtils;
import org.vclipse.vcml.vcml.Dependency;
import org.vclipse.vcml.vcml.Import;
import org.vclipse.vcml.vcml.Option;
import org.vclipse.vcml.vcml.VCObject;
import org.vclipse.vcml.vcml.VcmlFactory;
import org.vclipse.vcml.vcml.VcmlModel;
import org.vclipse.vcml.vcml.VcmlPackage;

/**
 * Proccessor that is suited for changes handling on the resource level.
 * 
 * It means that it handles the import and option statements.
 */
@Singleton
@SuppressWarnings("all")
public class ResourceChangesProcessor extends DiffBuilder {
  protected VcmlModel newVcmlModel;
  
  protected EList<Import> newImports;
  
  protected EList<VCObject> newObjects;
  
  protected EList<Option> newOptions;
  
  protected boolean importsHandled;
  
  protected Set<String> seenObjects;
  
  protected VcmlPackage vcmlPackage;
  
  protected VcmlFactory vcmlFactory;
  
  private Multimap<Integer,Exception> compareErrors;
  
  @Inject
  protected ImportUriExtractor uriUtility;
  
  @Inject
  protected DependencySourceUtils sourceUtils;
  
  @Inject
  protected IQualifiedNameProvider nameProvider;
  
  @Inject
  protected IWorkspaceRoot root;
  
  @Inject
  protected FeatureFilter featureFilter;
  
  public void initialize(final VcmlModel vcmlModel) {
    this.vcmlPackage = VcmlPackage.eINSTANCE;
    this.vcmlFactory = VcmlFactory.eINSTANCE;
    this.newVcmlModel = vcmlModel;
    EList<Import> _imports = vcmlModel.getImports();
    this.newImports = _imports;
    EList<VCObject> _objects = vcmlModel.getObjects();
    this.newObjects = _objects;
    EList<Option> _options = vcmlModel.getOptions();
    this.newOptions = _options;
    HashSet<String> _newHashSet = CollectionLiterals.<String>newHashSet();
    this.seenObjects = _newHashSet;
    this.importsHandled = false;
    HashMultimap<Integer,Exception> _create = HashMultimap.<Integer, Exception>create();
    this.compareErrors = _create;
  }
  
  /**
   * Returns the errors.
   */
  public Multimap<Integer,Exception> getErrors() {
    return this.compareErrors;
  }
  
  public void referenceChange(final Match match, final EReference reference, final EObject value, final DifferenceKind kind, final DifferenceSource source) {
    final EObject left = match.getLeft();
    final EObject right = match.getRight();
    boolean _not = (!this.importsHandled);
    if (_not) {
      if ((left instanceof VcmlModel)) {
        EList<Import> _imports = ((VcmlModel) left).getImports();
        for (final Import import_ : _imports) {
          EClass _import = this.vcmlPackage.getImport();
          String _instanceClassName = _import.getInstanceClassName();
          String _importURI = import_.getImportURI();
          String _plus = (_instanceClassName + _importURI);
          boolean _contains = this.seenObjects.contains(_plus);
          boolean _not_1 = (!_contains);
          if (_not_1) {
            final Import importStmt = VcmlFactory.eINSTANCE.createImport();
            String _importURI_1 = import_.getImportURI();
            importStmt.setImportURI(_importURI_1);
            this.newImports.add(importStmt);
          }
        }
        Resource _eResource = left.eResource();
        Resource _eResource_1 = this.newVcmlModel.eResource();
        final String importUri = this.uriUtility.getImportUri(_eResource, _eResource_1);
        EClass _import_1 = this.vcmlPackage.getImport();
        String _instanceClassName_1 = _import_1.getInstanceClassName();
        String _plus_1 = (_instanceClassName_1 + importUri);
        boolean _contains_1 = this.seenObjects.contains(_plus_1);
        boolean _not_2 = (!_contains_1);
        if (_not_2) {
          final Import importStmt_1 = VcmlFactory.eINSTANCE.createImport();
          importStmt_1.setImportURI(importUri);
          this.newImports.add(importStmt_1);
        }
        this.importsHandled = true;
      }
    }
    this.reference(left, right, value, reference, kind, source);
  }
  
  protected void reference(final EObject left, final EObject right, final EObject value, final EReference reference, final DifferenceKind kind, final DifferenceSource source) {
    if ((left instanceof Dependency)) {
      this.dependencyChange(((Dependency) left), ((Dependency) right), kind, source);
    }
  }
  
  /**
   * Handle the dependency changes directly.
   */
  public void dependencyChange(final Dependency left, final Dependency right, final DifferenceKind kind, final DifferenceSource source) {
    boolean _equals = Objects.equal(left, null);
    if (_equals) {
      return;
    }
    boolean _and = false;
    boolean _equals_1 = Objects.equal(DifferenceKind.ADD, kind);
    if (!_equals_1) {
      _and = false;
    } else {
      boolean _equals_2 = Objects.equal(DifferenceSource.LEFT, source);
      _and = (_equals_1 && _equals_2);
    }
    if (_and) {
      try {
        InputStream _inputStream = this.sourceUtils.getInputStream(left);
        DefaultInputSupplier _defaultInputSupplier = new DefaultInputSupplier(_inputStream);
        final DefaultInputSupplier leftSupplier = _defaultInputSupplier;
        InputStream _xifexpression = null;
        boolean _equals_3 = Objects.equal(right, null);
        if (_equals_3) {
          StringInputStream _stringInputStream = new StringInputStream("");
          _xifexpression = _stringInputStream;
        } else {
          InputStream _inputStream_1 = this.sourceUtils.getInputStream(right);
          _xifexpression = _inputStream_1;
        }
        DefaultInputSupplier _defaultInputSupplier_1 = new DefaultInputSupplier(_xifexpression);
        final DefaultInputSupplier rightSupplier = _defaultInputSupplier_1;
        boolean _equal = ByteStreams.equal(leftSupplier, rightSupplier);
        boolean _not = (!_equal);
        if (_not) {
          QualifiedName _fullyQualifiedName = this.nameProvider.getFullyQualifiedName(left);
          final String name = _fullyQualifiedName.toString();
          boolean _contains = this.seenObjects.contains(name);
          boolean _not_1 = (!_contains);
          if (_not_1) {
            final Dependency depcopy = EcoreUtil.<Dependency>copy(left);
            this.newObjects.add(((VCObject) depcopy));
            final URI uri = this.sourceUtils.getSourceURI(depcopy);
            List<String> _segmentsList = uri.segmentsList();
            final List<String> projectName = _segmentsList.subList(1, 4);
            String _get = projectName.get(0);
            IProject _project = this.root.getProject(_get);
            String _get_1 = projectName.get(1);
            IFolder _folder = _project.getFolder(_get_1);
            String _get_2 = projectName.get(2);
            final IFolder newDepFolder = _folder.getFolder(_get_2);
            NullProgressMonitor _nullProgressMonitor = new NullProgressMonitor();
            final NullProgressMonitor monitor = _nullProgressMonitor;
            boolean _exists = newDepFolder.exists();
            boolean _not_2 = (!_exists);
            if (_not_2) {
              newDepFolder.create(true, true, monitor);
            }
            String _lastSegment = uri.lastSegment();
            final IFile newDepFile = newDepFolder.getFile(_lastSegment);
            boolean _exists_1 = newDepFile.exists();
            boolean _not_3 = (!_exists_1);
            if (_not_3) {
              InputStream _inputStream_2 = this.sourceUtils.getInputStream(left);
              newDepFile.create(_inputStream_2, true, monitor);
            }
            this.seenObjects.add(name);
          }
        }
      } catch (final Throwable _t) {
        if (_t instanceof Exception) {
          final Exception exception = (Exception)_t;
          this.compareErrors.put(Integer.valueOf(IStatus.ERROR), exception);
        } else {
          throw Exceptions.sneakyThrow(_t);
        }
      }
    }
  }
}
