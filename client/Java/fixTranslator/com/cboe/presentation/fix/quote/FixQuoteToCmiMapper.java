/*
 * Created on Aug 25, 2004
 *
 */
package com.cboe.presentation.fix.quote;

import com.cboe.domain.util.fixUtil.FixUtilConstants;
import com.cboe.domain.util.fixUtil.FixUtilDateTimeHelper;
import com.cboe.domain.util.fixUtil.FixUtilExchangeFirmMapper;
import com.cboe.domain.util.fixUtil.FixUtilMapper;
import com.cboe.domain.util.fixUtil.FixUtilPriceHelper;
import com.cboe.domain.util.fixUtil.FixUtilUserDefinedFieldTable;
import com.cboe.domain.util.fixUtil.FixUtilUserDefinedTagConstants;
import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.NotFoundException;
import com.cboe.exceptions.SystemException;
import com.cboe.idl.cmiConstants.ProductTypes;
import com.cboe.idl.cmiConstants.ReportTypes;
import com.cboe.idl.cmiConstants.StatusUpdateReasons;
import com.cboe.idl.cmiErrorCodes.DataValidationCodes;
import com.cboe.idl.cmiErrorCodes.NotAcceptedCodes;
import com.cboe.idl.cmiOrder.BustReportStruct;
import com.cboe.idl.cmiOrder.FilledReportStruct;
import com.cboe.idl.cmiProduct.ProductNameStruct;
import com.cboe.idl.cmiQuote.ClassQuoteResultStructV2;
import com.cboe.idl.cmiQuote.ClassQuoteResultStructV3;
import com.cboe.idl.cmiQuote.LockNotificationStruct;
import com.cboe.idl.cmiQuote.QuoteBustReportStruct;
import com.cboe.idl.cmiQuote.QuoteDetailStruct;
import com.cboe.idl.cmiQuote.QuoteFilledReportStruct;
import com.cboe.idl.cmiQuote.QuoteStruct;
import com.cboe.idl.cmiUser.ExchangeAcronymStruct;
import com.cboe.idl.cmiUtil.CboeIdStruct;
import com.cboe.idl.cmiUtil.DateStruct;
import com.cboe.interfaces.presentation.common.logging.IGUILogger;
import com.cboe.interfaces.presentation.product.Product;
import com.cboe.presentation.api.APIHome;
import com.cboe.presentation.common.logging.GUILoggerBusinessProperty;
import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.presentation.fix.util.FixExecutionReportToCmiMapper;
import com.cboe.util.ExceptionBuilder;

import com.javtech.appia.ExecutionReport;
import com.javtech.appia.MassQuote;
import com.javtech.appia.MessageObject;
import com.javtech.appia.Quote;
import com.javtech.appia.QuoteAcknowledgement;

/**
 * @author Mendelso
 *
 */
public class FixQuoteToCmiMapper {
 	
	/**
	 * Text that is populated by FIXCAS for StatusUpdateReasons values
	 * Ordered values ranged from StatusUpdateReasons.BOOKED = 1 through
	 * StatusUpdateReasons.QUOTE_TRIGGER_SELL = 12
	 */
	private static final String [] quoteStatusText = {
		"", "Booked", "Cancelled", "Filled", "Query", "Updated", "Open Outcry", 
		"New", "Busted", "Reinstated", "Possible Resend",  
		"Buy Quote Trigger", "Sell Quote Trigger"
	};
	
	/**
	 * Extract queue depth from FIX message
	 * @param fixMessage a FIX message
	 * @return the queue depth value from the message or zero if the message does not
	 * contain the value
	 */
	public static int getQueueDepth(MessageObject fixMessage) {
		int queueDepth = 0;
        FixUtilUserDefinedFieldTable udfTable = 
        	new FixUtilUserDefinedFieldTable(fixMessage.UserDefined);

        String strValue = 
        	udfTable.getValue(FixUtilUserDefinedTagConstants.APPLICATION_QUEUE_DEPTH);
        
        if (strValue != null && strValue.length() > 0) {
        	queueDepth = Integer.parseInt(strValue);
        }
        
		return queueDepth;
	}
	
