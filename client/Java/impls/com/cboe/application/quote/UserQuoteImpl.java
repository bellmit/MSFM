package com.cboe.application.quote;

import com.cboe.application.quote.common.QuoteStructBuilder;
import com.cboe.application.shared.ServicesHelper;
import com.cboe.application.shared.TransactionTimingUtil;
import com.cboe.application.shared.TransactionTimingRegistration;
import com.cboe.application.shared.LoggingUtil;
import com.cboe.application.supplier.UserSessionBaseSupplier;
import com.cboe.application.util.QuoteCallSnapshot;
//import com.cboe.domain.instrumentedChannel.event.InstrumentedEventChannelAdapterFactory;
import com.cboe.domain.supplier.proxy.GMDSupplierProxy;
import com.cboe.domain.util.*;
import com.cboe.exceptions.*;
import com.cboe.idl.cmiCallback.CMIRFQConsumer;
import com.cboe.idl.cmiCallbackV2.CMILockedQuoteStatusConsumer;
import com.cboe.idl.cmiErrorCodes.DataValidationCodes;
import com.cboe.idl.cmiQuote.*;
import com.cboe.idl.user.SessionProfileUserStructV2;
import com.cboe.infrastructureServices.foundationFramework.HomeFactory;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.instrumentationService.transactionTimingCommon.TransactionTimer;
import com.cboe.interfaces.application.QuoteStatusConsumerProxyHome;
import com.cboe.interfaces.application.QuoteV7;
import com.cboe.interfaces.domain.instrumentedChannel.InstrumentedChannelListenerProxy;
import com.cboe.util.*;
import com.cboe.util.channel.ChannelListener;
import com.cboe.util.channel.ChannelListenerProxy;
import com.cboe.util.channel.proxy.BaseChannelListenerProxy;
import static com.cboe.application.shared.LoggingUtil.*;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

/**
* Implemetation of the quote interface
* 
* @version 1/18/2008
*/
public class UserQuoteImpl extends UserQuoteBasicCollectorImpl implements  QuoteV7
{
    protected QuoteStructBuilder                 quoteStructBuilder;
    private static final String EMPTY_QUOTE_ARRAY_MESSAGE = "There is no data in quotes array.";

    public UserQuoteImpl()
    {
        super();
    }

    public void create(String name)
    {
        super.create(name);
        quoteStructBuilder = new QuoteStructBuilder(currentSession);
    }

    public void acceptUserSessionLogout() {
    	super.acceptUserSessionLogout();
    	quoteStructBuilder = null;
    }
    
    /////////////// IDL exported methods ////////////////////////////////////

    public void acceptQuote(QuoteEntryStruct quote)
       throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotAcceptedException, TransactionFailedException
    {
        QuoteCallSnapshot.enter();
        boolean exceptionWasThrown = true;
        long entityId = 0L;
        try
        {
            entityId = TransactionTimingUtil.setTTContext();
        }
        catch (Exception e)
        {
            Log.information(this, "Unable to set EntityID! Exception details: " + e.getMessage());
        }
        String sessInfo = currentSession.toString();
        Log.information(this, createQuoteLog("acceptQuote", entityId, sessInfo, quote.sessionName, quote.productKey));
        int classKey = getClassKey(quote.productKey);
        ClassQuoteResultStructV3[] classResultV3;
        
     	TransactionTimingUtil.generateQuoteEvent(TransactionTimingRegistration.getQuoteEmitPoint(), entityId, TransactionTimer.Enter );
        
        classResultV3 = userQuoteService.acceptQuotesForClassV3(classKey, quoteStructBuilder.buildQuoteStructsV3(quote));
        exceptionWasThrown = false;
        TransactionTimingUtil.generateQuoteEvent(TransactionTimingRegistration.getQuoteEmitPoint(), entityId,
             exceptionWasThrown?TransactionTimer.LeaveWithException:TransactionTimer.Leave );
        
        QuoteCallSnapshot.done();
        String snapshot = createQuoteLogSnapshot("acceptQuote", classKey, quote.sessionName, quote.userAssignedId, classResultV3[0].quoteResult.quoteKey, entityId, sessInfo);
        StringBuilder snap = new StringBuilder(snapshot.length()+TOKEN_DELIMITER.length()+PRODUCT_KEY.length()+VALUE_DELIMITER.length()+11);
        snap.append(snapshot).append(TOKEN_DELIMITER).append(PRODUCT_KEY).append(VALUE_DELIMITER).append(quote.productKey);
        Log.information(this, snap.toString());
    }

    public ClassQuoteResultStruct[] acceptQuotesForClass(int classKey, QuoteEntryStruct[] quotes)
         throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotAcceptedException, TransactionFailedException
    {
        if(quotes==null||quotes.length==0){
            Log.information(this, EMPTY_QUOTE_ARRAY_MESSAGE);
            throw ExceptionBuilder.dataValidationException(EMPTY_QUOTE_ARRAY_MESSAGE, DataValidationCodes.INCOMPLETE_QUOTE);
        }

        QuoteCallSnapshot.enter();
        boolean exceptionWasThrown = true;
        long entityId = 0L;
        try
        {
            entityId = TransactionTimingUtil.setTTContext();
        }
        catch (Exception e)
        {
            Log.information(this, "Unable to set EntityID! Exception details: " + e.getMessage());
        }
        String sessInfo = currentSession.toString();
        Log.information(this,createQuoteLog("acceptQuotesForClass", entityId, sessInfo, quotes[0].sessionName, classKey, quotes.length));
        ClassQuoteResultStruct[] classResult;
        ClassQuoteResultStructV3[] classResultV3;
        
        TransactionTimingUtil.generateQuoteEvent(TransactionTimingRegistration.getQuoteEmitPoint(), entityId, TransactionTimer.Enter );
        
        classResultV3 = userQuoteService.acceptQuotesForClassV3(classKey, quoteStructBuilder.buildQuoteStructsV3(quotes));
        classResult = quoteStructBuilder.buildClassQuoteResultsV1(classResultV3);
        exceptionWasThrown = false;       
        TransactionTimingUtil.generateQuoteEvent(TransactionTimingRegistration.getQuoteEmitPoint(), entityId,
            exceptionWasThrown?TransactionTimer.LeaveWithException:TransactionTimer.Leave );
        
        QuoteCallSnapshot.done();
        Log.information(this, createQuoteLogSnapshot("acceptQuotesForClass", classKey, quotes[0].sessionName, quotes[0].userAssignedId, classResultV3[0].quoteResult.quoteKey, entityId, sessInfo));
        return classResult;
    }

