//
// -----------------------------------------------------------------------------------
// Source file: ManualReportingAPI.java
//
// PACKAGE: com.cboe.interfaces.presentation.api
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2007 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.presentation.api;

import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.NotAcceptedException;
import com.cboe.exceptions.NotFoundException;
import com.cboe.exceptions.SystemException;
import com.cboe.exceptions.TransactionFailedException;
import com.cboe.idl.cmiMarketData.ClassRecapStructV5;
import com.cboe.idl.cmiMarketData.ProductClassVolumeStruct;
import com.cboe.idl.floorApplication.ManualReportingService;
import com.cboe.idl.marketData.ManualPriceReportEntryStruct;
import com.cboe.idl.quote.ManualQuoteStruct;
import com.cboe.idl.textMessage.DestinationStruct;
import com.cboe.idl.textMessage.MessageResultStruct;
import com.cboe.idl.textMessage.MessageTransportStruct;

public interface ManualReportingAPI {

    void acceptManualQuote(ManualQuoteStruct manualQuote)
            throws SystemException, CommunicationException, AuthorizationException,
                DataValidationException, NotFoundException, TransactionFailedException, NotAcceptedException;

    void cancelManualQuote(ManualQuoteStruct manualQuote)
            throws SystemException, CommunicationException, AuthorizationException,
                DataValidationException, NotFoundException, TransactionFailedException, NotAcceptedException;

    void acceptManualPriceReport(ManualPriceReportEntryStruct manualPrice)
            throws SystemException, CommunicationException, AuthorizationException,
                DataValidationException, NotAcceptedException;


    MessageResultStruct sendMessage( DestinationStruct[] receipients, MessageTransportStruct message)
    	    throws SystemException, CommunicationException, AuthorizationException,
                DataValidationException;

    ProductClassVolumeStruct getProductClassVolume(String sessionName, int theKey)
	        throws SystemException, NotFoundException, DataValidationException, CommunicationException,
                AuthorizationException;
    
    ClassRecapStructV5  getRecapForProduct(String sessionName, int productKey)
    		throws SystemException, NotFoundException, DataValidationException, CommunicationException,
    			AuthorizationException;

    void initializeService(ManualReportingService mrService);
}
