package com.cboe.application.quote;

import com.cboe.application.shared.ServicesHelper;
import com.cboe.application.failover.ServerFailureListenerBase;
import com.cboe.application.quote.common.QuoteSemaphoreHandler;
import com.cboe.interfaces.businessServices.MarketMakerQuoteService;
import com.cboe.interfaces.application.QuoteCacheControlService;
import com.cboe.interfaces.application.UserSessionQueryHome;
import com.cboe.domain.util.ServerFailureEventHolder;
import com.cboe.domain.util.TimeServiceWrapper;
import com.cboe.domain.util.UserActivityTimeoutEventHolder;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.idl.cmiQuote.QuoteDetailStruct;
import com.cboe.idl.constants.ServerTypes;
import com.cboe.exceptions.*;

public class QuoteCacheControlServiceImpl extends ServerFailureListenerBase implements QuoteCacheControlService
{
    private MarketMakerQuoteService marketMakerQuoteService;
    
    void initialize()
    {
        getMarketMakerQuoteService();
        
        if(marketMakerQuoteService == null)
        {
            throw new RuntimeException("Could not get hold of Market Maker Quote Service");
        }
    }
    
    void start() throws Exception
    {
        // We are subscribing by server type because we only want events that are a
        // result of a server failover; this concrete implementation of ServerFailureListenerBase
        // cleans up the QuoteCache, and we only do this if the trade server went down.
        subscribeForServerFailureEvents(ServerTypes.TRADE_SERVER);

        subscribeForUserActivityTimeoutEvents();
    }
    
    // Implementation of the template method in AbstractFailoverListener
    protected void processServerFailure(ServerFailureEventHolder holder)
    {
        long startTimeMillis = TimeServiceWrapper.getCurrentDateTimeInMillis();
        if(Log.isDebugOn())
        {
            Log.debug(this, "Processing server failure event.");
        }
        
        try
        {
            cleanupQuoteCaches(holder.getSessionName(), holder.getClassKeys(), holder.getActivityReason());
        }
        catch(Exception e)
        {
            Log.exception(this, "Unhandled exception during quote cache cleanup.", e);
        }
        long endTimeMillis = TimeServiceWrapper.getCurrentDateTimeInMillis();

        StringBuilder completed = new StringBuilder(80);
        completed.append("Completed cleaning up all quotes for this server failure event in ")
                 .append((endTimeMillis - startTimeMillis)).append("ms.");
        Log.information(this, completed.toString());
    }
    
    protected void processUserActivityTimeout(UserActivityTimeoutEventHolder holder)
    {
        long startTimeMillis = TimeServiceWrapper.getCurrentDateTimeInMillis();
        String userId = holder.getUserId();
        String sessionName = holder.getSessionName();
        StringBuilder sb = new StringBuilder(sessionName.length()+userId.length()+85);
        sb.append("Cleaning up quotes. UserActivityTimeout for session:").append(sessionName).append(" user:").append(userId);
        Log.information(this, sb.toString());

        int[] classKeys = holder.getClassKeys();
        short activityReason = holder.getActivityReason();
        for (int i = 0; i < classKeys.length; ++i)
        {
            try
            {
                deleteQuotes(userId, sessionName, classKeys[i], activityReason);
            }
            catch(Exception e)
            {
                Log.exception(this, "Unexpected exception encountered during quote deletion operation for userId=" +
                    userId + " sessionName=" + sessionName + " classKey=" + classKeys[i] + " activityReason=" +
                    activityReason, e);
            }
        }

        long endTimeMillis = TimeServiceWrapper.getCurrentDateTimeInMillis();
        sb.setLength(0);
        sb.append("Completed cleaning up all quotes for this UserActivityTimeout event in ")
          .append(endTimeMillis - startTimeMillis).append(" ms.");
        Log.information(this, sb.toString());
    }

    private void cleanupQuoteCaches(String sessionName, int[] classKeys, short activityReason)
    {
        UserSessionQueryHome userSessionQueryHome = ServicesHelper.getUserSessionQueryHome();
        String[] userIds = com.cboe.client.util.CollectionHelper.EMPTY_String_ARRAY;
        if(userSessionQueryHome != null)
        {
            try
            {
                userIds = userSessionQueryHome.getLoggedInUserIds();
            }
            catch(Exception e)
            {
                Log.exception(this, "Exception while getting list of logged in user ids.", e);
            }
        }

        if(Log.isDebugOn())
        {
            Log.debug(this, "Cleaning up quote caches for " + userIds.length + " users.");
        }
        
        for(int i = 0; i < userIds.length; i++)
        {
            for(int j = 0; j < classKeys.length; j++)
            {
                try
                {
                    deleteQuotes(userIds[i], sessionName, classKeys[j], activityReason);
                }
                catch(Exception e)
                {
                    Log.exception(this, "Unexpected exception encountered during quote deletion operation for userId=" +
                        userIds[i] + " sessionName=" + sessionName + " classKey=" + classKeys[j] + " activityReason=" +
                        activityReason, e);
                }
            }
        }
    }
    
