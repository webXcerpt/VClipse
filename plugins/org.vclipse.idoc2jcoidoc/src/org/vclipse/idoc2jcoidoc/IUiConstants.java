/*******************************************************************************
 * Copyright (c) 2009 webXcerpt Software GmbH (http://www.webxcerpt.com).
 * All rights reserved.
 *******************************************************************************/
package org.vclipse.idoc2jcoidoc;

/**
 * 
 */
public interface IUiConstants {
	
	public static final String NUMBERS_PROVIDER = Activator.ID + ".senderType";
	public static final String RFC_FOR_UPS_NUMBERS = Activator.ID + ".rfcForUpsNumbers";
	public static final String RFC_FOR_IDOC_NUMBERS = Activator.ID + ".rfcForIDocsNumbers";
	public static final String UPS_PACKAGE_PREFIX = Activator.ID + ".upsPackagePrefix";
	/**
	 * Values for sender types
	 */
	public static final String TARGET_SYSTEM = Activator.ID + ".targetSystem";
	public static final String USER_INPUT = Activator.ID + ".userInput";
	
	/**
	 * Images
	 */
	public static final String SEND_IDOCS_IMAGE = Activator.ID + "sendIDocsActionImage";
	public static final String SEND_IDOCS_IMAGE_DISABLED = Activator.ID + "sendIDocsActionImageDisabled";
	public static final String IDOC_DOCUMENT_IMAGE = Activator.ID + "idocDocumentImage";
	public static final String IDOC_SEGMENT_IMAGE = Activator.ID + "idocSegmentImage";
	
	/**
	 * Partner preferences/properties
	 */
	public static final String PARTNER_TYPE = Activator.ID + ".partnerType";
	public static final String PARTNER_NUMBER = Activator.ID + ".partnerNumber";
}
