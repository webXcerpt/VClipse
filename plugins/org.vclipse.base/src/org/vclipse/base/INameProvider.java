package org.vclipse.base;

import org.eclipse.emf.ecore.EObject;

import com.google.common.base.Function;

public interface INameProvider extends Function<EObject, String> {

	/**
	 * @return the name for the given object, <code>null</code> if this {@link INameProvider} is not
	 *         responsible or if the given object doesn't have qualified name.
	 */
	String getName(EObject obj);

	abstract class AbstractImpl implements INameProvider {
		public String apply(EObject from) {
			return getName(from);
		}
	}
}
