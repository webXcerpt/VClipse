/*******************************************************************************
 * Copyright (c) 2010 webXcerpt Software GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *  
 * Contributors:
 *     webXcerpt Software GmbH - initial creator
 ******************************************************************************/
/***  ****//**
 * 
 */
package org.vclipse.connection.wizards;

/**
 *	Describes interesting sections in ini files.
 */
public interface IInterestingINISections {

	/**
	 *	Section containing system name
	 */
	public static final String SYSTEM_NAME = "MSSysName";
	
	/**
	 *	Section containing host name
	 */
	public static final String HOST_NAME = "Server";
	
	/**
	 *	Section containing system number
	 */
	public static final String SYSTEM_NUMBER = "Database";
	
	/**
	 *	Section containing client number
	 */
	public static final String CLIENT_NUMBER = "ClientNumber";
	
	/**
	 *	Section containing user name
	 */
	public static final String USER_NAME = "UserName";
	
	/**
	 *	Section containing password
	 */
	public static final String PASSWORD = "Password";
	
	/**
	 *	Section containing language
	 */
	public static final String LANGUAGE = "Language";
	
	/**
	 *	All available sections
	 */
	public static final String[] SECTIONS = new String[] {
		SYSTEM_NAME,
		HOST_NAME,
		SYSTEM_NUMBER,
		CLIENT_NUMBER,
		USER_NAME,
		PASSWORD,
		LANGUAGE
	};
}
