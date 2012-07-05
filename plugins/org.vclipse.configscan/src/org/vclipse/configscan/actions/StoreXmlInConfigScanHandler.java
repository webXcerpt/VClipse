package org.vclipse.configscan.actions;

import java.io.IOException;
import java.io.InputStream;

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

public class StoreXmlInConfigScanHandler extends AbstractStoreTestCaseHandler {

	private String matNr;
	private String docNumber;
	private String docDescr;
	protected String docVersion;
	protected String docPart;
	
	@Override
	protected void storeTestcaseFileInConfigScan(IFile file) throws JCoException {
		XMLReader xmlReader;
		try {
			xmlReader = XMLReaderFactory.createXMLReader();
			xmlReader.setContentHandler(new DefaultHandler() {
				@Override
				public void processingInstruction(String target, String data)
						throws SAXException {
					if (ConfigScanUploadProcessingInstructionExtractor.CONFIGSCAN_UPLOAD.equals(target) && data != null) {
						matNr = ConfigScanUploadProcessingInstructionExtractor.extract(ConfigScanUploadProcessingInstructionExtractor.MATNR, data, matNr);
						docNumber = ConfigScanUploadProcessingInstructionExtractor.extract(ConfigScanUploadProcessingInstructionExtractor.DOCNUMBER, data, docNumber);
						docDescr = ConfigScanUploadProcessingInstructionExtractor.extract(ConfigScanUploadProcessingInstructionExtractor.DOCDESCR, data, docDescr);
						docVersion = ConfigScanUploadProcessingInstructionExtractor.extract(ConfigScanUploadProcessingInstructionExtractor.DOCVERSION, data, docVersion);
						docPart = ConfigScanUploadProcessingInstructionExtractor.extract(ConfigScanUploadProcessingInstructionExtractor.DOCPART, data, docPart);
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
					docNumber, docDescr, docVersion, docPart==null ? "XML" : docPart);
		} catch (SAXException e) {
			throw new WrappedException(e);
		} catch (CoreException e) {
			throw new WrappedException(e);
		} catch (IOException e) {
			throw new WrappedException(e);
		}
	}
}
