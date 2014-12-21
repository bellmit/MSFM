package com.cboe.consumers.eventChannel;

import com.cboe.interfaces.businessServices.OrderStatusSubscriptionService;
import com.cboe.interfaces.events.IECOrderStatusConsumerHome;
import com.cboe.interfaces.events.OrderStatusAdminConsumer;
import com.cboe.interfaces.events.OrderStatusAdminConsumerHome;
import com.cboe.exceptions.SystemException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.AuthorizationException;
import com.cboe.idl.consumers.OrderStatusConsumer;
import com.cboe.idl.consumers.OrderStatusConsumerV2;
import com.cboe.idl.cmiUser.ExchangeFirmStruct;
import com.cboe.idl.order.OrderAcknowledgeStruct;
import com.cboe.idl.order.OrderAcknowledgeStructV3;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.infrastructureServices.foundationFramework.HomeFactory;
import com.cboe.infrastructureServices.foundationFramework.exceptionHandling.CBOELoggableException;
import com.cboe.util.ExceptionBuilder;
import com.cboe.util.ChannelKey;
import com.cboe.util.ChannelType;
import com.cboe.domain.util.ExchangeFirmStructContainer;

import java.util.Map;
import java.util.HashMap;

/**
 * Author: mahoney
 * Date: Aug 15, 2008
 * Used by Client programs to subscribe directly to channel in the FE DN,
 * rather than asking FE to make the subscription and relay the messages back
 * to the Client.
 * @see com.cboe.consumers.eventChannel.OrderStatusConsumerHomeCallbackImpl
 * @see com.cboe.businessServices.orderStatusSubscriptionService.OrderStatusSubscriptionServiceImpl
 */
public class OrderStatusSubscriptionServiceClientChannelImpl implements OrderStatusSubscriptionService
{
    private HashMap<String, Map> subscribersBySource; // source -> Map(userId, #subscriptions)
    private Map<String,Integer> userSubscriptions; // Map(userId, #subscriptions)
    private IECOrderStatusConsumerHome homeForOrderFilters;
    private OrderStatusAdminConsumer orderStatusAdminPublisher;

    public OrderStatusSubscriptionServiceClientChannelImpl()
    {
        super();
        initialize();
    }

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

    private void initialize()
    {
        subscribersBySource = new HashMap<String, Map>();
        userSubscriptions = new HashMap();
    }

    /**
     *  Get Home that adds and removes filters for order status calls from server.
     * @return The Home.
     */
    private IECOrderStatusConsumerHome getHomeForOrderFilters()
    {
        if(homeForOrderFilters == null)
        {
            try
            {
                homeForOrderFilters = (IECOrderStatusConsumerHome) HomeFactory.getInstance().findHome(IECOrderStatusConsumerHome.CLIENT_CHANNEL_HOME_NAME);
            }
            catch (CBOELoggableException e)
            {
                Log.exception(e);
                // a really ugly way to get around the missing exception in the interface...
                throw new NullPointerException("Could not find OrderStatusConsumerHome (UOQ)");
            }
        }
        return homeForOrderFilters;
    }

    private OrderStatusAdminConsumer getOrderStatusAdminPublisher()
    {
        if(orderStatusAdminPublisher == null)
        {
            try
            {
                OrderStatusAdminConsumerHome home = (OrderStatusAdminConsumerHome) HomeFactory.getInstance().findHome(OrderStatusAdminConsumerHome.PUBLISHER_HOME_NAME);
                orderStatusAdminPublisher = home.find();
            }
            catch (CBOELoggableException e)
            {
                Log.exception(e);
                // a really ugly way to get around the missing exception in the interface...
                throw new NullPointerException("Could not find OrderStatusAdminPublisher ");
            }
        }
        return orderStatusAdminPublisher;
    }

