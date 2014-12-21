package com.cboe.consumers.eventChannel;

import com.cboe.domain.util.ExchangeFirmStructContainer;
import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.SystemException;
import com.cboe.idl.cmiUser.ExchangeFirmStruct;
import com.cboe.idl.consumers.QuoteStatusConsumer;
import com.cboe.idl.consumers.QuoteStatusConsumerV2;
import com.cboe.idl.quote.QuoteAcknowledgeStruct;
import com.cboe.idl.quote.QuoteAcknowledgeStructV3;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.infrastructureServices.foundationFramework.exceptionHandling.CBOELoggableException;
import com.cboe.infrastructureServices.foundationFramework.HomeFactory;
import com.cboe.interfaces.businessServices.QuoteStatusSubscriptionService;
import com.cboe.interfaces.events.IECQuoteStatusConsumerHome;
import com.cboe.util.ChannelKey;
import com.cboe.util.ChannelType;
import com.cboe.util.ExceptionBuilder;

import java.util.HashMap;
import java.util.Map;

/** Used by Client programs to subscribe directly to channel in the FE DN,
 * rather than asking FE to make the subscription and relay the messages back
 * to the Client.
 * @see com.cboe.consumers.eventChannel.QuoteStatusConsumerHomeCallbackImpl
 * @see com.cboe.businessServices.quoteStatusSubscriptionService.QuoteStatusSubscriptionServiceImpl
 */