    public QuoteDetailStruct getQuote(String sessionName, int productKey)
       throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        return userQuoteService.getQuote(sessionName, productKey);
    }

    public void subscribeQuoteStatus(com.cboe.idl.cmiCallback.CMIQuoteStatusConsumer clientListener, boolean gmdCallback)
       throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (Log.isDebugOn()) {Log.debug(this, "calling subscribeQuoteStatus for " + currentSession + " gmd:" + gmdCallback);}
        subscribeQuoteStatusWithoutPublish(clientListener, gmdCallback);
        queryQuoteStatus(true);
    }

    public void subscribeQuoteStatusWithoutPublish(com.cboe.idl.cmiCallback.CMIQuoteStatusConsumer clientListener, boolean gmdCallback)
       throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (Log.isDebugOn()) {Log.debug(this, "calling subscribeQuoteStatusWithoutPublish for " + currentSession + " gmd:" + gmdCallback);}

        ChannelListener proxyListener =
            ServicesHelper.getQuoteStatusConsumerProxy(
                clientListener,
                currentSession,
                gmdCallback);

        checkProxy(quoteStatusSupplier, proxyListener, gmdCallback, true, null);
        addAllQuoteConsumer(proxyListener);
        // request all unAck quotes from the QSSS/QSS
        userQuoteService.publishUnAckedQuotes();
    }

    public void subscribeQuoteStatusForFirmWithoutPublish(com.cboe.idl.cmiCallback.CMIQuoteStatusConsumer clientListener, boolean gmdCallback)
       throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (Log.isDebugOn()) {Log.debug(this, "calling subscribeQuoteStatusForFirm for " + currentSession + " gmd:" + gmdCallback);}

        if ( clientListener != null)
        {
            ChannelListener proxyListener =
                ServicesHelper.getQuoteStatusConsumerProxy(
                    clientListener,
                    currentSession,
                    gmdCallback);

            if(currentSession.isTradingFirmEnabled())
            {
                subscribeQuoteStatusForTradingFirm(proxyListener);
                return;
            }

            checkProxy(quoteStatusSupplier, proxyListener, gmdCallback, false, null);

            ///////// add the call back consumer to the supplier list/////
            ChannelKey channelKey = new ChannelKey(ChannelType.CB_QUOTE_FILLED_REPORT_BY_FIRM, thisFirmKeyContainer);
            quoteStatusSupplier.addChannelListener(this, proxyListener, channelKey, thisFirmKeyContainer);

            channelKey = new ChannelKey(ChannelType.CB_QUOTE_BUST_REPORT_BY_FIRM, thisFirmKeyContainer);
            quoteStatusSupplier.addChannelListener(this, proxyListener, channelKey, thisFirmKeyContainer);

            //now subscribe to CBOE events
            channelKey = new ChannelKey(ChannelType.QUOTE_FILL_REPORT_BY_FIRM, thisFirmKeyContainer);
            addListenerForSupplier(channelKey);

            channelKey = new ChannelKey(ChannelType.QUOTE_BUST_REPORT_BY_FIRM, thisFirmKeyContainer);
            addListenerForSupplier(channelKey);

            subscriptionService.addFirmInterest(proxyListener);
        }
    }

    private void subscribeQuoteStatusForTradingFirm(ChannelListener proxyListener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling subscribeQuoteStatusForTradingFirm for " + currentSession);
        }

        List<String> users = new ArrayList<String>();
        users.add(currentSession.getUserId());
        users.addAll(currentSession.getTradingFirmGroup());
        for(String user : users)
        {
            ChannelKey channelKey;

            channelKey = new ChannelKey(ChannelType.CB_QUOTE_FILLED_REPORT_BY_TRADING_FIRM, user);
            quoteStatusSupplier.addChannelListener(this, proxyListener, channelKey, user);

            channelKey = new ChannelKey(ChannelType.CB_QUOTE_BUST_REPORT_BY_TRADING_FIRM, user);
            quoteStatusSupplier.addChannelListener(this, proxyListener, channelKey, user);

            //now subscribe to CBOE events
            channelKey = new ChannelKey(ChannelType.QUOTE_FILL_REPORT_BY_TRADING_FIRM, user);
            quoteStatusCollectorSupplier.addChannelListener(this, quoteStatusProxy, channelKey);

            channelKey = new ChannelKey(ChannelType.QUOTE_BUST_REPORT_BY_TRADING_FIRM, user);
            quoteStatusCollectorSupplier.addChannelListener(this, quoteStatusProxy, channelKey);
        }

        subscriptionService.addTradingFirmInterest(proxyListener, users);
    }

    private void subscribeQuoteStatusForTradingFirmV2(ChannelListener proxyListener)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling subscribeQuoteStatusForTradingFirmV2 for " + currentSession);
        }

        List<String> users = new ArrayList<String>();
        users.add(currentSession.getUserId());
        users.addAll(currentSession.getTradingFirmGroup());
        for(String user : users)
        {
            ChannelKey channelKey;

            channelKey = new ChannelKey(ChannelType.CB_QUOTE_FILLED_REPORT_BY_TRADING_FIRM, user);
            quoteStatusV2Supplier.addChannelListener(this, proxyListener, channelKey, user);

            channelKey = new ChannelKey(ChannelType.CB_QUOTE_BUST_REPORT_BY_TRADING_FIRM, user);
            quoteStatusV2Supplier.addChannelListener(this, proxyListener, channelKey, user);

            //now subscribe to CBOE events
            channelKey = new ChannelKey(ChannelType.QUOTE_FILL_REPORT_BY_TRADING_FIRM, user);
            quoteStatusCollectorSupplier.addChannelListener(this, quoteStatusProxy, channelKey);

            channelKey = new ChannelKey(ChannelType.QUOTE_BUST_REPORT_BY_TRADING_FIRM, user);
            quoteStatusCollectorSupplier.addChannelListener(this, quoteStatusProxy, channelKey);
        }

        subscriptionService.addTradingFirmInterest(proxyListener, users);
    }

    public void subscribeQuoteStatusForFirm(com.cboe.idl.cmiCallback.CMIQuoteStatusConsumer clientListener, boolean gmdCallback)
       throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (Log.isDebugOn()) {Log.debug(this, "calling subscribeQuoteStatusForFirmWithoutPublish for " + currentSession + " gmd:" + gmdCallback);}
        subscribeQuoteStatusForFirmWithoutPublish(clientListener, gmdCallback);
    }

    public void cancelQuote(String sessionName, int productKey)
         throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotAcceptedException, TransactionFailedException, NotFoundException
    {
        QuoteCallSnapshot.enter();
        boolean exceptionWasThrown  = true;
        long entityId = 0L;
        try
        {
            entityId = TransactionTimingUtil.setEntityID();
        }
        catch (Exception e)
        {
            Log.information(this, "Unable to set EntityID! Exception details: " + e.getMessage());
        }
        String sessInfo = currentSession.toString();
        int classkey;       
             
    	TransactionTimingUtil.generateQuoteEvent(TransactionTimingRegistration.getCancelQuoteEmitPoint(), entityId, TransactionTimer.Enter );
        
        classkey = userQuoteService.cancelQuote(sessionName, productKey);
        exceptionWasThrown = false;
        TransactionTimingUtil.generateQuoteEvent(TransactionTimingRegistration.getCancelQuoteEmitPoint(), entityId,
             exceptionWasThrown?TransactionTimer.LeaveWithException:TransactionTimer.Leave );
       
        QuoteCallSnapshot.done();
        Log.information(this, createQuoteLogSnapshotForCancel("cancelQuote", sessionName, classkey, productKey, entityId, sessInfo));
    }

    public void cancelAllQuotes(String sessionName)
           throws SystemException, CommunicationException, AuthorizationException, TransactionFailedException, NotAcceptedException
    {
        try
        {
            QuoteCallSnapshot.enter();
            boolean exceptionWasThrown  = true;
            long entityId = 0L;
            try
            {
                entityId = TransactionTimingUtil.setEntityID();
                
            }
            catch (Exception e)
            {
                Log.information(this, "Unable to set EntityID! Exception details: " + e.getMessage());
            }
            
            
            String sessInfo = currentSession.toString();
        	TransactionTimingUtil.generateQuoteEvent(TransactionTimingRegistration.getCancelQuoteEmitPoint(), entityId, TransactionTimer.Enter );
            
            userQuoteService.cancelAllQuotes(sessionName);
            exceptionWasThrown = false;       
            TransactionTimingUtil.generateQuoteEvent(TransactionTimingRegistration.getCancelQuoteEmitPoint(), entityId,
                exceptionWasThrown?TransactionTimer.LeaveWithException:TransactionTimer.Leave );
            
            QuoteCallSnapshot.done();
            Log.information(this, createQuoteLogSnapshotForCancel("cancelAllQuotes", sessionName, 0, 0, entityId, sessInfo));
        }
        catch(DataValidationException e)
        {
            Log.exception(this, e);
        }
    }

    public void cancelQuotesByClass(String sessionName, int classKey)
         throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotAcceptedException, TransactionFailedException, NotFoundException
    {
        QuoteCallSnapshot.enter();
        boolean exceptionWasThrown  = true;
        long entityId = 0L;
        try
        {
            entityId = TransactionTimingUtil.setEntityID();
        }
        catch (Exception e)
        {
            Log.information(this, "Unable to set EntityID! Exception details: " + e.getMessage());
        }
        
        String sessInfo = currentSession.toString();       
    	TransactionTimingUtil.generateQuoteEvent(TransactionTimingRegistration.getCancelQuoteEmitPoint(), entityId, TransactionTimer.Enter );
        
        userQuoteService.cancelQuotesByClass(sessionName, classKey);
        exceptionWasThrown = false;
        TransactionTimingUtil.generateQuoteEvent(TransactionTimingRegistration.getCancelQuoteEmitPoint(), entityId,
            exceptionWasThrown?TransactionTimer.LeaveWithException:TransactionTimer.Leave );
        
        QuoteCallSnapshot.done();
        Log.information(this, createQuoteLogSnapshotForCancel("cancelQuotesByClass", sessionName, classKey, 0, entityId, sessInfo));
    }

    public void cancelQuoteV5(java.lang.String sessionName, int productKey, boolean sendCancelReports)
        throws com.cboe.exceptions.SystemException, com.cboe.exceptions.CommunicationException, com.cboe.exceptions.AuthorizationException, com.cboe.exceptions.DataValidationException, com.cboe.exceptions.TransactionFailedException, com.cboe.exceptions.NotAcceptedException, com.cboe.exceptions.NotFoundException
    {
        QuoteCallSnapshot.enter();
        boolean exceptionWasThrown  = true;
        long entityId = 0L;
        try
        {
            entityId = TransactionTimingUtil.setEntityID();
        }
        catch (Exception e)
        {
            Log.information(this, "Unable to set EntityID! Exception details: " + e.getMessage());
        }
        
        String sessInfo = currentSession.toString();    
    	TransactionTimingUtil.generateQuoteEvent(TransactionTimingRegistration.getCancelQuoteEmitPoint(), entityId, TransactionTimer.Enter );
        
        userQuoteService.cancelQuoteV5(sessionName, productKey, sendCancelReports);
        exceptionWasThrown = false;   
        TransactionTimingUtil.generateQuoteEvent(TransactionTimingRegistration.getCancelQuoteEmitPoint(), entityId,
            exceptionWasThrown?TransactionTimer.LeaveWithException:TransactionTimer.Leave );
        
        QuoteCallSnapshot.done();
        Log.information(this, createQuoteLogSnapshotForCancel("cancelQuoteV5", sessionName, 0, productKey, entityId, sessInfo));
    }

    public void cancelQuotesByClassV5(java.lang.String sessionName, int classKey, boolean sendCancelReports)
        throws com.cboe.exceptions.SystemException, com.cboe.exceptions.CommunicationException, com.cboe.exceptions.AuthorizationException, com.cboe.exceptions.DataValidationException, com.cboe.exceptions.TransactionFailedException, com.cboe.exceptions.NotAcceptedException, com.cboe.exceptions.NotFoundException
    {
        QuoteCallSnapshot.enter();
        boolean exceptionWasThrown  = true;
        long entityId = 0L;
        try
        {
            entityId = TransactionTimingUtil.setEntityID();
        }
        catch (Exception e)
        {
            Log.information(this, "Unable to set EntityID! Exception details: " + e.getMessage());
        }
        
        String sessInfo = currentSession.toString();
        TransactionTimingUtil.generateQuoteEvent(TransactionTimingRegistration.getCancelQuoteEmitPoint(), entityId, TransactionTimer.Enter );
        
        userQuoteService.cancelQuotesByClassV5(sessionName, classKey, sendCancelReports);
        exceptionWasThrown = false;
        TransactionTimingUtil.generateQuoteEvent(TransactionTimingRegistration.getCancelQuoteEmitPoint(), entityId,
            exceptionWasThrown?TransactionTimer.LeaveWithException:TransactionTimer.Leave );
       
        QuoteCallSnapshot.done();
        Log.information(this, createQuoteLogSnapshotForCancel("cancelQuotesByClassV5", sessionName, classKey, 0, entityId, sessInfo));
    }

    public void cancelAllQuotesV5(java.lang.String sessionName, boolean sendCancelReports)
        throws com.cboe.exceptions.SystemException, com.cboe.exceptions.CommunicationException, com.cboe.exceptions.AuthorizationException, com.cboe.exceptions.DataValidationException, com.cboe.exceptions.NotAcceptedException, com.cboe.exceptions.TransactionFailedException
    {
        try
        {
            QuoteCallSnapshot.enter();
            boolean exceptionWasThrown  = true;
            long entityId = 0L;
            try
            {
                entityId = TransactionTimingUtil.setEntityID();
            }
            catch (Exception e)
            {
                Log.information(this, "Unable to set EntityID! Exception details: " + e.getMessage());
            }
            
            String sessInfo = currentSession.toString();
        	TransactionTimingUtil.generateQuoteEvent(TransactionTimingRegistration.getCancelQuoteEmitPoint(), entityId, TransactionTimer.Enter );
            
            userQuoteService.cancelAllQuotesV5(sessionName, sendCancelReports);
            exceptionWasThrown = false;
	        TransactionTimingUtil.generateQuoteEvent(TransactionTimingRegistration.getCancelQuoteEmitPoint(), entityId,
	            exceptionWasThrown?TransactionTimer.LeaveWithException:TransactionTimer.Leave );
	       
            QuoteCallSnapshot.done();
            Log.information(this, createQuoteLogSnapshotForCancel("cancelAllQuotesV5", sessionName, 0, 0, entityId, sessInfo));
        }
        catch(DataValidationException e)
        {
            Log.exception(this, e);
        }
    }

    public void subscribeRFQ(String sessionName, int classKey, CMIRFQConsumer clientListener)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (Log.isDebugOn()) {Log.debug(this, "calling subscribeRFQ for " + currentSession);}
        userQuoteService.verifyUserRFQEnablementForClass(sessionName, classKey);
        ServicesHelper.getProductQueryServiceAdapter().checkProductCacheLoaded(classKey);
        if ( clientListener != null)
        {
            ///////// add the call back consumer to the supplier list/////
            SessionKeyContainer key = new SessionKeyContainer(sessionName, classKey);
            UserSessionKey theKey = new UserSessionKey(currentSession, key);
            ChannelKey channelKey = new ChannelKey(ChannelType.CB_RFQ, theKey);

            ChannelListener proxyListener = ServicesHelper.getRFQConsumerProxy(clientListener, currentSession);
            rfqSupplier.addChannelListener(this, proxyListener, channelKey, key);

            subscriptionService.addRFQClassInterest(proxyListener, sessionName, classKey);

            channelKey = new ChannelKey(ChannelType.RFQ, key);
            internalEventChannel.addChannelListener(this, rfqProcessor, channelKey);
            RFQStruct[] rfqs = userQuoteService.getRFQ(sessionName, classKey);
            for (int i = 0; i < rfqs.length; i++)
            {
                acceptRFQ(rfqs[i]);
            }
        }

    }

    public void unsubscribeRFQ(String sessionName, int classKey, CMIRFQConsumer clientListener)
       throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (Log.isDebugOn()) {Log.debug(this, "calling unsubscribeRFQ for " + currentSession);}
        userQuoteService.verifyUserRFQEnablementForClass(sessionName, classKey);
        if ( clientListener != null)
        {
            ///////// add the call back consumer to the supplier list/////
            SessionKeyContainer key = new SessionKeyContainer(sessionName, classKey);
            UserSessionKey theKey = new UserSessionKey(currentSession, key);
            ChannelKey channelKey= new ChannelKey(ChannelType.CB_RFQ, theKey);

            ChannelListener proxyListener = ServicesHelper.getRFQConsumerProxy(clientListener, currentSession);
            subscriptionService.removeRFQClassInterest(proxyListener, sessionName, classKey);
            rfqSupplier.removeChannelListener(clientListener, proxyListener, channelKey, key);
        }
    }

    public void unsubscribeQuoteStatusForFirm(com.cboe.idl.cmiCallback.CMIQuoteStatusConsumer clientListener)
          throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (Log.isDebugOn()) {Log.debug(this, "calling unsubscribeQuoteStatusForFirm for " + currentSession);}
        if ( clientListener != null)
        {
            ///////// add the call back consumer to the supplier list/////
            ChannelListener proxyListener =
                ServicesHelper.getQuoteStatusConsumerProxy(
                    clientListener,
                    currentSession,
                    false);

            if(currentSession.isTradingFirmEnabled())
            {
                unsubscribeQuoteStatusForTradingFirm(proxyListener);
                return;
            }

            ChannelKey channelKey = new ChannelKey(ChannelType.CB_QUOTE_FILLED_REPORT_BY_FIRM, thisFirmKeyContainer);
            quoteStatusSupplier.removeChannelListener(this, proxyListener, channelKey, thisFirmKeyContainer);

            channelKey = new ChannelKey(ChannelType.CB_QUOTE_BUST_REPORT_BY_FIRM, thisFirmKeyContainer);
            quoteStatusSupplier.removeChannelListener(this, proxyListener, channelKey, thisFirmKeyContainer);

            removeQuoteStatusConsumerProxy(
                proxyListener,
                false,  // true == for firm user (not normal user)
                null);  // null == not for a specific class
        }
    }

    public void unsubscribeQuoteStatusForTradingFirm(ChannelListener proxyListener)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling unsubscribeQuoteStatusForTradingFirm for " + currentSession);
        }

        List<String> users = new ArrayList<String>();
        users.add(currentSession.getUserId());
        users.addAll(currentSession.getTradingFirmGroup());
        for(String user : users)
        {
            ChannelKey channelKey = new ChannelKey(ChannelType.CB_QUOTE_FILLED_REPORT_BY_TRADING_FIRM, user);
            quoteStatusSupplier.removeChannelListener(this, proxyListener, channelKey, user);

            channelKey = new ChannelKey(ChannelType.CB_QUOTE_BUST_REPORT_BY_TRADING_FIRM, user);
            quoteStatusSupplier.removeChannelListener(this, proxyListener, channelKey, user);
        }
        removeQuoteStatusConsumerProxy(proxyListener, true, null);
    }

    public void unsubscribeQuoteStatus(com.cboe.idl.cmiCallback.CMIQuoteStatusConsumer clientListener)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (Log.isDebugOn()) {Log.debug(this, "calling unsubscribeQuoteStatus for " + currentSession);}
        if ( clientListener != null)
        {
            ///////// add the call back consumer to the supplier list/////
            ChannelListener proxyListener =
                ServicesHelper.getQuoteStatusConsumerProxy(
                    clientListener,
                    currentSession,
                    false);

            String theKey = thisUserId;

            ChannelKey channelKey = new ChannelKey(ChannelType.CB_ALL_QUOTES, theKey);
            quoteStatusSupplier.removeChannelListener(this, proxyListener, channelKey, theKey);

            channelKey = new ChannelKey(ChannelType.CB_QUOTE_FILLED_REPORT, theKey);
            quoteStatusSupplier.removeChannelListener(this, proxyListener, channelKey, theKey);

            channelKey = new ChannelKey(ChannelType.CB_QUOTE_BUST_REPORT, theKey);
            quoteStatusSupplier.removeChannelListener(this, proxyListener, channelKey, theKey);

            channelKey = new ChannelKey(ChannelType.CB_QUOTE_CANCEL_REPORT, theKey);
            quoteStatusSupplier.removeChannelListener(this, proxyListener, channelKey, theKey);

            removeQuoteStatusConsumerProxy(
                proxyListener,
                true,   // true == for normal user (not firm user)
                null);  // null == not for a specific class
        }
    }


    ///////////////////////// end of IDL exported methods ///////////////////////

    //////////////////// QuoteStatusCollector Interface Impl ///////////////////

    /**
     * adds the quote status consumer for all products to the callback list and registers
     * with the Internal Event Channel
     */
    private void addAllQuoteConsumer(ChannelListener proxyListener)
    {
        String theKey = thisUserId;
        ChannelKey channelKey;

        channelKey = new ChannelKey(ChannelType.CB_ALL_QUOTES, theKey);
        quoteStatusSupplier.addChannelListener(this, proxyListener, channelKey, theKey);

        channelKey = new ChannelKey(ChannelType.CB_QUOTE_CANCEL_REPORT, theKey);
        quoteStatusSupplier.addChannelListener(this, proxyListener, channelKey, theKey);

        channelKey = new ChannelKey(ChannelType.CB_QUOTE_FILLED_REPORT, theKey);
        quoteStatusSupplier.addChannelListener(this, proxyListener, channelKey, theKey);

        channelKey = new ChannelKey(ChannelType.CB_QUOTE_BUST_REPORT, theKey);
        quoteStatusSupplier.addChannelListener(this, proxyListener, channelKey, theKey);
    }

    //
    // Following are new methods introduced in cmiV2
    //

    public void subscribeQuoteLockedNotificationForClass(int classKey,
                                                         boolean b,
                                                         CMILockedQuoteStatusConsumer clientListener,
                                                         boolean gmdCallback)
            throws SystemException, CommunicationException, DataValidationException, AuthorizationException
    {
        String smgr = currentSession.toString();
        StringBuilder calling = new StringBuilder(smgr.length()+75);
        calling.append("calling subscribeQuoteLockedNotificationForClass for ").append(smgr)
               .append(" classKey:").append(classKey);
        Log.information(this, calling.toString());

        ChannelListener proxyListener = null;
        UserClassContainer theKey;

        ServicesHelper.getProductQueryServiceAdapter().checkProductCacheLoaded(classKey);

        if ( clientListener != null)
        {
            proxyListener = ServicesHelper.getQuoteNotificationConsumerProxy(clientListener, currentSession);
        }
        if ( proxyListener != null)
        {

            theKey = new UserClassContainer(thisUserId, classKey);
            ///////// add the call back consumer to the supplier list/////
            ChannelKey channelKey = new ChannelKey(ChannelType.CB_QUOTE_LOCKED_NOTIFICATION_BY_CLASS, theKey);
            quoteNotificationSupplier.addChannelListener(this, proxyListener, channelKey, theKey);


            channelKey = new ChannelKey(ChannelType.QUOTE_LOCKED_NOTIFICATION, Integer.valueOf(thisUserKey));
            internalEventChannel.addChannelListener(this, quoteNotificationProcessor, channelKey);
            subscriptionService.addSessionClassInterest(proxyListener, "", classKey);
            Object source = new Pair(proxyListener, Integer.valueOf(classKey));
            subscriptionService.addQuoteLockedNotificationUserInterest(source);
        }
    }

    public void subscribeQuoteStatusForClassV2(int classKey,
                                               boolean publishOnSubscribe,
                                               boolean includeBookedStatus,
                                               com.cboe.idl.cmiCallbackV2.CMIQuoteStatusConsumer clientListener,
                                               boolean gmdCallback)
            throws SystemException, CommunicationException, DataValidationException, AuthorizationException
    {
    	String smgr = currentSession.toString();
        StringBuilder sb = new StringBuilder(smgr.length()+100);
        sb.append("calling subscribeQuoteStatusForClassV2 for ")
        	   .append(smgr)
               .append(" classKey:").append(classKey)               
               .append(" gmd:")
               .append(gmdCallback);
        Log.information(this, sb.toString());
        
        ChannelListener proxyListener =
            ServicesHelper.getQuoteStatusConsumerProxy(
                clientListener,
                currentSession,
                gmdCallback);

        Integer theKey = classKey;

        checkProxy(quoteStatusV2Supplier, proxyListener, gmdCallback, true, theKey);
        ServicesHelper.getProductQueryServiceAdapter().checkProductCacheLoaded(classKey);

        ChannelKey channelKey;        
         
        if (includeBookedStatus)
        {
            channelKey = new ChannelKey(ChannelType.CB_QUOTES_BOOKED_BY_CLASS_V2, theKey);
            quoteStatusV2Supplier.addChannelListener(this, proxyListener, channelKey, theKey);
            
            sb.setLength(0);           
            sb.append("subscribeQuoteStatusForClassV2")
			            .append(" includeBookedStatus=")
			            .append(includeBookedStatus)			                   
			            .append(", may cause Latency in QuoteFillReport delivery for UA:")			           
			            .append(currentSession.getUserId());            
            Log.alarm(this, sb.toString());
        }

        channelKey = new ChannelKey(ChannelType.CB_ALL_QUOTES_BY_CLASS_V2, theKey);
        ChannelListenerProxy iecProxy = quoteStatusV2Supplier.addChannelListener(this, proxyListener, channelKey, theKey);
        try
        {
            ((InstrumentedChannelListenerProxy)iecProxy).addUserData(UserDataTypes.CLASS, Integer.toString(classKey));
        }
        catch(ClassCastException e)
        {
            Log.exception(this, "Unable to add user data.", e);
        }

        channelKey = new ChannelKey(ChannelType.CB_QUOTE_CANCEL_REPORT_BY_CLASS_V2, theKey);
        quoteStatusV2Supplier.addChannelListener(this, proxyListener, channelKey, theKey);

        channelKey = new ChannelKey(ChannelType.CB_QUOTE_FILLED_REPORT_BY_CLASS_V2, theKey);
        quoteStatusV2Supplier.addChannelListener(this, proxyListener, channelKey, theKey);

        channelKey = new ChannelKey(ChannelType.CB_QUOTE_BUST_REPORT_BY_CLASS_V2, theKey);
        quoteStatusV2Supplier.addChannelListener(this, proxyListener, channelKey, theKey);


        queryQuoteStatusByClass(classKey, publishOnSubscribe);

        // request all unAck quotes from the QSSS/QSS
        userQuoteService.publishUnAckedQuotesForClass(classKey);
    }

    public void subscribeQuoteStatusForFirmForClassV2(int classKey,
                                                      com.cboe.idl.cmiCallbackV2.CMIQuoteStatusConsumer clientListener,
                                                      boolean gmdCallback)
            throws SystemException, CommunicationException, DataValidationException, AuthorizationException
    {
        if (Log.isDebugOn()) {Log.debug(this, "calling subscribeQuoteStatusForFirmForClass for " + currentSession + " gmd:" + gmdCallback);}

        ServicesHelper.getProductQueryServiceAdapter().checkProductCacheLoaded(classKey);
        if ( clientListener != null)
        {
            ChannelListener proxyListener =
                ServicesHelper.getQuoteStatusConsumerProxy(
                    clientListener,
                    currentSession,
                    gmdCallback);

            Integer classKeyObj = classKey;
            checkProxy(quoteStatusV2Supplier, proxyListener, gmdCallback, true, classKeyObj);
            FirmClassContainer thisFirmClassKey = new FirmClassContainer(this.thisFirmKeyStruct, classKey);

            ///////// add the call back consumer to the supplier list/////
            ChannelKey channelKey = new ChannelKey(ChannelType.CB_QUOTE_FILLED_REPORT_BY_FIRM_BY_CLASS_V2, thisFirmClassKey);
            ChannelListenerProxy iecProxy = quoteStatusV2Supplier.addChannelListener(this, proxyListener, channelKey, thisFirmClassKey);
            try
            {
                ((InstrumentedChannelListenerProxy)iecProxy).addUserData(UserDataTypes.CLASS, classKeyObj.toString());
            }
            catch(ClassCastException e)
            {
                Log.exception(this, "Unable to add user data.", e);
            }

            channelKey = new ChannelKey(ChannelType.CB_QUOTE_BUST_REPORT_BY_FIRM_BY_CLASS_V2, thisFirmClassKey);
            quoteStatusV2Supplier.addChannelListener(this, proxyListener, channelKey, thisFirmClassKey);

            //now subscribe to CBOE events
            channelKey = new ChannelKey(ChannelType.QUOTE_FILL_REPORT_BY_FIRM, thisFirmKeyContainer);
            addListenerForSupplier(channelKey);

            channelKey = new ChannelKey(ChannelType.QUOTE_BUST_REPORT_BY_FIRM, thisFirmKeyContainer);
            addListenerForSupplier(channelKey);

            subscriptionService.addFirmInterest(proxyListener);
        }
    }

    public void subscribeQuoteStatusV2(com.cboe.idl.cmiCallbackV2.CMIQuoteStatusConsumer clientListenerV2,
                                       boolean publishOnSubscribe,
                                       boolean includeBookedStatus,
                                       boolean gmdCallback)
            throws SystemException, CommunicationException, DataValidationException, AuthorizationException
    {
        if (Log.isDebugOn()) {Log.debug(this, "calling subscribeQuoteStatusV2 for " + currentSession + " gmd:" + gmdCallback);}

        // get the Proxy Wrapper for the CORBA callback object
        ChannelListener proxyListener =
            ServicesHelper.getQuoteStatusConsumerProxy(
                clientListenerV2,
                currentSession,
                gmdCallback);

        checkProxy(quoteStatusV2Supplier, proxyListener, gmdCallback, true, null);

        String theKey = thisUserId;
        ChannelKey channelKey;
                       
        if (includeBookedStatus)
        {
            channelKey = new ChannelKey(ChannelType.CB_QUOTES_BOOKED_V2, theKey);
            quoteStatusV2Supplier.addChannelListener(this, proxyListener, channelKey, theKey);   
            
            StringBuffer alarmMessage = new StringBuffer(110);            
            alarmMessage.append("subscribeQuoteStatusV2")
			            .append(" includeBookedStatus=")
			            .append(includeBookedStatus)
			            .append(" for UA:")
			            .append(currentSession.getUserId())           
			            .append(" may cause Latency in QuoteFillReport delivery");            
            Log.alarm(this, alarmMessage.toString());
        }

        channelKey = new ChannelKey(ChannelType.CB_ALL_QUOTES_V2, theKey);
        quoteStatusV2Supplier.addChannelListener(this, proxyListener, channelKey, theKey);

        channelKey = new ChannelKey(ChannelType.CB_QUOTE_CANCEL_REPORT_V2, theKey);
        quoteStatusV2Supplier.addChannelListener(this, proxyListener, channelKey, theKey);

        channelKey = new ChannelKey(ChannelType.CB_QUOTE_FILLED_REPORT_V2, theKey);
        quoteStatusV2Supplier.addChannelListener(this, proxyListener, channelKey, theKey);

        channelKey = new ChannelKey(ChannelType.CB_QUOTE_BUST_REPORT_V2, theKey);
        quoteStatusV2Supplier.addChannelListener(this, proxyListener, channelKey, theKey);

        queryQuoteStatus(publishOnSubscribe);

        // request all unAck quotes from the QSSS/QSS
        userQuoteService.publishUnAckedQuotes();
    }

    public void unsubscribeQuoteLockedNotificationForClass(int classKey, CMILockedQuoteStatusConsumer clientListener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (Log.isDebugOn()) {Log.debug(this, "calling unsubscribeQuoteLockedNotificationClass for " + currentSession);}
        if ( clientListener != null)
        {
            UserClassContainer theKey = new UserClassContainer(thisUserId, classKey);
            ChannelKey channelKey= new ChannelKey(ChannelType.CB_QUOTE_LOCKED_NOTIFICATION, theKey);
            ChannelListener proxyListener = ServicesHelper.getQuoteNotificationConsumerProxy(clientListener, currentSession);
            quoteNotificationSupplier.removeChannelListener(this, proxyListener, channelKey, theKey);
            subscriptionService.removeSessionClassInterest(proxyListener, "", classKey);
            Object source = new Pair(proxyListener, Integer.valueOf(classKey));
            subscriptionService.removeQuoteLockedNotificationUserInterest(source);
        }
    }

    public void unsubscribeQuoteStatusForClassV2(
            int classKey,
            com.cboe.idl.cmiCallbackV2.CMIQuoteStatusConsumer clientListener)
        throws  SystemException,
                CommunicationException,
                AuthorizationException,
                DataValidationException
    {
        if (Log.isDebugOn()) {Log.debug(this, "calling unsubscribeQuoteStatusForClassV2 for " + currentSession);}

        if ( clientListener != null)
        {
            ///////// add the call back consumer to the supplier list/////
            ChannelListener proxyListener =
                ServicesHelper.getQuoteStatusConsumerProxy(
                    clientListener,
                    currentSession,
                    false);

            Integer theKey = classKey;

            ChannelKey channelKey = new ChannelKey(ChannelType.CB_ALL_QUOTES_BY_CLASS_V2, theKey);
            quoteStatusV2Supplier.removeChannelListener(this, proxyListener, channelKey, theKey);

            channelKey = new ChannelKey(ChannelType.CB_QUOTES_BOOKED_BY_CLASS_V2, theKey);
            quoteStatusV2Supplier.removeChannelListener(this, proxyListener, channelKey, theKey);

            channelKey = new ChannelKey(ChannelType.CB_QUOTE_FILLED_REPORT_BY_CLASS_V2, theKey);
            quoteStatusV2Supplier.removeChannelListener(this, proxyListener, channelKey, theKey);

            channelKey = new ChannelKey(ChannelType.CB_QUOTE_BUST_REPORT_BY_CLASS_V2, theKey);
            quoteStatusV2Supplier.removeChannelListener(this, proxyListener, channelKey, theKey);

            channelKey = new ChannelKey(ChannelType.CB_QUOTE_CANCEL_REPORT_BY_CLASS_V2, theKey);
            ChannelListenerProxy iecProxy = quoteStatusV2Supplier.removeChannelListener(this, proxyListener, channelKey, theKey);
            try
            {
                ((InstrumentedChannelListenerProxy)iecProxy).removeUserData(UserDataTypes.CLASS, Integer.toString(classKey));
            }
            catch(ClassCastException e)
            {
                Log.exception(this, "Unable to remove user data.", e);
            }

            removeQuoteStatusConsumerProxy(
                proxyListener,
                true,  // true == for normal user (not firm user)
                theKey);
        }
    }

    /**
     *
     */
    public void unsubscribeQuoteStatusForFirmForClassV2(
            int classKey,
            com.cboe.idl.cmiCallbackV2.CMIQuoteStatusConsumer clientListener)
        throws  SystemException,
                CommunicationException,
                AuthorizationException,
                DataValidationException
    {
        if (Log.isDebugOn()) {Log.debug(this, "calling unsubscribeQuoteStatusForFirmForClassV2 for " + currentSession);}
        if ( clientListener != null)
        {
            ///////// add the call back consumer to the supplier list/////
            ChannelListener proxyListener =
                ServicesHelper.getQuoteStatusConsumerProxy(
                    clientListener,
                    currentSession,
                    false);

            FirmClassContainer firmClassKey = new FirmClassContainer(thisFirmKeyStruct, classKey);

            ChannelKey channelKey = new ChannelKey(ChannelType.CB_QUOTE_FILLED_REPORT_BY_FIRM_BY_CLASS_V2, firmClassKey);
            quoteStatusV2Supplier.removeChannelListener(this, proxyListener, channelKey, firmClassKey);

            channelKey = new ChannelKey(ChannelType.CB_QUOTE_BUST_REPORT_BY_FIRM_BY_CLASS_V2, firmClassKey);
            ChannelListener iecProxy = quoteStatusV2Supplier.removeChannelListener(this, proxyListener, channelKey);
            try
            {
                ((InstrumentedChannelListenerProxy)iecProxy).removeUserData(UserDataTypes.CLASS, Integer.toString(classKey));
            }
            catch(ClassCastException e)
            {
                Log.exception(this, "Unable to remove user data.", e);
            }

            removeQuoteStatusConsumerProxy(
                proxyListener,
                false,  // false == for firm user (not normal user)
                Integer.valueOf(classKey));
        }
    }

    public void unsubscribeQuoteStatusV2(com.cboe.idl.cmiCallbackV2.CMIQuoteStatusConsumer clientListenerV2)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (Log.isDebugOn()) {Log.debug(this, "calling unsubscribeQuoteStatusV2 for " + currentSession);}
        if ( clientListenerV2 != null)
        {
            ///////// remove the call back consumer to the supplier list/////
            ChannelListener proxyListener =
                ServicesHelper.getQuoteStatusConsumerProxy(
                    clientListenerV2,
                    currentSession,
                    false);

            String theKey = thisUserId;

            ChannelKey channelKey = new ChannelKey(ChannelType.CB_ALL_QUOTES_V2, theKey);
            quoteStatusV2Supplier.removeChannelListener(this, proxyListener, channelKey, theKey);

            channelKey = new ChannelKey(ChannelType.CB_QUOTE_FILLED_REPORT_V2, theKey);
            quoteStatusV2Supplier.removeChannelListener(this, proxyListener, channelKey, theKey);

            channelKey = new ChannelKey(ChannelType.CB_QUOTE_BUST_REPORT_V2, theKey);
            quoteStatusV2Supplier.removeChannelListener(this, proxyListener, channelKey, theKey);

            channelKey = new ChannelKey(ChannelType.CB_QUOTE_CANCEL_REPORT_V2, theKey);
            quoteStatusV2Supplier.removeChannelListener(this, proxyListener, channelKey, theKey);

            channelKey = new ChannelKey(ChannelType.CB_QUOTES_BOOKED_V2, theKey);
            quoteStatusV2Supplier.removeChannelListener(this, proxyListener, channelKey, theKey);

            removeQuoteStatusConsumerProxy(
                proxyListener,
                true,   // true == for normal user (not firm user)
                null);  // null == for all classes (not a particular class)
        }
    }

    public ClassQuoteResultStructV2[] acceptQuotesForClassV2(int classKey, QuoteEntryStruct[] quotes)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotAcceptedException, TransactionFailedException
    {
        if(quotes==null||quotes.length==0){
            Log.information(this, EMPTY_QUOTE_ARRAY_MESSAGE);
            throw ExceptionBuilder.dataValidationException(EMPTY_QUOTE_ARRAY_MESSAGE, DataValidationCodes.INCOMPLETE_QUOTE);
        }

        QuoteCallSnapshot.enter();
        boolean exceptionWasThrown  = true;
        long entityId = 0L;
        try
        {
            entityId = TransactionTimingUtil.setTTContext();
        }
        catch (Exception e)
        {
            Log.information(this, "Unable to set EntityID! Exception details: " + e.getMessage());
        }
        String sessInfo = currentSession.toString();
        Log.information(this,createQuoteLog("acceptQuotesForClassV2", entityId, sessInfo, quotes[0].sessionName, classKey, quotes.length));
        ClassQuoteResultStructV2[] classResultV2;
        ClassQuoteResultStructV3[] classResultV3;
        
    	TransactionTimingUtil.generateQuoteEvent(TransactionTimingRegistration.getQuoteEmitPoint(), entityId, TransactionTimer.Enter );
       
        classResultV3 = userQuoteService.acceptQuotesForClassV3(classKey, quoteStructBuilder.buildQuoteStructsV3(quotes));
        classResultV2 = quoteStructBuilder.buildClassQuoteResultsV2(classResultV3);
	    exceptionWasThrown = false;
        TransactionTimingUtil.generateQuoteEvent(TransactionTimingRegistration.getQuoteEmitPoint(), entityId,
            exceptionWasThrown?TransactionTimer.LeaveWithException:TransactionTimer.Leave );
        
        QuoteCallSnapshot.done();
        Log.information(this, createQuoteLogSnapshot("acceptQuotesForClassV2", classKey, quotes[0].sessionName, quotes[0].userAssignedId, classResultV3[0].quoteResult.quoteKey, entityId, sessInfo));
        return classResultV2;
    }

    public void subscribeQuoteLockedNotification(boolean publishOnSubscribe, com.cboe.idl.cmiCallbackV2.CMILockedQuoteStatusConsumer clientListener, boolean gmdCallback)
            throws SystemException, CommunicationException, DataValidationException, AuthorizationException
    {
        if (Log.isDebugOn()) {Log.debug(this, "calling subscribeQuoteLockedNotification for " + currentSession);}


        ChannelListener proxyListener = null;
        if ( clientListener != null)
        {
            proxyListener = ServicesHelper.getQuoteNotificationConsumerProxy(clientListener, currentSession);
        }
        if ( proxyListener != null)
        {
            ///////// add the call back consumer to the supplier list/////
            ChannelKey channelKey = new ChannelKey(ChannelType.CB_QUOTE_LOCKED_NOTIFICATION, userStruct.userId);
            quoteNotificationSupplier.addChannelListener(this, proxyListener, channelKey, userStruct.userId);

            channelKey = new ChannelKey(ChannelType.QUOTE_LOCKED_NOTIFICATION, Integer.valueOf(thisUserKey));
            internalEventChannel.addChannelListener(this, quoteNotificationProcessor, channelKey);

            subscriptionService.addQuoteLockedNotificationUserInterest(proxyListener);
        }

    }

    public void unsubscribeQuoteLockedNotification(com.cboe.idl.cmiCallbackV2.CMILockedQuoteStatusConsumer clientListener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (Log.isDebugOn()) {Log.debug(this, "calling unsubscribeQuoteLockedNotification for " + currentSession);}
        if ( clientListener != null)
        {
            ///////// add the call back consumer to the supplier list/////
            ChannelKey channelKey= new ChannelKey(ChannelType.CB_QUOTE_LOCKED_NOTIFICATION,  userStruct.userId);
            ChannelListener proxyListener = ServicesHelper.getQuoteNotificationConsumerProxy(clientListener, currentSession);

            SessionProfileUserStructV2 userStructV2 = currentSession.getValidSessionProfileUserV2();
            String userId = userStructV2.userInfo.userId;
            subscriptionService.removeQuoteLockedNotificationUserInterest(proxyListener);
            quoteNotificationSupplier.removeChannelListener(this, proxyListener, channelKey, userId);
        }
    }

    public void subscribeQuoteStatusForFirmV2(
            com.cboe.idl.cmiCallbackV2.CMIQuoteStatusConsumer clientListener,
            boolean gmdCallback)
        throws  SystemException,
                CommunicationException,
                DataValidationException,
                AuthorizationException
    {
        if (Log.isDebugOn()) {Log.debug(this, "calling subscribeQuoteStatusForFirmV2 for " + currentSession + " gmd:" + gmdCallback);}

        if ( clientListener != null)
        {
            ChannelListener proxyListener =
                ServicesHelper.getQuoteStatusConsumerProxy(
                    clientListener,
                    currentSession,
                    gmdCallback);

            if(currentSession.isTradingFirmEnabled())
            {
                subscribeQuoteStatusForTradingFirmV2(proxyListener);
                return;
            }

            checkProxy(quoteStatusV2Supplier, proxyListener, gmdCallback, false, null);

            ///////// add the call back consumer to the supplier list/////
            ChannelKey channelKey = new ChannelKey(ChannelType.CB_QUOTE_FILLED_REPORT_BY_FIRM_V2, thisFirmKeyContainer);
            quoteStatusV2Supplier.addChannelListener(this, proxyListener, channelKey, thisFirmKeyContainer);

            channelKey = new ChannelKey(ChannelType.CB_QUOTE_BUST_REPORT_BY_FIRM_V2, thisFirmKeyContainer);
            quoteStatusV2Supplier.addChannelListener(this, proxyListener, channelKey, thisFirmKeyContainer);

            //now subscribe to CBOE events
            channelKey = new ChannelKey(ChannelType.QUOTE_FILL_REPORT_BY_FIRM, thisFirmKeyContainer);
            addListenerForSupplier(channelKey);

            channelKey = new ChannelKey(ChannelType.QUOTE_BUST_REPORT_BY_FIRM, thisFirmKeyContainer);
            addListenerForSupplier(channelKey);

            subscriptionService.addFirmInterest(proxyListener);
        }
    }

    public void unsubscribeQuoteStatusForFirmV2(
            com.cboe.idl.cmiCallbackV2.CMIQuoteStatusConsumer clientListener)
        throws  SystemException,
                CommunicationException,
                AuthorizationException,
                DataValidationException
    {
        if (Log.isDebugOn()) {Log.debug(this, "calling unsubscribeQuoteStatusForFirmV2 for " + currentSession);}

        if ( clientListener != null)
        {
            ///////// remove the call back consumer from the supplier list/////
            ChannelListener proxyListener =
                ServicesHelper.getQuoteStatusConsumerProxy(
                    clientListener,
                    currentSession,
                    false);

            if(currentSession.isTradingFirmEnabled())
            {
                unsubscribeQuoteStatusForTradingFirm(proxyListener);
                return;
            }

            ChannelKey channelKey = new ChannelKey(ChannelType.CB_QUOTE_FILLED_REPORT_BY_FIRM_V2, thisFirmKeyContainer);
            quoteStatusSupplier.removeChannelListener(this, proxyListener, channelKey, thisFirmKeyContainer);

            channelKey = new ChannelKey(ChannelType.CB_QUOTE_BUST_REPORT_BY_FIRM_V2, thisFirmKeyContainer);
            quoteStatusSupplier.removeChannelListener(this, proxyListener, channelKey, thisFirmKeyContainer);

            removeQuoteStatusConsumerProxy(
                proxyListener,
                false,  // false == for firm user (not normal user)
                null);  // null == for all classes (not a particular class)
        }
    }

    public void subscribeRFQV2(String sessionName, int classKey, com.cboe.idl.cmiCallbackV2.CMIRFQConsumer clientListener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (Log.isDebugOn()) {Log.debug(this, "calling subscribeRFQV2 for " + currentSession + " classKey: " + classKey);}
        //verify if it is enabled
        userQuoteService.verifyUserRFQEnablementForClass(sessionName, classKey);
        ServicesHelper.getProductQueryServiceAdapter().checkProductCacheLoaded(classKey);
        if ( clientListener != null)
        {
            RFQStruct[] rfqs = userQuoteService.getRFQ(sessionName, classKey);
           ///////// add the call back consumer to the supplier list/////
            SessionKeyContainer key = new SessionKeyContainer(sessionName, classKey);
            UserSessionKey theKey = new UserSessionKey(currentSession, key);
            ChannelKey channelKey = new ChannelKey(ChannelType.CB_RFQ_V2, theKey);

            ChannelListener proxyListener = ServicesHelper.getRFQV2ConsumerProxy(clientListener, currentSession);
            rfqV2Supplier.addChannelListener(this, proxyListener, channelKey, key);

            subscriptionService.addRFQClassInterest(proxyListener, sessionName, classKey);

            channelKey = new ChannelKey(ChannelType.RFQ, key);
            internalEventChannel.addChannelListener(this, rfqProcessor, channelKey);

            for (int i = 0; i < rfqs.length; i++) {
                acceptRFQ(rfqs[i]);
            }
        }
    }
    public void unsubscribeRFQV2(String sessionName, int classKey, com.cboe.idl.cmiCallbackV2.CMIRFQConsumer clientListener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (Log.isDebugOn()) {Log.debug(this, "calling unsubscribeRFQV2 for " + currentSession + " classKey: " + classKey);}
        if ( clientListener != null)
        {
            ///////// add the call back consumer to the supplier list/////
            SessionKeyContainer key = new SessionKeyContainer(sessionName, classKey);
            UserSessionKey theKey = new UserSessionKey(currentSession, key);
            ChannelKey channelKey= new ChannelKey(ChannelType.CB_RFQ_V2, theKey);

            ChannelListener proxyListener = ServicesHelper.getRFQV2ConsumerProxy(clientListener, currentSession);
            subscriptionService.removeRFQClassInterest(proxyListener, sessionName, classKey);
            rfqV2Supplier.removeChannelListener(clientListener, proxyListener, channelKey, key);
        }
    }

    public void acceptQuoteNotificationsForClass(com.cboe.idl.cmiQuote.LockNotificationStruct [] quoteLock)
    {
        if (Log.isDebugOn()) {Log.debug(this, "calling acceptQuoteNotificationForClass for " + currentSession + " quote[0].notification.classKey:" );}
    }

    public ClassQuoteResultStructV3[] acceptQuotesForClassV3(int classKey, QuoteEntryStructV3[] quotes)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotAcceptedException, TransactionFailedException
    {
        if(quotes==null||quotes.length==0){
            Log.information(this, EMPTY_QUOTE_ARRAY_MESSAGE);
            throw ExceptionBuilder.dataValidationException(EMPTY_QUOTE_ARRAY_MESSAGE, DataValidationCodes.INCOMPLETE_QUOTE);
        }
        QuoteCallSnapshot.enter();
        boolean exceptionWasThrown = true;
        long entityId = 0L;
        try
        {
            entityId = TransactionTimingUtil.setTTContext();        
        }
        catch (Exception e)
        {
            Log.information(this, "Unable to set EntityID! Exception details: " + e.getMessage());
        }
        String sessInfo = currentSession.toString();
        Log.information(this, createQuoteLog("acceptQuotesForClassV3", entityId, sessInfo, quotes[0].quoteEntry.sessionName, classKey, quotes.length, quotes[0].quoteUpdateControlId));
        ClassQuoteResultStructV3[] classResultV3;
        TransactionTimingUtil.generateQuoteEvent(TransactionTimingRegistration.getQuoteEmitPoint(), entityId, TransactionTimer.Enter );
        
        QuoteStructV3[] quoteStructs = quoteStructBuilder.buildQuoteStructsV3(quotes);
        classResultV3 = userQuoteService.acceptQuotesForClassV3(classKey, quoteStructs);
        exceptionWasThrown = false;
        TransactionTimingUtil.generateQuoteEvent(TransactionTimingRegistration.getQuoteEmitPoint(), entityId,
             exceptionWasThrown?TransactionTimer.LeaveWithException:TransactionTimer.Leave );
        
        QuoteCallSnapshot.done();
        Log.information(this, createQuoteLogSnapshot("acceptQuotesForClassV3", classKey, quotes[0].quoteEntry.sessionName, quotes[0].quoteEntry.userAssignedId, classResultV3[0].quoteResult.quoteKey, entityId, sessInfo));
        return classResultV3;
    }

    public void cancelAllQuotesV3(String sessionName)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotAcceptedException, TransactionFailedException
    {
        QuoteCallSnapshot.enter();
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
        
        String sessInfo = currentSession.toString();
    	TransactionTimingUtil.generateQuoteEvent(TransactionTimingRegistration.getCancelQuoteEmitPoint(), entityId, TransactionTimer.Enter );
         
        userQuoteService.cancelAllQuotes(sessionName);
        exceptionWasThrown = false;
        TransactionTimingUtil.generateQuoteEvent(TransactionTimingRegistration.getCancelQuoteEmitPoint(), entityId,
            exceptionWasThrown?TransactionTimer.LeaveWithException:TransactionTimer.Leave );
       
        QuoteCallSnapshot.done();
        Log.information(this, createQuoteLogSnapshotForCancel("cancelAllQuotesV3", sessionName, 0, 0, entityId, sessInfo));
    }

    public void acceptQuoteV7(QuoteEntryStructV4 quote)
       throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotAcceptedException, TransactionFailedException
    {
        QuoteCallSnapshot.enter();
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
        String sessInfo = currentSession.toString();
        Log.information(this, createQuoteLog("acceptQuoteV7", entityId, sessInfo, quote.quoteEntryV3.quoteEntry.sessionName, quote.quoteEntryV3.quoteEntry.productKey));
        int classKey = getClassKey(quote.quoteEntryV3.quoteEntry.productKey);
        ClassQuoteResultStructV3[] classResultV3;

     	TransactionTimingUtil.generateQuoteEvent(TransactionTimingRegistration.getQuoteEmitPoint(), entityId, TransactionTimer.Enter );

        classResultV3 = userQuoteService.acceptQuotesForClassV7(classKey, quoteStructBuilder.buildQuoteStructsV4(quote));
        exceptionWasThrown = false;
        TransactionTimingUtil.generateQuoteEvent(TransactionTimingRegistration.getQuoteEmitPoint(), entityId,
             exceptionWasThrown?TransactionTimer.LeaveWithException:TransactionTimer.Leave );

        QuoteCallSnapshot.done();
        Log.information(this,
                createQuoteLogSnapshot("acceptQuoteV7", classKey, quote.quoteEntryV3.quoteEntry.sessionName,
                        quote.quoteEntryV3.quoteEntry.userAssignedId, classResultV3[0].quoteResult.quoteKey, entityId, sessInfo)
                + TOKEN_DELIMITER + PRODUCT_KEY + VALUE_DELIMITER + quote.quoteEntryV3.quoteEntry.productKey);

    }

    public ClassQuoteResultStructV3[] acceptQuotesForClassV7(int classKey, QuoteEntryStructV4[] quotes)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotAcceptedException, TransactionFailedException
    {
        QuoteCallSnapshot.enter();
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
        String sessInfo = currentSession.toString();
        Log.information(this, createQuoteLog("acceptQuotesForClassV7", entityId, sessInfo,
                quotes[0].quoteEntryV3.quoteEntry.sessionName, classKey, quotes.length, quotes[0].quoteEntryV3.quoteUpdateControlId));
        ClassQuoteResultStructV3[] classResultV3;
        TransactionTimingUtil.generateQuoteEvent(TransactionTimingRegistration.getQuoteEmitPoint(), entityId, TransactionTimer.Enter );

        QuoteStructV4[] quoteStructs = quoteStructBuilder.buildQuoteStructsV4(quotes);
        classResultV3 = userQuoteService.acceptQuotesForClassV7(classKey, quoteStructs);
        exceptionWasThrown = false;
        TransactionTimingUtil.generateQuoteEvent(TransactionTimingRegistration.getQuoteEmitPoint(), entityId,
             exceptionWasThrown?TransactionTimer.LeaveWithException:TransactionTimer.Leave );

        QuoteCallSnapshot.done();
        Log.information(this, createQuoteLogSnapshot("acceptQuotesForClassV3", classKey, quotes[0].quoteEntryV3.quoteEntry.sessionName,
                quotes[0].quoteEntryV3.quoteEntry.userAssignedId, classResultV3[0].quoteResult.quoteKey, entityId, sessInfo));
        return classResultV3;
    }

    private void removeQuoteStatusConsumerProxy(
            ChannelListener proxy,
            boolean forUser,
            Integer classKey)
    {
        try
        {
            // Now that we have a proxy for this consumer, we need to have the
            // proxy home impl remove it from its maps.
            QuoteStatusConsumerProxyHome home =
                (QuoteStatusConsumerProxyHome) HomeFactory.getInstance().findHome(
                    QuoteStatusConsumerProxyHome.HOME_NAME);

            home.removeGMDProxy(proxy, forUser, classKey);
        }
        catch (Exception e)
        {
            Log.exception(e);
        }
    }

    /**
     *
     */
    private ChannelListener checkProxy(
            UserSessionBaseSupplier supplier,
            ChannelListener newProxy,
            boolean gmd,
            boolean forUser,
            Integer classKey)
        throws DataValidationException
    {
        BaseChannelListenerProxy baseProxy =
            (BaseChannelListenerProxy) supplier.getProxyForDelegate(newProxy);

        ChannelListener existingProxy = null;
        ChannelListener proxy = null;

        if (baseProxy != null)
        {
            existingProxy = baseProxy.getDelegateListener();
        }

        if (existingProxy != null)
        {
            GMDSupplierProxy gmdProxy = (GMDSupplierProxy) existingProxy;

            if (gmdProxy.getGMDStatus() != gmd)
            {
                throw ExceptionBuilder.dataValidationException(
                    "GMD flag does not match existing GMD flag",
                    DataValidationCodes.GMD_LISTENER_ALREADY_REGISTERED);
            }

            proxy = existingProxy;
        }
        else
        {
            proxy = newProxy;
        }

        try
        {
            QuoteStatusConsumerProxyHome home =
                (QuoteStatusConsumerProxyHome) HomeFactory.getInstance().findHome(
                    QuoteStatusConsumerProxyHome.HOME_NAME);
            home.addGMDProxy(proxy, forUser, classKey);
        }
        catch (DataValidationException e)
        {
            throw e;
        }
        catch (Exception e)
        {
            Log.exception(e);
        }

        return proxy;
    }
}