	/**
	 * Extract quote status from FIX message
	 * @param fixQuote a FIX message
	 * @return the quote status or an empty if the message does not
	 * contain the value
	 */
	public static String getQuoteStatus(Quote fixQuote) {
        FixUtilUserDefinedFieldTable udfTable = 
        	new FixUtilUserDefinedFieldTable(fixQuote.UserDefined);

        // Returns empty string if udf tag not found
        return udfTable.getValue(FixUtilUserDefinedTagConstants.QUOTE_STATUS);
	}
	
	/**
	 * Map a FIX Quote message to CMi quote status
	 * @param fixQuote a FIX Quote message
	 * @param quoteDetail a CMi quote status structure
	 * @throws SystemException
	 * @throws CommunicationException
	 * @throws AuthorizationException
	 * @throws DataValidationException
	 * @throws NotFoundException
	 */
	public static void mapQuote(Quote fixQuote, QuoteDetailStruct quoteDetail)
			throws SystemException, CommunicationException,
			AuthorizationException, DataValidationException, NotFoundException {
		int productKey = Integer.parseInt(fixQuote.SecurityID);
		Product product = getProduct(productKey);
		
		quoteDetail.productKeys = product.getProductKeysStruct();
		quoteDetail.productName = product.getProductNameStruct();
		quoteDetail.quote = new QuoteStruct();
		quoteDetail.quote.askPrice = FixUtilPriceHelper.makeValuedPrice(fixQuote.OfferPx);
		quoteDetail.quote.askQuantity = (int) fixQuote.OfferSize;
		quoteDetail.quote.bidPrice = FixUtilPriceHelper.makeValuedPrice(fixQuote.BidPx);
		quoteDetail.quote.bidQuantity = (int) fixQuote.BidSize;
		quoteDetail.quote.productKey = productKey;
		quoteDetail.quote.quoteKey = 0; // ? No mapping to FIX
		quoteDetail.quote.sessionName = fixQuote.TradingSessionID;
		quoteDetail.quote.transactionSequenceNumber = 0; // ? No mapping to FIX
		quoteDetail.quote.userAssignedId = fixQuote.UserDefined;
		quoteDetail.quote.userId = "";	// ? No mapping to FIX
		
        FixUtilUserDefinedFieldTable udfTable = 
        	new FixUtilUserDefinedFieldTable(fixQuote.UserDefined);

        // Returns empty string if udf tag not found
        String strValue = 
        	udfTable.getValue(FixUtilUserDefinedTagConstants.QUOTE_STATUS);
		quoteDetail.statusChange = getQuoteStatus(strValue);
	}
	
	/**
	 * Map a FIX quote acknowledgement message to CMi quote status
	 * @param request a FIX mass quote message
	 * @param ack a FIX quote acknowledgement message
	 * @param quoteDetails a CMi quote status structure
	 * @throws NotFoundException
	 * @throws DataValidationException
	 * @throws AuthorizationException
	 * @throws CommunicationException
	 * @throws SystemException
	 */
	public static void mapQuoteStatus(MassQuote request, QuoteAcknowledgement quoteAck,
			QuoteDetailStruct[] quoteDetails) throws SystemException,
			CommunicationException, AuthorizationException,
			DataValidationException, NotFoundException {
		
		IGUILogger logger = GUILoggerHome.find();
		
		for (int i=0; i < quoteDetails.length; i++) {
			quoteDetails[i] = new QuoteDetailStruct();

			Product product = getProduct(quoteAck.Symbol[0][i],
	 				quoteAck.SecurityType[0][i],
					quoteAck.MaturityMonthYear[0][i],
					quoteAck.StrikePrice[0][i], quoteAck.PutOrCall[0][i]);
			
			quoteDetails[i].productKeys = product.getProductKeysStruct();
			quoteDetails[i].productName = product.getProductNameStruct();
			quoteDetails[i].quote = new QuoteStruct();
			// Populate ask and bid from request message since they're not supplied in ack
			quoteDetails[i].quote.askPrice = FixUtilPriceHelper.makeValuedPrice(request.OfferPx[0][i]);
			quoteDetails[i].quote.askQuantity = (int) request.OfferSize[0][i];
			quoteDetails[i].quote.bidPrice = FixUtilPriceHelper.makeValuedPrice(request.BidPx[0][i]);
			quoteDetails[i].quote.bidQuantity = (int) request.BidSize[0][i];
			quoteDetails[i].quote.productKey = product.getProductKey();
			quoteDetails[i].quote.quoteKey = 0; // ? No mapping to FIX
			quoteDetails[i].quote.sessionName = quoteAck.TradingSessionID;
			quoteDetails[i].quote.transactionSequenceNumber = 0; // ? No mapping to FIX
			quoteDetails[i].quote.userAssignedId = quoteAck.UserDefined;
			quoteDetails[i].quote.userId = "";	// ? No mapping to FIX
			
	        FixUtilUserDefinedFieldTable udfTable = 
	        	new FixUtilUserDefinedFieldTable(quoteAck.UserDefined);
	
	        // Returns empty string if udf tag not found
	        String strValue = 
	        	udfTable.getValue(FixUtilUserDefinedTagConstants.QUOTE_STATUS);
	        quoteDetails[i].statusChange = getQuoteStatus(strValue);
		}
	}
	
