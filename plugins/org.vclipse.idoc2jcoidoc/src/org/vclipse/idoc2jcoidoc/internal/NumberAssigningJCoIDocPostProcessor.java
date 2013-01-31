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
package org.vclipse.idoc2jcoidoc.internal;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.vclipse.idoc2jcoidoc.IJCoIDocPostprocessor;

import com.sap.conn.idoc.IDocDocument;
import com.sap.conn.idoc.IDocException;
import com.sap.conn.idoc.IDocSegment;

/**
 *
 */
public class NumberAssigningJCoIDocPostProcessor implements IJCoIDocPostprocessor {

	/**
	 * 
	 */
	private final String upsNumber;
	
	/**
	 * 
	 */
	private BigInteger currentIDocNumber;

	/**
	 * @param upsNumber
	 * @param initialIDocNumber
	 */
	public NumberAssigningJCoIDocPostProcessor(final String upsNumber, final BigInteger initialIDocNumber) {
		this.upsNumber = upsNumber;
		this.currentIDocNumber = initialIDocNumber;
	}

	/**
	 * @see org.vclipse.idoc2jcoidoc.IJCoIDocPostprocessor#postprocess(java.util.List)
	 */
	@Override
	public void postprocess(final List<IDocDocument> iDocDocuments) throws IDocException {
		final Map<BigInteger, BigInteger> iDocNumbers = new HashMap<BigInteger, BigInteger>();
		for(IDocDocument iDocDocument : iDocDocuments) {
			currentIDocNumber = currentIDocNumber.add(BigInteger.valueOf(1));
			iDocNumbers.put(new BigInteger(iDocDocument.getIDocNumber()), currentIDocNumber);
			// set IDoc numbers
			//  in documents
			iDocDocument.setIDocNumber(currentIDocNumber.toString());
			if (upsNumber!=null) {
				// set UPS number
				final IDocSegment segmentRoot = iDocDocument.getRootSegment(); 
				if ("UPSMAS01".equals(iDocDocument.getIDocType())) {
					for (IDocSegment segmentE1UPSHDR : segmentRoot.getChildren("E1UPSHDR")) {
						segmentE1UPSHDR.setValue("UPSNAM", upsNumber);
					}
				} else if ("VCUI_SAVEM02".equals(iDocDocument.getIDocType())) {
					for (IDocSegment segmentE1BP_UPSLINK_CORE : segmentRoot.getChildren("E1BP_UPSLINK_CORE")) {
						segmentE1BP_UPSLINK_CORE.setValue("UPSNAM", upsNumber);
						segmentE1BP_UPSLINK_CORE.setValue("ORGNAM", upsNumber);
					}
				} else {
					for (IDocSegment segmentE1UPSLINK : segmentRoot.getChildren("E1UPSLINK")) {
						segmentE1UPSLINK.setValue("UPSNAM", upsNumber);
						segmentE1UPSLINK.setValue("ORGNAM", upsNumber);
					}
				}
			}
		}
		if (upsNumber!=null) {
			for(IDocDocument iDocDocument : iDocDocuments) {
				if ("UPSMAS01".equals(iDocDocument.getIDocType())) {
					for (IDocSegment segmentE1UPSHDR : iDocDocument.getRootSegment().getChildren("E1UPSHDR")) {
						for (IDocSegment segmentE1UPSITM : segmentE1UPSHDR.getChildren("E1UPSITM")) {
							final BigInteger oldIDocNumber = segmentE1UPSITM.getBigInteger("SNDDOC");
							final BigInteger newIDocNumber = iDocNumbers.get(oldIDocNumber);
							if (newIDocNumber == null) {
								throw new RuntimeException("IDoc number not found: " + oldIDocNumber + " " + iDocNumbers);
								// TODO have some exception
							} else {
								segmentE1UPSITM.setValue("SNDDOC", newIDocNumber.toString());
							}
						}
					}
				}
			}
		}
	}

}
