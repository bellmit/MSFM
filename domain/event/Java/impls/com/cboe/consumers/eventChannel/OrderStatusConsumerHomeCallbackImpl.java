package com.cboe.consumers.eventChannel;

import com.cboe.domain.startup.ClientBOHome;
import com.cboe.domain.util.ExchangeFirmStructContainer;
import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.SystemException;
import com.cboe.idl.cmiUser.ExchangeFirmStruct;
import com.cboe.idl.order.OrderAcknowledgeStruct;
import com.cboe.infrastructureServices.foundationFramework.FoundationFramework;
import com.cboe.infrastructureServices.foundationFramework.HomeFactory;
import com.cboe.infrastructureServices.foundationFramework.POAHelper;
import com.cboe.infrastructureServices.foundationFramework.exceptionHandling.CBOELoggableException;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.businessServices.OrderStatusSubscriptionService;
import com.cboe.interfaces.businessServices.OrderStatusSubscriptionServiceHome;
import com.cboe.interfaces.events.OrderStatusConsumerV2;
import com.cboe.interfaces.events.IECOrderStatusConsumerV2Home;
import com.cboe.interfaces.domain.TradingFirmGroupWrapper;
import com.cboe.util.ChannelKey;
import com.cboe.util.ChannelType;

import java.util.HashMap;
import java.util.List;

    /**
     * <b> Description </b>
     * <p>
     *      The Order Status Listener class.
     * </p>
     *
     * @author Jeff Illian
     * @author Keval Desai
     * @author Gijo Joseph
     * 
     * @version 4/18/2006 
     */
public class OrderStatusConsumerHomeCallbackImpl extends ClientBOHome implements IECOrderStatusConsumerV2Home
{
    private OrderStatusSubscriptionService orderStatusService;
    private OrderStatusSubscriptionService orderSubscriptionObject;

    private String casSource;
    private HashMap consumers = new HashMap(11);
    private HashMap callbacks = new HashMap(11);
    public static final String GENERAL_USER = "GENERAL";
    
    public static final String CAS_REMOTE = "IsCasRemote";
    public static final String CAS_REMOTE_DEFAULT = "false";
    public static final String ORDER_STATUS_BLOCKSIZE_REMOTE = "OrderStatusBlockSizeRemote";
    public static final String ORDER_STATUS_BLOCKSIZE_LOCAL = "OrderStatusBlockSizeLocal";
    public static final String ORDER_STATUS_BLOCKSIZE_REMOTE_DEFAULT = "25";
    public static final String ORDER_STATUS_BLOCKSIZE_LOCAL_DEFAULT = "1";
    private short orderStatusBlockSize = 0;  
    private boolean casRemote = false;
    
    /**
     * OrderStatusConsumerHomeCallbackImpl constructor
     */
    public OrderStatusConsumerHomeCallbackImpl()
    {
        super();
        try
        {
            try
            {
            	casRemote = Boolean.parseBoolean(System.getProperty(CAS_REMOTE, CAS_REMOTE_DEFAULT)); 
            	if (casRemote)
            	{
                	orderStatusBlockSize = (short)Integer.parseInt(System.getProperty(ORDER_STATUS_BLOCKSIZE_REMOTE, ORDER_STATUS_BLOCKSIZE_REMOTE_DEFAULT));            		
            	}
            	else
            	{
                	orderStatusBlockSize = (short)Integer.parseInt(System.getProperty(ORDER_STATUS_BLOCKSIZE_LOCAL, ORDER_STATUS_BLOCKSIZE_LOCAL_DEFAULT));            		
            	}
            }
            catch (Exception e)
            {
                Log.exception(e);
            }
            finally
            {
                StringBuilder info = new StringBuilder(50);
                info.append("casRemote=").append(casRemote).append(" : orderStatusBlockSize=").append(orderStatusBlockSize);
                Log.information(this, info.toString());
            }
        }
        catch (Exception e)
        {
        	
        }
    }

    /**
      * Return the OrderStatusService (If first time, find home and get object).
      * @return OrderStatusService
      */
    private OrderStatusSubscriptionService getOrderStatusService()
    {
        if (orderStatusService == null) {
            //This code was copied from ServicesHelper to remove an unwanted dependency.
            try {
                OrderStatusSubscriptionServiceHome home = (OrderStatusSubscriptionServiceHome)HomeFactory.getInstance().findHome(OrderStatusSubscriptionServiceHome.HOME_NAME);
                orderStatusService = (OrderStatusSubscriptionService)home.find();
            }
            catch (CBOELoggableException e) {
                Log.exception(this, e);
                // a really ugly way to get around the missing exception in the interface...
                throw new NullPointerException("Could not find OrderStatusSubscriptionServiceHome (UOQ)");
            }
        }

        return orderStatusService;
    }

    /**
     * Get the object for making and revoking subscriptions.
     * @return An object which adds and removes filters for a channel in this process.
     */
    private OrderStatusSubscriptionService getOrderSubscriptionObject()
    {
        if (null == orderSubscriptionObject)
        {
            orderSubscriptionObject = new OrderStatusSubscriptionServiceClientChannelImpl();
        }
        return orderSubscriptionObject;
    }

