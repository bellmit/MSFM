package com.cboe.domain.util;

// import com.cboe.infrastructureServices.foundationFramework.FoundationFramework;
import com.cboe.exceptions.DataValidationException;
import com.cboe.idl.cmiConstants.ExtensionFields;
import com.cboe.idl.cmiErrorCodes.DataValidationCodes;
import com.cboe.idl.cmiOrder.FilledReportStruct;
import com.cboe.idl.cmiUtil.*;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.util.ExceptionBuilder;
import com.cboe.util.FormatNotFoundException;
import java.util.*;
import java.util.regex.Pattern;
import java.text.*;

/**
 * A wrapper to make it easier to parse fields in ExtensionsHelper
 */
public class ExtensionsFieldParser
{
    private static final String IPADDRESSDELIMITER = ".";
    private static final String COMMA_DELIMITER = ",";
    private static final String EMPTY_STRING = "";

    public static DateWrapper getTransactionTimeForLinkageFill(FilledReportStruct filledReport) throws DataValidationException
    {
        ExtensionsHelper helper=null;
        try
        
        {
             helper = new ExtensionsHelper(filledReport.extensions);
        }
        catch (java.text.ParseException e)
        {
            Log.exception("this.getClass().getName()", e);
        }

        if(helper == null)
        {
            throw ExceptionBuilder.dataValidationException("Away exchange transaction time is not given.", DataValidationCodes.INVALID_EXTENSIONS);
        }

        String temp = helper.getValue(ExtensionFields.AWAY_EXCHANGE_TRANSACT_TIME);
        if(temp == null)
        {
            throw ExceptionBuilder.dataValidationException("Away exchange transaction time is not given.", DataValidationCodes.INVALID_EXTENSIONS);
        }

        try
        {
            return DateWrapper.parse(DateWrapper.FIX_LINKAGE_DATE_FORMAT_NAME, temp);            
        }
        catch (FormatNotFoundException e)
        {
             throw ExceptionBuilder.dataValidationException(" Could not find the FIX_LINKAGE_DATE_FORMAT_NAME", DataValidationCodes.INVALID_TIME); //OrderID will be logged up in calling method.
        }
        catch (ParseException e)
        {
            throw ExceptionBuilder.dataValidationException(" Could not parse the AWAY_EXCHANGE_TRANSACT_TIME ", DataValidationCodes.INVALID_TIME); //OrderID will be logged up in calling method.
        }
        
    }
    
    public static String getFirmNumber(FilledReportStruct filledReport) throws DataValidationException
    {
        ExtensionsHelper helper=null;
        try
        {
             helper = new ExtensionsHelper(filledReport.extensions);
        }
        catch (java.text.ParseException e)
        {
            Log.exception("this.getClass().getName()", e);
        }

        if(helper == null)
        {
            throw ExceptionBuilder.dataValidationException("Away exchange extensions for firm not given.", DataValidationCodes.INVALID_EXTENSIONS);
        }

        String temp = helper.getValue(ExtensionFields.AWAY_EXCHANGE_EXEC_ID);
        if(temp == null)
        {
            throw ExceptionBuilder.dataValidationException("Away exchange extensions for firm is not given.", DataValidationCodes.INVALID_EXTENSIONS);
        }
        
        return temp;
    }
    
    public static String getFadeExchange(String extensions)
    {
        ExtensionsHelper helper=null;
        try
        {
             helper = new ExtensionsHelper(extensions);
        }
        catch (java.text.ParseException e)
        {
            Log.exception("this.getClass().getName()", e);
        }

      

        String temp = helper.getValue(ExtensionFields.FADE_EXCHANGE);
        
        
        return temp;
    }
    
    public static void setFadeExchange(FilledReportStruct filledReport,String extension) throws DataValidationException
    {
        ExtensionsHelper helper=null;
        try
        {
             helper = new ExtensionsHelper(filledReport.extensions);
        }
        catch (java.text.ParseException e)
        {
            Log.exception("this.getClass().getName()", e);
        }

        if(helper == null)
        {
            throw ExceptionBuilder.dataValidationException("FADE_EXCHANGE extensions  not given.", DataValidationCodes.INVALID_EXTENSIONS);
        }

        try
        {             
                StringBuilder buf = new StringBuilder(45);
                buf.append(ExtensionFields.FADE_EXCHANGE)
                .append("=")
                .append(extension)
                .append(helper.getFieldDelimiter());
                helper.appendExtensions(buf.toString()); 
                                                             
                filledReport.extensions=(helper.toString());
        }
        catch (ParseException e)
        {
            throw ExceptionBuilder.dataValidationException("FADE_EXCHANGE extensions  is not set.", DataValidationCodes.INVALID_EXTENSIONS);

        }
    }
       
