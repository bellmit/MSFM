/*
 * Created on Aug 23, 2004
 *
 */
package com.cboe.presentation.fix.quote;

import com.cboe.domain.util.fixUtil.FixUtilConstants;
import com.cboe.domain.util.fixUtil.FixUtilMapper;
import com.cboe.domain.util.fixUtil.FixUtilPriceHelper;
import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.NotFoundException;
import com.cboe.exceptions.SystemException;
import com.cboe.idl.cmiConstants.ProductTypes;
import com.cboe.idl.cmiQuote.QuoteEntryStruct;
import com.cboe.idl.cmiQuote.QuoteEntryStructV3;
import com.cboe.interfaces.presentation.product.Product;
import com.cboe.interfaces.presentation.product.ProductClass;
import com.cboe.presentation.api.APIHome;
import com.cboe.util.ExceptionBuilder;

import com.javtech.appia.MassQuote;
import com.javtech.appia.Quote;
import com.javtech.appia.QuoteCancel;
import com.javtech.appia.QuoteStatusRequest;

/**
 * Map QuoteEntryStruct request structures to FIX messages.
 * All methods are static--this class does not maintain state.
 * @author Don Mendelson
 */
public class CmiQuoteToFixMapper {
	 
	 /**
	  * Populate a FIX mass quote message from an array of CMi quotes
	  * @param classKey unique ID of the product class
	  * @param quotes array of product quotes
	  * @param fixQuote a FIX mass quote to populate
	 * @throws DataValidationException
	 * @throws AuthorizationException
	 * @throws CommunicationException
	 * @throws SystemException
	  */
	 public static void mapQuote(int classKey, QuoteEntryStructV3[] quotes,
			MassQuote fixQuote) throws SystemException, CommunicationException,
			AuthorizationException, DataValidationException {
	 	fixQuote.NoQuoteSets = 1;
	 	fixQuote.QuoteSetID = new String[1];
	 	fixQuote.QuoteSetID[0] = "1";
	 	
	 	fixQuote.UnderlyingSymbol = new String[1];
	 	try {
			fixQuote.UnderlyingSymbol[0] = getProductClass(classKey).getClassSymbol();
		} catch (NotFoundException e) {
			throw ExceptionBuilder.dataValidationException("Product class not found",
					e.details.error);
		}
		
	 	fixQuote.TotQuoteEntries = new int[1];
	 	fixQuote.TotQuoteEntries[0] = quotes.length;
	 	fixQuote.NoQuoteEntries = new int[1];
	 	fixQuote.NoQuoteEntries[0] = quotes.length;
	 	
		fixQuote.SecurityType = new String[1][quotes.length];
		fixQuote.Symbol = new String[1][quotes.length];
		fixQuote.MaturityMonthYear = new String[1][quotes.length];
		fixQuote.PutOrCall = new int[1][quotes.length];
		fixQuote.StrikePrice = new double[1][quotes.length];
		fixQuote.QuoteEntryID = new String[1][quotes.length];
		fixQuote.OfferSize = new double[1][quotes.length];
		fixQuote.OfferPx = new double[1][quotes.length];
		fixQuote.BidPx = new double[1][quotes.length];
		fixQuote.BidSize = new double[1][quotes.length];
		fixQuote.TradingSessionID = new String[1][quotes.length];
		
	 	for (int i = 0; i < quotes.length; i++) {
	 		mapSecurity(quotes[i], fixQuote, i);
	 		fixQuote.QuoteEntryID[0][i] = Integer.toString(i);
	 		fixQuote.OfferSize[0][i] = quotes[i].quoteEntry.askQuantity;
	 		fixQuote.OfferPx[0][i] = 
	 			FixUtilPriceHelper.priceStructToDouble(quotes[i].quoteEntry.askPrice);		
	 		fixQuote.BidPx[0][i] = 
	 			FixUtilPriceHelper.priceStructToDouble(quotes[i].quoteEntry.bidPrice);
	 		fixQuote.BidSize[0][i] = quotes[i].quoteEntry.bidQuantity;
	 	}
	 }