    /**
      * Return the OrderStatusConsumer
      * @return OrderStatusConsumer
      */
    public OrderStatusConsumerV2 create()
    {
        return find();
    }

    /**
	 * Return the OrderStatusConsumer
     * @param userId   
     * @return OrderStatusConsumer corresponding to the specified user.
     * @author Gijo Joseph
     * @version 4/19/2006
     */       
	public OrderStatusConsumerV2 create(String userId)
	{
		return find(userId);
	}
	
    /**
      * Return the OrderStatusConsumer    
      * @return OrderStatusConsumer
      */
    public OrderStatusConsumerV2 find()
    {
        return find (GENERAL_USER);
    }

    
    /**
	 * Return the OrderStatusConsumer
     * @param userId   
     * @return OrderStatusConsumer corresponding to the specified user.
     * @author Gijo Joseph
     * @version 4/19/2006
     */       
	public OrderStatusConsumerV2 find(String userId)
	{
	   	synchronized (callbacks) 
		{
	    	OrderStatusConsumerV2 consumer = (OrderStatusConsumerV2)consumers.get(userId);
	    	if (consumer == null) 
	    	{    	
		        FoundationFramework ff = FoundationFramework.getInstance();
		
		        // Create the object that publishes on the IEC
		        OrderStatusConsumerIECImpl orderStatusConsumer = new OrderStatusConsumerIECImpl();
		        orderStatusConsumer.create(String.valueOf(orderStatusConsumer.hashCode()));
		        addToContainer(orderStatusConsumer);
		        OrderStatusEventConsumerInterceptor orderStatusEventConsumerInterceptor = new OrderStatusEventConsumerInterceptor(orderStatusConsumer);
		        if(getInstrumentationEnablementProperty())
		        {
		            orderStatusEventConsumerInterceptor.startInstrumentation(getInstrumentationProperty());
		        }
		        consumer = orderStatusEventConsumerInterceptor;
		        consumers.put(userId, consumer);
	    	}
	    	return consumer;
		}
	}

   
    public void clientStart()
        throws Exception
    {
    }

    public void clientInitialize()
        throws Exception
    {
        casSource = FoundationFramework.getInstance().getConfigService().getProperty("Process.name()");
    }

    /**
     * @param userId   
     *@return OrderStatusConsumer corresponding to the specified user.
     * @author Gijo Joseph
     * @version 4/19/2006
     */
    protected com.cboe.idl.consumers.OrderStatusConsumerV2 getOrderStatusCallback(String userId) throws SystemException
    {
    	synchronized (callbacks) 
    	{
	    	com.cboe.idl.consumers.OrderStatusConsumerV2 orderStatusCallbackV2 = (com.cboe.idl.consumers.OrderStatusConsumerV2)callbacks.get(userId);
	    	if (orderStatusCallbackV2 == null)
	    	{
	    		try 
	    		{
	    	        // bind to orb so that is ready for callbacks
	    			org.omg.CORBA.Object obj = 
	    				POAHelper.connect(new com.cboe.idl.consumers.POA_OrderStatusConsumerV2_tie(find(userId)), this);
	    			orderStatusCallbackV2 = com.cboe.idl.consumers.OrderStatusConsumerV2Helper.narrow(obj);
	    			callbacks.put(userId, orderStatusCallbackV2);
	    		}
	    		catch (Exception e)
	    		{
	                Log.exception(this, "Exception while creating orderstatus callback for user:" + userId, e);
	                throw new SystemException();
	    		}
	    	}
	    	return orderStatusCallbackV2;
    	}
    }
    
