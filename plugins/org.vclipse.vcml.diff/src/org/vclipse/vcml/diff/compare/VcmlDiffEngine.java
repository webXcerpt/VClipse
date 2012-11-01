package org.vclipse.vcml.diff.compare;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.emf.compare.FactoryException;
import org.eclipse.emf.compare.diff.engine.GenericDiffEngine;
import org.eclipse.emf.compare.diff.metamodel.DiffFactory;
import org.eclipse.emf.compare.diff.metamodel.DiffGroup;
import org.eclipse.emf.compare.diff.metamodel.DiffModel;
import org.eclipse.emf.compare.diff.metamodel.ModelElementChangeLeftTarget;
import org.eclipse.emf.compare.match.metamodel.Match2Elements;
import org.eclipse.emf.compare.match.metamodel.MatchModel;
import org.eclipse.emf.ecore.EObject;
import org.vclipse.base.BasePlugin;
import org.vclipse.vcml.utils.DependencySourceUtils;
import org.vclipse.vcml.vcml.Dependency;

import com.google.common.collect.Lists;
import com.google.common.io.ByteStreams;
import com.google.common.io.InputSupplier;
import com.google.inject.Inject;

public class VcmlDiffEngine extends GenericDiffEngine {

	private Logger logger = Logger.getLogger(VcmlDiffEngine.class);
	
	@Inject
	private DependencySourceUtils sourceUtils;
	
	private List<Dependency> dependencies;
	
	class DefaultInputSupplier implements InputSupplier<InputStream> {
		
		private InputStream stream;
		
		public DefaultInputSupplier(InputStream stream) {
			this.stream = stream;
		}

		@Override
		public InputStream getInput() throws IOException {
			return stream;
		}
	}
	
	public VcmlDiffEngine() {
		dependencies = Lists.newArrayList();
	}
	
	@Override
	public DiffModel doDiff(MatchModel match) {
		dependencies.clear();
		return super.doDiff(match);
	}
	
	public List<Dependency> getChangedDependencies() {
		return dependencies;
	}

	@Override
	protected void checkForDiffs(DiffGroup current, Match2Elements match) throws FactoryException {
		EObject leftElement = match.getLeftElement();
		EObject rightElement = match.getRightElement();
		if(leftElement instanceof Dependency && rightElement instanceof Dependency && sourceUtils.hasBody((Dependency)leftElement) && sourceUtils.hasBody((Dependency)rightElement)) {
			try {
				Dependency leftDependency = (Dependency)leftElement;
				InputStream streamLeft = sourceUtils.getInputStream(leftDependency);
				InputStream streamRight = sourceUtils.getInputStream((Dependency)rightElement);
				if(!ByteStreams.equal(new DefaultInputSupplier(streamLeft), new DefaultInputSupplier(streamRight))) {
					ModelElementChangeLeftTarget leftChange = DiffFactory.eINSTANCE.createModelElementChangeLeftTarget();
					leftChange.setLeftElement(leftElement);
					leftChange.setRightParent(rightElement.eContainer());
					current.getSubDiffElements().add(leftChange);
					dependencies.add(leftDependency);
				}
			} catch(IOException exception) {
				logger.error("Error during diff engine execution. " + exception.getMessage());
				BasePlugin.log(exception.getMessage(), exception);
			}
		}
		super.checkForDiffs(current, match);
	}
}
