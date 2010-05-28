/**
 * 
 */
package org.vclipse.vcml.diff;

import org.eclipse.emf.compare.diff.metamodel.DifferenceKind;
import org.eclipse.emf.ecore.EObject;

/**
 *	
 */
public interface IDiffFilter {

	/**
	 * @param parent
	 * @param object
	 * @param kind
	 * @return
	 */
	public boolean filter(EObject object, DifferenceKind kind);
}
