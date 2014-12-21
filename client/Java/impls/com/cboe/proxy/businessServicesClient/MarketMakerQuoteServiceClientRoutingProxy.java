package com.cboe.proxy.businessServicesClient;

import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.NotAcceptedException;
import com.cboe.exceptions.NotFoundException;
import com.cboe.exceptions.SystemException;
import com.cboe.exceptions.TransactionFailedException;
import com.cboe.idl.businessServices.MarketMakerQuoteService;
import com.cboe.idl.cmiErrorCodes.NotAcceptedCodes;
import com.cboe.idl.cmiQuote.*;
import com.cboe.idl.cmiUtil.CboeIdStruct;
import com.cboe.idl.cmiUtil.DateTimeStruct;
import com.cboe.idl.cmiUtil.KeyValueStruct;
import com.cboe.idl.constants.ServerResponseCodes;
import com.cboe.idl.quote.InternalQuoteStruct;
import com.cboe.idl.quote.ManualQuoteStruct;
import com.cboe.idl.util.RoutingParameterStruct;
import com.cboe.idl.util.ServerResponseStruct;
import com.cboe.application.shared.TransactionTimingUtil;
import com.cboe.application.shared.TransactionTimingRegistration;
import com.cboe.client.util.ClientFederatedServiceHelper;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.instrumentationService.transactionTimingCommon.TransactionTimer;
import com.cboe.domain.util.ServerFailureEventHolder;
import com.cboe.util.ExceptionBuilder;
import org.omg.CORBA.OBJECT_NOT_EXIST;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * This class is a routing proxy that delegates the incoming requests to the
 * appropriate Market Maker Quote Service. The class maintains a table which
 * maps every service to its respective process ( route ).
 *
 * @date  January 07, 2009
 *
 */