	/**
	 * Map a FIX execution report to a CMi quote fill
	 * @param execReport a FIX execution report message
	 * @param filledReport a CMi quote fill structure
	 * @throws SystemException
	 * @throws CommunicationException
	 * @throws AuthorizationException
	 * @throws DataValidationException
	 * @throws NotFoundException
	 */
	public static void mapQuoteFill(ExecutionReport execReport,
			QuoteFilledReportStruct filledReport) throws SystemException,
			CommunicationException, AuthorizationException,
			DataValidationException, NotFoundException {
		int productKey = Integer.parseInt(execReport.SecurityID);
		Product product = getProduct(productKey);
		
		FixUtilUserDefinedFieldTable udfTable = new FixUtilUserDefinedFieldTable(execReport.UserDefined);
		
		filledReport.filledReport = new FilledReportStruct[1];
		filledReport.filledReport[0] = new FilledReportStruct();
		filledReport.filledReport[0].account = execReport.Account;
		
		filledReport.filledReport[0].cmta = 
			FixExecutionReportToCmiMapper.mapToCrossedOrderExchangeFirmStruct(execReport);
		
		filledReport.filledReport[0].contraParties =
			FixExecutionReportToCmiMapper.mapToContraPartyStruct(execReport);
		
		filledReport.filledReport[0].executingBroker = "";

		FixUtilExchangeFirmMapper execOrGiveUpFirmMapper = new FixUtilExchangeFirmMapper(execReport.ExecBroker);
		filledReport.filledReport[0].executingOrGiveUpFirm = 
			execOrGiveUpFirmMapper.getCMIExchangeFirmStruct();

		filledReport.filledReport[0].extensions = "";
		
		if (execReport.MultiLegReportingType != null
				&& execReport.MultiLegReportingType.length() > 0) {
			filledReport.filledReport[0].fillReportType = (short) FixUtilMapper
					.getCmiReportType(execReport.MultiLegReportingType);
		} else {
			filledReport.filledReport[0].fillReportType = ReportTypes.REGULAR_REPORT;
		}
		
		filledReport.filledReport[0].leavesQuantity = (int) execReport.LeavesQty;
		filledReport.filledReport[0].optionalData = 
			udfTable.getValue(FixUtilUserDefinedTagConstants.CLEARING_OPTIONAL_DATA);
		
		filledReport.filledReport[0].originator = 
			FixExecutionReportToCmiMapper.mapToOriginatorStruct(execReport);
		
		filledReport.filledReport[0].orsId = execReport.SecondaryOrderID;
		
		filledReport.filledReport[0].positionEffect = 
			FixUtilMapper.getCmiPositionEffect(execReport.OpenClose);
		
		filledReport.filledReport[0].price = 
			FixUtilPriceHelper.makeValuedPrice(execReport.Price);
		
		filledReport.filledReport[0].productKey = productKey;
		filledReport.filledReport[0].sessionName = execReport.TradingSessionID;
		
		filledReport.filledReport[0].side = 
			FixUtilMapper.getCmiSide(execReport.Side, execReport.SecurityType);
		
		filledReport.filledReport[0].subaccount = execReport.ClearingAccount;
		
		try {
			filledReport.filledReport[0].timeSent = 
				FixUtilDateTimeHelper.makeDateTimeStruct(execReport.TransactTime);
		} catch (Exception e) {
			GUILoggerHome.find().exception("Invalid FilledReport.timeSent", e);
			filledReport.filledReport[0].timeSent = 
				FixUtilDateTimeHelper.makeEmptyDateTimeStruct();
		}
		
		filledReport.filledReport[0].tradedQuantity = (int) execReport.CumQty;
		filledReport.filledReport[0].tradeId = mapExecIdToTradeId(execReport.ExecID);
		filledReport.filledReport[0].transactionSequenceNumber = 0;
		
		filledReport.filledReport[0].userAcronym = 
			FixExecutionReportToCmiMapper.mapToUserAcronymStruct(execReport);
		
		filledReport.filledReport[0].userAssignedId = execReport.UserDefined;
		filledReport.filledReport[0].userId = "";

		filledReport.productKeys = product.getProductKeysStruct();
		filledReport.productName = product.getProductNameStruct();
		filledReport.quoteKey = Integer.parseInt(execReport.OrderID);
		
		if (FixUtilConstants.PossResend.POSSIBLE_RESEND.equals(execReport.header.PossResend)) {
			filledReport.statusChange = StatusUpdateReasons.POSSIBLE_RESEND;
		} else {
			filledReport.statusChange = StatusUpdateReasons.FILL;
		}
	}