    /**
      * Adds a  Filter to the internal event channel. Constraints based on the
      * ChannelKey will be added as well.
      *
      * @param channelKey the event channel key
      *
      * @author Connie Feng
      * @author Jeff Illian
      * @author Keval Desai
      * @author Emily Huang
      * @author Gijo Joseph
      * @version 4/19/2006
      */
    public void addFilter ( ChannelKey channelKey )
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
		if (Log.isDebugOn())
        {
            Log.debug(this, "->addFilter");
        }
        // Register tie object with the Order Status Service if by memberKey
        // else register by firmKey
        if (    channelKey.channelType == ChannelType.NEW_ORDER_BY_FIRM
            ||  channelKey.channelType == ChannelType.ORDER_UPDATE_BY_FIRM
            ||  channelKey.channelType == ChannelType.ORDER_FILL_REPORT_BY_FIRM
            ||  channelKey.channelType == ChannelType.ORDER_ACCEPTED_BY_BOOK_BY_FIRM
            ||  channelKey.channelType == ChannelType.CANCEL_REPORT_BY_FIRM
            ||  channelKey.channelType == ChannelType.ACCEPT_ORDERS_BY_FIRM
            ||  channelKey.channelType == ChannelType.ORDER_BUST_REPORT_BY_FIRM
            ||  channelKey.channelType == ChannelType.ORDER_BUST_REINSTATE_REPORT_BY_FIRM
            )
        {

            ExchangeFirmStructContainer id = (ExchangeFirmStructContainer) channelKey.key;
            getOrderSubscriptionObject().subscribeOrderStatusForFirmV2(new ExchangeFirmStruct(id.getExchange(), id.getFirmNumber()),
            		getOrderStatusCallback(id.getExchange()+id.getFirmNumber()), casSource, orderStatusBlockSize);
        }
    else if (channelKey.channelType == ChannelType.ORDER_FILL_REPORT_BY_TRADING_FIRM
        ||  channelKey.channelType == ChannelType.ORDER_BUST_REPORT_BY_TRADING_FIRM
        ||  channelKey.channelType == ChannelType.ORDER_BUST_REINSTATE_REPORT_BY_TRADING_FIRM)
    {

        TradingFirmGroupWrapper groupContainer = (TradingFirmGroupWrapper) channelKey.key;
        String id = groupContainer.getTradingFirmId();
        List<String> users = groupContainer.getUsers();
        for(String user: users)
        {
            getOrderSubscriptionObject().subscribeOrderStatusForTradingFirmUser(user, id, getOrderStatusCallback(id), casSource, (short)0);
        }
    }
        else
        {
            String id = (String) channelKey.key;
            getOrderSubscriptionObject().subscribeOrderStatusV2(id, getOrderStatusCallback(id), casSource, orderStatusBlockSize);
        }
    }

    /**
      * Removes the event channel Filter from the CBOE event channel.
      * ChannelKey will be added as well.
      *
      * @param channelKey the event channel key
      *
      * @author Connie Feng
      * @author Keval Desai
      * @author Gijo Joseph
      * @version 4/19/2006
      */
    public void removeFilter ( ChannelKey channelKey )
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "->removeFilter");
        }
        // Unregister tie object with the Order Status Service if by memberKey
        // else unregister by firmKey
        if (    channelKey.channelType == ChannelType.NEW_ORDER_BY_FIRM
            ||  channelKey.channelType == ChannelType.ORDER_UPDATE_BY_FIRM
            ||  channelKey.channelType == ChannelType.ORDER_FILL_REPORT_BY_FIRM
            ||  channelKey.channelType == ChannelType.ORDER_ACCEPTED_BY_BOOK_BY_FIRM
            ||  channelKey.channelType == ChannelType.CANCEL_REPORT_BY_FIRM
            ||  channelKey.channelType == ChannelType.ACCEPT_ORDERS_BY_FIRM
            ||  channelKey.channelType == ChannelType.ORDER_BUST_REPORT_BY_FIRM
            ||  channelKey.channelType == ChannelType.ORDER_BUST_REINSTATE_REPORT_BY_FIRM
            )
        {
            ExchangeFirmStructContainer id = (ExchangeFirmStructContainer) channelKey.key;
            getOrderSubscriptionObject().unsubscribeOrderStatusForFirmV2
                    ( new ExchangeFirmStruct(id.getExchange(), id.getFirmNumber()),
                    		getOrderStatusCallback(id.getExchange()+id.getFirmNumber()), casSource);
    }
    else if (channelKey.channelType == ChannelType.ORDER_FILL_REPORT_BY_TRADING_FIRM
        ||  channelKey.channelType == ChannelType.ORDER_BUST_REPORT_BY_TRADING_FIRM
        ||  channelKey.channelType == ChannelType.ORDER_BUST_REINSTATE_REPORT_BY_TRADING_FIRM)
    {

        TradingFirmGroupWrapper groupContainer = (TradingFirmGroupWrapper) channelKey.key;
        String id = groupContainer.getTradingFirmId();
        List<String> users = groupContainer.getUsers();
        for(String user: users)
        {
            getOrderSubscriptionObject().unsubscribeOrderStatusForTradingFirmUser(user, id, getOrderStatusCallback(id), casSource);
        }
        }
        else
        {
        	String id = (String) channelKey.key;
            getOrderSubscriptionObject().unsubscribeOrderStatusV2((String) channelKey.key, getOrderStatusCallback(id), casSource);
        }
        // I think it is probably better not to remove the callback object for the user from the map.
        // This eliminates the need to recreate them for each user re-login. Moreover, with unsubscribe,
        // this callback will be removed from the server side anyway. --Gijo.         
    }

  /**
   * Have OSSS/OSS send any unAcknowledged events.
   *
   * @param userId of logged in user
   */
    public void resubscribeOrderStatus(String userId)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        getOrderStatusService().publishOrderStatus(userId);
    }


    public void ackOrderStatus(OrderAcknowledgeStruct orderAcknowledge)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        getOrderStatusService().ackOrderStatus( orderAcknowledge );
    }

    public void publishUnackedOrderStatusByClass(String userId, int classKey)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        getOrderStatusService().publishOrderStatusByClass(userId, classKey);
    }

    // Unused methods declared in home interface for server usage.
    public void addConsumer(OrderStatusConsumerV2 consumer, ChannelKey key) {}
    public void removeConsumer(OrderStatusConsumerV2 consumer, ChannelKey key) {}
    public void removeConsumer(OrderStatusConsumerV2 consumer) {}

}// EOF