public class MarketMakerQuoteServiceClientRoutingProxy extends NonGlobalServiceClientRoutingProxy 
            implements com.cboe.interfaces.businessServices.MarketMakerQuoteService
{
        private static final String CANCEL_QUOTES = "cancelQuotesForUsers";

        private static final String GET_QUOTES = "getQuoteCountForUsers";
        
        private static final ClassQuoteResultStruct[] EMPTY_ClassQuoteResultStruct_ARRAY = new ClassQuoteResultStruct[0];

        /**
         * Default constructor
         */

        public	MarketMakerQuoteServiceClientRoutingProxy()
        {
        }

        /**
         * Default create method
         */
        public void create(String name)
        {
            super.create(name);
            replyHandlerManager = new MarketMakerQuoteServiceClientReplyHandlerManager();    
         }

        /**   	 
         *  * Forwards request to delegate
         */
    	public InternalQuoteStruct getQuoteForProduct(String sessionName, int productKey, CboeIdStruct cboeId) throws SystemException,
    			CommunicationException, DataValidationException, NotFoundException, AuthorizationException
    	{
    	    MarketMakerQuoteService service = (MarketMakerQuoteService) getServiceByProduct(sessionName, productKey);
    		return service.getQuoteForProduct(sessionName, productKey, cboeId);
    	}

    	/**
    	 * * Forwards request to delegate
    	 */
    	public ClassQuoteResultStruct[] acceptQuotesForClass(int classKey, QuoteStruct[] quotes) throws SystemException,
			    CommunicationException, DataValidationException, NotAcceptedException, AuthorizationException
        {
    		ClassQuoteResultStruct[] classQuoteResults;
            classQuoteResults = EMPTY_ClassQuoteResultStruct_ARRAY;
            long quoteBlock = (long) quotes.length;
            long id = TransactionTimingUtil.generateQuoteMetricId((long) classKey, quotes[0].sessionName, quoteBlock);
            TransactionTimingUtil.sendMethodEvent(TransactionTimingRegistration.QUOTE_BLOCK_COLLECTOR_TYPE, id, 0);

    		long entityId = 0L;
            try
            {
                entityId = TransactionTimingUtil.getEntityID();
            }
            catch (Exception e)
            {
                Log.information(this, "Unable to set EntityID! Exception details: " + e.getMessage());
            }
            
    	    boolean exceptionWasThrown = true;
            try
            {
            	TransactionTimingUtil.generateQuoteEvent(TransactionTimingRegistration.getQuoteRoutingProxyEmitPoint(), entityId, TransactionTimer.Enter );
                
                MarketMakerQuoteService service = (MarketMakerQuoteService) getServiceByClass(quotes[0].sessionName, classKey);
                classQuoteResults = service.acceptQuotesForClass(classKey, quotes);
                TransactionTimingUtil.sendMethodEvent(TransactionTimingRegistration.QUOTE_BLOCK_COLLECTOR_TYPE, id, 1);
                exceptionWasThrown = false;
            }
            catch (OBJECT_NOT_EXIST e)
            {
                throw convertToNotAcceptedException(e, "acceptQuotesForClass failed, userId[0]:" + quotes[0].userId
                        + " sessionName[0]:" + quotes[0].sessionName + " classKey:" + classKey
                        + " userAssignedId[0]:" + quotes[0].userAssignedId);
            }
            finally
            {
                TransactionTimingUtil.generateQuoteEvent(TransactionTimingRegistration.getQuoteRoutingProxyEmitPoint(), entityId,
                        exceptionWasThrown ? TransactionTimer.LeaveWithException : TransactionTimer.Leave );
            }
            return classQuoteResults;
        }

        /**
	    *     	Forward request to delegate for V3
	    */
    	public ClassQuoteResultStructV3[] acceptQuotesForClassV3(int classKey, QuoteStructV3[] quotes, int sessionKey)
			    throws SystemException, CommunicationException, DataValidationException, NotAcceptedException, AuthorizationException

        {   	    
            ClassQuoteResultStructV3[] classQuoteResults;
            long quoteBlock = (long) quotes.length;
    		long id = TransactionTimingUtil.generateQuoteMetricId((long) classKey, quotes[0].quote.sessionName, quoteBlock);
            TransactionTimingUtil.sendMethodEvent(TransactionTimingRegistration.QUOTE_BLOCK_COLLECTOR_TYPE, id, 0);
            long entityId = 0L;
            try
            {
                entityId = TransactionTimingUtil.getEntityID();
            }
            catch (Exception e)
            {
                Log.information(this, "Unable to set EntityID! Exception details: " + e.getMessage());
            }
            
            boolean exceptionWasThrown = true;
            try
            {
                TransactionTimingUtil.generateQuoteEvent(TransactionTimingRegistration.getQuoteRoutingProxyEmitPoint(), entityId, TransactionTimer.Enter );
            
            MarketMakerQuoteService service = (MarketMakerQuoteService) getServiceByClass(quotes[0].quote.sessionName, classKey);
            classQuoteResults = service.acceptQuotesForClassV3(classKey, quotes, sessionKey);
            TransactionTimingUtil.sendMethodEvent(TransactionTimingRegistration.QUOTE_BLOCK_COLLECTOR_TYPE, id, 1);
                exceptionWasThrown = false;
            }
            finally
            {
                TransactionTimingUtil.generateQuoteEvent(TransactionTimingRegistration.getQuoteRoutingProxyEmitPoint(), entityId,
                        exceptionWasThrown ? TransactionTimer.LeaveWithException : TransactionTimer.Leave );
            }
            return classQuoteResults;
        }

    	/**
        *     	Forward request to delegate for V3
        */
        public ClassQuoteResultStructV3[] acceptQuotesForClassV4(int classKey, QuoteStructV4[] quotes, int sessionKey)
                throws SystemException, CommunicationException, DataValidationException, NotAcceptedException, AuthorizationException

        {
            ClassQuoteResultStructV3[] classQuoteResults;
            long quoteBlock = (long) quotes.length;
            long id = TransactionTimingUtil.generateQuoteMetricId((long) classKey, quotes[0].quoteV3.quote.sessionName, quoteBlock);
            TransactionTimingUtil.sendMethodEvent(TransactionTimingRegistration.QUOTE_BLOCK_COLLECTOR_TYPE, id, 0);
            long entityId = 0L;
            try
            {
                entityId = TransactionTimingUtil.getEntityID();
            }
            catch (Exception e)
            {
                Log.information(this, "Unable to set EntityID! Exception details: " + e.getMessage());
            }

            TransactionTimingUtil.generateQuoteEvent(TransactionTimingRegistration.getQuoteEmitPoint(), entityId, TransactionTimer.Enter );

            MarketMakerQuoteService service = (MarketMakerQuoteService) getServiceByClass(quotes[0].quoteV3.quote.sessionName, classKey);
            try{
                classQuoteResults = service.acceptQuotesForClassV4(classKey, quotes, sessionKey);
            }
            catch (org.omg.CORBA.SystemException cse)
            {
                boolean MAYBE = cse.completed.value() == org.omg.CORBA.CompletionStatus._COMPLETED_MAYBE;
                if(MAYBE&&quotes!=null&&quotes.length>0&&quotes[0]!=null){
                    StringBuffer infoBuffer = new StringBuffer();
                    infoBuffer.append("acceptQuotesForClassV4 MAYBE failed for UserId: ").append(quotes[0].quoteV3.quote.userId).
                            append(" quoteKey: ").append(quotes[0].quoteV3.quote.quoteKey).
                            append(" classKey: ").append(classKey);
                    //Log.alarm(this, infoBuffer.toString());
                    Log.exception("MarketMakerQuoteServiceClientRoutingProxy>>acceptQuotesForClassV4>>org.omg.CORBA.SystemException: " + infoBuffer.toString(), cse);
                }

                throw cse;
            }

            TransactionTimingUtil.sendMethodEvent(TransactionTimingRegistration.QUOTE_BLOCK_COLLECTOR_TYPE, id, 1);
            TransactionTimingUtil.generateQuoteEvent(TransactionTimingRegistration.getQuoteEmitPoint(), entityId, TransactionTimer.Leave );
            return classQuoteResults;
        }

    	/**
    	 * * Forwards request to delegate
    	 */
    	public void acceptQuote(QuoteStruct quote) throws SystemException, CommunicationException, DataValidationException,
			    TransactionFailedException, NotAcceptedException, AuthorizationException
		{   
    		long quoteBlock = 1L;
    		long id = TransactionTimingUtil.generateQuoteMetricId((long) quote.productKey, quote.sessionName, quoteBlock);
    		TransactionTimingUtil.sendMethodEvent(TransactionTimingRegistration.QUOTE_BLOCK_COLLECTOR_TYPE, id, 0);

    		long entityId = 0L;
            try
            {
                entityId = TransactionTimingUtil.getEntityID();
            }
            catch (Exception e)
            {
                Log.information(this, "Unable to set EntityID! Exception details: " + e.getMessage());
            }
            
            boolean exceptionWasThrown = true;
    	    try
    	    {
    	    	TransactionTimingUtil.generateQuoteEvent(TransactionTimingRegistration.getQuoteRoutingProxyEmitPoint(), entityId, TransactionTimer.Enter );
                
		        MarketMakerQuoteService service = (MarketMakerQuoteService) getServiceByProduct(quote.sessionName, quote.productKey);
		        service.acceptQuote(quote);
		        TransactionTimingUtil.sendMethodEvent(TransactionTimingRegistration.QUOTE_BLOCK_COLLECTOR_TYPE, id, 1);
		        exceptionWasThrown = false;
    	    }
    	    catch (OBJECT_NOT_EXIST e)
    	    {
    	        throw convertToNotAcceptedException(e, "acceptQuote failed, userId:" + quote.userId
                        + " sessionName:" + quote.sessionName + " productKey:" + quote.productKey
                        + " userAssignedId:" + quote.userAssignedId);
    	    }
            finally
            {
                TransactionTimingUtil.generateQuoteEvent(TransactionTimingRegistration.getQuoteRoutingProxyEmitPoint(), entityId,
                        exceptionWasThrown ? TransactionTimer.LeaveWithException : TransactionTimer.Leave );                
            }
		}

    	/**
     * * Forwards request to delegate
     */
    public void acceptQuoteV4(QuoteStructV4 quote) throws SystemException, CommunicationException, DataValidationException,
            TransactionFailedException, NotAcceptedException, AuthorizationException
    {
        long quoteBlock = 1L;
        long id = TransactionTimingUtil.generateQuoteMetricId((long) quote.quoteV3.quote.productKey, quote.quoteV3.quote.sessionName, quoteBlock);
        TransactionTimingUtil.sendMethodEvent(TransactionTimingRegistration.QUOTE_BLOCK_COLLECTOR_TYPE, id, 0);

        long entityId = 0L;
        try
        {
            entityId = TransactionTimingUtil.getEntityID();
        }
        catch (Exception e)
        {
            Log.information(this, "Unable to set EntityID! Exception details: " + e.getMessage());
        }

        try
        {
            TransactionTimingUtil.generateQuoteEvent(TransactionTimingRegistration.getQuoteEmitPoint(), entityId, TransactionTimer.Enter );

            MarketMakerQuoteService service = (MarketMakerQuoteService) getServiceByProduct(quote.quoteV3.quote.sessionName, quote.quoteV3.quote.productKey);
            service.acceptQuoteV4(quote);
            TransactionTimingUtil.sendMethodEvent(TransactionTimingRegistration.QUOTE_BLOCK_COLLECTOR_TYPE, id, 1);
            TransactionTimingUtil.generateQuoteEvent(TransactionTimingRegistration.getQuoteEmitPoint(), entityId, TransactionTimer.Leave );

        }
        catch (OBJECT_NOT_EXIST e)
        {
            throw convertToNotAcceptedException(e, "acceptQuote failed, userId:" + quote.quoteV3.quote.userId
                    + " sessionName:" + quote.quoteV3.quote.sessionName + " productKey:" + quote.quoteV3.quote.productKey
                    + " userAssignedId:" + quote.quoteV3.quote.userAssignedId);
        }
    }

    	/**
    	 * 
    	 */
    	public void acceptManualQuote(ManualQuoteStruct manualQuote) throws SystemException, CommunicationException,
			    DataValidationException, TransactionFailedException, NotAcceptedException, AuthorizationException
		{    	    
    	    MarketMakerQuoteService service = (MarketMakerQuoteService) getServiceByProduct(manualQuote.sessionName,
			manualQuote.productKeys.productKey);
    	    boolean exceptionWasThrown = true;
            long entityId = 0L;
            
            try
            {
            	entityId = TransactionTimingUtil.getEntityID();
            }
            catch (Exception e)
            {
            	Log.information(this, "Unable to set EntityID! Exception details: " + e.getMessage());
            }
            
     		TransactionTimingUtil.generateOrderEvent(TransactionTimingRegistration.getManualQuoteRoutingProxyEmitPoint(), entityId, TransactionTimer.Enter );
           	try{
    	    service.acceptManualQuote(manualQuote);
           		exceptionWasThrown = false;
	       	}
	       	finally
	       	{
		    	TransactionTimingUtil.generateOrderEvent(TransactionTimingRegistration.getManualQuoteRoutingProxyEmitPoint(), entityId,
		        exceptionWasThrown?TransactionTimer.LeaveWithException:TransactionTimer.Leave );
	       	}
		}

    	public void cancelManualQuote(ManualQuoteStruct manualQuote) throws SystemException, CommunicationException,
			    DataValidationException, TransactionFailedException, NotAcceptedException, AuthorizationException, NotFoundException
		{    	    
    	    MarketMakerQuoteService service = (MarketMakerQuoteService) getServiceByProduct(manualQuote.sessionName,
			manualQuote.productKeys.productKey);
    	    boolean exceptionWasThrown = true;
            long entityId = 0L;
            
            try
            {
            	entityId = TransactionTimingUtil.getEntityID();
            }
            catch (Exception e)
            {
            	Log.information(this, "Unable to set EntityID! Exception details: " + e.getMessage());
            }
            
     		TransactionTimingUtil.generateOrderEvent(TransactionTimingRegistration.getCancelManualQuoteRoutingProxyEmitPoint(), entityId, TransactionTimer.Enter );
           	try{
    	    service.cancelManualQuote(manualQuote);
	    		exceptionWasThrown = false;
	       	}
	       	finally
	       	{
		    	TransactionTimingUtil.generateOrderEvent(TransactionTimingRegistration.getCancelManualQuoteRoutingProxyEmitPoint(), entityId,
		        exceptionWasThrown?TransactionTimer.LeaveWithException:TransactionTimer.Leave );
	       	}
		}

    	public void cancelManualQuoteWithReason(ManualQuoteStruct manualQuote, char reasonCode) throws SystemException, CommunicationException,
			    DataValidationException, TransactionFailedException, NotAcceptedException, AuthorizationException, NotFoundException
		{    	    
    	    MarketMakerQuoteService service = (MarketMakerQuoteService) getServiceByProduct(manualQuote.sessionName,
			manualQuote.productKeys.productKey);
    	    service.cancelManualQuoteWithReason(manualQuote, reasonCode);
		}

    	/**
         * Propagates the the request asynchronously to all known services
         */
    	public void cancelAllQuotes(String userId, String sessionName) throws SystemException, CommunicationException,
			    DataValidationException, TransactionFailedException, NotAcceptedException, AuthorizationException
        {    
            MarketMakerQuoteServiceClientReplyHandler replyHandler = (MarketMakerQuoteServiceClientReplyHandler) reserveReplyHandler();
            ArrayList services = getServicesBySession(sessionName);
            replyHandler.setNumberOfRequests(services.size());
            Iterator servicesIterator = services.iterator();
            int svcFailed = 0;
            long entityId = 0L;
            try
            {
                entityId = TransactionTimingUtil.getEntityID();
            }
            catch (Exception e)
            {
                Log.information(this, "Unable to set EntityID! Exception details: " + e.getMessage());
            }
            
            TransactionTimingUtil.generateQuoteEvent(TransactionTimingRegistration.getCancelQuoteRoutingProxyEmitPoint(), entityId, TransactionTimer.Enter );
           
            while (servicesIterator.hasNext())
            {
                MarketMakerQuoteService targetService = (MarketMakerQuoteService) servicesIterator.next();
                try
                {
                    targetService.sendc_cancelAllQuotes(replyHandler.getAMIHandler(), userId, sessionName);
                    TransactionTimingUtil.resetEntityID(entityId);
                }
                catch (org.omg.CORBA.SystemException e)
                {
                    if (Log.isDebugOn())
                    {
                        Log.debug(this, "Error sending request to service " + targetService.toString()); 
                    }                    
                    Log.exception(this, e);
                    synchronized (replyHandler)
                    {
                        replyHandler.setNumberOfRequests(replyHandler.getNumberOfRequests() - 1);
                    }
                    svcFailed++;
                }
            }
            TransactionTimingUtil.generateQuoteEvent(TransactionTimingRegistration.getCancelQuoteRoutingProxyEmitPoint(), entityId, TransactionTimer.Leave );
        }

    	/**
    	 * *Forwards request to delegate
         */
        public void cancelQuote(String userId, String sessionName, int productKey) throws SystemException, CommunicationException,
                DataValidationException, NotAcceptedException, TransactionFailedException, AuthorizationException
        {
        	long quoteBlock = 1L;
    		long id = TransactionTimingUtil.generateQuoteMetricId((long) productKey, sessionName, quoteBlock);
            TransactionTimingUtil.sendMethodEvent(TransactionTimingRegistration.CANCEL_QUOTE_COLLECTOR_TYPE, id, 0);
            long entityId = 0L;
            try
            {
                entityId = TransactionTimingUtil.getEntityID();
            }
            catch (Exception e)
            {
                Log.information(this, "Unable to set EntityID! Exception details: " + e.getMessage());
            }
            
            // Usually we set exceptionWasThrown=true, then set it to false at the end of the try { } block.
            // We reverse it for Cancel Quote, because some exceptions (such as deleting a quote that no
            // longer exists) are ok and shouldn't be noted as problems in TTE data. When we catch an
            // exception that we're sure is bad, then we set exceptionWasThrown=true.
            boolean exceptionWasThrown = false;
            try
            {
            	TransactionTimingUtil.generateQuoteEvent(TransactionTimingRegistration.getCancelQuoteRoutingProxyEmitPoint(), entityId, TransactionTimer.Enter );
                
                MarketMakerQuoteService service = (MarketMakerQuoteService) getServiceByProduct(sessionName, productKey);
                service.cancelQuote(userId, sessionName, productKey);
                
            }
            catch (OBJECT_NOT_EXIST e)
            {
                exceptionWasThrown = true;
                throw convertToNotAcceptedException(e, "cancelQuote failed, userId:" + userId
                        + " sessionName:" + sessionName + " productKey:" + productKey);
            }
            finally
            {
            TransactionTimingUtil.sendMethodEvent(TransactionTimingRegistration.CANCEL_QUOTE_COLLECTOR_TYPE, id, 1);
                TransactionTimingUtil.generateQuoteEvent(TransactionTimingRegistration.getCancelQuoteRoutingProxyEmitPoint(), entityId,
                        exceptionWasThrown ? TransactionTimer.LeaveWithException : TransactionTimer.Leave );
            }
        }

    	/**
    	 * New method for Hybrid Failover; method was added to avoid security
    	 * context issues in the CAS.
    	 */
    	public void systemCancelQuotesByClass(String userId, String sessionName, int[] classKeys) throws SystemException,
			    CommunicationException, AuthorizationException, DataValidationException, TransactionFailedException,
			    NotAcceptedException, NotFoundException
		{    	    
    	    HashMap svcGrouping = groupByService(sessionName, classKeys, BusinessServiceClientRoutingProxy.CLASS);
    	    Iterator keys = svcGrouping.keySet().iterator();
    	    while (keys.hasNext())
    	    {
    	        Object svc = keys.next();
    	        Object[] classes = ((ArrayList) svcGrouping.get(svc)).toArray();
    	        int[] tempClassKeys = new int[classes.length];
    	        for (int i = 0; i < classes.length; i++)
    	        {
    	            tempClassKeys[i] = ((Integer) classes[i]).intValue();
    	        }
    	        ((MarketMakerQuoteService) svc).systemCancelQuotesByClass(userId, sessionName, tempClassKeys);
    	    }
		}

    	/**
    	 * Determines where each class is being managed, forwards the request to the
    	 * appropriate MMQS
    	 */
    	public void cancelQuotesByClass(String userId, String sessionName, int[] classKeys) throws SystemException,
			    CommunicationException, AuthorizationException, DataValidationException, TransactionFailedException,
			    NotAcceptedException, NotFoundException
         {    
    		 long entityId = 0L;
    	     long quoteBlock = (long) classKeys.length;
    	     int classToUse = classKeys[0];
    	     long id = TransactionTimingUtil.generateQuoteMetricId(classToUse, sessionName, quoteBlock);

             TransactionTimingUtil.sendMethodEvent(TransactionTimingRegistration.QUOTE_CANCEL_BY_CLASS_COLLECTOR_TYPE, id, 0);
             
             if (classKeys.length > 1)
             {
                classToUse = 0;
             }
             
             try
             {
                 entityId = TransactionTimingUtil.getEntityID();
             }
             catch (Exception e)
             {
                 Log.information(this, "Unable to set EntityID! Exception details: " + e.getMessage());
             }
             TransactionTimingUtil.generateQuoteEvent(TransactionTimingRegistration.getCancelQuoteRoutingProxyEmitPoint(), entityId, TransactionTimer.Enter );
             
             HashMap svcGrouping = groupByService(sessionName, classKeys, BusinessServiceClientRoutingProxy.CLASS);
             Iterator keys = svcGrouping.keySet().iterator();
             while (keys.hasNext())
             {
                 Object svc = keys.next();
                 Object[] classes = ((ArrayList) svcGrouping.get(svc)).toArray();
                 int[] tempClassKeys = new int[classes.length];
                 for (int i = 0; i < classes.length; i++)
                 {
                     tempClassKeys[i] = ((Integer) classes[i]).intValue();
                 }
                 try
                 {
                     ((MarketMakerQuoteService) svc).cancelQuotesByClass(userId, sessionName, tempClassKeys);
                 }
                 catch (OBJECT_NOT_EXIST e)
                 {
                     throw convertToNotAcceptedException(e, "cancelQuotesByClass failed, userId:" + userId
                             + " sessionName:" + sessionName + " classKey[0]:" + classKeys[0]);
                 }
             }
             TransactionTimingUtil.generateQuoteEvent(TransactionTimingRegistration.getCancelQuoteRoutingProxyEmitPoint(), entityId, TransactionTimer.Leave);
             TransactionTimingUtil.sendMethodEvent(TransactionTimingRegistration.QUOTE_CANCEL_BY_CLASS_COLLECTOR_TYPE, id, 1);
         }

        /**
         * This method sends a getRFQ request to all services that handle the
         * respective classes. The argument to the method is an array of int (
         * classkeys ). The business service that handles each class needs to be
         * determined before the request can be forwarded. That way the class ( or
         * class list ) will forwarded to its respective business server.
         * 
         * @param sessionName
         * @param classKeys
         * @return
         */
    	public RFQStruct[] getRFQ(String sessionName, int[] classKeys) throws SystemException, CommunicationException,
			    DataValidationException, AuthorizationException
		{    	    
    	    if (classKeys.length == 1)
    	    {
    	        MarketMakerQuoteService service = (MarketMakerQuoteService) getServiceByClass(sessionName, classKeys[0]);
    	        return service.getRFQ(sessionName, classKeys);
    	    }
    	    MarketMakerQuoteServiceClientReplyHandler replyHandler = (MarketMakerQuoteServiceClientReplyHandler) reserveReplyHandler();
    	    replyHandler.setNumberOfRequests(routeMap.size());
    	    int systemFailures = 0;
    	    HashMap serviceClassMap = groupByService(sessionName, classKeys, BusinessServiceClientRoutingProxy.CLASS);
            Iterator services = serviceClassMap.keySet().iterator();
            while (services.hasNext())
            {
                Object service = services.next();
                Object[] classes = ((ArrayList) serviceClassMap.get(service)).toArray();
                int[] tmpClassKeys = new int[classes.length];
                for (int i = 0; i < classes.length; i++)
                {
                    tmpClassKeys[i] = ((Integer) classes[i]).intValue();
                }
                try
                {
                    if (Log.isDebugOn())
                    {
                        Log.debug(this, "Sending RFQ request to service");
                    }
                    ((MarketMakerQuoteService) service).sendc_getRFQ(replyHandler.getAMIHandler(), sessionName, tmpClassKeys);
                }
                catch (org.omg.CORBA.SystemException e)
                {
                    if (Log.isDebugOn())
                    {
                        Log.debug(this, "Error sending RFQ request to service");
                    }                    
                    Log.exception(this, e);
                    systemFailures++;
                }
            }
            if (systemFailures != serviceClassMap.size())
            {
                waitForReply(replyHandler);
            }
            RFQStruct[] rfqStructArray =  replyHandler.getQuoteData();
            return rfqStructArray;
		}

        /**
         * Request is forwarded to the service associated with the productkey not
         * classkey
         */
    	public void requestForQuote(RFQStruct rfqStruct, String memberKey) throws SystemException, CommunicationException,
			    TransactionFailedException, NotAcceptedException, AuthorizationException, DataValidationException
        { 
    		long id = TransactionTimingUtil.generateQuoteMetricId(rfqStruct.productKeys.productKey, rfqStruct.sessionName, 1L);
            TransactionTimingUtil.sendMethodEvent(TransactionTimingRegistration.RFQ_COLLECTOR_TYPE, id, 0);

            MarketMakerQuoteService service = (MarketMakerQuoteService) getServiceByProduct(rfqStruct.sessionName,
			rfqStruct.productKeys.productKey);
            service.requestForQuote(rfqStruct, memberKey);
            TransactionTimingUtil.sendMethodEvent(TransactionTimingRegistration.RFQ_COLLECTOR_TYPE, id, 1);
        }

    	public void shutdown()
    	{
    	}

        /**
         * Return the Service Helper class name
         *
         * @return String, the service helper class name related to this proxy
         */
    	protected String getHelperClassName()
    	{    	   
    	    return "com.cboe.idl.businessServices.MarketMakerQuoteServiceHelper";
    	}

        /**
         * Check if replyHandler is ready. If not, wait on it
         *
         * @param replyHandler
         */
    	private void waitForReply(MarketMakerQuoteServiceClientReplyHandler replyHandler)
    	{    	    
    	    synchronized (replyHandler)
    	    {
    	        if (!replyHandler.isReady())
    	        {
    	            try
    	            {
    	                replyHandler.wait();
    	                if (Log.isDebugOn())
                        {
    	                    Log.debug(this, "Received " + replyHandler.getNumberOfResponses() + " responses");
    	                    Log.debug(this, "Reveived " + replyHandler.getNumberOfExceptions() + " exceptions");
                         }
                    }
    	            catch (InterruptedException e)
    	            {
    	                Log.exception(this, e);
                    }
                }
            }
    	}

        /**
         * Cancel Quotes for user[s]
    	 * 
    	 * @param routingParams -
    	 *            Routing info
    	 * @param userIdRequestingCancel -
    	 *            The user id (login) of the help desk user invoking the request
    	 * @param userIds -
    	 *            A <code>java.lang.String</code> array of userIds whose Quote
    	 *            need to be cancelled
    	 * @param transactionId -
    	 *            The unique identifier for the request, as defined by the SAGUI
    	 *            (used to to idee cancntify this request).
    	 * @param timestamp -
    	 *            The timestamp "millis since 1/1/70 GMT" of the request being
    	 *            invoked from the SAGUI
    	 * @param properties -
    	 *            The <code>KeyValueStruct</code> instance
         * @return  ServerResponseStruct
         * @throws DataValidationException
         * @throws CommunicationException
         * @throws SystemException
         * @throws AuthorizationException
         */
    	public ServerResponseStruct[] cancelQuotesForUsers(RoutingParameterStruct routingParams, String userIdRequestingCancel,
    	        String[] userIds, String transactionId, DateTimeStruct timestamp, KeyValueStruct[] properties)
                throws DataValidationException, CommunicationException, SystemException, AuthorizationException
        { 
    	    return executeBulkServiceRequest(routingParams, userIdRequestingCancel, userIds, transactionId, timestamp, properties,
				CANCEL_QUOTES);
        }

        /**
         * Get the Quote Count for users
    	 * 
    	 * @param routingParams -
    	 *            Routing info
    	 * @param userIdRequestingCancel -
    	 *            The user id (login) of the help desk user invoking the request
    	 * @param userIds -
    	 *            A <code>java.lang.String</code> array of userIds whose Quote
    	 *            Count is required
    	 * @param transactionId -
    	 *            The unique identifier for the request, as defined by the SAGUI
    	 *            (used to to identify this request).
    	 * @param timestamp -
    	 *            The timestamp "millis since 1/1/70 GMT" of the request being
    	 *            invoked from the SAGUI
    	 * @param properties -
    	 *            The <code>KeyValueStruct</code> instance
         * @return ServerResponseStruct
         * @throws DataValidationException
         * @throws CommunicationException
         * @throws SystemException
         * @throws AuthorizationException
         */
    	public ServerResponseStruct[] getQuoteCountForUsers(RoutingParameterStruct routingParams, String userIdRequestingCancel,
    	        String[] userIds, String transactionId, DateTimeStruct timestamp, KeyValueStruct[] properties)
                throws DataValidationException, CommunicationException, SystemException, AuthorizationException
        {
    	    return executeBulkServiceRequest(routingParams, userIdRequestingCancel, userIds, transactionId, timestamp, properties,
				GET_QUOTES);
        }

        /**
         * Execute the Bulk Task Request(Cancel Quote & get Quote Count)
    	 * 
    	 * @param routingParams -
    	 *            Routing info
    	 * @param userIdRequestingCancel -
    	 *            The user id (login) of the help desk user invoking the request
    	 * @param userIds -
    	 *            A <code>java.lang.String</code> array of userIds whose Order
    	 *            need be cancelled
    	 * @param transactionId -
    	 *            The unique identifier for the request, as defined by the SAGUI
    	 *            (used to to identify this request).
    	 * @param timestamp -
    	 *            The timestamp "millis since 1/1/70 GMT" of the request being
    	 *            invoked from the SAGUI
    	 * @param properties -
    	 *            The <code>KeyValueStruct</code> instance
    	 * @param requestType -
    	 *            The Type of Request
    	 * @return ServerResponseStruct
    	 */
    	private ServerResponseStruct[] executeBulkServiceRequest(RoutingParameterStruct routingParams, String userIdRequestingCancel,
    	        String[] userIds, String transactionId, DateTimeStruct timestamp, KeyValueStruct[] properties, String requestType)
    	{
    	    
    	    List<String> serviceRoutes = ClientFederatedServiceHelper.getServiceRoutes(routeMap);
    	    ServerResponseStruct[] serverResponseStruct = new ServerResponseStruct[serviceRoutes.size()];
    	    int index = 0;
    	    ServerResponseStruct[] responseStructs = null;
    	    MarketMakerQuoteService targetService = null;
    	    for (String serviceRoute : serviceRoutes)
    	    {
    	        String exceptionMsg = ClientFederatedServiceHelper.EMPTY_STRING;
    	        targetService = (MarketMakerQuoteService) routeMap.get(serviceRoute);

    	        try
    	        {
    	            if (CANCEL_QUOTES.equals(requestType))
    	            {
    	                responseStructs = targetService.cancelQuotesForUsers(routingParams, userIdRequestingCancel, userIds,
                                                                         transactionId, timestamp, properties);
    	            }
    	            else if (GET_QUOTES.equals(requestType))
    	            {
    	                responseStructs = targetService.getQuoteCountForUsers(routingParams, userIdRequestingCancel, userIds,
							transactionId, timestamp, properties);
    	            }
    	            serverResponseStruct[index] = responseStructs[0];

    	        }
    	        catch (Exception e)
    	        {
    	            exceptionMsg = e.getMessage();
    	            Log.exception(this, new StringBuilder("Exception on sending ").append(requestType).append(" request to service ")
						.append(serviceRoute).toString(), e);
    	        }
    	        finally
    	        {
    	            if (serverResponseStruct[index] == null)
    	            {
    	                serverResponseStruct[index] = ClientFederatedServiceHelper.getServerResponseStruct(serviceRoute,
                                                                                  ServerResponseCodes.SYSTEM_EXCEPTION,
                                                                                  (exceptionMsg==null)?"(nomsg)":exceptionMsg);
    	            }                

    	            index++;
    	        }
    	    }
    	    
    	    return serverResponseStruct;
    	}

   /* protected org.omg.CORBA.Object createDecoratorForOutboundCalls(org.omg.CORBA.Object anObjectReference)
	{
		return new com.cboe.proxy.businessServices.MarketMakerQuoteServiceRoutingProxyDecoratorInterceptor (anObjectReference);
    }*/
	
}//EOF
