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
package org.vclipse.vcml.ui.labeling;

import org.eclipse.emf.edit.ui.provider.AdapterFactoryLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.swt.graphics.Image;
import org.vclipse.vcml.utils.DescriptionHandler;
import org.vclipse.vcml.utils.VcmlUtils;
import org.vclipse.vcml.vcml.BOMItem;
import org.vclipse.vcml.vcml.BillOfMaterial;
import org.vclipse.vcml.vcml.CharacteristicGroup;
import org.vclipse.vcml.vcml.CharacteristicOrValueDependencies;
import org.vclipse.vcml.vcml.CharacteristicValue;
import org.vclipse.vcml.vcml.ConfigurationProfile;
import org.vclipse.vcml.vcml.ConfigurationProfileEntry;
import org.vclipse.vcml.vcml.Constraint;
import org.vclipse.vcml.vcml.DateType;
import org.vclipse.vcml.vcml.DependencyNet;
import org.vclipse.vcml.vcml.Description;
import org.vclipse.vcml.vcml.Documentation;
import org.vclipse.vcml.vcml.FormattedDocumentationBlock;
import org.vclipse.vcml.vcml.InterfaceDesign;
import org.vclipse.vcml.vcml.Language;
import org.vclipse.vcml.vcml.Material;
import org.vclipse.vcml.vcml.MultiLanguageDescription;
import org.vclipse.vcml.vcml.MultiLanguageDescriptions;
import org.vclipse.vcml.vcml.MultipleLanguageDocumentation;
import org.vclipse.vcml.vcml.MultipleLanguageDocumentation_LanguageBlock;
import org.vclipse.vcml.vcml.NumberListEntry;
import org.vclipse.vcml.vcml.NumericCharacteristicValue;
import org.vclipse.vcml.vcml.NumericInterval;
import org.vclipse.vcml.vcml.NumericLiteral;
import org.vclipse.vcml.vcml.NumericType;
import org.vclipse.vcml.vcml.Option;
import org.vclipse.vcml.vcml.Precondition;
import org.vclipse.vcml.vcml.Procedure;
import org.vclipse.vcml.vcml.Row;
import org.vclipse.vcml.vcml.SelectionCondition;
import org.vclipse.vcml.vcml.SymbolicType;
import org.vclipse.vcml.vcml.VariantFunction;
import org.vclipse.vcml.vcml.VariantTable;
import org.vclipse.vcml.vcml.VariantTableContent;
import org.vclipse.vcml.vcml.util.VcmlSwitch;

import com.google.inject.Inject;

/**
 * Provides labels for a EObjects.
 * 
 * see http://www.eclipse.org/Xtext/documentation/latest/xtext.html#labelProvider
 */
public class VCMLLabelProvider extends AbstractVClipseLabelProvider {

	@Inject
	public VCMLLabelProvider(AdapterFactoryLabelProvider delegate) {
		super(delegate);
	}

	public String image(BillOfMaterial element) {
		return "b_slis.gif";
	}
	
	public String image(BOMItem element) {
		return "b_slip.gif";
	}
	
	public String image(CharacteristicGroup element) {
		return "b_aboa.gif";
	}

	public String image(CharacteristicOrValueDependencies element) {
		return "b_rela.gif";
	}

	public String image(CharacteristicValue element) {
		return "b_kons.gif";
	}
	
	public String image(ConfigurationProfile element) {
		return "b_conf.gif";
	}

	public Image image(ConfigurationProfileEntry element) {
		return getImage(element.getDependency());
	}
	
	public String image(Constraint element) {
		return "snapgr.gif";
	}
	
	public String image(DependencyNet element) {
		return "magrid.gif";
	}
	
	public String image(Description element) {
		return "b_text.gif";
	}
	
	public String image(Documentation element) {
		return "b_anno.gif";
	}
	
	public String image(InterfaceDesign element) {
		return "bwvisu.gif";
	}
	
	public String image(Material element) {
		return "b_matl.gif";
	}
	
	public String image(MultiLanguageDescription element) {
		return languageIcon(element.getLanguage());
	}
	
	public String image(MultipleLanguageDocumentation_LanguageBlock element) {
		return languageIcon(element.getLanguage());
	}
	
	public String image(Option ele) {
		return "cog.png";
	}
	
	public String image(Precondition element) {
		return "b_mbed.gif";
	}
	
	public String image(Procedure element) {
		return "b_bwfo.gif";
	}
	
	public String image(SelectionCondition element) {
		return "b_bedi.gif";
	}

	public String image(VariantFunction element) {
		return "b_abal.gif";
	}

	public String image(VariantTable element) {
		return "dbtabl.gif";
	}

