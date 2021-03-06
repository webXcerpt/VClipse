/*******************************************************************************
 * Copyright (c) 2010 - 2013 webXcerpt Software GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *  
 * Contributors:
 *     	webXcerpt Software GmbH - initial creator
 * 		www.webxcerpt.com
 ******************************************************************************/
package org.vclipse.configscan.views.labeling;

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
import org.vclipse.base.ui.util.ClasspathAwareImageHelper;
import org.vclipse.configscan.impl.model.TestCase;
import org.vclipse.configscan.impl.model.TestCase.Status;
import org.vclipse.configscan.impl.model.TestGroup;
import org.vclipse.configscan.impl.model.TestRun;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

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
	
	private class Statistics {
		int testCaseAmount = 0;
		int failuresAmount = 0;
	}
	
	protected static final String EMPTY = "";

	protected ClasspathAwareImageHelper imageHelper;
	
	protected TextStyle failureStyle;
	
	protected TextStyle successStyle;
	
	public AbstractLabelProvider() {
		failureStyle = new TextStyle();
		failureStyle.setColor(new RGB(0xcc, 0, 0));
				
		successStyle = new TextStyle();
		successStyle.setColor(new RGB(0x32, 0x92, 0));
	}
	
	@Inject
	public void setImageHelper(ClasspathAwareImageHelper imageHelper) {
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
			Statistics statistics = new Statistics();
			computeStatistics(testGroup, statistics);
			StyledString result = new StyledString("  ");
			StylerFactory stylerFactory = new StylerFactory();
			int successes = statistics.testCaseAmount - statistics.failuresAmount;
			if(successes > 0) {
				result.append(stylerFactory.createFromXtextStyle(successes + " success" + (successes > 1 ? "es" : ""), successStyle));
			}
			if(successes > 0 && statistics.failuresAmount > 0) {
				result.append(", ");
			}
			if(statistics.failuresAmount > 0) {
				result.append(new StylerFactory().createFromXtextStyle(statistics.failuresAmount + " failure" + (statistics.failuresAmount > 1 ? "s" : ""), failureStyle));
			}
			
			statistics = new Statistics();
			getStatisticsFromHierarchy(testGroup, statistics);
			successes = statistics.testCaseAmount - statistics.failuresAmount;
			if(successes > 0 && statistics.failuresAmount > 0) {
				result.append("    ConfigScan : ");
				result.append(stylerFactory.createFromXtextStyle(successes + " success" + (successes > 1 ? "es" : ""), successStyle));
				result.append(", ");
				result.append(stylerFactory.createFromXtextStyle(statistics.failuresAmount + " failure" + (statistics.failuresAmount > 1 ? "s" : ""), failureStyle));
			}
			return result;
		}
		return new StyledString(EMPTY);
 	}
	
	private void computeStatistics(TestGroup testGroup, Statistics statistics) {
		List<TestCase> testCases = testGroup.getTestCases();
		statistics.testCaseAmount = testCases.size();
		for(TestCase childTestCase : testCases) {
			if(Status.FAILURE == childTestCase.getStatus()) {
				statistics.failuresAmount++;
			}
		}
	}
	
	private void getStatisticsFromHierarchy(TestGroup testGroup, Statistics statistics) {
		for(TestCase testCase : testGroup.getTestCases()) {
			if(testCase instanceof TestGroup) {
				Statistics childStatistics = new Statistics();
				computeStatistics((TestGroup)testCase, childStatistics);
				statistics.testCaseAmount += childStatistics.testCaseAmount;
				statistics.failuresAmount += childStatistics.failuresAmount;
			}
		}
	}

	protected String extractText(NamedNodeMap nodes, String textToShow, String attribute) {
		StringBuffer strBuffer = new StringBuffer();
		Node namedItem = nodes.getNamedItem(attribute);
		if(namedItem == null) {
			return null;
		}
		strBuffer.append(textToShow).append(namedItem.getNodeValue());
		return strBuffer.toString();
	}
}