	/**
	 * Populate a FIX quote message from a CMi quote
	 * @param quote a CMi quote
	 * @param fixQuote a FIX quote message
	 * @throws DataValidationException
	 * @throws AuthorizationException
	 * @throws CommunicationException
	 * @throws SystemException
	 */
	 public static void mapQuote(QuoteEntryStruct quote, Quote fixQuote)
			throws SystemException, CommunicationException,
			AuthorizationException, DataValidationException {
	 	mapSecurity(quote.productKey, fixQuote);
	 	fixQuote.TradingSessionID = quote.sessionName;
	 	fixQuote.BidPx = FixUtilPriceHelper.priceStructToDouble(quote.bidPrice);
	 	fixQuote.BidSize = quote.bidQuantity;
	 	fixQuote.OfferPx = FixUtilPriceHelper.priceStructToDouble(quote.askPrice);
	 	fixQuote.OfferSize = quote.askQuantity;
	}
		
	 /**
	  * Populate a FIX quote cancel message to cancel by product
	  * @param sessionName the trading session
	  * @param productKey product identifier
	  * @param fixQuote a FIX quote cancel message
	  * @throws SystemException
	  * @throws CommunicationException
	  * @throws AuthorizationException
	  * @throws DataValidationException
	  */
	 public static void mapQuoteCancel(String sessionName, int productKey,
			QuoteCancel fixQuote) throws SystemException,
			CommunicationException, AuthorizationException,
			DataValidationException {
	 	mapSecurity(productKey, fixQuote);
		fixQuote.QuoteCancelType = FixUtilConstants.QuoteCancelType.CANCEL_FOR_SYMBOL;
		fixQuote.TradingSessionID = sessionName;
	 }
	 
	 /**
	  * Populate a FIX quote cancel message to canel all quotes
	  * @param sessionName the trading session
	  * @param fixQuote a FIX quote cancel message
	  */
	 public static void mapQuoteCancel(String sessionName, QuoteCancel fixQuote) {
		fixQuote.QuoteCancelType = FixUtilConstants.QuoteCancelType.CANCEL_ALL_QUOTES;
		fixQuote.NoQuoteEntries = 1;
		fixQuote.Symbol = new String[1];
		fixQuote.Symbol[0] = FixUtilConstants.Symbol.NA;
		fixQuote.TradingSessionID = sessionName;
	}
	 
	 /**
	  * Populate a FIX quote cancel message to cancel by product class
	  * @param sessionName the trading session
	  * @param classKey product class identifier
	  * @param fixQuote a FIX quote cancel message
	  * @throws SystemException
	  * @throws CommunicationException
	  * @throws AuthorizationException
	  * @throws DataValidationException
	  */
	 public static void mapQuoteCancelByClass(String sessionName, int classKey,
			QuoteCancel fixQuote) throws SystemException,
			CommunicationException, AuthorizationException,
			DataValidationException {
	 	mapSecurityClass(classKey, fixQuote);
		fixQuote.QuoteCancelType = FixUtilConstants.QuoteCancelType.CANCEL_FOR_UNDERLYING;
		fixQuote.TradingSessionID = sessionName;
	 }
	 
	 /**
	  * Populate a quote status request message to subscribe for RFQ's for a class
	  * @param sessionName a trading session
	  * @param classKey the unique ID of a product class
	  * @param fixQuote a FIX message to populate
	 * @throws NotFoundException
	 * @throws DataValidationException
	 * @throws AuthorizationException
	 * @throws CommunicationException
	 * @throws SystemException
	  */
	 public static void mapQuoteStatusSubscription(String sessionName, int classKey,
			QuoteStatusRequest fixQuote) throws SystemException,
			CommunicationException, AuthorizationException,
			DataValidationException, NotFoundException {
	 	ProductClass productClass = getProductClass(classKey);
	 	fixQuote.Symbol = productClass.getClassSymbol();
	 	fixQuote.SecurityType = 
	 		FixUtilMapper.getFixSecurityType(productClass.getProductType());
	 	fixQuote.SubscriptionRequestType = 
	 		FixUtilConstants.SubscriptionRequestType.SNAPSHOT_UPDATES;
	 	fixQuote.TradingSessionID = sessionName;
	 	// Side is required by FIX
	 	fixQuote.Side = FixUtilConstants.Side.BUY;
	 }
	 
