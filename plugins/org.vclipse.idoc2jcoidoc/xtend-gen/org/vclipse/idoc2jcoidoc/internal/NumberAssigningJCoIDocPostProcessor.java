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
package org.vclipse.idoc2jcoidoc.internal;

import com.sap.conn.idoc.IDocDocument;
import com.sap.conn.idoc.IDocException;
import com.sap.conn.idoc.IDocSegment;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.vclipse.idoc2jcoidoc.IJCoIDocPostprocessor;

@SuppressWarnings("all")
public class NumberAssigningJCoIDocPostProcessor implements IJCoIDocPostprocessor {
  private final String upsNumber;
  
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
    for (final IDocDocument iDocDocument : iDocDocuments) {
      {
        BigInteger _valueOf = BigInteger.valueOf(1);
        BigInteger _add = this.currentIDocNumber.add(_valueOf);
        this.currentIDocNumber = _add;
        String _iDocNumber = iDocDocument.getIDocNumber();
        BigInteger _bigInteger = new BigInteger(_iDocNumber);
        iDocNumbers.put(_bigInteger, this.currentIDocNumber);
        String _string = this.currentIDocNumber.toString();
        iDocDocument.setIDocNumber(_string);
        if ((this.upsNumber != null)) {
          final IDocSegment segmentRoot = iDocDocument.getRootSegment();
          String _iDocType = iDocDocument.getIDocType();
          boolean _equals = "UPSMAS01".equals(_iDocType);
          if (_equals) {
            IDocSegment[] _children = segmentRoot.getChildren("E1UPSHDR");
            for (final IDocSegment segmentE1UPSHDR : _children) {
              segmentE1UPSHDR.setValue("UPSNAM", this.upsNumber);
            }
          } else {
            String _iDocType_1 = iDocDocument.getIDocType();
            boolean _equals_1 = "VCUI_SAVEM02".equals(_iDocType_1);
            if (_equals_1) {
              IDocSegment[] _children_1 = segmentRoot.getChildren("E1BP_UPSLINK_CORE");
              for (final IDocSegment segmentE1BP_UPSLINK_CORE : _children_1) {
                {
                  segmentE1BP_UPSLINK_CORE.setValue("UPSNAM", this.upsNumber);
                  segmentE1BP_UPSLINK_CORE.setValue("ORGNAM", this.upsNumber);
                }
              }
            } else {
              IDocSegment[] _children_2 = segmentRoot.getChildren("E1UPSLINK");
              for (final IDocSegment segmentE1UPSLINK : _children_2) {
                {
                  segmentE1UPSLINK.setValue("UPSNAM", this.upsNumber);
                  segmentE1UPSLINK.setValue("ORGNAM", this.upsNumber);
                }
              }
            }
          }
        }
      }
    }
    if ((this.upsNumber != null)) {
      for (final IDocDocument iDocDocument_1 : iDocDocuments) {
        String _iDocType = iDocDocument_1.getIDocType();
        boolean _equals = "UPSMAS01".equals(_iDocType);
        if (_equals) {
          IDocSegment _rootSegment = iDocDocument_1.getRootSegment();
          IDocSegment[] _children = _rootSegment.getChildren("E1UPSHDR");
          for (final IDocSegment segmentE1UPSHDR : _children) {
            IDocSegment[] _children_1 = segmentE1UPSHDR.getChildren("E1UPSITM");
            for (final IDocSegment segmentE1UPSITM : _children_1) {
              {
                final BigInteger oldIDocNumber = segmentE1UPSITM.getBigInteger("SNDDOC");
                final BigInteger newIDocNumber = iDocNumbers.get(oldIDocNumber);
                if ((newIDocNumber == null)) {
                  StringConcatenation _builder = new StringConcatenation();
                  _builder.append("IDoc number not found: ");
                  _builder.append(oldIDocNumber, "");
                  _builder.append(" ");
                  _builder.append(iDocNumbers, "");
                  String _string = _builder.toString();
                  throw new RuntimeException(_string);
                } else {
                  String _string_1 = newIDocNumber.toString();
                  segmentE1UPSITM.setValue("SNDDOC", _string_1);
                }
              }
            }
          }
        }
      }
    }
  }
}
