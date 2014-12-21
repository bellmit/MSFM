package com.cboe.domain.util.fixUtil;

import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Pattern;

import com.cboe.domain.util.TimeHelper;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.SystemException;
import com.cboe.idl.cmiConstants.ActivityReasons;
import com.cboe.idl.cmiConstants.ContingencyTypes;
import com.cboe.idl.cmiConstants.ExchangeStrings;
import com.cboe.idl.cmiConstants.LoginSessionModes;
import com.cboe.idl.cmiConstants.OptionTypes;
import com.cboe.idl.cmiConstants.OrderNBBOProtectionTypes;
import com.cboe.idl.cmiConstants.OrderOrigins;
import com.cboe.idl.cmiConstants.PositionEffects;
import com.cboe.idl.cmiConstants.PriceTypes;
import com.cboe.idl.cmiConstants.ProductStates;
import com.cboe.idl.cmiConstants.ProductTypes;
import com.cboe.idl.cmiConstants.ReportTypes;
import com.cboe.idl.cmiConstants.Sides;
import com.cboe.idl.cmiConstants.StrategyTypes;
import com.cboe.idl.cmiConstants.TimesInForce;
import com.cboe.idl.cmiConstants.TradingSessionStates;
import com.cboe.idl.cmiOrder.OrderIdStruct;
import com.cboe.idl.cmiUtil.CboeIdStruct;
import com.cboe.idl.cmiUtil.DateStruct;
import com.cboe.idl.cmiUtil.DateTimeStruct;
import com.cboe.idl.cmiUtil.PriceStruct;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.util.ExceptionBuilder;

/**
 * This class contains static mapping classes that map between Appia's FIX
 * implementation and the CMi.
 * <br>
 * This class is the main utility class used for mapping data between FIX and CMi.
 * <p>
 * This class using the following idiom:
 * <p>
 * Mappings from FIX to CMi that are invalid throw a DataValidationException using the
 * FixDataValidationExceptionHelper to format the exception message in the format
 * advertised in the CBOE FIX documentation.
 * <p>
 * Mappings from CMi to FIX that are invalid throw a SystemException. using the
 * CmiSystemExceptionHelkper to format the exception message in the format that should
 * be documented in the operational guide for the CBOE FIX 4.2 service.
 * <br><br>
 * Copyright © 1999 by the Chicago Board Options Exchange ("CBOE"), as an unpublished work.
 * The information contained in this software program constitutes confidential and/or trade
 * secret information belonging to CBOE. This software program is made available to
 * CBOE members and member firms to enable them to develop software applications using
 * the CBOE Market Interface (CMi), and its use is subject to the terms and conditions
 * of a Software License Agreement that governs its use. This document is provided "AS IS"
 * with all faults and without warranty of any kind, either express or implied.
 *
 * @author Jim Northey
 * @version .04
 */

public class FixUtilMapper
{
    public static final char CBOE_ID_DELIMETER = ':';
    public static AtomicLong ttExecIdCounter = new AtomicLong(TimeHelper.getMillisSinceMidnight());
    public FixUtilMapper(){
    }

    /**********************************************************************/

    /**
    * Return the FirmAcronym portion of a connectionID, which is from
    * 1 to 3 characters of the connectionID by CBOE convention
    */
    public static String getFirmAcronym(String aConnectionID){
        StringBuilder firm = new StringBuilder();
        int i=0;
        while ( i < aConnectionID.length() && i < 3 && !Character.isDigit(aConnectionID.charAt(i)) ){
            firm.append(aConnectionID.charAt(i) );
            i++;
        }
        return firm.toString();
    }

    /**********************************************************************/

    /**
    * Return the CMi Option type corresponding to the FIX PutOrCall value
    */
    public static char getCmiOptionType (int fixPutOrCall) throws DataValidationException {

        switch (fixPutOrCall) {
            case FixUtilConstants.PutOrCall.PUT:
                return OptionTypes.PUT;
            case FixUtilConstants.PutOrCall.CALL:
                return OptionTypes.CALL;
            default:

                throw FixUtilDataValidationExceptionHelper.create(FixUtilConstants.PutOrCall.TAGNAME,
                                                            FixUtilConstants.PutOrCall.TAGNUMBER,
                                                            "Unknown option type specified",fixPutOrCall,
                                                            FixUtilCmiErrorCodes.FixDataValidationCodes.INVALID_PUTCALL);
        }
    }

    /**
    * Return the FIX PutOrCall value that corresponds to the cmiOptionType value
    */
    public static int getFixPutOrCall(char cmiOptionType) throws SystemException {
        switch(cmiOptionType){
            case OptionTypes.PUT:
                return FixUtilConstants.PutOrCall.PUT;
            case OptionTypes.CALL:
                return FixUtilConstants.PutOrCall.CALL;
            default:
                throw FixUtilCmiSystemExceptionHelper.create("OptionType",FixUtilConstants.PutOrCall.TAGNAME,
                                                    FixUtilConstants.PutOrCall.TAGNUMBER,
                                                    "Invalid or unexpected value for CMi OptionType",
                                                    cmiOptionType,
                                                    FixUtilCmiErrorCodes.FixDataValidationCodes.INVALID_PUTCALL);
        }
    }

    /**********************************************************************/

    /**
    * Return the CMi Side corresponding to the FIX Side value
    */
    public static char getCmiSide(String fixSide,String orderSecurityType) throws DataValidationException {

        if (orderSecurityType.equals(FixUtilConstants.SecurityType.MULTI_LEG))
        {
            if ((fixSide.equals(FixUtilConstants.Side.BUY) || (fixSide.equals(FixUtilConstants.Side.AS_DEFINED)))){
                return Sides.AS_DEFINED;
            }
            else if ((fixSide.equals(FixUtilConstants.Side.SELL) ||(fixSide.equals(FixUtilConstants.Side.OPPOSITE)))){
                return Sides.OPPOSITE;
            }
            else if (fixSide.equals("")){
                throw FixUtilDataValidationExceptionHelper.create(FixUtilConstants.Side.TAGNAME,
                                                            FixUtilConstants.Side.TAGNUMBER,
                                                            "Order Side not specified","",
                                                            FixUtilCmiErrorCodes.FixDataValidationCodes.INVALID_SIDE);
            }
            else {
                throw FixUtilDataValidationExceptionHelper.create(FixUtilConstants.Side.TAGNAME,
                                                            FixUtilConstants.Side.TAGNUMBER,
                                                            "Invalid side specified",fixSide,
                                                            FixUtilCmiErrorCodes.FixDataValidationCodes.INVALID_SIDE);
            }
        }
        else
        {
            if (fixSide.equals(FixUtilConstants.Side.BUY)){
                return Sides.BUY;
            }
            else if (fixSide.equals(FixUtilConstants.Side.SELL)){
                return Sides.SELL;
            }
            else if (fixSide.equals(FixUtilConstants.Side.SELL_SHORT)){
                return Sides.SELL_SHORT;
            }
            else if (fixSide.equals(FixUtilConstants.Side.SELL_SHORT_EXEMPT)){
                return Sides.SELL_SHORT_EXEMPT;
            }

            else if (fixSide.equals("")){
                throw FixUtilDataValidationExceptionHelper.create(FixUtilConstants.Side.TAGNAME,
                                                            FixUtilConstants.Side.TAGNUMBER,
                                                            "Order Side not specified","",
                                                            FixUtilCmiErrorCodes.FixDataValidationCodes.INVALID_SIDE);
            }
            else {
                throw FixUtilDataValidationExceptionHelper.create(FixUtilConstants.Side.TAGNAME,
                                                            FixUtilConstants.Side.TAGNUMBER,
                                                            "Invalid side specified",fixSide,
                                                            FixUtilCmiErrorCodes.FixDataValidationCodes.INVALID_SIDE);
            }

        }
    }

    /**
    * Map the Cmi side code to the FIX side code.
    */
    public static String getFixSide(char cmiSide) throws SystemException {
        switch (cmiSide) {
            case Sides.BUY:
            case Sides.AS_DEFINED:
                return FixUtilConstants.Side.BUY;
            case Sides.SELL:
            case Sides.OPPOSITE:
                return FixUtilConstants.Side.SELL;
            case Sides.SELL_SHORT:
                return FixUtilConstants.Side.SELL_SHORT;
            case Sides.SELL_SHORT_EXEMPT:
                return FixUtilConstants.Side.SELL_SHORT_EXEMPT;

            default:
                return null;
        }
    }

    /**********************************************************************/

    /**
    * Map the FIX Maturity Month Year to a CMi Expiration Date
    * This version ignores the FixMaturityDay
    */
    public static DateStruct getCmiExpirationDate(String fixMaturityMonthYear) throws DataValidationException
    {
        return getCmiExpirationDate(fixMaturityMonthYear,"");
    }

    /**
    * Map the FIX Maturity Month Year to a CMi Expiration Date
    */
    public static DateStruct getCmiExpirationDate(String fixMaturityMonthYear, String fixMaturityDay )
            throws DataValidationException
    {
        int maturityYear = 0;
        int maturityMonth = 0;
        int maturityDay = 0;

        if(fixMaturityDay == null || fixMaturityDay.equals("")) {
            fixMaturityDay = "01";
        }

        try {
            maturityYear = Integer.parseInt(fixMaturityMonthYear.substring(0, 4));
            maturityMonth = Integer.parseInt(fixMaturityMonthYear.substring(4, 6));
        }
        catch(Exception e) {
            throw FixUtilDataValidationExceptionHelper.create(FixUtilConstants.MaturityMonthYear.TAGNAME,
                                                    FixUtilConstants.MaturityMonthYear.TAGNUMBER,
                                                    e.getMessage(),
                                                    fixMaturityMonthYear,
                                                    FixUtilCmiErrorCodes.FixDataValidationCodes.INVALID_EXPIRATIONDATE);
        }

        try {
            maturityDay = Integer.parseInt(fixMaturityDay);
        }
        catch(Exception e) {
            throw FixUtilDataValidationExceptionHelper.create(FixUtilConstants.MaturityDay.TAGNAME,
                                                    FixUtilConstants.MaturityDay.TAGNUMBER,
                                                    e.getMessage(),
                                                    fixMaturityDay,
                                                    FixUtilCmiErrorCodes.FixDataValidationCodes.INVALID_EXPIRATIONDATE);
        }

        // begin cheap date validation - all in a block so it will be easy to pull later
        if (fixMaturityDay.length() > 2 ||
                maturityMonth < 1 || maturityMonth > 12) { // do not allow Octember, Septober, ...
            throw FixUtilDataValidationExceptionHelper.create(FixUtilConstants.MaturityMonthYear.TAGNAME,
                                                    FixUtilConstants.MaturityMonthYear.TAGNUMBER,
                                                    "Month not in Range",
                                                    fixMaturityMonthYear,
                                                    FixUtilCmiErrorCodes.FixDataValidationCodes.INVALID_EXPIRATIONDATE);
        } 

        if (maturityDay < 1 || maturityDay > 31) { // Outside this range is never valid
            throw FixUtilDataValidationExceptionHelper.create(FixUtilConstants.MaturityDay.TAGNAME,
                                                    FixUtilConstants.MaturityDay.TAGNUMBER,
                                                    "Day not in Range",
                                                    fixMaturityDay,
                                                    FixUtilCmiErrorCodes.FixDataValidationCodes.INVALID_EXPIRATIONDATE);
        } 

        switch (maturityMonth) { // Now deal with max days in month
            case 2: // Feb - not dealing with leap year - all Feb.29's can pass 
                if (maturityDay > 29 ) { 
                    throw FixUtilDataValidationExceptionHelper.create(FixUtilConstants.MaturityDay.TAGNAME,
                                                    FixUtilConstants.MaturityDay.TAGNUMBER,
                                                    "Day not in Range",
                                                    fixMaturityDay,
                                                    FixUtilCmiErrorCodes.FixDataValidationCodes.INVALID_EXPIRATIONDATE); 
                }
                break;

            case 4: // "30 days hath September, April, June, and November, ..."
            case 6:
            case 9:
            case 11:
                if (maturityDay > 30 ) {
                    throw FixUtilDataValidationExceptionHelper.create(FixUtilConstants.MaturityDay.TAGNAME,
                                                    FixUtilConstants.MaturityDay.TAGNUMBER,
                                                    "Day not in Range",
                                                    fixMaturityDay,
                                                    FixUtilCmiErrorCodes.FixDataValidationCodes.INVALID_EXPIRATIONDATE);
                }
                break;

            default: // "all the rest have 31 ...
                break;
        }
        // end cheap date validation - all in a block so it will be easy to pull later

        return FixUtilDateHelper.makeDateStruct(maturityYear, maturityMonth, maturityDay);
    }

