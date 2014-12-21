package com.cboe.application.inprocess.quoteEntry;

import com.cboe.application.shared.LoggingUtil;
import com.cboe.application.shared.ServicesHelper;
import com.cboe.application.shared.TransactionTimingUtil;
import com.cboe.application.shared.TransactionTimingRegistration;
import com.cboe.application.shared.consumer.UserSessionLogoutProcessor;
import com.cboe.application.shared.consumer.UserSessionLogoutProcessorFactory;
import com.cboe.application.util.QuoteCallSnapshot;
import com.cboe.domain.logout.LogoutServiceFactory;
import com.cboe.exceptions.*;
import com.cboe.idl.cmiQuote.ClassQuoteResultStructV3;
import com.cboe.idl.cmiQuote.QuoteStructV4;
import com.cboe.infrastructureServices.foundationFramework.BObject;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.instrumentationService.transactionTimingCommon.TransactionTimer;
import com.cboe.interfaces.application.UserQuoteService;
import com.cboe.interfaces.application.UserSessionLogoutCollector;
import com.cboe.interfaces.application.inprocess.InProcessSessionManager;
import com.cboe.interfaces.application.inprocess.QuoteEntry;
import com.cboe.util.event.EventChannelAdapterFactory;
import static com.cboe.application.shared.LoggingUtil.*;

/**
 * @author Jing Chen
 * @author Gijo Joseph
 */
public class QuoteEntryImpl extends BObject implements QuoteEntry, UserSessionLogoutCollector
{
    com.cboe.interfaces.application.inprocess.InProcessSessionManager sessionManager;
    ////////////////// member variables /////////////////////////////////
    protected UserSessionLogoutProcessor  	logoutProcessor;
    protected UserQuoteService 				userQuoteService;
    

    public QuoteEntryImpl()
    {
    }// end of constructor

    public void create(String name)
    {
        super.create(name);
        //quoteStructBuilder = new QuoteStructBuilder(sessionManager);
    }// end of create

    public void setInProcessSessionManager(InProcessSessionManager session)
    {
        sessionManager = session;
        logoutProcessor = UserSessionLogoutProcessorFactory.create(this);
        EventChannelAdapterFactory.find().addChannelListener(this, logoutProcessor, session);
        LogoutServiceFactory.find().addLogoutListener(session, this);
        userQuoteService = ServicesHelper.getUserQuoteService(session);
    }

    public void acceptQuote(QuoteStructV4 quoteV4)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotAcceptedException, TransactionFailedException
    {
        QuoteCallSnapshot.enter();
        boolean exceptionWasThrown = true;
        long entityId = 0L;
        try
        {
        	entityId = TransactionTimingUtil.setTTContext();
        	TransactionTimingUtil.generateQuoteEvent(TransactionTimingRegistration.getQuoteEmitPoint(), entityId, TransactionTimer.Enter );
           
        }
        catch (Exception e)
        {
        	Log.information(this, "Unable to set EntityID! Exception details: " + e.getMessage());
        }
        String sessInfo = sessionManager.toString();
        Log.information(this,createQuoteLog("acceptQuote", entityId, sessInfo, quoteV4.quoteV3.quote.sessionName, quoteV4.quoteV3.quote.productKey));
        int classKey;
        try
        {
        	classKey = userQuoteService.acceptQuoteV7(quoteV4);
        	exceptionWasThrown = false;
        }
        finally
	    {
	        TransactionTimingUtil.generateQuoteEvent(TransactionTimingRegistration.getQuoteEmitPoint(), entityId,
	             exceptionWasThrown?TransactionTimer.LeaveWithException:TransactionTimer.Leave );
	                  
	    }
        //ClassQuoteResultStructV3[] classResultV3 = userQuoteService.acceptQuotesForClassV3(classKey, quoteStructBuilder.buildQuoteStructsV3(quote));
        QuoteCallSnapshot.done();
        String snapshot =  createQuoteLogSnapshot("acceptQuote", classKey, quoteV4.quoteV3.quote.sessionName, quoteV4.quoteV3.quote.userAssignedId, entityId, sessInfo);
        StringBuilder snap = new StringBuilder(snapshot.length()+20);
        snap.append(snapshot).append(LoggingUtil.TOKEN_DELIMITER).append(LoggingUtil.PRODUCT_KEY).append(LoggingUtil.VALUE_DELIMITER).append(quoteV4.quoteV3.quote.productKey);
        Log.information(this, snap.toString());
    }