	 /**
	  * Populate a quote status request message to unsubscribe for RFQ's for a class
	  * @param sessionName a trading session
	  * @param classKey the unique ID of a product class
	  * @param fixQuote a FIX message to populate
	 * @throws NotFoundException
	 * @throws DataValidationException
	 * @throws AuthorizationException
	 * @throws CommunicationException
	 * @throws SystemException
	  */
	 public static void mapQuoteStatusUnsubscribe(String sessionName, int classKey,
			QuoteStatusRequest fixQuote) throws SystemException,
			CommunicationException, AuthorizationException,
			DataValidationException, NotFoundException {
	 	ProductClass productClass = getProductClass(classKey);
	 	fixQuote.Symbol = productClass.getClassSymbol();
	 	fixQuote.SecurityType = 
	 		FixUtilMapper.getFixSecurityType(productClass.getProductType());
	 	fixQuote.SubscriptionRequestType = 
	 		FixUtilConstants.SubscriptionRequestType.DISABLE;
	 	fixQuote.TradingSessionID = sessionName;
	 	// Side is required by FIX
	 	fixQuote.Side = FixUtilConstants.Side.BUY;
	 }
	 
	/**
	 * Lookup a product by product key
	 * @param productKey unique ID of product
	 * @return full product information
	 * @throws SystemException
	 * @throws CommunicationException
	 * @throws AuthorizationException
	 * @throws DataValidationException
	 * @throws NotFoundException
	 */
	private static Product getProduct(int productKey) throws SystemException,
			CommunicationException, AuthorizationException,
			DataValidationException, NotFoundException {
		return APIHome.findProductQueryAPI().getProductByKey(productKey);
	}
		
	/**
	 * Lookup a product class by class key
	 * @param classKey unique ID of the product class
	 * @return full product class information
	 * @throws SystemException
	 * @throws CommunicationException
	 * @throws AuthorizationException
	 * @throws DataValidationException
	 * @throws NotFoundException
	 */
	private static ProductClass getProductClass(int classKey)
			throws SystemException, CommunicationException,
			AuthorizationException, DataValidationException, NotFoundException {
		return APIHome.findProductQueryAPI().getProductClassByKey(classKey);
	}
	
	/**
	 * Map security attributes to a FIX order
	 * @param productKey product ID
	 * @param fixOrder FIX order
	 * @throws SystemException
	 * @throws AuthorizationException
	 * @throws DataValidationException
	 * @throws CommunicationException
	 */
	private static void mapSecurity(int productKey, Quote fixQuote)
			throws SystemException, CommunicationException,
			AuthorizationException, DataValidationException {
		try {
			Product product = getProduct(productKey);
			short productType = product.getProductType();
			fixQuote.SecurityType = FixUtilMapper.getFixSecurityType(productType);
			fixQuote.Symbol = product.getProductNameStruct().reportingClass;

			// Security attributes
			if (productType == ProductTypes.OPTION || 
				productType == ProductTypes.FUTURE) {
				fixQuote.MaturityMonthYear = FixUtilMapper.getFixMaturityMonthYear(product.getProductNameStruct().expirationDate);
			}
			
			if (productType == ProductTypes.OPTION) {
				fixQuote.PutOrCall = FixUtilMapper.getFixPutOrCall(product.getProductNameStruct().optionType);
				fixQuote.StrikePrice = FixUtilMapper.getFixStrikePrice(product.getProductNameStruct().exercisePrice);
			}
			
			if (productType == ProductTypes.STRATEGY) {
				fixQuote.SecurityDesc = FixUtilMapper.getFixSecurityDesc(productType);
				fixQuote.SecurityID = Integer.toString(productKey);
				fixQuote.IDSource = FixUtilConstants.IDSource.EXCHANGE;

			}
		} catch (NotFoundException e) {
			throw ExceptionBuilder.dataValidationException("Product not found",
					e.details.error);
		}
	}

