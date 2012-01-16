package org.vclipse.configscan.views;

import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider.IStyledLabelProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.ui.progress.PendingUpdateAdapter;
import org.eclipse.xtext.ui.editor.utils.TextStyle;
import org.eclipse.xtext.ui.label.StylerFactory;
import org.eclipse.xtext.util.Pair;
import org.vclipse.configscan.ConfigScanImageHelper;
import org.vclipse.configscan.IConfigScanImages;
import org.vclipse.configscan.IConfigScanXMLProvider;
import org.vclipse.configscan.extension.ExtensionPointReader;
import org.vclipse.configscan.impl.model.TestCase;
import org.vclipse.configscan.impl.model.TestCase.Status;
import org.vclipse.configscan.impl.model.TestRunAdapter;
import org.vclipse.configscan.utils.TestCaseUtility;

import com.google.inject.Inject;

public final class LabelProvider extends ColumnLabelProvider implements IStyledLabelProvider {

	private TestCaseUtility testCaseUtility;
	
	private ConfigScanImageHelper imageHelper;
	
	private static final String EMPTY = "";
	
	private boolean shouldDelegate;
	
	private Map<String, Pair<IConfigScanXMLProvider, ILabelProvider>> extensions;
	
	private TextStyle failureStyle;
	
	private TextStyle successStyle;
	
	@Inject
	public LabelProvider(ConfigScanImageHelper imageHelper, ExtensionPointReader reader, TestCaseUtility utility) {
		extensions = reader.getExtensions();
		this.imageHelper = imageHelper;
		this.testCaseUtility = utility;
		
		failureStyle = new TextStyle();
		failureStyle.setColor(new RGB(0xcc, 0, 0));
				
		successStyle = new TextStyle();
		successStyle.setColor(new RGB(0x32, 0x92, 0));
	}
	
	public void enableExtension(boolean delegate) {
		this.shouldDelegate = delegate;
	}
	
	@Override
	public StyledString getStyledText(Object element) {
		if(element instanceof TestCase && !shouldDelegate) {
			TestCase testCase = (TestCase)element;
			return new StyledString(testCase.getTitle()).append(getStatistics(testCase));
		} else {
			return new StyledString(getText(element));
		}
	}
	
	@Override
	public String getText(Object object) {
		if(object instanceof TestRunAdapter) {
			return ((TestRunAdapter)object).getLabel(null);
		} else if(object instanceof TestCase) {
			if(shouldDelegate) {
				ILabelProvider labelProvider = getLabelProviderExtension((TestCase)object);
				if(labelProvider != null) {
					return labelProvider.getText(object);
				}
			}
			return ((TestCase)object).getTitle();
		} else if(object instanceof PendingUpdateAdapter) {
			return ((PendingUpdateAdapter)object).getLabel(object);
		}
		return EMPTY;
	}
	
	@Override
	public Image getImage(Object object) {
		if(object instanceof TestRunAdapter) {
			return imageHelper.getImage(((TestRunAdapter)object).getImageDescriptor(null));
		} else if(object instanceof TestCase) {
			TestCase testCase = (TestCase)object;
			if(shouldDelegate) {
				ILabelProvider labelProvider = getLabelProviderExtension(testCase);
				if(labelProvider != null) {
					return labelProvider.getImage(object);
				}
			} else {
				return testCase.getStatus() == Status.SUCCESS 
						? imageHelper.getImage(IConfigScanImages.SUCCESS) 
								: imageHelper.getImage(IConfigScanImages.ERROR);
			}
		}
		return null;
	}

	@Override
	public int getToolTipDisplayDelayTime(Object object) {
		return 500;
	}
	
	@Override
	public int getToolTipTimeDisplayed(Object object) {
 		return 10000;
 	}
	
 	public String getToolTipText(Object object) {
 		if(object instanceof TestCase) {
 			TestCase testCase = (TestCase)object;
 			if(testCase.getAdapter(TestRunAdapter.class) == null) {
  				ILabelProvider labelProviderExtension = getLabelProviderExtension(testCase);
  				String[] split = testCase.getTitle().split("on");
  				if(split.length == 2) {
  					
  				}
 	 			String tooltip = "ConfigScan XML LOG: " + split[0];
// 	 			NamedNodeMap namedNodeMap = inputElement.getAttributes();
// 	 			for(int i = 0; i < namedNodeMap.getLength(); i++) {
// 	 				Node node = namedNodeMap.item(i);
// 	 				tooltip += " " + node.getNodeName() + "=\"" + node.getNodeValue() + "\"";
// 	 			}
 	 			URI uri = testCase.getSourceUri();
 	 			TestCase root = testCaseUtility.getRoot(testCase);
 	 			EObject testModel = ((TestRunAdapter)root.getAdapter(TestRunAdapter.class)).getTestModel();
 	 			EObject eObject = testModel.eResource().getResourceSet().getEObject(uri, true);
 	 			if(eObject != null && shouldDelegate && labelProviderExtension != null) {
 	 				tooltip += "\n" + labelProviderExtension.getText(eObject);
 	 			}
 	 			return tooltip;
 			}
 		}
 		return null;
 	}

 	protected StyledString getStatistics(TestCase testCase) {
		List<TestCase> children = testCase.getChildren();
		if(!children.isEmpty()) {
			int failures = 0;
			for(TestCase childTestCase : testCase.getChildren()) {
				if(Status.FAILURE == childTestCase.getStatus()) {
					failures++;
				}
			}
			
			int numberOfTestCases = children.size();
			StyledString numberOfTests = new StyledString("     Number of tests = { " + numberOfTestCases + " } ");
			StylerFactory stylerFactory = new StylerFactory();
			numberOfTests.append(stylerFactory.createFromXtextStyle("Success = { " + (numberOfTestCases - failures) + " } ", successStyle));
			numberOfTests.append(new StylerFactory().createFromXtextStyle("Failures = { " + failures + " } ", failureStyle));
			return numberOfTests;
		}
		return new StyledString();
 	}
 	
 	private boolean canHandleExtension(TestCase testObject, String targetExtension) {
 		TestCase testCase = testCaseUtility.getRoot(testObject);
 		if(testCase == null) {
 			return false;
 		}
 		Object adapter = testCase.getAdapter(TestRunAdapter.class);
 		if(adapter != null) {
 			TestRunAdapter testRun = (TestRunAdapter)adapter;
 			String fileExtension = testRun.getTestModel().eResource().getURI().fileExtension();
 			return targetExtension.equals(fileExtension); 			
 		}
 		return false;
 	}
 	
 	private ILabelProvider getLabelProviderExtension(TestCase testCase) {
 		for(String fileExtension : extensions.keySet()) {
			if(canHandleExtension(testCase, fileExtension)) {
				return extensions.get(fileExtension).getSecond();
			}
		}
 		return null;
 	}

}
