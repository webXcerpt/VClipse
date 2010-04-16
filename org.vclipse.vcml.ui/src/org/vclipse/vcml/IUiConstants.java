/*******************************************************************************
 * Copyright (c) 2010 webXcerpt Software GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    webXcerpt Software GmbH - initial creator
 *******************************************************************************/
package org.vclipse.vcml;

/**
 * Constants used by the {@link VCMLUiPlugin}.
 */
public interface IUiConstants {
	
	/**
	 * Preference name for storing the sap hierarchy activated/deactivated value
	 */
	public static final String SAP_HIERARCHY_ACTIVATED = VCMLUiPlugin.ID + ".sapHierarchyActivated";
	
	/**
	 * Name for the image describing document hierarchy
	 */
	public static final String DOC_HIERARCHY_IMAGE = VCMLUiPlugin.ID + ".docHierarchyImage";
	
	/**
	 * Name for the image describing sap hierarchy
	 */
	public static final String SAP_HIERARCHY_IMAGE = VCMLUiPlugin.ID + ".sapHierarchyImage";

	/**
	 * Preference names for presenting/storing the created sap code
	 */
	public static final String OUTPUT_TO_FILE = VCMLUiPlugin.ID + ".outputToFile";
	public static final String OVERWRITE = VCMLUiPlugin.ID + ".overwrite";

}