    public static String getOutboundVendor(FilledReportStruct filledReport) throws DataValidationException
    {
        ExtensionsHelper helper = null;
        try
        {
             helper = new ExtensionsHelper(filledReport.extensions);
        }
        catch (java.text.ParseException e)
        {
            Log.exception("this.getClass().getName()", e);
        }

        if (helper == null)
        {
            throw ExceptionBuilder.dataValidationException("Outbound Vendor extensions not given.", DataValidationCodes.INVALID_EXTENSIONS);
        }
        String temp = helper.getValue(ExtensionFields.BROKER_ROUTING_ID);
        if (temp == null)
        {
            throw ExceptionBuilder.dataValidationException("Outbound Vendor extensions not given.", DataValidationCodes.INVALID_EXTENSIONS);
        }
        
        return temp;
    }


    /**
     * Checks if an order is marked for DAIM
     * @param extensions
     * @return
     * @throws DataValidationException
     */
    public static boolean isOrderMarkedForDAIM(String extensions)throws DataValidationException
    {
    	boolean orderMarkedForDAIM = (getFirmForDirectedAIM(extensions) != null)? true:false;
    	return orderMarkedForDAIM;
    }
    
    /**
     * Gets the target firm from the order extensions.
     * @param extensions
     * @return
     * @throws DataValidationException
     */
    public static String getFirmForDirectedAIM(String extensions)throws DataValidationException
    {
        ExtensionsHelper helper=null;
        String directedFirm = null;
        try
        {
             helper = new ExtensionsHelper(extensions);
        }
        catch (java.text.ParseException e)
        {
            Log.exception("this.getClass().getName()", e);
        }
        if(helper == null)
        {
            throw ExceptionBuilder.dataValidationException("ExtensionsHelper for DAIM FIRM parsing failed to initialize.", DataValidationCodes.INVALID_EXTENSIONS);
        }
        directedFirm = helper.getValue(ExtensionFields.DIRECTED_FIRM);
        return directedFirm;
    }
    
    public static String getLocationId(String extensions) throws DataValidationException
    {
        ExtensionsHelper helper = getExtensionsHelper(extensions);
        if (null != helper) {
            String locationId = helper.getValue(ExtensionFields.MANUAL_QUOTE_LOCATION_ID);
            return null == locationId ? EMPTY_STRING : locationId;
        }
        return EMPTY_STRING;
    }

    public static String getIpAddress(String extensions) throws DataValidationException
    {
        ExtensionsHelper helper = getExtensionsHelper(extensions);
        if (null == helper) {
            return EMPTY_STRING;
        }
        String incomingIpAddress = helper.getValue(ExtensionFields.MANUAL_QUOTE_IP_ADDRESS);
        if (null == incomingIpAddress ) {
            return EMPTY_STRING;
        }
        String parsedIp = incomingIpAddress;
        try{
            StringTokenizer str = new StringTokenizer(incomingIpAddress, IPADDRESSDELIMITER);
            if(str.countTokens() == 4) {
                String firstSegment =  str.nextToken();
                String secondSegement = str.nextToken();
                String thirdSegement = str.nextToken();
                String fourthSegement = str.nextToken();
                parsedIp = thirdSegement + IPADDRESSDELIMITER + fourthSegement;
            }
        }
        catch(Exception exe) {
            Log.exception("Unable to Parse Manual Quote, will send the ip address as is: ", exe);
        }

        return null == parsedIp ? EMPTY_STRING : parsedIp;
    }

    public static String getParId(String extensions) throws DataValidationException
    {
        ExtensionsHelper helper = getExtensionsHelper(extensions);
         if (null != helper) {
             String parId = helper.getValue(ExtensionFields.MANUAL_QUOTE_PAR_ID);
             return null == parId ? EMPTY_STRING : parId;
        }
        return EMPTY_STRING;
    }

    public static String replaceWithDefaultDelimiter(String keyAndValue)
    {
        if (keyAndValue.indexOf(COMMA_DELIMITER) > -1) {
            return keyAndValue.replaceAll(COMMA_DELIMITER, ExtensionsHelper.DEFAULT_FIELD_DELIMITER);
        }
        else {
            return keyAndValue;
        }
    }

    public static boolean isManualQuoteEnabled(String extensions)
    {
        if (null == extensions || extensions.trim().length() == 0 ) {
            return false;
        }
        ExtensionsHelper helper = getExtensionsHelper(extensions);
        if (null == helper) {
            return false;
        }
        String manulQuote = helper.getValue(ExtensionFields.MANUAL_ORDER_ENABLE);
        if (null == manulQuote) {
            return false;
        }
        return (Boolean.TRUE.toString().equalsIgnoreCase(manulQuote));
    }
    
    private static ExtensionsHelper getExtensionsHelper(String keyAndValues)
    {
        ExtensionsHelper helper = null;
        if (keyAndValues.length() > 0) {
            try {
                 helper = new ExtensionsHelper(replaceWithDefaultDelimiter(keyAndValues));
            }
            catch (java.text.ParseException e) {
                Log.exception("this.getClass().getName()", e);
            }
            if(helper == null) {
                Log.information("Faile to parse the keyAndValues: " + keyAndValues);
            }
        }
        return helper;
    }
}