    public ClassQuoteResultStructV3[] acceptQuotesForClass(int classKey, QuoteStructV4[] quoteStructs)
      throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotAcceptedException, TransactionFailedException
    {
        QuoteCallSnapshot.enter();
        boolean exceptionWasThrown = true;
        long entityId = 0L;
        try
        {
        	entityId = TransactionTimingUtil.setTTContext();
        	TransactionTimingUtil.generateQuoteEvent(TransactionTimingRegistration.getQuoteEmitPoint(), entityId, TransactionTimer.Enter );
        }
        catch (Exception e)
        {
        	Log.information(this, "Unable to get EntityID! Exception details: " + e.getMessage());
        }
        String sessInfo = sessionManager.toString();
        Log.information(this,createQuoteLog("acceptQuotesForClass", entityId, sessInfo, quoteStructs[0].quoteV3.quote.sessionName, classKey, quoteStructs.length));
        ClassQuoteResultStructV3[] results;
        try
        {
        	results = userQuoteService.acceptQuotesForClassV7(classKey, quoteStructs);
        	exceptionWasThrown = false;
        }
        finally
	    {
        	TransactionTimingUtil.generateQuoteEvent(TransactionTimingRegistration.getQuoteEmitPoint(), entityId,
	             exceptionWasThrown?TransactionTimer.LeaveWithException:TransactionTimer.Leave );      
	    }
        QuoteCallSnapshot.done();
        Log.information(this, createQuoteLogSnapshot("acceptQuotesForClass", classKey, quoteStructs[0].quoteV3.quote.sessionName, quoteStructs[0].quoteV3.quote.userAssignedId, entityId, sessInfo));
        return results;
    }
    public void cancelQuote(String sessionName, int productKey)
         throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotAcceptedException, TransactionFailedException, NotFoundException
    {
        QuoteCallSnapshot.enter();
        boolean exceptionWasThrown = true;
        long entityId = 0L;
        try
        {
        	entityId = TransactionTimingUtil.setEntityID();
        	TransactionTimingUtil.generateQuoteEvent(TransactionTimingRegistration.getCancelQuoteEmitPoint(), entityId, TransactionTimer.Enter );
        
        }
        catch (Exception e)
        {
        	Log.information(this, "Unable to get EntityID! Exception details: " + e.getMessage());
        }
        String sessInfo = sessionManager.toString();
        int classkey;
        try
        {
        	classkey = userQuoteService.cancelQuote(sessionName, productKey);
        	exceptionWasThrown = false;
        }
        finally
	    {
	        TransactionTimingUtil.generateQuoteEvent(TransactionTimingRegistration.getCancelQuoteEmitPoint(), entityId,
	             exceptionWasThrown?TransactionTimer.LeaveWithException:TransactionTimer.Leave );
	                   
	    }
        QuoteCallSnapshot.done();
        Log.information(this, createQuoteLogSnapshotForCancel("cancelQuote", sessionName, classkey, productKey, entityId, sessInfo));
    }

    public void cancelAllQuotes(String sessionName)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException, TransactionFailedException, NotAcceptedException
    {
        QuoteCallSnapshot.enter();
        boolean exceptionWasThrown = true;
        long entityId = 0L;
        try
        {
        	entityId = TransactionTimingUtil.setEntityID();
        	TransactionTimingUtil.generateQuoteEvent(TransactionTimingRegistration.getCancelQuoteEmitPoint(), entityId, TransactionTimer.Enter );
        }
        catch (Exception e)
        {
        	Log.information(this, "Unable to get EntityID! Exception details: " + e.getMessage());
        }
        String sessInfo = sessionManager.toString();
        
        try
        {
        	userQuoteService.cancelAllQuotes(sessionName);
        	exceptionWasThrown = false;
        }
        finally
	    {
        	TransactionTimingUtil.generateQuoteEvent(TransactionTimingRegistration.getCancelQuoteEmitPoint(), entityId,
	             exceptionWasThrown?TransactionTimer.LeaveWithException:TransactionTimer.Leave );
	              
	    }
        QuoteCallSnapshot.done();
        Log.information(this, createQuoteLogSnapshotForCancel("cancelAllQuotes", sessionName, 0, 0, entityId, sessInfo));
    }

    public void cancelQuotesByClass(String sessionName, int classKey)
         throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotAcceptedException, TransactionFailedException, NotFoundException
    {
        QuoteCallSnapshot.enter();
        boolean exceptionWasThrown = true;
        long entityId = 0L;
        try
        {
        	entityId = TransactionTimingUtil.setEntityID();
        	TransactionTimingUtil.generateQuoteEvent(TransactionTimingRegistration.getCancelQuoteEmitPoint(), entityId, TransactionTimer.Enter );
            
        }
        catch (Exception e)
        {
        	Log.information(this, "Unable to get EntityID! Exception details: " + e.getMessage());
        }
        String sessInfo = sessionManager.toString();
        
        try
        {
        	userQuoteService.cancelQuotesByClass(sessionName, classKey);
        	exceptionWasThrown = false;
        }	
        finally
	    {
        	TransactionTimingUtil.generateQuoteEvent(TransactionTimingRegistration.getCancelQuoteEmitPoint(), entityId,
	             exceptionWasThrown?TransactionTimer.LeaveWithException:TransactionTimer.Leave );
	                 
	    }
        QuoteCallSnapshot.done();
        Log.information(this, createQuoteLogSnapshotForCancel("cancelQuotesByClass", sessionName, classKey, 0, entityId, sessInfo));
    }

    public void acceptUserSessionLogout() {
        if (Log.isDebugOn())
        {
        Log.debug(this, "calling acceptUserSessionLogout for " + sessionManager);
        }
        EventChannelAdapterFactory.find().removeListenerGroup(this);
        LogoutServiceFactory.find().logoutComplete(sessionManager,this);
        logoutProcessor.setParent(null);
        logoutProcessor = null;
        // Clean up instance variables.
        sessionManager = null;
        userQuoteService = null;
    }
}