    /**
    * Map CMi Expiration Date to a FIX Maturity Month Year
    */
    public static String getFixMaturityMonthYear(DateStruct cmiExpirationDate){

        return FixUtilDateHelper.dateStructToYYYYMM(cmiExpirationDate);
    }

    /**
    * Map CMi Expiration Date to a Fix Maturity Day
    *
    * Note: At this time we do not accept a date on the way in, but
    * we will return it on the way out - since it is available
    * (This method formats the day as 1-31 - no leading zeros)
    */
    public static String getFixMaturityZDay(DateStruct cmiExpirationDate){

        return FixUtilDateHelper.dateStructToZD(cmiExpirationDate);
    }

    /**
    * Map CMi Expiration Date to a Fix Maturity Day
    *
    * Note: At this time we do not accept a date on the way in, but
    * we will return it on the way out - since it is available
    * (This method formats the day as 01-31 - with leading zeros)
    */
    public static String getFixMaturityDay(DateStruct cmiExpirationDate){

        return FixUtilDateHelper.dateStructToDD(cmiExpirationDate);
    }

    /**
    * Provided these setters for mapping to FixOrderID, in case the
    * method for mapping the internal keys is ever changed
    */
    public static String getFixOrderID(OrderIdStruct orderIdStruct)
    {
		StringBuilder sb = new StringBuilder();
		formatCBOEKey(sb, orderIdStruct.highCboeId, orderIdStruct.lowCboeId) ;
		return sb.toString();
    }

    /**
    * Setter to format the key contained in a CBOEIdStruct
    */
    public static String getFixOrderID(CboeIdStruct cboeIdStruct)
    {
    	StringBuilder sb = new StringBuilder();
        formatCBOEKey(sb, cboeIdStruct.highCboeId,cboeIdStruct.lowCboeId) ;
        return sb.toString();
    }

    /**
     * Infer FIX OrdType (tag 40) value from CMi Price type
     * @param cmiPriceType value of type in PriceStruct
     * @param cmiContingencyType CMi contingency
     * @return a valid value of OrdType
     */
    public static String getFixOrderType(short cmiPriceType, 
    		short cmiContingencyType) {
    	String ordType = "";
    	switch (cmiContingencyType) {
    		case ContingencyTypes.CLOSE :
    			ordType = FixUtilConstants.OrdType.MARKET_ON_CLOSE;
				break;
			case ContingencyTypes.MIT :
				ordType = FixUtilConstants.OrdType.MARKET_IF_TOUCHED;
				break;
    		case ContingencyTypes.STP :
    		case ContingencyTypes.STP_LOSS :
    			ordType = FixUtilConstants.OrdType.STOP;
    			break;
    		case ContingencyTypes.STP_LIMIT :
    			ordType = FixUtilConstants.OrdType.STOP_LIMIT;
    			break;
    		default :
		    	switch (cmiPriceType) {
		    		case PriceTypes.MARKET :
		    			ordType = FixUtilConstants.OrdType.MARKET;
		    			break;
		    		case PriceTypes.CABINET :
		    		case PriceTypes.LIMIT :	
		    			ordType = FixUtilConstants.OrdType.LIMIT;
	    				break;
		    	}
    	}	
    	return ordType;
    }
    
    /**
    *  Getter for unique long to be used for execId's
    *  Fix ExecID
    */
    public static long getNextExecId(){
        return ttExecIdCounter.getAndIncrement();
    }

    /**
    *  Setter to format a transaction sequence number (into) a
    *  Fix ExecID
    */
    public static String getFixExecID(int cmiTransactionSequenceNumber )
    {
        return String.valueOf(cmiTransactionSequenceNumber) ;
    }

    /**
    *  Setter to format the tradeID (which is a CboeIdStruct) to a
    *  Fix ExecID or ExecRefID
    */
    public static String getFixExecID(CboeIdStruct cboeIdStruct)
    {
		StringBuilder sb = new StringBuilder();
		formatCBOEKey(sb, cboeIdStruct.highCboeId, cboeIdStruct.lowCboeId) ;
		return sb.toString();
    }

    /**
     *
     * @param orderIdStruct
     * @param cboeIdStruct
     * @param cmiTransactionSequenceNumber
     * @return  execId String as OrderHigh:OrderLow.TradeHigh:TradeLow.SequenceNumber
     */
    public static String getFixExecID(OrderIdStruct orderIdStruct,
                                      CboeIdStruct cboeIdStruct,
                                      int cmiTransactionSequenceNumber)
    {
        StringBuilder sb = new StringBuilder();
        if ((cboeIdStruct.highCboeId == 0)
            && (cboeIdStruct.lowCboeId ==0)
            && cmiTransactionSequenceNumber ==0 )
        {
            formatCBOEKey(sb, orderIdStruct.highCboeId,orderIdStruct.lowCboeId);
            sb.append('.');
            formatCBOEKey(sb, cboeIdStruct.highCboeId, cboeIdStruct.lowCboeId);
			sb.append('.');
			sb.append(System.currentTimeMillis());

        } else {
            formatCBOEKey(sb, orderIdStruct.highCboeId,orderIdStruct.lowCboeId);
			sb.append('.');
            formatCBOEKey(sb, cboeIdStruct.highCboeId, cboeIdStruct.lowCboeId);
			sb.append('.');
			sb.append(cmiTransactionSequenceNumber);
        }
        return sb.toString();
    }

    /**
     * @param orderIdStruct
     * @param uniqueLong
     * @param cmiTransactionSequenceNumber
     * @return  execId String as OrderHigh:OrderLow.uniqueLong.SequenceNumber
     */
    public static String getFixExecID(OrderIdStruct orderIdStruct,
                                      long uniqueLong,
                                      int cmiTransactionSequenceNumber)
    {
        StringBuilder sb = new StringBuilder();
        formatCBOEKey(sb, orderIdStruct.highCboeId,orderIdStruct.lowCboeId);
        sb.append('.');
        // Commented line below as some firms are expecting a numeric value instead of hexa
//        sb.append(Long.toHexString(uniqueLong));
        sb.append(uniqueLong);
        sb.append('.');
        sb.append(cmiTransactionSequenceNumber);
        return sb.toString();
    }

    /**
     * @param quoteKey
     * @param uniqueLong
     * @param cmiTransactionSequenceNumber
     * @return  execId String as QuoteKey:0.uniqueLong.SequenceNumber
     */
    public static String getFixExecIDForQuotes(int quoteKey,long uniqueLong,
                                      int cmiTransactionSequenceNumber)
    {
        StringBuilder sb = new StringBuilder();
        formatCBOEKey(sb, quoteKey, 0);
        sb.append('.');
//      Commented line below as some firms are expecting a numeric value instead of hexa
//      sb.append(Long.toHexString(uniqueLong));
        sb.append(uniqueLong);
        sb.append('.');
        sb.append(cmiTransactionSequenceNumber);
        return sb.toString();
    }

    public static boolean isTestUser(String sessionId) {
        String userPattern = System.getProperty("userIdPattern");
        try {
        if (Pattern.matches(userPattern, sessionId)){
            return true;
        }
        }
        catch (Exception e){
            Log.exception(e);
            return false;
        }
        return false;
    }


    /**
    /**
     * @param quoteKey
     * @param cboeIdStruct
     * @param cmiTransactionSequenceNumber
     * @return  execId String as QuoteKey:0.TradeHigh:TradeLow.SequenceNumber
     */
    public static String getFixExecIDForQuotes(int quoteKey,CboeIdStruct cboeIdStruct,
                                      int cmiTransactionSequenceNumber)
    {
        StringBuilder sb = new StringBuilder();
        if ((cboeIdStruct.highCboeId == 0)
            && (cboeIdStruct.lowCboeId ==0)
            && cmiTransactionSequenceNumber ==0 )
        {
            formatCBOEKey(sb, quoteKey, 0);
            sb.append('.');
            formatCBOEKey(sb, cboeIdStruct.highCboeId, cboeIdStruct.lowCboeId);
			sb.append('.');
			sb.append(System.currentTimeMillis());

        } else {
            formatCBOEKey(sb, quoteKey, 0);
			sb.append('.');
            formatCBOEKey(sb, cboeIdStruct.highCboeId, cboeIdStruct.lowCboeId);
			sb.append('.');
			sb.append(cmiTransactionSequenceNumber);
        }

        return sb.toString();
    }

    /**
     * Map CMi contingency type to FIX ExecInst (tag 18) value. Although the
     * FIX field is multiple value string type, the CMi input selects only
     * one value.
     * @param cmiContingencyType a value of ContingencyTypes
     * @return a string suitable for ExecInst field
     */
    public static String getFixExecInst(short cmiContingencyType) {
    	String fixExecInst = null;
    	switch (cmiContingencyType) {
    		case ContingencyTypes.AON :
    			fixExecInst = FixUtilConstants.ExecInst.AON;
    			break;
    		case ContingencyTypes.NOTHELD :
    			fixExecInst = FixUtilConstants.ExecInst.NOT_HELD;
				break;
            case ContingencyTypes.INTERMARKET_SWEEP :
                fixExecInst = FixUtilConstants.ExecInst.INTERMARKET_SWEEP;
                break;
            case ContingencyTypes.AUTOLINK_CROSS :
                fixExecInst = FixUtilConstants.ExecInst.AUTOLINK_CROSS;
                break;
            case ContingencyTypes.AUTOLINK_CROSS_MATCH :
                fixExecInst = FixUtilConstants.ExecInst.AUTOLINK_CROSS_MATCH;
                break;
            case ContingencyTypes.TIED_CROSS :
                fixExecInst = FixUtilConstants.ExecInst.TIED_CROSS;
                break;
            case ContingencyTypes.CROSS :
            case ContingencyTypes.MIDPOINT_CROSS:
            case ContingencyTypes.BID_PEG_CROSS :
            case ContingencyTypes.OFFER_PEG_CROSS :
                fixExecInst = FixUtilConstants.ExecInst.OK_TO_CROSS;
                break;                

    	}
		return fixExecInst;
    }
    
