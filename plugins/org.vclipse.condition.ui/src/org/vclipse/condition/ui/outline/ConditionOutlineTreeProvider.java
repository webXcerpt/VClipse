/*
* generated by Xtext
*/
package org.vclipse.condition.ui.outline;

import org.eclipse.jface.preference.IPreferenceStore;
import org.vclipse.vcml.ui.outline.VCMLOutlineTreeProvider;

import com.google.inject.Inject;

/**
 * customization of the default outline structure
 * 
 */
public class ConditionOutlineTreeProvider extends VCMLOutlineTreeProvider {

	@Inject
	public ConditionOutlineTreeProvider(IPreferenceStore preferenceStore) {
		super(preferenceStore);
	}
	
}
