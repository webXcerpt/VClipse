/*******************************************************************************
 * Copyright (c) 2012 webXcerpt Software GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    webXcerpt Software GmbH - initial creator
 ******************************************************************************/
package org.vclipse.configscan.views;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import org.w3c.dom.Element;

/** This class is for utility methods
 * 
 * @author kulig
 *
 */
public class Util {

	/** The isSuccess method returns true if element has an attribute
	 *  named status with a value of "S"
	 *  
	 */
	public static boolean isSuccess(Element element) {
		return "S".equals(element.getAttribute("status"));
	}

	
	
	public static void saveStringToDisc(String filename, String xmlLog) {		// TODO: use Files.writeStringIntoFile()
		System.err.println("saving to file: " + filename);
		
		
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(filename));
			out.write(xmlLog);
			out.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		
	}
	
}