    /**
    * Utility routine to map a CBOE two part key into a string
    * Right now the two parts of the key are concatenated and separated by the
    * CBOE_ID_DELIMITER
    * @param sb buffer to append to
    * @param highKey part of transaction ID
    * @param lowKey part of transaction ID
    */
    protected static void formatCBOEKey(StringBuilder sb, int highKey, int lowKey)
    {
		sb.append(highKey);
		sb.append(CBOE_ID_DELIMETER);
		sb.append(lowKey);
    }

    /**
    *
    */
    public static void setOrderIdValues(String fixVersionOfOrderId, OrderIdStruct orderIdStruct)
            throws DataValidationException
    {
        // The OrderID is not a required field so we need to test if fixVersionOfOrderId is an empty String.
        // If so, set the highCboeId and lowCboeId to zero.

        if(fixVersionOfOrderId.length() == 0)
        {
            orderIdStruct.highCboeId = 0;
            orderIdStruct.lowCboeId = 0;
        }
        // Else, parse the fixVersionOfOrderId to get the highCboeId and lowCboeId
        else
        {
            int idx = fixVersionOfOrderId.indexOf( CBOE_ID_DELIMETER );

            if(idx < 0)
            {
                throw FixUtilDataValidationExceptionHelper.create(FixUtilConstants.OrderID.TAGNAME,
                                                            FixUtilConstants.OrderID.TAGNUMBER,
                                                            "Exchange OrderID has invalid format",
                                                            fixVersionOfOrderId,
                                                            FixUtilCmiErrorCodes.FixDataValidationCodes.INVALID_ORDER_ID);
            }

            String high = fixVersionOfOrderId.substring(0, idx);
            String low = fixVersionOfOrderId.substring( idx + 1);

            try
            {
                orderIdStruct.highCboeId = Integer.parseInt(high);
                orderIdStruct.lowCboeId = Integer.parseInt(low);
            }
            catch(NumberFormatException ex)
            {
                throw FixUtilDataValidationExceptionHelper.create(FixUtilConstants.OrderID.TAGNAME,
                                                        FixUtilConstants.OrderID.TAGNUMBER,
                                                        "Exchange OrderID has invalid format",
                                                        fixVersionOfOrderId,
                                                        FixUtilCmiErrorCodes.FixDataValidationCodes.INVALID_ORDER_ID);
            }
        }
    }
    /**********************************************************************/

    /**
    * Create a UTC Fix formatted time from the date time struct.
    */
    public static String getFixTransactTime(DateTimeStruct dtStruct)
    {
        Date date = FixUtilDateTimeHelper.dateTimeStructToDate(dtStruct);
        return FixUtilDateTimeFormatter.getFixTime(date);
    }
    /**
    * Create a cmi DateTimeStruct from the String Fix UTC time value.
    */
    public static DateTimeStruct getCmiTimeStamp(String utcTimeStamp) throws DataValidationException
    {
        try
        {
            Date date = FixUtilDateTimeFormatter.getLocalDate(utcTimeStamp);
            return FixUtilDateTimeHelper.makeDateTimeStruct(date);
        }
        catch(java.text.ParseException ex)
        {
            throw ExceptionBuilder.dataValidationException("Invalid Data - UTCTimeStamp = "
                                                            +  utcTimeStamp +" [" + ex.getMessage() + "]",
                                                            FixUtilCmiErrorCodes.FixDataValidationCodes.INVALID_TIME);
        }
    }

    /**********************************************************************/
    public static int getFixTradSesStatus(short cmiTradingSessionState) throws DataValidationException
    {
        if(cmiTradingSessionState == TradingSessionStates.OPEN)
        {
            return FixUtilConstants.TradSesStatus.OPEN;
        }
        else
            if(cmiTradingSessionState == TradingSessionStates.CLOSED)
        {
            return FixUtilConstants.TradSesStatus.CLOSED;
        }
            throw FixUtilDataValidationExceptionHelper.create(FixUtilConstants.TradSesStatus.TAGNAME,
                                            FixUtilConstants.TradSesStatus.TAGNUMBER,
                                            "Unknown cmi trading session state",
                                            (int) cmiTradingSessionState,
                                            FixUtilCmiErrorCodes.FixDataValidationCodes.INVALID_TRADING_SESSION_STATE);
    }
    /**********************************************************************/
    /**
    */
    public static String getFixExchange( final String cboeExchangeCode ) throws DataValidationException
    {
        String result = null;
        if( cboeExchangeCode != null )
        {
            if( cboeExchangeCode.equals("CO") ) result = FixUtilConstants.MDMkt.CBOE;
            else if( cboeExchangeCode.equals("W") ) result = FixUtilConstants.MDMkt.CBOE;
            else if( cboeExchangeCode.equals("AO") ) result = FixUtilConstants.MDMkt.AMEX;
            else if( cboeExchangeCode.equals("A") ) result = FixUtilConstants.MDMkt.AMEX;
            else if( cboeExchangeCode.equals("IO") ) result = FixUtilConstants.MDMkt.ISC;
            else if( cboeExchangeCode.equals("T") ) result = FixUtilConstants.MDMkt.NASDAQ;
            else if( cboeExchangeCode.equals("Q") ) result = FixUtilConstants.MDMkt.NASDAQ;
            else if( cboeExchangeCode.equals("N") ) result = FixUtilConstants.MDMkt.NYSE;
            else if( cboeExchangeCode.equals("PO") ) result = FixUtilConstants.MDMkt.PACIFIC;
            else if( cboeExchangeCode.equals("P") ) result = FixUtilConstants.MDMkt.PACIFIC;
            else if( cboeExchangeCode.equals("XO") ) result = FixUtilConstants.MDMkt.PHIL_OPTIONS;
            else if( cboeExchangeCode.equals("X") ) result = FixUtilConstants.MDMkt.PHIL;
        }
        if(result == null)
        {
            throw FixUtilDataValidationExceptionHelper.create(
                                            FixUtilConstants.MDMkt.TAGNAME,
                                            FixUtilConstants.MDMkt.TAGNUMBER,
                                            "Unknown exchange code",
                                            cboeExchangeCode,
                                            FixUtilCmiErrorCodes.FixDataValidationCodes.INVALID_MARKETDATA_EXCHANGECODE);
        }
        return result;
    }

    /**********************************************************************/

    /**
    * Map FIX Strike Price to CMi Exercise Price
    */
    public static PriceStruct getCmiExercisePrice(double fixStrikePrice) throws DataValidationException {

        try
        {
            return FixUtilPriceHelper.makeValuedPrice(fixStrikePrice);
        }
        catch (Exception e)
        {
            throw FixUtilDataValidationExceptionHelper.create(FixUtilConstants.StrikePrice.TAGNAME,
                                                        FixUtilConstants.StrikePrice.TAGNUMBER,
                                                        e.getMessage(),
                                                        fixStrikePrice,
                                                        FixUtilCmiErrorCodes.FixDataValidationCodes.INVALID_PRICE);
        }
    }


    /**
    * Map CMi Exercise Price to a FIX Strike Price
    */
    public static double getFixStrikePrice(PriceStruct cmiExercisePrice){

        return FixUtilPriceHelper.priceStructToDouble(cmiExercisePrice);
    }

    /**********************************************************************/

    /**
    * MAP FIX CustomerOrFirm value to a CMi OriginType
    */
    public static char getCmiOriginType(int fixCustomerOrFirm)throws DataValidationException {
        switch (fixCustomerOrFirm)
        {
            case FixUtilConstants.CustomerOrFirm.CUSTOMER:
                return OrderOrigins.CUSTOMER;
            case FixUtilConstants.CustomerOrFirm.FIRM:
                return OrderOrigins.FIRM;
            case FixUtilConstants.CustomerOrFirm.MARKET_MAKER:
                return OrderOrigins.MARKET_MAKER;
            case FixUtilConstants.CustomerOrFirm.BROKER_DEALER:
                return OrderOrigins.BROKER_DEALER;
            case FixUtilConstants.CustomerOrFirm.CUSTOMER_BROKER_DEALER:
                return OrderOrigins.CUSTOMER_BROKER_DEALER;
            default:
                throw FixUtilDataValidationExceptionHelper.create(
                                                        FixUtilConstants.CustomerOrFirm.TAGNAME,
                                                        FixUtilConstants.CustomerOrFirm.TAGNUMBER,
                                                        "Invalid or unknown CustomerOrFirm",
                                                        fixCustomerOrFirm,
                                                        FixUtilCmiErrorCodes.FixDataValidationCodes.INVALID_ORIGIN_TYPE);
        }
    }

    /**
     * Map CMi FIX OrderCapacity value, also known as Rule80A, to CMi OrderOrigins value 
     */
     public static char getCmiOrderCapacity(String fixOrderCapacity) {
     	if (fixOrderCapacity == null) {
         	return ' '; 
     	} else if (FixUtilConstants.OrderCapacity.CUSTOMER.equals(fixOrderCapacity)) {
             return OrderOrigins.CUSTOMER;
         } else if (FixUtilConstants.OrderCapacity.FIRM.equals(fixOrderCapacity)) {
             return  OrderOrigins.FIRM;
         } else if (FixUtilConstants.OrderCapacity.MARKET_MAKER.equals(fixOrderCapacity)) {
         	return  OrderOrigins.MARKET_MAKER;
         } else if (FixUtilConstants.OrderCapacity.BROKER_DEALER.equals(fixOrderCapacity)) {
         	return OrderOrigins.BROKER_DEALER;
         } else if (FixUtilConstants.OrderCapacity.CUSTOMER_BROKER_DEALER.equals(fixOrderCapacity)) {
            return OrderOrigins.CUSTOMER_BROKER_DEALER;
         } else if (FixUtilConstants.OrderCapacity.MEMBER_CUSTOMER_SEGREGATED_ACCOUNT.equals(fixOrderCapacity)) {
         	return OrderOrigins.CUSTOMER_FBW;
         } else if (FixUtilConstants.OrderCapacity.PROXY_CUSTOMER_SEGREGATED_ACCOUNT.equals(fixOrderCapacity)) {
            return OrderOrigins.PRINCIPAL_ACTING_AS_AGENT;
         } else {
             // Default action is to pass through Cmi value to FIX
             return fixOrderCapacity.charAt(0);
         }
     }
     
