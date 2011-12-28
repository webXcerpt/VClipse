package org.vclipse.configscan.actions;

import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.common.util.WrappedException;
import org.eclipse.xtext.util.Files;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import com.sap.conn.jco.JCoException;

/**
 *
 */
public class StoreXmlTestcaseInConfigScanAction extends
		AbstractStoreTestcaseInConfigScanAction {

	final static String CONFIGSCAN_UPLOAD = "configscan-upload";
	final static Pattern MATNR = Pattern.compile("^materialid\\s+(.+)$");
	final static Pattern DOCNUMBER = Pattern.compile("^documentname\\s+(.+)$");
	final static Pattern DOCDESCR = Pattern
			.compile("^documentdescription\\s+(.+)$");
	final static Pattern DOCVERSION = Pattern
			.compile("^documentversion\\s+(.+)$");
	final static Pattern DOCPART = Pattern.compile("^documentpart\\s+(.+)$");

	private String matNr;
	private String docNumber;
	private String docDescr;
	protected String docVersion;
	protected String docPart;

	private String extract(Pattern p, String s, String nomatch) {
		Matcher m = p.matcher(s);
		if (m.matches()) {
			return m.group(1).trim();
		} else {
			return nomatch;
		}
	}

	protected void storeTestcaseFileInConfigScan(IFile file)
			throws JCoException {
		XMLReader xmlReader;
		try {
			xmlReader = XMLReaderFactory.createXMLReader();
			xmlReader.setContentHandler(new DefaultHandler() {
				@Override
				public void processingInstruction(String target, String data)
						throws SAXException {
					if (CONFIGSCAN_UPLOAD.equals(target) && data != null) {
						matNr = extract(MATNR, data, matNr);
						docNumber = extract(DOCNUMBER, data, docNumber);
						docDescr = extract(DOCDESCR, data, docDescr);
						docVersion = extract(DOCVERSION, data, docVersion);
						docPart = extract(DOCPART, data, docPart);
					} else {
						super.processingInstruction(target, data);
					}
				}
			});
			InputStream fileContents = file.getContents(true);
			xmlReader.parse(new InputSource(fileContents));
			String docNumber = this.docNumber;
			if (docNumber == null) {
				docNumber = file.getName();
			}
			storeTestcaseInConfigScan(
					Files.readStreamIntoString(file.getContents(true)), matNr,
					docNumber, docDescr, docVersion, docPart);
		} catch (SAXException e) {
			throw new WrappedException(e);
		} catch (CoreException e) {
			throw new WrappedException(e);
		} catch (IOException e) {
			throw new WrappedException(e);
		}
	}

}