    //// TO BE CHANGED - GJ.
    private void deleteQuotes(String userId, String sessionName, int classKey, short activityReason)
    {
        QuoteSemaphoreHandler.acquireQuoteDeleteAccess(userId, sessionName, classKey);
        try
        {
        	deleteQuotesFromCache(userId, sessionName, classKey, activityReason);
        }
        finally
        {
        	QuoteSemaphoreHandler.releaseQuoteDeleteAccess(userId, sessionName, classKey);
        }
    }    
            
    private void deleteQuotesFromCache(String userId, String sessionName, int classKey, short activityReason)
    {
        // Quote Cache may be null if user logged out in between fetching user Id list
        // (earlier) from the factory and the next call. Pass false here to prevent factory
        // from creating a new QuoteCache object for this user.
        QuoteCache quoteCache = QuoteCacheFactory.find(userId, false);
        if(quoteCache != null)
        {
            QuoteDetailStruct[] quotes = quoteCache.getQuotesForClass(sessionName, classKey);
            if(quotes != null && quotes.length > 0)
            {
                cancelQuotesOnServer(userId, sessionName, classKey);

                if(Log.isDebugOn())
                {
                    Log.debug(this, "Deleting " + quotes.length + " quotes for user/session/class " +
                        userId + "/" + sessionName + "/" + classKey + " and will notify user.");
                }

                quoteCache.cancelQuotesByClass(sessionName, classKey, activityReason);
            }
            else
            {
                if(Log.isDebugOn())
                {
                    Log.debug(this, "No quotes in cache to delete for user/session/class " + 
                            userId + "/" + sessionName + "/" + classKey + ".");
                }
            }
        }
        else
        {
            StringBuilder removed = new StringBuilder(userId.length()+75);
            removed.append("Quote cache for user ").append(userId).append(" was removed from factory -- no quotes to delete.");
            Log.information(this, removed.toString());
        }
    }
    
    private void cancelQuotesOnServer(String userId, String sessionName, int classKey)
    {
        try
        {
            int[] classKeys = { classKey };

            // Note.  Because this is an IEC thread we need to use the system cancel method.
            // If, on the other hand, we ever had to expose this functionality as an IDL service
            // we would need to call the other method. -- Eric Fredericks 4/1/04
            getMarketMakerQuoteService().systemCancelQuotesByClass(userId, sessionName, classKeys);
        }
        catch(AuthorizationException e)
        {
            Log.exception(this, "AuthorizationException when canceling quotes for user/session/class " + userId + "/" + sessionName + "/" + classKey + " after trade server failure; quotes will be deleted from cache!", e);
        }
        catch(TransactionFailedException e)
        {
            Log.exception(this, "TransactionFailedException when canceling quotes for user/session/class " + userId +  "/" + sessionName + "/" + classKey + " after trade server failure; quotes will be deleted from cache!",e);
        }
        catch(CommunicationException e)
        {
            Log.exception(this, "CommunicationException when canceling quotes for user/session/class " + userId +  "/" + sessionName + "/" + classKey + " after trade server failure; quotes will be deleted from cache!",e);
        }
        catch(SystemException e)
        {
            Log.exception(this, "SystemException when canceling quotes for user/session/class " + userId +  "/" + sessionName + "/" + classKey + " after trade server failure; quotes will be deleted from cache!",e);
        }
        catch(DataValidationException e)
        {
            Log.exception(this, "DataValidationException when canceling quotes for user/session/class " + userId +  "/" + sessionName + "/" + classKey + " after trade server failure; quotes will be deleted from cache!",e);
        }
        catch(NotFoundException e)
        {
            Log.exception(this, "NotFoundException when canceling quotes for user/session/class " + userId +  "/" + sessionName + "/" + classKey + " after trade server failure; quotes will be deleted from cache!",e);
        }
        catch(NotAcceptedException e)
        {
            Log.exception(this, "NotAcceptedException when canceling quotes for user/session/class " + userId +  "/" + sessionName + "/" + classKey + " after trade server failure; quotes will be deleted from cache!",e);
        }
        catch(Exception e)
        {
            Log.exception(this, "Exception when canceling quotes for user/session/class " + userId +  "/" + sessionName + "/" + classKey + " after trade server failure; quotes will be deleted from cache!",e);
        }
        
    }
    
    private MarketMakerQuoteService getMarketMakerQuoteService()
    {
        if(marketMakerQuoteService == null)
        {
            marketMakerQuoteService = ServicesHelper.getMarketMakerQuoteService();
        }
        return marketMakerQuoteService;
    }
}