    /**
    * Map CMi OriginType value to a FIX CustomerOrFirm Value
    */
    public static int getFixCustomerOrFirm(char cmiOriginType) throws SystemException {
        switch (cmiOriginType) {
            case OrderOrigins.CUSTOMER:
                return FixUtilConstants.CustomerOrFirm.CUSTOMER;
            case OrderOrigins.FIRM:
                return FixUtilConstants.CustomerOrFirm.FIRM;
            case OrderOrigins.MARKET_MAKER:
                return FixUtilConstants.CustomerOrFirm.MARKET_MAKER;
            case OrderOrigins.BROKER_DEALER:
                return FixUtilConstants.CustomerOrFirm.BROKER_DEALER;
            case OrderOrigins.CUSTOMER_BROKER_DEALER:
                return FixUtilConstants.CustomerOrFirm.CUSTOMER_BROKER_DEALER;

            default:
                throw FixUtilCmiSystemExceptionHelper.create("OriginType",
                                                    FixUtilConstants.CustomerOrFirm.TAGNAME,
                                                    FixUtilConstants.CustomerOrFirm.TAGNUMBER,
                                                    "Invalid or unknown OriginType returned by the CAS",
                                                    cmiOriginType,
                                                    FixUtilCmiErrorCodes.FixDataValidationCodes.INVALID_ORIGIN_TYPE);
        }
    }
    
    /**
     * Map CMi OrderOrigins value to a FIX OrderCapacity value, also known as Rule80A
     */
     public static String getFixOrderCapacity(char cmiOrderOrigins) {
         switch (cmiOrderOrigins) {
             case OrderOrigins.CUSTOMER:
                 return FixUtilConstants.OrderCapacity.CUSTOMER;
             case OrderOrigins.FIRM:
                 return FixUtilConstants.OrderCapacity.FIRM;
             case OrderOrigins.MARKET_MAKER:
                 return FixUtilConstants.OrderCapacity.MARKET_MAKER;
             case OrderOrigins.BROKER_DEALER:
                 return FixUtilConstants.OrderCapacity.BROKER_DEALER;
             case OrderOrigins.CUSTOMER_BROKER_DEALER:
                 return FixUtilConstants.OrderCapacity.CUSTOMER_BROKER_DEALER;
             case OrderOrigins.CUSTOMER_FBW :
             	return FixUtilConstants.OrderCapacity.MEMBER_CUSTOMER_SEGREGATED_ACCOUNT;
             case OrderOrigins.PRINCIPAL_ACTING_AS_AGENT :
             	return FixUtilConstants.OrderCapacity.PROXY_CUSTOMER_SEGREGATED_ACCOUNT;
             default:
             	// Default action is to pass through Cmi value to FIX
             	return new String(new char[] {cmiOrderOrigins} );
         }
     }
    /**********************************************************************/


    /**
    *  Scan the FIX Symbol and determine if it is a CBOE numeric product key
    *  if so, return true
    */

    public static int getCmiProductKey(String fixSecurityID) throws DataValidationException
    {

        int productKey=FixUtilConstants.SbtValues.DataKeys.DEFAULT_KEY;

        try{
            productKey = Integer.parseInt(fixSecurityID);
        }
        catch (Exception e){

            throw FixUtilDataValidationExceptionHelper.create(FixUtilConstants.SecurityID.TAGNAME,
                                                        FixUtilConstants.SecurityID.TAGNUMBER,
                                                        "Invalid data found - must be numeric only",
                                                        fixSecurityID,
                                                        FixUtilCmiErrorCodes.FixDataValidationCodes.INVALID_PRODUCT);
        }

        return productKey;

    }

    /**
    * Convert a cmiProductKey to a string
    */
    public static String getFixSecurityID(int cmiProductKey)
    {
        return String.valueOf(cmiProductKey);
    }

    /********************************************************************************************************/

    /**
    * Convert from Fix Security Type to Cmi Product Type
    */
    public static short getCmiProductType(String aFixSecurityType) throws DataValidationException
    {

        short productType=0;
        // prevent NullPointerException
        if (aFixSecurityType == null || aFixSecurityType.trim().equals(""))
        {
            throw ExceptionBuilder.dataValidationException("SecurityType not specified "
                                                + FixUtilConstants.SecurityType.TAGNAME
                                                +"("+FixUtilConstants.SecurityType.TAGNUMBER+"), "
                                                + FixUtilConstants.UnderlyingSecurityType.TAGNAME
                                                +"("+FixUtilConstants.UnderlyingSecurityType.TAGNUMBER+"), ",
                                                FixUtilCmiErrorCodes.FixDataValidationCodes.INVALID_PRODUCT_TYPE);
        }

        if (aFixSecurityType.equals(FixUtilConstants.SecurityType.COMMON_STOCK) )
        {
            productType = ProductTypes.EQUITY;
        }
        else if (aFixSecurityType.equals(FixUtilConstants.SecurityType.OPTION) )
        {
            productType = ProductTypes.OPTION;
        }
        else if (aFixSecurityType.equals(FixUtilConstants.SecurityType.FUTURE) )
        {
            productType = ProductTypes.FUTURE;
        }
        else if (aFixSecurityType.equals(FixUtilConstants.SecurityType.WARRANT) )
        {
            productType = ProductTypes.WARRANT;
        }
        else if (aFixSecurityType.equals(FixUtilConstants.SecurityType.INDEX) )
        {
            productType = ProductTypes.INDEX;
        }
        else if (aFixSecurityType.equals(FixUtilConstants.SecurityType.VOLATILITY_INDEX) )
        {
            productType = ProductTypes.VOLATILITY_INDEX;
        }
        else if (aFixSecurityType.equals(FixUtilConstants.SecurityType.MULTI_LEG) )
        {
            productType = ProductTypes.STRATEGY;
        }
        else if ( aFixSecurityType.equals( FixUtilConstants.SecurityType.COMMODITY ) )
        {
            productType = ProductTypes.COMMODITY;
        }
        else if ( aFixSecurityType.equals( FixUtilConstants.SecurityType.LINKED_NOTE ) )
        {
            productType = ProductTypes.LINKED_NOTE;
        }
        else if ( aFixSecurityType.equals( FixUtilConstants.SecurityType.UNIT_INVESTMENT_TRUST ) )
        {
            productType = ProductTypes.UNIT_INVESTMENT_TRUST;
        }
        else if ( aFixSecurityType.equals( FixUtilConstants.SecurityType.US_TREASURY_BILL ) )
        {
            productType = ProductTypes.DEBT;
        }
        else
        {
            throw ExceptionBuilder.dataValidationException(
                                                "Invalid or unsupported SecurityType - "+aFixSecurityType + ", "
                                                + FixUtilConstants.SecurityType.TAGNAME
                                                +"("+FixUtilConstants.SecurityType.TAGNUMBER+"), "
                                                + FixUtilConstants.UnderlyingSecurityType.TAGNAME
                                                +"("+FixUtilConstants.UnderlyingSecurityType.TAGNUMBER+"), ",
                                                FixUtilCmiErrorCodes.FixDataValidationCodes.INVALID_PRODUCT_TYPE);
        }

        return productType;
    }
    /**
    * Convert from Cmi Product Type to Fix Security Type
    */
    public static String getFixSecurityType(short aCmiProductType) throws SystemException
    {
        String securityType = "";

        switch (aCmiProductType)
        {
            case ProductTypes.EQUITY:
                securityType = FixUtilConstants.SecurityType.COMMON_STOCK;
                break;
            case ProductTypes.OPTION:
                securityType = FixUtilConstants.SecurityType.OPTION;
                break;
            case ProductTypes.FUTURE:
                securityType = FixUtilConstants.SecurityType.FUTURE;
                break;
            case ProductTypes.WARRANT:
                securityType = FixUtilConstants.SecurityType.WARRANT;
                break;

            case ProductTypes.VOLATILITY_INDEX:
                securityType = FixUtilConstants.SecurityType.VOLATILITY_INDEX;
                break;
            case ProductTypes.INDEX:
                securityType = FixUtilConstants.SecurityType.INDEX;
                break;
            case ProductTypes.STRATEGY:
                securityType = FixUtilConstants.SecurityType.MULTI_LEG;
                break;
            case ProductTypes.DEBT:
                securityType = FixUtilConstants.SecurityType.US_TREASURY_BILL;
                break;

            case ProductTypes.COMMODITY:
                securityType = FixUtilConstants.SecurityType.COMMODITY;
                break;
            case ProductTypes.LINKED_NOTE:
                securityType = FixUtilConstants.SecurityType.LINKED_NOTE;
                break;
            case ProductTypes.UNIT_INVESTMENT_TRUST:
                securityType = FixUtilConstants.SecurityType.UNIT_INVESTMENT_TRUST;
                break;
            default:
                throw FixUtilCmiSystemExceptionHelper.create("ProductType",
                                                    FixUtilConstants.SecurityType.TAGNAME,
                                                    FixUtilConstants.SecurityType.TAGNUMBER,
                                                    " Unknown ProductType",
                                                    aCmiProductType,
                                                    FixUtilCmiErrorCodes.FixDataValidationCodes.INVALID_PRODUCT_TYPE);
        }

        return securityType;
    }

    /**
    * Convert between a CMi Product State and a FIX Security Trading Status
    *
    */
    public static int getFixSecurityTradingStatus(short aCmiProductState) throws SystemException
    {

        int securityTradingStatus = 0;

        switch (aCmiProductState)
        {

            case ProductStates.CLOSED:
                securityTradingStatus =  FixUtilConstants.SecurityTradingStatus.NOT_AVAILABLE_FOR_TRADING;
                break;

            case ProductStates.ENDING_HOLD:
                securityTradingStatus = FixUtilConstants.SecurityTradingStatus.OFF_HOLD;
                break;

            case ProductStates.FAST_MARKET:
                securityTradingStatus = FixUtilConstants.SecurityTradingStatus.FAST_MARKET;
                break;

            case ProductStates.HALTED:
                securityTradingStatus = FixUtilConstants.SecurityTradingStatus.TRADING_HALT;
                break;

            case ProductStates.NO_SESSION:
                securityTradingStatus = FixUtilConstants.SecurityTradingStatus.NOT_AVAILABLE_FOR_TRADING;
                break;

            case ProductStates.ON_HOLD:
                securityTradingStatus = FixUtilConstants.SecurityTradingStatus.ON_HOLD;
                break;

            case ProductStates.OPEN:
                securityTradingStatus = FixUtilConstants.SecurityTradingStatus.READY_TO_TRADE;
                break;

            case ProductStates.OPENING_ROTATION:
                securityTradingStatus = FixUtilConstants.SecurityTradingStatus.OPENING_ROTATION;
                break;

            case ProductStates.PRE_OPEN:
                securityTradingStatus = FixUtilConstants.SecurityTradingStatus.PRE_OPEN;
                break;

            case ProductStates.SUSPENDED:
                securityTradingStatus = FixUtilConstants.SecurityTradingStatus.TEMPORARILY_NOT_AVAILABLE_FOR_TRADING;
                break;

            default:

                throw FixUtilCmiSystemExceptionHelper.create("ProductState",
                                                    FixUtilConstants.SecurityTradingStatus.TAGNAME,
                                                    FixUtilConstants.SecurityTradingStatus.TAGNUMBER,
                                                    " Unknown ProductState",
                                                    aCmiProductState,
                                                    FixUtilCmiErrorCodes.FixDataValidationCodes.INVALID_PRODUCT_STATE);

        }

        return securityTradingStatus;
    }