public class QuoteStatusSubscriptionServiceClientChannelImpl
        implements QuoteStatusSubscriptionService
{
    private short quoteStatusBlockSize;
    private IECQuoteStatusConsumerHome homeForQuoteFilters;
    private static final String QUOTE_STATUS_BLOCK_SIZE = "quoteStatusBlockSize";
    private HashMap<String, Map> subscribersBySource; // source -> Map(userId, #subscriptions)
    private Map<String,Integer> userSubscriptions; // Map(userId, #subscriptions)

    public QuoteStatusSubscriptionServiceClientChannelImpl()
    {
        super();
        initialize();
    }
    
    private void initialize()
    {
        subscribersBySource = new HashMap<String, Map>();
        userSubscriptions = new HashMap();
        StringBuilder msg = new StringBuilder(50);

        try
        {
            quoteStatusBlockSize = Short.parseShort(System.getProperty(QUOTE_STATUS_BLOCK_SIZE));
        }
        catch (Exception e)
        {
            msg.append("Failed to retrieve ").append(QUOTE_STATUS_BLOCK_SIZE).append(": ").append(e.getMessage());
            Log.information(msg.toString());
            msg.setLength(0);  // reset for use in finally { } block
        }
        finally
        {
            msg.append("Client value for ").append(QUOTE_STATUS_BLOCK_SIZE).append(":").append(quoteStatusBlockSize);
            Log.information(msg.toString());
        }
    }
    // variables and methods for tracking subscriptions

    

    private Map getMap(Map<String, Map> mapDirectory, String key)
    {
        synchronized (mapDirectory)
        {
            Map result = mapDirectory.get(key);
            if (null == result)
            {
                result = new HashMap();
                mapDirectory.put(key, result);
            }
            return result;
        }
    }
    /**
     *  Get Home that adds and removes filters for quote status calls from server.
     * @return The Home.
     */
    private IECQuoteStatusConsumerHome getHomeForQuoteFilters()
    {
        if(homeForQuoteFilters == null)
        {
            try
            {
                homeForQuoteFilters = (IECQuoteStatusConsumerHome) HomeFactory.getInstance().findHome(IECQuoteStatusConsumerHome.CLIENT_CHANNEL_HOME_NAME);
            }
            catch (CBOELoggableException e)
            {
                Log.exception(e);
                // a really ugly way to get around the missing exception in the interface...
                throw new NullPointerException("Could not find QuoteStatusConsumerHome (UOQ)");
            }
        }
        return homeForQuoteFilters;

        /*
        if (homeForQuoteFilters == null)
        {
            homeForQuoteFilters = ServicesHelper.getQuoteStatusConsumerClientChannelHome();
        }
        return homeForQuoteFilters;
        */
    }

    /**
     * Add filters so we receive CORBA calls for QuoteStatus for this user.
     * @param userId User making the subscription request.
     * @param quoteStatusConsumer User's callback object.
     * @param source Client where user is logged in.
     * @param blockSize Ignored.
     */
   	protected void doSubscribeQuoteStatus(String userId, QuoteStatusConsumer quoteStatusConsumer, String source, int blockSize)
	   throws SystemException, CommunicationException, DataValidationException, AuthorizationException
	{
        StringBuilder dosub = new StringBuilder(90);
        dosub.append("doSubscribeQuoteStatus userId:").append(userId).append(" source:").append(source);
        Log.information(dosub.toString());

        // Filter in CORBA calls for Quote Status by user
        getHomeForQuoteFilters().addFilter(new ChannelKey( ChannelType.QUOTE_FILL_REPORT, userId ) );
        getHomeForQuoteFilters().addFilter(new ChannelKey( ChannelType.QUOTE_BUST_REPORT, userId ) );
        getHomeForQuoteFilters().addFilter(new ChannelKey( ChannelType.QUOTE_DELETE_REPORT, userId ) );
        getHomeForQuoteFilters().addFilter(new ChannelKey( ChannelType.QUOTES_DELETE_REPORTV2, userId ) );
        getHomeForQuoteFilters().addFilter(new ChannelKey( ChannelType.QUOTE_STATUS_UPDATE, userId ) );
      
	}

    /**
     * Remove filters so we no longer receive CORBA calls for QuoteStatus for this user.
     * @param userId User making the unsubscription request.
     * @param quoteStatusConsumer User's callback object.
     * @param source Client where user is logged in.
     */
    protected void doUnsubscribeQuoteStatus(String userId, QuoteStatusConsumer quoteStatusConsumer, String source)
            throws SystemException, CommunicationException, DataValidationException, AuthorizationException
    {
        StringBuilder dounsub = new StringBuilder(70);
        dounsub.append("doUnsubscribeQuoteStatus userId:").append(userId).append(" source:").append(source);
        Log.information(dounsub.toString());

        // Remove filters that enabled CORBA calls for Quote Status by user
        getHomeForQuoteFilters().removeFilter(new ChannelKey( ChannelType.QUOTE_FILL_REPORT, userId));
        getHomeForQuoteFilters().removeFilter(new ChannelKey( ChannelType.QUOTE_BUST_REPORT, userId));
        getHomeForQuoteFilters().removeFilter(new ChannelKey( ChannelType.QUOTE_DELETE_REPORT, userId));
        getHomeForQuoteFilters().removeFilter(new ChannelKey( ChannelType.QUOTES_DELETE_REPORTV2, userId));
        getHomeForQuoteFilters().removeFilter(new ChannelKey( ChannelType.QUOTE_STATUS_UPDATE, userId));
    }

    protected void doSubscribeQuoteStatusForFirm(ExchangeFirmStructContainer exFirmStructContainer, QuoteStatusConsumer quoteStatusConsumer, String source, int blockSize)
            throws SystemException, CommunicationException, DataValidationException, AuthorizationException
    {
        StringBuilder dosub = new StringBuilder(80);
        dosub.append("doSubscribeQuoteStatusForFirm:").append(exFirmStructContainer.getExchange())
             .append(":").append(exFirmStructContainer.getFirmNumber())
             .append(" source:").append(source);
        Log.information(dosub.toString());

        // Filter in CORBA calls for Quote Status By Firm
        getHomeForQuoteFilters().addFilter(new ChannelKey(ChannelType.QUOTE_FILL_REPORT_BY_FIRM, exFirmStructContainer));
        getHomeForQuoteFilters().addFilter(new ChannelKey(ChannelType.QUOTE_BUST_REPORT_BY_FIRM, exFirmStructContainer));
    }

    protected void doUnsubscribeQuoteStatusForFirm(ExchangeFirmStructContainer exFirmStruct, QuoteStatusConsumer quoteStatusConsumer, String source)
            throws SystemException, CommunicationException, DataValidationException, AuthorizationException
    {
        StringBuilder dounsub = new StringBuilder(80);
        dounsub.append("doUnsubscribeQuoteStatusForFirm: ").append(exFirmStruct.getExchange())
               .append(":").append(exFirmStruct.getFirmNumber())
               .append(" source:").append(source);
        Log.information(dounsub.toString());

        // Remove filters that enabled CORBA calls for Quote Status By Firm
        getHomeForQuoteFilters().removeFilter(new ChannelKey(ChannelType.QUOTE_FILL_REPORT_BY_FIRM, exFirmStruct));
        getHomeForQuoteFilters().removeFilter(new ChannelKey(ChannelType.QUOTE_BUST_REPORT_BY_FIRM, exFirmStruct));
    }

    protected void doSubscribeQuoteStatusForTradingFirm(String userId, QuoteStatusConsumer quoteStatusConsumer, String source, int blockSize)
            throws SystemException, CommunicationException, DataValidationException, AuthorizationException
    {
        StringBuilder dosub = new StringBuilder(90);
        dosub.append("doSubscribeQuoteStatusForTradingFirm userId:").append(userId)
             .append(" source:").append(source);
        Log.information(dosub.toString());

        // Filter in CORBA calls for Quote Status that Trading Firm will need
        // Reminder: the channel key given here is translated into filter strings. The
        // correspondences can be confusing, such as in this case.
        getHomeForQuoteFilters().addFilter(new ChannelKey(ChannelType.QUOTE_FILL_REPORT, userId));
        getHomeForQuoteFilters().addFilter(new ChannelKey(ChannelType.QUOTE_BUST_REPORT, userId));
    }

    protected void doUnsubscribeQuoteStatusForTradingFirm(String userId, QuoteStatusConsumer quoteStatusConsumer, String source)
            throws SystemException, CommunicationException, DataValidationException, AuthorizationException
    {
        StringBuilder dounsub = new StringBuilder(70);
        dounsub.append("doUnsubscribeQuoteStatus user:").append(userId)
               .append(" source:").append(source);
        Log.information(dounsub.toString());

        // Remove filters that enabled CORBA calls for Quote Status By Trading Firm
        getHomeForQuoteFilters().removeFilter(new ChannelKey(ChannelType.QUOTE_FILL_REPORT, userId));
        getHomeForQuoteFilters().removeFilter(new ChannelKey(ChannelType.QUOTE_BUST_REPORT, userId));
    }

    

    /** Determine if a user has entered a subscription from a Client.
     * @param source ID of Client (CAS, FIXCAS...).
     * @param subscriberId UserId or Exchange:Firm.
     * @return true if any subscription exists, else false.
     */
    private boolean isSubscribed(String source, String subscriberId)
    {
        synchronized (subscribersBySource)
        {
            Map sourceSubscriptions = getMap(subscribersBySource, source);
            return sourceSubscriptions.containsKey(subscriberId);
        }
    }

    private int changeNumberOfSubscriptions(String source, String subscriberId, int delta)
            throws SystemException
    {
        int numSubscriptions;
        synchronized (subscribersBySource)
        {
            try
            {
                Map<String,Integer> sourceSubscriptions = (Map<String,Integer>) getMap(subscribersBySource, source);
                Integer oldCount = sourceSubscriptions.get(subscriberId);
                if (null == oldCount)
                {
                    numSubscriptions = -1;
                }
                else
                {
                    numSubscriptions = oldCount + delta;
                    sourceSubscriptions.put(subscriberId, numSubscriptions);
                }
            }
            catch (Exception e)
            {
                Log.exception(e);
                throw ExceptionBuilder.systemException(e.getMessage(), 0);
            }
        }
        return numSubscriptions;
    }

    /** Add 1 to the count of the number of times that a user has made a subscription.
     * @param source ID of Client (CAS, FIXCAS...).
     * @param subscriberId UserId or Exchange:Firm.
     * @return New number of subscriptions.
     */
    private int incrementSubscriptions(String source, String subscriberId)
            throws SystemException
    {
        return changeNumberOfSubscriptions(source, subscriberId, +1);
    }

    /** Subtract 1 from the count of the number of times that a user has made a subscription.
     * @param source ID of Client (CAS, FIXCAS...).
     * @param subscriberId UserId or Exchange:Firm.
     * @return New number of subscriptions.
     */
    private int decrementSubscriptions(String source, String subscriberId)
            throws SystemException
    {
        return changeNumberOfSubscriptions(source, subscriberId, -1);
    }

    // interface QuoteStatusSubscriptionService

    /** Apply filters for QuoteStatusV2 messages.
     * @param userId User making subscription request.
     * @param quoteStatusConsumer User's callback object.
     * @param source Client where user is logged in.
     * @param blockSize Maximum population of a block of messages.
     */
    public void subscribeQuoteStatusV2(String userId, QuoteStatusConsumerV2 quoteStatusConsumer, String source, short blockSize)
            throws SystemException, CommunicationException, DataValidationException, AuthorizationException
    {
        if (Log.isDebugOn())
        {
            Log.debug("subscribeQuoteStatusV2 userId:" + userId + " source:" + source);
        }
        if (!isSubscribed(source, userId))
        {
            doSubscribeQuoteStatus(userId, quoteStatusConsumer, source, blockSize > 0 ? blockSize : quoteStatusBlockSize);
            //Add user to Subscribe Map
            userSubscriptions.put(userId,new Integer(1));               
            synchronized (subscribersBySource)
            {
                try
                {
                    subscribersBySource.put(source,userSubscriptions);
                }
                catch (Exception e)
                {
                    Log.exception(e);
                    throw ExceptionBuilder.systemException(e.getMessage(), 0);
                }
            }

        }
        else
        {
            int count = incrementSubscriptions(source, userId);
            StringBuilder subscribe = new StringBuilder(80);
            subscribe.append("subscribeQuoteStatusV2 userId:").append(userId)
                     .append(" source:").append(source)
                     .append(" count:").append(count);
            Log.information(subscribe.toString());
        }
    }

    /** Remove filters for QuoteStatusV2 messages.
     * @param userId User making unsubscription request.
     * @param quoteStatusConsumer User's callback object.
     * @param source Client where user is logged in.
     */
    public void unsubscribeQuoteStatusV2(String userId, QuoteStatusConsumerV2 quoteStatusConsumer, String source)
            throws SystemException, CommunicationException, DataValidationException, AuthorizationException
    {
        if (Log.isDebugOn())
        {
            Log.debug("unsubscribeQuoteStatusV2 userId:" + userId + " source:" + source);
        }
        int remaining = decrementSubscriptions(source, userId);
        if (remaining == 0)
        {
            doUnsubscribeQuoteStatus(userId, quoteStatusConsumer, source);
            userSubscriptions.remove(userId);               
            synchronized (subscribersBySource)
            {
                try
                {
                    subscribersBySource.put(source,userSubscriptions);
                }
                catch (Exception e)
                {
                    Log.exception(e);
                    throw ExceptionBuilder.systemException(e.getMessage(), 0);
                }
            }
            
        }
        else if (remaining > 0)
        {
            StringBuilder unsubRemaining = new StringBuilder(80);
            unsubRemaining.append("unsubscribeQuoteStatusV2 userId:").append(userId)
                          .append(" source:").append(source)
                          .append(" remaining:").append(remaining);
            Log.information(unsubRemaining.toString());
        }
    }

    /** Apply filters for QuoteStatusV2 messages.
     * @param firm Firm user making subscription request.
     * @param quoteStatusConsumer User's callback object.
     * @param source Client where user is logged in.
     * @param blockSize Maximum population of a block of messages.
     */
    public void subscribeQuoteStatusForFirmV2(ExchangeFirmStruct firm, QuoteStatusConsumerV2 quoteStatusConsumer, String source, short blockSize)
            throws SystemException, CommunicationException, DataValidationException, AuthorizationException
    {
        StringBuilder subscribe = new StringBuilder(85);
        subscribe.append(firm.exchange).append(':').append(firm.firmNumber);
        String subscriberId = subscribe.toString();
        if (Log.isDebugOn())
        {
            Log.debug("subscribeQuoteStatusForFirmV2 firm:" + subscriberId + " source:" + source);
        }
        if (quoteStatusConsumer != null)
        {
            if (!isSubscribed(source, subscriberId))
            {
                ExchangeFirmStructContainer exFirmContainer = new ExchangeFirmStructContainer(firm);
                doSubscribeQuoteStatusForFirm(exFirmContainer, quoteStatusConsumer, source, blockSize > 0? blockSize : quoteStatusBlockSize);
            }
            else
            {
                int count = incrementSubscriptions(source, subscriberId);
                subscribe.setLength(0);
                subscribe.append("subscribeQuoteStatusForFirmV2 firm:").append(subscriberId)
                         .append(" source:").append(source)
                         .append(" count:").append(count);
                Log.information(subscribe.toString());
            }
        }
    }

    public void unsubscribeQuoteStatusForFirmV2(ExchangeFirmStruct firm, QuoteStatusConsumerV2 quoteStatusConsumer, String source)
            throws SystemException, CommunicationException, DataValidationException, AuthorizationException
    {
        StringBuilder unsub = new StringBuilder(90);
        unsub.append(firm.exchange).append(':').append(firm.firmNumber);
        String subscriberId = unsub.toString();
        if (Log.isDebugOn())
        {
            Log.debug("unsubscribeQuoteStatusForFirmV2 firm:" + subscriberId + " source:" + source);
        }
        if (quoteStatusConsumer != null)
        {
        	int remaining = decrementSubscriptions(source, subscriberId);
           	if (remaining == 0)
        	{
                ExchangeFirmStructContainer exFirmContainer = new ExchangeFirmStructContainer(firm);
                doUnsubscribeQuoteStatusForFirm(exFirmContainer, quoteStatusConsumer, source);
        	}
           	else if (remaining > 0)
           	{
                unsub.setLength(0);
                unsub.append("unsubscribeQuoteStatusForFirmV2 firm:").append(subscriberId)
                     .append(" source:").append(source)
                     .append(" remaining:").append(remaining);
                Log.information(unsub.toString());
           	}
        }
    }

    public void subscribeQuoteStatusForTradingFirmUser(String userId, String groupId, QuoteStatusConsumerV2 quoteStatusConsumer, String source, short blockSize)
            throws SystemException, CommunicationException, DataValidationException, AuthorizationException
    {
        if (Log.isDebugOn())
        {
            Log.debug("subscribeQuoteStatusForTradingFirmUser userId:" + userId + " source:" + source);
        }
        if (quoteStatusConsumer != null)
        {
            if (!isSubscribed(source,userId))
            {
                doSubscribeQuoteStatusForTradingFirm(userId, quoteStatusConsumer, source, blockSize > 0? blockSize : quoteStatusBlockSize);
            }
            else
            {
                int count = incrementSubscriptions(source, userId);
                StringBuilder sub = new StringBuilder(95);
                sub.append("subscribeQuoteStatusForTradingFirmUser userId:").append(userId)
                   .append(" source:").append(source)
                   .append(" count:").append(count);
                Log.information(sub.toString());
            }
        }
    }

    /** Remove subscription for a Firm user.
     * @param userId ID of the Firm user.
     * @param groupId ID of the Firm.
     * @param quoteStatusConsumer User's callback object.
     * @param source Client (CAS, FIXCAS...) where user application is running.
     */
    public void unsubscribeQuoteStatusForTradingFirmUser(String userId, String groupId, QuoteStatusConsumerV2 quoteStatusConsumer, String source)
            throws SystemException, CommunicationException, DataValidationException, AuthorizationException
    {
        if (Log.isDebugOn())
        {
            Log.debug("unsubscribeQuoteStatusForTradingFirmUser userId:" + userId + " source:" + source);
        }
        if ( quoteStatusConsumer != null)
        {
            int remaining = decrementSubscriptions(source, userId);
            if (remaining == 0)
            {
                doUnsubscribeQuoteStatusForTradingFirm(userId, quoteStatusConsumer, source);
            }
            else if (remaining > 0)
            {
                StringBuilder unsubRemaining = new StringBuilder(110);
                unsubRemaining.append("unsubscribeQuoteStatusForTradingFirmUser userId:").append(userId)
                              .append(" source:").append(source)
                              .append(" remaining:").append(remaining);
                Log.information(unsubRemaining.toString());
            }
        }
    }

    // Methods implemented in FE

    public void ackQuoteStatus(QuoteAcknowledgeStruct quoteAcknowledge)
            throws SystemException, CommunicationException, DataValidationException, AuthorizationException
    {
        throw ExceptionBuilder.systemException("Method not implemented: ackQuoteStatus", 0);
    }

    public void publishQuoteStatus(String userId)
            throws SystemException, CommunicationException, DataValidationException, AuthorizationException
    {
        throw ExceptionBuilder.systemException("Method not implemented: publishQuoteStatus", 0);
    }

    public void publishQuoteStatusByClass(String userId, int classKey)
            throws SystemException, CommunicationException, DataValidationException, AuthorizationException
    {
        throw ExceptionBuilder.systemException("Method not implemented: publishQuoteStatusByClass", 0);
    }

    // Methods not used by our caller

    public void ackQuoteStatusV3(QuoteAcknowledgeStructV3 quoteAcknowledge)
            throws SystemException, CommunicationException, DataValidationException, AuthorizationException
    {
        throw ExceptionBuilder.systemException("Method not implemented: ackQuoteStatusV3", 0);
    }

    public void subscribeQuoteStatus(String userId, QuoteStatusConsumer quoteStatusConsumer, String source)
            throws SystemException, CommunicationException, DataValidationException, AuthorizationException
    {
        throw ExceptionBuilder.systemException("Method not implemented: subscribeQuoteStatus", 0);
    }

    public void subscribeQuoteStatusForFirm(ExchangeFirmStruct firm, QuoteStatusConsumer quoteStatusConsumer, String source)
            throws SystemException, CommunicationException, DataValidationException, AuthorizationException
    {
        throw ExceptionBuilder.systemException("Method not implemented: subscribeQuoteStatusForFirm", 0);
    }

    public void unsubscribeQuoteStatus(String userId, QuoteStatusConsumer quoteStatusConsumer, String source)
            throws SystemException, CommunicationException, DataValidationException, AuthorizationException
    {
        throw ExceptionBuilder.systemException("Method not implemented: unsubscribeQuoteStatus", 0);
    }

    public void unsubscribeQuoteStatusForFirm(ExchangeFirmStruct firm, QuoteStatusConsumer quoteStatusConsumer, String source)
            throws SystemException, CommunicationException, DataValidationException, AuthorizationException
    {
        throw ExceptionBuilder.systemException("Method not implemented: unsubscribeQuoteStatusForFirm", 0);
    }

    public void registerConsumerForProcessBackReferences() throws SystemException
    {
        throw ExceptionBuilder.systemException("Method not implemented: registerConsumerForProcessBackReferences", 0);
    }
}
