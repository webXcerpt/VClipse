/**
 * Copyright (c) 2010 - 2013 webXcerpt Software GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * webXcerpt Software GmbH - initial creator
 * www.webxcerpt.com
 */
package org.vclipse.idoc2jcoidoc;

import org.vclipse.idoc2jcoidoc.IDoc2JCoIDocPlugin;

@SuppressWarnings("all")
public interface IUiConstants {
  public final static String NUMBERS_PROVIDER = (IDoc2JCoIDocPlugin.ID + ".senderType");
  
  public final static String NUMBERS_VIA_RFC = (IDoc2JCoIDocPlugin.ID + ".numbersViaRfc");
  
  public final static String RFC_FOR_UPS_NUMBERS = "RfcSetting_UPS_NUMBER";
  
  public final static String RFC_FOR_IDOC_NUMBERS = "RfcSetting_IDOC_NUMBER";
  
  public final static String URL_FOR_UPS_NUMBERS = "UrlSetting_UPS_NUMBER";
  
  public final static String URL_FOR_IDOC_NUMBERS = "UrlSetting_IDOC_NUMBER";
  
  public final static String FORMAT_FOR_UPS_NUMBERS = "FORMAT_UPS_NUMBER";
  
  public final static String FORMAT_FOR_IDOC_NUMBERS = "FORMAT_IDOC_NUMBER";
  
  public final static String UPS_PACKAGE_PREFIX = (IDoc2JCoIDocPlugin.ID + ".upsPackagePrefix");
  
  /**
   * Values for sender types
   */
  public final static String TARGET_SYSTEM = (IDoc2JCoIDocPlugin.ID + ".targetSystem");
  
  public final static String USER_INPUT = (IDoc2JCoIDocPlugin.ID + ".userInput");
  
  /**
   * Images
   */
  public final static String SEND_IDOCS_IMAGE = (IDoc2JCoIDocPlugin.ID + "sendIDocsActionImage");
  
  public final static String SEND_IDOCS_IMAGE_DISABLED = (IDoc2JCoIDocPlugin.ID + "sendIDocsActionImageDisabled");
  
  public final static String IDOC_DOCUMENT_IMAGE = (IDoc2JCoIDocPlugin.ID + "idocDocumentImage");
  
  public final static String IDOC_SEGMENT_IMAGE = (IDoc2JCoIDocPlugin.ID + "idocSegmentImage");
  
  /**
   * Partner preferences/properties
   */
  public final static String PARTNER_TYPE = (IDoc2JCoIDocPlugin.ID + ".partnerType");
  
  public final static String PARTNER_NUMBER = (IDoc2JCoIDocPlugin.ID + ".partnerNumber");
}