    /********************************************************************************************************/

    /**
    * Convert between a FIX OpenClose and CMi Position Effect
    *
    */
    public static char getCmiPositionEffect(String aFixOpenClose) throws DataValidationException
    {

        if (aFixOpenClose == null)
        {
            throw FixUtilDataValidationExceptionHelper.create( FixUtilConstants.OpenClose.TAGNAME,
                                                    FixUtilConstants.OpenClose.TAGNUMBER,
                                                    "You Must Specify an Open/Close code on an order",
                                                    "",
                                                    FixUtilCmiErrorCodes.FixDataValidationCodes.INVALID_POSITION_EFFECT);
        }

        char cmiPositionEffect = ' ';

        if (aFixOpenClose.equals(FixUtilConstants.OpenClose.CLOSE))
        {
            cmiPositionEffect = PositionEffects.CLOSED;
        }
        else if (aFixOpenClose.equals(FixUtilConstants.OpenClose.OPEN))
        {
            cmiPositionEffect = PositionEffects.OPEN;
        }
        else if (aFixOpenClose.equals(FixUtilConstants.OpenClose.NONE))
        {
            cmiPositionEffect = PositionEffects.NOTAPPLICABLE;
        }
        else
        {

            throw FixUtilDataValidationExceptionHelper.create(
                                                    FixUtilConstants.OpenClose.TAGNAME,
                                                    FixUtilConstants.OpenClose.TAGNUMBER,
                                                    " Invalid or unsupported OpenClose Code",
                                                    aFixOpenClose,
                                                    FixUtilCmiErrorCodes.FixDataValidationCodes.INVALID_POSITION_EFFECT);
        }

        return cmiPositionEffect;
    }


    /**
    * Convert between a CMi Position Effect and FIX OpenClose field
    *
    */
    public static String getFixOpenClose(char aCmiPositionEffect) throws SystemException
    {
        String fixOpenClose="";

        switch (aCmiPositionEffect)
        {
            case PositionEffects.CLOSED:
                fixOpenClose = FixUtilConstants.OpenClose.CLOSE;
                break;
            case PositionEffects.OPEN:
                fixOpenClose = FixUtilConstants.OpenClose.OPEN;
                break;
            case PositionEffects.NOTAPPLICABLE:
                fixOpenClose = FixUtilConstants.OpenClose.NONE;
                break;

            default:
                throw FixUtilCmiSystemExceptionHelper.create("PositionEffect",FixUtilConstants.OpenClose.TAGNAME,
                                                    FixUtilConstants.OpenClose.TAGNUMBER,
                                                    " Unknown PositionEffect",
                                                    aCmiPositionEffect,
                                                    FixUtilCmiErrorCodes.FixDataValidationCodes.INVALID_POSITION_EFFECT);

        }

        return fixOpenClose;
    }


    /**
    * Convert from CMi LoginSessionMode to FIX TargetSystemID
    *
    */
    public static char getCmiLoginSessionMode(String aFixTargetSystemId) throws DataValidationException
    {
        char cmiLoginSessionMode = ' ';

        if(aFixTargetSystemId.equals(FixUtilConstants.TargetSubID.TEST)) {
            cmiLoginSessionMode = LoginSessionModes.NETWORK_TEST;
        }
        else if(aFixTargetSystemId.equals(FixUtilConstants.TargetSubID.PRODUCTION)) {
            cmiLoginSessionMode = LoginSessionModes.PRODUCTION;
        }
        else if(aFixTargetSystemId.equals(FixUtilConstants.TargetSubID.SIMULATOR)) {
            cmiLoginSessionMode = LoginSessionModes.STAND_ALONE_TEST;
        }
        else {
            throw FixUtilDataValidationExceptionHelper.create(FixUtilConstants.TargetSubID.TAGNAME,
                                                    FixUtilConstants.TargetSubID.TAGNUMBER,
                                                    " Invalid or unsupported TargetSubID",
                                                    aFixTargetSystemId,
                                                    FixUtilCmiErrorCodes.FixDataValidationCodes.INVALID_LOGIN_SESSION_MODE);
        }

        return cmiLoginSessionMode;
    }

    /**
     * Tranlate a CMi LoginSessionModes value to a string suitable for FIX TargetSubID
     * in Login message.
     * @param cmiSessionMode a value of LoginSessionModes
     * @return an appropriate FIX string if successful. If an invalid value is passed in,
     * an empty string is returned.
     */
    public static String getFixLoginSessionMode(char cmiSessionMode) {
    	String fixSessionMode = "";
    	
    	switch (cmiSessionMode) {
    	case LoginSessionModes.NETWORK_TEST :
    		fixSessionMode = FixUtilConstants.TargetSubID.TEST;
    		break;
    	case LoginSessionModes.PRODUCTION :
    		fixSessionMode = FixUtilConstants.TargetSubID.PRODUCTION;
			break;
    	case LoginSessionModes.STAND_ALONE_TEST :
    		fixSessionMode = FixUtilConstants.TargetSubID.SIMULATOR;
			break;			
        }

        return fixSessionMode;
    }
    
    /**
    * Get a locally generated ExecID. This routine will format the current time
    * in UTC time down to millisecond as the initial part of the key. The second
    * portion will be appended following the CBOE delimiter ":". This second
    * portion is optional and does not have to be provided.
    * I think we will put in something like "REJ" for rejectedo orders.
    */
    public static String getLocalExecID(String appendedValue)
    {
        String localExecID = FixUtilDateTimeFormatter.currentTimeUTC();
        localExecID += ":";

        if(appendedValue != null) {
            localExecID += appendedValue;
        }

        return localExecID;
    }

    public static String getFixTickDirection(char cmiTickDirection) {
        switch(cmiTickDirection) {
            case '+': return FixUtilConstants.TickDirection.PLUS_TICK;
            case '-': return FixUtilConstants.TickDirection.MINUS_TICK;
            default: return "";
        }
    }
    
   /**
    * Map CMi time in force and contigency type to FIX time in force
    * @param cmiTimeInForce CMi time in force
    * @param cmiContingencyType CMi contingency
    * @return FIX value for time in force (tag 59)
    */
   public static String getFixTimeInForce(char cmiTimeInForce, 
   		short cmiContingencyType) {
   		String tif = "";
   		switch (cmiContingencyType) {
   			case ContingencyTypes.OPG :
   				tif = FixUtilConstants.TimeInForce.OPG;
   				break;
   			case ContingencyTypes.IOC :
   				tif = FixUtilConstants.TimeInForce.IOC;
				break;
			case ContingencyTypes.FOK :
				tif = FixUtilConstants.TimeInForce.FOK;
			break;
			default:
		   		switch (cmiTimeInForce) {
		   			case TimesInForce.DAY : 
		   				tif = FixUtilConstants.TimeInForce.DAY;
		   				break;
		   			case TimesInForce.GTC : 
		   				tif =  FixUtilConstants.TimeInForce.GTC;
		   				break;
		   			case TimesInForce.GTD : 
		   				tif =  FixUtilConstants.TimeInForce.GTD;
		   				break;
		   		}
   		}
   		return tif;
   }

    /* Map CMI Strategy type to FIX Security Desc */
    public static String getFixSecurityDesc(int cmiStrategyType) throws DataValidationException
    {
        switch (cmiStrategyType) {

			case StrategyTypes.STRADDLE: return FixUtilConstants.SecurityDesc.STRADDLE_1;
			case StrategyTypes.PSEUDO_STRADDLE: return FixUtilConstants.SecurityDesc.PSEUDO_STRADDLE_1;
			case StrategyTypes.VERTICAL: return FixUtilConstants.SecurityDesc.VERTICAL_1;
			case StrategyTypes.RATIO: return FixUtilConstants.SecurityDesc.RATIO_1;
			case StrategyTypes.TIME: return FixUtilConstants.SecurityDesc.TIME_1;
			case StrategyTypes.DIAGONAL: return FixUtilConstants.SecurityDesc.DIAGONAL_1;
			case StrategyTypes.COMBO: return FixUtilConstants.SecurityDesc.COMBO_1;
			case StrategyTypes.BUY_WRITE: return FixUtilConstants.SecurityDesc.BUY_WRITE_1;
			case StrategyTypes.UNKNOWN: return FixUtilConstants.SecurityDesc.UNKNOWN_1;

			default: return FixUtilConstants.SecurityDesc.UNKNOWN;

        }

    }


