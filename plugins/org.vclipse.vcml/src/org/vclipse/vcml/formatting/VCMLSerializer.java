package org.vclipse.vcml.formatting;

import java.io.IOException;
import java.io.Writer;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.resource.SaveOptions;
import org.eclipse.xtext.serializer.impl.Serializer;
import org.vclipse.vcml.VCMLPlugin;
import org.vclipse.vcml.utils.ISapConstants;
import org.vclipse.vcml.vcml.ConditionSource;
import org.vclipse.vcml.vcml.ConstraintSource;
import org.vclipse.vcml.vcml.ProcedureSource;

public class VCMLSerializer extends Serializer {

	@Override
	public String serialize(EObject obj, SaveOptions options) {
		if(usePrettyPrinter()) { 
			return serialize(obj);
		} else {
			return super.serialize(obj, options);
		}
	}

	@Override
	public void serialize(EObject obj, Writer writer, SaveOptions options) throws IOException {
		writer.append(serialize(obj));
		writer.flush();
	}

	@Override
	public String serialize(EObject obj) {
		if(usePrettyPrinter()) {
			if(obj instanceof ConditionSource || obj instanceof ProcedureSource) {
				return new ProcedurePrettyPrinter().prettyPrint(obj);
			} else if (obj instanceof ConstraintSource) {
				return new ConstraintPrettyPrinter().prettyPrint(obj);
			} else {
				return new VCMLPrettyPrinter().prettyPrint(obj);
			}
		} else {
			return super.serialize(obj);
		}
	}

	// FIXME
	/*
	@Override
	public TreeConstructionReport serialize(EObject obj, OutputStream out, CompositeNode node, boolean format) throws IOException {
		if (usePrettyPrinter()) {
			out.write(serialize(obj).getBytes("UTF-8"));
			return new TreeConstructionReport() {
				public TreeConstructionDiagnostic getDiagnostic() {
					return null;
				}
				public boolean isSuccess() {
					return true;
				}
			};
		} else {
			return super.serialize(obj, out, node, format);
		}
	}
*/

	private boolean usePrettyPrinter() {
		IPreferencesService preferencesService = Platform.getPreferencesService();
		return preferencesService == null ? false : preferencesService.getBoolean(VCMLPlugin.PREFERENCES_ID, ISapConstants.USE_PRETTY_PRINTER, false, null);
	}
}