	private String languageIcon (Language language) {
		return language.getName().toLowerCase() + ".png";
	}
	
	public String text(BillOfMaterial element) {
		return "BOM";
	}
	
	public StyledString text(BOMItem element) {
		return new StyledString(element.getItemnumber() + " ", StyledString.COUNTER_STYLER).append(getStyledText(element.getMaterial()));
	}
	
	public StyledString text(CharacteristicGroup element) {
		return createStyledString(element.getName(), element.getDescription());
	}
	
	public String text(CharacteristicOrValueDependencies element) {
		return "dependencies";
	}
	
	public StyledString text(ConfigurationProfileEntry element) {
		return new StyledString(element.getSequence() + " ", StyledString.COUNTER_STYLER).append(getStyledText(element.getDependency()));
	}
	
	public StyledString text(Constraint element) {
		return createStyledString(element.getName(), element.getDescription());
	}
	
	public StyledString text(DependencyNet element) {
		return createStyledString(element.getName(), element.getDescription());
	}
	
	public String text(FormattedDocumentationBlock element) {
		String format = element.getFormat();
		return (format==null ? "" : (format + " ")) + element.getValue();
	}
	
	public StyledString text(Material element) {
		return createStyledString(element.getName(), element.getDescription());
	}

	public String text(MultiLanguageDescription element) {
		return element.getLanguage() + " " + element.getValue();
	}

	public String text(MultiLanguageDescriptions element) {
		final StringBuffer label = new StringBuffer();
		new DescriptionHandler() {
			private Language defaultLanguage = VcmlUtils.getDefaultLanguage(); 
			@Override
			public void handleSingleDescription(Language language, String value) {
				int length = label.length();
				if (length==0 || defaultLanguage.equals(language)) {
					label.delete(0, length);
					label.append(value);
				}
			}
		}.handleDescription(element);
		return label.toString();
	}

	public String text(MultipleLanguageDocumentation element) {
		final StringBuffer label = new StringBuffer();
		new VcmlSwitch<Object>() {
			private Language defaultLanguage = VcmlUtils.getDefaultLanguage(); 
			@Override
			public Object caseFormattedDocumentationBlock(
					FormattedDocumentationBlock object) {
				label.append(object.getValue());
				label.append(' ');
				return this;
			}
			@Override
			public Object caseMultipleLanguageDocumentation(
					MultipleLanguageDocumentation object) {
				for(MultipleLanguageDocumentation_LanguageBlock lb : object.getLanguageblocks()) {
					doSwitch(lb);
				}
				return this;
			}
			@Override
			public Object caseMultipleLanguageDocumentation_LanguageBlock(
					MultipleLanguageDocumentation_LanguageBlock object) {
				int length = label.length();
				if (length==0 || defaultLanguage.equals(object.getLanguage())) {
					label.delete(0, length);
					for(FormattedDocumentationBlock fdb : object.getFormattedDocumentationBlocks()) {
						doSwitch(fdb);
					}
				}
				return this;
			}
		}.doSwitch(element);
		return label.toString();
	}

	public String text(NumericType element) {
		return "NUM " + element.getNumberOfChars() + "." + element.getDecimalPlaces();
	}
	
	public String text(NumericCharacteristicValue value) {
		NumberListEntry entry = value.getEntry();
		if(entry instanceof NumericLiteral) {
			return ((NumericLiteral)entry).getValue();
		}
		if(entry instanceof NumericInterval) {
			NumericInterval interval = (NumericInterval)entry;
			return interval.getLowerBound() + " ... " + interval.getUpperBound();
		}
		return "";
	}

	public StyledString text(Option option) {
		return new StyledString(option.getName().getLiteral()).append(" : " + option.getValue(), StyledString.DECORATIONS_STYLER);
	}
	
	public StyledString text(Precondition element) {
		return createStyledString(element.getName(), element.getDescription());
	}
	
	public StyledString text(Procedure element) {
		return createStyledString(element.getName(), element.getDescription());
	}

	public StyledString text(SelectionCondition element) {
		return createStyledString(element.getName(), element.getDescription());
	}

	public String text(SymbolicType element) {
		return "CHAR " + element.getNumberOfChars();
	}

	public String text(DateType element) {
		return "DATE";
	}

	public StyledString text(VariantFunction element) {
		return createStyledString(element.getName(), element.getDescription());
	}
	
	public StyledString text(VariantTable element) {
		return createStyledString(element.getName(), element.getDescription());
	}
	
	public StyledString text(VariantTableContent element) {
		return createStyledString(element.getTable().getName() + " (content)", element.getDescription());
	}
	
	public StyledString text(Row row) {
		return createStyledString("row", null);
	}
}