    /* Map  FIX  security desc to CMI Strategy types*/
    public static short getCmiStrategyType (String fixSecurityDesc) throws DataValidationException
    {
        if (fixSecurityDesc == null)
        {
            throw FixUtilDataValidationExceptionHelper.create( FixUtilConstants.SecurityDesc.TAGNAME,
                                                        FixUtilConstants.SecurityDesc.TAGNUMBER,
                                                        "You must specify a valid Security Desc ",
                                                        "",
                                                        FixUtilCmiErrorCodes.FixDataValidationCodes.INVALID_SPREAD);
        }

        short cmiSecurityDesc = 0;

		if (  fixSecurityDesc.equals(FixUtilConstants.SecurityDesc.STRADDLE) ||
			  fixSecurityDesc.equalsIgnoreCase(FixUtilConstants.SecurityDesc.STRADDLE_1)
		   )
		{
			cmiSecurityDesc = StrategyTypes.STRADDLE;
		}
		else if ( fixSecurityDesc.equals(FixUtilConstants.SecurityDesc.PSEUDO_STRADDLE)   ||
				  fixSecurityDesc.equalsIgnoreCase(FixUtilConstants.SecurityDesc.PSEUDO_STRADDLE_1) ||
				  fixSecurityDesc.equalsIgnoreCase(FixUtilConstants.SecurityDesc.PSEUDO_STRADDLE_2) ||
				  fixSecurityDesc.equalsIgnoreCase(FixUtilConstants.SecurityDesc.PSEUDO_STRADDLE_3)
				)
		{
			cmiSecurityDesc = StrategyTypes.PSEUDO_STRADDLE;
		}
		else if ( fixSecurityDesc.equals(FixUtilConstants.SecurityDesc.VERTICAL) ||
				  fixSecurityDesc.equalsIgnoreCase(FixUtilConstants.SecurityDesc.VERTICAL_1)
				)
		{
			cmiSecurityDesc = StrategyTypes.VERTICAL;
		}
		else if ( fixSecurityDesc.equals(FixUtilConstants.SecurityDesc.RATIO)  ||
				  fixSecurityDesc.equalsIgnoreCase(FixUtilConstants.SecurityDesc.RATIO_1)
				)
		{
			cmiSecurityDesc = StrategyTypes.RATIO;
		}
		else if ( fixSecurityDesc.equals(FixUtilConstants.SecurityDesc.TIME) ||
				  fixSecurityDesc.equalsIgnoreCase(FixUtilConstants.SecurityDesc.TIME_1)
				)
		{
			cmiSecurityDesc = StrategyTypes.TIME;
		}
		else if ( fixSecurityDesc.equals(FixUtilConstants.SecurityDesc.DIAGONAL) ||
				  fixSecurityDesc.equalsIgnoreCase(FixUtilConstants.SecurityDesc.DIAGONAL_1)
				)
		{
			cmiSecurityDesc = StrategyTypes.DIAGONAL;
		}
		else if ( fixSecurityDesc.equals(FixUtilConstants.SecurityDesc.COMBO) ||
				  fixSecurityDesc.equalsIgnoreCase(FixUtilConstants.SecurityDesc.COMBO_1)
				)
		{
			cmiSecurityDesc = StrategyTypes.COMBO;
		}
		else if ( fixSecurityDesc.equals(FixUtilConstants.SecurityDesc.BUY_WRITE)   ||
				  fixSecurityDesc.equalsIgnoreCase(FixUtilConstants.SecurityDesc.BUY_WRITE_1) ||
				  fixSecurityDesc.equalsIgnoreCase(FixUtilConstants.SecurityDesc.BUY_WRITE_2) ||
				  fixSecurityDesc.equalsIgnoreCase(FixUtilConstants.SecurityDesc.BUY_WRITE_3)
				)
		{
			cmiSecurityDesc = StrategyTypes.BUY_WRITE;
		}
		else
		{

			throw FixUtilDataValidationExceptionHelper.create(
												  FixUtilConstants.SecurityDesc.TAGNAME,
												  FixUtilConstants.SecurityDesc.TAGNUMBER,
												  " Invalid or unsupported Security Desc",
												  fixSecurityDesc,
												  FixUtilCmiErrorCodes.FixDataValidationCodes.INVALID_SPREAD);
		}

		return cmiSecurityDesc;
    }

    /* Map  FIX  MultiLegReportingType to CMI Reporting type*/

    public static int getCmiReportType (String fixMultiLegReportingType) throws DataValidationException
    {
        if (fixMultiLegReportingType == null)
        {
            throw FixUtilDataValidationExceptionHelper.create( FixUtilConstants.MultiLegReportingType.TAGNAME,
                                                        FixUtilConstants.MultiLegReportingType.TAGNUMBER,
                                                        "You must specify a valid MultiLeg Reporting type ",
                                                        "",
                                                        FixUtilCmiErrorCodes.FixDataValidationCodes.INVALID_SPREAD);
        }

        short cmiReportType = 0;

        if (fixMultiLegReportingType.equals(FixUtilConstants.MultiLegReportingType.SINGLE_SECURITY))
        {
            cmiReportType = ReportTypes.REGULAR_REPORT;
        }
        else if (fixMultiLegReportingType.equals(FixUtilConstants.MultiLegReportingType.INDIVIDUAL_LEG_OF_MULTILEG))
        {
            cmiReportType = ReportTypes.STRATEGY_LEG_REPORT;
        }
        else if (fixMultiLegReportingType.equals(FixUtilConstants.MultiLegReportingType.MULTILEG_SECURITY))
        {
            cmiReportType = ReportTypes.STRATEGY_REPORT;
        }
        else
        {

            throw FixUtilDataValidationExceptionHelper.create(
                                                    FixUtilConstants.MultiLegReportingType.TAGNAME,
                                                    FixUtilConstants.MultiLegReportingType.TAGNUMBER,
                                                    " Invalid or unsupported MultiLeg Reporting type",
                                                    fixMultiLegReportingType,
                                                    FixUtilCmiErrorCodes.FixDataValidationCodes.INVALID_SPREAD);
        }

        return cmiReportType;

    }
    /* Map CMI Report type  to FIX  MultiLegReportingType*/

    public static String getFixMultiLegReportingType(int cmiReportType) throws DataValidationException
    {
        switch (cmiReportType) {

            case ReportTypes.REGULAR_REPORT:
                return FixUtilConstants.MultiLegReportingType.SINGLE_SECURITY;
            case ReportTypes.STRATEGY_LEG_REPORT:
                return FixUtilConstants.MultiLegReportingType.INDIVIDUAL_LEG_OF_MULTILEG;
            case ReportTypes.STRATEGY_REPORT:
                return FixUtilConstants.MultiLegReportingType.MULTILEG_SECURITY;
            case ReportTypes.NEW_ORDER_REJECT:
                return FixUtilConstants.MultiLegReportingType.SINGLE_SECURITY;  // new for linkage
            default:
                throw FixUtilDataValidationExceptionHelper.create(FixUtilConstants.MultiLegReportingType.TAGNAME,
                                                            FixUtilConstants.MultiLegReportingType.TAGNUMBER,
                                                            "Invalid MultiLegReportingType specified","",
                                                            FixUtilCmiErrorCodes.FixDataValidationCodes.INVALID_STRATEGY);

        }

    }

    /**********************************************************************/

    /**
    *  Map FIX NBBO Price Protection
    */
    public static short getCmiPriceProtectionScope(String fixPriceProtectionScope )
    throws DataValidationException, SystemException {
        if (fixPriceProtectionScope.equals(FixUtilConstants.PriceProtectionScope.NONE))
        {
            return OrderNBBOProtectionTypes.NONE;
        }
        else if (fixPriceProtectionScope.equals(FixUtilConstants.PriceProtectionScope.LOCAL))
        {
            return OrderNBBOProtectionTypes.NONE;
        }
        else if (fixPriceProtectionScope.equals(FixUtilConstants.PriceProtectionScope.NATIONAL))
        {
            return OrderNBBOProtectionTypes.FULL;
        }
        else
        {
            throw FixUtilDataValidationExceptionHelper.create(FixUtilConstants.PriceProtectionScope.TAGNAME,
                                            FixUtilConstants.PriceProtectionScope.TAGNUMBER,
                                            "Invalid or unknown value",
                                            fixPriceProtectionScope,
                                            FixUtilCmiErrorCodes.FixDataValidationCodes.INVALID_PRICE_PROTECTION_SCOPE);

        }

    }

    /**
    * MAP CMi NBBO Price Protection to FIX NBBO Price Protection
    */
    public static String getFixPriceProtectionScope(short cmiPriceProtectionScope) throws SystemException {

        if (cmiPriceProtectionScope == OrderNBBOProtectionTypes.NONE)
        {
            return FixUtilConstants.PriceProtectionScope.LOCAL;
        }
        else if (cmiPriceProtectionScope == OrderNBBOProtectionTypes.FULL)
        {
            return FixUtilConstants.PriceProtectionScope.NATIONAL;
        }
		else if (cmiPriceProtectionScope == 0)//Changed in Hybrid Version -By default it is 0 and it means Full protection
		{
			 return FixUtilConstants.PriceProtectionScope.NATIONAL;
		}
        else
        {

            throw FixUtilCmiSystemExceptionHelper.create("Coverage",FixUtilConstants.PriceProtectionScope.TAGNAME,
                                                FixUtilConstants.PriceProtectionScope.TAGNUMBER,
                                                "Invalid or unknown PriceProtectionScope returned by the CAS",
                                                cmiPriceProtectionScope,
                                                FixUtilCmiErrorCodes.FixDataValidationCodes.INVALID_PRICE_PROTECTION_SCOPE);

        }
    }

    /**********************************************************************/
    /**
    *  MAP CMi Echchange Strings to FIX OLA Exchange Destination Strings
    */
    public static String getFixOlaExchangeFromCmi( final String cmiExchangeCode ) throws DataValidationException
    {
        
        String result = null;
        if( cmiExchangeCode != null )
        {
            if(         cmiExchangeCode.equals(ExchangeStrings.AMEX)     ) result = FixUtilConstants.ExDestination.ASE;
            else if(    cmiExchangeCode.equals(ExchangeStrings.BOX)      ) result = FixUtilConstants.ExDestination.BOX;
            else if(    cmiExchangeCode.equals(ExchangeStrings.ISE)      ) result = FixUtilConstants.ExDestination.ISX;
            else if(    cmiExchangeCode.equals(ExchangeStrings.PHLX)     ) result = FixUtilConstants.ExDestination.PHO;
//          RaviR - Jun 18 2007
//          Changed as critical bugfix. OLA will use PCX for NYSE, but CBOE and other exchanges will be using NYSE
            else if(    cmiExchangeCode.equals(ExchangeStrings.NYSE)     ) result = FixUtilConstants.ExDestination.PSE;
            else if(    cmiExchangeCode.equals(ExchangeStrings.PSE)     ) result = FixUtilConstants.ExDestination.PSE;
            else if(    cmiExchangeCode.equals(ExchangeStrings.NASDAQ)     ) result = FixUtilConstants.ExDestination.NASDAQ;            
            else if(    cmiExchangeCode.equals(ExchangeStrings.BATS)     ) result = FixUtilConstants.ExDestination.BATS;            
        }
        if(result == null) {
            result = cmiExchangeCode;
        }
        if(result == null)
        {
            throw FixUtilDataValidationExceptionHelper.create(
                                        FixUtilConstants.ExDestination.TAGNAME,
                                        FixUtilConstants.ExDestination.TAGNUMBER,
                                        "Unknown cmi exchange code",
                                        cmiExchangeCode,
                                        FixUtilCmiErrorCodes.FixDataValidationCodes.INVALID_EXDESTINATION_EXCHANGE_CODE);
        }
        return result;
    }

    /**********************************************************************/
    /**
    *  MAP FIX OLA Exchange Destination Strings to CMi Echchange Strings
    */
    public static String getCmiExchangeFromFixOla( final String fixOlaExchangeCode )
            throws DataValidationException
    {
        String result = null;
        if( fixOlaExchangeCode != null )
        {
	          if(         fixOlaExchangeCode.equals(FixUtilConstants.ExDestination.ASE)   )
                result = ExchangeStrings.AMEX;
	          else if(    fixOlaExchangeCode.equals(FixUtilConstants.ExDestination.BOX )  )
                result = ExchangeStrings.BOX;
	          else if(    fixOlaExchangeCode.equals(FixUtilConstants.ExDestination.ISX)   )
                result = ExchangeStrings.ISE;
	          else if(    fixOlaExchangeCode.equals(FixUtilConstants.ExDestination.PHO)   )
                result = ExchangeStrings.PHLX;
//            RaviR - Jun 18 2007
//            Changed as critical bugfix. OLA will use PCX for NYSE, but CBOE and other exchanges will be using NYSE
	          else if(    fixOlaExchangeCode.equals(FixUtilConstants.ExDestination.PSE )  )
                result = ExchangeStrings.NYSE;
              else if(    fixOlaExchangeCode.equals(FixUtilConstants.ExDestination.NASDAQ )  )
                    result = ExchangeStrings.NASDAQ;
              else if(    fixOlaExchangeCode.equals(FixUtilConstants.ExDestination.BATS )  )
                    result = ExchangeStrings.BATS;
              
        }
        if(result == null) {
            result = fixOlaExchangeCode;
        }
        if(result == null)
        {
            throw FixUtilDataValidationExceptionHelper.create(
            FixUtilConstants.ExDestination.TAGNAME,
            FixUtilConstants.ExDestination.TAGNUMBER,
            "Unknown ola exchange code",
            fixOlaExchangeCode,
            FixUtilCmiErrorCodes.FixDataValidationCodes.INVALID_EXDESTINATION_EXCHANGE_CODE);
        }
        return result;
    }

