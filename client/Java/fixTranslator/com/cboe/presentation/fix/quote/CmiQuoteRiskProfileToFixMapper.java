/*
 * Created on Sep 8, 2004
 *
 */
package com.cboe.presentation.fix.quote;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

import com.cboe.domain.util.fixUtil.FixUtilConstants;
import com.cboe.domain.util.fixUtil.FixUtilMapper;
import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.NotFoundException;
import com.cboe.exceptions.SystemException;
import com.cboe.idl.cmiQuote.QuoteRiskManagementProfileStruct;
import com.cboe.idl.cmiQuote.UserQuoteRiskManagementProfileStruct;
import com.cboe.interfaces.presentation.product.ProductClass;
import com.cboe.presentation.api.APIHome;
import com.cboe.util.ExceptionBuilder;

import com.javtech.appia.QuoteStatusRequest;

/**
 * Map CMi Quote Risk Management structures to and from FIX messages
 * 
 * @author Don Mendelson
 *
 */
public class CmiQuoteRiskProfileToFixMapper {

	/**
	 * Get value of enabled status from response message
	 * @param xmlResponse an XML documented embedded in FIX response message
	 * @return the value of the "status" element
	 * @throws SystemException if no status element is found in the response
	 */
	public static boolean getEnabledStatus(String xmlResponse) throws SystemException {
		Document document = parseDocument(xmlResponse);
		Element root = document.getDocumentElement();
		NodeList elementsByTagName = root.getElementsByTagName("status");
		Node node = elementsByTagName.item(0);
		if (node != null ) {
			String text = getText(node);
			return Boolean.getBoolean(text);
		} else {
			throw ExceptionBuilder.systemException("Value of status not found", 0);
		}
	}
	
	/**
	 * Populate a FIX quote status request message to get the all QRM profiles
	 * @param qsr a FIX message to populate
	 * @throws SystemException
	 */
	public static void mapGetAllQuoteRiskProfiles(QuoteStatusRequest qsr)
			throws SystemException {
		// Required by FIX
		qsr.Symbol = "NONE";
		qsr.Side = FixUtilConstants.Side.BUY;
		
        Document doc = createDocument();
        Node rootElement = doc.appendChild(doc.createElement("getAllQuoteRiskProfiles"));

        String xml = XmlToString(doc);
        qsr.header.XmlDataLen = xml.length();
        qsr.header.XmlData = xml;	
	}
	
	/**
	 * Populate a FIX quote status request message to get the default QRM profile
	 * @param qsr a FIX message to populate
	 * @throws SystemException
	 */
	public static void mapGetDefaultQuoteRiskProfile(QuoteStatusRequest qsr)
			throws SystemException {
		// Required by FIX
		qsr.Symbol = "NONE";
		qsr.Side = FixUtilConstants.Side.BUY;
		
        Document doc = createDocument();
        Node rootElement = doc.appendChild(doc.createElement("getDefaultQuoteRiskProfile"));

        String xml = XmlToString(doc);
        qsr.header.XmlDataLen = xml.length();
        qsr.header.XmlData = xml;		
	}
	
	/**
	 * Populate a FIX quote status request message to get a QRM profile for a product class
	 * @param classKey the unique ID of the product class
	 * @param qsr a FIX message to populate
	 * @throws SystemException
	 * @throws NotFoundException
	 * @throws DataValidationException
	 * @throws AuthorizationException
	 * @throws CommunicationException
	 */
	public static void mapGetQuoteRiskProfile(int classKey,
			QuoteStatusRequest qsr) throws SystemException,
			CommunicationException, AuthorizationException,
			DataValidationException, NotFoundException {
		mapProductClass(classKey, qsr);
		
		// Required by FIX
		qsr.Side = FixUtilConstants.Side.BUY;
		
        Document doc = createDocument();
        Node rootElement = doc.appendChild(doc.createElement("getQuoteRiskManagementProfileByClass"));

        String xml = XmlToString(doc);
        qsr.header.XmlDataLen = xml.length();
        qsr.header.XmlData = xml;	
	}
	
