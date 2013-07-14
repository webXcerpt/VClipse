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
package org.vclipse.idoc2jcoidoc;

/**
 * 
 */
public interface IUiConstants {
	
	public static final String NUMBERS_PROVIDER = IDoc2JCoIDocPlugin.ID + ".senderType";
	public static final String NUMBERS_VIA_RFC = IDoc2JCoIDocPlugin.ID + ".numbersViaRfc";
	public static final String RFC_FOR_UPS_NUMBERS = "RfcSetting_UPS_NUMBER";
	public static final String RFC_FOR_IDOC_NUMBERS = "RfcSetting_IDOC_NUMBER";
	public static final String URL_FOR_UPS_NUMBERS = "UrlSetting_UPS_NUMBER";
	public static final String URL_FOR_IDOC_NUMBERS = "UrlSetting_IDOC_NUMBER";
	public static final String FORMAT_FOR_UPS_NUMBERS = "FORMAT_UPS_NUMBER";
	public static final String FORMAT_FOR_IDOC_NUMBERS = "FORMAT_IDOC_NUMBER";
	public static final String UPS_PACKAGE_PREFIX = IDoc2JCoIDocPlugin.ID + ".upsPackagePrefix";
	/**
	 * Values for sender types
	 */
	public static final String TARGET_SYSTEM = IDoc2JCoIDocPlugin.ID + ".targetSystem";
	public static final String USER_INPUT = IDoc2JCoIDocPlugin.ID + ".userInput";
	
	/**
	 * Images
	 */
	public static final String SEND_IDOCS_IMAGE = IDoc2JCoIDocPlugin.ID + "sendIDocsActionImage";
	public static final String SEND_IDOCS_IMAGE_DISABLED = IDoc2JCoIDocPlugin.ID + "sendIDocsActionImageDisabled";
	public static final String IDOC_DOCUMENT_IMAGE = IDoc2JCoIDocPlugin.ID + "idocDocumentImage";
	public static final String IDOC_SEGMENT_IMAGE = IDoc2JCoIDocPlugin.ID + "idocSegmentImage";
	
	/**
	 * Partner preferences/properties
	 */
	public static final String PARTNER_TYPE = IDoc2JCoIDocPlugin.ID + ".partnerType";
	public static final String PARTNER_NUMBER = IDoc2JCoIDocPlugin.ID + ".partnerNumber";
}