	/**
	 * Map a FIX execution report to a CMi quote fill bust
	 * @param execReport a FIX execution report message
	 * @param bustReport a CMi quote bust report
	 * @throws SystemException
	 * @throws CommunicationException
	 * @throws AuthorizationException
	 * @throws DataValidationException
	 * @throws NotFoundException
	 */
	public static void mapQuoteFillBust(ExecutionReport execReport,
			QuoteBustReportStruct bustReport) throws SystemException,
			CommunicationException, AuthorizationException,
			DataValidationException, NotFoundException {
		
		int productKey = Integer.parseInt(execReport.SecurityID);
		Product product = getProduct(productKey);
		
		FixUtilUserDefinedFieldTable udfTable = 
			new FixUtilUserDefinedFieldTable(execReport.UserDefined);
		
		bustReport.bustedReport = new BustReportStruct[1];
		bustReport.bustedReport[0] = new BustReportStruct();
		
		bustReport.bustedReport[0].bustedQuantity = 
			Integer.parseInt( udfTable.getValue(FixUtilUserDefinedTagConstants.LAST_BUST_SHARES) );
		
		if (execReport.MultiLegReportingType != null
				&& execReport.MultiLegReportingType.length() > 0) {
			bustReport.bustedReport[0].bustReportType = (short) FixUtilMapper
					.getCmiReportType(execReport.MultiLegReportingType);
		} else {
			bustReport.bustedReport[0].bustReportType = ReportTypes.REGULAR_REPORT;
		}

		FixUtilExchangeFirmMapper execOrGiveUpFirmMapper = 
			new FixUtilExchangeFirmMapper(execReport.ExecBroker);
		bustReport.bustedReport[0].executingOrGiveUpFirm = 
			execOrGiveUpFirmMapper.getCMIExchangeFirmStruct();

		bustReport.bustedReport[0].price = 
			FixUtilPriceHelper.makeValuedPrice(execReport.Price);
		
		bustReport.bustedReport[0].productKey = productKey;
		bustReport.bustedReport[0].reinstateRequestedQuantity = 0;
		bustReport.bustedReport[0].sessionName = execReport.TradingSessionID;
		
		bustReport.bustedReport[0].side = 
			FixUtilMapper.getCmiSide(execReport.Side, execReport.SecurityType);
		
		try {
			bustReport.bustedReport[0].timeSent = 
				FixUtilDateTimeHelper.makeDateTimeStruct(execReport.TransactTime);
		} catch (Exception e) {
			GUILoggerHome.find().exception("Invalid BustedReport.timeSent", e);
			FixUtilDateTimeHelper.makeEmptyDateTimeStruct();
		}
		
		bustReport.bustedReport[0].tradeId = 
            FixExecutionReportToCmiMapper.mapToCboeTradeIdStruct(execReport.ExecRefID);
		
		bustReport.bustedReport[0].transactionSequenceNumber = 
			FixExecutionReportToCmiMapper.getTransactionSequenceNo(execReport);
		
		bustReport.bustedReport[0].userAcronym = 
			FixExecutionReportToCmiMapper.mapToUserAcronymStruct(execReport);
		
		// No mapping from FIX
		bustReport.bustedReport[0].userId = "";
		
		bustReport.productKeys = product.getProductKeysStruct();
		bustReport.productName = product.getProductNameStruct();
		bustReport.quoteKey = Integer.parseInt(execReport.OrderID);
		
		if (FixUtilConstants.PossResend.POSSIBLE_RESEND.equals(execReport.header.PossResend)) {
			bustReport.statusChange = StatusUpdateReasons.POSSIBLE_RESEND;
		} else {
			bustReport.statusChange = StatusUpdateReasons.BUST;
		}
	}
	