	/**
	 * Populate a FIX quote status request message to enable or disable QRM
	 * @param status whether QRM should be enabled or disabled for user
	 * @param qsr a FIX message to populate
	 * @throws SystemException
	 */
	public static void mapQuoteRiskManagementEnabled(boolean status,
			QuoteStatusRequest qsr) throws SystemException {
		// Required by FIX
		qsr.Symbol = "NONE";
		qsr.Side = FixUtilConstants.Side.BUY;
		
        Document doc = createDocument();
        Node rootElement = doc.appendChild(doc.createElement("setQuoteRiskManagementEnabledStatus"));
        Node QRMStructElement = rootElement.appendChild(doc.createElement("QRMStruct"));
        Node enabledElement = QRMStructElement.appendChild(doc.createElement("quoteRiskManagementEnabled"));
        enabledElement.appendChild(doc.createTextNode(Boolean.toString(status)));

        String xml = XmlToString(doc);
        qsr.header.XmlDataLen = xml.length();
        qsr.header.XmlData = xml;	
	}
	
	/**
	 * Populate a FIX quote status request message to get status of QRM
	 * @param qsr a FIX message to populate
	 * @throws SystemException
	 */
	public static void mapQuoteRiskManagementEnabled(QuoteStatusRequest qsr)
			throws SystemException {
		// Required by FIX
		qsr.Symbol = "NONE";
		qsr.Side = FixUtilConstants.Side.BUY;
		
        Document doc = createDocument();
        Node rootElement = doc.appendChild(doc.createElement("getQuoteRiskManagementEnabledStatus"));

        String xml = XmlToString(doc);
        qsr.header.XmlDataLen = xml.length();
        qsr.header.XmlData = xml;	
	}

	/**
	 * 
	 * @param xmlResponse
	 * @return
	 * @throws SystemException
	 */
	public static QuoteRiskManagementProfileStruct mapQuoteRiskManagementProfile(
			String xmlResponse) throws SystemException {
		QuoteRiskManagementProfileStruct qrm = new QuoteRiskManagementProfileStruct();
		
		Document document = parseDocument(xmlResponse);
		Element root = document.getDocumentElement();
		
		mapQRMProfile(root, qrm);

		return qrm;
	}
	
	/**
	 * Populate a FIX quote status request message from a CMi QuoteRiskManagementProfileStruct
	 * @param quoteRiskProfile a CMi structure for QRM
	 * @param qsr a FIX message
	 * @throws SystemException
	 * @throws NotFoundException
	 * @throws DataValidationException
	 * @throws AuthorizationException
	 * @throws CommunicationException
	 */
	public static void mapQuoteRiskProfile(
			QuoteRiskManagementProfileStruct quoteRiskProfile,
			QuoteStatusRequest qsr) throws SystemException,
			CommunicationException, AuthorizationException,
			DataValidationException, NotFoundException {
		
		if (quoteRiskProfile.classKey != 0) {
			mapProductClass(quoteRiskProfile.classKey, qsr);
		} else {
			// Class key zero is used for default profile
			qsr.Symbol = "NONE";
			qsr.SecurityType = "OPT";
		}
		
		// Required by FIX
		qsr.Side = FixUtilConstants.Side.BUY;
		
        Document doc = createDocument();
        Node rootElement = doc.appendChild(doc.createElement("setQuoteRiskProfile"));
        Node QRMStructElement = rootElement.appendChild(doc.createElement("QRMStruct"));
        Node enabledElement = QRMStructElement.appendChild(doc.createElement("quoteRiskManagementEnabled"));
        enabledElement.appendChild(doc.createTextNode(Boolean.toString(quoteRiskProfile.quoteRiskManagementEnabled)));
        Node timeElement = QRMStructElement.appendChild(doc.createElement("timeWindow"));
        timeElement.appendChild(doc.createTextNode(Integer.toString(quoteRiskProfile.timeWindow)));
        Node volumeElement = QRMStructElement.appendChild(doc.createElement("volumeThreshold"));
        volumeElement.appendChild(doc.createTextNode(Integer.toString(quoteRiskProfile.volumeThreshold)));

        String xml = XmlToString(doc);
        qsr.header.XmlDataLen = xml.length();
        qsr.header.XmlData = xml;
	}

