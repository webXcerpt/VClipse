package org.vclipse.configscan.views;

import java.util.Collections;
import java.util.List;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider.IStyledLabelProvider;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.xtext.ui.editor.utils.TextStyle;
import org.eclipse.xtext.ui.label.StylerFactory;
import org.eclipse.xtext.util.PolymorphicDispatcher;
import org.eclipse.xtext.util.PolymorphicDispatcher.ErrorHandler;
import org.vclipse.configscan.ConfigScanImageHelper;
import org.vclipse.configscan.impl.model.TestCase;
import org.vclipse.configscan.impl.model.TestGroup;
import org.vclipse.configscan.impl.model.TestCase.Status;
import org.vclipse.configscan.impl.model.TestRun;

import com.google.inject.Inject;

public abstract class AbstractLabelProvider extends ColumnLabelProvider implements IStyledLabelProvider, ILabelDecorator {

	private final PolymorphicDispatcher<Object> textDispatcher = new PolymorphicDispatcher<Object>("text", 1, 1,
			Collections.singletonList(this), new ErrorHandler<Object>() {
				public Object handle(Object[] params, Throwable e) {
					return EMPTY;
				}
			});
	
	private final PolymorphicDispatcher<Object> imageDispatcher = new PolymorphicDispatcher<Object>("image", 1, 1,
			Collections.singletonList(this), new ErrorHandler<Object>() {
				public Object handle(Object[] params, Throwable e) {
					return null;
				}
			});
	
	private final PolymorphicDispatcher<Object> toolTipDispatcher = new PolymorphicDispatcher<Object>("toolTip", 1, 1,
			Collections.singletonList(this), new ErrorHandler<Object>() {
				public Object handle(Object[] params, Throwable e) {
					return null;
				}
			});
	
	private final PolymorphicDispatcher<Object> styledTextDispatcher = new PolymorphicDispatcher<Object>("styledText", 1, 1,
			Collections.singletonList(this), new ErrorHandler<Object>() {
				public Object handle(Object[] params, Throwable e) {
					return new StyledString(EMPTY);
				}
			});
	
	private final PolymorphicDispatcher<Object> decorateImageDispatcher = new PolymorphicDispatcher<Object>("decoratedImage", 2, 2,
			Collections.singletonList(this), new ErrorHandler<Object>() {
				public Object handle(Object[] params, Throwable e) {
					return new StyledString(EMPTY);
				}
			});
	
		
	protected static final String EMPTY = "";

	protected ConfigScanImageHelper imageHelper;
	
	protected TextStyle failureStyle;
	
	protected TextStyle successStyle;
	
	public AbstractLabelProvider() {
		failureStyle = new TextStyle();
		failureStyle.setColor(new RGB(0xcc, 0, 0));
				
		successStyle = new TextStyle();
		successStyle.setColor(new RGB(0x32, 0x92, 0));
	}
	
	@Inject
	public void setImageHelper(ConfigScanImageHelper imageHelper) {
		this.imageHelper = imageHelper;
	}
	
	@Override
	public final Image decorateImage(Image image, Object element) {
		return (Image)decorateImageDispatcher.invoke(image, element);
	}

	@Override
	public final String decorateText(String text, Object element) {
		return null;
	}

	@Override
	public final StyledString getStyledText(Object element) {
		return (StyledString)styledTextDispatcher.invoke(element);
	}

	@Override
	public final Image getImage(Object element) {
		return (Image)imageDispatcher.invoke(element);
	}
	
	@Override
	public final String getText(Object element) {
		return (String)textDispatcher.invoke(element);
	}
	
	@Override
	public final String getToolTipText(Object element) {
		return (String)toolTipDispatcher.invoke(element);
	}

	protected EObject getReferencedEObject(Object element) {
		if(element instanceof TestCase) {
			TestCase testCase = (TestCase)element;
			EObject testModel = ((TestRun)testCase.getRoot()).getTestModel();
			if(testModel != null) {
				URI sourceUri = testCase.getSourceURI();
				if(sourceUri != null) {
					return testModel.eResource().getResourceSet().getEObject(sourceUri, true);							
				}
			}
		}
		return null;
	}
	
	protected StyledString getStatistics(TestCase testCase) {
		if(testCase instanceof TestGroup) {
			TestGroup testGroup = (TestGroup)testCase;
			List<TestCase> testCases = testGroup.getTestCases();
			if(!testCases.isEmpty()) {
				int numberOfTestCases = testCases.size();
				int failures = 0;
				for(TestCase childTestCase : testCases) {
					if(Status.FAILURE == childTestCase.getStatus()) {
						failures++;
					}
				}
				StyledString result = new StyledString("  ");
				StylerFactory stylerFactory = new StylerFactory();
				int successes = numberOfTestCases - failures;
				if (successes > 0) {
					result.append(stylerFactory.createFromXtextStyle(successes + " success" + (successes > 1 ? "es" : ""), successStyle));
				}
				if (successes > 0 && failures > 0) {
					result.append(", ");
				}
				if (failures > 0) {
					result.append(new StylerFactory().createFromXtextStyle(failures + " failure" + (failures > 1 ? "s" : ""), failureStyle));
				}
				return result;
			}
		}
		return new StyledString(EMPTY);
 	}
}
