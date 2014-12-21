package com.cboe.floorApplication.manualReporting;

import com.cboe.idl.quote.ManualQuoteStruct;
import com.cboe.idl.marketData.ManualPriceReportEntryStruct;
import com.cboe.idl.textMessage.MessageResultStruct;
import com.cboe.idl.textMessage.DestinationStruct;
import com.cboe.idl.textMessage.MessageTransportStruct;
import com.cboe.idl.cmiMarketData.ProductClassVolumeStruct;
import com.cboe.idl.cmiMarketData.ClassRecapStructV5;
import com.cboe.idl.cmiOrder.OrderStruct;

import com.cboe.instrumentationService.transactionTimingCommon.TransactionTimer;
import com.cboe.interfaces.application.SessionManager;
import com.cboe.infrastructureServices.foundationFramework.BObject;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.floorApplication.ManualReportingService;
import com.cboe.interfaces.businessServices.MarketMakerQuoteService;
import com.cboe.interfaces.businessServices.MarketDataService;
import com.cboe.interfaces.businessServices.TextMessagingService;
import com.cboe.interfaces.businessServices.MarketDataReportService;
import com.cboe.exceptions.*;
import com.cboe.application.shared.ServicesHelper;
import com.cboe.application.shared.TransactionTimingRegistration;
import com.cboe.application.shared.TransactionTimingUtil;

import org.omg.CORBA.UserException;

/**
 * Author: mahoney
 * Date: Jul 18, 2007
 */
public class ManualReportingServiceImpl extends BObject implements ManualReportingService
{
    private SessionManager currentSession;
    private MarketMakerQuoteService marketMakerQuoteService;
    private MarketDataService marketDataService;
    private MarketDataReportService marketDataReportService;
    private TextMessagingService textMessageService;
    private String userId;


    public ManualReportingServiceImpl(SessionManager sessionManager)
    {
        super();
        currentSession = sessionManager;

        try
        {
            userId = sessionManager.getValidUser().userId;
        }
        catch(UserException e)
        {
            Log.exception(this, "could not get user id from session : " + sessionManager, e);
        }
    }

    public void create(String name)
    {
        super.create(name);
        marketMakerQuoteService = ServicesHelper.getMarketMakerQuoteService();
        marketDataService = ServicesHelper.getMarketDataService();
        marketDataReportService = ServicesHelper.getMarketDataReportService();
        textMessageService = ServicesHelper.getTextMessagingService();
    }

    public void acceptManualQuote(ManualQuoteStruct manualQuoteStruct)
            throws SystemException, CommunicationException, DataValidationException, NotFoundException,
                AuthorizationException, TransactionFailedException, NotAcceptedException
    {
    	
        String us = currentSession.toString();
        StringBuilder calling = new StringBuilder(us.length()+55);
        calling.append("calling acceptManualQuote for sessionManager: ").append(us);
        Log.information(this, calling.toString());
        boolean exceptionWasThrown = true;
        long entityId = 0L;
        
        try
        {
        	entityId = TransactionTimingUtil.setEntityID();
        }
        catch (Exception e)
        {
        	Log.information(this, "Unable to set EntityID! Exception details: " + e.getMessage());
        }
        
 		TransactionTimingUtil.generateOrderEvent(TransactionTimingRegistration.getManualQuoteEmitPoint(), entityId, TransactionTimer.Enter );
       	try{	
       		marketMakerQuoteService.acceptManualQuote(manualQuoteStruct);
       		exceptionWasThrown = false;
       	}
       	finally
       	{
	    	TransactionTimingUtil.generateOrderEvent(TransactionTimingRegistration.getManualQuoteEmitPoint(), entityId,
	        exceptionWasThrown?TransactionTimer.LeaveWithException:TransactionTimer.Leave );
       	}
    }

    public void cancelManualQuote(ManualQuoteStruct manualQuoteStruct)
            throws SystemException, CommunicationException, DataValidationException, NotFoundException,
                AuthorizationException, TransactionFailedException, NotAcceptedException
    {
        String us = currentSession.toString();
        StringBuilder calling = new StringBuilder(us.length()+55);
        calling.append("calling cancelManualQuote for sessionManager: ").append(us);
        Log.information(this, calling.toString());
        boolean exceptionWasThrown = true;
        long entityId = 0L;
        
        try
        {
        	entityId = TransactionTimingUtil.setEntityID();
        }
        catch (Exception e)
        {
        	Log.information(this, "Unable to set EntityID! Exception details: " + e.getMessage());
        }
        
 		TransactionTimingUtil.generateOrderEvent(TransactionTimingRegistration.getCancelManualQuoteEmitPoint(), entityId, TransactionTimer.Enter );
       	try{
       		marketMakerQuoteService.cancelManualQuote(manualQuoteStruct);
       		exceptionWasThrown = false;
       	}
       	finally
       	{
	    	TransactionTimingUtil.generateOrderEvent(TransactionTimingRegistration.getManualQuoteEmitPoint(), entityId,
	        exceptionWasThrown?TransactionTimer.LeaveWithException:TransactionTimer.Leave );
       	}
    }

    public void acceptManualPriceReport(ManualPriceReportEntryStruct manualPriceReportEntryStruct)
            throws SystemException, CommunicationException, DataValidationException, AuthorizationException, NotAcceptedException
    {
        String us = currentSession.toString();
        StringBuilder calling = new StringBuilder(us.length()+55);
        calling.append("calling acceptManualPriceReport for sessionManager: ").append(us);
        Log.information(this, calling.toString());
        marketDataService.acceptManualPriceReport(userId, manualPriceReportEntryStruct);
    }

    public MessageResultStruct sendMessage(DestinationStruct[] recipients, MessageTransportStruct messageTransportStruct)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        String us = currentSession.toString();
        StringBuilder calling = new StringBuilder(us.length()+55);
        calling.append("calling sendMessage for sessionManager: ").append(us);
        Log.information(this, calling.toString());
        return textMessageService.sendMessage(recipients, messageTransportStruct);
    }

	public ProductClassVolumeStruct getProductClassVolume(String sessionName, int classKey)
	    throws SystemException, NotFoundException, DataValidationException, CommunicationException,AuthorizationException
    {
        String us = currentSession.toString();
        StringBuilder calling = new StringBuilder(us.length()+55);
        calling.append("calling getProductClassVolume for sessionManager: ").append(us);
        Log.information(this, calling.toString());
        return marketDataReportService.getProductClassVolume(sessionName, classKey);
    }

    public ClassRecapStructV5 getRecapForProduct(String sessionName, int productKey) throws SystemException, CommunicationException,
            DataValidationException, AuthorizationException, NotFoundException
    {
        String us = currentSession.toString();
        StringBuilder calling = new StringBuilder(us.length()+55);
        calling.append("calling getRecapForProduct for sessionManager: ").append(us);
        Log.information(this, calling.toString());
        return marketDataReportService.getRecapForProduct(sessionName, productKey);
    }
}