	public static void mapQuoteLock(Quote fixQuote,
			LockNotificationStruct lockedQuote) throws SystemException,
			CommunicationException, AuthorizationException,
			DataValidationException, NotFoundException {
		int productKey = Integer.parseInt(fixQuote.SecurityID);
		Product product = getProduct(productKey);
		
		FixUtilUserDefinedFieldTable udfTable = 
        	new FixUtilUserDefinedFieldTable(fixQuote.UserDefined);

        // Returns empty string if udf tag not found
        String text = 
        	udfTable.getValue(FixUtilUserDefinedTagConstants.QUOTE_TEXT);
        
        String buyers = extractTextValue(text, "BUY");
		lockedQuote.buySideUserAcronyms = extractAcronyms(buyers);
		lockedQuote.classKey = product.getProductKeysStruct().classKey;
		lockedQuote.extensions = extractTextValue(text, "Ext");
		lockedQuote.price = 
			FixUtilPriceHelper.makeValuedPrice(extractTextValue(text, "Price"));
		lockedQuote.productKey = productKey;
		lockedQuote.productType = product.getProductType();
		lockedQuote.quantity = Integer.parseInt(extractTextValue(text, "Qty"));
		String sellers = extractTextValue(text, "SELL");
		lockedQuote.sellSideUserAcronyms = extractAcronyms(sellers);
		lockedQuote.sessionName = fixQuote.TradingSessionID;;
		lockedQuote.side = ' ';	// ? No mapping to FIX;
	}
	