		/**
		 * Maps a cmi Activity Reason to FIX -- for populating tag 58 on the Order Cancel Report.
		 * @param cmiActivityReason
		 * @return String
		 */

    public static String mapToFixOrderCancelReason(short cmiActivityReason)
    {
        switch(cmiActivityReason) {
            case ActivityReasons.NOTHING_DONE:
                return "NOTHING_DONE";
            case ActivityReasons.USER:
                return "USER";
            case ActivityReasons.SYSTEM:
                return "SYSTEM";
            case ActivityReasons.LOST_CONNECTION:
                return "LOST_CONNECTION";
            case ActivityReasons.QRM_REMOVED:
                return "QRM_REMOVED";

            // New reasons being added for SToC
            case ActivityReasons.CROSS_IN_PROGRESS:
                return "CROSS_IN_PROGRESS";
            case ActivityReasons.NOT_WITHIN_NBBO:
                return "NOT_WITHIN_NBBO";
            case ActivityReasons.TRADE_THROUGH_CBOE:
                return "TRADE_THROUGH_CBOE";
            case ActivityReasons.INSUFFICIENT_CUSTOMER_ORDER_QUANTITY:
                return "INSUFFICIENT_CUSTOMER_ORDER_QUANTITY";
            case ActivityReasons.INSUFFICIENT_CROSS_ORDER_SIZE:
                return "INSUFFICIENT_CROSS_ORDER_SIZE";
            case ActivityReasons.INSUFFICIENT_CROSS_ORDER_DOLLAR_AMOUNT:
                return "INSUFFICIENT_CROSS_ORDER_DOLLAR_AMOUNT";

            case ActivityReasons.NO_USER_ACTIVITY:
                return "NO_USER_ACTIVITY";
                // NO_USER_ACTIVITY = 23;
            case ActivityReasons.PRODUCT_HALTED:
                return "PRODUCT_HALTED";
            case ActivityReasons.PRODUCT_SUSPENDED:
                return "PRODUCT_SUSPENDED";
            case ActivityReasons.CANCEL_ON_RSS:
                return "CANCEL_ON_RSS";
            case ActivityReasons.WASH_TRADE_PREVENTION:
                return "WASH_TRADE_PREVENTION";

            default:
                return "CANCELED " + cmiActivityReason ;
        }
    }
    /**********************************************************************/

    /**
    * Maps a CMi ActivityReason to a FIX OLAOrdRejReason. If the CMi ActivityReason
    * cannot be mapped to a valid OLAOrdRejReason a DataValidaitonException will
    * be thrown.
     * ----- DANGER, DANGER WILL ROBINSON - This code stolen from Linkage --
     *                                  Maintenance will require coordination -----
    */
    public static int mapToFixOlaOrdRejReason(short cmiActivityReason)
            throws DataValidationException
    {
        switch(cmiActivityReason) {
            case ActivityReasons.BROKER_OPTION:
                return FixUtilLinkageFixConstants.OLAOrdRejReason.BROKER_OR_EXCHANGE_OPTION;
                // BROKER_OPTION = 100
            case ActivityReasons.CROWD_TRADE:
                return FixUtilLinkageFixConstants.OLAOrdRejReason.MANUAL_TRADE;
                // CROWD_TRADE = 102;
            case ActivityReasons.DUPLICATE_ORDER:
                return FixUtilLinkageFixConstants.OLAOrdRejReason.DUPLICATE_ORDER;
                // DUPLICATE_ORDER = 103;
            case ActivityReasons.EXCHANGE_CLOSED:
                return FixUtilLinkageFixConstants.OLAOrdRejReason.EXCHANGE_OR_TRADING_SESSION_CLOSED;
                // EXCHANGE_CLOSED = 104;
            case ActivityReasons.GATE_VIOLATION:
                return FixUtilLinkageFixConstants.OLAOrdRejReason.ORDER_RECEIVED_TOO_SOON;
                // GATE_VIOLATION = 105;
            case ActivityReasons.INVALID_ACCOUNT:
                return FixUtilLinkageFixConstants.OLAOrdRejReason.MISSING_CLEARING_ACCOUNT;
                // INVALID_ACCOUNT = 106;
            case ActivityReasons.INVALID_AUTOEX_VALUE:
                return FixUtilLinkageFixConstants.OLAOrdRejReason.INVALID_AUTO_EX;
                // INVALID_AUTOEX_VALUE = 107;
            case ActivityReasons.INVALID_CMTA:
                return FixUtilLinkageFixConstants.OLAOrdRejReason.UNKNOWN_CLEARING_FIRM;
                // INVALID_CMTA = 108;
            case ActivityReasons.INVALID_FIRM:
                return FixUtilLinkageFixConstants.OLAOrdRejReason.MISSING_EXEC_BROKER;
                // INVALID_FIRM = 109;
            case ActivityReasons.INVALID_ORIGIN_TYPE:
                return FixUtilLinkageFixConstants.OLAOrdRejReason.INVALID_ORDER_CAPACITY;
                // INVALID_ORIGIN_TYPE = 110;
            case ActivityReasons.INVALID_POSITION_EFFECT:
                return FixUtilLinkageFixConstants.OLAOrdRejReason.INVALID_OPEN_CLOSE;
                // INVALID_POSITION_EFFECT = 111;
            case ActivityReasons.INVALID_PRICE:
                return FixUtilLinkageFixConstants.OLAOrdRejReason.PRICE_OUT_OF_BOUNDS;
                // INVALID_PRICE = 112;
            case ActivityReasons.INVALID_PRODUCT:
                return FixUtilLinkageFixConstants.OLAOrdRejReason.UNKNOWN_SYMBOL;
                // INVALID_PRODUCT = 113;
            case ActivityReasons.INVALID_PRODUCT_TYPE:
                return FixUtilLinkageFixConstants.OLAOrdRejReason.COMPLEX_ORDER;
                // INVALID_PRODUCT_TYPE = 114;
            case ActivityReasons.INVALID_QUANTITY:
                return FixUtilLinkageFixConstants.OLAOrdRejReason.ORDER_EXCEEDS_LIMIT;
                // INVALID_QUANTITY = 115;
            case ActivityReasons.INVALID_SUBACCOUNT:
                return FixUtilLinkageFixConstants.OLAOrdRejReason.SUB_ACCOUNT_ID_MISSING;
                // INVALID_SUBACCOUNT = 117;
            case ActivityReasons.INVALID_TIME_IN_FORCE:
                return FixUtilLinkageFixConstants.OLAOrdRejReason.INVALID_TIME_IN_FORCE;
                // INVALID_TIME_IN_FORCE = 118;
            case ActivityReasons.INVALID_USER:
                return FixUtilLinkageFixConstants.OLAOrdRejReason.ACCOUNT_MISSING;
                // INVALID_USER = 119;
            case ActivityReasons.LATE_PRINT:
                return FixUtilLinkageFixConstants.OLAOrdRejReason.LATE_PRINT_TO_OPRA_TAPE;
                // LATE_PRINT = 120;
            case ActivityReasons.NOT_FIRM:
                return FixUtilLinkageFixConstants.OLAOrdRejReason.INVALID_INSTRUMENT_STATE_NON_FIRM;
                // NOT_FIRM = 121;
            case ActivityReasons.MISSING_EXEC_INFO:
                return FixUtilLinkageFixConstants.OLAOrdRejReason.MISSING_EXEC_INFO;
                // MISSING_EXEC_INFO = 122;
            case ActivityReasons.NON_BLOCK_TRADE:
                return FixUtilLinkageFixConstants.OLAOrdRejReason.CANCEL_DUE_TO_NON_BLOCK_TRADE;
                // NON_BLOCK_TRADE = 124;
            case ActivityReasons.NOT_NBBO:
                return FixUtilLinkageFixConstants.OLAOrdRejReason.NOT_AT_NBBO;
                // NOT_NBBO = 125;
            case ActivityReasons.COMM_DELAYS:
                return FixUtilLinkageFixConstants.OLAOrdRejReason.COMMUNICATION_DELAYS_TO_OPRA;
                // COMM_DELAYS = 126;
            case ActivityReasons.ORIGINAL_ORDER_REJECTED:
                return FixUtilLinkageFixConstants.OLAOrdRejReason.ORIGINAL_ORDER_REJECTED;
                // ORIGINAL_ORDER_REJECTED = 127;
            case ActivityReasons.PROCESSING_PROBLEMS:
                return FixUtilLinkageFixConstants.OLAOrdRejReason.PROCESSING_PROBLEMS_AT_MARKET_CENTER;
                // PROCESSING_PROBLEMS = 129;
            case ActivityReasons.PRODUCT_HALTED:
                return FixUtilLinkageFixConstants.OLAOrdRejReason.INVALID_INSTRUMENT_STATE_HALTED;
                // PRODUCT_HALTED = 130;
            case ActivityReasons.PRODUCT_IN_ROTATION:
                return FixUtilLinkageFixConstants.OLAOrdRejReason.INVALID_INSTRUMENT_STATE_ROTATION;
                // PRODUCT_IN_ROTATION = 131;
            case ActivityReasons.STALE_ORDER:
                return FixUtilLinkageFixConstants.OLAOrdRejReason.STALE_ORDER;
                // STALE_ORDER = 133;
            case ActivityReasons.ORDER_TOO_LATE:
                return FixUtilLinkageFixConstants.OLAOrdRejReason.TOO_LATE_TO_ENTER;
                // ORDER_TOO_LATE = 134;
            case ActivityReasons.TRADE_BUSTED:
                return FixUtilLinkageFixConstants.OLAOrdRejReason.TRADE_BUSTED_CORRECTED;
                // TRADE_BUSTED = 135;
            case ActivityReasons.TRADE_REJECTED:
                return FixUtilLinkageFixConstants.OLAOrdRejReason.TRADE_REJECTED;
                // TRADE_REJECTED = 136;
            case ActivityReasons.UNKNOWN_ORDER:
                return FixUtilLinkageFixConstants.OLAOrdRejReason.UNKNOWN_ORDER;
                // UNKNOWN_ORDER = 137;

            // The following were not in the original spec, but are added for completeness
            // (discovered during testing)
            case ActivityReasons.INVALD_EXCHANGE:
                return ActivityReasons.INVALD_EXCHANGE;
                // TPF:INVALD_EXCHANGE = 138;
            case ActivityReasons.TRANSACTION_FAILED:
                return ActivityReasons.TRANSACTION_FAILED;
                // TPF:TRANSACTION_FAILED = 139;
            case ActivityReasons.NOT_ACCEPTED:
                return ActivityReasons.NOT_ACCEPTED;
                // TPF:NOT_ACCEPTED = 140;

            // added in hybrid failover
            case ActivityReasons.PRODUCT_SUSPENDED:
                return ActivityReasons.PRODUCT_SUSPENDED;

            default:
                throw ExceptionBuilder.dataValidationException(
                        "Invalid or unsupported CMi ActivityReason: " + cmiActivityReason, 0);
        }
    }

