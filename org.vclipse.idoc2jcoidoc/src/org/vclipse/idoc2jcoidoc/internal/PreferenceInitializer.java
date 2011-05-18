/*******************************************************************************
 * Copyright (c) 2009 webXcerpt Software GmbH (http://www.webxcerpt.com).
 * All rights reserved.
 *******************************************************************************/
package org.vclipse.idoc2jcoidoc.internal;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.vclipse.idoc2jcoidoc.IUiConstants;

import com.google.inject.Inject;

/**
 * 
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer {

	/**
	 * Defaults
	 */
	private static final String DEFAULT_PARTNER_TYPE = "LS";
	private static final String DEFAULT_PARTNER_NUMBER = "CML";
	
	/**
	 * 
	 */
	private final IPreferenceStore preferenceStore;
	
	/**
	 * @param preferenceStore
	 */
	@Inject
	public PreferenceInitializer(IPreferenceStore preferenceStore) {
		this.preferenceStore = preferenceStore;
	}
	
	/**
	 * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#initializeDefaultPreferences()
	 */
	@Override
	public void initializeDefaultPreferences() {
		preferenceStore.putValue(IUiConstants.NUMBERS_PROVIDER, IUiConstants.TARGET_SYSTEM);
		preferenceStore.putValue(IUiConstants.RFC_FOR_UPS_NUMBERS, "CML_GET_UPS_NUMBER");
		preferenceStore.putValue(IUiConstants.RFC_FOR_IDOC_NUMBERS, "CML_GET_IDOC_NUMBERS");
		preferenceStore.putValue(IUiConstants.PARTNER_NUMBER, DEFAULT_PARTNER_NUMBER);
		preferenceStore.putValue(IUiConstants.PARTNER_TYPE, DEFAULT_PARTNER_TYPE);
	}
}