	/**
	 * Map FIX quote reject reason to a CMi user exception
	 * @param quoteAck  a FIX message rejecting a quote
	 * @return a CMi exception
	 */
	public static DataValidationException mapQuoteRejectReason(QuoteAcknowledgement quoteAck) {
		switch (quoteAck.QuoteRejectReason) {
		case FixUtilConstants.QuoteRejectReason.UNKNOWN_SYMBOL :
			return ExceptionBuilder.dataValidationException(quoteAck.Text, 
					DataValidationCodes.INVALID_PRODUCT);
		case FixUtilConstants.QuoteRejectReason.EXCHANGE_OR_SECURITY_CLOSED :
			return ExceptionBuilder.dataValidationException(quoteAck.Text,
					NotAcceptedCodes.INVALID_STATE);
		case FixUtilConstants.QuoteRejectReason.ORDER_EXCEEDS_LIMIT :
			return ExceptionBuilder.dataValidationException(quoteAck.Text, 
					DataValidationCodes.INVALID_QUANTITY);
		case FixUtilConstants.QuoteRejectReason.INVALID_PRICE :
			return ExceptionBuilder.dataValidationException(quoteAck.Text, 
					DataValidationCodes.INVALID_PRICE);
		case FixUtilConstants.QuoteRejectReason.INCOMPLETE_QUOTE :
			return ExceptionBuilder.dataValidationException(quoteAck.Text, 
					DataValidationCodes.INCOMPLETE_QUOTE);
		case FixUtilConstants.QuoteRejectReason.INVALID_SIDE :
			return ExceptionBuilder.dataValidationException(quoteAck.Text, 
					DataValidationCodes.INVALID_SIDE);
		case FixUtilConstants.QuoteRejectReason.INVALID_TRADING_SESSION :
			return ExceptionBuilder.dataValidationException(quoteAck.Text, 
					DataValidationCodes.INVALID_SESSION);
		case FixUtilConstants.QuoteRejectReason.CALL_LIMIT_EXCEEDED :
			return ExceptionBuilder.dataValidationException(quoteAck.Text,
					NotAcceptedCodes.RATE_EXCEEDED);
		case FixUtilConstants.QuoteRejectReason.QUOTE_RATE_EXCEEDED :
			return ExceptionBuilder.dataValidationException(quoteAck.Text,
					NotAcceptedCodes.QUOTE_RATE_EXCEEDED);
		case FixUtilConstants.QuoteRejectReason.SEQUENCE_LIMIT_EXCEEDED :
			return ExceptionBuilder.dataValidationException(quoteAck.Text,
					NotAcceptedCodes.SEQUENCE_SIZE_EXCEEDED);
		case FixUtilConstants.QuoteRejectReason.QUOTE_BEING_PROCESSED :
			return ExceptionBuilder.dataValidationException(quoteAck.Text,
					NotAcceptedCodes.QUOTE_BEING_PROCESSED);
		case FixUtilConstants.QuoteRejectReason.UNSPECIFIED_REASON :
			return ExceptionBuilder.dataValidationException(quoteAck.Text,
					NotAcceptedCodes.SERVER_NOT_AVAILABLE);
		case FixUtilConstants.QuoteRejectReason.QUOTE_UPDATE_CONTROL :
			return ExceptionBuilder.dataValidationException(quoteAck.Text,
					NotAcceptedCodes.QUOTE_CONTROL_ID);
		default : // includes UNSPECIFIED_REASON
			return ExceptionBuilder.dataValidationException(quoteAck.Text, 0);
		
		}
		
	}
	
	/**
	 * Extract a value from a comma delimited string of key=value pairs
	 * @param text string to extract value from
	 * @param key key of parameter
	 * @return the value as a string, or empty string if it is not found
	 */
	private static String extractTextValue(String text, String key) {
		String value = "";
		int start = text.indexOf(key+"=") + key.length() + 1;
		if (start >= 0) {
			int end = text.indexOf(',', start);
			if (end < 0) {
				value = text.substring(start);
			} else {
				value = text.substring(start, end);
			}
		}
		return value;
	}
	
	/**
	 * Deserialize an array of acronym structures. Each structure is comma
	 * delimited while fields within the structure are colon delimited.
	 * @param str serialized as a string
	 * @return an array of structures
	 */
	private static ExchangeAcronymStruct [] extractAcronyms(String str) {
		String[] tokens = str.split(",");
		ExchangeAcronymStruct [] acronyms = new ExchangeAcronymStruct[tokens.length];
		for (int i=0; i < tokens.length; i++) {
			String[] fields = tokens[i].split(":");
			if (fields.length >= 2) {
				acronyms[i] = new ExchangeAcronymStruct(fields[0], fields[1]);
			} else {
				acronyms[i] = new ExchangeAcronymStruct();
			}
		}
		return acronyms;
	}