    public static char getCmiOppositeSide(char fixSide) throws DataValidationException {
        if ((fixSide == Sides.BUY ) || (fixSide == (Sides.AS_DEFINED))){
            return Sides.OPPOSITE;
        }
        else if ((fixSide ==  Sides.SELL) ||(fixSide == (Sides.OPPOSITE))){
            return Sides.AS_DEFINED;
        }
        else {
            throw FixUtilDataValidationExceptionHelper.create(FixUtilConstants.Side.TAGNAME,
                                                            FixUtilConstants.Side.TAGNUMBER,
                                                            "Invalid side specified",fixSide,
                                                            FixUtilCmiErrorCodes.FixDataValidationCodes.INVALID_SIDE);
            }
        }

    public static String getFixMarketAlertType(short alertType) throws DataValidationException
    {
        switch (alertType) {

	//		case MarktAlertTypes.P_ORDER_PARTIAL_TRADED: return FixConstants.MarketAlertTypes.P_ORDER_PARTIAL_TRADED ;
			default: return Short.toString(alertType);

        }

    }

    /**********************************************************************/
    /**
    *  MAP PAR Exchange Strings to FIX Exchange (207) Strings
    *  The targetFixTagParm determines what happens to parExchangeCodes that have no translation
    *  
    *  tag 207 must be a valid Reuters code and will receive "W" (FixConstants.SecurityExchange.CBOE)
    *  other target tags (probably 9380) will receive the untranslatable input string unchanged)
    */
    public static String getFixExchangeFromPar( final String parExchangeCode, final String targetFixTag )
    {
        String result = null;
        if( parExchangeCode != null )
        {
            if(      parExchangeCode.equals(FixUtilConstants.SbtValues.ParExchange.CBOE)         ) result = FixUtilConstants.SecurityExchange.CBOE;
            else if( parExchangeCode.equals(FixUtilConstants.SbtValues.ParExchange.CINCINNATI)   ) result = FixUtilConstants.SecurityExchange.CINCINNATI;
            else if( parExchangeCode.equals(FixUtilConstants.SbtValues.ParExchange.AMERICAN)     ) result = FixUtilConstants.SecurityExchange.AMERICAN;
            else if( parExchangeCode.equals(FixUtilConstants.SbtValues.ParExchange.BOSTON)       ) result = FixUtilConstants.SecurityExchange.BOSTON;
            else if( parExchangeCode.equals(FixUtilConstants.SbtValues.ParExchange.CHICAGO)      ) result = FixUtilConstants.SecurityExchange.CHICAGO;
            else if( parExchangeCode.equals(FixUtilConstants.SbtValues.ParExchange.NASDAQ)       ) result = FixUtilConstants.SecurityExchange.NASDAQ;
            else if( parExchangeCode.equals(FixUtilConstants.SbtValues.ParExchange.NYSE)         ) result = FixUtilConstants.SecurityExchange.NYSE;
            else if( parExchangeCode.equals(FixUtilConstants.SbtValues.ParExchange.PACIFIC)      ) result = FixUtilConstants.SecurityExchange.PACIFIC;
            else if( parExchangeCode.equals(FixUtilConstants.SbtValues.ParExchange.PHILIDELPHIA) ) result = FixUtilConstants.SecurityExchange.PHILIDELPHIA;
        }
        if(result == null) {
            if ( targetFixTag.equals(FixUtilConstants.SecurityExchange.TAGNAME) ) { 
                result = FixUtilConstants.SecurityExchange.CBOE;
                if (Log.isDebugOn()) {
                    Log.debug("parExchangeCode: " + parExchangeCode + " unknown: converted to: " +  FixUtilConstants.SecurityExchange.CBOE );
                }
            } else {
                result = parExchangeCode ;
                if (Log.isDebugOn()) {
                    Log.debug("parExchangeCode: " + parExchangeCode + " unknown: passed on unchanged ");
                }
            }
        }
        return result;
    }
    /**********************************************************************/
    /**
    *  MAP Reuters Exchange Strings to PAR Exchange Strings
    */
    public static String getParExchangeFromFix( final String fixExchangeCode )
    {
        String result = null;
        if( fixExchangeCode != null )
        {
            if(      fixExchangeCode.equals(FixUtilConstants.SecurityExchange.CBOE)         ) result = FixUtilConstants.SbtValues.ParExchange.CBOE;
            else if( fixExchangeCode.equals(FixUtilConstants.SecurityExchange.CINCINNATI)   ) result = FixUtilConstants.SbtValues.ParExchange.CINCINNATI;
            else if( fixExchangeCode.equals(FixUtilConstants.SecurityExchange.AMERICAN)     ) result = FixUtilConstants.SbtValues.ParExchange.AMERICAN;
            else if( fixExchangeCode.equals(FixUtilConstants.SecurityExchange.BOSTON)       ) result = FixUtilConstants.SbtValues.ParExchange.BOSTON;
            else if( fixExchangeCode.equals(FixUtilConstants.SecurityExchange.CHICAGO)      ) result = FixUtilConstants.SbtValues.ParExchange.CHICAGO;
            else if( fixExchangeCode.equals(FixUtilConstants.SecurityExchange.NASDAQ)       ) result = FixUtilConstants.SbtValues.ParExchange.NASDAQ;
            else if( fixExchangeCode.equals(FixUtilConstants.SecurityExchange.NYSE)         ) result = FixUtilConstants.SbtValues.ParExchange.NYSE;
            else if( fixExchangeCode.equals(FixUtilConstants.SecurityExchange.PACIFIC)      ) result = FixUtilConstants.SbtValues.ParExchange.PACIFIC;
            else if( fixExchangeCode.equals(FixUtilConstants.SecurityExchange.PHILIDELPHIA) ) result = FixUtilConstants.SbtValues.ParExchange.PHILIDELPHIA;
        }
        if(result == null) {
            result = fixExchangeCode;
            if (Log.isDebugOn()) {
                Log.debug("fixExchangeCode: " + fixExchangeCode + " unknown: passed on unchanged ");
            }
        }
        return result;
    }

    /*------------------------------------------*/
    /**
    * Map cmi exchange Strings (probably from CurrentMarketStruct, NBBOStruct) 
    * to FIX (Reuters) exchange Strings
    **/
    public static String getFixExchangeFromCmi( final String cmiExchangeString )
    {
        String result = null;
        if( cmiExchangeString != null )
        {
            if(         cmiExchangeString.equals(ExchangeStrings.AMEX)     ) result = FixUtilConstants.SecurityExchange.AMERICAN;
            else if(    cmiExchangeString.equals(ExchangeStrings.BSE)      ) result = FixUtilConstants.SecurityExchange.BOSTON;
            else if(    cmiExchangeString.equals(ExchangeStrings.CBOE)     ) result = FixUtilConstants.SecurityExchange.CBOE;
            else if(    cmiExchangeString.equals(ExchangeStrings.CBOT)     ) result = FixUtilConstants.SecurityExchange.CBOT;
            else if(    cmiExchangeString.equals(ExchangeStrings.CHX)      ) result = FixUtilConstants.SecurityExchange.CHICAGO;
            else if(    cmiExchangeString.equals(ExchangeStrings.CME)      ) result = FixUtilConstants.SecurityExchange.CME;
            else if(    cmiExchangeString.equals(ExchangeStrings.CSE)      ) result = FixUtilConstants.SecurityExchange.CINCINNATI;
            else if(    cmiExchangeString.equals(ExchangeStrings.ISE)      ) result = FixUtilConstants.SecurityExchange.ISE;
            else if(    cmiExchangeString.equals(ExchangeStrings.LIFFE)    ) result = FixUtilConstants.SecurityExchange.LIFFE;
            else if(    cmiExchangeString.equals(ExchangeStrings.NASD)     ) result = FixUtilConstants.SecurityExchange.NASDAQ;
            else if(    cmiExchangeString.equals(ExchangeStrings.NYME)     ) result = FixUtilConstants.SecurityExchange.NYME;
            else if(    cmiExchangeString.equals(ExchangeStrings.NYSE)     ) result = FixUtilConstants.SecurityExchange.NYSE;
            else if(    cmiExchangeString.equals(ExchangeStrings.ONE)      ) result = FixUtilConstants.SecurityExchange.ONE;
            else if(    cmiExchangeString.equals(ExchangeStrings.PHLX)     ) result = FixUtilConstants.SecurityExchange.PHILIDELPHIA;
//          RaviR - Jun 18 2007
//          Changed as critical bugfix. OLA will use PCX for NYSE, but CBOE and other exchanges will be using NYSE  
//          Note : this method is not called by anybody. Changed for consistancy purpose            
            else if(    cmiExchangeString.equals(ExchangeStrings.NYSE)     ) result = FixUtilConstants.SecurityExchange.PACIFIC;
        //  else if(    cmiExchangeString.equals(ExchangeStrings.NQLX)     ) result = FixUtilConstants.SecurityExchange.NQLX; // ??
            else if(    cmiExchangeString.equals(ExchangeStrings.BOX)      ) result = FixUtilConstants.SecurityExchange.BOX;
        //  else if(    cmiExchangeString.equals(ExchangeStrings.CFE)      ) result = FixUtilConstants.SecurityExchange.CFE; // ??
        //  else if(    cmiExchangeString.equals(ExchangeStrings.NSX)      ) result = FixUtilConstants.SecurityExchange.NSX; // ??
            else if(    cmiExchangeString.equals(ExchangeStrings.NASDAQ)   ) result = FixUtilConstants.SecurityExchange.NASDAQAUTOQUOTE;
        }
        if(result == null) {
            result = cmiExchangeString;
            if (Log.isDebugOn()) {
                Log.debug("cmiExchangeString: " + cmiExchangeString + " not mapped: passed on unchanged ");
            }
        }
        return result;
    }

    public static String trimSpacesinBranchID(String branch) {
        String newBranch = branch.trim();
        Log.information("Branch Id has been trimmed - new branch id is |" + newBranch +"| :old is |"+branch+"|");
        return newBranch;
    }


}
