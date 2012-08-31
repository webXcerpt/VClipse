package org.vclipse.vcml.formatting;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.xtext.nodemodel.ICompositeNode;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;
import org.eclipse.xtext.resource.SaveOptions;
import org.eclipse.xtext.serializer.impl.Serializer;
import org.eclipse.xtext.util.ReplaceRegion;
import org.vclipse.vcml.vcml.ConditionSource;
import org.vclipse.vcml.vcml.ConstraintSource;
import org.vclipse.vcml.vcml.ProcedureSource;

import com.google.inject.Inject;

public class VCMLSerializer extends Serializer {

	@Inject
	private VCMLPrettyPrinter prettyPrinter;
	
	@Override
	public String serialize(EObject object, SaveOptions options) {
		String text = getDependencyObjectText(object);
		return text.isEmpty() ? prettyPrinter.prettyPrint(object) : text;
	}
	
	@Override
	public ReplaceRegion serializeReplacement(EObject object, SaveOptions options) {
		String text = getDependencyObjectText(object);
		if(text.isEmpty()) {
			return super.serializeReplacement(object, options);
		} else {
			ICompositeNode node = NodeModelUtils.findActualNodeFor(object);
			int offset = node.getOffset();
			int length = node.getLength();
			return new ReplaceRegion(offset, length, text);
		}
	}
	
	private String getDependencyObjectText(EObject object) {
		EObject rootContainer = EcoreUtil.getRootContainer(object);
		if(object instanceof ConstraintSource || rootContainer instanceof ConstraintSource) {
			return new ConstraintPrettyPrinter().prettyPrint(object);
		} else if(object instanceof ConditionSource || 
				rootContainer instanceof ConditionSource || 
					object instanceof ProcedureSource ||
						rootContainer instanceof ProcedureSource) {
			return new ProcedurePrettyPrinter().prettyPrint(object);
		}
		return "";
	}
}
