package org.vclipse.vcml.formatting;

import java.util.List;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.xtext.EcoreUtil2;
import org.vclipse.vcml.VCMLPlugin;
import org.vclipse.vcml.utils.ISapConstants;
import org.vclipse.vcml.vcml.Characteristic;
import org.vclipse.vcml.vcml.CharacteristicValue;
import org.vclipse.vcml.vcml.Class;
import org.vclipse.vcml.vcml.Constraint;
import org.vclipse.vcml.vcml.DateCharacteristicValue;
import org.vclipse.vcml.vcml.DependencyNet;
import org.vclipse.vcml.vcml.InterfaceDesign;
import org.vclipse.vcml.vcml.Material;
import org.vclipse.vcml.vcml.NumericCharacteristicValue;
import org.vclipse.vcml.vcml.Precondition;
import org.vclipse.vcml.vcml.Procedure;
import org.vclipse.vcml.vcml.SelectionCondition;
import org.vclipse.vcml.vcml.VariantFunction;
import org.vclipse.vcml.vcml.VariantTable;
import org.vclipse.vcml.vcml.VcmlFactory;
import org.vclipse.vcml.vcml.VcmlPackage;
import org.vclipse.vcml.vcml.util.VcmlSwitch;

import de.uka.ilkd.pp.DataLayouter;
import de.uka.ilkd.pp.NoExceptions;
import de.uka.ilkd.pp.StringBackend;

abstract class DefaultPrettyPrinter extends VcmlSwitch<DataLayouter<NoExceptions>> {

	protected static final int INDENTATION = 2;
	protected static final int LINE_WIDTH = 70; // 72 is allowed in SAP, we reduce by 1 to be able to append punctuation
	
	protected int lineLength = 70;
	
	protected DataLayouter<NoExceptions> layouter;
	
	protected static VcmlPackage VCMLPACKAGE = VcmlPackage.eINSTANCE;
	
	protected static VcmlFactory VCML = VcmlFactory.eINSTANCE;
	
	public static Pattern idPattern = Pattern.compile("[a-zA-Z_][a-zA-Z_0-9]*");
	
	protected StringBuilder stringBuilder;
	
	public void initialize() {
		stringBuilder = new StringBuilder();
		IPreferencesService preferencesService = Platform.getPreferencesService();
		if(preferencesService != null) {
			lineLength = preferencesService.getInt(VCMLPlugin.PREFERENCES_ID, ISapConstants.PP_LINE_LENGTH, lineLength, null);
		}
		StringBackend stringBackend = new StringBackend(stringBuilder, lineLength);
		layouter = new DataLayouter<NoExceptions>(stringBackend, INDENTATION);
	}
	
	protected boolean hasBody(Characteristic object) {
		return object.getDescription()!=null
		|| object.getDocumentation()!=null;
	}
	
	protected boolean hasBody(CharacteristicValue object) {
		return object.getDescription() != null 
		|| object.getDocumentation() != null
		|| object.getDependencies() != null;
	}

	protected boolean hasBody(NumericCharacteristicValue object) {
		return object.getDocumentation() != null
		|| object.getDependencies() != null;
	}

	protected boolean hasBody(DateCharacteristicValue object) {
		return object.getDocumentation() != null
		|| object.getDependencies() != null;
	}

	protected boolean hasBody(Class object) {
		return object.getDescription()!=null;
	}
	
	protected boolean hasBody(Constraint object) {
		return object.getDescription()!=null
		|| object.getDocumentation()!=null;
	}
	
	protected boolean hasBody(DependencyNet object) {
		return object.getDescription()!=null
		|| object.getDocumentation()!=null;
	}
	
	protected boolean hasBody(Material object) {
		return object.getDescription()!=null
		|| !object.getBillofmaterials().isEmpty()
		|| !object.getClassifications().isEmpty()
		|| !object.getConfigurationprofiles().isEmpty();
	}
	
	protected boolean hasBody(Precondition object) {
		return object.getDescription()!=null
		|| object.getDocumentation()!=null;
	}
	
	protected boolean hasBody(Procedure object) {
		return object.getDescription()!=null
		|| object.getDocumentation()!=null;
	}
	
	protected boolean hasBody(SelectionCondition object) {
		return object.getDescription()!=null
		|| object.getDocumentation()!=null;
	}
	
	protected boolean hasBody(VariantFunction object) {
		return object.getDescription()!=null;
	}
	
	protected boolean hasBody(VariantTable object) {
		return object.getDescription()!=null;
	}
	
	protected boolean hasBody(InterfaceDesign object) {
		return !object.getCharacteristicGroups().isEmpty();
	}
	
	
	public void printNullsafe(Object object) {
		layouter.print(object==null ? "null" : object);
	}
	
	public void printCrossReference(EObject object, EReference reference, EAttribute attribute) {
		EObject referenceValue = (EObject)object.eGet(reference);
		printCrossReference(object, referenceValue, reference, attribute);
	}
	
	public void printCrossReference(EObject context, EObject object, EReference reference, EAttribute attribute) {
		if(object == null) {
			if(context == null) {
				printNullsafe("###UNKNOWN###");				
			} else {
				Object value = context.eGet(reference);
				if(value instanceof EObject) {
					printNullsafe(value);
				} else if(value instanceof List<?>) {
					for(Object entry : (List<?>)value) {
						printNullsafe(entry);
					}
				}
			}
		} else {
			Object value = object.eGet(attribute);
			if(value == null) {
				if(context.eIsProxy()) {
					EcoreUtil2.resolve(context, context.eResource());
				}
				printNullsafe("###UNKNOWN###");
			} else {
				String linkText = value.toString();
				if(VcmlPackage.Literals.CLASS == reference.getEReferenceType()) {
					printNullsafe(linkText);
				} else {
					printNullsafe(asSymbol(linkText));
				}
			}
		}
	}
	
	public String symbolName(String theString) {
		return "'" + theString + "'";
	}
	
	public String asSymbol(String theString) {
		if(theString == null) {
			return null;
		} else if(idPattern.matcher(theString).matches()) {
			return theString;
		} else {
			return symbolName(theString);
		}
	}
}