    public void subscribeOrderStatusV2(String userId, OrderStatusConsumerV2 orderStatusConsumerV2, String casSource, short i) throws SystemException, CommunicationException, DataValidationException, AuthorizationException
    {
    	if (Log.isDebugOn())
    	{
    		Log.debug("subscribeOrderStatus userId:"+userId+" casSource:"+casSource);
    	}
        if ( orderStatusConsumerV2 != null)
        {
        	if (isSubscribed(casSource,userId) == false)
	        {
	            doSubscribeOrderStatusV2(userId, orderStatusConsumerV2, casSource, -1);
	            //Add user to Subscribe Map
	            userSubscriptions.put(userId,new Integer(1));	            
	            synchronized (subscribersBySource)
	            {
	                try
	                {
	                    subscribersBySource.put(casSource,userSubscriptions);
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
        		int count = incrementSubscriptions(casSource, userId);
                StringBuilder increment = new StringBuilder(100);
                increment.append("incrementing the subscriptions. UserId:").append(userId)
                         .append(" casSource:").append(casSource)
                         .append(" count:").append(count);
                Log.information(increment.toString());
        	}
        }
    }

    protected void doSubscribeOrderStatusV2(String userId, OrderStatusConsumer orderStatusConsumer, String casSource, int blockSize)
        throws SystemException, CommunicationException, DataValidationException, AuthorizationException
    {
        StringBuilder dosubscribe = new StringBuilder(80);
        dosubscribe.append("doSubscribeOrderStatusV2 userId:").append(userId)
                   .append(" casSource:").append(casSource);
        Log.information(dosubscribe.toString());

        // Setup the CBOE event channel filter for "all" events
        getHomeForOrderFilters().addFilter(new ChannelKey(ChannelType.NEW_ORDER, userId));
        getHomeForOrderFilters().addFilter(new ChannelKey(ChannelType.ORDER_UPDATE, userId));
        getHomeForOrderFilters().addFilter(new ChannelKey(ChannelType.ORDER_FILL_REPORT, userId));
        getHomeForOrderFilters().addFilter(new ChannelKey(ChannelType.ORDER_BUST_REPORT, userId));
        getHomeForOrderFilters().addFilter(new ChannelKey(ChannelType.ORDER_BUST_REINSTATE_REPORT, userId));
        getHomeForOrderFilters().addFilter(new ChannelKey(ChannelType.ORDER_ACCEPTED_BY_BOOK, userId));
        getHomeForOrderFilters().addFilter(new ChannelKey(ChannelType.CANCEL_REPORT, userId));
        getHomeForOrderFilters().addFilter(new ChannelKey(ChannelType.ORDER_QUERY_EXCEPTION, userId));
        getHomeForOrderFilters().addFilter(new ChannelKey(ChannelType.ACCEPT_ORDERS, userId));
        getHomeForOrderFilters().addFilter(new ChannelKey(ChannelType.ORDER_STATUS_UPDATE, userId));
        
        
        
    }

    public void unsubscribeOrderStatusV2(String userId, OrderStatusConsumerV2 orderStatusConsumerV2, String casSource)
            throws SystemException, CommunicationException, DataValidationException, AuthorizationException
    {
        if (Log.isDebugOn())
        {
            Log.debug("unsubscribeOrderStatusV2 userId:" + userId + " source:" + casSource);
        }
        
        int remaining = decrementSubscriptions(casSource, userId);
        if (remaining == 0)
        {
            doUnsubscribeOrderStatusV2(userId, orderStatusConsumerV2, casSource);
            userSubscriptions.remove(userId);               
            synchronized (subscribersBySource)
            {
                try
                {
                    subscribersBySource.put(casSource,userSubscriptions);
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
            StringBuilder unsubscribe = new StringBuilder(85);
            unsubscribe.append("unsubscribeOrderStatusV2 userId:").append(userId)
                       .append(" source:").append(casSource)
                       .append(" remaining:").append(remaining);
            Log.information(unsubscribe.toString());
        }
    }

        /**
     * Remove filters so we no longer receive CORBA calls for OrderStatus for this user.
     * @param userId User making the unsubscription request.
     * @param orderStatusConsumer User's callback object.
     * @param casSource Client where user is logged in.
     */
    protected void doUnsubscribeOrderStatusV2(String userId, OrderStatusConsumerV2 orderStatusConsumer, String casSource)
            throws SystemException, CommunicationException, DataValidationException, AuthorizationException
    {
        StringBuilder dounsubscribe = new StringBuilder(80);
        dounsubscribe.append("doUnsubscribeOrderStatusV2 userId:").append(userId)
                     .append(" source:").append(casSource);
        Log.information(dounsubscribe.toString());

        // Remove filters that enabled CORBA calls for Order Status by user
        getHomeForOrderFilters().removeFilter(new ChannelKey(ChannelType.NEW_ORDER, userId));
        getHomeForOrderFilters().removeFilter(new ChannelKey(ChannelType.ORDER_UPDATE, userId));
        getHomeForOrderFilters().removeFilter(new ChannelKey(ChannelType.ORDER_FILL_REPORT, userId));
        getHomeForOrderFilters().removeFilter(new ChannelKey(ChannelType.ORDER_BUST_REPORT, userId));
        getHomeForOrderFilters().removeFilter(new ChannelKey(ChannelType.ORDER_BUST_REINSTATE_REPORT, userId));
        getHomeForOrderFilters().removeFilter(new ChannelKey(ChannelType.ORDER_ACCEPTED_BY_BOOK, userId));
        getHomeForOrderFilters().removeFilter(new ChannelKey(ChannelType.CANCEL_REPORT, userId));
        getHomeForOrderFilters().removeFilter(new ChannelKey(ChannelType.ORDER_QUERY_EXCEPTION, userId));
        getHomeForOrderFilters().removeFilter(new ChannelKey(ChannelType.ACCEPT_ORDERS, userId));
        getHomeForOrderFilters().removeFilter(new ChannelKey(ChannelType.ORDER_STATUS_UPDATE, userId));
    }

    public void subscribeOrderStatusForFirmV2(ExchangeFirmStruct exchangeFirmStruct, OrderStatusConsumerV2 orderStatusConsumerV2, String casSource, short blockSize) throws SystemException, CommunicationException, DataValidationException, AuthorizationException
    {
        String subscriberId = exchangeFirmStruct.exchange + ':' + exchangeFirmStruct.firmNumber;
        if (Log.isDebugOn())
        {
            Log.debug("subscribeOrderStatusForFirmV2 firm:" + subscriberId + " source:" + casSource);
        }
        if (orderStatusConsumerV2 != null)
        {
            if (!isSubscribed(casSource, subscriberId))
            {
                ExchangeFirmStructContainer exFirmContainer = new ExchangeFirmStructContainer(exchangeFirmStruct);
                doSubscribeOrderStatusForFirm(exFirmContainer, orderStatusConsumerV2, casSource, 0);
            }
            else
            {
                int count = incrementSubscriptions(casSource, subscriberId);
                StringBuilder subscribe = new StringBuilder(85);
                subscribe.append("subscribeOrderStatusForFirmV2 firm:").append(subscriberId)
                         .append(" source:").append(casSource)
                         .append(" count:").append(count);
                Log.information(subscribe.toString());
            }
        }
    }

    protected void doSubscribeOrderStatusForFirm(ExchangeFirmStructContainer firmContainer, OrderStatusConsumer orderStatusConsumer, String casSource, int blockSize)
        throws SystemException, CommunicationException, DataValidationException, AuthorizationException
    {
        StringBuilder dosubscribe = new StringBuilder(80);
        dosubscribe.append("doSubscribeOrderStatusForFirm:")
                   .append(firmContainer.getExchange()).append(firmContainer.getFirmNumber())
                   .append(" casSource:").append(casSource);
        Log.information(dosubscribe.toString());

        // Setup the CBOE event channel filter for "all" events
        getHomeForOrderFilters().addFilter(new ChannelKey( ChannelType.NEW_ORDER_BY_FIRM, firmContainer));
        getHomeForOrderFilters().addFilter(new ChannelKey( ChannelType.ORDER_UPDATE_BY_FIRM, firmContainer));
        getHomeForOrderFilters().addFilter(new ChannelKey( ChannelType.ORDER_FILL_REPORT_BY_FIRM, firmContainer));
        getHomeForOrderFilters().addFilter(new ChannelKey( ChannelType.ORDER_BUST_REPORT_BY_FIRM, firmContainer));
        getHomeForOrderFilters().addFilter(new ChannelKey( ChannelType.ORDER_BUST_REINSTATE_REPORT_BY_FIRM, firmContainer));
        getHomeForOrderFilters().addFilter(new ChannelKey( ChannelType.ORDER_ACCEPTED_BY_BOOK_BY_FIRM, firmContainer));
        getHomeForOrderFilters().addFilter(new ChannelKey( ChannelType.CANCEL_REPORT_BY_FIRM, firmContainer));
        getHomeForOrderFilters().addFilter(new ChannelKey( ChannelType.ACCEPT_ORDERS_BY_FIRM, firmContainer));
    }

    public void unsubscribeOrderStatusForFirmV2(ExchangeFirmStruct exchangeFirmStruct, OrderStatusConsumerV2 orderStatusConsumerV2, String casSource) throws SystemException, CommunicationException, DataValidationException, AuthorizationException
    {
        String subscriberId = exchangeFirmStruct.exchange + ':' + exchangeFirmStruct.firmNumber;
        if (Log.isDebugOn())
        {
            Log.debug("unsubscribeOrderStatusForFirmV2 firm:" + subscriberId + " source:" + casSource);
        }
        if (orderStatusConsumerV2 != null)
        {
        	int remaining = decrementSubscriptions(casSource, subscriberId);
           	if (remaining == 0)
        	{
                ExchangeFirmStructContainer exchangeFirmContainer = new ExchangeFirmStructContainer(exchangeFirmStruct);
                doUnsubscribeOrderStatusForFirm(exchangeFirmContainer, orderStatusConsumerV2, casSource);
        	}
           	else if (remaining > 0)
           	{
                   StringBuilder unsubscribe = new StringBuilder(100);
                   unsubscribe.append("unsubscribeOrderStatusForFirmV2 firm:").append(subscriberId)
                              .append(" source:").append(casSource)
                              .append(" remaining:").append(remaining);
                   Log.information(unsubscribe.toString());
           	}
        }
    }

    protected void doUnsubscribeOrderStatusForFirm(ExchangeFirmStructContainer firmContainer, OrderStatusConsumer orderStatusConsumer, String casSource)
        throws SystemException, CommunicationException, DataValidationException, AuthorizationException
    {
        StringBuilder dounsubscribe = new StringBuilder(85);
        dounsubscribe.append("doUnsubscribeOrderStatusForFirm:")
                     .append(firmContainer.getExchange()).append(firmContainer.getFirmNumber())
                     .append(" casSource:").append(casSource);
        Log.information(dounsubscribe.toString());

        // Remove the CBOE event channel filter for "all" events
        getHomeForOrderFilters().removeFilter(new ChannelKey(ChannelType.NEW_ORDER_BY_FIRM, firmContainer));
        getHomeForOrderFilters().removeFilter(new ChannelKey(ChannelType.ORDER_UPDATE_BY_FIRM, firmContainer));
        getHomeForOrderFilters().removeFilter(new ChannelKey(ChannelType.ORDER_FILL_REPORT_BY_FIRM, firmContainer));
        getHomeForOrderFilters().removeFilter(new ChannelKey(ChannelType.ORDER_ACCEPTED_BY_BOOK_BY_FIRM, firmContainer));
        getHomeForOrderFilters().removeFilter(new ChannelKey(ChannelType.ORDER_BUST_REPORT_BY_FIRM, firmContainer));
        getHomeForOrderFilters().removeFilter(new ChannelKey(ChannelType.ORDER_BUST_REINSTATE_REPORT_BY_FIRM, firmContainer));
        getHomeForOrderFilters().removeFilter(new ChannelKey(ChannelType.CANCEL_REPORT_BY_FIRM, firmContainer));
        getHomeForOrderFilters().removeFilter(new ChannelKey(ChannelType.ACCEPT_ORDERS_BY_FIRM, firmContainer));
    }

    public void subscribeOrderStatusForTradingFirmUser(String userId, String groupId, OrderStatusConsumerV2 orderStatusConsumer, String casSource, short blockSize)
            throws SystemException, CommunicationException, DataValidationException, AuthorizationException
    {
        if (Log.isDebugOn())
        {
            Log.debug("subscribeOrderStatusForTradingFirmUser userId:" + userId + " source:" + casSource);
        }
        if (orderStatusConsumer != null)
        {
            if (!isSubscribed(casSource,userId))
            {
                doSubscribeOrderStatusForTradingFirm(userId, orderStatusConsumer, casSource, 0);
            }
            else
            {
                int count = incrementSubscriptions(casSource, userId);
                StringBuilder subscribe = new StringBuilder(100);
                subscribe.append("subscribeOrderStatusForTradingFirmUser userId:").append(userId)
                         .append(" source:").append(casSource)
                         .append(" count:").append(count);
                Log.information(subscribe.toString());
            }
        }
    }

    protected void doSubscribeOrderStatusForTradingFirm(String userId, OrderStatusConsumer orderStatusConsumer, String casSource, int blockSize)
        throws SystemException, CommunicationException, DataValidationException, AuthorizationException
    {
        StringBuilder dosubscribe = new StringBuilder(90);
        dosubscribe.append("doSubscribeOrderStatusForTradingFirm userId:").append(userId)
                   .append(" casSource:").append(casSource);
        Log.information(dosubscribe.toString());

        // Setup the CBOE event channel filter for "all" events
        getHomeForOrderFilters().addFilter(new ChannelKey(ChannelType.ORDER_FILL_REPORT, userId));
        getHomeForOrderFilters().addFilter(new ChannelKey(ChannelType.ORDER_BUST_REPORT, userId));
        getHomeForOrderFilters().addFilter(new ChannelKey(ChannelType.ORDER_BUST_REINSTATE_REPORT, userId));
    }

    public void unsubscribeOrderStatusForTradingFirmUser(String userId, String groupId, OrderStatusConsumerV2 orderStatusConsumer, String casSource)
        throws SystemException, CommunicationException, DataValidationException, AuthorizationException
    {
        if (Log.isDebugOn())
        {
            Log.debug("unsubscribeOrderStatusForTradingFirmUser userId:" + userId + " source:" + casSource);
        }
        if (orderStatusConsumer != null)
        {
            int remaining = decrementSubscriptions(casSource, userId);
            if (remaining == 0)
            {
                doUnsubscribeOrderStatusForTradingFirm(userId, orderStatusConsumer, casSource);
            }
            else if (remaining > 0)
            {
                StringBuilder unsubscribe = new StringBuilder(100);
                unsubscribe.append("unsubscribeOrderStatusForTradingFirmUser userId:").append(userId)
                           .append(" source:").append(casSource)
                           .append(" remaining:").append(remaining);
                Log.information(unsubscribe.toString());
            }
        }
    }

    protected void doUnsubscribeOrderStatusForTradingFirm(String userId, OrderStatusConsumer orderStatusConsumer, String casSource)
        throws SystemException, CommunicationException, DataValidationException, AuthorizationException
    {
        StringBuilder dounsubscribe = new StringBuilder(85);
        dounsubscribe.append("doUnsubscribeOrderStatusForTradingFirm user:").append(userId).append(" casSource:").append(casSource);
        Log.information(dounsubscribe.toString());

        // Remove "all" of the CBOE event channel filter(s)
        getHomeForOrderFilters().removeFilter(new ChannelKey(ChannelType.ORDER_FILL_REPORT, userId));
        getHomeForOrderFilters().removeFilter(new ChannelKey(ChannelType.ORDER_BUST_REPORT, userId));
        getHomeForOrderFilters().removeFilter(new ChannelKey(ChannelType.ORDER_BUST_REINSTATE_REPORT, userId));
    }

    public void ackOrderStatus(OrderAcknowledgeStruct orderAcknowledgeStruct) throws SystemException, CommunicationException, DataValidationException, AuthorizationException
    {
        throw ExceptionBuilder.systemException("Method not implemented: ackOrderStatus", 0);
    }

    public void publishOrderStatus(String userId) throws SystemException, CommunicationException, DataValidationException, AuthorizationException
    {
        throw ExceptionBuilder.systemException("Method not implemented: publishOrderStatus", 0);
    }

    public void publishOrderStatusByClass(String userId, int classKey) throws SystemException, CommunicationException, DataValidationException, AuthorizationException
    {
        throw ExceptionBuilder.systemException("Method not implemented: publishOrderStatusByClass", 0);
    }

    public void ackOrderStatusV3(OrderAcknowledgeStructV3 orderAcknowledgeStructV3) throws SystemException, CommunicationException, DataValidationException, AuthorizationException
    {
        throw ExceptionBuilder.systemException("Method not implemented: ackOrderStatusV3", 0);
    }

    public void subscribeOrderStatus(String userId, OrderStatusConsumer orderStatusConsumer, String casSource) throws SystemException, CommunicationException, DataValidationException, AuthorizationException
    {
        throw ExceptionBuilder.systemException("Method not implemented: subscribeOrderStatus", 0);
    }

    public void unsubscribeOrderStatus(String string, OrderStatusConsumer orderStatusConsumer, String string1) throws SystemException, CommunicationException, DataValidationException, AuthorizationException
    {
        throw ExceptionBuilder.systemException("Method not implemented: unsubscribeOrderStatus", 0);
    }

    public void subscribeOrderStatusForFirm(ExchangeFirmStruct exchangeFirmStruct, OrderStatusConsumer orderStatusConsumer, String string) throws SystemException, CommunicationException, DataValidationException, AuthorizationException
    {
        throw ExceptionBuilder.systemException("Method not implemented: subscribeOrderStatusForFirm", 0);
    }

    public void unsubscribeOrderStatusForFirm(ExchangeFirmStruct exchangeFirmStruct, OrderStatusConsumer orderStatusConsumer, String string) throws SystemException, CommunicationException, DataValidationException, AuthorizationException
    {
        throw ExceptionBuilder.systemException("Method not implemented: unsubscribeOrderStatusForFirm", 0);
    }

    public void registerConsumerForProcessBackReferences() throws SystemException
    {
        throw ExceptionBuilder.systemException("Method not implemented: registerConsumerForProcessBackReferences", 0);
    }
}