	/**
	  * Map a quote acknowledement message to an array of CMi quote result structures
	  * @param quoteAck a FIX quote ack
	  * @param results an array of CMi quote result structures
	 * @throws NotFoundException
	 * @throws DataValidationException
	 * @throws AuthorizationException
	 * @throws CommunicationException
	 * @throws SystemException
	  */
	 public static void mapQuoteResults(QuoteAcknowledgement quoteAck, 
	 		ClassQuoteResultStructV3[] results) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException {
	 	for (int i =0; i < quoteAck.NoQuoteEntries[0]; i++) {
	 		results[i] = new ClassQuoteResultStructV3();
	 		results[i].quoteUpdateControlId = 0;
	 		results[i].quoteResult = new ClassQuoteResultStructV2();
	 		results[i].quoteResult.errorCode = quoteAck.QuoteEntryRejectReason[0][i];
	 		
	 		Product product = getProduct(quoteAck.Symbol[0][i],
	 				quoteAck.SecurityType[0][i],
					quoteAck.MaturityMonthYear[0][i],
					quoteAck.StrikePrice[0][i], quoteAck.PutOrCall[0][i]); 
	 		results[i].quoteResult.productKey = product.getProductKey();
	 		
	 		results[i].quoteResult.quoteKey = 0;
	 	}
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
	 * Lookup a product based on FIX fields
	 * @param symbol trading symbol
	 * @param fixMaturityMonthYear
	 * @param fixStrikePrice exercise price
	 * @param putOrCall option type
	 * @return a product
	 * @throws SystemException
	 * @throws CommunicationException
	 * @throws AuthorizationException
	 * @throws DataValidationException
	 * @throws NotFoundException if product is not found
	 */
	private static Product getProduct(String symbol, String aFixSecurityType,
			String fixMaturityMonthYear, double fixStrikePrice, int putOrCall)
			throws SystemException, CommunicationException,
			AuthorizationException, DataValidationException, NotFoundException {
		
		short productType = FixUtilMapper.getCmiProductType(aFixSecurityType);
		
		ProductNameStruct productName = new ProductNameStruct();
		
		if (fixMaturityMonthYear != null && fixMaturityMonthYear.length() > 0) {
			productName.expirationDate = FixUtilMapper.getCmiExpirationDate(fixMaturityMonthYear);
		} else {
			productName.expirationDate = new DateStruct();
		}
				
		if (ProductTypes.OPTION == productType) {
			productName.optionType = FixUtilMapper.getCmiOptionType(putOrCall);
			productName.exercisePrice = FixUtilMapper.getCmiExercisePrice(fixStrikePrice);
		} else {
			productName.optionType = ' ';
			productName.exercisePrice = FixUtilPriceHelper.makeNoPrice();
		}
		
		productName.productSymbol = "";
		productName.reportingClass = symbol;
		return APIHome.findProductQueryAPI().getProductByName(productName);
	}
	
	/**
	 * Returns a value of StatusUpdateReasons derived from reason text that
	 * is entered by the FIXCAS.
	 * @param reason text in user defened tag
	 * @return a value of StatusUpdateReasons corresponding to the text, or zero
	 * if no match is found
	 */
	 private static short getQuoteStatus(String reason) {
	 	short status = 0;
		
		for (short i = 0; i < quoteStatusText.length; i++) {
			if (quoteStatusText[i].equals(reason )) {
				status = i;
				break;
			}
		}
				
		return status;
	 }

	 /**
	  * Returns a CMi trade ID extracted from a FIX exec ID field
	  * @param execID as QuoteKey:0.TradeHigh:TradeLow.SequenceNumber
	  * @return a CMi ID struct
	  */
	 private static CboeIdStruct mapExecIdToTradeId(String execID) {
	 	int hi = 0;
	 	int lo = 0;
	 	
	 	String [] fields = execID.split(".");
	 	if (fields.length >= 2) {
	 		String [] subfields = fields[1].split(":");
	 		if (subfields.length == 2) {
	 			hi = Integer.parseInt(subfields[0]);
	 			lo = Integer.parseInt(subfields[1]);
	 		}
	 	}

	 	CboeIdStruct id = new CboeIdStruct(hi, lo);
	 	return id;
	 }
}
