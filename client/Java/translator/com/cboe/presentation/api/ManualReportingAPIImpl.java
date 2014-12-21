//
// -----------------------------------------------------------------------------------
// Source file: ManualReportingAPIImpl.java
//
// PACKAGE: com.cboe.presentation.api
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2007 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.api;

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
import com.cboe.interfaces.presentation.api.ManualReportingAPI;
import com.cboe.presentation.common.logging.GUILoggerBusinessProperty;
import com.cboe.presentation.common.logging.GUILoggerHome;

public class ManualReportingAPIImpl implements ManualReportingAPI {

    private ManualReportingService mrService;

    public ManualReportingAPIImpl()
    {
    }

    public ManualReportingAPIImpl(ManualReportingService mrService)
    {
        initializeService(mrService);
    }

    public void initializeService(ManualReportingService mrService)
    {
        this.mrService = mrService;
    }

    private void verifyService()
    {
        if(mrService == null)
        {
            throw new IllegalStateException("ManualReporting service has not been initialized yet.");
        }
    }

    public void acceptManualQuote(ManualQuoteStruct manualQuote)
            throws SystemException, CommunicationException, AuthorizationException,
                DataValidationException, NotFoundException, TransactionFailedException, NotAcceptedException
    {
        GUILoggerHome.find().debug(getClass().getName() + ": acceptManualQuote",
                                   GUILoggerBusinessProperty.MANUAL_REPORTING, manualQuote);

        verifyService();

        mrService.acceptManualQuote(manualQuote);
    }

    public void cancelManualQuote(ManualQuoteStruct manualQuote)
            throws SystemException, CommunicationException, AuthorizationException,
                DataValidationException, NotFoundException, TransactionFailedException, NotAcceptedException
    {
        GUILoggerHome.find().debug(getClass().getName() + ": cancelManualQuote",
                                   GUILoggerBusinessProperty.MANUAL_REPORTING, manualQuote);

        verifyService();

        mrService.cancelManualQuote(manualQuote);
    }

    public void acceptManualPriceReport(ManualPriceReportEntryStruct manualPrice)
            throws SystemException, CommunicationException, AuthorizationException,
                DataValidationException, NotAcceptedException
    {
        GUILoggerHome.find().audit(getClass().getName() + ": acceptManualPriceReport",
                                   manualPrice);

        verifyService();

        mrService.acceptManualPriceReport(manualPrice);
    }

    public MessageResultStruct sendMessage(DestinationStruct[] recipients, MessageTransportStruct message) throws SystemException, CommunicationException, AuthorizationException, DataValidationException {
        if (GUILoggerHome.find().isDebugOn())
        {
            Object[] argObj = new Object[2];
            argObj[0] = recipients[0];
            argObj[1] = message;
            GUILoggerHome.find().debug(getClass().getName() +": sendMessage ",
                    GUILoggerBusinessProperty.MANUAL_REPORTING, argObj);
        }

        verifyService();

        return mrService.sendMessage(recipients,message);
    }

    public ProductClassVolumeStruct getProductClassVolume(String sessionName, int productClassKey) throws SystemException, NotFoundException, DataValidationException, CommunicationException, AuthorizationException
    {
        GUILoggerHome.find().debug(getClass().getName() + ": getProductClassVolume",
                                   GUILoggerBusinessProperty.MANUAL_REPORTING, productClassKey);

        verifyService();

        return mrService.getProductClassVolume(sessionName, productClassKey);
    }
    
    public ClassRecapStructV5  getRecapForProduct(String sessionName, int productKey) throws SystemException, NotFoundException, DataValidationException, CommunicationException, AuthorizationException
    {
        GUILoggerHome.find().debug(getClass().getName() + ": getRecapForProduct",
                GUILoggerBusinessProperty.MANUAL_REPORTING, productKey);

        verifyService();

        return mrService.getRecapForProduct(sessionName, productKey);
    }
}