	/**
	 * Populate a FIX quote status request message to remove all quote risk profiles
	 * @param qsr a FIX message to populate
	 * @throws SystemException
	 */
	public static void mapRemoveAllQuoteRiskProfiles(QuoteStatusRequest qsr) 
		throws SystemException {
		
		// Required by FIX
		qsr.Symbol = "NONE";
		qsr.Side = FixUtilConstants.Side.BUY;
		
        Document doc = createDocument();
        Node rootElement = doc.appendChild(doc.createElement("removeAllQuoteRiskProfiles"));

        String xml = XmlToString(doc);
        qsr.header.XmlDataLen = xml.length();
        qsr.header.XmlData = xml;	

	}

	/**
	 * Populate a FIX quote status request message to remove a quote risk profile
	 * @param classKey product class of profile to remove
	 * @param qsr a FIX message to populate
	 * @throws SystemException
	 * @throws NotFoundException
	 * @throws DataValidationException
	 * @throws AuthorizationException
	 * @throws CommunicationException
	 */
	public static void mapRemoveQuoteRiskProfile(int classKey,
			QuoteStatusRequest qsr) throws SystemException,
			CommunicationException, AuthorizationException,
			DataValidationException, NotFoundException {
		
		mapProductClass(classKey, qsr);
		
		// Required by FIX
		qsr.Side = FixUtilConstants.Side.BUY;
		
        Document doc = createDocument();
        Node rootElement = doc.appendChild(doc.createElement("removeQuoteRiskProfile"));

        String xml = XmlToString(doc);
        qsr.header.XmlDataLen = xml.length();
        qsr.header.XmlData = xml;	
	}
	
	/**
	 * 
	 * @param xmlResponse
	 * @return
	 * @throws SystemException
	 */
	public static UserQuoteRiskManagementProfileStruct mapUserQuoteRiskManagementProfile(
			String xmlResponse) throws SystemException {
		
		UserQuoteRiskManagementProfileStruct userQrm = new UserQuoteRiskManagementProfileStruct();
		
		Document document = parseDocument(xmlResponse);
		Element root = document.getDocumentElement();
		
		NodeList elementsByTagName = root.getElementsByTagName("globalQuoteRiskManagementEnabled");
		Node node = elementsByTagName.item(0);
		if (node != null ) {
			String text = getText(node);
			userQrm.globalQuoteRiskManagementEnabled = Boolean.getBoolean(text);
		} else {
			throw ExceptionBuilder.systemException("Value of globalQuoteRiskManagementEnabled not found", 0);
		}
		
		elementsByTagName = root.getElementsByTagName("defaultQuoteRiskProfile");
		node = elementsByTagName.item(0);
		userQrm.defaultQuoteRiskProfile = new QuoteRiskManagementProfileStruct();
		if (node.getNodeType() == Node.ELEMENT_NODE) {
			mapQRMProfile((Element)node, userQrm.defaultQuoteRiskProfile);
		}
		
		elementsByTagName = root.getElementsByTagName("quoteRiskProfiles");
		node = elementsByTagName.item(0);
		elementsByTagName = root.getElementsByTagName("quoteRiskProfile");
		userQrm.quoteRiskProfiles = new QuoteRiskManagementProfileStruct[elementsByTagName.getLength()];
		for (int i=0; i < elementsByTagName.getLength(); i++) {
			userQrm.quoteRiskProfiles[i] = new QuoteRiskManagementProfileStruct();
			node = elementsByTagName.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				mapQRMProfile((Element)node, userQrm.quoteRiskProfiles[i]);
			}
		}
		