	/**
	 * Map security attributes to a FIX order
	 * @param productKey product ID
	 * @param fixOrder FIX order
	 * @throws SystemException
	 * @throws AuthorizationException
	 * @throws DataValidationException
	 * @throws CommunicationException
	 */
	private static void mapSecurity(int productKey, QuoteCancel fixQuote)
			throws SystemException, CommunicationException,
			AuthorizationException, DataValidationException {
		try {
			Product product = getProduct(productKey);
			short productType = product.getProductType();
			
			fixQuote.NoQuoteEntries = 1;
			fixQuote.SecurityType = new String[1];
			fixQuote.SecurityType[0] = FixUtilMapper.getFixSecurityType(productType);
			fixQuote.Symbol = new String[1];
			fixQuote.Symbol[0] = product.getProductNameStruct().reportingClass;

			// Security attributes
			if (productType == ProductTypes.OPTION || 
				productType == ProductTypes.FUTURE) {
				fixQuote.MaturityMonthYear = new String[1];
				fixQuote.MaturityMonthYear[0] = FixUtilMapper.getFixMaturityMonthYear(product.getProductNameStruct().expirationDate);
			}
			
			if (productType == ProductTypes.OPTION) {
				fixQuote.PutOrCall = new int[1];
				fixQuote.PutOrCall[0] = FixUtilMapper.getFixPutOrCall(product.getProductNameStruct().optionType);
				fixQuote.StrikePrice = new double[1];
				fixQuote.StrikePrice[0] = FixUtilMapper.getFixStrikePrice(product.getProductNameStruct().exercisePrice);
			}
			
			if (productType == ProductTypes.STRATEGY) {
				fixQuote.SecurityDesc = new String[1];
				fixQuote.SecurityDesc[0] = FixUtilMapper.getFixSecurityDesc(productType);
				fixQuote.SecurityID = new String[1];
				fixQuote.SecurityID[0] = Integer.toString(productKey);
				fixQuote.IDSource = new String[1];
				fixQuote.IDSource[0] = FixUtilConstants.IDSource.EXCHANGE;

			}
		} catch (NotFoundException e) {
			throw ExceptionBuilder.dataValidationException("Product not found",
					e.details.error);
		}
	}
	
	/**
	 * Map a quote entry into a FIX mass quote
	 * @param quote a CMi quote entry
	 * @param fixQuote a FIX mass quote message
	 * @param entry index into quote entries
	 * @throws SystemException
	 * @throws CommunicationException
	 * @throws AuthorizationException
	 * @throws DataValidationException
	 */
	private static void mapSecurity(QuoteEntryStructV3 quote,
			MassQuote fixQuote, int entry) throws SystemException,
			CommunicationException, AuthorizationException,
			DataValidationException {
		try {
			Product product = getProduct(quote.quoteEntry.productKey);
			short productType = product.getProductType();
			fixQuote.SecurityType[0][entry] = 
				FixUtilMapper.getFixSecurityType(productType);
			fixQuote.Symbol[0][entry] = product.getProductNameStruct().reportingClass;

			// Security attributes
			if (productType == ProductTypes.OPTION || 
				productType == ProductTypes.FUTURE) {
				fixQuote.MaturityMonthYear[0][entry] = 
					FixUtilMapper.getFixMaturityMonthYear(product.getProductNameStruct().expirationDate);
			}
			
			if (productType == ProductTypes.OPTION) {
				fixQuote.PutOrCall[0][entry] = 
					FixUtilMapper.getFixPutOrCall(product.getProductNameStruct().optionType);
				fixQuote.StrikePrice[0][entry] = 
					FixUtilMapper.getFixStrikePrice(product.getProductNameStruct().exercisePrice);
			}
			
			fixQuote.TradingSessionID[0][entry] = quote.quoteEntry.sessionName;
			
		} catch (NotFoundException e) {
			throw ExceptionBuilder.dataValidationException("Product not found",
					e.details.error);
		}	
	}

	/**
	 * Map product class attributes to a FIX quote cancel message
	 * @param classKey unique ID of the product class
	 * @param fixQuote a quote cancel message
	 * @throws SystemException
	 * @throws CommunicationException
	 * @throws AuthorizationException
	 * @throws DataValidationException
	 */
	private static void mapSecurityClass(int classKey, QuoteCancel fixQuote)
			throws SystemException, CommunicationException,
			AuthorizationException, DataValidationException {
		try {
			ProductClass productClass = getProductClass(classKey);
			short productType = productClass.getProductType();
			fixQuote.NoQuoteEntries = 1;
			fixQuote.SecurityType = new String[1];
			fixQuote.SecurityType[0] = FixUtilMapper.getFixSecurityType(productType);
			fixQuote.Symbol = new String[1];
			fixQuote.Symbol[0] = productClass.getClassSymbol();
		} catch (NotFoundException e) {
			throw ExceptionBuilder.dataValidationException("Product class not found",
					e.details.error);
		}
	}
}