		return userQrm;
	}
    
    /**
	 * Create a new DOM document
	 * 
	 * @return a DOM document
	 * @throws SystemException
	 *             if a parser exception occurs
	 */
	private static Document createDocument() throws SystemException {
		DocumentBuilder builder = null;
		try {
			builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			throw ExceptionBuilder.systemException("Parser configuration error: " +
					e.getMessage(), 0);
		} catch (FactoryConfigurationError e) {
			throw ExceptionBuilder.systemException("Parser factory configuration error: " +
					e.getMessage(), 0);
		}
		Document doc = builder.newDocument();
		return doc;
	}
	
	/**
	 * Extract text from an XML node
	 * @param node an XML node
	 * @return text contained in the node, if any. Otherwise returns null.
	 */
    private static String getText(Node node) {
		Node child = node.getFirstChild();
		if (child != null && child.getNodeType() == Node.TEXT_NODE) {
			Text text = (Text) child;
			return text.getData();
		} else
			return null;
	}

	/**
	 * Populate a QuoteStatusRequest with product class attributes
	 * @param classKey unique ID of the reporting class
	 * @param qsr message to populate
	 * @throws SystemException
	 * @throws NotFoundException
	 * @throws DataValidationException
	 * @throws AuthorizationException
	 * @throws CommunicationException
	 */
	private static void mapProductClass(int classKey, QuoteStatusRequest qsr)
			throws SystemException, CommunicationException,
			AuthorizationException, DataValidationException, NotFoundException {
		
		ProductClass productClass;
		productClass = APIHome.findProductQueryAPI().getProductClassByKey(classKey);

		qsr.Symbol = productClass.getClassSymbol();
		qsr.SecurityType = 
			FixUtilMapper.getFixSecurityType(productClass.getProductType());
	}
	
	/**
	 * Populate a QRM profile from an XML element
	 * @param parent an XML element with the values to populate
	 * @param qrm a CMi QRM structure
	 * @throws NumberFormatException
	 * @throws SystemException
	 */
	private static void mapQRMProfile(Element parent,
			QuoteRiskManagementProfileStruct qrm) 
			throws NumberFormatException, SystemException {
		
		NodeList elementsByTagName = parent.getElementsByTagName("classKey");
		Node node = elementsByTagName.item(0);
		if (node != null ) {
			String text = getText(node);
			qrm.classKey = Integer.parseInt(text);
		} else {
			throw ExceptionBuilder.systemException("Value of classKey not found", 0);
		}

		elementsByTagName = parent.getElementsByTagName("quoteRiskManagementEnabled");
		node = elementsByTagName.item(0);
		if (node != null ) {
			String text = getText(node);
			qrm.quoteRiskManagementEnabled = Boolean.getBoolean(text);
		} else {
			throw ExceptionBuilder.systemException("Value of quoteRiskManagementEnabled not found", 0);
		}

		elementsByTagName = parent.getElementsByTagName("timeWindow");
		node = elementsByTagName.item(0);
		if (node != null ) {
			String text = getText(node);
			qrm.timeWindow = Integer.parseInt(text);
		} else {
			throw ExceptionBuilder.systemException("Value of timeWindow not found", 0);
		}

		elementsByTagName = parent.getElementsByTagName("volumeThreshold");
		node = elementsByTagName.item(0);
		if (node != null ) {
			String text = getText(node);
			qrm.volumeThreshold = Integer.parseInt(text);
		} else {
			throw ExceptionBuilder.systemException("Value of volumeThreshold not found", 0);
		}
	}
	
    /**
	 * Parse an XML document
	 * @return a DOM document
	 * @throws SystemException if a parser exception occurs
	 */
	private static Document parseDocument(String xml) throws SystemException {
		DocumentBuilder builder = null;
		try {
			builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			throw ExceptionBuilder.systemException("Parser configuration error: " +
					e.getMessage(), 0);
		} catch (FactoryConfigurationError e) {
			throw ExceptionBuilder.systemException("Parser factory configuration error: " +
					e.getMessage(), 0);
		}
		Document doc;
		try {
			doc = builder.parse(new ByteArrayInputStream(xml.getBytes()) );
		} catch (SAXException e) {
			throw ExceptionBuilder.systemException("Parser error: " +
					e.getMessage(), 0);
		} catch (IOException e) {
			throw ExceptionBuilder.systemException("Parser IO error: " +
					e.getMessage(), 0);
		}
		return doc;
	}

	/**
	 * This method returns a DOM document as a String
	 * @param doc a DOM document
	 * @return the XML document as text or null if transformation fails
	 */ 
    private static String XmlToString(Document doc) {
    	String output = null;
    	try {
    		// Prepare the DOM document for writing
    		Source source = new DOMSource(doc);
    		
    		// Prepare the output sink
    		StringWriter writer = new StringWriter();
    		Result result = new StreamResult(writer);
    		
    		// Serialize the DOM document
    		Transformer xformer = TransformerFactory.newInstance().newTransformer();
    		xformer.transform(source, result);
    		
    		output = writer.toString();
    		return output;
    	} catch (TransformerConfigurationException e) {
    	} catch (TransformerException e) {
    	}
    	return output;
    }


}
